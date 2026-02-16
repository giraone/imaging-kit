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

    private static final String TEST_FILE_MP4_01 = "EKG-14s-960x540.mp4";
    private static final String TEST_FILE_MP4_02 = "eyeball-10s-720x304.mp4";

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
        File inputFile = new File("src/test/resources/" + TEST_FILE_MP4_02);
        File outputFile = File.createTempFile("mp4-to-thumb-", ".jpg");
        outputFile.deleteOnExit();
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

    @Test
    void createThumbnails() throws Exception {
        String ffmpegBinary = System.getenv(FFMPEG_BIN_ENV);
        if (ffmpegBinary == null || ffmpegBinary.trim().isEmpty() || !new File(ffmpegBinary).exists()) {
            LOGGER.warn("Environment variable \"{}\" no set. Skipping test!", FFMPEG_BIN_ENV);
            return;
        }

        /// arrange
        File inputFile = new File("src/test/resources/" + TEST_FILE_MP4_01);
        File outputFile1 = File.createTempFile("mp4-to-thumb-", ".jpg");
        File outputFile2 = File.createTempFile("mp4-to-thumb-", ".jpg");
        outputFile1.deleteOnExit();
        outputFile2.deleteOnExit();
        ConversionCommand.CompressionQuality quality = ConversionCommand.CompressionQuality.LOSSY_BEST;
        int thumbPixelMaxSize = 400;
        ConversionCommand conversionCommand1 = ConversionCommand.buildConversionCommand(
            outputFile1, MIME_TYPE_JPEG, thumbPixelMaxSize, thumbPixelMaxSize, quality);
        ConversionCommand conversionCommand2 = ConversionCommand.buildConversionCommand(
            outputFile2, MIME_TYPE_JPEG, thumbPixelMaxSize / 2, thumbPixelMaxSize / 2, quality);
        /// act
        videoProviderUnderTest.createThumbnails(inputFile, new ConversionCommand[] { conversionCommand1, conversionCommand2 });
        /// assert
        assertThat(outputFile1.exists());
        assertThat(outputFile2.exists());
        FileInfo fileInfo1 = imagingProvider.fetchFileInfo(outputFile1);
        assertThat(fileInfo1.getWidth()).isEqualTo(400);
        assertThat(fileInfo1.getHeight()).isEqualTo(225);
        FileInfo fileInfo2 = imagingProvider.fetchFileInfo(outputFile2);
        assertThat(fileInfo2.getWidth()).isEqualTo(200);
        assertThat(fileInfo2.getHeight()).isEqualTo(112);
    }
}