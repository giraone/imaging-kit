package com.giraone.imaging.text;

import com.giraone.imaging.ConversionCommand;
import com.giraone.imaging.java2.ImageToFileWriter;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import org.w3c.dom.Document;
import org.xhtmlrenderer.simple.Graphics2DRenderer;
import org.xhtmlrenderer.util.ImageUtil;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * A class to provide thumbnail creation of Markdown documents using a two-step approach with
 * <ul>
 *     <li>Markdown to HTML conversion using com.vladsch.flexmark:flexmark-all</li>
 *     <li>HTML to Java2D image conversion using org.xhtmlrenderer:flying-saucer-core</li>
 * </ul>
 */
public class MarkdownProviderFlexmark implements MarkdownProvider {

    // DIN A4 dimensions in millimeters
    public static final int A4_WIDTH_MM = 210;
    public static final int A4_HEIGHT_MM = 297;
    // Default DPI for screen/image rendering
    public static final int DEFAULT_DPI_SCREEN = 96;
    // Default DPI for print rendering
    public static final int DEFAULT_DPI_PRINT = 300;

    // A4 = 210 mm × 297 mm = 8.27 inch × 11.69 inch
    // A4 at 96 DPI = 794 × 1122 pixels
    public static final int A4_WIDTH_PX_SCREEN = mmToPixels(A4_WIDTH_MM, DEFAULT_DPI_SCREEN);
    public static final int A4_HEIGHT_PX_SCREEN = mmToPixels(A4_HEIGHT_MM, DEFAULT_DPI_SCREEN);
    // A4 at 96 DPI = 794 × 1122 pixels
    public static final int A4_WIDTH_PX_PRINT = mmToPixels(A4_WIDTH_MM, DEFAULT_DPI_PRINT);
    public static final int A4_HEIGHT_PX_PRINT = mmToPixels(A4_HEIGHT_MM, DEFAULT_DPI_PRINT);
    // 1 point (pt) = 1/72 inch
    // 12 × 96/72 = 12×1.33 = 16, 12 pt = 16 pixels (CSS pixels / device pixels at 96 DPI)
    public static final int PT12_PX = ptToPixels(12, DEFAULT_DPI_SCREEN);

    // for debugging the HTML generation
    private static final boolean DUMP_HTML = false;
    // the HTML document template (DIN A4 portrait mode) to be used

