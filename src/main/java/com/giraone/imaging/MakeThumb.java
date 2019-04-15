package com.giraone.imaging;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Simple main program to create thumbnails using an imaging provider.
 */
public class MakeThumb {
    public static void main(String[] args) throws Exception {
        if (args.length != 6) {
            System.err.println("java MakeThumb <provider> <in> <out> <width> <height> <quality>");
            System.err.println("provider: imgscalr|java2");
            System.err.println("quality: 0-100");
            System.err.println(" - 0: Lossless compression");
            System.err.println(" - 1: Lossy compression with best quality");
            System.err.println(" - 100: Lossy compression with worst quality");
            System.exit(1);
        }
        String providerName = args[0];
        String inFile = args[1];
        String outFile = args[2];
        int width = new Integer(args[3]).intValue();
        int height = new Integer(args[4]).intValue();
        int quality = new Integer(args[5]).intValue();

        try {
            @SuppressWarnings("unchecked")
            Class<ImagingProvider> cls = (Class<ImagingProvider>) Class.forName("de.datev.dms.imaging." + providerName + ".Provider");
            ImagingProvider provider = (ImagingProvider) cls.newInstance();
            ConversionCommand command = new ConversionCommand();
            command.setOutputFormat("image/jpeg");
            command.setDimension(new Dimension(width, height));
            command.setQuality(quality);
            try (FileOutputStream outStream = new FileOutputStream(outFile)) {
                provider.convertImage(new File(inFile), outStream, command);
            }
            long inlen = new File(inFile).length();
            long outlen = new File(outFile).length();
            System.err.println("Thumbnail created " + inlen + " Bytes -> " + outlen + " Bytes");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}