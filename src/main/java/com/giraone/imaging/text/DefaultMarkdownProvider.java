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
public class DefaultMarkdownProvider implements MarkdownProvider {

    // for debugging the HTML generation
    private static final boolean DUMP_HTML = true;
    // the HTML document template (DIN A4 portrait mode) to be used
    private static final String HTML_WRAP_A4_PORTRAIT = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8"/>
        <style>
        body {
            font-family: font-family: Arial,Helvetica,sans-serif;
            font-size: 12pt;
            width: 210mm;
            max-height: 270mm;
            margin: 13mm;
        }
        h1, h2, h3 {
            border-bottom: 1px solid #ccc;
            padding-bottom: 4px;
        }
        ul {
            margin-left: 1mm;
            padding-left: 3mm;
        }
        </style>
        </head>
        <body>
        %bodyHtml%
        </body>
        </html>
        """;

    private static final DefaultMarkdownProvider _THIS = new DefaultMarkdownProvider();

    // is thread-safe - see JavaDoc
    private static final Parser markdownParser = Parser.builder().build();
    // is thread-safe - see JavaDoc
    private static final HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();

    /**
     * Get the singleton instance of the PdfProviderPdfBox.
     * @return the singleton instance
     */
    @SuppressWarnings("unused")
    public static DefaultMarkdownProvider getInstance() {
        return _THIS;
    }

    /**
     * Create a thumbnail image for a given Markdown file.
     * @param inputFile Input file.
     * @param outputStream OutputStream, to which the thumbnail is written. Important: Stream is not closed!
     * @param format Output file format given as a MIME type.
     * @param width Width in pixel.
     * @param height Height in pixel.
     * @param quality Quality factor for output compression.
     * @throws Exception on any error opening the file, converting the file or writing to the output.
     */
    @Override
    public void createThumbnail(File inputFile, OutputStream outputStream, String format, int width, int height,
                                ConversionCommand.CompressionQuality quality) throws Exception {

        final BufferedImage image;
        // 0. Prepare reader
        try (final FileReader reader = new FileReader(inputFile)) {
            // 1. Markdown to HTML
            // 2. Wrap HTML with CSS for consistent rendering
            // 3. Render HTML to BufferedImage
            image = createThumbnailAsBufferedImage(reader, width, height);
        }
        // 4. Write image
        ImageIO.write(image, ImageToFileWriter.mimeTypeToIoWriteFormat(format), outputStream);
    }

    public static BufferedImage createThumbnailAsBufferedImage(Reader reader, int width, int height) throws IOException {
        // 1. Markdown to HTML
        final Node document = markdownParser.parseReader(reader);
        final String htmlString = htmlRenderer.render(document);
        // 2. Wrap HTML with CSS for consistent rendering
        final String fullHtml = wrapHtml(htmlString);
        dumpHtml(fullHtml);
        // 3. Render HTML to BufferedImage
        return renderHtmlToImage(fullHtml, width, height);
    }

    private static void dumpHtml(String fullHtml) {
        try {
            final File tmpFile = File.createTempFile("markdown-", ".html");
            Files.writeString(tmpFile.toPath(), fullHtml);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String wrapHtml(String bodyHtml) {
        return HTML_WRAP_A4_PORTRAIT
            .replace("%bodyHtml%", bodyHtml);
    }

    /**
     * Renders HTML into a BufferedImage.
     * @param html full HTML document
     * @param width fixed render width (px)
     * @param height fixed render height (px)
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
}
