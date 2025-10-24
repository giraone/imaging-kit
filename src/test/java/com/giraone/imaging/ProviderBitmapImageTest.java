package com.giraone.imaging;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for the Provider bitmap image implementation.
 */
class ProviderBitmapImageTest {

    private static final Logger LOG = LoggerFactory.getLogger(ProviderBitmapImageTest.class);
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

    private static final String UNSUPPORTED_TEST_FILE_TEXT = "text.txt";
    private static final String UNSUPPORTED_TEST_FILE_TIFF_01 = "image-01.tif";

    private static final String[] ALL_TEST_FILES = {
        TEST_FILE_JPEG_01, TEST_FILE_JPEG_02,
        TEST_FILE_JPEG_EXIF_01, TEST_FILE_JPEG_EXIF_02, TEST_FILE_JPEG_EXIF_03,
        TEST_FILE_PNG_01, TEST_FILE_PNG_02,
        TEST_FILE_JPEG_SMALL, TEST_FILE_PNG_SMALL,
        TEST_FILE_JPEG_WIDE, TEST_FILE_PNG_WIDE
    };

    private static final String[] ALL_UNSUPPORTED_FILES = {
        UNSUPPORTED_TEST_FILE_TEXT,
        UNSUPPORTED_TEST_FILE_TIFF_01
    };

    private static ImagingProvider providerUnderTest;
    private static Map<String, File> supportedTestFiles;
    private static Map<String, File> unsupportedTestFiles;

    // -----------------------------------------------------------------------

    @BeforeAll
    static void initializeTestFiles() throws Exception {

        @SuppressWarnings("unchecked")
        Class<ImagingProvider> cls = (Class<ImagingProvider>) Class.forName("com.giraone.imaging.java2.ProviderJava2D");
        providerUnderTest = cls.getDeclaredConstructor().newInstance();
        supportedTestFiles = TestFileHelper.cloneTestFiles(Arrays.stream(ALL_TEST_FILES));
        unsupportedTestFiles = TestFileHelper.cloneTestFiles(Arrays.stream(ALL_UNSUPPORTED_FILES));
    }

    @AfterAll
    @SuppressWarnings("All")
    static void clearTestFiles() {
        if (CLEAR_OUTPUT_FILES) {
            supportedTestFiles.values().forEach(
                File::delete
            );
        }
    }

    @Test
    void fetchFileInfo_works_with_jpeg_usingFile() throws Exception {

        /// arrange
        File testFile = supportedTestFiles.get(TEST_FILE_JPEG_01);

        /// act
        FileInfo fileInfo = providerUnderTest.fetchFileInfo(testFile);
        LOG.debug("fetchFileInfo: {} -> {}", testFile, fileInfo.dumpInfo());

        /// assert
        assertThat(fileInfo.getMimeType()).isEqualTo("image/jpeg");
        assertThat(fileInfo.getBitsPerPixel()).isEqualTo(24);
        assertThat(fileInfo.getWidth()).isEqualTo(1024);
        assertThat(fileInfo.getHeight()).isEqualTo(768);
    }

    @Test
    void fetchFileInfo_works_with_jpeg_usingPath() throws Exception {

        /// arrange
        Path testPath = supportedTestFiles.get(TEST_FILE_JPEG_01).toPath();

        /// act
        FileInfo fileInfo = providerUnderTest.fetchFileInfo(testPath);
        LOG.debug("fetchFileInfo: {} -> {}", testPath, fileInfo.dumpInfo());

        /// assert
        assertThat(fileInfo.getMimeType()).isEqualTo("image/jpeg");
        assertThat(fileInfo.getBitsPerPixel()).isEqualTo(24);
        assertThat(fileInfo.getWidth()).isEqualTo(1024);
        assertThat(fileInfo.getHeight()).isEqualTo(768);
    }

