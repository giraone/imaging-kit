package com.giraone.imaging.java2.codecs;

import java.io.IOException;
import java.io.InputStream;

/**
 * Reads raw 8-bit, 16-bit or 32-bit (float or RGB) images from a stream or URL.
 */
public class ImageReader {

    private FileInfoDetails fi;
    private int width, height;
    private long skipCount;
    private int bytesPerPixel, bufferSize, byteCount, nPixels;
    private int eofErrorCount;

    /**
     * Constructs a new ImageReader using a FileInfoDetails object to describe the file to be read.
     */
    public ImageReader(FileInfoDetails fi) {
        this.fi = fi;
        width = fi.width;
        height = fi.height;
        skipCount = fi.longOffset > 0 ? fi.longOffset : fi.offset;
    }

    void eofError() {
        eofErrorCount++;
    }

    byte[] read8bitImage(InputStream in) throws IOException {
        byte[] pixels;
        int totalRead = 0;
        pixels = new byte[nPixels];
        int count, actuallyRead;

        while (totalRead < byteCount) {
            if (totalRead + bufferSize > nPixels)
                count = nPixels - totalRead;
            else
                count = bufferSize;
            actuallyRead = in.read(pixels, totalRead, count);
            if (actuallyRead == -1) {
                eofError();
                break;
            }
            totalRead += actuallyRead;
        }
        return pixels;
    }

    /**
     * Reads a 16-bit image. Signed pixels are converted to unsigned by adding 32768.
     */
    short[] read16bitImage(InputStream in) throws IOException {
        int pixelsRead;
        byte[] buffer = new byte[bufferSize];
        short[] pixels = new short[nPixels];
        int totalRead = 0;
        int base = 0;
        int count;
        int bufferCount;

        while (totalRead < byteCount) {
            if ((totalRead + bufferSize) > byteCount)
                bufferSize = byteCount - totalRead;
            bufferCount = 0;
            while (bufferCount < bufferSize) { // fill the buffer
                count = in.read(buffer, bufferCount, bufferSize - bufferCount);
                if (count == -1) {
                    eofError();
                    if (fi.fileType == FileInfoDetails.GRAY16_SIGNED)
                        for (int i = base; i < pixels.length; i++)
                            pixels[i] = (short) 32768;
                    return pixels;
                }
                bufferCount += count;
            }
            totalRead += bufferSize;
            pixelsRead = bufferSize / bytesPerPixel;
            if (fi.intelByteOrder) {
                if (fi.fileType == FileInfoDetails.GRAY16_SIGNED)
                    for (int i = base, j = 0; i < (base + pixelsRead); i++, j += 2)
                        pixels[i] = (short) ((((buffer[j + 1] & 0xff) << 8) | (buffer[j] & 0xff)) + 32768);
                else
                    for (int i = base, j = 0; i < (base + pixelsRead); i++, j += 2)
                        pixels[i] = (short) (((buffer[j + 1] & 0xff) << 8) | (buffer[j] & 0xff));
            } else {
                if (fi.fileType == FileInfoDetails.GRAY16_SIGNED)
                    for (int i = base, j = 0; i < (base + pixelsRead); i++, j += 2)
                        pixels[i] = (short) ((((buffer[j] & 0xff) << 8) | (buffer[j + 1] & 0xff)) + 32768);
                else
                    for (int i = base, j = 0; i < (base + pixelsRead); i++, j += 2)
                        pixels[i] = (short) (((buffer[j] & 0xff) << 8) | (buffer[j + 1] & 0xff));
            }
            base += pixelsRead;
        }
        return pixels;
    }

