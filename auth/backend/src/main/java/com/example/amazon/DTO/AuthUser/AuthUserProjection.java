package com.example.amazon.DTO.AuthUser;

import java.util.List;

public interface AuthUserProjection {
    Long getId();
    String getEmail();
    boolean getIsVerified();
    boolean getIsActive();
    List<String> getRoles();
}
