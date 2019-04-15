package com.giraone.imaging;

import java.awt.*;

/**
 * Definition of an image conversion (command pattern).
 */
public class ConversionCommand {
    private String outputFormat;
    private boolean compression;
    private int quality;
    private Dimension dimension;
    private Float scale;
    private SpeedHint speedHint;

    public ConversionCommand() {
        this.compression = false;
        this.quality = 0;
        this.speedHint = SpeedHint.BALANCED;
    }

    /**
     * Set output format.
     *
     * @param value Format given as MIME type.
     */
    public void setOutputFormat(String value) {
        this.outputFormat = value;
    }

    public String getOutputFormat() {
        return this.outputFormat;
    }

    /**
     * Set output quality, if compression is used.
     *
     * @param value Output quality (compression).
     *              <ul>
     *              <li><tt>0</tt>: Lossless compression.
     *              <li><tt>1</tt>: Lossy compression with best quality.
     *              <li><tt>100</tt>: Lossy compression with worst quality.
     *              </ul>
     */
    public void setQuality(int value) {
        this.quality = value;
    }

    public void setQuality(ConversionCommand.CompressionQuality compressionQuality) {
        int iQuality;
        switch (compressionQuality) {
            case LOSSLESS:
                iQuality = 0;
                break;
            case LOSSY_BEST:
                iQuality = 1;
                break;
            case LOSSY_MEDIUM:
                iQuality = 50;
                break;
            case LOSSY_SPEED:
                iQuality = 100;
                break;
            default:
                iQuality = 50;
        }
        this.setQuality(iQuality);
    }

    public int getQuality() {
        return this.quality;
    }

    public boolean useCompression() {
        return compression;
    }

    /**
     * Define, whether compression is used.
     */
    public void setCompression(boolean compression) {
        this.compression = compression;
    }

    /**
     * Set output dimension
     *
     * @param value Output dimension.
     */
    public void setDimension(Dimension value) {
        this.dimension = value;
    }

    public Dimension getDimension() {
        return this.dimension;
    }

    /**
     * Set scale factor.
     *
     * @param scale Scale factor. 1.0 = keep.
     */
    public void setScale(float scale) {
        this.scale = new Float(scale);
    }

    public void setNoScale() {
        this.scale = null;
    }

    public Float getScale() {
        return scale;
    }

    public SpeedHint getSpeedHint() {
        return speedHint;
    }

    public void setSpeedHint(SpeedHint speedHint) {
        this.speedHint = speedHint;
    }

    public Dimension getDimensionFromScale(int originalWidth, int originalHeight) {
        Float scale = this.getScale();
        if (scale != null) {
            final float scaleF = scale.floatValue();
            if (scaleF != 1.0f) {
                final int newWidth = (int) (originalWidth * scaleF);
                final int newHeight = (int) (originalHeight * scaleF);
                return new Dimension(newWidth, newHeight);
            } else {
                return null;
            }
        } else {
            return this.getDimension();
        }
    }

    public Dimension getDimensionFromLimits(int originalWidth, int originalHeight) {
        if (this.dimension == null)
            return new Dimension(originalWidth, originalHeight);

        int maxWidth = this.dimension.width;
        int maxHeight = this.dimension.height;

        float dW = (float) maxWidth / (float) originalWidth;
        float dH = (float) maxHeight / (float) originalHeight;

        float scaleF = dW < dH ? dW : dH;

        final int newWidth = (int) (originalWidth * scaleF);
        final int newHeight = (int) (originalHeight * scaleF);

        return new Dimension(newWidth, newHeight);
    }

    // ----------------------------------------------------------------------------

    public static enum SpeedHint {
        SPEED, BALANCED, QUALITY, ULTRA_QUALITY;

        private SpeedHint() {
        }
    }

    // ----------------------------------------------------------------------------

    public static enum CompressionQuality {
        LOSSLESS, LOSSY_BEST, LOSSY_MEDIUM, LOSSY_SPEED;

        private CompressionQuality() {
        }
    }
}