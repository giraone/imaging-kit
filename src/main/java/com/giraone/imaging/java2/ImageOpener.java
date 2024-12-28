package com.giraone.imaging.java2;

//--------------------------------------------------------------------------------

import com.giraone.imaging.FileInfo;
import com.giraone.imaging.FileTypeDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

//--------------------------------------------------------------------------------

public class ImageOpener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageOpener.class);
    private static final ImageObserver imageObserver = LoggerImageObserver.getInstance();

    // Hide constructor
    private ImageOpener() {
    }

    // --------------------------------------------------------------------------------

    /**
     * Open an image file and return the buffered image plus some image information.
     * @param file the image file to open
     * @return the image and information tupel
     * @throws IOException on any error opening the image file
     */
    public static ImagePlusInfo openImage(File file) throws IOException {
        final FileTypeDetector.FileType fileType = FileTypeDetector.getInstance().getFileType(file);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("openImage: fileType = {}", fileType);
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
            LOGGER.debug("openImage: fileType = {}", fileType);
        }
        return openImage(path, fileType);
    }

    static ImagePlusInfo openImage(File file, FileTypeDetector.FileType fileType) {
        BufferedImage image;
        FileInfo fileInfo;
        switch (fileType) {
            case JPEG:
                image = loadImageUsingToolkit(file);
                fileInfo = getFileInfo(image, "image/jpeg", FileTypeDetector.FileType.JPEG);
                break;
            case GIF:
                image = loadImageUsingToolkit(file);
                fileInfo = getFileInfo(image, "image/gif", FileTypeDetector.FileType.GIF);
                break;
            case PNG:
                image = loadImageUsingToolkit(file);
                fileInfo = getFileInfo(image, "image/png", FileTypeDetector.FileType.PNG);
                break;
            /*
            case TIFF:
                fileInfo = new FileInfo();
                fileInfo.setMimeType("image/tiff");
                fileInfo.setProviderFormat(FileTypeDetector.FileType.TIFF.toString());
                try (FileInputStream in = new FileInputStream(file)) {
                    MemoryImageSource imageSource = Decoder_tiff.getImageSource(in, fileInfo);
                    image = new BufferedImage(fileInfo.getWidth(), fileInfo.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Image image2 = Toolkit.getDefaultToolkit().createImage(imageSource);
                    Graphics g = image.getGraphics();
                    g.drawImage(image2, 0, 0, LoggerImageObserver.getInstance());
                }
                break;
            */
            default:
                LOGGER.warn("ImageOpener|openImage: {} -> unknown fileType = {}", file, fileType);
                return null;
        }
        return new ImagePlusInfo(image, fileInfo);
    }

    static ImagePlusInfo openImage(Path path, FileTypeDetector.FileType fileType) {
        BufferedImage image;
        FileInfo fileInfo;
        switch (fileType) {
            case JPEG:
                image = loadImageUsingToolkit(path);
                fileInfo = getFileInfo(image, "image/jpeg", FileTypeDetector.FileType.JPEG);
                break;
            case GIF:
                image = loadImageUsingToolkit(path);
                fileInfo = getFileInfo(image, "image/gif", FileTypeDetector.FileType.GIF);
                break;
            case PNG:
                image = loadImageUsingToolkit(path);
                fileInfo = getFileInfo(image, "image/png", FileTypeDetector.FileType.PNG);
                break;
            /*
            case TIFF:
                fileInfo = new FileInfo();
                fileInfo.setMimeType("image/tiff");
                fileInfo.setProviderFormat(FileTypeDetector.FileType.TIFF.toString());
                try (FileInputStream in = new FileInputStream(file)) {
                    MemoryImageSource imageSource = Decoder_tiff.getImageSource(in, fileInfo);
                    image = new BufferedImage(fileInfo.getWidth(), fileInfo.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Image image2 = Toolkit.getDefaultToolkit().createImage(imageSource);
                    Graphics g = image.getGraphics();
                    g.drawImage(image2, 0, 0, LoggerImageObserver.getInstance());
                }
                break;
            */
            default:
                LOGGER.warn("ImageOpener|openImage: {} -> unknown fileType = {}", path, fileType);
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
            default:
                throw new IllegalArgumentException("Invalid image type " + providerType);
        }
        return fileInfo;
    }
}