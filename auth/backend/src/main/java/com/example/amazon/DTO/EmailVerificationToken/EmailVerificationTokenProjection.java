package com.example.amazon.DTO.EmailVerificationToken;

import java.time.Instant;

public interface EmailVerificationTokenProjection {
    String getToken();
    Instant getExpiration();
}
