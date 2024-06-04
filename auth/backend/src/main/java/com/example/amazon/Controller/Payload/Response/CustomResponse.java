package com.example.amazon.Controller.Payload.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomResponse {
    private String message;

    @JsonProperty("status")
    private String status;

    @JsonProperty("time_stamp")
    private String timeStamp = new SimpleDateFormat("hh.mm:ssa dd-MM-yyyy")
            .format(new Date())
            .toUpperCase();

    @JsonProperty("data")
    private Object responseObject;

    public CustomResponse(String message, HttpStatus status, Object responseObject) {
        this.message = message;
        this.status = status.value() + " " + status.getReasonPhrase();
        this.responseObject = responseObject;
    }
}
