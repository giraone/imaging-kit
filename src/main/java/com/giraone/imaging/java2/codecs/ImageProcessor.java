package com.giraone.imaging.java2.codecs;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;

/**
 * This abstract class is the superclass for classes that process the four data types (byte, short, float and RGB)
 * supported by ImageJ.
 */
public abstract class ImageProcessor extends Object {

    /**
     * Value of pixels included in masks.
     */
    public static final int BLACK = 0xFF000000;

    /**
     * Value returned by getMinThreshold() when thresholding is not enabled.
     */
    public static final double NO_THRESHOLD = -808080.0;

    static public final int RED_LUT = 0, BLACK_AND_WHITE_LUT = 1, NO_LUT_UPDATE = 2, OVER_UNDER_LUT = 3;
    static final int INVERT = 0, FILL = 1, ADD = 2, MULT = 3, AND = 4, OR = 5, XOR = 6, GAMMA = 7, LOG = 8, MINIMUM = 9,
            MAXIMUM = 10, SQR = 11, SQRT = 12;
    static final int BLUR_MORE = 0, FIND_EDGES = 1, MEDIAN_FILTER = 2, MIN = 3, MAX = 4;
    static final double rWeight = 0.299, gWeight = 0.587, bWeight = 0.114;
    static final String WRONG_LENGTH = "(width*height) != pixels.length";

    int fgColor = 0;
    protected int lineWidth = 1;
    protected int cx, cy; // current drawing coordinates
    protected Font font;
    protected FontMetrics fontMetrics;
    protected boolean antialiasedText;
    protected boolean boldFont;

    boolean pixelsModified;
    protected int width, snapshotWidth;
    protected int height, snapshotHeight;
    protected int roiX, roiY, roiWidth, roiHeight;
    protected int xMin, xMax, yMin, yMax;
    boolean newSnapshot = false; // true if pixels = snapshotPixels
    int[] mask = null;
    protected ColorModel baseCM; // base color model
    protected ColorModel cm;
    protected byte[] rLUT1, gLUT1, bLUT1; // base LUT
    protected byte[] rLUT2, gLUT2, bLUT2; // LUT as modified by setMinAndMax and
    // setThreshold
    protected boolean interpolate;
    protected double minThreshold = NO_THRESHOLD, maxThreshold = NO_THRESHOLD;
    protected int histogramSize = 256;
    protected float[] cTable;
    protected boolean lutAnimation;
    protected MemoryImageSource source;
    protected Image img;
    protected boolean newPixels;
    protected Color drawingColor = Color.black;

    /**
     * Returns the width of this image in pixels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of this image in pixels.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns this processor's color model. For non-RGB processors, this is the base lookup table (LUT), not the one
     * that may have been modified by setMinAndMax() or setThreshold().
     */
    public ColorModel getColorModel() {
        if (cm == null)
            makeDefaultColorModel();
        if (baseCM != null)
            return baseCM;
        else
            return cm;
    }

    /**
     * Sets the color model. Must be an IndexColorModel (aka LUT) for all processors except the ColorProcessor.
     */
    public void setColorModel(ColorModel cm) {
        if (!(this instanceof ColorProcessor) && !(cm instanceof IndexColorModel))
            throw new IllegalArgumentException("Must be IndexColorModel");
        this.cm = cm;
        baseCM = null;
        rLUT1 = rLUT2 = null;
        newPixels = true;
        inversionTested = false;
        minThreshold = NO_THRESHOLD;
    }

    protected void makeDefaultColorModel() {
        byte[] rLUT = new byte[256];
        byte[] gLUT = new byte[256];
        byte[] bLUT = new byte[256];
        for (int i = 0; i < 256; i++) {
            rLUT[i] = (byte) i;
            gLUT[i] = (byte) i;
            bLUT[i] = (byte) i;
        }
        cm = new IndexColorModel(8, 256, rLUT, gLUT, bLUT);
    }

    /**
     * Inverts the values in this image's LUT (indexed color model). Does nothing if this is a ColorProcessor.
     */
    public void invertLut() {
        if (cm == null)
            makeDefaultColorModel();
        IndexColorModel icm = (IndexColorModel) cm;
        int mapSize = icm.getMapSize();
        byte[] reds = new byte[mapSize];
        byte[] greens = new byte[mapSize];
        byte[] blues = new byte[mapSize];
        byte[] reds2 = new byte[mapSize];
        byte[] greens2 = new byte[mapSize];
        byte[] blues2 = new byte[mapSize];
        icm.getReds(reds);
        icm.getGreens(greens);
        icm.getBlues(blues);
        for (int i = 0; i < mapSize; i++) {
            reds2[i] = (byte) (reds[mapSize - i - 1] & 255);
            greens2[i] = (byte) (greens[mapSize - i - 1] & 255);
            blues2[i] = (byte) (blues[mapSize - i - 1] & 255);
        }
        cm = new IndexColorModel(8, mapSize, reds2, greens2, blues2);
        newPixels = true;
        baseCM = null;
        rLUT1 = rLUT2 = null;
        inversionTested = false;
    }

