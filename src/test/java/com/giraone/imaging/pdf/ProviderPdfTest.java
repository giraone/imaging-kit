package com.giraone.imaging.pdf;

import com.giraone.imaging.ConversionCommand;
import com.giraone.imaging.FileInfo;
import com.giraone.imaging.ImagingFactory;
import com.giraone.imaging.ImagingProvider;
import com.giraone.imaging.TestFileHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import static com.giraone.imaging.ConversionCommand.MIME_TYPE_JPEG;
import static com.giraone.imaging.TestFileHelper.cloneTestFile;
import static com.giraone.imaging.TestFileHelper.readTestFile;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the Provider PDF implementation.
 */
class ProviderPdfTest {

    private static final Logger LOG = LoggerFactory.getLogger(ProviderPdfTest.class);

    private static final boolean CLEAR_OUTPUT_FILES = true;

    private static final String TEST_FILE_PDF_01 = "document-01-PDF-1.3.pdf";
    private static final String TEST_FILE_PDF_02 = "document-02-PDF-1.4.pdf";
    private static final String TEST_FILE_JPEG_01 = "image-01.jpg";
    private static final String TEST_FILE_JPEG_02 = "image-02.jpg";
    private static final String TEST_FILE_PNG_01 = "image-01.png";
    private static final String TEST_FILE_PNG_02 = "image-02.png";

    private static final String[] ALL_TEST_FILES = {TEST_FILE_PDF_01, TEST_FILE_PDF_02};

    private static PdfProvider providerUnderTest;
    private static ImagingProvider imagingProvider;
    private static Map<String, File> testFiles;

    // -----------------------------------------------------------------------

