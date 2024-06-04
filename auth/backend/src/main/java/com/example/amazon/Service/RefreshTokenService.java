package com.example.amazon.Service;

import com.example.amazon.Model.RefreshToken;
import com.example.amazon.Repository.AuthUserRepository;
import com.example.amazon.Repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {
    private final AuthUserRepository authUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${jwt.expiration.refresh.ms}")
    private int refreshTokenExpiration;

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);

        RefreshToken token = RefreshToken.builder()
                .user(authUserRepository.getReferenceById(userId))
                .token(UUID.randomUUID().toString())
                .expiration(Instant.now().plusMillis(refreshTokenExpiration))
                .build();

        refreshTokenRepository.save(token);

        return token;
    }

    @Transactional(readOnly=true)
    public Instant getTokenExpirationByUserId(Long userId) {
        return refreshTokenRepository.findExpirationByUserId(userId);
    }

    @Transactional
    public void removeTokenByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