    /**
     * Returns the LUT index that's the best match for this color.
     */
    public int getBestIndex(Color c) {
        if (cm == null)
            makeDefaultColorModel();
        IndexColorModel icm = (IndexColorModel) cm;
        int mapSize = icm.getMapSize();
        byte[] rLUT = new byte[mapSize];
        byte[] gLUT = new byte[mapSize];
        byte[] bLUT = new byte[mapSize];
        icm.getReds(rLUT);
        icm.getGreens(gLUT);
        icm.getBlues(bLUT);
        int minDistance = Integer.MAX_VALUE;
        int distance;
        int minIndex = 0;
        int r1 = c.getRed();
        int g1 = c.getGreen();
        int b1 = c.getBlue();
        int r2, b2, g2;
        for (int i = 0; i < mapSize; i++) {
            r2 = rLUT[i] & 0xff;
            g2 = gLUT[i] & 0xff;
            b2 = bLUT[i] & 0xff;
            distance = (r2 - r1) * (r2 - r1) + (g2 - g1) * (g2 - g1) + (b2 - b1) * (b2 - b1);
            // ij.IJ.write(i+" "+minIndex+" "+distance+" "+(rLUT[i]&255));
            if (distance < minDistance) {
                minDistance = distance;
                minIndex = i;
            }
            if (minDistance == 0.0)
                break;
        }
        return minIndex;
    }

    protected boolean inversionTested = false;
    protected boolean invertedLut;

    /**
     * Returns true if this image uses an inverted LUT.
     */
    public boolean isInvertedLut() {
        if (inversionTested)
            return invertedLut;
        inversionTested = true;
        if (cm == null || !(cm instanceof IndexColorModel))
            return (invertedLut = false);
        IndexColorModel icm = (IndexColorModel) cm;
        invertedLut = true;
        int v1, v2;
        for (int i = 1; i < 255; i++) {
            v1 = icm.getRed(i - 1) + icm.getGreen(i - 1) + icm.getBlue(i - 1);
            v2 = icm.getRed(i) + icm.getGreen(i) + icm.getBlue(i);
            if (v1 < v2) {
                invertedLut = false;
                break;
            }
        }
        return invertedLut;
    }

    /**
     * Returns true if this image uses a color LUT.
     */
    public boolean isColorLut() {
        if (cm == null || !(cm instanceof IndexColorModel))
            return false;
        IndexColorModel icm = (IndexColorModel) cm;
        int mapSize = icm.getMapSize();
        byte[] reds = new byte[mapSize];
        byte[] greens = new byte[mapSize];
        byte[] blues = new byte[mapSize];
        icm.getReds(reds);
        icm.getGreens(greens);
        icm.getBlues(blues);
        boolean isColor = false;
        for (int i = 0; i < mapSize; i++) {
            if ((reds[i] != greens[i]) || (greens[i] != blues[i])) {
                isColor = true;
                break;
            }
        }
        return isColor;
    }

    /**
     * Returns true if this image uses a pseudocolor LUT.
     */
    boolean isPseudoColorLut() {
        if (cm == null || !(cm instanceof IndexColorModel))
            return false;
        IndexColorModel icm = (IndexColorModel) cm;
        int mapSize = icm.getMapSize();
        if (mapSize != 256)
            return false;
        byte[] reds = new byte[mapSize];
        byte[] greens = new byte[mapSize];
        byte[] blues = new byte[mapSize];
        icm.getReds(reds);
        icm.getGreens(greens);
        icm.getBlues(blues);
        int r, g, b, d;
        int r2 = reds[0] & 255, g2 = greens[0] & 255, b2 = blues[0] & 255;
        double sum = 0.0, sum2 = 0.0;
        for (int i = 0; i < mapSize; i++) {
            r = reds[i] & 255;
            g = greens[i] & 255;
            b = blues[i] & 255;
            d = r - r2;
            sum += d;
            sum2 += d * d;
            d = g - g2;
            sum += d;
            sum2 += d * d;
            d = b - b2;
            sum += d;
            sum2 += d * d;
            r2 = r;
            g2 = g;
            b2 = b;
        }
        double stdDev = (768 * sum2 - sum * sum) / 768.0;
        if (stdDev > 0.0)
            stdDev = Math.sqrt(stdDev / (767.0));
        else
            stdDev = 0.0;
        return stdDev < 10.0;
    }