    @BeforeAll
    static void initializeTestFiles() {

        providerUnderTest = PdfProviderFactory.getInstance().getProvider();
        // Used to test, whether creates dimensions are correct
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
    void countPages_works_for_all_test_files() throws Exception {

        /// act
        int pages1 = providerUnderTest.countPages(testFiles.get(TEST_FILE_PDF_01));
        int pages2 = providerUnderTest.countPages(testFiles.get(TEST_FILE_PDF_02));

        /// assert
        assertThat(pages1).isEqualTo(3);
        assertThat(pages2).isEqualTo(1);
    }

    @Test
    void getDocumentInformation_works_for_all_test_files() throws Exception {

        /// act
        PdfDocumentInformation info1 = providerUnderTest.getDocumentInformation(testFiles.get(TEST_FILE_PDF_01));
        PdfDocumentInformation info2 = providerUnderTest.getDocumentInformation(testFiles.get(TEST_FILE_PDF_02));

        /// assert
        assertThat(info1.getProducer()).isEqualTo("FPDF 1.53");
        assertThat(info2.getProducer()).isEqualTo("OpenOffice 4.1.2");
    }

    @Test
    void createThumbnail_works_for_all_test_files_using_output_stream() {

        int thumbPixelMaxSize = 180;
        for (File file : testFiles.values()) {
            try {
                createThumbnailUsingOutputStream(thumbPixelMaxSize, file);
            } catch (Exception e) {
                LOG.error("Test createThumbnail_works_for_all_test_files_using_output_stream failed!", e);
            }
        }
    }

    @Test
    void createThumbnails_works() throws Exception {

        /// arrange
        File inputFile = new File("src/test/resources/" + TEST_FILE_PDF_01);
        File outputFile1 = File.createTempFile("pdf-to-thumb-", ".jpg");
        File outputFile2 = File.createTempFile("pdf-to-thumb-", ".jpg");
        outputFile1.deleteOnExit();
        outputFile2.deleteOnExit();
        ConversionCommand.CompressionQuality quality = ConversionCommand.CompressionQuality.LOSSY_BEST;
        int thumbPixelMaxSize = 600;
        ConversionCommand conversionCommand1 = ConversionCommand.buildConversionCommand(
            outputFile1, MIME_TYPE_JPEG, thumbPixelMaxSize, thumbPixelMaxSize, quality);
        ConversionCommand conversionCommand2 = ConversionCommand.buildConversionCommand(
            outputFile2, MIME_TYPE_JPEG, thumbPixelMaxSize / 2, thumbPixelMaxSize / 2, quality);
        /// act
        providerUnderTest.createThumbnails(inputFile, new ConversionCommand[] { conversionCommand1, conversionCommand2 });
        /// assert
        assertThat(outputFile1.exists());
        assertThat(outputFile2.exists());
        FileInfo fileInfo1 = imagingProvider.fetchFileInfo(outputFile1);
        assertThat(fileInfo1.getWidth()).isEqualTo(424);
        assertThat(fileInfo1.getHeight()).isEqualTo(600);
        FileInfo fileInfo2 = imagingProvider.fetchFileInfo(outputFile2);
        assertThat(fileInfo2.getWidth()).isEqualTo(212);
        assertThat(fileInfo2.getHeight()).isEqualTo(300);
    }

    @Test
    void createPdfFromImages_works() throws Exception {

        /// arrange
        File image1 = cloneTestFile(TEST_FILE_JPEG_01);
        File image2 = cloneTestFile(TEST_FILE_JPEG_02);
        File image3 = cloneTestFile(TEST_FILE_PNG_01);
        File image4 = cloneTestFile(TEST_FILE_PNG_02);
        File[] imageFiles = new File[]{image1, image2, image3, image4};
        File pdfFile = File.createTempFile("pdf-from-image", ".pdf");
        pdfFile.deleteOnExit();
        PdfDocumentInformation documentInformation = new PdfDocumentInformation();
        documentInformation.setTitle("title");
        documentInformation.setSubject("subject");
        documentInformation.setAuthor("author");
        documentInformation.setKeywords("keyword1, keyword2");
        documentInformation.setProducer("producer");
        documentInformation.setCreator("creator");
        Calendar now = new GregorianCalendar();
        documentInformation.setCreationDate(now);
        documentInformation.setModificationDate(now);
        /// act
        providerUnderTest.createPdfFromImages(imageFiles, documentInformation, pdfFile);

        /// assert
        assertThat(pdfFile.length()).isGreaterThan(1000);
        assertThat(providerUnderTest.countPages(pdfFile)).isEqualTo(imageFiles.length);
        assertThat(providerUnderTest.getDocumentInformation(pdfFile).getTitle()).isEqualTo(documentInformation.getTitle());
        assertThat(providerUnderTest.getDocumentInformation(pdfFile).getSubject()).isEqualTo(documentInformation.getSubject());
        assertThat(providerUnderTest.getDocumentInformation(pdfFile).getAuthor()).isEqualTo(documentInformation.getAuthor());
        assertThat(providerUnderTest.getDocumentInformation(pdfFile).getKeywords()).isEqualTo(documentInformation.getKeywords());
        assertThat(providerUnderTest.getDocumentInformation(pdfFile).getProducer()).isEqualTo(documentInformation.getProducer());
        assertThat(providerUnderTest.getDocumentInformation(pdfFile).getCreator()).isEqualTo(documentInformation.getCreator());
        assertThat(providerUnderTest.getDocumentInformation(pdfFile).getCreationDate().getTime()).isCloseTo(documentInformation.getCreationDate().getTime(), 1000);
        assertThat(providerUnderTest.getDocumentInformation(pdfFile).getModificationDate().getTime()).isCloseTo(documentInformation.getModificationDate().getTime(), 1000);
    }

    @Test
    void createPdfFromByteArray_works() throws Exception {

        /// arrange
        byte[] image1 = readTestFile(TEST_FILE_JPEG_01);
        byte[] image2 = readTestFile(TEST_FILE_JPEG_02);
        byte[] image3 = readTestFile(TEST_FILE_PNG_01);
        byte[] image4 = readTestFile(TEST_FILE_PNG_02);
        byte[][] imageFiles = new byte[][]{image1, image2, image3, image4};
        File pdfFile = File.createTempFile("pdf-from-bytes", ".pdf");
        pdfFile.deleteOnExit();
        PdfDocumentInformation documentInformation = new PdfDocumentInformation();
        documentInformation.setTitle("title");

        /// act
        try (FileOutputStream outputStream = new FileOutputStream(pdfFile)) {
            providerUnderTest.createPdfFromImages(imageFiles, documentInformation, 1440, 1440, outputStream);
        }

        /// assert
        assertThat(pdfFile.length()).isGreaterThan(1000);
        assertThat(providerUnderTest.countPages(pdfFile)).isEqualTo(imageFiles.length);
        assertThat(providerUnderTest.getDocumentInformation(pdfFile).getTitle()).isEqualTo(documentInformation.getTitle());
    }

    // -----------------------------------------------------------------------

    private void createThumbnailUsingOutputStream(int thumbPixelMaxSize, File file) throws Exception {

        /// arrange
        File outFile = File.createTempFile("provider-thumb-", ".jpg");
        if (CLEAR_OUTPUT_FILES) {
            outFile.deleteOnExit();
        }

        /// act
        providerUnderTest.createThumbnail(file, outFile,
            MIME_TYPE_JPEG, thumbPixelMaxSize, thumbPixelMaxSize,
            ConversionCommand.CompressionQuality.LOSSY_MEDIUM);

        /// assert
        assertThat(outFile.exists()).isTrue();
        FileInfo fileInfo = imagingProvider.fetchFileInfo(outFile);
        assertThat(fileInfo.getMimeType()).isEqualTo(MIME_TYPE_JPEG);
        assertThat(fileInfo.getBitsPerPixel()).isEqualTo(24);
        assertThat(fileInfo.getWidth()).isLessThanOrEqualTo(thumbPixelMaxSize);
        assertThat(fileInfo.getHeight()).isLessThanOrEqualTo(thumbPixelMaxSize);
    }
}