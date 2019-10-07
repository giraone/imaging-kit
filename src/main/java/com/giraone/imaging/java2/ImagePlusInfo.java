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

    public ImagePlusInfo(BufferedImage image, FileInfo fileInfo) {
        this.image = image;
        this.fileInfo = fileInfo;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public FileInfo getFileInfo() {
        return this.fileInfo;
    }

    public void setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

}