    float[] read32bitImage(InputStream in) throws IOException {
        int pixelsRead;
        byte[] buffer = new byte[bufferSize];
        float[] pixels = new float[nPixels];
        int totalRead = 0;
        int base = 0;
        int count;
        int bufferCount;
        int tmp;

        while (totalRead < byteCount) {
            if ((totalRead + bufferSize) > byteCount)
                bufferSize = byteCount - totalRead;
            bufferCount = 0;
            while (bufferCount < bufferSize) { // fill the buffer
                count = in.read(buffer, bufferCount, bufferSize - bufferCount);
                if (count == -1) {
                    eofError();
                    return pixels;
                }
                bufferCount += count;
            }
            totalRead += bufferSize;
            pixelsRead = bufferSize / bytesPerPixel;
            int j = 0;
            if (fi.intelByteOrder)
                for (int i = base; i < (base + pixelsRead); i++) {
                    tmp = (((buffer[j + 3] & 0xff) << 24) | ((buffer[j + 2] & 0xff) << 16)
                            | ((buffer[j + 1] & 0xff) << 8) | (buffer[j] & 0xff));
                    if (fi.fileType == FileInfoDetails.GRAY32_FLOAT)
                        pixels[i] = Float.intBitsToFloat(tmp);
                    else if (fi.fileType == FileInfoDetails.GRAY32_UNSIGNED)
                        pixels[i] = (float) (tmp & 0xffffffffL);
                    else
                        pixels[i] = tmp;
                    j += 4;
                }
            else
                for (int i = base; i < (base + pixelsRead); i++) {
                    tmp = (((buffer[j] & 0xff) << 24) | ((buffer[j + 1] & 0xff) << 16) | ((buffer[j + 2] & 0xff) << 8)
                            | (buffer[j + 3] & 0xff));
                    if (fi.fileType == FileInfoDetails.GRAY32_FLOAT)
                        pixels[i] = Float.intBitsToFloat(tmp);
                    else if (fi.fileType == FileInfoDetails.GRAY32_UNSIGNED)
                        pixels[i] = (float) (tmp & 0xffffffffL);
                    else
                        pixels[i] = tmp;
                    j += 4;
                }
            base += pixelsRead;
        }
        return pixels;
    }

    int[] readChunkyRGB(InputStream in) throws IOException {
        int pixelsRead;
        bufferSize = 24 * width;
        byte[] buffer = new byte[bufferSize];
        int[] pixels = new int[nPixels];
        int totalRead = 0;
        int base = 0;
        int count;
        int bufferCount;
        int r, g, b;

        while (totalRead < byteCount) {
            if ((totalRead + bufferSize) > byteCount)
                bufferSize = byteCount - totalRead;
            bufferCount = 0;
            while (bufferCount < bufferSize) { // fill the buffer
                count = in.read(buffer, bufferCount, bufferSize - bufferCount);
                if (count == -1) {
                    eofError();
                    return pixels;
                }
                bufferCount += count;
            }
            totalRead += bufferSize;
            pixelsRead = bufferSize / bytesPerPixel;
            boolean bgr = fi.fileType == FileInfoDetails.BGR;
            int j = 0;
            for (int i = base; i < (base + pixelsRead); i++) {
                if (bytesPerPixel == 4)
                    j++; // ignore alfa byte
                r = buffer[j++] & 0xff;
                g = buffer[j++] & 0xff;
                b = buffer[j++] & 0xff;
                if (bgr)
                    pixels[i] = 0xff000000 | (b << 16) | (g << 8) | r;
                else
                    pixels[i] = 0xff000000 | (r << 16) | (g << 8) | b;
            }
            base += pixelsRead;
        }
        return pixels;
    }

    int[] readPlanarRGB(InputStream in) throws IOException {
        int planeSize = nPixels; // 1/3 image size
        byte[] buffer = new byte[planeSize];
        int[] pixels = new int[nPixels];
        int r, g, b;

        int bytesRead;
        bytesRead = in.read(buffer, 0, planeSize);
        if (bytesRead == -1) {
            eofError();
            return pixels;
        }

        for (int i = 0; i < planeSize; i++) {
            r = buffer[i] & 0xff;
            pixels[i] = 0xff000000 | (r << 16);
        }

        bytesRead = in.read(buffer, 0, planeSize);
        if (bytesRead == -1) {
            eofError();
            return pixels;
        }

        for (int i = 0; i < planeSize; i++) {
            g = buffer[i] & 0xff;
            pixels[i] |= g << 8;
        }

        bytesRead = in.read(buffer, 0, planeSize);
        if (bytesRead == -1) {
            eofError();
            return pixels;
        }

        for (int i = 0; i < planeSize; i++) {
            b = buffer[i] & 0xff;
            pixels[i] |= b;
        }

        return pixels;
    }

