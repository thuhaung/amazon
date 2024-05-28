package com.example.amazon.Exception;

import com.example.amazon.Exception.Authentication.RefreshTokenException;
import com.example.amazon.Exception.Authentication.UnauthenticatedException;
import com.example.amazon.Exception.Authentication.VerificationTokenException;
import com.example.amazon.Exception.Authorization.InvalidTargetException;
import com.example.amazon.Exception.Data.*;
import com.example.amazon.Exception.Data.User.ExpiredBankAccountException;
import com.example.amazon.Exception.Data.User.UserInfoException;
import com.example.amazon.Exception.Kafka.KafkaException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleDefaultException(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "An unexpected error occurred. Please try again later.";

        e.printStackTrace();

        ErrorResponse errorResponse = new ErrorResponse(
            message,
            status
        );

        return new ResponseEntity<ErrorResponse>(
            errorResponse,
            status
        );
    }

    @ExceptionHandler({
        InvalidTargetException.class,
        AccessDeniedException.class
    })
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(Exception e) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        String message = "Access denied.";

        ErrorResponse errorResponse = new ErrorResponse(
            message,
            status
        );

        return new ResponseEntity<ErrorResponse>(
            errorResponse,
            status
        );
    }

    @ExceptionHandler({
            BadCredentialsException.class,
            UnauthenticatedException.class,
            RefreshTokenException.class,
            JwtException.class
    })
    public ResponseEntity<ErrorResponse> handleAuthenticationException(Exception e) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String message = "Unauthorized";

        if (e instanceof BadCredentialsException) {
            message = "Incorrect credentials.";
        }
        else if (e instanceof UnauthenticatedException) {
            message = e.getMessage();
        }
        else if (e instanceof ExpiredJwtException) {
            message = "Access token expired.";
        }

        ErrorResponse errorResponse = new ErrorResponse(
            message,
            status
        );

        return new ResponseEntity<ErrorResponse>(
            errorResponse,
            status
        );
    }

    @ExceptionHandler({
            ResourceNotFoundException.class,
            ExpiredBankAccountException.class,
            UsernameNotFoundException.class,
            VerificationTokenException.class,
            UserInfoException.class,
            HttpMessageNotReadableException.class,
            InternalAuthenticationServiceException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse errorResponse = new ErrorResponse(
            e.getMessage(),
            status
        );

        return new ResponseEntity<ErrorResponse>(
            errorResponse,
            status
        );
    }

    @ExceptionHandler(KafkaException.class)
    public ResponseEntity<ErrorResponse> handleKafkaException(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = new ErrorResponse(
            e.getMessage(),
            status
        );

        return new ResponseEntity<ErrorResponse>(
            errorResponse,
            status
        );
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse errorResponse = new ErrorResponse(
            "Field not valid",
            status
        );

        errorResponse.setFieldErrors(getFieldErrorMessages(e.getBindingResult().getFieldErrors()));

        return new ResponseEntity<ErrorResponse>(
            errorResponse,
            status
        );
    }

    public List<String> getFieldErrorMessages(List<FieldError> fieldErrors) {
        List<String> fieldErrorMessages = new ArrayList<>();

        for (FieldError fieldError : fieldErrors) {
            fieldErrorMessages.add(fieldError.getDefaultMessage());
        }

        return fieldErrorMessages;
    }

//    public String getStatusCode(HttpStatus httpStatus) {
//        return httpStatus.value() + " " + httpStatus.getReasonPhrase();
//    }
}
