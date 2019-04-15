package com.giraone.imaging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static com.giraone.imaging.FileTypeDetector.FileType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the FileTypeDetector implementation.
 */
class FileTypeDetectorTest {

    private static final Logger LOG = LogManager.getLogger(FileTypeDetectorTest.class);

    private static final String TEST_FILE_JPEG_01 = "image-01.jpg";
    private static final String TEST_FILE_JPEG_EXIF_01 = "image-exif-01.jpg";
    private static final String TEST_FILE_PNG_01 = "image-01.png";
    private static final String TEST_FILE_PDF_01 = "document-01-PDF-1.3.pdf";

    // -----------------------------------------------------------------------

    @Test
    void test_jpeg() {
        this.checkExpectedType(TEST_FILE_JPEG_01, JPEG);
    }

    @Test
    void test_exif_jpeg() {
        this.checkExpectedType(TEST_FILE_JPEG_EXIF_01, JPEG);
    }

    @Test
    void test_png() {
        this.checkExpectedType(TEST_FILE_PNG_01, PNG);
    }

    @Test
    void test_pdf()  {
        this.checkExpectedType(TEST_FILE_PDF_01, PDF);
    }

    private void checkExpectedType(String fileName, FileTypeDetector.FileType expectedFileType) {

        FileTypeDetector.FileType detectedFileType = null;
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (is != null) {
                detectedFileType = FileTypeDetector.getInstance().getFileType(is);
                LOG.debug("FileTypeDetector.getFileType: {} -> {}", fileName, detectedFileType);
            } else {
                System.err.println("Cannot read test file \"" + fileName + "\"");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        assertEquals(expectedFileType, detectedFileType);
    }
}