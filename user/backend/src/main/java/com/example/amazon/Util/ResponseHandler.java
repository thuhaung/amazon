package com.example.amazon.Util;

import com.example.amazon.Controller.Payload.Response.CustomResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHandler {

    public static ResponseEntity<CustomResponse> generateResponse(
        HttpStatus status,
        String message,
        Object responseObject
    ) {
        CustomResponse response = new CustomResponse(message, status, responseObject);

        return new ResponseEntity<>(
            response,
            status
        );
    }

    public static ResponseEntity<CustomResponse> generateResponse(
        HttpStatus status,
        Object responseObject
    ) {
        CustomResponse response = new CustomResponse(
            "Information retrieved successfully",
            status,
            responseObject
        );

        return new ResponseEntity<>(
            response,
            status
        );
    }

    public static ResponseEntity<CustomResponse> generateResponse(
        HttpStatus status,
        String message
    ) {
        CustomResponse response = new CustomResponse(message, status);

        return new ResponseEntity<>(
            response,
            status
        );
    }

    public static ResponseEntity<CustomResponse> generateResponse(
        HttpStatus status,
        String message,
        HttpHeaders headers,
        Object responseObject
    ) {
        CustomResponse response = new CustomResponse(message, status, responseObject);

        return new ResponseEntity<>(
            response,
            headers,
            status
        );
    }

    public static ResponseEntity<CustomResponse> generateResponse(
        HttpStatus status,
        String message,
        HttpHeaders headers
    ) {
        CustomResponse response = new CustomResponse(message, status);

        return new ResponseEntity<>(
            response,
            headers,
            status
        );
    }
}
