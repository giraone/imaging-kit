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

    // for debugging the HTML generation
    private static final boolean DUMP_HTML = true;

    // Flexmark Markdown Parser is thread-safe - see Javadoc
    private static final Parser markdownParser = Parser.builder().build();
    // Flexmark HTML renderer is thread-safe - see Javadoc
    private static final HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
    private static final HtmlToPdfProvider htmlToPdfProvider = new HtmlToPdfProviderOpenHtml();

    /**
     * Create new instance
     */
    MarkdownProviderFlexmark() {
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
        final String fullHtml = convertToHtml(reader, dimension, MarkdownProvider.HTML_WRAP_A4_PORTRAIT);
        // 2. Render HTML to BufferedImage
        return renderHtmlToImage(fullHtml, dimension.width, dimension.height);
    }

    @Override
    public void createPdf(File inputMarkdownFile, File outputPdfFile, Dimension dimension, String htmlWrapTemplate) throws Exception {
        try (final FileReader reader = new FileReader(inputMarkdownFile)) {
            // 1. Markdown to HTML in A4 dimension
            final String fullHtml = convertToHtml(reader, dimension, htmlWrapTemplate);
            // 2. Render HTML to PDF
            htmlToPdfProvider.renderHtmlToPdf(fullHtml, outputPdfFile);
        }
    }

    @Override
    public void createHtml(File inputMarkdownFile, File outputHtmlFile, Dimension dimension, String htmlWrapTemplate) throws Exception {
        try (final FileReader reader = new FileReader(inputMarkdownFile)) {
            // 1. Markdown to HTML in A4 dimension
            final String fullHtml = convertToHtml(reader, dimension, htmlWrapTemplate);
            // 2. Store HTML in file
            Files.writeString(outputHtmlFile.toPath(), fullHtml, StandardCharsets.UTF_8);
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

    private static String convertToHtml(Reader reader, Dimension dimension, String htmlWrapTemplate) throws IOException {
        // 1. Markdown to HTML
        final Node document = markdownParser.parseReader(reader);
        final String htmlString = htmlRenderer.render(document);
        // 2. Wrap HTML with CSS for consistent rendering
        final String fullHtml = wrapHtml(htmlWrapTemplate, htmlString,
            PT12_PX * dimension.height / A4_HEIGHT_PX_SCREEN, dimension.width, dimension.height);
        if (DUMP_HTML) {
            dumpHtml(fullHtml);
        }
        return fullHtml;
    }

    @SuppressWarnings("SameParameterValue")
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
