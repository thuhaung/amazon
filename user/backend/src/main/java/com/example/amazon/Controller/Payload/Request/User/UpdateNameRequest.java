package com.example.amazon.Controller.Payload.Request.User;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNameRequest {
    @Size(max=255, message="Name must not be longer than {max} characters.")
    @NotEmpty(message="Name cannot be empty.")
    private String name;
}
