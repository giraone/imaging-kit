package com.giraone.imaging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface for imaging operation to be implemented by different imaging providers.
 */
public interface ImagingProvider {
    /**
     * Extract information about a given file including image information.
     *
     * @param inputFile Input file.
     * @return The file information value object.
     * @throws IOException on any error opening the file
     * @throws FormatNotSupportedException if the file is opened, but the file type is not supported
     */
    FileInfo fetchFileInfo(File inputFile) throws IOException, FormatNotSupportedException;

    /**
     * Convert an image to another image using image conversion functions.
     *
     * @param inputFile    Input file.
     * @param outputStream OutputStream, to which the new image is written. Important: Stream is not closed!
     * @param command      An image conversion command.
     * @throws Exception on any error opening the file, converting the file or writing to the output.
     */
    void convertImage(File inputFile, OutputStream outputStream, ConversionCommand command)
            throws Exception;

    /**
     * Create a thumbnail image for a given file.
     *
     * @param inputFile    Input file.
     * @param outputStream OutputStream, to which the thumbnail is written. Important: Stream is not closed!
     * @param format       Output file format given as a MIME type.
     * @param width        Width in pixel.
     * @param height       Height in pixel.
     * @param quality      Quality factor for output compression.
     * @throws Exception on any error opening the file, converting the file or writing to the output.
     */
    void createThumbNail(File inputFile, OutputStream outputStream, String format, int width, int height,
                         ConversionCommand.CompressionQuality quality) throws Exception;

    /**
     * Convert an image to another image using image conversion functions.
     *
     * @param inputFile  Input file.
     * @param outputFile Output file, to which the new image is written.
     * @param command    An image conversion command.
     * @throws Exception on any error opening the file, converting the file or writing to the output.
     */
    @SuppressWarnings("unused")
    default void convertImage(File inputFile, File outputFile, ConversionCommand command)
            throws Exception {

        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            this.convertImage(inputFile, outputStream, command);
        }
    }

    /**
     * Create a thumbnail image for a given file.
     *
     * @param inputFile  Input file.
     * @param outputFile Output file, to which the thumbnail is written.
     * @param format     Output file format given as a MIME type.
     * @param width      Width in pixel.
     * @param height     Height in pixel.
     * @param quality    Quality factor for output compression.
     * @throws Exception on any error opening the file, converting the file or writing to the output.
     */
    default void createThumbNail(File inputFile, File outputFile,
                                 String format, int width, int height,
                                 ConversionCommand.CompressionQuality quality) throws Exception {

        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            this.createThumbNail(inputFile, outputStream, format, width, height, quality);
        }
    }
}