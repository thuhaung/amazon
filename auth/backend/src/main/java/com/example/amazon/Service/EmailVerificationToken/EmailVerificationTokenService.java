package com.example.amazon.Service.EmailVerificationToken;

import com.example.amazon.DTO.EmailVerificationToken.EmailVerificationTokenProjection;
import com.example.amazon.Model.AuthUser;
import com.example.amazon.Model.EmailVerificationToken;

public interface EmailVerificationTokenService {
    EmailVerificationTokenProjection getTokenByUserId(Long id);
    EmailVerificationToken generateToken(AuthUser user);
    void verifyEmail(String token, Long id);
}
