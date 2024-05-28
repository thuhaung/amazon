package com.example.amazon.Service.RefreshToken;

import com.example.amazon.Model.RefreshToken;

import java.time.Instant;

public interface RefreshTokenService {
    Instant getTokenExpirationByUserId(Long userId);
    RefreshToken createRefreshToken(Long userId);
    RefreshToken verifyExpiration(RefreshToken token);
    int deleteByUserId(Long userId);
    int deleteByToken(String token);
}
