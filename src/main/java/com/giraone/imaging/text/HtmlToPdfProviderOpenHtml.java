package com.giraone.imaging.text;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class HtmlToPdfProviderOpenHtml implements HtmlToPdfProvider {

    /**
     * Convert HTML input into PDF output
     * @param inputHtml input HTML content
     * @param outputPdfFile Path of the Output PDF file
     * @throws Exception on any error
     */
    @Override
    public void renderHtmlToPdf(String inputHtml, File outputPdfFile) throws Exception {

        final PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withUri(outputPdfFile.getAbsolutePath());
        // builder.useSVGDrawer(new BatikSVGDrawer());  // SVG support
        builder.withHtmlContent(inputHtml, null);
        try (OutputStream out = new FileOutputStream(outputPdfFile)) {
            builder.toStream(out);
            builder.run();
        }
    }
}
