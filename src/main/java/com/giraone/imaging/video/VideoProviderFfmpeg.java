package com.giraone.imaging.video;

import com.giraone.imaging.ConversionCommand;
import de.medstage.common.imaging.ConversionCommand;
import de.medstage.common.imaging.FileInfo;
import de.medstage.common.imaging.ImagingProvider;
import de.medstage.common.imaging.java2.Provider;
import de.medstage.common.os.OsCommandResult;
import de.medstage.common.os.OsUtil;

import java.io.File;

public class VideoProviderFfmpeg implements VideoProvider {

    // -ss 1            Seeks position (1 seconds) - must be given before -i
    // -i <input>       The input video
    // -frames:v 1      Stop writing to the stream after 1 frame
    // -q:v 2           Use fixed quality scale (VBR)
    // -v quiet         log level "quiet"
    // -y               overwrite files

    private static final String BINARY_WINDOWS = "C:/Tools/Videos/ffmpeg/bin/ffmpeg.exe";
    private static final String[] COMMAND_WINDOWS = new String[]{BINARY_WINDOWS, "-ss", "1", "-i", INFILE, "-frames:v", "1", "-q:v", "2", "-v", "quiet", "-y", OUTFILE};

    private static final String BINARY_LINUX = "./ffmpeg";
    private static final String[] COMMAND_LINUX = new String[]{BINARY_LINUX, "-ss", "1", "-i", INFILE, "-frames:v", "1", "-q:v", "2", "-v", "quiet", "-y", OUTFILE};

    /**
     * Create a thumbnail image for a given file.
     * @param inputFile Input file.
     * @param conversionCommand The command with the definitions of the output (path, format, width, height and quality).
     * @throws Exception on any error opening the file, converting the file or writing to the output.
     */
    @Override
    public void createThumbnail(File inputFile, ConversionCommand conversionCommand) throws Exception {
        final File tempFile = File.createTempFile("v2png", ".png");
        final String tempFilePath = tempFile.getAbsolutePath();
        // Remove the file in 2 minutes using the background worker
        // JobUtil.removeFile(tempFilePath, 120);
        String[] ffmpegCommands;
        if (System.getProperty("os.name").startsWith("Windows")) {
            ffmpegCommands = COMMAND_WINDOWS.clone();
        } else {
            ffmpegCommands = COMMAND_LINUX.clone();
        }
        for (int i = 0; i < ffmpegCommands.length; i++) {
            if (INFILE.equals(ffmpegCommands[i])) ffmpegCommands[i] = inputFilePath;
            if (OUTFILE.equals(ffmpegCommands[i])) ffmpegCommands[i] = tempFilePath;
        }

        final OsCommandResult result = OsUtil.runCommandAndReadOutput(ffmpegCommands, 60);
        if (result.getCode() >= 0) {
            if (tempFile.length() > 100L) {
                final Provider imagingProvider = new Provider();
                imagingProvider.convertImage(tempFilePath, conversionCommands);
                tempFile.delete();
            } else {
                throw new RuntimeException("Cannot create thumbnail for video \"" + inputFilePath + "\"! Empty PNG output.");
            }
        } else {
            if (result.getException() != null) {
                throw new RuntimeException("Cannot create thumbnail for video \"" + inputFilePath + "\"! ", result.getException());
            } else {
                throw new RuntimeException("Cannot create thumbnail for video \"" + inputFilePath + "\"! " + result.getOutput());
            }
        }
    }
}
