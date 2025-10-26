package com.giraone.imaging.java2;

//--------------------------------------------------------------------------------

import com.giraone.imaging.FileInfo;

import java.awt.image.BufferedImage;

//--------------------------------------------------------------------------------

/**
 * Value object for image plus its file information.
 */
public class ImagePlusInfo {

    private BufferedImage image;
    private FileInfo fileInfo;

    /**
     * Constructs a new ImagePlusInfo with the specified image and file information.
     * @param image the buffered image
     * @param fileInfo the file information
     */
    public ImagePlusInfo(BufferedImage image, FileInfo fileInfo) {
        this.image = image;
        this.fileInfo = fileInfo;
    }

    /**
     * Get the buffered image.
     * @return the buffered image
     */
    public BufferedImage getImage() {
        return this.image;
    }

    /**
     * Set the buffered image.
     * @param image the buffered image to set
     */
    public void setImage(BufferedImage image) {
        this.image = image;
    }

    /**
     * Get the file information.
     * @return the file information
     */
    public FileInfo getFileInfo() {
        return this.fileInfo;
    }

    /**
     * Set the file information.
     * @param fileInfo the file information to set
     */
    public void setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

}