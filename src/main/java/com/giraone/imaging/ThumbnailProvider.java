package com.giraone.imaging;

import java.io.File;
import java.nio.file.Path;

/**
 * Interface for thumbnail generation operations to be implemented by different imaging providers.
 */
public interface ThumbnailProvider {

    /**
     * Create a thumbnail image for a given file.
     * @param inputFile Input file.
     * @param conversionCommand The command with the definitions of the output (path, format, width, height and quality).
     * @throws Exception on any error opening the file, converting the file or writing to the output.
     */
    void createThumbnail(File inputFile, ConversionCommand conversionCommand) throws Exception;

    /**
     * Create a thumbnail image for a given file.
     * @param inputPath Input file path.
     * @param conversionCommand The command with the definitions of the output (path, format, width, height and quality).
     * @throws Exception on any error opening the file, converting the file or writing to the output.
     */
    default void createThumbnail(Path inputPath, ConversionCommand conversionCommand) throws Exception {
        createThumbnail(inputPath.toFile(), conversionCommand);
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

        createThumbnail(inputFile, ConversionCommand.buildConversionCommand(outputFile, format, width, height, quality));
    }

    /**
     * Create a thumbnail image for a given file.
     * @param inputPath Input file path.
     * @param outputPath Output file, to which the thumbnail is written.
     * @param format Output file format given as a MIME type.
     * @param width Width in pixel.
     * @param height Height in pixel.
     * @param quality Quality factor for output compression.
     * @throws Exception on any error opening the file, converting the file or writing to the output.
     */
    default void createThumbnail(Path inputPath, Path outputPath, String format, int width, int height,
                                 ConversionCommand.CompressionQuality quality) throws Exception {

        createThumbnail(inputPath.toFile(), ConversionCommand.buildConversionCommand(outputPath.toFile(), format, width, height, quality));
    }
}