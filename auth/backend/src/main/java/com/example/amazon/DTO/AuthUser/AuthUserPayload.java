package com.example.amazon.DTO.AuthUser;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserPayload {
    private Long id;
    private String email;
    @JsonProperty("is_verified")
    private boolean isVerified;
    @JsonProperty("is_active")
    private boolean isActive;
    private List<String> roles;
}
