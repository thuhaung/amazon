package com.example.amazon.Exception.Authentication;

public class VerificationTokenException extends RuntimeException {
    public VerificationTokenException() {
    }

    public VerificationTokenException(String message) {
        super(message);
    }

    public VerificationTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public VerificationTokenException(Throwable cause) {
        super(cause);
    }

    public VerificationTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
