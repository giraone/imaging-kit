package com.giraone.imaging.java2;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import static com.giraone.imaging.MimeTypes.IMAGE_GIF;
import static com.giraone.imaging.MimeTypes.IMAGE_PNG;

/**
 * Input/Output routines for image load and store.
 */
public class ImageToFileWriter {

    private static final String OUTPUT_FORMAT_JPEG = "jpeg";
    private static final String OUTPUT_FORMAT_PNG = "png";
    private static final String OUTPUT_FORMAT_GIF = "gif";

    private static final ThreadLocal<ImageWriter> ImageWriterThreadLocal = ThreadLocal.withInitial(() -> {
        final Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(OUTPUT_FORMAT_JPEG);
        if (!writers.hasNext())
            throw new IllegalStateException("No ImageIO writers found");
        return writers.next();
    });

    // Hide constructor
    private ImageToFileWriter() {
    }

    /**
     * Save a bitmap as an JPEG image
     * <pre>
     * Some guidelines:
     * 0.75 high quality
     * 0.5  medium quality
     * 0.25 low quality
     * </pre>
     * @param bufferedImage the image bitmap to be saved
     * @param outputStream Stream to write the image to. Stream is flushed, but not closed.
     * @param quality 0.0-1.0 setting of desired quality level.
     * @throws IOException On any IO exception
     */
    public static void saveJpeg(BufferedImage bufferedImage, OutputStream outputStream, float quality) throws IOException {

        // See also: http://www.java2s.com/Code/Java/2D-Graphics-GUI/WritesanimagetoanoutputstreamasaJPEGfileTheJPEGqualitycanbespecifiedinpercent.htm
        final ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream);

        final JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
        jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpegParams.setCompressionQuality(quality);

        // Performance: we may re-use our ImageWriters
        final ImageWriter writer = ImageWriterThreadLocal.get();
        writer.setOutput(ios);
        writer.write(/* IIOMetadata */ null, new IIOImage(bufferedImage, /* thumbnails */ null, /* IIOMetadata */ null), jpegParams);
        //writer.dispose(); // We do not dispose, because of the re-use!!!
        outputStream.flush();
    }

    /**
     * Save a bitmap as a PNG image (lossless compression).
     * PNG format supports transparency and provides lossless compression.
     * @param bufferedImage the image bitmap to be saved
     * @param outputStream Stream to write the image to. Stream is flushed, but not closed.
     * @throws IOException On any IO exception
     */
    public static void savePng(BufferedImage bufferedImage, OutputStream outputStream) throws IOException {
        ImageIO.write(bufferedImage, OUTPUT_FORMAT_PNG, outputStream);
        outputStream.flush();
    }

    /**
     * Save a bitmap as a GIF image.
     * Note: GIF format is limited to 256 colors, so images may be color-reduced.
     * @param bufferedImage the image bitmap to be saved
     * @param outputStream Stream to write the image to. Stream is flushed, but not closed.
     * @throws IOException On any IO exception
     */
    public static void saveGif(BufferedImage bufferedImage, OutputStream outputStream) throws IOException {
        ImageIO.write(bufferedImage, OUTPUT_FORMAT_GIF, outputStream);
        outputStream.flush();
    }

    public static String mimeTypeToIoWriteFormat(String imageMimeType) {
        return switch (imageMimeType) {
            case IMAGE_PNG -> OUTPUT_FORMAT_PNG;
            case IMAGE_GIF -> OUTPUT_FORMAT_GIF;
            default -> OUTPUT_FORMAT_JPEG;
        };
    }
}