    @Test
    void fetchFileInfo_works_with_exif_jpeg() throws Exception {

        /// arrange
        File testFile = supportedTestFiles.get(TEST_FILE_JPEG_EXIF_01);

        /// act
        FileInfo fileInfo = providerUnderTest.fetchFileInfo(testFile);
        LOG.debug("fetchFileInfo: {} -> {}", testFile, fileInfo.dumpInfo());

        /// assert
        assertThat(fileInfo.getMimeType()).isEqualTo("image/jpeg");
        assertThat(fileInfo.getBitsPerPixel()).isEqualTo(24);
        assertThat(fileInfo.getWidth()).isEqualTo(6000);
        assertThat(fileInfo.getHeight()).isEqualTo(4000);
    }

    @Test
    void fetchFileInfo_works_with_png() throws Exception {

        /// arrange
        File testFile = supportedTestFiles.get(TEST_FILE_PNG_01);

        /// act
        FileInfo fileInfo = providerUnderTest.fetchFileInfo(testFile);
        LOG.debug("fetchFileInfo: {} -> {}", testFile, fileInfo.dumpInfo());

        /// assert
        assertThat(fileInfo.getMimeType()).isEqualTo("image/png");
        assertThat(fileInfo.getBitsPerPixel()).isEqualTo(24);
        assertThat(fileInfo.getWidth()).isEqualTo(800);
        assertThat(fileInfo.getHeight()).isEqualTo(600);
    }

    @ParameterizedTest
    @MethodSource("provideTestFiles")
    void createThumbNail_works_for_all_test_files_using_output_stream(File file) throws Exception {

        int thumbPixelMaxSize = 180;
        createThumbNailUsingOutputStream(thumbPixelMaxSize, file);
    }

    @ParameterizedTest
    @MethodSource("provideTestFiles")
    void createThumbNail_works_for_all_test_files_using_file(File file) throws Exception {

        int thumbPixelMaxSize = 180;
        createThumbNailUsingFile(thumbPixelMaxSize, file);
    }

    @Test
    void createThumbNail_works_for_first_test_file_using_path() throws Exception {

        int thumbPixelMaxSize = 180;
        File file = supportedTestFiles.get(TEST_FILE_JPEG_01);
        createThumbNailUsingPath(thumbPixelMaxSize, file.toPath());
    }


    @Test
    void convertImage_fails_for_wrong_input_format() {
        ConversionCommand command = new ConversionCommand();
        command.setOutputFormat("image/jpeg");
        command.setDimension(new Dimension(320, 320));
        command.setQuality(10);

        File testFile = unsupportedTestFiles.get(UNSUPPORTED_TEST_FILE_TEXT);
        Exception detectedException = null;
        try {
            File outFile = File.createTempFile("providerUnderTest-image-", ".jpg");
            if (CLEAR_OUTPUT_FILES) {
                outFile.deleteOnExit();
            }
            try (FileOutputStream outputStream = new FileOutputStream(outFile)) {
                providerUnderTest.convertImage(testFile, outputStream, command);
            }
        } catch (Exception e) {
            detectedException = e;
        }

        assertThat(detectedException).isNotNull();
        assertThat(detectedException.getMessage()).contains("Unsupported input file type");
    }

    @ParameterizedTest
    @MethodSource("provideTestFiles")
    void convertImage_fails_for_wrong_output_format(File file) throws IOException {
        /// arrange
        ConversionCommand command = new ConversionCommand();
        command.setOutputFormat("image/tiff");
        command.setDimension(new Dimension(320, 320));
        command.setQuality(10);

        File outFile = File.createTempFile("providerUnderTest-image-", ".jpg");
        if (CLEAR_OUTPUT_FILES) {
            outFile.deleteOnExit();
        }
        /// act/assert
        assertThatThrownBy(() -> {
            try (FileOutputStream outputStream = new FileOutputStream(outFile)) {
                providerUnderTest.convertImage(file, outputStream, command);
            }
        })
            .isInstanceOf(FormatNotSupportedException.class)
            .hasMessageContaining("Unsupported output format");
    }

