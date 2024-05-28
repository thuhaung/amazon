package com.example.amazon.Exception.Data.User;

public class UserInfoException extends RuntimeException {
    public UserInfoException() {
    }

    public UserInfoException(String message) {
        super(message);
    }

    public UserInfoException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserInfoException(Throwable cause) {
        super(cause);
    }

    public UserInfoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
