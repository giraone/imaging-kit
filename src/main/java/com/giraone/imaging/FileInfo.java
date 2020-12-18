package com.giraone.imaging;

import java.io.File;

/**
 * FileInfo holds information about file formats. It is mainly used as the return value of
 * {@link ImagingProvider#fetchFileInfo(File)}}.
 */
public class FileInfo {

    public static final int COMPRESSION_FORMAT_UNKNOWN = -1;
    public static final int COMPRESSION_FORMAT_NONE = 0;
    public static final int COMPRESSION_FORMAT_RLE = 1;
    public static final int COMPRESSION_FORMAT_JPEG = 2;
    public static final int COMPRESSION_FORMAT_LZW = 3;
    public static final int COMPRESSION_FORMAT_LZ77 = 4;
    public static final int COMPRESSION_FORMAT_G3 = 5;
    public static final int COMPRESSION_FORMAT_G4 = 6;

    // ----------------------------------------------------------------------------

    private String mimeType;
    private int compressionFormat;
    private int bitsPerPixel;
    private int width;
    private int height;
    private Object providerFormat;

    // ----------------------------------------------------------------------------

    public FileInfo() {
        this.mimeType = "application/octet-stream";
        this.compressionFormat = COMPRESSION_FORMAT_NONE;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String value) {
        this.mimeType = value;
    }

    public int getCompressionFormat() {
        return this.compressionFormat;
    }

    public void setCompressionFormat(int value) {
        this.compressionFormat = value;
    }

    public int getBitsPerPixel() {
        return this.bitsPerPixel;
    }

    public void setBitsPerPixel(int value) {
        this.bitsPerPixel = value;
    }

    /**
     * Return width of file, if it is an image.
     * @return width in pixels.
     */
    public int getWidth() {
        return this.width;
    }

    public void setWidth(int value) {
        this.width = value;
    }

    /**
     * Return height of file, if it is an image.
     * @return height in pixels.
     */
    public int getHeight() {
        return this.height;
    }

    public void setHeight(int value) {
        this.height = value;
    }

    /**
     * Return image format as defined by the provider.
     * @return image format depending on used provider.
     */
    public Object getProviderFormat() {
        return this.providerFormat;
    }

    public void setProviderFormat(Object value) {
        this.providerFormat = value;
    }

    /**
     * Return a summary of the file info.
     * @return Debug information on information gathered.
     */
    public String dumpInfo() {
        return "MimeType=" +
                this.getMimeType() +
                ";CompressionFormat=" +
                this.getCompressionFormat() +
                ";Width=" +
                this.getWidth() +
                ";Height=" +
                this.getHeight() +
                ";CompressionFormat=" +
                this.getCompressionFormat() +
                ";BitsPerPixel=" +
                this.getBitsPerPixel() +
                ";ProviderFormat=" +
                this.getProviderFormat();
    }
}