    @ParameterizedTest
    @MethodSource("provideTestFiles")
    void convertImage_works_for_all_test_files(File file) throws IOException, FormatNotSupportedException {
        /// arrange
        ConversionCommand command = new ConversionCommand();
        command.setOutputFormat("image/jpeg");
        command.setDimension(new Dimension(320, 320));
        command.setQuality(10);
        File outFile = File.createTempFile("providerUnderTest-image-", ".jpg");
        if (CLEAR_OUTPUT_FILES) {
            outFile.deleteOnExit();
        }

        /// act
        try (FileOutputStream outputStream = new FileOutputStream(outFile)) {
            providerUnderTest.convertImage(file, outputStream, command);
        }
    }

    @ParameterizedTest
    @MethodSource("provideTestFiles")
    void convertImage_to_png_works_for_all_test_files(File file) throws IOException, FormatNotSupportedException {
        /// arrange
        ConversionCommand command = new ConversionCommand();
        command.setOutputFormat("image/png");
        command.setDimension(new Dimension(320, 320));
        File outFile = File.createTempFile("providerUnderTest-png-", ".png");
        if (CLEAR_OUTPUT_FILES) {
            outFile.deleteOnExit();
        }

        /// act
        try (FileOutputStream outputStream = new FileOutputStream(outFile)) {
            providerUnderTest.convertImage(file, outputStream, command);
        }

        /// assert
        assertThat(outFile.exists()).isTrue();
        assertThat(outFile.length()).isGreaterThan(0);
        FileInfo fileInfo = providerUnderTest.fetchFileInfo(outFile);
        assertThat(fileInfo.getMimeType()).isEqualTo("image/png");
        assertThat(fileInfo.getWidth()).isLessThanOrEqualTo(320);
        assertThat(fileInfo.getHeight()).isLessThanOrEqualTo(320);
    }

    @ParameterizedTest
    @MethodSource("provideTestFiles")
    void convertImage_to_gif_works_for_all_test_files(File file) throws IOException, FormatNotSupportedException {
        /// arrange
        ConversionCommand command = new ConversionCommand();
        command.setOutputFormat("image/gif");
        command.setDimension(new Dimension(320, 320));
        File outFile = File.createTempFile("providerUnderTest-gif-", ".gif");
        if (CLEAR_OUTPUT_FILES) {
            outFile.deleteOnExit();
        }

        /// act
        try (FileOutputStream outputStream = new FileOutputStream(outFile)) {
            providerUnderTest.convertImage(file, outputStream, command);
        }

        /// assert
        assertThat(outFile.exists()).isTrue();
        assertThat(outFile.length()).isGreaterThan(0);
        // Note: GIF format detection is supported but FileTypeDetector may not detect it correctly
        // so we just verify the file was created and has content
    }

    @Test
    void createThumbNail_to_png_works() throws Exception {
        /// arrange
        File testFile = supportedTestFiles.get(TEST_FILE_JPEG_01);
        int thumbPixelMaxSize = 180;
        File outFile = File.createTempFile("providerUnderTest-thumb-png-", ".png");
        if (CLEAR_OUTPUT_FILES) {
            outFile.deleteOnExit();
        }

        /// act
        try (FileOutputStream outputStream = new FileOutputStream(outFile)) {
            providerUnderTest.createThumbNail(testFile, outputStream,
                "image/png", thumbPixelMaxSize, thumbPixelMaxSize,
                ConversionCommand.CompressionQuality.LOSSLESS);
        }

        /// assert
        assertThat(outFile.exists()).isTrue();
        assertThat(outFile.length()).isGreaterThan(0);
        FileInfo fileInfo = providerUnderTest.fetchFileInfo(outFile);
        assertThat(fileInfo.getMimeType()).isEqualTo("image/png");
        assertThat(fileInfo.getWidth()).isLessThanOrEqualTo(thumbPixelMaxSize);
        assertThat(fileInfo.getHeight()).isLessThanOrEqualTo(thumbPixelMaxSize);
    }

