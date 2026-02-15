package com.giraone.imaging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * Interface for imaging operation to be implemented by different imaging providers.
 */
public interface ThumbnailProvider {

    /**
     * Create a thumbnail image for a given file.
     * @param inputFile Input file.
     * @param outputStream OutputStream, to which the thumbnail is written. Important: Stream is not closed!
     * @param format Output file format given as a MIME type.
     * @param width Width in pixel.
     * @param height Height in pixel.
     * @param quality Quality factor for output compression.
     * @throws Exception on any error opening the file, converting the file or writing to the output.
     */
    void createThumbnail(File inputFile, OutputStream outputStream, String format, int width, int height,
                         ConversionCommand.CompressionQuality quality) throws Exception;

    /**
     * Create a thumbnail image for a given file.
     * @param inputPath Input file path.
     * @param outputStream OutputStream, to which the thumbnail is written. Important: Stream is not closed!
     * @param format Output file format given as a MIME type.
     * @param width Width in pixel.
     * @param height Height in pixel.
     * @param quality Quality factor for output compression.
     * @throws Exception on any error opening the file, converting the file or writing to the output.
     */
    default void createThumbnail(Path inputPath, OutputStream outputStream, String format, int width, int height,
                                 ConversionCommand.CompressionQuality quality) throws Exception {
        createThumbnail(inputPath.toFile(), outputStream, format, width, height, quality);
    }

    /**
     * Create a thumbnail image for a given file.
     * @param inputFile Input file.
     * @param outputFile Output file, to which the thumbnail is written.
     * @param format Output file format given as a MIME type.
     * @param width Width in pixel.
     * @param height Height in pixel.
     * @param quality Quality factor for output compression.
     * @throws Exception on any error opening the file, converting the file or writing to the output.
     */
    default void createThumbnail(File inputFile, File outputFile, String format, int width, int height,
                                 ConversionCommand.CompressionQuality quality) throws Exception {

        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            createThumbnail(inputFile, outputStream, format, width, height, quality);
        }
    }

    /**
     * Create a thumbnail image for a given file.
     * @param inputPath Input file path.
     * @param outputPath Output file path, to which the thumbnail is written.
     * @param format Output file format given as a MIME type.
     * @param width Width in pixel.
     * @param height Height in pixel.
     * @param quality Quality factor for output compression.
     * @throws Exception on any error opening the file, converting the file or writing to the output.
     */
    default void createThumbnail(Path inputPath, Path outputPath, String format, int width, int height,
                                 ConversionCommand.CompressionQuality quality) throws Exception {

        try (FileOutputStream outputStream = new FileOutputStream(outputPath.toFile())) {
            createThumbnail(inputPath, outputStream, format, width, height, quality);
        }
    }
}