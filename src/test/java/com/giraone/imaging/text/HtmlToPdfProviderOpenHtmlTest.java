package com.giraone.imaging.text;

import com.giraone.assertions.pdf.PdfContentAssertion;
import com.giraone.assertions.pdf.model.RectangleInMm;
import com.giraone.imaging.FileInfo;
import com.giraone.imaging.ImagingFactory;
import com.giraone.imaging.ImagingProvider;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.awt.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static com.giraone.imaging.MimeTypes.APPLICATION_PDF;
import static com.giraone.imaging.text.MarkdownProvider.DIMENSION_A4_PORTRAIT;
import static org.assertj.core.api.Assertions.assertThat;

class HtmlToPdfProviderOpenHtmlTest {

    private final HtmlToPdfProviderOpenHtml htmlToPdfProviderOpenHtmlTestUnderTest = new HtmlToPdfProviderOpenHtml();
    // Used to test, whether output is a PDF
    private static final ImagingProvider imagingProvider = ImagingFactory.getInstance().getProvider();

    @ParameterizedTest
    @CsvSource({
        "document-01,3", // document-01 has its own CSS-based sizing and overwrites A3_PORTRAIT!
        "document-02,2",
    })
    void createA4Document(String fileBaseName, int numberOfPages) throws Exception {
        // arrange
        File inputFile = new File("src/test/resources/" + fileBaseName + ".html");
        String inputHtml = Files.readString(inputFile.toPath(), StandardCharsets.UTF_8);
        File outputFile = File.createTempFile("html-to-pdf-" + fileBaseName + "-", ".pdf");
        outputFile.deleteOnExit();
        // act
        htmlToPdfProviderOpenHtmlTestUnderTest.renderHtmlToPdf(inputHtml, outputFile, DIMENSION_A4_PORTRAIT);
        // assert
        assertThat(outputFile.exists());
        FileInfo fileInfo = imagingProvider.fetchFileInfo(outputFile);
        assertThat(fileInfo.getMimeType()).isEqualTo(APPLICATION_PDF);
        // assert (PDF assertions)
        PdfContentAssertion.assertThat(outputFile)
            .isValidPdf()
            .hasNumberOfPages(numberOfPages)
        ;
    }
}