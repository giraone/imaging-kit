package com.giraone.imaging;

import com.giraone.imaging.imgscalr.ProviderImgScalr;
import com.giraone.imaging.java2.ProviderJava2D;
import com.giraone.imaging.pdf.PdfProviderFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Map;

/**
 * A performance test to test different imaging providers.
 */
public class JpegScalePerformanceTest {

    private static final String TEST_FILE_JPEG_01 = "image-01.jpg";
    private static final String TEST_FILE_JPEG_02 = "image-02.jpg";
    private static final String TEST_FILE_JPEG_EXIF_01 = "image-exif-01.jpg";
    private static final String TEST_FILE_JPEG_EXIF_02 = "image-exif-02.jpg";
    private static final String TEST_FILE_JPEG_EXIF_03 = "image-exif-03.jpg";
    private static final String TEST_FILE_PNG_01 = "image-01.png";
    private static final String TEST_FILE_PNG_02 = "image-02.png";

    private static final String[] ALL_TEST_FILES = {
            TEST_FILE_JPEG_01, TEST_FILE_JPEG_02,
            TEST_FILE_JPEG_EXIF_01, TEST_FILE_JPEG_EXIF_02, TEST_FILE_JPEG_EXIF_03,
            TEST_FILE_PNG_01, TEST_FILE_PNG_02
    };

    private static Map<String, File> testFiles;

    // -----------------------------------------------------------------------

    @BeforeAll
    static void initializeTestFiles() {

        testFiles = TestFileHelper.cloneTestFiles(Arrays.stream(ALL_TEST_FILES));
    }

    @Test
    void testAlFiles() throws Exception {

        run(testFiles.values());
    }

    private static void run(Iterable<File> files) throws Exception {
        int thumbWidthAndHeight = 180;
        int scaledWidthAndHeight = 900;
        {
            ImagingProvider provider = new ProviderJava2D();
            String name = "java2d";

            long totalStart = System.currentTimeMillis();
            for (File inFile : files) {
                scale(provider, name, inFile, thumbWidthAndHeight, scaledWidthAndHeight);
            }
            long totalEnd = System.currentTimeMillis();
            System.err.println(name + " ==> " + (totalEnd - totalStart) + " msecs");
        }

        for (ConversionCommand.SpeedHint speedHint : new ConversionCommand.SpeedHint[]
                {ConversionCommand.SpeedHint.SPEED, ConversionCommand.SpeedHint.BALANCED, ConversionCommand.SpeedHint.QUALITY, ConversionCommand.SpeedHint.ULTRA_QUALITY}) {
            ImagingProvider provider = new ProviderImgScalr();
            String name = "imgscalr-" + speedHint;

            long totalStart = System.currentTimeMillis();
            for (File inFile : files) {
                scale(provider, name, inFile, thumbWidthAndHeight, scaledWidthAndHeight);
            }
            long totalEnd = System.currentTimeMillis();
            System.err.println(name + " ==> " + (totalEnd - totalStart) + " msecs");
        }
    }

    private static void scale(ImagingProvider provider, String dirName, File inFile, int thumbWidthAndHeight, int scaledWidthAndHeight) throws Exception {

        File outDir = new File(inFile.getParentFile(), dirName);
        outDir.mkdirs();

        String newName1 = inFile.getName().substring(0, inFile.getName().indexOf('.') - 1) + "-thumb.jpg";
        String newName2 = inFile.getName().substring(0, inFile.getName().indexOf('.') - 1) + "-small.jpg";
        String outFile1 = outDir + "/" + newName1;
        String outFile2 = outDir + "/" + newName2;

        //long start1 = System.currentTimeMillis();
        try (FileOutputStream outputStream = new FileOutputStream(outFile1)) {
            provider.createThumbNail(inFile, outputStream, "image/jpeg", thumbWidthAndHeight, thumbWidthAndHeight,
                    ConversionCommand.CompressionQuality.LOSSY_MEDIUM, ConversionCommand.SpeedHint.SPEED);
        }
        //long end1 = System.currentTimeMillis();
        //System.err.println(inFile + " ==> " + outfile1 + " : " + (end1-start1) + " msecs");

        //long start2 = System.currentTimeMillis();
        try (FileOutputStream outputStream = new FileOutputStream(outFile2)) {
            provider.createThumbNail(inFile, outputStream, "image/jpeg", scaledWidthAndHeight, scaledWidthAndHeight,
                    ConversionCommand.CompressionQuality.LOSSY_BEST, ConversionCommand.SpeedHint.ULTRA_QUALITY);
        }
        //long end2 = System.currentTimeMillis();
        //System.err.println(inFile + " ==> " + outfile2 + " : " + (end2-start2) + " msecs");
    }

    public static BufferedImage loadImageUsingToolkit(String imagePath, ImageObserver imageObserver) {
        Image image = getImageUsingToolkit(imagePath);
        MediaTracker mediaTracker = new MediaTracker(new Container());
        mediaTracker.addImage(image, 0);
        try {
            mediaTracker.waitForID(0);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Loading " + imagePath + " interrupted: " + e.getMessage());
        }

        int width = image.getWidth(imageObserver);
        int height = image.getHeight(imageObserver);

        // draw image to GUI object
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, width, height, imageObserver);

        return bufferedImage;
    }

    static Image getImageUsingToolkit(String imagePath) {
        return Toolkit.getDefaultToolkit().getImage(imagePath);
    }
}

/*
java2d ==> 1720 msecs
imgscalr-SPEED ==> 2773 msecs
imgscalr-BALANCED ==> 2600 msecs
imgscalr-QUALITY ==> 2563 msecs
imgscalr-ULTRA_QUALITY ==> 2540 msecs

// Mit ThreadLocal
java2d ==> 1723 msecs
imgscalr-SPEED ==> 2797 msecs
imgscalr-BALANCED ==> 2634 msecs
imgscalr-QUALITY ==> 2590 msecs
*/