package com.giraone.imaging.text;

import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static com.giraone.imaging.text.PixelMath.DEFAULT_DPI_PRINT;
import static com.giraone.imaging.text.PixelMath.pixelToMm;

public class HtmlToPdfProviderOpenHtml implements HtmlToPdfProvider {

    /**
     * Convert HTML input into PDF output
     * @param inputHtml input HTML content
     * @param outputPdfFile Path of the Output PDF file
     * @param dimension Page dimension in pixels
     * @throws Exception on any error
     */
    @Override
    public void renderHtmlToPdf(String inputHtml, File outputPdfFile, Dimension dimension) throws Exception {

        final PdfRendererBuilder builder = new PdfRendererBuilder();
        final float width = pixelToMm(dimension.getWidth(), DEFAULT_DPI_PRINT);
        final float height = pixelToMm(dimension.getHeight(), DEFAULT_DPI_PRINT);
        builder.useDefaultPageSize(width, height, BaseRendererBuilder.PageSizeUnits.MM);
        builder.withUri(outputPdfFile.getAbsolutePath());
        // builder.useSVGDrawer(new BatikSVGDrawer());  // SVG support
        builder.withHtmlContent(inputHtml, null);
        try (OutputStream out = new FileOutputStream(outputPdfFile)) {
            builder.toStream(out);
            builder.run();
        }
    }
}
