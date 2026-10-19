package com.giraone.imaging.java2;

import com.giraone.imaging.FileInfo;
import com.giraone.imaging.FileTypeDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

import static com.giraone.imaging.MimeTypes.*;

public class ImageOpener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageOpener.class);
    private static final ImageObserver imageObserver = LoggerImageObserver.getInstance();

    // Hide constructor
    private ImageOpener() {
    }

    /**
     * Open an image file and return the buffered image plus some image information.
     * @param file the image file to open
     * @return the image and information tupel
     * @throws IOException on any error opening the image file
     */
    public static ImagePlusInfo openImage(File file) throws IOException {
        final FileTypeDetector.FileType fileType = FileTypeDetector.getInstance().getFileType(file);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("openImage (using File): fileType = {}", fileType);
        }
        return openImage(file, fileType);
    }

    /**
     * Open an image file and return the buffered image plus some image information.
     * @param path the image path to open
     * @return the image and information tupel
     * @throws IOException on any error opening the image file
     */
    public static ImagePlusInfo openImage(Path path) throws IOException {
        final FileTypeDetector.FileType fileType = FileTypeDetector.getInstance().getFileType(path);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("openImage (using Path): fileType = {}", fileType);
        }
        return openImage(path, fileType);
    }

    static ImagePlusInfo openImage(File file, FileTypeDetector.FileType fileType) throws IOException {
        BufferedImage image;
        FileInfo fileInfo;
        switch (fileType) {
            case JPEG:
                image = loadImageUsingToolkit(file);
                fileInfo = getFileInfo(image, IMAGE_JPEG, FileTypeDetector.FileType.JPEG);
                break;
            case GIF:
                image = loadImageUsingToolkit(file);
                fileInfo = getFileInfo(image, IMAGE_GIF, FileTypeDetector.FileType.GIF);
                break;
            case PNG:
                image = loadImageUsingToolkit(file);
                fileInfo = getFileInfo(image, IMAGE_PNG, FileTypeDetector.FileType.PNG);
                break;
            case TIFF:
                {
                    final TiffResult tiffResult = readTiff(file);
                    image = tiffResult.image();
                    fileInfo = getFileInfo(image, IMAGE_TIFF, FileTypeDetector.FileType.TIFF);
                    fileInfo.setCompressionFormat(tiffResult.compressionFormat());
                }
                break;
            default:
                LOGGER.warn("ImageOpener|openImage (using File): {} -> unknown fileType = {}", file, fileType);
                return null;
        }
        return new ImagePlusInfo(image, fileInfo);
    }

    static ImagePlusInfo openImage(Path path, FileTypeDetector.FileType fileType) throws IOException {
        BufferedImage image;
        FileInfo fileInfo;
        switch (fileType) {
            case JPEG:
                image = loadImageUsingToolkit(path);
                fileInfo = getFileInfo(image, IMAGE_JPEG, FileTypeDetector.FileType.JPEG);
                break;
            case GIF:
                image = loadImageUsingToolkit(path);
                fileInfo = getFileInfo(image, IMAGE_GIF, FileTypeDetector.FileType.GIF);
                break;
            case PNG:
                image = loadImageUsingToolkit(path);
                fileInfo = getFileInfo(image, IMAGE_PNG, FileTypeDetector.FileType.PNG);
                break;
            case TIFF:
                {
                    final TiffResult tiffResult = readTiff(path);
                    image = tiffResult.image();
                    fileInfo = getFileInfo(image, IMAGE_TIFF, FileTypeDetector.FileType.TIFF);
                    fileInfo.setCompressionFormat(tiffResult.compressionFormat());
                }
                break;
            default:
                LOGGER.warn("ImageOpener|openImage (using Path): {} -> unknown fileType = {}", path, fileType);
                return null;
        }
        return new ImagePlusInfo(image, fileInfo);
    }

    /**
     * Get Image (PNG, GIF or JPEG) using default toolkit.
     * @param imageFile the input image file
     * @return the AWT image object
     */
    private static Image getImageUsingToolkit(File imageFile) {
        return Toolkit.getDefaultToolkit().getImage(imageFile.getAbsolutePath());
    }

    /**
     * Get Image (PNG, GIF or JPEG) using default toolkit.
     * @param imagePath the input image path
     * @return the AWT image object
     */
    private static Image getImageUsingToolkit(Path imagePath) {
        return Toolkit.getDefaultToolkit().getImage(imagePath.toAbsolutePath().toString());
    }

    /**
     * Load PNG, GIF or JPEG using default toolkit.
     * @param imageFile the input image file
     * @return the loaded buffered image object
     */
    private static BufferedImage loadImageUsingToolkit(File imageFile) {
        final Image image = getImageUsingToolkit(imageFile);
        return buildBufferedImage(image, imageFile.getAbsolutePath());
    }

    /**
     * Load PNG, GIF or JPEG using default toolkit.
     * @param imagePath the input image path
     * @return the loaded buffered image object
     */
    private static BufferedImage loadImageUsingToolkit(Path imagePath) {
        final Image image = getImageUsingToolkit(imagePath);
        return buildBufferedImage(image, imagePath.toString());
    }


    private static BufferedImage buildBufferedImage(Image image, String filePath) {
        final MediaTracker mediaTracker = new MediaTracker(new Container());
        mediaTracker.addImage(image, 0);
        try {
            mediaTracker.waitForID(0);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Loading \"" + filePath + "\" interrupted: " + e.getMessage());
        }

        final int width = image.getWidth(imageObserver);
        final int height = image.getHeight(imageObserver);

        // draw image to gui object
        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, width, height, imageObserver);
        return bufferedImage;
    }

    /**
     * Result of reading a TIFF file.
     * @param image the decoded image as RGB image
     * @param compressionFormat one of the FileInfo.COMPRESSION_FORMAT_* constants
     */
    private record TiffResult(BufferedImage image, int compressionFormat) {
    }

    private static TiffResult readTiff(File file) throws IOException {
        try (ImageInputStream inputStream = ImageIO.createImageInputStream(file)) {
            return readTiff(inputStream, file.toString());
        }
    }

    private static TiffResult readTiff(Path path) throws IOException {
        try (ImageInputStream inputStream = ImageIO.createImageInputStream(path.toFile())) {
            return readTiff(inputStream, path.toString());
        }
    }

    /**
     * Decode the first page of a TIFF using the TIFF reader of the JDK (ImageIO).
     * The result is always converted to RGB, because later scaling expects it.
     */
    private static TiffResult readTiff(ImageInputStream inputStream, String name) throws IOException {
        if (inputStream == null) {
            throw new IOException("Cannot open TIFF \"" + name + "\"");
        }
        final Iterator<ImageReader> readers = ImageIO.getImageReaders(inputStream);
        if (!readers.hasNext()) {
            throw new IOException("No TIFF reader available for \"" + name + "\"");
        }
        final ImageReader reader = readers.next();
        try {
            reader.setInput(inputStream, true, false);
            final BufferedImage source = reader.read(0);
            final int compressionFormat = getTiffCompressionFormat(reader);
            final BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
            final Graphics2D graphics2D = image.createGraphics();
            try {
                // white background for images with transparency
                graphics2D.setColor(Color.WHITE);
                graphics2D.fillRect(0, 0, image.getWidth(), image.getHeight());
                graphics2D.drawImage(source, 0, 0, null);
            } finally {
                graphics2D.dispose();
            }
            return new TiffResult(image, compressionFormat);
        } finally {
            reader.dispose();
        }
    }

    private static int getTiffCompressionFormat(ImageReader reader) {
        try {
            final IIOMetadata metadata = reader.getImageMetadata(0);
            final Node root = metadata.getAsTree("javax_imageio_1.0");
            final NodeList nodes = ((org.w3c.dom.Element) root).getElementsByTagName("CompressionTypeName");
            if (nodes.getLength() == 0) {
                return FileInfo.COMPRESSION_FORMAT_UNKNOWN;
            }
            final String name = ((org.w3c.dom.Element) nodes.item(0)).getAttribute("value");
            return switch (name) {
                case "LZW" -> FileInfo.COMPRESSION_FORMAT_LZW;
                case "ZLib", "Deflate" -> FileInfo.COMPRESSION_FORMAT_LZ77;
                case "JPEG", "Old JPEG" -> FileInfo.COMPRESSION_FORMAT_JPEG;
                case "PackBits", "CCITT RLE" -> FileInfo.COMPRESSION_FORMAT_RLE;
                case "CCITT T.4" -> FileInfo.COMPRESSION_FORMAT_G3;
                case "CCITT T.6" -> FileInfo.COMPRESSION_FORMAT_G4;
                case "None" -> FileInfo.COMPRESSION_FORMAT_NONE;
                default -> FileInfo.COMPRESSION_FORMAT_UNKNOWN;
            };
        } catch (IOException | RuntimeException e) {
            return FileInfo.COMPRESSION_FORMAT_UNKNOWN;
        }
    }

    private static FileInfo getFileInfo(BufferedImage bufferedImage, String mimeType, FileTypeDetector.FileType providerType) {
        final int infoBits = bufferedImage.getColorModel().getPixelSize();
        final int infoWidth = bufferedImage.getWidth();
        final int infoHeight = bufferedImage.getHeight();
        final FileInfo fileInfo = new FileInfo();
        fileInfo.setMimeType(mimeType);
        fileInfo.setProviderFormat(providerType.toString());
        fileInfo.setBitsPerPixel(infoBits);
        fileInfo.setWidth(infoWidth);
        fileInfo.setHeight(infoHeight);
        switch (providerType) {
            case GIF:
                fileInfo.setBitsPerPixel(8);
                break;
            case PNG:
                fileInfo.setCompressionFormat(FileInfo.COMPRESSION_FORMAT_LZ77);
                break;
            case JPEG:
                fileInfo.setCompressionFormat(FileInfo.COMPRESSION_FORMAT_JPEG);
                break;
            case TIFF:
                break;
            default:
                throw new IllegalArgumentException("Invalid image type " + providerType);
        }
        return fileInfo;
    }
}