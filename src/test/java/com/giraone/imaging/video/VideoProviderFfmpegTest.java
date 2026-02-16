package com.giraone.imaging.video;

import com.giraone.imaging.ConversionCommand;
import com.giraone.imaging.FileInfo;
import com.giraone.imaging.ImagingFactory;
import com.giraone.imaging.ImagingProvider;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.giraone.imaging.ConversionCommand.MIME_TYPE_JPEG;
import static com.giraone.imaging.video.VideoProviderFfmpeg.FFMPEG_BIN_ENV;
import static org.assertj.core.api.Assertions.assertThat;

class VideoProviderFfmpegTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoProviderFfmpegTest.class);

    private static final VideoProvider videoProviderUnderTest = VideoProvider.getInstance();
    // Used to test, whether creates dimensions are correct
    private static final ImagingProvider imagingProvider = ImagingFactory.getInstance().getProvider();

    @Test
    void createThumbnail() throws Exception {

        String ffmpegBinary = System.getenv(FFMPEG_BIN_ENV);
        if (ffmpegBinary == null || ffmpegBinary.trim().isEmpty() || !new File(ffmpegBinary).exists()) {
            LOGGER.warn("Environment variable \"{}\" no set. Skipping test!", FFMPEG_BIN_ENV);
            return;
        }

        /// arrange
        File inputFile = new File("src/test/resources/EKG-960x540.mp4");
        File outputFile = File.createTempFile("mp4-to-thumb-", ".jpg");
        //outputFile.deleteOnExit();
        ConversionCommand.CompressionQuality quality = ConversionCommand.CompressionQuality.LOSSY_BEST;
        int thumbPixelMaxSize = 400;
        /// act
        videoProviderUnderTest.createThumbnail(inputFile, outputFile, MIME_TYPE_JPEG, thumbPixelMaxSize, thumbPixelMaxSize, quality);
        /// assert
        assertThat(outputFile.exists());
        FileInfo fileInfo = imagingProvider.fetchFileInfo(outputFile);
        assertThat(fileInfo.getMimeType()).isEqualTo(MIME_TYPE_JPEG);
        assertThat(fileInfo.getWidth()).isEqualTo(400);
        assertThat(fileInfo.getHeight()).isEqualTo(225);
    }
}