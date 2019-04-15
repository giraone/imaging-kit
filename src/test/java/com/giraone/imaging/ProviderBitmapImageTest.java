package com.giraone.imaging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Tests for the Provider bitmap image implementation.
 */
class ProviderBitmapImageTest {

    private static final Logger LOG = LogManager.getLogger(ProviderBitmapImageTest.class);
    private static final boolean CLEAR_OUTPUT_FILES = true;

    private static final String TEST_FILE_JPEG_01 = "image-01.jpg";
    private static final String TEST_FILE_JPEG_02 = "image-02.jpg";
    private static final String TEST_FILE_JPEG_EXIF_01 = "image-exif-01.jpg";
    private static final String TEST_FILE_JPEG_EXIF_02 = "image-exif-02.jpg";
    private static final String TEST_FILE_JPEG_EXIF_03 = "image-exif-03.jpg";
    private static final String TEST_FILE_PNG_01 = "image-01.png";
    private static final String TEST_FILE_PNG_02 = "image-02.png";

    private static final String TEST_FILE_JPEG_SMALL = "small.jpg";
    private static final String TEST_FILE_PNG_SMALL = "small.png";
    private static final String TEST_FILE_JPEG_WIDE = "wide.jpg";
    private static final String TEST_FILE_PNG_WIDE = "wide.png";

    private static final String[] ALL_TEST_FILES = {
            TEST_FILE_JPEG_01, TEST_FILE_JPEG_02,
            TEST_FILE_JPEG_EXIF_01, TEST_FILE_JPEG_EXIF_02, TEST_FILE_JPEG_EXIF_03,
            TEST_FILE_PNG_01, TEST_FILE_PNG_02,
            TEST_FILE_JPEG_SMALL, TEST_FILE_PNG_SMALL,
            TEST_FILE_JPEG_WIDE, TEST_FILE_PNG_WIDE
    };

    private static ImagingProvider providerUnderTest;
    private static Map<String, File> testFiles;

    // -----------------------------------------------------------------------

    @BeforeAll
    static void initializeTestFiles() throws Exception {

        @SuppressWarnings("unchecked")
        Class<ImagingProvider> cls = (Class<ImagingProvider>) Class.forName("com.giraone.imaging.java2.ProviderJava2D");
        providerUnderTest = cls.newInstance();
        testFiles = TestFileHelper.cloneTestFiles(Arrays.stream(ALL_TEST_FILES));
    }

    @AfterAll
    static void clearTestFiles() {
        if (CLEAR_OUTPUT_FILES) {
            testFiles.values().forEach(File::delete);
        }
    }

    @Test
    void testThat_fetchFileInfo_works_with_jpeg() throws Exception {

        // arrange
        File testFile = testFiles.get(TEST_FILE_JPEG_01);

        // act
        FileInfo fileInfo = providerUnderTest.fetchFileInfo(testFile);
        LOG.debug("fetchFileInfo: {} -> {}", testFile, fileInfo.dumpInfo());

        // assert
        assertThat(fileInfo.getMimeType()).isEqualTo("image/jpeg");
        assertThat(fileInfo.getBitsPerPixel()).isEqualTo(24);
        assertThat(fileInfo.getWidth()).isEqualTo(1024);
        assertThat(fileInfo.getHeight()).isEqualTo(768);
    }

    @Test
    void testThat_fetchFileInfo_works_with_exif_jpeg() throws Exception {

        // arrange
        File testFile = testFiles.get(TEST_FILE_JPEG_EXIF_01);

        // act
        FileInfo fileInfo = providerUnderTest.fetchFileInfo(testFile);
        LOG.debug("fetchFileInfo: {} -> {}", testFile, fileInfo.dumpInfo());

        // assert
        assertThat(fileInfo.getMimeType()).isEqualTo("image/jpeg");
        assertThat(fileInfo.getBitsPerPixel()).isEqualTo(24);
        assertThat(fileInfo.getWidth()).isEqualTo(6000);
        assertThat(fileInfo.getHeight()).isEqualTo(4000);
    }

    @Test
    void testThat_fetchFileInfo_works_with_png() throws Exception {

        // arrange
        File testFile = testFiles.get(TEST_FILE_PNG_01);

        // act
        FileInfo fileInfo = providerUnderTest.fetchFileInfo(testFile);
        LOG.debug("fetchFileInfo: {} -> {}", testFile, fileInfo.dumpInfo());

        // assert
        assertThat(fileInfo.getMimeType()).isEqualTo("image/png");
        assertThat(fileInfo.getBitsPerPixel()).isEqualTo(24);
        assertThat(fileInfo.getWidth()).isEqualTo(800);
        assertThat(fileInfo.getHeight()).isEqualTo(600);
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

    @Test
    void testThat_createThumbNail_works_for_all_test_files_using_file() {

        int thumbPixelMaxSize = 180;
        for (File file : testFiles.values()) {
            try {
                createThumbNailUsingFile(thumbPixelMaxSize, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void testThat_convertImage_works_for_all_test_files() {
        ConversionCommand command = new ConversionCommand();
        command.setOutputFormat("image/jpeg");
        command.setDimension(new Dimension(320, 320));
        command.setQuality(10);

        testFiles.values().forEach((file) -> {
            try {
                File outFile = File.createTempFile("providerUnderTest-image-", ".jpg");
                if (CLEAR_OUTPUT_FILES) {
                    outFile.deleteOnExit();
                }

                try (FileOutputStream outputStream = new FileOutputStream(outFile)) {
                    providerUnderTest.convertImage(file, outputStream, command);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // -----------------------------------------------------------------------

    private void createThumbNailUsingOutputStream(int thumbPixelMaxSize, File file) throws Exception {

        // arrange
        File outFile = File.createTempFile("providerUnderTest-thumb-", ".jpg");
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
        FileInfo fileInfo = providerUnderTest.fetchFileInfo(outFile);
        assertThat(fileInfo.getMimeType()).isEqualTo("image/jpeg");
        assertThat(fileInfo.getBitsPerPixel()).isEqualTo(24);
        assertThat(fileInfo.getWidth()).isLessThanOrEqualTo(thumbPixelMaxSize);
        assertThat(fileInfo.getHeight()).isLessThanOrEqualTo(thumbPixelMaxSize);
    }

    private void createThumbNailUsingFile(int thumbPixelMaxSize, File file) throws Exception {

        // arrange
        File outFile = File.createTempFile("providerUnderTest-thumb-", ".jpg");
        if (CLEAR_OUTPUT_FILES) {
            outFile.deleteOnExit();
        }

        // act
        providerUnderTest.createThumbNail(file, outFile,
                "image/jpeg", thumbPixelMaxSize, thumbPixelMaxSize,
                ConversionCommand.CompressionQuality.LOSSY_MEDIUM, ConversionCommand.SpeedHint.SPEED);

        // assert
        assertThat(outFile.exists()).isTrue();
        FileInfo fileInfo = providerUnderTest.fetchFileInfo(outFile);
        assertThat(fileInfo.getMimeType()).isEqualTo("image/jpeg");
        assertThat(fileInfo.getBitsPerPixel()).isEqualTo(24);
        assertThat(fileInfo.getWidth()).isLessThanOrEqualTo(thumbPixelMaxSize);
        assertThat(fileInfo.getHeight()).isLessThanOrEqualTo(thumbPixelMaxSize);
    }
}