package com.giraone.imaging.java2.codecs;

import java.awt.*;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;

/**
 * This is an 32-bit RGB image and methods that operate on that image.. Based on the ImageProcessor class from
 * "KickAss Java Programming" by Tonny Espeset (http://www.sn.no/~espeset).
 */
public class ColorProcessor extends ImageProcessor {

    private int[] pixels;
    private int[] snapshotPixels = null;
    private int min = 0, max = 255;

    /**
     * Creates a ColorProcessor from an AWT Image.
     */
    public ColorProcessor(Image img) {
        width = img.getWidth(null);
        height = img.getHeight(null);
        pixels = new int[width * height];
        PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, pixels, 0, width);
        try {
            pg.grabPixels();
        } catch (InterruptedException ignore) {
        }
        createColorModel();
        fgColor = 0xff000000; // black
    }

    /**
     * Creates a blank ColorProcessor of the specified dimensions.
     */
    public ColorProcessor(int width, int height) {
        this(width, height, new int[width * height]);
    }

    /**
     * Creates a ColorProcessor from a pixel array.
     */
    public ColorProcessor(int width, int height, int[] pixels) {
        if (pixels != null && width * height != pixels.length)
            throw new IllegalArgumentException(WRONG_LENGTH);
        this.width = width;
        this.height = height;
        createColorModel();
        fgColor = 0xff000000; // black
        this.pixels = pixels;
    }

    void createColorModel() {
        cm = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);
    }

    public Image createImage() {
        if (source == null) {
            source = new MemoryImageSource(width, height, cm, pixels, 0, width);
            source.setAnimated(true);
            source.setFullBufferUpdates(true);
            img = Toolkit.getDefaultToolkit().createImage(source);
        } else if (newPixels) {
            source.newPixels(pixels, cm, 0, width);
            newPixels = false;
        } else
            source.newPixels();
        return img;
    }

    /**
     * Returns a new, blank ShortProcessor with the specified width and height.
     */
    public ImageProcessor createProcessor(int width, int height) {
        int[] pixels = new int[width * height];
        for (int i = 0; i < width * height; i++)
            pixels[i] = -1;
        return new ColorProcessor(width, height, pixels);
    }

    public Color getColor(int x, int y) {
        int c = pixels[y * width + x];
        int r = (c & 0xff0000) >> 16;
        int g = (c & 0xff00) >> 8;
        int b = c & 0xff;
        return new Color(r, g, b);
    }

    /**
     * Sets the foreground color.
     */
    public void setColor(Color color) {
        fgColor = color.getRGB();
        drawingColor = color;
    }

    /**
     * Sets the default fill/draw value, where value is interpreted as an RGB int.
     */
    public void setValue(double value) {
        fgColor = (int) value;
    }

    /**
     * Returns the smallest displayed pixel value.
     */
    public double getMin() {
        return min;
    }

    /**
     * Returns the largest displayed pixel value.
     */
    public double getMax() {
        return max;
    }

    /**
     * Uses a table look-up to map the pixels in this image from min-max to 0-255.
     */
    public void setMinAndMax(double min, double max) {
        setMinAndMax(min, max, 7);
    }

    public void setMinAndMax(double min, double max, int channels) {
        if (max < min)
            return;
        this.min = (int) min;
        this.max = (int) max;
        int v;
        int[] lut = new int[256];
        for (int i = 0; i < 256; i++) {
            v = i - this.min;
            v = (int) (256.0 * v / (max - min));
            if (v < 0)
                v = 0;
            if (v > 255)
                v = 255;
            lut[i] = v;
        }
        reset();
        if (channels == 7)
            applyTable(lut);
        else
            applyTable(lut, channels);
    }

    public void snapshot() {
        snapshotWidth = width;
        snapshotHeight = height;
        if (snapshotPixels == null || snapshotPixels.length != pixels.length) {
            snapshotPixels = new int[width * height];
        }
        System.arraycopy(pixels, 0, snapshotPixels, 0, width * height);
        newSnapshot = true;
    }

    public void reset() {
        if (snapshotPixels == null) {
            return;
        }
        System.arraycopy(snapshotPixels, 0, pixels, 0, width * height);
        newSnapshot = true;
    }

    public void reset(int[] mask) {
        if (mask == null || snapshotPixels == null || mask.length != roiWidth * roiHeight)
            return;
        for (int y = roiY, my = 0; y < (roiY + roiHeight); y++, my++) {
            int i = y * width + roiX;
            int mi = my * roiWidth;
            for (int x = roiX; x < (roiX + roiWidth); x++) {
                if (mask[mi++] != BLACK)
                    pixels[i] = snapshotPixels[i];
                i++;
            }
        }
    }

    /**
     * Fills pixels that are within roi and part of the mask. Throws an IllegalArgumentException if the mask is null or
     * the size of the mask is not the same as the size of the ROI.
     */
    public void fill(int[] mask) {
        if (mask == null) {
            fill();
            return;
        }
        if (mask.length < roiWidth * roiHeight) {
            throw new IllegalArgumentException();
        }
        for (int y = roiY, my = 0; y < (roiY + roiHeight); y++, my++) {
            int i = y * width + roiX;
            int mi = my * roiWidth;
            for (int x = roiX; x < (roiX + roiWidth); x++) {
                if (mask[mi++] == BLACK)
                    pixels[i] = fgColor;
                i++;
            }
        }
    }

    public Object getPixelsCopy() {
        if (newSnapshot)
            return snapshotPixels;
        else {
            int[] pixels2 = new int[width * height];
            System.arraycopy(pixels, 0, pixels2, 0, width * height);
            return pixels2;
        }
    }

    /**
     * Returns a reference to the snapshot pixel array. Used by the ContrastAdjuster.
     */
    public Object getSnapshotPixels() {
        return snapshotPixels;
    }

    public int getPixel(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height)
            return pixels[y * width + x];
        else
            return 0;
    }

    /**
     * Returns the 3 samples for the pixel at (x,y) in an array of int. Returns zeros if the the coordinates are not in
     * bounds. iArray is an optional preallocated array.
     */
    public int[] getPixel(int x, int y, int[] iArray) {
        if (iArray == null)
            iArray = new int[3];
        int c = getPixel(x, y);
        iArray[0] = (c & 0xff0000) >> 16;
        iArray[1] = (c & 0xff00) >> 8;
        iArray[2] = c & 0xff;
        return iArray;
    }

    /**
     * Sets a pixel in the image using a 3 element (R, G and B) int array of samples.
     */
    public void putPixel(int x, int y, int[] iArray) {
        int r = iArray[0], g = iArray[1], b = iArray[2];
        putPixel(x, y, 0xff000000 + (r << 16) + (g << 8) + b);
    }

    /**
     * Calls getPixelValue(x,y).
     */
    public double getInterpolatedPixel(double x, double y) {
        int ix = (int) (x + 0.5);
        int iy = (int) (y + 0.5);
        if (ix < 0)
            ix = 0;
        if (ix >= width)
            ix = width - 1;
        if (iy < 0)
            iy = 0;
        if (iy >= height)
            iy = height - 1;
        return getPixelValue(ix, iy);
    }

    /**
     * Stores the specified value at (x,y).
     */
    public void putPixel(int x, int y, int value) {
        if (x >= 0 && x < width && y >= 0 && y < height)
            pixels[y * width + x] = value | 0xff000000;
    }

    /**
     * Stores the specified real grayscale value at (x,y). Does nothing if (x,y) is outside the image boundary. The
     * value is clamped to be in the range 0-255.
     */
    public void putPixelValue(int x, int y, double value) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            if (value > 255.0)
                value = 255;
            else if (value < 0.0)
                value = 0.0;
            int gray = (int) (value + 0.5);
            pixels[y * width + x] = 0xff000000 + (gray << 16) + (gray << 8) + gray;

        }
    }

    /**
     * Converts the specified pixel to grayscale (g=r*0.299+g*0.587+b*0.114) and returns it as a float.
     */
    public float getPixelValue(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            int c = pixels[y * width + x];
            int r = (c & 0xff0000) >> 16;
            int g = (c & 0xff00) >> 8;
            int b = c & 0xff;
            return (float) (r * 0.299 + g * 0.587 + b * 0.114);
        } else
            return 0;
    }

    /**
     * Draws a pixel in the current foreground color.
     */
    public void drawPixel(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height)
            pixels[y * width + x] = fgColor;
    }

    /**
     * Returns a reference to the int array containing this image's pixel data.
     */
    public Object getPixels() {
        return pixels;
    }

    public void setPixels(Object pixels) {
        this.pixels = (int[]) pixels;
        resetPixels(pixels);
        snapshotPixels = null;
    }

    /**
     * Returns hue, saturation and brightness in 3 byte arrays.
     */
    public void getHSB(byte[] H, byte[] S, byte[] B) {
        int c, r, g, b;
        float[] hsb = new float[3];
        for (int i = 0; i < width * height; i++) {
            c = pixels[i];
            r = (c & 0xff0000) >> 16;
            g = (c & 0xff00) >> 8;
            b = c & 0xff;
            hsb = Color.RGBtoHSB(r, g, b, hsb);
            H[i] = (byte) ((int) (hsb[0] * 255.0));
            S[i] = (byte) ((int) (hsb[1] * 255.0));
            B[i] = (byte) ((int) (hsb[2] * 255.0));
        }
    }

    /**
     * Returns the red, green and blue planes as 3 byte arrays.
     */
    public void getRGB(byte[] R, byte[] G, byte[] B) {
        int c, r, g, b;
        for (int i = 0; i < width * height; i++) {
            c = pixels[i];
            r = (c & 0xff0000) >> 16;
            g = (c & 0xff00) >> 8;
            b = c & 0xff;
            R[i] = (byte) r;
            G[i] = (byte) g;
            B[i] = (byte) b;
        }
    }

    /**
     * Sets the current pixels from 3 byte arrays (reg, green, blue).
     */
    public void setRGB(byte[] R, byte[] G, byte[] B) {
        for (int i = 0; i < width * height; i++)
            pixels[i] = 0xff000000 | ((R[i] & 0xff) << 16) | ((G[i] & 0xff) << 8) | B[i] & 0xff;
    }

    /**
     * Sets the current pixels from 3 byte arrays (hue, saturation and brightness).
     */
    public void setHSB(byte[] H, byte[] S, byte[] B) {
        float hue, saturation, brightness;
        for (int i = 0; i < width * height; i++) {
            hue = (float) ((H[i] & 0xff) / 255.0);
            saturation = (float) ((S[i] & 0xff) / 255.0);
            brightness = (float) ((B[i] & 0xff) / 255.0);
            pixels[i] = Color.HSBtoRGB(hue, saturation, brightness);
        }
    }

    /* Filters start here */
    public void applyTable(int[] lut) {
        int c, r, g, b;
        for (int y = roiY; y < (roiY + roiHeight); y++) {
            int i = y * width + roiX;
            for (int x = roiX; x < (roiX + roiWidth); x++) {
                c = pixels[i];
                r = lut[(c & 0xff0000) >> 16];
                g = lut[(c & 0xff00) >> 8];
                b = lut[c & 0xff];
                pixels[i] = 0xff000000 + (r << 16) + (g << 8) + b;
                i++;
            }
        }
    }

    public void applyTable(int[] lut, int channels) {
        int c, r = 0, g = 0, b = 0;
        for (int y = roiY; y < (roiY + roiHeight); y++) {
            int i = y * width + roiX;
            for (int x = roiX; x < (roiX + roiWidth); x++) {
                c = pixels[i];
                if (channels == 4) {
                    r = lut[(c & 0xff0000) >> 16];
                    g = (c & 0xff00) >> 8;
                    b = c & 0xff;
                } else if (channels == 2) {
                    r = (c & 0xff0000) >> 16;
                    g = lut[(c & 0xff00) >> 8];
                    b = c & 0xff;
                } else if (channels == 1) {
                    r = (c & 0xff0000) >> 16;
                    g = (c & 0xff00) >> 8;
                    b = lut[c & 0xff];
                } else if ((channels & 6) == 6) {
                    r = lut[(c & 0xff0000) >> 16];
                    g = lut[(c & 0xff00) >> 8];
                    b = c & 0xff;
                } else if ((channels & 5) == 5) {
                    r = lut[(c & 0xff0000) >> 16];
                    g = (c & 0xff00) >> 8;
                    b = lut[c & 0xff];
                } else if ((channels & 3) == 3) {
                    r = (c & 0xff0000) >> 16;
                    g = lut[(c & 0xff00) >> 8];
                    b = lut[c & 0xff];
                }
                pixels[i] = 0xff000000 + (r << 16) + (g << 8) + b;
                i++;
            }
        }
    }

    /**
     * Fills the current rectangular ROI.
     */
    public void fill() {
        for (int y = roiY; y < (roiY + roiHeight); y++) {
            int i = y * width + roiX;
            for (int x = roiX; x < (roiX + roiWidth); x++)
                pixels[i++] = fgColor;

        }
    }
}
