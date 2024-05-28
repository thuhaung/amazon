package com.example.amazon.Exception;


import com.example.amazon.Controller.Payload.Response.CustomResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResponse extends CustomResponse {
//    private final String message;
//
//    @JsonProperty("status_code")
//    private final String statusCode;
//
//    @JsonProperty("time_stamp")
//    private final String timeStamp = new SimpleDateFormat("hh.mm:ssa dd-MM-yyyy")
//                                        .format(new Date())
//                                        .toUpperCase();

    public ErrorResponse(String message, HttpStatus status) {
        super(message, status, null);
    }

    @JsonProperty("field_errors")
    private List<String> fieldErrors = new ArrayList<>();
}