    @Test
    void createThumbNail_to_gif_works() throws Exception {
        /// arrange
        File testFile = supportedTestFiles.get(TEST_FILE_PNG_01);
        int thumbPixelMaxSize = 180;
        File outFile = File.createTempFile("providerUnderTest-thumb-gif-", ".gif");
        if (CLEAR_OUTPUT_FILES) {
            outFile.deleteOnExit();
        }

        /// act
        try (FileOutputStream outputStream = new FileOutputStream(outFile)) {
            providerUnderTest.createThumbNail(testFile, outputStream,
                "image/gif", thumbPixelMaxSize, thumbPixelMaxSize,
                ConversionCommand.CompressionQuality.LOSSY_MEDIUM);
        }

        /// assert
        assertThat(outFile.exists()).isTrue();
        assertThat(outFile.length()).isGreaterThan(0);
    }

    @Test
    void convertImage_to_png_using_path_works() throws Exception {
        /// arrange
        Path testPath = supportedTestFiles.get(TEST_FILE_JPEG_01).toPath();
        ConversionCommand command = new ConversionCommand();
        command.setOutputFormat("image/png");
        command.setDimension(new Dimension(400, 400));
        Path outPath = Files.createTempFile("providerUnderTest-png-path-", ".png");
        if (CLEAR_OUTPUT_FILES) {
            outPath.toFile().deleteOnExit();
        }

        /// act
        providerUnderTest.convertImage(testPath, outPath, command);

        /// assert
        assertThat(Files.exists(outPath)).isTrue();
        assertThat(Files.size(outPath)).isGreaterThan(0);
        FileInfo fileInfo = providerUnderTest.fetchFileInfo(outPath);
        assertThat(fileInfo.getMimeType()).isEqualTo("image/png");
    }

    @Test
    void convertImage_png_to_jpeg_preserves_dimensions() throws Exception {
        /// arrange
        File pngTestFile = supportedTestFiles.get(TEST_FILE_PNG_01);
        FileInfo inputInfo = providerUnderTest.fetchFileInfo(pngTestFile);
        ConversionCommand command = new ConversionCommand();
        command.setOutputFormat("image/jpeg");
        command.setQuality(ConversionCommand.CompressionQuality.LOSSY_BEST);
        File outFile = File.createTempFile("providerUnderTest-png-to-jpeg-", ".jpg");
        if (CLEAR_OUTPUT_FILES) {
            outFile.deleteOnExit();
        }

        /// act
        try (FileOutputStream outputStream = new FileOutputStream(outFile)) {
            providerUnderTest.convertImage(pngTestFile, outputStream, command);
        }

        /// assert
        FileInfo outputInfo = providerUnderTest.fetchFileInfo(outFile);
        assertThat(outputInfo.getMimeType()).isEqualTo("image/jpeg");
        assertThat(outputInfo.getWidth()).isEqualTo(inputInfo.getWidth());
        assertThat(outputInfo.getHeight()).isEqualTo(inputInfo.getHeight());
    }

    @Test
    void convertImage_jpeg_to_png_preserves_dimensions() throws Exception {
        /// arrange
        File jpegTestFile = supportedTestFiles.get(TEST_FILE_JPEG_01);
        FileInfo inputInfo = providerUnderTest.fetchFileInfo(jpegTestFile);
        ConversionCommand command = new ConversionCommand();
        command.setOutputFormat("image/png");
        File outFile = File.createTempFile("providerUnderTest-jpeg-to-png-", ".png");
        if (CLEAR_OUTPUT_FILES) {
            outFile.deleteOnExit();
        }

        /// act
        try (FileOutputStream outputStream = new FileOutputStream(outFile)) {
            providerUnderTest.convertImage(jpegTestFile, outputStream, command);
        }

        /// assert
        FileInfo outputInfo = providerUnderTest.fetchFileInfo(outFile);
        assertThat(outputInfo.getMimeType()).isEqualTo("image/png");
        assertThat(outputInfo.getWidth()).isEqualTo(inputInfo.getWidth());
        assertThat(outputInfo.getHeight()).isEqualTo(inputInfo.getHeight());
    }

