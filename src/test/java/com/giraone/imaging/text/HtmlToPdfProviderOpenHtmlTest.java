package com.giraone.imaging.text;

import com.giraone.imaging.ConversionCommand;
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
import static org.assertj.core.api.Assertions.assertThat;

class HtmlToPdfProviderOpenHtmlTest {

    private final HtmlToPdfProviderOpenHtml htmlToPdfProviderOpenHtmlTestUnderTest = new HtmlToPdfProviderOpenHtml();
    // Used to test, whether output is a PDF
    private static final ImagingProvider imagingProvider = ImagingFactory.getInstance().getProvider();

    @ParameterizedTest
    @CsvSource({
        "document-01.html",
        "document-02.html",
    })
    void createA4Document(String fileName) throws Exception {
        /// arrange
        File inputFile = new File("src/test/resources/" + fileName);
        String inputHtml = Files.readString(inputFile.toPath(), StandardCharsets.UTF_8);
        File outputFile = File.createTempFile("html-to-pdf-" + fileName + "-", ".pdf");
        //outputFile.deleteOnExit();
        /// act
        htmlToPdfProviderOpenHtmlTestUnderTest.renderHtmlToPdf(inputHtml, outputFile);
        /// assert
        assertThat(outputFile.exists());
        FileInfo fileInfo = imagingProvider.fetchFileInfo(outputFile);
        assertThat(fileInfo.getMimeType()).isEqualTo(APPLICATION_PDF);
        // TODO: Add PDF assertion lib
    }
}