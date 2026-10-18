package com.giraone.imaging.os;

/**
 * Result of a operating system (OS) command.
 * @param code exit code
 * @param output STDOUT
 * @param exception exception caught when starting/executing the command
 */
public record OsCommandResult(int code, String output, Exception exception) {

    /**
     * Result of a operating system (OS) command, where no exception was caught.
     * @param code exit code
     * @param output STDOUT
     */
    public OsCommandResult(int code, String output) {
        this(code, output, null);
    }

    /**
     * Result of a operating system (OS) command, where an exception was caught.
     * @param code exit code
     * @param exception exception caught when starting/executing the command
     */
    public OsCommandResult(int code, Exception exception) {
        this(code, null, exception);
    }
}
