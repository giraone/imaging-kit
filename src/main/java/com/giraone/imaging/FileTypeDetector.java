package com.giraone.imaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;

/**
 * A basic file type detection class based on looking for 'magic numbers' or text strings in the file header.
 */
public class FileTypeDetector {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileTypeDetector.class);
    private static final FileTypeDetector _THIS = new FileTypeDetector();

    public static FileTypeDetector getInstance() {
        return _THIS;
    }

    private FileTypeDetector() {
    }

    /**
     * Determine the file type.
     * @param file a File object
     * @return The detected file type or UNKNOWN, if detection fails
     * @throws IOException on errors opening the file
     */
    public FileType getFileType(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            return this.getFileType(is);
        }
    }

    /**
     * Determine the file type.
     * @param path a Path object
     * @return The detected file type or UNKNOWN, if detection fails
     * @throws IOException on errors opening the file
     */
    public FileType getFileType(Path path) throws IOException {
        try (AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ)) {
            final ByteBuffer buffer = ByteBuffer.allocate(256);
            final Future<Integer> operation = fileChannel.read(buffer, 0);
            try {
                operation.get();
            } catch (Exception e) {
                throw new IOException(e);
            }
            final FileType ret = getFileType(buffer.array());
            buffer.clear();
            return ret;
        }
    }

    /**
     * Determine the file type.
     * @param filePath a file path string
     * @return The detected file type or UNKNOWN, if detection fails
     * @throws IOException on errors opening the file
     */
    @SuppressWarnings("unused")
    public FileType getFileType(String filePath) throws IOException {
        try (InputStream is = new FileInputStream(filePath)) {
            return this.getFileType(is);
        }
    }

    /**
     * Determine the file type.
     * @param is an input stream providing the file. The input stream will NOT be closed after processing is done.
     * @return The detected file type or UNKNOWN, if detection fails
     */
    public FileType getFileType(InputStream is) {
        byte[] firstBytes = new byte[132];
        try {
            int r = is.read(firstBytes, 0, 132);
            if (r < 4) return FileType.UNKNOWN;
            is.close();
        } catch (IOException e) {
            LOGGER.error("Error reading input stream", e);
            return FileType.UNKNOWN;
        }
        return getFileType(firstBytes);
    }

    /**
     * Determine the file type.
     * @param firstBytes First 132 bytes of the file to be detected.
     * @return The detected file type or UNKNOWN, if detection fails
     */
    public FileType getFileType(byte[] firstBytes) {

        int b0 = firstBytes[0] & 255;
        int b1 = firstBytes[1] & 255;
        int b2 = firstBytes[2] & 255;
        int b3 = firstBytes[3] & 255;

        // PDF (%PDF-1.X)
        if (b0 == '%' && b1 == 'P' && b2 == 'D' && b3 == 'F')
            return FileType.PDF;

        // PNG
        if (b0 == 137 && b1 == 80 && b2 == 78 && b3 == 71)
            return FileType.PNG;

        // Big-endian TIFF ("MM")
        if (b0 == 73 && b1 == 73 && b2 == 42 && b3 == 0)
            return FileType.TIFF;

        // Little-endian TIFF ("II")
        if (b0 == 77 && b1 == 77 && b2 == 0 && b3 == 42)
            return FileType.TIFF;

        // JPEG
        if (b0 == 255 && b1 == 216 && b2 == 255)
            return FileType.JPEG;

        // GIF ("GIF8")
        if (b0 == 71 && b1 == 73 && b2 == 70 && b3 == 56)
            return FileType.GIF;

        // DICOM ("DICM" at offset 128)
        if (firstBytes[128] == 68 && firstBytes[129] == 73 && firstBytes[130] == 67 && firstBytes[131] == 77) {
            return FileType.DICOM;
        }

        // ACR/NEMA with first tag = 00002,00xx or 00008,00xx
        if ((b0 == 8 || b0 == 2) && b1 == 0 && b3 == 0)
            return FileType.DICOM;

        // PGM ("P2" or "P5")
        if (b0 == 80 && (b1 == 50 || b1 == 53) && (b2 == 10 || b2 == 13 || b2 == 32 || b2 == 9))
            return FileType.PGM;

        // BMP ("BM") - TODO: Not really correct!!! We also need name.endsWith(".bmp")
        if (b0 == 66 && b1 == 77)
            return FileType.BMP;

        return FileType.UNKNOWN;
    }

    public boolean isSupportedImage(FileType fileType) {
        return switch (fileType) {
            case JPEG, PNG -> true;
            default -> false;
        };
    }

    /**
     * Enumeration for the supported (detectable) file types.
     */
    public enum FileType {
        UNKNOWN, JPEG, PNG, TIFF, GIF, BMP, PGM, DICOM, PDF
    }
}
