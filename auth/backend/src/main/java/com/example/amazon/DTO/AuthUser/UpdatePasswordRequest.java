package com.example.amazon.DTO.AuthUser;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePasswordRequest {
    @Size(min=5, message="Password must be longer than {min} characters.")
    @NotEmpty(message="New password cannot be empty.")
    @JsonProperty("new_password")
    private String newPassword;

    @NotEmpty(message="Please enter your current password.")
    @JsonProperty("old_password")
    private String oldPassword;
}
