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

    /**
     * Extract metadata information from a PDF file.
     * @param pdfFile the PDF file to read
     * @return the document information containing title, author, subject, keywords, etc.
     * @throws Exception on any error reading the PDF file
     */
    PdfDocumentInformation getDocumentInformation(File pdfFile) throws Exception;

    /**
     * Count the number of pages in a PDF file.
     * @param pdfFile the PDF file to analyze
     * @return the number of pages in the PDF
     * @throws Exception on any error reading the PDF file
     */
    int countPages(File pdfFile) throws Exception;

    /**
     * Create a PDF document from multiple image files.
     * Each image will be placed on a separate page.
     * @param imageFiles array of image files to include in the PDF
     * @param documentInformation metadata to embed in the PDF document
     * @param outputPdfFile the output PDF file to create
     * @throws Exception on any error reading the images or writing the PDF
     */
    void createPdfFromImages(File[] imageFiles, PdfDocumentInformation documentInformation, File outputPdfFile) throws Exception;

    /**
     * Create a PDF document from multiple image byte arrays.
     * Each image will be placed on a separate page with the specified dimensions.
     * @param imageFileByteArrays array of image data as byte arrays
     * @param documentInformation metadata to embed in the PDF document
     * @param width page width in pixels
     * @param height page height in pixels
     * @param outputStream output stream to which the PDF is written. Important: Stream is not closed!
     * @throws Exception on any error processing the images or writing the PDF
     */
    void createPdfFromImages(byte[][] imageFileByteArrays, PdfDocumentInformation documentInformation,
                             int width, int height, OutputStream outputStream) throws Exception;
}