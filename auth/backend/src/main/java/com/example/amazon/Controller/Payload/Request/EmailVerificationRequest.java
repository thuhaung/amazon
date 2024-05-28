package com.example.amazon.Controller.Payload.Request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationRequest {
    @Size(max=6, message="Token is invalid.")
    @NotEmpty(message="Token cannot be empty.")
    private String token;
}