    public static final String HTML_WRAP_A4_PORTRAIT_IMAGE = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8"/>
            <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        html {
            font-family: Arial, Helvetica, sans-serif;
            font-size: %fontSizePx%px;
            background-color: white;
        }
        body {
            width: %bodyWidthPx%px;
            max-height: %bodyHeightPx%px;
            padding: 5%;
            overflow: hidden;
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
        }
        ul, ol {
            margin-left: 0.2em;
            padding-left: 1.5em;
            margin-bottom: 0.5em;
        }
        code {
            padding: 0.2em 0.2em;
            border-radius: 0.2em;
        }
        pre {
            background-color: #f8f8f8;
            padding: 0.2em;
            border-radius: 0.5em;
            margin-bottom: 0.5em;
            white-space: pre-wrap;     /* preserve whitespace + allow wrapping */
            word-break: break-word;    /* break long tokens if needed */
        }
        </style>
        </head>
        <body>
        %bodyHtml%
        </body>
        </html>
        """;

    public static final String HTML_WRAP_A4_PORTRAIT_PDF = """
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
            white-space: pre-wrap;     /* preserve whitespace + allow wrapping */
            page-break-inside: avoid;
        }
        </style>
        </head>
        <body>
        %bodyHtml%
        </body>
        </html>
        """;

    // Flexmark Markdown Parser is thread-safe - see JavaDoc
    private static final Parser markdownParser = Parser.builder().build();
    // Flexmark HTML renderer is thread-safe - see JavaDoc
    private static final HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
    private static final HtmlToPdfProvider htmlToPdfProvider = new HtmlToPdfProviderOpenHtml();

    /**
     * Convert millimeters to pixels at a given DPI.
     * @param mm millimeters
     * @param dpi dots per inch
     * @return pixels
     */
    @SuppressWarnings("SameParameterValue")
    private static int mmToPixels(int mm, int dpi) {
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
    private static int ptToPixels(int pt, int dpi) {
        // pt × dpi/72
        return pt * dpi / 72;
    }

    /**
     * Create a thumbnail image for a given file.
     * @param inputFile Input file.
     * @param conversionCommand The command with the definitions of the output (path, format, width, height and quality).
     * @throws Exception on any error opening the file, converting the file or writing to the output.
     */
    @Override
    public void createThumbnail(File inputFile, ConversionCommand conversionCommand) throws Exception {

        try (final OutputStream outputStream = new FileOutputStream(conversionCommand.getOutputFile())) {
            final BufferedImage image;
            // 0. Prepare reader
            try (final FileReader reader = new FileReader(inputFile)) {
                // 1. Markdown to HTML
                // 2. Wrap HTML with CSS for consistent rendering
                // 3. Render HTML to BufferedImage
                image = createThumbnailAsBufferedImage(reader, conversionCommand.getDimension());
            }
            // 4. Write image
            ImageIO.write(image, ImageToFileWriter.mimeTypeToIoWriteFormat(conversionCommand.getOutputFormat()), outputStream);
        }
    }

    public BufferedImage createThumbnailAsBufferedImage(Reader reader, Dimension dimension) throws IOException {
        // 1. Markdown to HTML in given size (dimension)
        final String fullHtml = convertToHtml(reader, dimension);
        // 2. Render HTML to BufferedImage
        return renderHtmlToImage(fullHtml, dimension.width, dimension.height);
    }

    @Override
    public void createPdf(File inputMarkdownFile, File outputPdfFile) throws Exception {
        try (final FileReader reader = new FileReader(inputMarkdownFile)) {
            // 1. Markdown to HTML in A4 dimension
            final String fullHtml = convertToHtml(reader, new Dimension(A4_WIDTH_PX_SCREEN, A4_HEIGHT_PX_SCREEN));
            // 2. Render HTML to PDF
            htmlToPdfProvider.renderHtmlToPdf(fullHtml, outputPdfFile);
        }
    }

    //--- convert to HTML -----------------------------------------------------------------------------------------------

    private static Document parseDocument(InputSource source) throws Exception {
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        dbFactory.setExpandEntityReferences(false);
        dbFactory.setXIncludeAware(false);
        final DocumentBuilder builder = dbFactory.newDocumentBuilder();
        try {
            return builder.parse(source);
        } catch (SAXParseException spe) {
            throw new Exception(spe.getMessage() + " in Line/Col " + spe.getLineNumber() + "/" + spe.getColumnNumber());
        }
    }

    private static String convertToHtml(Reader reader, Dimension dimension) throws IOException {
        // 1. Markdown to HTML
        final Node document = markdownParser.parseReader(reader);
        final String htmlString = htmlRenderer.render(document);
        // 2. Wrap HTML with CSS for consistent rendering
        final String fullHtml = wrapHtml(HTML_WRAP_A4_PORTRAIT_IMAGE, htmlString,
            PT12_PX * dimension.height / A4_HEIGHT_PX_SCREEN, dimension.width, dimension.height);
        if (DUMP_HTML) {
            dumpHtml(fullHtml);
        }
        return fullHtml;
    }

    private static String wrapHtml(String template, String bodyHtml, int fontSizePx, int bodyWidthPx, int bodyHeightPx) {
        return template
            .replace("%fontSizePx%", Integer.toString(fontSizePx))
            .replace("%bodyWidthPx%", Integer.toString(bodyWidthPx))
            .replace("%bodyHeightPx%", Integer.toString(bodyHeightPx))
            .replace("%bodyHtml%", bodyHtml);
    }

    private static void dumpHtml(String fullHtml) {
        try {
            final File tmpFile = File.createTempFile("markdown-", ".html");
            Files.writeString(tmpFile.toPath(), fullHtml);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //--- convert to image ---------------------------------------------------------------------------------------------

    /**
     * Renders HTML into a BufferedImage.
     * IMPORTANT: The HTML template uses A4 dimensions (210mm x 297mm). For proper rendering,
     * the width and height should maintain A4 proportions. Use A4_WIDTH_PX and A4_HEIGHT_PX
     * constants or scale proportionally.
     * @param html full HTML document
     * @param width fixed render width (px) - should match A4 proportions
     * @param height fixed render height (px) - should match A4 proportions
     */
    private static BufferedImage renderHtmlToImage(String html, int width, int height) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8))) {
            return renderHtmlToImage(in, width, height);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static BufferedImage renderHtmlToImage(ByteArrayInputStream in, int width, int height) throws Exception {
        final InputSource inputSource = new InputSource(in);
        final Document document = parseDocument(inputSource);
        final Graphics2DRenderer g2r = new Graphics2DRenderer(document, "");
        final Dimension dim = new Dimension(width, height);
        final BufferedImage bufferedImage = new BufferedImage((int) dim.getWidth(), (int) dim.getHeight(), BufferedImage.TYPE_INT_RGB);
        ImageUtil.withGraphics(bufferedImage, (g) -> {
            // Fill background with white to avoid black areas on short documents
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, (int) dim.getWidth(), (int) dim.getHeight());
            // Render HTML content
            g2r.layout(g, dim);
            g2r.render(g);
        });
        return bufferedImage;
    }
}
