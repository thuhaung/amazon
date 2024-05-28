package com.example.amazon.Controller.Payload.Request.User;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMobileRequest {
    @Size(max=30, message="Mobile number must not be longer than {max} characters.")
    @NotEmpty(message="Mobile number cannot be empty.")
    private String mobile;
}
