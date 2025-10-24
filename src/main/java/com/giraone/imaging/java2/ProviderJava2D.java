package com.giraone.imaging.java2;

import com.giraone.imaging.ConversionCommand;
import com.giraone.imaging.FileInfo;
import com.giraone.imaging.FileTypeDetector;
import com.giraone.imaging.FormatNotSupportedException;
import com.giraone.imaging.ImageConversionException;
import com.giraone.imaging.ImagingProvider;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * Imaging provider based on Java 2 classes
 */
public class ProviderJava2D implements ImagingProvider {

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

    @Override
    public FileInfo fetchFileInfo(Path path) throws IOException, FormatNotSupportedException {
        final FileTypeDetector.FileType fileType = FileTypeDetector.getInstance().getFileType(path);
        if (FileTypeDetector.getInstance().isSupportedImage(fileType)) {
            final ImagePlusInfo imagePlusInfo = ImageOpener.openImage(path, fileType);
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
                                ConversionCommand.CompressionQuality quality) throws IOException, FormatNotSupportedException {

        final ConversionCommand command = ConversionCommand.buildConversionCommand(format, width, height, quality);
        this.convertImage(inputFile, out, command);
    }

    @Override
    public void createThumbNail(Path inputPath, OutputStream out, String format, int width, int height,
                                ConversionCommand.CompressionQuality quality) throws IOException, FormatNotSupportedException {
        final ConversionCommand command = ConversionCommand.buildConversionCommand(format, width, height, quality);
        this.convertImage(inputPath, out, command);
    }

    public void convertImage(File inputFile, OutputStream out, ConversionCommand command) throws IOException, FormatNotSupportedException {

        final ImagePlusInfo imagePlusInfo = ImageOpener.openImage(inputFile);
        if (imagePlusInfo == null) {
            throw new FormatNotSupportedException("Unsupported input file type for file " + inputFile);
        }
        final BufferedImage bufferedImage = imagePlusInfo.getImage();
        convertAndWriteImage(bufferedImage, out, command);
    }

    @Override
    public void convertImage(Path inputPath, OutputStream out, ConversionCommand command) throws IOException, FormatNotSupportedException {
        final ImagePlusInfo imagePlusInfo = ImageOpener.openImage(inputPath);
        if (imagePlusInfo == null) {
            throw new FormatNotSupportedException("Unsupported input file type for file " + inputPath);
        }
        final BufferedImage bufferedImage = imagePlusInfo.getImage();
        convertAndWriteImage(bufferedImage, out, command);
    }

    /**
     * Convert a buffered image to the specified output format.
     * Supports JPEG, PNG, and GIF output formats.
     *
     * @param bufferedImage the image to convert and save
     * @param out OutputStream, to which the new image is written. Important: Stream is not closed!
     * @param command the image conversion command
     * @throws IOException on any error opening the file, converting the file or writing to the output
     * @throws FormatNotSupportedException if the output format is not supported
     * @throws ImageConversionException if an error occurs during image conversion or scaling
     */
    public void convertAndWriteImage(BufferedImage bufferedImage, OutputStream out, ConversionCommand command)
        throws IOException, FormatNotSupportedException {

        final Dimension dimension = command.getDimensionFromLimits(bufferedImage.getWidth(), bufferedImage.getHeight());
        Image targetImage = bufferedImage;
        if (dimension != null) {
            targetImage = targetImage.getScaledInstance(dimension.width, dimension.height, Image.SCALE_DEFAULT);
            bufferedImage = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_RGB);
            Graphics g = bufferedImage.getGraphics();
            g.drawImage(targetImage, 0, 0, LoggerImageObserver.getInstance());
        }

        final String outputFormat = command.getOutputFormat();
        switch (outputFormat) {
            case "image/jpeg":
                final int normedQuality = command.getQuality();
                final float internalQuality = this.getInternalQuality(normedQuality);
                ImageToFileWriter.saveJpeg(bufferedImage, out, internalQuality);
                break;
            case "image/png":
                ImageToFileWriter.savePng(bufferedImage, out);
                break;
            case "image/gif":
                ImageToFileWriter.saveGif(bufferedImage, out);
                break;
            default:
                throw new FormatNotSupportedException("Unsupported output format: " + outputFormat +
                    ". Supported formats are: image/jpeg, image/png, image/gif");
        }
    }

    /**
     * Convert a buffered image to a JPEG output stream.
     * @param bufferedImage the image to convert and save
     * @param out OutputStream, to which the new image is written. Important: Stream is not closed!
     * @param command the image conversion command
     * @throws IOException on any error opening the file, converting the file or writing to the output
     * @throws ImageConversionException if an error occurs during image conversion or scaling
     * @deprecated Use {@link #convertAndWriteImage(BufferedImage, OutputStream, ConversionCommand)} instead.
     *             This method is kept for backward compatibility.
     */
    @Deprecated
    public void convertAndWriteImageAsJpeg(BufferedImage bufferedImage, OutputStream out, ConversionCommand command)
        throws IOException, FormatNotSupportedException {

       convertAndWriteImage(bufferedImage, out, command);
    }

    //---------------------------------------------------------------------------------

    /**
     * Return internal quality value, when normed value is given
     * <ul>
     * <li><code>0</code>: Lossless compression (NOT SUPPORTED)
     * <li><code>1</code>: Lossy compression with the best quality.
     * <li><code>100</code>: Lossy compression with the worst quality.
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