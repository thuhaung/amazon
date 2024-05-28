package com.example.amazon.Exception.Data.User;

public class ExpiredBankAccountException extends RuntimeException {
    public ExpiredBankAccountException() {
    }

    public ExpiredBankAccountException(String message) {
        super(message);
    }

    public ExpiredBankAccountException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpiredBankAccountException(Throwable cause) {
        super(cause);
    }

    public ExpiredBankAccountException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
