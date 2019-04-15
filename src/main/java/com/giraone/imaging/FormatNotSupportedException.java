package com.giraone.imaging;

// ---------------------------------------------------------------------------

/**
 * Exception to indicate an unsupported format
 */
public class FormatNotSupportedException extends Exception {
    private static final long serialVersionUID = 1L;

    public FormatNotSupportedException() {
        super();
    }

    public FormatNotSupportedException(String message) {
        super(message);
    }
}