    /**
     * Sets the default fill/draw value to the pixel value closest to the specified color.
     */
    public abstract void setColor(Color color);

    /**
     * Obsolete (use setValue)
     */
    public void setColor(int value) {
        fgColor = value;
    }

    /**
     * Sets the default fill/draw value.
     */
    public abstract void setValue(double value);

    /**
     * Returns the smallest displayed pixel value.
     */
    public abstract double getMin();

    /**
     * Returns the largest displayed pixel value.
     */
    public abstract double getMax();

    /**
     * This image will be displayed by mapping pixel values in the range min-max to screen values in the range 0-255.
     * For byte images, this mapping is done by updating the LUT. For short and float images, it's done by generating
     * 8-bit AWT images. For RGB images, it's done by changing the pixel values.
     */
    public abstract void setMinAndMax(double min, double max);

    /**
     * For short and float images, recalculates the min and max image values needed to correctly display the image. For
     * ByteProcessors, resets the LUT.
     */
    public void resetMinAndMax() {
    }

    /**
     * Sets the lower and upper threshold levels. The 'lutUpdate' argument can be RED_LUT, BLACK_AND_WHITE_LUT,
     * OVER_UNDER_LUT or NO_LUT_UPDATE. Thresholding of RGB images is not supported.
     */
    public void setThreshold(double minThreshold, double maxThreshold, int lutUpdate) {
        // ij.IJ.write("setThreshold: "+" "+minThreshold+" "+maxThreshold+"
        // "+lutUpdate);
        if (this instanceof ColorProcessor)
            return;
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;

        if (minThreshold == NO_THRESHOLD) {
            resetThreshold();
            return;
        }

        if (lutUpdate == NO_LUT_UPDATE)
            return;
        if (rLUT1 == null) {
            if (cm == null)
                makeDefaultColorModel();
            baseCM = cm;
            IndexColorModel m = (IndexColorModel) cm;
            rLUT1 = new byte[256];
            gLUT1 = new byte[256];
            bLUT1 = new byte[256];
            m.getReds(rLUT1);
            m.getGreens(gLUT1);
            m.getBlues(bLUT1);
            rLUT2 = new byte[256];
            gLUT2 = new byte[256];
            bLUT2 = new byte[256];
        }
        int t1 = (int) minThreshold;
        int t2 = (int) maxThreshold;
        if (lutUpdate == RED_LUT)
            for (int i = 0; i < 256; i++) {
                if (i >= t1 && i <= t2) {
                    rLUT2[i] = (byte) 255;
                    gLUT2[i] = (byte) 0;
                    bLUT2[i] = (byte) 0;
                } else {
                    rLUT2[i] = rLUT1[i];
                    gLUT2[i] = gLUT1[i];
                    bLUT2[i] = bLUT1[i];
                }
            }
        else if (lutUpdate == BLACK_AND_WHITE_LUT)
            for (int i = 0; i < 256; i++) {
                if (i >= t1 && i <= t2) {
                    rLUT2[i] = (byte) 0;
                    gLUT2[i] = (byte) 0;
                    bLUT2[i] = (byte) 0;
                } else {
                    rLUT2[i] = (byte) 255;
                    gLUT2[i] = (byte) 255;
                    bLUT2[i] = (byte) 255;
                }
            }
        else
            for (int i = 0; i < 256; i++) {
                if (i >= t1 && i <= t2) {
                    rLUT2[i] = rLUT1[i];
                    gLUT2[i] = gLUT1[i];
                    bLUT2[i] = bLUT1[i];

                } else if (i > t2) {
                    rLUT2[i] = (byte) 0;
                    gLUT2[i] = (byte) 255;
                    bLUT2[i] = (byte) 0;
                } else {
                    rLUT2[i] = (byte) 0;
                    gLUT2[i] = (byte) 0;
                    bLUT2[i] = (byte) 255;
                }
            }

        cm = new IndexColorModel(8, 256, rLUT2, gLUT2, bLUT2);
        newPixels = true;
    }

    /**
     * Disables thresholding.
     */
    public void resetThreshold() {
        minThreshold = NO_THRESHOLD;
        if (baseCM != null) {
            cm = baseCM;
            baseCM = null;
        }
        rLUT1 = rLUT2 = null;
        inversionTested = false;
        newPixels = true;
    }

