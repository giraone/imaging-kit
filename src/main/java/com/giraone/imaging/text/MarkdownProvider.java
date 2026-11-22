package com.giraone.imaging.text;

import com.giraone.imaging.ThumbnailProvider;

import java.awt.*;
import java.io.File;

import static com.giraone.imaging.text.PixelMath.*;

/**
 * Interface for thumbnail generation operations on Markdown documents.
 */
public interface MarkdownProvider extends ThumbnailProvider {

    //--- DIN A4 Portrait ---

    // Dimensions in millimeters
    float A4_PORTRAIT_WIDTH_MM = 210.0f;
    float A4_PORTRAIT_HEIGHT_MM = 297.0f;
    int A4_PORTRAIT_WIDTH_PX_SCREEN = mmToPixels(A4_PORTRAIT_WIDTH_MM, DEFAULT_DPI_SCREEN);
    int A4_PORTRAIT_HEIGHT_PX_SCREEN = mmToPixels(A4_PORTRAIT_HEIGHT_MM, DEFAULT_DPI_SCREEN);
    int A4_PORTRAIT_WIDTH_PX_PRINT = mmToPixels(A4_PORTRAIT_WIDTH_MM, DEFAULT_DPI_PRINT);
    int A4_PORTRAIT_HEIGHT_PX_PRINT = mmToPixels(A4_PORTRAIT_HEIGHT_MM, DEFAULT_DPI_PRINT);
    Dimension DIMENSION_A4_PORTRAIT = new Dimension(A4_PORTRAIT_WIDTH_PX_PRINT, A4_PORTRAIT_HEIGHT_PX_PRINT);

    //--- DIN A4 Landscape ---

    // Dimensions in millimeters
    float A4_LANDSCAPE_WIDTH_MM = 297.0f;
    float A4_LANDSCAPE_HEIGHT_MM = 210.0f;
    int A4_LANDSCAPE_WIDTH_PX_PRINT = mmToPixels(A4_LANDSCAPE_WIDTH_MM, DEFAULT_DPI_PRINT);
    int A4_LANDSCAPE_HEIGHT_PX_PRINT = mmToPixels(A4_LANDSCAPE_HEIGHT_MM, DEFAULT_DPI_PRINT);
    Dimension DIMENSION_A4_LANDSCAPE = new Dimension(A4_LANDSCAPE_WIDTH_PX_PRINT, A4_LANDSCAPE_HEIGHT_PX_PRINT);

    //--- US Letter Portrait ---

    float LETTER_PORTRAIT_WIDTH_MM = 215.9f;
    float LETTER_PORTRAIT_HEIGHT_MM = 279.4f;
    int LETTER_PORTRAIT_WIDTH_PX_PRINT = mmToPixels(LETTER_PORTRAIT_WIDTH_MM, DEFAULT_DPI_PRINT);
    int LETTER_PORTRAIT_HEIGHT_PX_PRINT = mmToPixels(LETTER_PORTRAIT_HEIGHT_MM, DEFAULT_DPI_PRINT);
    Dimension DIMENSION_LETTER_PORTRAIT = new Dimension(LETTER_PORTRAIT_WIDTH_PX_PRINT, LETTER_PORTRAIT_HEIGHT_PX_PRINT);

    //--- US Letter Landscape ---

    float LETTER_LANDSCAPE_WIDTH_MM = 279.4f;
    float LETTER_LANDSCAPE_HEIGHT_MM = 215.9f;
    int LETTER_LANDSCAPE_WIDTH_PX_PRINT = mmToPixels(LETTER_LANDSCAPE_WIDTH_MM, DEFAULT_DPI_PRINT);
    int LETTER_LANDSCAPE_HEIGHT_PX_PRINT = mmToPixels(LETTER_LANDSCAPE_HEIGHT_MM, DEFAULT_DPI_PRINT);
    Dimension DIMENSION_LETTER_LANDSCAPE = new Dimension(LETTER_LANDSCAPE_WIDTH_PX_PRINT, LETTER_LANDSCAPE_HEIGHT_PX_PRINT);

    //------------------------

    String HTML_WRAP_DEFAULT = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8"/>
            <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <style>
        @page {
          margin: 36px 48px;
        }
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        html {
            font-family: Arial, Helvetica, sans-serif;
            font-size: 12pt;
            background-color: white;
        }
        body {
            width: 100%;
            overflow-x: auto;
        }
        h1, h2, h3 {
            border-bottom: 1px solid #ccc;
            padding-bottom: 0.3em;
            margin-top: 0.5em;
            margin-bottom: 0.3em;
        }
        h1 { font-size: 1.5em; }
        h2 { font-size: 1.3em; }
        h3 { font-size: 1.1em; }
        
        p {
            margin-bottom: 0.5em;
            page-break-inside: avoid;
        }
        ul, ol {
            margin-left: 0.2em;
            padding-left: 1.5em;
            margin-bottom: 0.5em;
            page-break-inside: avoid;
        }
        code {
            padding: 0.2em 0.2em;
            border-radius: 0.2em;
            page-break-inside: avoid;
        }
        pre {
            background-color: #f8f8f8;
            padding: 0.2em;
            border-radius: 0.5em;
            margin-bottom: 0.5em;
            white-space: pre-wrap; /* preserve whitespace + allow wrapping */
            page-break-inside: avoid;
        }
        </style>
        </head>
        <body>
        %bodyHtml%
        </body>
        </html>
        """;

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
     * Convert a Markdown file to a printable PDF
     * @param inputMarkdownFile input MD file
     * @param outputPdfFile output PDF file
     * @param dimension Dimension, e.g. DIMENSION_A4_PORTRAIT, DIMENSION_A4_LANDSCAPE
     * @param htmlWrapTemplate HTML template to be used.
     * @throws Exception on any error
     */
    void createPdf(File inputMarkdownFile, File outputPdfFile, Dimension dimension, String htmlWrapTemplate) throws Exception;

    /**
     * Convert a Markdown file to a printable HTML
     * @param inputMarkdownFile input MD file
     * @param outputHtmlFile output HTML file
     * @param dimension Dimension, e.g. DIMENSION_A4_PORTRAIT, DIMENSION_A4_LANDSCAPE
     * @param htmlWrapTemplate HTML template to be used.
     * @throws Exception on any error
     */
    void createHtml(File inputMarkdownFile, File outputHtmlFile, Dimension dimension, String htmlWrapTemplate) throws Exception;

    /**
     * Convert a Markdown file to a printable PDF
     * @param inputMarkdownFile input MD file
     * @param outputPdfFile output PDF file
     * @param dimension Dimension, e.g. DIMENSION_A4_PORTRAIT, DIMENSION_A4_LANDSCAPE
     * @throws Exception on any error
     */
    default void createPdf(File inputMarkdownFile, File outputPdfFile, Dimension dimension) throws Exception {
        createPdf(inputMarkdownFile, outputPdfFile, dimension, HTML_WRAP_DEFAULT);
    }

    /**
     * Convert a Markdown file to a printable HTML
     * @param inputMarkdownFile input MD file
     * @param outputPdfFile output HTML file
     * @param dimension Dimension, e.g. DIMENSION_A4_PORTRAIT, DIMENSION_A4_LANDSCAPE
     * @throws Exception on any error
     */
    default void createHtml(File inputMarkdownFile, File outputPdfFile, Dimension dimension) throws Exception {
        createHtml(inputMarkdownFile, outputPdfFile, dimension, HTML_WRAP_DEFAULT);
    }
}
