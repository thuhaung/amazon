package com.example.amazon.Exception.Authorization;

public class InvalidTargetException extends RuntimeException {
    public InvalidTargetException() {
    }

    public InvalidTargetException(String message) {
        super(message);
    }

    public InvalidTargetException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTargetException(Throwable cause) {
        super(cause);
    }

    public InvalidTargetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
