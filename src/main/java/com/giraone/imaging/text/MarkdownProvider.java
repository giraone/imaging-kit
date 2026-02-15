package com.giraone.imaging.text;

import com.giraone.imaging.ConversionCommand;

import java.io.File;
import java.io.OutputStream;

/**
 * Interface for imaging operation on Markdown documents.
 */
public interface MarkdownProvider {

/**
 * Create a thumbnail image for a given Markdown file.
 * @param inputFile Input file.
 * @param outputStream OutputStream, to which the thumbnail is written. Important: Stream is not closed!
 * @param format Output file format given as a MIME type.
 * @param width Width in pixel.
 * @param height Height in pixel.
 * @param quality Quality factor for output compression.
 * @throws Exception on any error opening the file, converting the file or writing to the output.
 */
void createThumbnail(File inputFile, OutputStream outputStream,
                     String format, int width, int height,
                     ConversionCommand.CompressionQuality quality) throws Exception;
}
