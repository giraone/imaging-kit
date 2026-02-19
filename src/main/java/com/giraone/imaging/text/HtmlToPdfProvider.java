package com.giraone.imaging.text;

import com.giraone.imaging.video.VideoProvider;
import com.giraone.imaging.video.VideoProviderFfmpeg;

import java.io.File;

public interface HtmlToPdfProvider {

    HtmlToPdfProvider _THIS = new HtmlToPdfProviderOpenHtml();

    /**
     * Get the singleton instance of the HtmlToPdfProviderOpenHtml.
     * @return the singleton instance
     */
    @SuppressWarnings("unused")
    static HtmlToPdfProvider getInstance() {
        return _THIS;
    }

    /**
     * Convert HTML input into PDF output
     * @param inputHtml input HTML content
     * @param outputPdfFile Path of the Output PDF file
     * @throws Exception on any error
     */
    void renderHtmlToPdf(String inputHtml, File outputPdfFile) throws Exception;
}