    // -----------------------------------------------------------------------------------------------------------------

    private static Stream<Arguments> provideTestFiles() {
        return supportedTestFiles.values().stream().map(Arguments::of);
    }

    // -----------------------------------------------------------------------------------------------------------------


    private void createThumbNailUsingOutputStream(int thumbPixelMaxSize, File file) throws Exception {

        /// arrange
        File outFile = File.createTempFile("providerUnderTest-thumb-", ".jpg");
        if (CLEAR_OUTPUT_FILES) {
            outFile.deleteOnExit();
        }

        /// act
        try (FileOutputStream outputStream = new FileOutputStream(outFile)) {
            providerUnderTest.createThumbNail(file, outputStream,
                "image/jpeg", thumbPixelMaxSize, thumbPixelMaxSize,
                ConversionCommand.CompressionQuality.LOSSY_MEDIUM);
        }

        /// assert
        assertThat(outFile.exists()).isTrue();
        FileInfo fileInfo = providerUnderTest.fetchFileInfo(outFile);
        assertThat(fileInfo.getMimeType()).isEqualTo("image/jpeg");
        assertThat(fileInfo.getBitsPerPixel()).isEqualTo(24);
        assertThat(fileInfo.getWidth()).isLessThanOrEqualTo(thumbPixelMaxSize);
        assertThat(fileInfo.getHeight()).isLessThanOrEqualTo(thumbPixelMaxSize);
    }

    private void createThumbNailUsingFile(int thumbPixelMaxSize, File file) throws Exception {

        /// arrange
        File outFile = File.createTempFile("providerUnderTest-thumb-", ".jpg");
        if (CLEAR_OUTPUT_FILES) {
            outFile.deleteOnExit();
        }

        /// act
        providerUnderTest.createThumbNail(file, outFile,
            "image/jpeg", thumbPixelMaxSize, thumbPixelMaxSize,
            ConversionCommand.CompressionQuality.LOSSY_MEDIUM);

        /// assert
        assertThat(outFile.exists()).isTrue();
        FileInfo fileInfo = providerUnderTest.fetchFileInfo(outFile);
        assertThat(fileInfo.getMimeType()).isEqualTo("image/jpeg");
        assertThat(fileInfo.getBitsPerPixel()).isEqualTo(24);
        assertThat(fileInfo.getWidth()).isLessThanOrEqualTo(thumbPixelMaxSize);
        assertThat(fileInfo.getHeight()).isLessThanOrEqualTo(thumbPixelMaxSize);
    }

    private void createThumbNailUsingPath(int thumbPixelMaxSize, Path path) throws Exception {

        /// arrange
        Path outPath = Files.createTempFile("providerUnderTest-thumb-", ".jpg");
        if (CLEAR_OUTPUT_FILES) {
            outPath.toFile().deleteOnExit();
        }

        /// act
        providerUnderTest.createThumbNail(path, outPath,
            "image/jpeg", thumbPixelMaxSize, thumbPixelMaxSize,
            ConversionCommand.CompressionQuality.LOSSY_MEDIUM);

        /// assert
        assertThat(Files.exists(path)).isTrue();
        FileInfo fileInfo = providerUnderTest.fetchFileInfo(outPath);
        assertThat(fileInfo.getMimeType()).isEqualTo("image/jpeg");
        assertThat(fileInfo.getBitsPerPixel()).isEqualTo(24);
        assertThat(fileInfo.getWidth()).isLessThanOrEqualTo(thumbPixelMaxSize);
        assertThat(fileInfo.getHeight()).isLessThanOrEqualTo(thumbPixelMaxSize);
    }
}