package com.example.amazon.Exception.Data;

public class AlreadyVerifiedException extends RuntimeException {
    public AlreadyVerifiedException() {
    }

    public AlreadyVerifiedException(String message) {
        super(message);
    }

    public AlreadyVerifiedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyVerifiedException(Throwable cause) {
        super(cause);
    }

    public AlreadyVerifiedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
