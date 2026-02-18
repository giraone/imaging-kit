package com.giraone.imaging;

import com.giraone.imaging.pdf.PdfProvider;
import com.giraone.imaging.text.MarkdownProvider;
import com.giraone.imaging.video.VideoProvider;

import java.io.File;
import java.nio.file.Path;

import static com.giraone.imaging.MimeTypes.*;

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
     * Create multiple thumbnail images (e.g. different sizes) for a given file.
     * The default implementation just iterates and re-reads the input file multiple times.
     * Implementing classes should optimize this a read the input only once, then create multiple output thumbnails.
     * @param inputFile Input file.
     * @param conversionCommands Array of commands. Each with the definitions of the output (path, format, width, height and quality).
     * @throws Exception on any error opening the file, converting the file or writing to the output.
     */
    default void createThumbnails(File inputFile, ConversionCommand[] conversionCommands) throws Exception {
        for (ConversionCommand conversionCommand: conversionCommands) {
            createThumbnail(inputFile, conversionCommand);
        }
    }

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

    /**
     * Return default ThumbnailProvider for a given MIME type.
     * @param mimeType MIME to get the provider for
     * @return a ThumbnailProvider (singleton) - the default is to return the default {@link ImagingProvider} singleton.
     */
    static ThumbnailProvider getThumbnailProvider(String mimeType) {
        if (mimeType.startsWith(PREFIX_VIDEO)) {
            return VideoProvider.getInstance();
        } else if (mimeType.equals(APPLICATION_PDF)) {
            return PdfProvider.getInstance();
        } else if (mimeType.startsWith(PREFIX_TEXT)) {
            return MarkdownProvider.getInstance();
        } else {
            return ImagingProvider.getInstance();
        }
    }
}