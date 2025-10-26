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
    private boolean keepAspectRatio;
    private Float scale;

    public ConversionCommand() {
        this.compression = false;
        this.quality = 0;
        this.keepAspectRatio = true;
    }

    /**
     * Set output format.
     * @param value Format given as MIME type.
     */
    public void setOutputFormat(String value) {
        this.outputFormat = value;
    }

    /**
     * Get the output format.
     * @return the output format as MIME type (e.g., "image/jpeg", "image/png")
     */
    public String getOutputFormat() {
        return this.outputFormat;
    }

    /**
     * Set output quality, if compression is used.
     * @param value Output quality (compression).
     *  <ul>
     *   <li><code>0</code>: Lossless compression.
     *   <li><code>1</code>: Lossy compression with best quality.
     *   <li><code>100</code>: Lossy compression with worst quality.
     * </ul>
     */
    public void setQuality(int value) {
        this.quality = value;
    }

    /**
     * Set output quality using a predefined compression quality level.
     * @param compressionQuality the compression quality level
     * @see CompressionQuality
     */
    public void setQuality(ConversionCommand.CompressionQuality compressionQuality) {
        final int iQuality = switch (compressionQuality) {
            case LOSSLESS -> 0;
            case LOSSY_BEST -> 1;
            case LOSSY_SPEED -> 100;
            default -> 50;
        };
        this.setQuality(iQuality);
    }

    /**
     * Get the output quality value.
     * @return quality value (0 = highest quality, 100 = lowest quality/fastest)
     */
    public int getQuality() {
        return this.quality;
    }

    /**
     * Check whether compression is enabled.
     * @return true if compression is enabled, false otherwise
     */
    @SuppressWarnings("unused")
    public boolean useCompression() {
        return compression;
    }

    /**
     * Define, whether compression is used.
     * @param compression true = with compression, false = without compression
     */
    @SuppressWarnings("unused")
    public void setCompression(boolean compression) {
        this.compression = compression;
    }

    /**
     * Set output dimension.
     * If keepAspectRatio is true, the dimension defines the maximum width and height.
     * @param value Output dimension.
     */
    public void setDimension(Dimension value) {
        this.dimension = value;
    }

    /**
     * Get the output dimension (width and height limits).
     * @return the dimension or null if no dimension constraint is set
     */
    public Dimension getDimension() {
        return this.dimension;
    }

    /**
     * Check, whether to keep the aspect ratio.
     * @return true = keep aspect ratio, false = do not keep aspect ratio
     */
    @SuppressWarnings("unused")
    public boolean isKeepAspectRatio() {
        return keepAspectRatio;
    }

    /**
     * Define, whether to keep the aspect ratio.
     * @param keepAspectRatio true = keep aspect ratio, false = do not keep aspect ratio
     */
    public void setKeepAspectRatio(boolean keepAspectRatio) {
        this.keepAspectRatio = keepAspectRatio;
    }

    /**
     * Set scale factor.
     * @param scale Scale factor. 1.0 = keep.
     */
    @SuppressWarnings("unused")
    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * Define to use no scaling.
     */
    @SuppressWarnings("unused")
    public void setNoScale() {
        this.scale = null;
    }

    /**
     * Get the scale factor.
     * @return the scale factor (1.0 = original size, null = use dimension limits instead)
     */
    @SuppressWarnings("unused")
    public Float getScale() {
        return scale;
    }

    /**
     * Return a new dimension that is calculated by the given original width and height and scaled
     * using the scale factor defined by {@link #setScale(float)}.
     * @param originalWidth The width of the original image used for the calculation.
     * @param originalHeight The height of the original image used for the calculation.
     * @return The new dimension that is scaled from the original dimension.
     */
    @SuppressWarnings("unused")
    public Dimension getDimensionFromScale(int originalWidth, int originalHeight) {
        if (scale != null) {
            final float scaleF = scale;
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

    /**
     * Return a new dimension that is calculated by the given original width and height.
     * Depends on the settings of {@link #setKeepAspectRatio(boolean)} and {@link #setDimension(Dimension)}.
     * If keepAspectRatio is true, the returned dimension keeps the original
     * aspect ratio and is with the limits defined by {@link #setDimension(Dimension)}.
     * @param originalWidth The width of the original image used for the calculation.
     * @param originalHeight The height of the original image used for the calculation.
     * @return The new dimension that is scaled from the original dimension.
     */
    public Dimension getTargetDimension(int originalWidth, int originalHeight) {

        if (this.dimension == null) {
            return new Dimension(originalWidth, originalHeight);
        } else if (!this.keepAspectRatio) {
            return new Dimension(this.dimension.width, this.dimension.height);
        } else {
            return getDimensionFromLimits(originalWidth, originalHeight);
        }
    }

    /**
     * Return a new dimension that is calculated by the given original width and height and that keeps the
     * aspect ratio and is with the limits defined by {@link #setDimension(Dimension)}.
     * @param originalWidth The width of the original image used for the calculation.
     * @param originalHeight The height of the original image used for the calculation.
     * @return The new dimension that is scaled from the original dimension.
     */
    public Dimension getDimensionFromLimits(int originalWidth, int originalHeight) {

        if (this.dimension == null) {
            return new Dimension(originalWidth, originalHeight);
        }

        final int maxWidth = this.dimension.width;
        final int maxHeight = this.dimension.height;

        final float dW = (float) maxWidth / (float) originalWidth;
        final float dH = (float) maxHeight / (float) originalHeight;

        final float scaleF = Math.min(dW, dH);

        final int newWidth = (int) (originalWidth * scaleF);
        final int newHeight = (int) (originalHeight * scaleF);

        return new Dimension(newWidth, newHeight);
    }

    /**
     * Helper method to build ConversionCommand
     * @param format image format
     * @param width image width
     * @param height image height
     * @param quality image quality
     * @return newly created ConversionCommand
     */
    public static ConversionCommand buildConversionCommand(String format, int width, int height, ConversionCommand.CompressionQuality quality) {
        final ConversionCommand command = new ConversionCommand();
        command.setOutputFormat(format);
        command.setDimension(new Dimension(width, height));
        final int iQuality = switch (quality) {
            case LOSSLESS -> 0;
            case LOSSY_BEST -> 1;
            case LOSSY_SPEED -> 100;
            default -> 50;
        };
        command.setQuality(iQuality);
        return command;
    }

    // ----------------------------------------------------------------------------

    /**
     * Enumeration for the compression quality defined in 4 steps.
     * <p>
     * <strong>Important Note:</strong> For inherently lossy formats like JPEG, even the LOSSLESS
     * option will produce lossy compression (maximum quality lossy). True lossless compression
     * requires formats like PNG.
     * </p>
     * <ul>
     *   <li>{@link #LOSSLESS} - Maximum quality (note: for JPEG this is still lossy compression)</li>
     *   <li>{@link #LOSSY_BEST} - High quality lossy compression</li>
     *   <li>{@link #LOSSY_MEDIUM} - Medium quality lossy compression (balanced)</li>
     *   <li>{@link #LOSSY_SPEED} - Low quality lossy compression (optimized for speed/size)</li>
     * </ul>
     */
    public enum CompressionQuality {
        /**
         * Maximum quality compression. For JPEG output, this produces the highest quality
         * lossy compression (not truly lossless).
         */
        LOSSLESS,

        /**
         * High quality lossy compression with minimal visible artifacts.
         */
        LOSSY_BEST,

        /**
         * Medium quality lossy compression, balanced between quality and file size.
         */
        LOSSY_MEDIUM,

        /**
         * Low quality lossy compression, optimized for smallest file size and fastest encoding.
         */
        LOSSY_SPEED
    }
}