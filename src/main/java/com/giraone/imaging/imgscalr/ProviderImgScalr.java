package com.giraone.imaging.imgscalr;

import com.giraone.imaging.ConversionCommand;
import com.giraone.imaging.FileInfo;
import com.giraone.imaging.FormatNotSupportedException;
import com.giraone.imaging.ImagingProvider;
import com.giraone.imaging.java2.ImageOpener;
import com.giraone.imaging.java2.ImagePlusInfo;
import com.giraone.imaging.java2.ImageToFileWriter;
import com.giraone.imaging.java2.ProviderJava2D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Imaging provider based on IMGSCALR implementation (https://github.com/rkalla/imgscalr).
 */
public class ProviderImgScalr implements ImagingProvider {
    
    private static final Logger LOGGER = LogManager.getLogger(ProviderImgScalr.class);

    // TODO: This is the Java2 solution!
    public FileInfo fetchFileInfo(File inputFile) throws IOException, FormatNotSupportedException {
        return new ProviderJava2D().fetchFileInfo(inputFile);
    }

    public void convertImage(File inputFile, OutputStream outputStream, ConversionCommand command) throws Exception {
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
            LOGGER.debug("Provider.openImage: Time = {} ms", end - start);
            start = end;
        }

        BufferedImage bufferedImage = imagePlusInfo.getImage();
        if (LOGGER.isDebugEnabled()) {
            final long end = System.currentTimeMillis();
            LOGGER.debug("Provider.bufferedImage: Time = {} ms, pixels = {}x{}",
                end - start, bufferedImage.getWidth(), bufferedImage.getHeight());
            start = end;
        }

        final Dimension dimension = command.getDimensionFromLimits(bufferedImage.getWidth(), bufferedImage.getHeight());

        final BufferedImage sourceImage;
        try (InputStream sourceStream = new FileInputStream(inputFile)) {
            sourceImage = ImageIO.read(sourceStream);
        }
        final BufferedImage targetImage = Scalr.resize(sourceImage, this.getInternalSpeedHint(command.getSpeedHint()),
                dimension.width, dimension.height);

        targetImage.flush();
        sourceImage.flush();

        int normedQuality = command.getQuality();
        float internalQuality = this.getInternalQuality(normedQuality);
        ImageToFileWriter.saveJpeg(targetImage, outputStream, internalQuality);
        if (LOGGER.isDebugEnabled()) {
            final long end = System.currentTimeMillis();
            LOGGER.debug("Provider.save_jpeg: Time = {} ms", end - start);
        }
    }

    public void createThumbNail(File inputFile, OutputStream outputStream, String format, int width, int height,
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
        this.convertImage(inputFile, outputStream, command);
    }

    //---------------------------------------------------------------------------------

    /**
     * Return internal quality value, when normed value is given
     * <ul>
     * <li><tt>0</tt>: Lossless compression (NOT SUPPORTED for JPEG)
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

    private Scalr.Method getInternalSpeedHint(ConversionCommand.SpeedHint speedHint) {
        switch (speedHint) {
            case ULTRA_QUALITY:
                return Scalr.Method.ULTRA_QUALITY;
            case QUALITY:
                return Scalr.Method.QUALITY;
            case BALANCED:
                return Scalr.Method.BALANCED;
            default:
                return Scalr.Method.SPEED;
        }
    }
}
