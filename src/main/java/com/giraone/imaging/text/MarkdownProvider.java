package com.giraone.imaging.text;

import com.giraone.imaging.ThumbnailProvider;

import java.awt.*;
import java.io.File;

/**
 * Interface for thumbnail generation operations on Markdown documents.
 */
public interface MarkdownProvider extends ThumbnailProvider {

    // DIN A4 dimensions in millimeters
    int A4_WIDTH_MM = 210;
    int A4_HEIGHT_MM = 297;
    // Default DPI for screen/image rendering
    int DEFAULT_DPI_SCREEN = 96;
    // Default DPI for print rendering
    int DEFAULT_DPI_PRINT = 300;

    // A4 = 210 mm × 297 mm = 8.27 inch × 11.69 inch
    // A4 at 96 DPI = 794 × 1122 pixels
    int A4_WIDTH_PX_SCREEN = mmToPixels(A4_WIDTH_MM, DEFAULT_DPI_SCREEN);
    int A4_HEIGHT_PX_SCREEN = mmToPixels(A4_HEIGHT_MM, DEFAULT_DPI_SCREEN);
    // A4 at 96 DPI = 794 × 1122 pixels
    int A4_WIDTH_PX_PRINT = mmToPixels(A4_WIDTH_MM, DEFAULT_DPI_PRINT);
    int A4_HEIGHT_PX_PRINT = mmToPixels(A4_HEIGHT_MM, DEFAULT_DPI_PRINT);

    // 1 point (pt) = 1/72 inch
    // 12 × 96/72 = 12×1.33 = 16, 12 pt = 16 pixels (CSS pixels / device pixels at 96 DPI)
    int PT12_PX = ptToPixels(12, DEFAULT_DPI_SCREEN);

    Dimension DIMENSION_A4_PORTRAIT = new Dimension(A4_WIDTH_PX_SCREEN, A4_HEIGHT_PX_SCREEN);

    String HTML_WRAP_A4_PORTRAIT = """
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
     * @param dimension Dimension, e.g. DIMENSION_A4_PORTRAIT
     * @param htmlWrapTemplate HTML template to be used.
     * @throws Exception on any error
     */
    void createPdf(File inputMarkdownFile, File outputPdfFile, Dimension dimension, String htmlWrapTemplate) throws Exception;

    /**
     * Convert a Markdown file to a printable HTML
     * @param inputMarkdownFile input MD file
     * @param outputHtmlFile output HTML file
     * @param dimension Dimension, e.g. DIMENSION_A4_PORTRAIT
     * @param htmlWrapTemplate HTML template to be used.
     * @throws Exception on any error
     */
    void createHtml(File inputMarkdownFile, File outputHtmlFile, Dimension dimension, String htmlWrapTemplate) throws Exception;

    /**
     * Convert a Markdown file to a printable PDF
     * @param inputMarkdownFile input MD file
     * @param outputPdfFile output PDF file
     * @throws Exception on any error
     */
    default void createPdf(File inputMarkdownFile, File outputPdfFile) throws Exception {
        createPdf(inputMarkdownFile, outputPdfFile, DIMENSION_A4_PORTRAIT, HTML_WRAP_A4_PORTRAIT);
    }

    /**
     * Convert a Markdown file to a printable HTML
     * @param inputMarkdownFile input MD file
     * @param outputPdfFile output HTML file
     * @throws Exception on any error
     */
    default void createHtml(File inputMarkdownFile, File outputPdfFile) throws Exception {
        createHtml(inputMarkdownFile, outputPdfFile, DIMENSION_A4_PORTRAIT, HTML_WRAP_A4_PORTRAIT);
    }


    /**
     * Convert millimeters to pixels at a given DPI.
     * @param mm millimeters
     * @param dpi dots per inch
     * @return pixels
     */
    @SuppressWarnings("SameParameterValue")
    static int mmToPixels(int mm, int dpi) {
        // 1 inch = 25.4 mm
        return (int) Math.round(mm * dpi / 25.4);
    }

    /**
     * Convert pt to pixels at a given DPI.
     * @param pt pt (Punkt)
     * @param dpi dots per inch
     * @return pixels
     */
    @SuppressWarnings("SameParameterValue")
    static int ptToPixels(int pt, int dpi) {
        // pt × dpi/72
        return pt * dpi / 72;
    }
}
