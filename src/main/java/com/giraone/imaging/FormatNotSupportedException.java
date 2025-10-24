package com.giraone.imaging;

// ---------------------------------------------------------------------------

import java.io.Serial;

/**
 * Exception to indicate an unsupported format
 */
public class FormatNotSupportedException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public FormatNotSupportedException(String message) {
        super(message);
    }
}