    /**
     * Returns the lower threshold level. Returns NO_THRESHOLD if thresholding is not enabled.
     */
    public double getMinThreshold() {
        return minThreshold;
    }

    /**
     * Returns the upper threshold level.
     */
    public double getMaxThreshold() {
        return maxThreshold;
    }

    /**
     * Sets an int array used as a mask to limit processing to an irregular ROI. The size of the array must be equal to
     * roiWidth*roiHeight. Pixels in the array with a value of BLACK are inside the mask, all other pixels are outside
     * the mask.
     */
    public void setMask(int[] mask) {
        this.mask = mask;
    }

    /**
     * For images with irregular ROIs, returns a mask, otherwise, returns null. Pixels inside the mask have a value of
     * BLACK.
     */
    public int[] getMask() {
        return mask;
    }

    /**
     * Setting 'interpolate' true causes scale(), resize(), rotate() and getLine() to do bilinear interpolation.
     */
    public void setInterpolate(boolean interpolate) {
        this.interpolate = interpolate;
    }

    /**
     * Returns the value of the interpolate field.
     */
    public boolean getInterpolate() {
        return interpolate;
    }

    /**
     * Obsolete.
     */
    public boolean isKillable() {
        return false;
    }

    private void process(int op, double value) {
        double SCALE = 255.0 / Math.log(255.0);
        int v;

        int[] lut = new int[256];
        for (int i = 0; i < 256; i++) {
            switch (op) {
                case INVERT:
                    v = 255 - i;
                    break;
                case FILL:
                    v = fgColor;
                    break;
                case ADD:
                    v = i + (int) value;
                    break;
                case MULT:
                    v = (int) Math.round(i * value);
                    break;
                case AND:
                    v = i & (int) value;
                    break;
                case OR:
                    v = i | (int) value;
                    break;
                case XOR:
                    v = i ^ (int) value;
                    break;
                case GAMMA:
                    v = (int) (Math.exp(Math.log(i / 255.0) * value) * 255.0);
                    break;
                case LOG:
                    if (i == 0)
                        v = 0;
                    else
                        v = (int) (Math.log(i) * SCALE);
                    break;
                case SQR:
                    v = i * i;
                    break;
                case SQRT:
                    v = (int) Math.sqrt(i);
                    break;
                case MINIMUM:
                    if (i < value)
                        v = (int) value;
                    else
                        v = i;
                    break;
                case MAXIMUM:
                    if (i > value)
                        v = (int) value;
                    else
                        v = i;
                    break;
                default:
                    v = i;
            }
            if (v < 0)
                v = 0;
            if (v > 255)
                v = 255;
            lut[i] = v;
        }
        applyTable(lut);
    }

    /**
     * Returns an array containing the pixel values along the line starting at (x1,y1) and ending at (x2,y2). For byte
     * and short images, returns calibrated values if a calibration table has been set using setCalibrationTable().
     *
     * @see ImageProcessor#setInterpolate
     */
    public double[] getLine(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        int n = (int) Math.round(Math.sqrt(dx * dx + dy * dy));
        double xinc = dx / n;
        double yinc = dy / n;
        n++;
        double[] data = new double[n];
        double rx = x1;
        double ry = y1;
        if (interpolate)
            for (int i = 0; i < n; i++) {
                if (cTable != null)
                    data[i] = getInterpolatedValue(rx, ry);
                else
                    data[i] = getInterpolatedPixel(rx, ry);
                rx += xinc;
                ry += yinc;
            }
        else
            for (int i = 0; i < n; i++) {
                data[i] = getPixelValue((int) (rx + 0.5), (int) (ry + 0.5));
                rx += xinc;
                ry += yinc;
            }
        return data;
    }

    /**
     * Returns the pixel values along the horizontal line starting at (x,y).
     */
    public void getRow(int x, int y, int[] data, int length) {
        for (int i = 0; i < length; i++)
            data[i] = getPixel(x++, y);
    }

    /**
     * Returns the pixel values down the column starting at (x,y).
     */
    public void getColumn(int x, int y, int[] data, int length) {
        for (int i = 0; i < length; i++)
            data[i] = getPixel(x, y++);
    }

    /**
     * Inserts the pixels contained in 'data' into a horizontal line starting at (x,y).
     */
    public void putRow(int x, int y, int[] data, int length) {
        for (int i = 0; i < length; i++)
            putPixel(x++, y, data[i]);
    }

