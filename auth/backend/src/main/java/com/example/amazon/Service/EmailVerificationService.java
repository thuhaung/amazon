package com.example.amazon.Service;

import com.example.amazon.Controller.Payload.Request.EmailVerificationRequest;
import com.example.amazon.DTO.EmailVerificationToken.EmailVerificationTokenProjection;
import com.example.amazon.Exception.Authentication.VerificationTokenException;
import com.example.amazon.Exception.Data.AlreadyVerifiedException;
import com.example.amazon.Exception.Data.ResourceNotFoundException;
import com.example.amazon.Model.AuthUser;
import com.example.amazon.Model.EmailVerificationToken;
import com.example.amazon.Repository.EmailVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailVerificationService {
    private final AuthUserService authUserService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    private String getSaltString() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random random = new Random();

        while (salt.length() < 6) {
            int index = (int) (random.nextFloat() * chars.length());
            salt.append(chars.charAt(index));
        }

        return salt.toString();
    }

    private boolean isExpired(EmailVerificationToken token) {
        return token.getExpiration().isBefore(Instant.now());
    }

    @Transactional
    public String generateToken(AuthUser user) {
        if (user.isVerified()) {
            throw new AlreadyVerifiedException("Email is already verified.");
        }

        Optional<EmailVerificationToken> optionalToken = emailVerificationTokenRepository
                                                            .findByUserId(user.getId());

        if (optionalToken.isPresent()) {
            EmailVerificationToken existingToken = optionalToken.get();

            // token exists and still hasn't expired yet, user cannot send new request to verify
            if (!isExpired(existingToken)) {
                throw new VerificationTokenException("Email verification token has already been sent.");
            }
            else {
                // token already expired, user didn't complete verification process
                emailVerificationTokenRepository.deleteByUserId(user.getId());
            }
        }

        EmailVerificationToken token = EmailVerificationToken.builder()
                .user(user)
                .token(getSaltString())
                .expiration(Instant.now().plusMillis(120000))
                .build();

        token = emailVerificationTokenRepository.save(token);

        log.info(String.format("Email verification token generated for user email %s.", user.getEmail()));

        return token.getToken();
    }

    @Transactional
    public void verifyEmail(Long userId, EmailVerificationRequest request) {
        String token = request.getToken();

        EmailVerificationToken existingToken = emailVerificationTokenRepository.findByUserId(userId).orElseThrow(
            () -> new ResourceNotFoundException("No email verification tokens exist for user.")
        );

        if (existingToken.getToken().equals(token)) {
            authUserService.setUserVerificationStatus(userId, true);
            emailVerificationTokenRepository.deleteByUserId(userId);
        }
        else {
            throw new VerificationTokenException("Invalid or incorrect email verification token.");
        }
    }
}
