package com.giraone.imaging.pdf;

import com.giraone.imaging.ConversionCommand;
import com.giraone.imaging.java2.ProviderJava2D;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;

/**
 * Interface for imaging operation on PDFs.
 */
public class PdfProviderPdfBox implements PdfProvider {

    private final static PdfProviderPdfBox _THIS = new PdfProviderPdfBox();
    private final ProviderJava2D imagingProvider = new ProviderJava2D();

    public static PdfProviderPdfBox getInstance() {
        return _THIS;
    }

    public PdfProviderPdfBox() {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
    }

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

    public void createThumbNail(File inputFile, OutputStream outputStream, String format, int width, int height,
                                ConversionCommand.CompressionQuality quality, ConversionCommand.SpeedHint speedHint) throws Exception {
        ConversionCommand command = new ConversionCommand();
        command.setOutputFormat(format);
        command.setDimension(new Dimension(width, height));
        command.setQuality(quality);
        command.setSpeedHint(speedHint);

        try (PDDocument doc = PDDocument.load(inputFile)) {
            PDFRenderer renderer = new PDFRenderer(doc);

            // Page 1, do not scale DPIs and use RGB
            BufferedImage image = renderer.renderImage(0, 1.0f, ImageType.RGB);

            // ImageIO.write(image, "JPEG", new File(outputFileName));

            imagingProvider.convertAndWriteImageAsJpeg(image, outputStream, command);
        }
    }
}