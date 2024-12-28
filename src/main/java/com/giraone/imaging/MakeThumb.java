package com.giraone.imaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Simple main program to create thumbnails using an imaging provider.
 */
public class MakeThumb {

    private static final Logger LOG = LoggerFactory.getLogger(MakeThumb.class);

    public static void main(String[] args) {

        final String packageName = MakeThumb.class.getPackage().getName();
        final String className = MakeThumb.class.getSimpleName();
        if (args.length != 6) {
            System.err.println("java " + packageName + "." + className + " <in> <out> <width> <height> <quality>");
            System.err.println("quality: 0-100");
            System.err.println(" - 0: Lossless compression");
            System.err.println(" - 1: Lossy compression with best quality");
            System.err.println(" - 100: Lossy compression with worst quality");
            System.err.println("e.g. java " + packageName + "." + className
                + " java2.ProviderJava2D src/test/resources/image-01.jpg thumb-java2D.jpg 80 80 1");
            System.exit(1);
        }
        String providerName = args[0];
        String inFile = args[1];
        String outFile = args[2];
        int width = Integer.parseInt(args[3]);
        int height = Integer.parseInt(args[4]);
        int quality = Integer.parseInt(args[5]);

        try {
            @SuppressWarnings("unchecked")
            Class<ImagingProvider> cls = (Class<ImagingProvider>) Class.forName(
                packageName + "." + providerName);
            ImagingProvider provider = cls.getDeclaredConstructor().newInstance();
            ConversionCommand command = new ConversionCommand();
            command.setOutputFormat("image/jpeg");
            command.setDimension(new Dimension(width, height));
            command.setQuality(quality);
            try (FileOutputStream outStream = new FileOutputStream(outFile)) {
                provider.convertImage(new File(inFile), outStream, command);
            }
            long inLength = new File(inFile).length();
            long outLength = new File(outFile).length();
            System.err.println("Thumbnail created " + inLength + " Bytes -> " + outLength + " Bytes");
        } catch (Exception e) {
            LOG.error("MakeThumb failed", e);
        }
    }
}