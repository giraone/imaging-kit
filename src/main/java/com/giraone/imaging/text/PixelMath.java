package com.giraone.imaging.text;

/**
 * Pixel and DPI math tools.
 */
public final class PixelMath {

    // Default DPI for screen/image rendering
    public static final int DEFAULT_DPI_SCREEN = 96;
    // Default DPI for print rendering
    public static final int DEFAULT_DPI_PRINT = 300;
    // 1 point (pt) = 1/72 inch
    // 12 × 96/72 = 12×1.33 = 16, 12 pt = 16 pixels (CSS pixels / device pixels at 96 DPI)
    public static final int PT12_PX = ptToPixels(12, DEFAULT_DPI_SCREEN);

    /**
     * Convert millimeters to pixels at a given DPI.
     * @param mm millimeters
     * @param dpi dots per inch
     * @return pixels
     */
    public static int mmToPixels(float mm, int dpi) {
        // 1 inch = 25.4 mm
        return Math.round(mm * dpi / 25.4f);
    }

    /**
     * Convert pixels to millimeters at a given DPI.
     * @param pixel pixels
     * @param dpi dots per inch
     * @return mm
     */
    public static float pixelToMm(int pixel, int dpi) {
        // 1 inch = 25.4 mm
        return Math.round(pixel * 25.4f / dpi);
    }

    /**
     * Convert pixels to millimeters at a given DPI.
     * @param pixel pixels
     * @param dpi dots per inch
     * @return mm
     */
    public static float pixelToMm(float pixel, int dpi) {
        // 1 inch = 25.4 mm
        return Math.round(pixel * 25.4f / dpi);
    }

    /**
     * Convert pixels to millimeters at a given DPI.
     * @param pixel pixels
     * @param dpi dots per inch
     * @return mm
     */
    public static float pixelToMm(double pixel, int dpi) {
        // 1 inch = 25.4 mm
        return Math.round(pixel * 25.4f / dpi);
    }

    /**
     * Convert pt to pixels at a given DPI.
     * @param pt pt (Point)
     * @param dpi dots per inch
     * @return pixels
     */
    public static int ptToPixels(int pt, int dpi) {
        // pt × dpi/72
        return pt * dpi / 72;
    }
}