    void skip(InputStream in) throws IOException {
        if (skipCount > 0) {
            long bytesRead = 0;
            int skipAttempts = 0;
            long count;
            while (bytesRead < skipCount) {
                count = in.skip(skipCount - bytesRead);
                skipAttempts++;
                if (count == -1 || skipAttempts > 5)
                    break;
                bytesRead += count;
            }
        }
        byteCount = width * height * bytesPerPixel;
        nPixels = width * height;
        bufferSize = byteCount / 25;
        if (bufferSize < 8192)
            bufferSize = 8192;
        else
            bufferSize = (bufferSize / 8192) * 8192;
    }

    /**
     * Reads the image from the InputStream and returns the pixel array (byte, short, int or float). Returns null if
     * there was an IO exception. Does not close the InputStream.
     */
    public Object readPixels(InputStream in) {
        try {
            switch (fi.fileType) {
                case FileInfoDetails.GRAY8:
                case FileInfoDetails.COLOR8:
                    bytesPerPixel = 1;
                    skip(in);
                    return read8bitImage(in);
                case FileInfoDetails.GRAY16_SIGNED:
                case FileInfoDetails.GRAY16_UNSIGNED:
                    bytesPerPixel = 2;
                    skip(in);
                    return read16bitImage(in);
                case FileInfoDetails.GRAY32_INT:
                case FileInfoDetails.GRAY32_UNSIGNED:
                case FileInfoDetails.GRAY32_FLOAT:
                    bytesPerPixel = 4;
                    skip(in);
                    return read32bitImage(in);
                case FileInfoDetails.RGB:
                case FileInfoDetails.BGR:
                case FileInfoDetails.ARGB:
                    bytesPerPixel = fi.fileType == FileInfoDetails.ARGB ? 4 : 3;
                    skip(in);
                    return readChunkyRGB(in);
                case FileInfoDetails.RGB_PLANAR:
                    bytesPerPixel = 3;
                    skip(in);
                    return readPlanarRGB(in);
                case FileInfoDetails.BITMAP:
                    bytesPerPixel = 1;
                    skip(in);
                    byte[] bitmap = read8bitImage(in);
                    expandBitmap(bitmap);
                    return bitmap;
                default:
                    return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Skips the specified number of bytes, then reads an image and returns the pixel array (byte, short, int or float).
     * Returns null if there was an IO exception. Does not close the InputStream.
     */
    public Object readPixels(InputStream in, long skipCount) {
        this.skipCount = skipCount;
        Object pixels = readPixels(in);
        if (eofErrorCount > 0)
            return null;
        else
            return pixels;
    }

    void expandBitmap(byte[] pixels) {
        int scan = width / 8;
        int pad = width % 8;
        if (pad > 0)
            scan++;
        int len = scan * height;
        byte bitmap[] = new byte[len];
        System.arraycopy(pixels, 0, bitmap, 0, len);
        int value1, value2, offset;
        int n = 0;
        for (int y = 0; y < height; y++) {
            offset = y * scan;
            for (int x = 0; x < scan; x++) {
                if (n + 8 >= width * height)
                    continue;
                value1 = bitmap[offset + x] & 0xff;
                for (int i = 7; i >= 0; i--) {
                    value2 = (value1 & (1 << i)) != 0 ? 255 : 0;
                    pixels[n++] = (byte) value2;
                }
            }
            n -= pad > 0 ? 8 - pad : 0;
        }
    }
}
