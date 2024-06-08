package com.example.amazon.Service;

import com.example.amazon.Controller.Payload.Request.EmailVerificationRequest;
import com.example.amazon.Exception.Authentication.VerificationTokenException;
import com.example.amazon.Exception.Data.AlreadyVerifiedException;
import com.example.amazon.Exception.Data.ResourceNotFoundException;
import com.example.amazon.Model.AuthUser;
import com.example.amazon.Model.EmailVerificationToken;
import com.example.amazon.Repository.EmailVerificationTokenRepository;
import com.example.amazon.Util.TokenGenerationUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailVerificationServiceTest {
    @Mock
    private AuthUserService authUserService;
    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;
    @InjectMocks
    private EmailVerificationService emailVerificationService;

    @Nested
    class GenerateToken {
        private AuthUser user;

        @BeforeEach
        public void init() {
            this.user = AuthUser.builder()
                .id(1L)
                .isVerified(false)
                .build();
        }

        @AfterEach
        public void cleanUp() {
            this.user = null;
        }

        @Test
        public void givenVerifiedUser_whenGenerateToken_thenThrowException() {
            // given
            this.user.setVerified(true);

            // then
            assertThrows(AlreadyVerifiedException.class, () -> emailVerificationService.generateToken(user));
        }

        @Test
        public void givenUnverifiedUserAndExistentToken_whenGenerateToken_thenThrowException() {
            // given
            EmailVerificationToken token = EmailVerificationToken.builder()
                .expiration(Instant.now().plusSeconds(20))
                .build();

            when(emailVerificationTokenRepository.findByUserId(anyLong())).thenReturn(Optional.of(token));

            // then
            assertThrows(VerificationTokenException.class, () -> emailVerificationService.generateToken(user));
        }

        @Test
        public void givenUnverifiedUserAndNoTokensExistOrTokenExpired_whenGenerateToken_thenGenerateNewToken() {
            // given
            when(emailVerificationTokenRepository.findByUserId(anyLong())).thenReturn(Optional.empty());
            when(emailVerificationTokenRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

            try (MockedStatic<TokenGenerationUtil> generationMock = mockStatic(TokenGenerationUtil.class)) {
                generationMock.when(() -> TokenGenerationUtil.generateTokenForEmail()).thenReturn("abcdef");

                // when
                String token = emailVerificationService.generateToken(user);

                // then
                assertNotNull(token);
            }
        }
    }

    @Nested
    class VerifyEmail {
        private EmailVerificationRequest request;

        @BeforeEach
        public void init() {
            this.request = EmailVerificationRequest.builder()
                .token("abc")
                .build();
        }

        @Test
        public void givenNonExistentToken_whenVerifyEmail_thenThrowException() {
            // given
            when(emailVerificationTokenRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

            // then
            assertThrows(ResourceNotFoundException.class, () -> emailVerificationService.verifyEmail(anyLong(), request));

        }

        @Test
        public void givenIncorrectToken_whenVerifyEmail_thenThrowsException() {
            // given
            EmailVerificationToken token = EmailVerificationToken.builder()
                .token("cba")
                .build();

            when(emailVerificationTokenRepository.findByUserId(anyLong())).thenReturn(Optional.of(token));

            // then
            assertThrows(VerificationTokenException.class, () -> emailVerificationService.verifyEmail(anyLong(), request));
        }

        @Test
        public void givenExpiredCorrectToken_whenVerifyEmail_thenThrowsException() {
            // given
            EmailVerificationToken token = EmailVerificationToken.builder()
                    .token("abc")
                    .expiration(Instant.now().minusMillis(10))
                    .build();

            when(emailVerificationTokenRepository.findByUserId(anyLong())).thenReturn(Optional.of(token));

            // then
            assertThrows(VerificationTokenException.class, () -> emailVerificationService.verifyEmail(anyLong(), request));
        }

        @Test
        public void givenCorrectAndNonExpiredToken_whenVerifyEmail_thenUpdateUserVerificationStatusAndDeleteToken() {
            // given
            EmailVerificationToken token = EmailVerificationToken.builder()
                    .token("abc")
                    .expiration(Instant.now().plusSeconds(20))
                    .build();

            when(emailVerificationTokenRepository.findByUserId(anyLong())).thenReturn(Optional.of(token));

            // when
            emailVerificationService.verifyEmail(1L, request);

            // then
            verify(authUserService, times(1)).setUserVerificationStatus(1L, true);
            verify(emailVerificationTokenRepository, times(1)).deleteByUserId(1L);
        }
    }
}
