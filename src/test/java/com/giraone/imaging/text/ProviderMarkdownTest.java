package com.giraone.imaging.text;

import com.giraone.imaging.ConversionCommand;
import com.giraone.imaging.FileInfo;
import com.giraone.imaging.ImagingFactory;
import com.giraone.imaging.ImagingProvider;
import org.junit.jupiter.api.Test;

import java.io.File;

import static com.giraone.imaging.ConversionCommand.MIME_TYPE_JPEG;
import static com.giraone.imaging.text.MarkdownProviderFlexmark.*;
import static org.assertj.core.api.Assertions.assertThat;

class ProviderMarkdownTest {

    private static final MarkdownProvider markdownProviderUnderTest = MarkdownProviderFlexmark.getInstance();
    // Used to test, whether creates dimensions are correct
    private static final ImagingProvider imagingProvider = ImagingFactory.getInstance().getProvider();

    @Test
    void createThumbnail() throws Exception {
        /// arrange
        File inputFile = new File("src/test/resources/document-01.md");
        File outputFile = File.createTempFile("md-to-thumb-", ".jpg");
        outputFile.deleteOnExit();
        ConversionCommand.CompressionQuality quality = ConversionCommand.CompressionQuality.LOSSY_BEST;
        int width = A4_WIDTH_MM * 400 / A4_WIDTH_MM;
        int height = A4_HEIGHT_MM * 400 / A4_WIDTH_MM;
        /// act
        markdownProviderUnderTest.createThumbnail(inputFile, outputFile, MIME_TYPE_JPEG, width, height, quality);
        /// assert
        assertThat(outputFile.exists());
        FileInfo fileInfo = imagingProvider.fetchFileInfo(outputFile);
        assertThat(fileInfo.getMimeType()).isEqualTo(MIME_TYPE_JPEG);
        assertThat(fileInfo.getWidth()).isEqualTo(400);
        assertThat(fileInfo.getHeight()).isEqualTo(565);
    }

    @Test
    void createA4Document() throws Exception {
        /// arrange
        File inputFile = new File("src/test/resources/document-01.md");
        File outputFile = File.createTempFile("md-to-a4-", ".jpg");
        outputFile.deleteOnExit();
        ConversionCommand.CompressionQuality quality = ConversionCommand.CompressionQuality.LOSSY_BEST;
        /// act
        markdownProviderUnderTest.createThumbnail(inputFile, outputFile, MIME_TYPE_JPEG, A4_WIDTH_PX, A4_HEIGHT_PX, quality);
        /// assert
        assertThat(outputFile.exists());
        FileInfo fileInfo = imagingProvider.fetchFileInfo(outputFile);
        assertThat(fileInfo.getMimeType()).isEqualTo(MIME_TYPE_JPEG);
        assertThat(fileInfo.getWidth()).isEqualTo(A4_WIDTH_PX);
        assertThat(fileInfo.getHeight()).isEqualTo(A4_HEIGHT_PX);

    }
}