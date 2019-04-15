package com.giraone.imaging.java2;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * Input/Output routines for image load and store.
 */
public class ImageToFileWriter {

    protected static ImageObserver imageObserver = LoggerImageObserver.getInstance();

    final static ThreadLocal<ImageWriter> ImageWriterThreadLocal = new ThreadLocal<ImageWriter>() {
        @Override
        protected ImageWriter initialValue() {
            final Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
            if (!writers.hasNext())
                throw new IllegalStateException("No ImageIO writers found");
            return writers.next();
        }
    };

    /**
     * <pre>
     * Some guidelines:
     * 0.75 high quality
     * 0.5  medium quality
     * 0.25 low quality
     * </pre>
     *
     * @param bufferedImage the image bitmap to be saved
     * @param outputStream  Stream to write the image to. Stream is flushed, but not closed.
     * @param quality       0.0-1.0 setting of desired quality level.
     * @throws IOException
     */
    public static void saveJpeg(BufferedImage bufferedImage, OutputStream outputStream, float quality)
            throws IOException {

        // See also: http://www.java2s.com/Code/Java/2D-Graphics-GUI/WritesanimagetoanoutputstreamasaJPEGfileTheJPEGqualitycanbespecifiedinpercent.htm
        ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream);

        final JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
        jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpegParams.setCompressionQuality(quality);
		
		/*
		/ Alternative!
		ImageWriter writer = null;
    	Iterator iter = ImageIO.getImageWritersByFormatName("jpg");
    	if (iter.hasNext()) {
      	writer = (ImageWriter) iter.next();
    	}
		*/

        // Performance: we may re-use our ImageWriters
        final ImageWriter writer = ImageWriterThreadLocal.get();
        writer.setOutput(ios);
        writer.write(/* IIOMetadata */ null,
                new IIOImage(bufferedImage, /* thumbnails */ null, /* IIOMetadata */ null), jpegParams);
        //writer.dispose(); // We do not dispose, because of the re-use!!!
        outputStream.flush();
    }

}