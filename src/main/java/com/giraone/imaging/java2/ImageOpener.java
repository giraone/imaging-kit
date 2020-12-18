package com.giraone.imaging.java2;

//--------------------------------------------------------------------------------

import com.giraone.imaging.FileInfo;
import com.giraone.imaging.FileTypeDetector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

//--------------------------------------------------------------------------------

public class ImageOpener {

    private static final Logger LOGGER = LogManager.getLogger(ImageOpener.class);
    private static final ImageObserver imageObserver = LoggerImageObserver.getInstance();

    // Hide constructor
    private ImageOpener() {
    }

    // --------------------------------------------------------------------------------

    /**
     * Open an image file an return the buffered image plus some image information.
     * @param file the image file to open
     * @return the image and information tupel
     * @throws IOException on any error opening the image file
     */
    public static ImagePlusInfo openImage(File file) throws IOException {
        FileTypeDetector.FileType fileType = FileTypeDetector.getInstance().getFileType(file);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("openImage: fileType = {}", fileType);
        }

        return openImage(file, fileType);
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

    /**
     * Get Image (PNG, GIF or JPEG) using default toolkit.
     *
     * @param imageFile the input image file
     * @return the AWT image object
     */
    private static Image getImageUsingToolkit(File imageFile) {
        return Toolkit.getDefaultToolkit().getImage(imageFile.getAbsolutePath());
    }

    /**
     * Load PNG, GIF or JPEG using default toolkit.
     *
     * @param imageFile the input image file
     * @return the loaded buffered image object
     */
    private static BufferedImage loadImageUsingToolkit(File imageFile) {
        Image image = getImageUsingToolkit(imageFile);
        MediaTracker mediaTracker = new MediaTracker(new Container());
        mediaTracker.addImage(image, 0);
        try {
            mediaTracker.waitForID(0);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Loading " + imageFile + " interrupted: " + e.getMessage());
        }

        int width = image.getWidth(imageObserver);
        int height = image.getHeight(imageObserver);

        // draw image to gui object
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, width, height, imageObserver);

        return bufferedImage;
    }

    private static FileInfo getFileInfo(BufferedImage bufferedImage, String mimeType, FileTypeDetector.FileType providerType) {
        int infoBits = bufferedImage.getColorModel().getPixelSize();
        int infoWidth = bufferedImage.getWidth();
        int infoHeight = bufferedImage.getHeight();
        FileInfo fileInfo = new FileInfo();
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