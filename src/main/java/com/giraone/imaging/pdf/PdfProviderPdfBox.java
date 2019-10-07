package com.giraone.imaging.pdf;

import com.giraone.imaging.ConversionCommand;
import com.giraone.imaging.FileInfo;
import com.giraone.imaging.FormatNotSupportedException;
import com.giraone.imaging.java2.ProviderJava2D;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface for imaging operation on PDFs.
 */
public class PdfProviderPdfBox implements PdfProvider {

    private static final float POINTS_PER_INCH = 72;

    private final static PdfProviderPdfBox _THIS = new PdfProviderPdfBox();

    public static PdfProviderPdfBox getInstance() {
        return _THIS;
    }

    private final ProviderJava2D imagingProvider = new ProviderJava2D();

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
    @Override
    public void createThumbNail(File inputFile, OutputStream outputStream, String format, int width, int height,
                                ConversionCommand.CompressionQuality quality, ConversionCommand.SpeedHint speedHint) throws Exception {
        ConversionCommand command = new ConversionCommand();
        command.setOutputFormat(format);
        command.setDimension(new Dimension(width, height));
        command.setQuality(quality);
        command.setSpeedHint(speedHint);

        try (PDDocument document = PDDocument.load(inputFile)) {
            PDFRenderer renderer = new PDFRenderer(document);

            // Page 1, do not scale DPIs and use RGB
            BufferedImage image = renderer.renderImage(0, 1.0f, ImageType.RGB);

            imagingProvider.convertAndWriteImageAsJpeg(image, outputStream, command);
        }
    }

    @Override
    public int countPages(File pdfFile) throws Exception {

        try (PDDocument document = PDDocument.load(pdfFile)) {
            return document.getPages().getCount();
        }
    }

    @Override
    public PdfDocumentInformation getDocumentInformation(File pdfFile) throws Exception {

        try (PDDocument document = PDDocument.load(pdfFile)) {
            return PdfDocumentInformation.build(document.getDocumentInformation());
        }
    }

    @Override
    public void createPdfFromImages(File[] imageFiles, PdfDocumentInformation documentInformation,
                                    File outputPdfFile) throws Exception {

        try (PDDocument document = new PDDocument()) {
            PDDocumentInformation pdDocumentInformation = documentInformation.build();
            document.setDocumentInformation(pdDocumentInformation);
            for (File imageFile : imageFiles) {
                PDRectangle rectangle = getPdRectangle(imageFile);
                PDPage page = new PDPage(rectangle);
                document.addPage(page);
                PDImageXObject pdImage = PDImageXObject.createFromFileByExtension(imageFile, document);
                try (PDPageContentStream contents = new PDPageContentStream(document, page)) {
                    contents.drawImage(pdImage, 0, 0);
                }
            }
            document.save(outputPdfFile.getAbsolutePath());
        }
    }

    @Override
    public void createPdfFromImages(byte[][] imageFileByteArrays, PdfDocumentInformation documentInformation,
                                    int width, int height, OutputStream outputStream) throws Exception {

        try (PDDocument document = new PDDocument()) {
            PDDocumentInformation pdDocumentInformation = documentInformation.build();
            document.setDocumentInformation(pdDocumentInformation);
            int imageNumber = 0; // only for error messages
            for (byte[] imageFileByteArray : imageFileByteArrays) {
                PDRectangle rectangle = new PDRectangle(width, height);
                PDPage page = new PDPage(rectangle);
                document.addPage(page);
                PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageFileByteArray, "image-" + (++imageNumber));
                try (PDPageContentStream contents = new PDPageContentStream(document, page)) {
                    contents.drawImage(pdImage, 0, 0);
                }
            }
            document.save(outputStream);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private PDRectangle getPdRectangle(File imageFile) throws IOException, FormatNotSupportedException {
        final FileInfo imageInfo = imagingProvider.fetchFileInfo(imageFile);
        return new PDRectangle(0, 0, imageInfo.getWidth(), imageInfo.getHeight());
    }
}