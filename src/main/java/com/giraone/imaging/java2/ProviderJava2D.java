package com.giraone.imaging.java2;

import com.giraone.imaging.ConversionCommand;
import com.giraone.imaging.FileInfo;
import com.giraone.imaging.FileTypeDetector;
import com.giraone.imaging.FormatNotSupportedException;
import com.giraone.imaging.ImagingProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Imaging provider based on Java 2 classes
 */
public class ProviderJava2D implements ImagingProvider {

    private static final Logger LOGGER = LogManager.getLogger(ProviderJava2D.class);

    public FileInfo fetchFileInfo(File file) throws IOException, FormatNotSupportedException {

        final FileTypeDetector.FileType fileType = FileTypeDetector.getInstance().getFileType(file);
        if (FileTypeDetector.getInstance().isSupportedImage(fileType)) {
            final ImagePlusInfo imagePlusInfo = ImageOpener.openImage(file, fileType);
            if (imagePlusInfo != null)
                return imagePlusInfo.getFileInfo();
            else
                throw new FormatNotSupportedException("Unknown image format: " + fileType + "!");
        } else if (FileTypeDetector.FileType.PDF == fileType) {
            final FileInfo fileInfo = new FileInfo();
            fileInfo.setMimeType("application/pdf");
            return fileInfo;
        } else {
            throw new FormatNotSupportedException("Unknown file format: " + fileType + "!");
        }
    }

    public void createThumbNail(File inputFile, OutputStream out, String format, int width, int height,
                                ConversionCommand.CompressionQuality quality, ConversionCommand.SpeedHint speedHint) throws Exception {

        final ConversionCommand command = new ConversionCommand();
        command.setOutputFormat(format);
        command.setDimension(new Dimension(width, height));
        int iQuality;
        switch (quality) {
            case LOSSLESS:
                iQuality = 0;
                break;
            case LOSSY_BEST:
                iQuality = 1;
                break;
            case LOSSY_MEDIUM:
                iQuality = 50;
                break;
            case LOSSY_SPEED:
                iQuality = 100;
                break;
            default:
                iQuality = 50;
        }
        command.setQuality(iQuality);
        this.convertImage(inputFile, out, command);
    }

    public void convertImage(File inputFile, OutputStream out, ConversionCommand command)
            throws Exception {

        // We support only JPEG as the target format
        if (!command.getOutputFormat().equals("image/jpeg"))
            throw new FormatNotSupportedException("Unsupported target format " + command.getOutputFormat());

        long start = System.currentTimeMillis();

        final ImagePlusInfo imagePlusInfo = ImageOpener.openImage(inputFile);
        if (imagePlusInfo == null) {
            throw new FormatNotSupportedException("Unsupported input file type for file " + inputFile);
        }

        if (LOGGER.isDebugEnabled()) {
            final long end = System.currentTimeMillis();
            LOGGER.debug("Provider.openImage: Time = {} ms",end - start);
        }

        start = System.currentTimeMillis();

        final BufferedImage bufferedImage = imagePlusInfo.getImage();

        if (LOGGER.isDebugEnabled()) {
            final long end = System.currentTimeMillis();
            LOGGER.debug("Provider.bufferedImage: Time = {} ms, pixels = {}x{}",
                end - start, bufferedImage.getWidth(), bufferedImage.getHeight());
        }

        convertAndWriteImageAsJpeg(bufferedImage, out, command);
    }

    /**
     * Convert a buffered image to a JPEG output stream.
     * @param bufferedImage the image to convert and save
     * @param out OutputStream, to which the new image is written. Important: Stream is not closed!
     * @param command the image conversion command
     * @throws IOException on any error opening the file, converting the file or writing to the output.
     */
    public void convertAndWriteImageAsJpeg(BufferedImage bufferedImage, OutputStream out, ConversionCommand command)
            throws IOException {

        long start = System.currentTimeMillis();

        final Dimension dimension = command.getDimensionFromLimits(bufferedImage.getWidth(), bufferedImage.getHeight());

        Image targetImage = bufferedImage;
        if (dimension != null) {
            targetImage = targetImage.getScaledInstance(dimension.width, dimension.height, Image.SCALE_DEFAULT);
            bufferedImage = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_RGB);
            Graphics g = bufferedImage.getGraphics();
            g.drawImage(targetImage, 0, 0, LoggerImageObserver.getInstance());
        }

        final int normedQuality = command.getQuality();
        final float internalQuality = this.getInternalQuality(normedQuality);
        ImageToFileWriter.saveJpeg(bufferedImage, out, internalQuality);
        if (LOGGER.isDebugEnabled()) {
            final long end = System.currentTimeMillis();
            LOGGER.debug("Provider.save_jpeg: Time = {}",end - start);
        }
    }

    //---------------------------------------------------------------------------------

    /**
     * Return internal quality value, when normed value is given
     * <ul>
     * <li><code>0</code>: Lossless compression (NOT SUPPORTED)
     * <li><code>1</code>: Lossy compression with best quality.
     * <li><code>100</code>: Lossy compression with worst quality.
     * <li><code>2-99</code>: Other lossy compression values (50 == medium)
     * </ul>
     */
    private float getInternalQuality(int normedQuality) {
        if (normedQuality == 0) // lossless (NOT SUPPORTED)
            return 1.0f;
        else if (normedQuality == 1) // best lossy
            return 1.0f;
        else if (normedQuality == 100) // worst lossy
            return 0.2f;
        else
            return 1.0f - (normedQuality * 0.8f / 100.0f);
    }
}