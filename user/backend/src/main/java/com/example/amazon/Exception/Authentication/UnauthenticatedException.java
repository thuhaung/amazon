package com.example.amazon.Exception.Authentication;

import org.springframework.security.core.AuthenticationException;

public class UnauthenticatedException extends AuthenticationException {

    public UnauthenticatedException() {
        super("Unauthorized.");
    }

    public UnauthenticatedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UnauthenticatedException(String msg) {
        super(msg);
    }
}
