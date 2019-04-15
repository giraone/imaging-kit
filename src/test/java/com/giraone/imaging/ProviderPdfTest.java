package com.giraone.imaging;

import com.giraone.imaging.pdf.PdfProvider;
import com.giraone.imaging.pdf.PdfProviderFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Tests for the Provider PDF implementation.
 */
class ProviderPdfTest {

    private static final boolean CLEAR_OUTPUT_FILES = true;

    private static final String TEST_FILE_PDF_01 = "document-01-PDF-1.3.pdf";
    private static final String TEST_FILE_PDF_02 = "document-02-PDF-1.4.pdf";

    private static final String[] ALL_TEST_FILES = {TEST_FILE_PDF_01, TEST_FILE_PDF_02};

    private static PdfProvider providerUnderTest;
    private static ImagingProvider imagingProvider;
    private static Map<String, File> testFiles;

    // -----------------------------------------------------------------------

    @BeforeAll
    static void initializeTestFiles() {

        providerUnderTest = PdfProviderFactory.getInstance().getProvider();
        // Used to test, whether pixel width is correct
        imagingProvider = ImagingFactory.getInstance().getProvider();
        testFiles = TestFileHelper.cloneTestFiles(Arrays.stream(ALL_TEST_FILES));
    }

    @AfterAll
    static void clearTestFiles() {

        if (CLEAR_OUTPUT_FILES) {
            testFiles.values().forEach(File::delete);
        }
    }

    @Test
    void testThat_createThumbNail_works_for_all_test_files_using_output_stream() {

        int thumbPixelMaxSize = 180;
        for (File file : testFiles.values()) {
            try {
                createThumbNailUsingOutputStream(thumbPixelMaxSize, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // -----------------------------------------------------------------------

    private void createThumbNailUsingOutputStream(int thumbPixelMaxSize, File file) throws Exception {

        // arrange
        File outFile = File.createTempFile("provider-thumb-", ".jpg");
        if (CLEAR_OUTPUT_FILES) {
            outFile.deleteOnExit();
        }

        // act
        try (FileOutputStream outputStream = new FileOutputStream(outFile)) {
            providerUnderTest.createThumbNail(file, outputStream,
                    "image/jpeg", thumbPixelMaxSize, thumbPixelMaxSize,
                    ConversionCommand.CompressionQuality.LOSSY_MEDIUM, ConversionCommand.SpeedHint.SPEED);
        }

        // assert
        assertThat(outFile.exists()).isTrue();
        FileInfo fileInfo = imagingProvider.fetchFileInfo(outFile);
        assertThat(fileInfo.getMimeType()).isEqualTo("image/jpeg");
        assertThat(fileInfo.getBitsPerPixel()).isEqualTo(24);
        assertThat(fileInfo.getWidth()).isLessThanOrEqualTo(thumbPixelMaxSize);
        assertThat(fileInfo.getHeight()).isLessThanOrEqualTo(thumbPixelMaxSize);
    }
}