package com.giraone.imaging.text;

import com.giraone.assertions.pdf.PdfContentAssertion;
import com.giraone.assertions.pdf.model.RectangleInMm;
import com.giraone.imaging.ConversionCommand;
import com.giraone.imaging.FileInfo;
import com.giraone.imaging.ImagingFactory;
import com.giraone.imaging.ImagingProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;

import static com.giraone.imaging.MimeTypes.APPLICATION_PDF;
import static com.giraone.imaging.MimeTypes.IMAGE_JPEG;
import static com.giraone.imaging.text.MarkdownProviderFlexmark.*;
import static org.assertj.core.api.Assertions.assertThat;

class ProviderMarkdownTest {

    private static final MarkdownProvider markdownProviderUnderTest = MarkdownProvider.getInstance();
    // Used to test, whether created dimensions are correct
    private static final ImagingProvider imagingProvider = ImagingFactory.getInstance().getProvider();

    @ParameterizedTest
    @CsvSource({
        "document-01.md",
        "document-02.md",
    })
    void createThumbnail_400px(String fileName) throws Exception {
        // arrange
        File inputFile = new File("src/test/resources/" + fileName);
        File outputFile = File.createTempFile("md-to-thumb-" + fileName + "-", ".jpg");
        outputFile.deleteOnExit();
        ConversionCommand.CompressionQuality quality = ConversionCommand.CompressionQuality.LOSSY_BEST;
        int width = 400;
        int height = (int) (A4_PORTRAIT_HEIGHT_MM * 400 / A4_PORTRAIT_WIDTH_MM);
        // act
        markdownProviderUnderTest.createThumbnail(inputFile, outputFile, IMAGE_JPEG, width, height, quality);
        // assert
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
        // arrange
        File inputFile = new File("src/test/resources/" + fileName);
        File outputFile = File.createTempFile("md-to-a4-" + fileName + "-", ".jpg");
        outputFile.deleteOnExit();
        ConversionCommand.CompressionQuality quality = ConversionCommand.CompressionQuality.LOSSY_BEST;
        // act
        markdownProviderUnderTest.createThumbnail(inputFile, outputFile, IMAGE_JPEG, A4_PORTRAIT_WIDTH_PX_PRINT, A4_PORTRAIT_HEIGHT_PX_PRINT, quality);
        // assert
        assertThat(outputFile.exists());
        FileInfo fileInfo = imagingProvider.fetchFileInfo(outputFile);
        assertThat(fileInfo.getMimeType()).isEqualTo(IMAGE_JPEG);
        assertThat(fileInfo.getWidth()).isEqualTo(A4_PORTRAIT_WIDTH_PX_PRINT);
        assertThat(fileInfo.getHeight()).isEqualTo(A4_PORTRAIT_HEIGHT_PX_PRINT);
    }

    @ParameterizedTest
    @CsvSource({
        "document-01.md,1",
        "document-02.md,2",
    })
    void createPdf(String mdFileName, int expectedNumberOfPages) throws Exception {
        // arrange
        File inputFile = new File("src/test/resources/" + mdFileName);
        File outputFile = File.createTempFile("md-to-pdf-" + mdFileName + "-", ".pdf");
        outputFile.deleteOnExit();
        // act
        markdownProviderUnderTest.createPdf(inputFile, outputFile, DIMENSION_A4_PORTRAIT);
        // assert (simple)
        assertThat(outputFile.exists());
        FileInfo fileInfo = imagingProvider.fetchFileInfo(outputFile);
        assertThat(fileInfo.getMimeType()).isEqualTo(APPLICATION_PDF);
        // assert (PDF assertions)
        PdfContentAssertion.assertThat(outputFile)
            .isValidPdf()
            .hasNumberOfPages(expectedNumberOfPages)
        ;
    }

    @Test
    void createPdfLineBreaksWork_A4_Portrait() throws Exception {
        // arrange
        File inputFile = new File("src/test/resources/document-01.md");
        File outputFile = File.createTempFile("md-to-pdf-a4-portrait-", ".pdf");
        outputFile.deleteOnExit();
        // act
        markdownProviderUnderTest.createPdf(inputFile, outputFile, DIMENSION_A4_PORTRAIT);
        // assert (simple)
        assertThat(outputFile.exists());
        // assert (PDF assertions)
        PdfContentAssertion.assertThat(outputFile)
            .isValidPdf()
            .containsTextStringAtPosition(1, new RectangleInMm(
                    12.7f, 71.8f, 194.1f, 74.3f),
                "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed HERE COMES THE LINE BREAK")
            .containsTextStringAtPosition(1, new RectangleInMm(
                    12.7f, 76.4f, 190.8f, 78.9f),
                "NEW LINE et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo")
        ;
    }

    @Test
    void createPdfLineBreaksWork_A4_Landscape() throws Exception {
        // arrange
        File inputFile = new File("src/test/resources/document-01.md");
        File outputFile = File.createTempFile("md-to-pdf-a4-landscape-", ".pdf");
        outputFile.deleteOnExit();
        // act
        markdownProviderUnderTest.createPdf(inputFile, outputFile, DIMENSION_A4_LANDSCAPE);
        // assert (simple)
        assertThat(outputFile.exists());
        // assert (PDF assertions)
        PdfContentAssertion.assertThat(outputFile)
            .isValidPdf()
            .containsTextStringAtPosition(1, new RectangleInMm(
                    12.7f, 71.8f, 283.3f, 74.3f),
                "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed HERE COMES THE LINE BREAK NEW LINE et dolore magna aliquyam erat, sed")
            .containsTextStringAtPosition(1, new RectangleInMm(
                    12.7f, 76.4f, 148.7f, 78.9f),
                "diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.")
        ;
    }

    @Test
    void createPdfLineBreaksWork_Letter_Portrait() throws Exception {
        // arrange
        File inputFile = new File("src/test/resources/document-01.md");
        File outputFile = File.createTempFile("md-to-pdf-letter-portrait-", ".pdf");
        outputFile.deleteOnExit();
        // act
        markdownProviderUnderTest.createPdf(inputFile, outputFile, DIMENSION_LETTER_PORTRAIT);
        // assert (simple)
        assertThat(outputFile.exists());
        // assert (PDF assertions)
        PdfContentAssertion.assertThat(outputFile)
            .isValidPdf()
            .containsTextStringAtPosition(1, new RectangleInMm(
                    12.7f, 71.8f, 194.1f, 74.3f),
                "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed HERE COMES THE LINE BREAK")
            .containsTextStringAtPosition(1, new RectangleInMm(
                    12.7f, 76.4f, 199.1f, 78.9f),
                "NEW LINE et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo")
        ;
    }

    @Test
    void createPdfLineBreaksWork_Letter_Landscape() throws Exception {
        // arrange
        File inputFile = new File("src/test/resources/document-01.md");
        File outputFile = File.createTempFile("md-to-pdf-letter-landscape-", ".pdf");
        //outputFile.deleteOnExit();
        // act
        markdownProviderUnderTest.createPdf(inputFile, outputFile, DIMENSION_LETTER_LANDSCAPE);
        // assert (simple)
        assertThat(outputFile.exists());
        // assert (PDF assertions)
        PdfContentAssertion.assertThat(outputFile)
            .isValidPdf()
            .containsTextStringAtPosition(1, new RectangleInMm(
                    12.7f, 71.8f, 265.6f, 74.3f),
                "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed HERE COMES THE LINE BREAK NEW LINE et dolore magna aliquyam")
            .containsTextStringAtPosition(1, new RectangleInMm(
                    12.7f, 76.4f, 166.4f, 78.9f),
                "erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum")
        ;
    }
}
