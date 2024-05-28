package com.example.amazon.Exception.Authentication;

public class RefreshTokenException extends RuntimeException {

    public RefreshTokenException() {
    }

    public RefreshTokenException(String message) {
        super(message);
    }

    public RefreshTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public RefreshTokenException(Throwable cause) {
        super(cause);
    }

    public RefreshTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
