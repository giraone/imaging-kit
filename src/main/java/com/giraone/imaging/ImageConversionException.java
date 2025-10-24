package com.giraone.imaging;

import java.io.IOException;

/**
 * Exception thrown when image conversion, scaling, or thumbnail generation fails.
 * This exception wraps lower-level IO or processing errors that occur during
 * image manipulation operations.
 */
public class ImageConversionException extends IOException {

    /**
     * Constructs a new ImageConversionException with the specified detail message.
     *
     * @param message the detail message explaining the conversion failure
     */
    public ImageConversionException(String message) {
        super(message);
    }

    /**
     * Constructs a new ImageConversionException with the specified detail message and cause.
     *
     * @param message the detail message explaining the conversion failure
     * @param cause the underlying cause of the conversion failure
     */
    public ImageConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
