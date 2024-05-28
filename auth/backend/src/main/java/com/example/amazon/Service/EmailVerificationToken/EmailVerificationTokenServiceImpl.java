package com.example.amazon.Service.EmailVerificationToken;

import com.example.amazon.DTO.EmailVerificationToken.EmailVerificationTokenProjection;
import com.example.amazon.Exception.Authentication.VerificationTokenException;
import com.example.amazon.Exception.Data.AlreadyVerifiedException;
import com.example.amazon.Model.AuthUser;
import com.example.amazon.Model.EmailVerificationToken;
import com.example.amazon.Repository.EmailVerificationTokenRepository;
import com.example.amazon.Service.AuthUser.AuthUserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationTokenServiceImpl implements EmailVerificationTokenService {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final AuthUserServiceImpl authUserService;

    @Override
    public EmailVerificationTokenProjection getTokenByUserId(Long id) {
        return emailVerificationTokenRepository.findByUserId(id).orElse(null);
    }

    protected String getSaltString() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random random = new Random();

        while (salt.length() < 6) {
            int index = (int) (random.nextFloat() * chars.length());
            salt.append(chars.charAt(index));
        }

        return salt.toString();
    }

    @Override
    @Transactional
    public EmailVerificationToken generateToken(AuthUser user) {
        if (user.isVerified()) {
            throw new AlreadyVerifiedException("User is already verified.");
        }

        EmailVerificationTokenProjection previousToken = getTokenByUserId(user.getId());

        if (previousToken != null) {
            if (!isExpired(previousToken)) {
                throw new VerificationTokenException("Email verification token already exists.");
            }
            else {
                emailVerificationTokenRepository.deleteByToken(previousToken.getToken());
            }
        }

        EmailVerificationToken emailVerificationToken = EmailVerificationToken.builder()
                .user(user)
                .token(getSaltString())
                .expiration(Instant.now().plusMillis(120000))
                .build();

        emailVerificationTokenRepository.save(emailVerificationToken);

        return emailVerificationToken;
    }

    @Override
    @Transactional
    public void verifyEmail(String token, Long id) {
        EmailVerificationTokenProjection userToken = getTokenByUserId(id);

        if (userToken != null) {
            if (!isExpired(userToken)) {
                if (token.equals(userToken.getToken())) {
                    authUserService.verify(id);
                    emailVerificationTokenRepository.deleteByToken(userToken.getToken());
                }
                else {
                    throw new VerificationTokenException("Email verification token is invalid.");
                }
            }
            else {
                emailVerificationTokenRepository.deleteByToken(userToken.getToken());
                throw new VerificationTokenException("Email verification token is expired.");
            }
        }
        else {
            throw new VerificationTokenException("Email verification token does not exist.");
        }
    }

    public boolean isExpired(EmailVerificationTokenProjection token) {
        return token.getExpiration().isBefore(Instant.now());
    }
}
