package com.giraone.imaging.text;

import com.giraone.imaging.ConversionCommand;
import com.giraone.imaging.FileInfo;
import com.giraone.imaging.ImagingFactory;
import com.giraone.imaging.ImagingProvider;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;

import static com.giraone.imaging.MimeTypes.APPLICATION_PDF;
import static com.giraone.imaging.MimeTypes.IMAGE_JPEG;
import static com.giraone.imaging.text.MarkdownProviderFlexmark.*;
import static org.assertj.core.api.Assertions.assertThat;

class ProviderMarkdownTest {

    private static final MarkdownProvider markdownProviderUnderTest = MarkdownProvider.getInstance();
    // Used to test, whether creates dimensions are correct
    private static final ImagingProvider imagingProvider = ImagingFactory.getInstance().getProvider();

    @ParameterizedTest
    @CsvSource({
        "document-01.md",
        "document-02.md",
    })
    void createThumbnail_400px(String fileName) throws Exception {
        /// arrange
        File inputFile = new File("src/test/resources/" + fileName);
        File outputFile = File.createTempFile("md-to-thumb-" + fileName + "-", ".jpg");
        //outputFile.deleteOnExit();
        ConversionCommand.CompressionQuality quality = ConversionCommand.CompressionQuality.LOSSY_BEST;
        int width = A4_WIDTH_MM * 400 / A4_WIDTH_MM;
        int height = A4_HEIGHT_MM * 400 / A4_WIDTH_MM;
        /// act
        markdownProviderUnderTest.createThumbnail(inputFile, outputFile, IMAGE_JPEG, width, height, quality);
        /// assert
        assertThat(outputFile.exists());
        FileInfo fileInfo = imagingProvider.fetchFileInfo(outputFile);
        assertThat(fileInfo.getMimeType()).isEqualTo(IMAGE_JPEG);
        assertThat(fileInfo.getWidth()).isEqualTo(400);
        assertThat(fileInfo.getHeight()).isEqualTo(565);
    }

    @ParameterizedTest
    @CsvSource({
        "document-01.md",
        "document-02.md",
    })
    void createThumbnail_A4(String fileName) throws Exception {
        /// arrange
        File inputFile = new File("src/test/resources/" + fileName);
        File outputFile = File.createTempFile("md-to-a4-" + fileName + "-", ".jpg");
        //outputFile.deleteOnExit();
        ConversionCommand.CompressionQuality quality = ConversionCommand.CompressionQuality.LOSSY_BEST;
        /// act
        markdownProviderUnderTest.createThumbnail(inputFile, outputFile, IMAGE_JPEG, A4_WIDTH_PX_PRINT, A4_HEIGHT_PX_PRINT, quality);
        /// assert
        assertThat(outputFile.exists());
        FileInfo fileInfo = imagingProvider.fetchFileInfo(outputFile);
        assertThat(fileInfo.getMimeType()).isEqualTo(IMAGE_JPEG);
        assertThat(fileInfo.getWidth()).isEqualTo(A4_WIDTH_PX_PRINT);
        assertThat(fileInfo.getHeight()).isEqualTo(A4_HEIGHT_PX_PRINT);
    }

    @ParameterizedTest
    @CsvSource({
        "document-01.md",
        "document-02.md",
    })
    void createPdf(String mdFileName) throws Exception {
        /// arrange
        File inputFile = new File("src/test/resources/" + mdFileName);
        File outputFile = File.createTempFile("md-to-pdf-" + mdFileName + "-", ".pdf");
        //outputFile.deleteOnExit();
        /// act
        markdownProviderUnderTest.createPdf(inputFile, outputFile);
        /// assert
        assertThat(outputFile.exists());
        FileInfo fileInfo = imagingProvider.fetchFileInfo(outputFile);
        assertThat(fileInfo.getMimeType()).isEqualTo(APPLICATION_PDF);
        // TODO: Add PDF assertion lib
    }
}