package com.giraone.imaging.java2;

import com.giraone.imaging.*;
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

    private final static Logger LOGGER = LogManager.getLogger(ProviderJava2D.class);

    public ProviderJava2D() {
    }

    public FileInfo fetchFileInfo(File file) throws IOException, FormatNotSupportedException {
        FileTypeDetector.FileType fileType = FileTypeDetector.getInstance().getFileType(file);
        if (FileTypeDetector.getInstance().isSupportedImage(fileType)) {
            ImagePlusInfo imagePlusInfo = ImageOpener.openImage(file, fileType);
            if (imagePlusInfo != null)
                return imagePlusInfo.getFileInfo();
            else
                throw new FormatNotSupportedException("Unknown image format: " + fileType + "!");
        } else if (FileTypeDetector.FileType.PDF == fileType) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setMimeType("application/pdf");
            return fileInfo;
        } else {
            throw new FormatNotSupportedException("Unknown file format: " + fileType + "!");
        }
    }

    public void createThumbNail(File inputFile, OutputStream out, String format, int width, int height,
                                ConversionCommand.CompressionQuality quality, ConversionCommand.SpeedHint speedHint) throws Exception {
        ConversionCommand command = new ConversionCommand();
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

        ImagePlusInfo imagePlusInfo = ImageOpener.openImage(inputFile);
        if (imagePlusInfo == null) {
            throw new FormatNotSupportedException("Unsupported input file type for file " + inputFile);
        }

        if (LOGGER.isDebugEnabled()) {
            final long end = System.currentTimeMillis();
            LOGGER.debug("Provider.openImage: Time = " + (end - start) + " ms");
        }

        start = System.currentTimeMillis();

        BufferedImage bufferedImage = imagePlusInfo.getImage();

        if (LOGGER.isDebugEnabled()) {
            final long end = System.currentTimeMillis();
            LOGGER.debug("Provider.bufferedImage: Time = " + (end - start) + " ms, pixels = " + bufferedImage.getWidth()
                    + "x" + bufferedImage.getHeight());
        }

        convertAndWriteImageAsJpeg(bufferedImage, out, command);
    }

    public void convertAndWriteImageAsJpeg(BufferedImage bufferedImage, OutputStream out, ConversionCommand command)
            throws IOException {

        long start = System.currentTimeMillis();

        Dimension dimension = command.getDimensionFromLimits(bufferedImage.getWidth(), bufferedImage.getHeight());

        Image targetImage = bufferedImage;
        if (dimension != null) {
            targetImage = targetImage.getScaledInstance(dimension.width, dimension.height, Image.SCALE_DEFAULT);
            bufferedImage = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_RGB);
            Graphics g = bufferedImage.getGraphics();
            g.drawImage(targetImage, 0, 0, LoggerImageObserver.getInstance());
        }

        int normedQuality = command.getQuality();
        float internalQuality = this.getInternalQuality(normedQuality);
        ImageToFileWriter.saveJpeg(bufferedImage, out, internalQuality);
        if (LOGGER.isDebugEnabled()) {
            final long end = System.currentTimeMillis();
            LOGGER.debug("Provider.save_jpeg: Time = " + (end - start) + " ms");
        }
    }

    //---------------------------------------------------------------------------------

    /**
     * Return internal quality value, when normed value is given
     * <ul>
     * <li><tt>0</tt>: Lossless compression (NOT SUPPORTED)
     * <li><tt>1</tt>: Lossy compression with best quality.
     * <li><tt>100</tt>: Lossy compression with worst quality.
     * <li><tt>2-99</tt>: Other lossy compression values (50 == medium)
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