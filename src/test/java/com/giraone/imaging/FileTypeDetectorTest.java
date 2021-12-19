package com.giraone.imaging;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

import static com.giraone.imaging.FileTypeDetector.FileType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the FileTypeDetector implementation.
 */
class FileTypeDetectorTest {

    private static final Logger LOG = LoggerFactory.getLogger(FileTypeDetectorTest.class);

    private static final String TEST_FILE_JPEG_01 = "image-01.jpg";
    private static final String TEST_FILE_JPEG_02 = "image-02.jpg";
    private static final String TEST_FILE_JPEG_EXIF_01 = "image-exif-01.jpg";
    private static final String TEST_FILE_JPEG_EXIF_02 = "image-exif-02.jpg";
    private static final String TEST_FILE_JPEG_EXIF_03 = "image-exif-03.jpg";
    private static final String TEST_FILE_PNG_01 = "image-01.png";
    private static final String TEST_FILE_PNG_02 = "image-02.png";
    private static final String TEST_FILE_TIFF_01 = "image-01.tif";
    private static final String TEST_FILE_TIFF_02 = "image-02.tif";
    private static final String TEST_FILE_TIFF_03 = "image-03.tif";
    private static final String TEST_FILE_BMP_01 = "image-01.bmp";
    private static final String TEST_FILE_DICOM_01 = "image-01.dcm";
    private static final String TEST_FILE_PDF_01 = "document-01-PDF-1.3.pdf";
    private static final String TEST_FILE_PDF_02 = "document-02-PDF-1.4.pdf";

    // -----------------------------------------------------------------------

    @Test
    void jpegIsDetected() {
        this.checkExpectedType(TEST_FILE_JPEG_01, JPEG);
        this.checkExpectedType(TEST_FILE_JPEG_02, JPEG);
    }

    @Test
    void exifJpegIsDetected() {
        this.checkExpectedType(TEST_FILE_JPEG_EXIF_01, JPEG);
        this.checkExpectedType(TEST_FILE_JPEG_EXIF_02, JPEG);
        this.checkExpectedType(TEST_FILE_JPEG_EXIF_03, JPEG);
    }

    @Test
    void pngIsDetected() {
        this.checkExpectedType(TEST_FILE_PNG_01, PNG);
        this.checkExpectedType(TEST_FILE_PNG_02, PNG);
    }

    @Test
    void tiffIsDetected()  {
        this.checkExpectedType(TEST_FILE_TIFF_01, TIFF);
        this.checkExpectedType(TEST_FILE_TIFF_02, TIFF);
        this.checkExpectedType(TEST_FILE_TIFF_03, TIFF);
    }

    @Test
    void bmpIsDetected()  {
        this.checkExpectedType(TEST_FILE_BMP_01, BMP);
    }

    @Test
    void dicomIsDetected()  {
        this.checkExpectedType(TEST_FILE_DICOM_01, DICOM);
    }

    @Test
    void pdfIsDetected()  {
        this.checkExpectedType(TEST_FILE_PDF_01, PDF);
        this.checkExpectedType(TEST_FILE_PDF_02, PDF);
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