package com.giraone.imaging.text;

import com.giraone.imaging.ThumbnailProvider;

import java.io.File;

/**
 * Interface for thumbnail generation operations on Markdown documents.
 */
public interface MarkdownProvider extends ThumbnailProvider {

    MarkdownProviderFlexmark _THIS = new MarkdownProviderFlexmark();

    /**
     * Get the singleton instance of the MarkdownProviderFlexmark.
     * @return the singleton instance
     */
    @SuppressWarnings("unused")
    static MarkdownProviderFlexmark getInstance() {
        return _THIS;
    }

    /**
     * Convert a markdown file to a printable PDF
     * @param inputMarkdownFile input MD file
     * @param outputPdfFile output PDF file
     * @throws Exception on any error
     */
    void createPdf(File inputMarkdownFile, File outputPdfFile) throws Exception;

    /**
     * Convert a markdown file to a printable PDF
     * @param inputMarkdownFile input MD file
     * @param outputHtmlFile output PDF file
     * @throws Exception on any error
     */
    void createHtml(File inputMarkdownFile, File outputHtmlFile) throws Exception;
}