    /**
     * Inserts the pixels contained in 'data' into a column starting at (x,y).
     */
    public void putColumn(int x, int y, int[] data, int length) {
        // if (x>=0 && x<width && y>=0 && (y+length)<=height)
        // ((ShortProcessor)this).putColumn2(x, y, data, length);
        // else
        for (int i = 0; i < length; i++)
            putPixel(x, y++, data[i]);
    }

    /**
     * Fills the image or ROI with the current fill/draw value.
     */
    public void fill() {
        process(FILL, 0.0);
    }

    /**
     * Fills pixels that are within the ROI and part of the mask (i.e. pixels that have a value=BLACK in the mask
     * array). Throws and IllegalArgumentException if the mask is null or the size of the mask is not the same as the
     * size of the ROI.
     */
    public abstract void fill(int[] mask);

    /**
     * Set a lookup table used by getPixelValue(), getLine() and convertToFloat() to calibrate pixel values. The length
     * of the table must be 256 for byte images and 65536 for short images. RGB and float processors do not do
     * calibration.
     */
    public void setCalibrationTable(float[] cTable) {
        this.cTable = cTable;
    }

    /**
     * Set the number of bins to be used for float histograms.
     */
    public void setHistogramSize(int size) {
        histogramSize = size;
    }

    /**
     * Returns the number of float image histogram bins. The bin count is fixed at 256 for the other three data types.
     */
    public int getHistogramSize() {
        return histogramSize;
    }

    /**
     * Returns a reference to this image's pixel array. The array type (byte[], short[], float[] or int[]) varies
     * depending on the image type.
     */
    public abstract Object getPixels();

    /**
     * Returns a reference to this image's snapshot (undo) array. If the snapshot array is null, returns a copy of the
     * pixel data. The array type varies depending on the image type.
     */
    public abstract Object getPixelsCopy();

    /**
     * Returns the value of the pixel at (x,y). For RGB images, the argb values are packed in an int. For float images,
     * the the value must be converted using Float.intBitsToFloat(). Returns zero if either the x or y coodinate is out
     * of range.
     */
    public abstract int getPixel(int x, int y);

    /**
     * Returns the samples for the pixel at (x,y) in an int array. RGB pixels have three samples, all others have one.
     * Returns zeros if the the coordinates are not in bounds. iArray is an optional preallocated array.
     */
    public int[] getPixel(int x, int y, int[] iArray) {
        if (iArray == null)
            iArray = new int[1];
        iArray[0] = getPixel(x, y);
        return iArray;
    }

    /**
     * Sets a pixel in the image using an int array of samples. RGB pixels have three samples, all others have one.
     */
    public void putPixel(int x, int y, int[] iArray) {
        putPixel(x, y, iArray[0]);
    }

    /**
     * Uses bilinear interpolation to find the pixel value at real coordinates (x,y).
     */
    public abstract double getInterpolatedPixel(double x, double y);

    /**
     * For color and float images, this is the same as getInterpolatedPixel().
     */
    public double getInterpolatedValue(double x, double y) {
        return getInterpolatedPixel(x, y);
    }

    /**
     * Stores the specified value at (x,y). For RGB images, the argb values are packed in 'value'. For float images,
     * 'value' is expected to be a float converted to an int using Float.floatToIntBits().
     */
    public abstract void putPixel(int x, int y, int value);

    /**
     * Returns the value of the pixel at (x,y). For byte and short images, returns a calibrated value if a calibration
     * table has been set using setCalibraionTable(). For RGB images, returns the luminance value.
     */
    public abstract float getPixelValue(int x, int y);

    /**
     * Stores the specified value at (x,y).
     */
    public abstract void putPixelValue(int x, int y, double value);

    /**
     * Sets the pixel at (x,y) to the current fill/draw value.
     */
    public abstract void drawPixel(int x, int y);

    /**
     * Sets a new pixel array for the image and resets the snapshot buffer. The length of the array must be equal to
     * width*height.
     */
    public abstract void setPixels(Object pixels);

    /**
     * Transforms the image or ROI using a lookup table. The length of the table must be 256 for byte images and 65536
     * for short images. RGB and float images are not supported.
     */
    public abstract void applyTable(int[] lut);

    /**
     * Returns a copy of this image is the form of an AWT Image.
     */
    public abstract Image createImage();

    /**
     * Returns a new, blank processor with the specified width and height.
     */
    public abstract ImageProcessor createProcessor(int width, int height);

    void resetPixels(Object pixels) {
        if (pixels == null) {
            if (img != null) {
                img.flush();
                img = null;
            }
            source = null;
        }
        newPixels = true;
    }
}
