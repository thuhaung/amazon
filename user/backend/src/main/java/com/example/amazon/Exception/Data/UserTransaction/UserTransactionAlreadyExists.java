package com.example.amazon.Exception.Data.UserTransaction;

public class UserTransactionAlreadyExists extends RuntimeException {
    public UserTransactionAlreadyExists() {
    }

    public UserTransactionAlreadyExists(String message) {
        super(message);
    }

    public UserTransactionAlreadyExists(String message, Throwable cause) {
        super(message, cause);
    }

    public UserTransactionAlreadyExists(Throwable cause) {
        super(cause);
    }

    public UserTransactionAlreadyExists(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
