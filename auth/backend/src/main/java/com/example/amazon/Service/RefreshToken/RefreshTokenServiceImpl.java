package com.example.amazon.Service.RefreshToken;

import com.example.amazon.Exception.Authentication.RefreshTokenException;
import com.example.amazon.Model.RefreshToken;
import com.example.amazon.Repository.AuthUserRepository;
import com.example.amazon.Repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthUserRepository authUserRepository;
    @Value("${jwt.expiration.refresh.ms}")
    private int refreshTokenExpiration;

    @Override
    public Instant getTokenExpirationByUserId(Long userId) {
        return refreshTokenRepository.findExpirationByUserId(userId).orElseThrow(
            () -> new RefreshTokenException("Refresh token not found for user with id " + userId + ".")
        );
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken token = RefreshToken.builder()
                .user(authUserRepository.getReferenceById(userId))
                .token(UUID.randomUUID().toString())
                .expiration(Instant.now().plusMillis(refreshTokenExpiration))
                .build();

        refreshTokenRepository.save(token);

        return token;
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiration().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);

            throw new RefreshTokenException("Refresh token expired.");
        }

        return token;
    }

    @Override
    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public int deleteByToken(String token) {
        return refreshTokenRepository.deleteByToken(token);
    }
}
