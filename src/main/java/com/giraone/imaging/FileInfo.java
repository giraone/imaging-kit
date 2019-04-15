package com.giraone.imaging;

/**
 * FileInfo holds information about file formats. It is mainly used as the return value of
 * {@link ImagingFactory#fetchFileInfo}.
 */
public class FileInfo {
    public FileInfo() {
        this.mimeType = "application/octet-stream";
        this.compressionFormat = COMPRESSION_FORMAT_NONE;
    }

    /**
     * Return MIME type.
     */
    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String value) {
        this.mimeType = value;
    }

    /**
     * Return compression format.
     */
    public int getCompressionFormat() {
        return this.compressionFormat;
    }

    public void setCompressionFormat(int value) {
        this.compressionFormat = value;
    }

    /**
     * Return number of bits per pixel.
     */
    public int getBitsPerPixel() {
        return this.bitsPerPixel;
    }

    public void setBitsPerPixel(int value) {
        this.bitsPerPixel = value;
    }

    /**
     * Return width in pixels.
     */
    public int getWidth() {
        return this.width;
    }

    public void setWidth(int value) {
        this.width = value;
    }

    /**
     * Return height in pixels.
     */
    public int getHeight() {
        return this.height;
    }

    public void setHeight(int value) {
        this.height = value;
    }

    /**
     * Return image format as defined by the provider.
     */
    public Object getProviderFormat() {
        return this.providerFormat;
    }

    public void setProviderFormat(Object value) {
        this.providerFormat = value;
    }

    /**
     * Return a summary of the file info
     */
    public String dumpInfo() {
        StringBuffer buf = new StringBuffer();
        buf.append("MimeType=");
        buf.append(this.getMimeType());
        buf.append(";CompressionFormat=");
        buf.append(this.getCompressionFormat());
        buf.append(";Width=");
        buf.append(this.getWidth());
        buf.append(";Height=");
        buf.append(this.getHeight());
        buf.append(";CompressionFormat=");
        buf.append(this.getCompressionFormat());
        buf.append(";BitsPerPixel=");
        buf.append(this.getBitsPerPixel());
        buf.append(";ProviderFormat=");
        buf.append(this.getProviderFormat());
        return buf.toString();
    }

    // ----------------------------------------------------------------------------

    private String mimeType;
    private int compressionFormat;
    private int bitsPerPixel;
    private int width;
    private int height;

    private Object providerFormat;

    // ----------------------------------------------------------------------------

    public static int COMPRESSION_FORMAT_UNKNOWN = -1;
    public static int COMPRESSION_FORMAT_NONE = 0;
    public static int COMPRESSION_FORMAT_RLE = 1;
    public static int COMPRESSION_FORMAT_JPEG = 2;
    public static int COMPRESSION_FORMAT_LZW = 3;
    public static int COMPRESSION_FORMAT_LZ77 = 4;
    public static int COMPRESSION_FORMAT_G3 = 5;
    public static int COMPRESSION_FORMAT_G4 = 6;
}
