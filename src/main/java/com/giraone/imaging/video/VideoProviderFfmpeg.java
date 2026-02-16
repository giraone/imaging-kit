package com.giraone.imaging.video;

import com.giraone.imaging.ConversionCommand;
import com.giraone.imaging.ImagingFactory;
import com.giraone.imaging.ImagingProvider;
import com.giraone.imaging.os.OsCommandResult;
import com.giraone.imaging.os.OsUtil;

import java.io.File;

public class VideoProviderFfmpeg implements VideoProvider {

    static final String FFMPEG_BIN_ENV = "FFMPEG_BIN";
    static final String FFMPEG_SEEK_SECONDS_ENV = "FFMPEG_SEEK_SECONDS";

    private static final String FFMPEG_BIN = System.getenv(FFMPEG_BIN_ENV);
    private static String FFMPEG_SEEK_SECONDS = System.getenv(FFMPEG_SEEK_SECONDS_ENV);

    private static final String SECONDS = "SECONDS";
    private static final String INFILE = "INFILE";
    private static final String OUTFILE = "OUTFILE";

    // -ss 1            Seeks position (1 seconds) - must be given before -i
    // -i <input>       The input video
    // -frames:v 1      Stop writing to the stream after 1 frame
    // -q:v 2           Use fixed quality scale (VBR)
    // -v quiet         log level "quiet"
    // -y               overwrite files

    private static final String[] COMMAND = new String[]{FFMPEG_BIN, "-ss", SECONDS, "-i", INFILE, "-frames:v", "1", "-q:v", "2", "-v", "quiet", "-y", OUTFILE};

    static {
        if (FFMPEG_SEEK_SECONDS == null || FFMPEG_SEEK_SECONDS.trim().isEmpty()) {
            FFMPEG_SEEK_SECONDS = "1";
        }
    }

    final ImagingProvider imagingProvider = ImagingFactory.getInstance().getProvider();

    /**
     * Create a thumbnail image for a given file.
     * @param inputFile Input file.
     * @param conversionCommand The command with the definitions of the output (path, format, width, height and quality).
     * @throws Exception on any error opening the file, converting the file or writing to the output.
     */
    @Override
    public void createThumbnail(File inputFile, ConversionCommand conversionCommand) throws Exception {

        if (FFMPEG_BIN == null || FFMPEG_BIN.trim().isEmpty()) {
            throw new IllegalStateException("Environment variable \"" + FFMPEG_BIN_ENV + "\" not set!");
        }

        final File tempPngFileInOriginalSize = File.createTempFile("v2png", ".png");
        final String[] ffmpegCommands = COMMAND.clone();
        for (int i = 0; i < ffmpegCommands.length; i++) {
            if (SECONDS.equals(ffmpegCommands[i])) {
                ffmpegCommands[i] = FFMPEG_SEEK_SECONDS;
            } else if (INFILE.equals(ffmpegCommands[i])) {
                ffmpegCommands[i] = inputFile.getAbsolutePath();
            } else if (OUTFILE.equals(ffmpegCommands[i])) {
                ffmpegCommands[i] = tempPngFileInOriginalSize.getAbsolutePath();
            }
        }

        final OsCommandResult result = OsUtil.runCommandAndReadOutput(ffmpegCommands, 60);
        if (result.getCode() >= 0) {
            if (tempPngFileInOriginalSize.length() > 100L) {
                imagingProvider.createThumbnail(tempPngFileInOriginalSize, conversionCommand);
                //tempPngFileInOriginalSize.delete();
            } else {
                throw new RuntimeException("Cannot create thumbnail for video \"" + inputFile + "\"! Empty PNG output from ffmpeg.");
            }
        } else {
            if (result.getException() != null) {
                throw new RuntimeException("Cannot create thumbnail for video \"" + inputFile + "\" using \"" + FFMPEG_BIN + "\"!", result.getException());
            } else {
                throw new RuntimeException("Cannot create thumbnail for video \"" + inputFile + "\" using \"" + FFMPEG_BIN + "\"!");
            }
        }
    }
}
