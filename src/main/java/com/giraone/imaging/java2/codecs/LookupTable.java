package com.giraone.imaging.java2.codecs;

//--------------------------------------------------------------------------------

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.PixelGrabber;

//--------------------------------------------------------------------------------

/**
 * This class represents a color look-up table.
 */

public class LookupTable extends Object {
    private int mapSize = 0;
    private ColorModel cm;
    private byte[] rLUT, gLUT, bLUT;

    /**
     * Constructs a LookUpTable object from an AWT Image.
     */
    public LookupTable(Image img) {
        PixelGrabber pg = new PixelGrabber(img, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
            cm = pg.getColorModel();
        } catch (InterruptedException e) {
        }
        ;
        getColors(cm);
    }

    /**
     * Constructs a LookUpTable object from a ColorModel.
     */
    public LookupTable(ColorModel cm) {
        getColors(cm);
    }

    void getColors(ColorModel cm) {
        if (cm instanceof IndexColorModel) {
            IndexColorModel m = (IndexColorModel) cm;
            mapSize = m.getMapSize();
            rLUT = new byte[mapSize];
            gLUT = new byte[mapSize];
            bLUT = new byte[mapSize];
            m.getReds(rLUT);
            m.getGreens(gLUT);
            m.getBlues(bLUT);
        }
    }

    public int getMapSize() {
        return mapSize;
    }

    public byte[] getReds() {
        return rLUT;
    }

    public byte[] getGreens() {
        return gLUT;
    }

    public byte[] getBlues() {
        return bLUT;
    }

    public ColorModel getColorModel() {
        return cm;
    }

    public boolean isGrayscale() {
        boolean isGray = true;

        if (mapSize < 256)
            return false;
        for (int i = 0; i < mapSize; i++)
            if ((rLUT[i] != gLUT[i]) || (gLUT[i] != bLUT[i]))
                isGray = false;
        return isGray;
    }

    public static ColorModel createGrayscaleColorModel(boolean invert) {
        byte[] rLUT = new byte[256];
        byte[] gLUT = new byte[256];
        byte[] bLUT = new byte[256];
        if (invert)
            for (int i = 0; i < 256; i++) {
                rLUT[255 - i] = (byte) i;
                gLUT[255 - i] = (byte) i;
                bLUT[255 - i] = (byte) i;
            }
        else {
            for (int i = 0; i < 256; i++) {
                rLUT[i] = (byte) i;
                gLUT[i] = (byte) i;
                bLUT[i] = (byte) i;
            }
        }
        return (new IndexColorModel(8, 256, rLUT, gLUT, bLUT));
    }
}