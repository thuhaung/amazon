package com.example.amazon.DTO.AuthUser;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequest {
    @NotEmpty(message="Email cannot be empty.")
    private String email;

    @NotEmpty(message="Password cannot be empty.")
    private String password;
}
