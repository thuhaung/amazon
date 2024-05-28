package com.example.amazon.DTO.AuthUser;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmailRequest {
    @Size(max=256, message="Email must not be longer than {max} characters.")
    @NotEmpty(message="Email cannot be empty.")
    private String email;
}
