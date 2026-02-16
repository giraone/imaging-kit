package com.giraone.imaging.os;

public class OsCommandResult {
    int code;
    String output;
    Exception exception;

    public OsCommandResult(int code, String output) {
        super();
        this.code = code;
        this.output = output;
    }

    public OsCommandResult(int code, Exception exception) {
        super();
        this.code = code;
        this.exception = exception;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
