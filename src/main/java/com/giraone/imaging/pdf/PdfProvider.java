package com.giraone.imaging.pdf;

import com.giraone.imaging.ConversionCommand;

import java.io.File;
import java.io.OutputStream;

/**
 * Interface for imaging operation on PDFs.
 */
public interface PdfProvider {
    /**
     * Create a thumbnail image for a given PDF file.
     *
     * @param inputFile     Input file.
     * @param outputStream  OutputStream, to which the thumbnail is written. Important: Stream is not closed!
     * @param format        Output file format given as a MIME type.
     * @param width         Width in pixel.
     * @param height        Height in pixel.
     * @param quality       Quality factor for output compression.
     * @param speedHint     Speed factor for conversion.
     */

    void createThumbNail(File inputFile, OutputStream outputStream,
                         String format, int width, int height,
                         ConversionCommand.CompressionQuality quality,
                         ConversionCommand.SpeedHint speedHint) throws Exception;

    PdfDocumentInformation getDocumentInformation(File pdfFile) throws Exception;

    int countPages(File pdfFile) throws Exception;

    void createPdfFromImages(File[] imageFiles, PdfDocumentInformation documentInformation, File outputPdfFile) throws Exception;
}