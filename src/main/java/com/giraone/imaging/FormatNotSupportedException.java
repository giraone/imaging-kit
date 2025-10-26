package com.giraone.imaging;

// ---------------------------------------------------------------------------

import java.io.Serial;

/**
 * Exception to indicate an unsupported format
 */
public class FormatNotSupportedException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new FormatNotSupportedException with the specified detail message.
     * @param message the detail message explaining which format is not supported
     */
    public FormatNotSupportedException(String message) {
        super(message);
    }
}