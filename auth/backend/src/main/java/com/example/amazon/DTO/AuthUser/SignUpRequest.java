package com.example.amazon.DTO.AuthUser;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    @NotEmpty(message="Email cannot be empty.")
    @Size(max=256, message="Email must not be longer than {max} characters.")
    private String email;

    @NotEmpty(message="Password cannot be empty.")
    @Size(min=5, message="Password must be longer than {min} characters.")
    @JsonProperty(access= WRITE_ONLY)
    private String password;
}
