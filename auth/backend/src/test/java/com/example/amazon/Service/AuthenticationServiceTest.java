package com.example.amazon.Service;

import com.example.amazon.DTO.AuthUser.SignUpRequest;
import com.example.amazon.Model.AuthUser;
import com.example.amazon.Model.AuthUserTransaction;
import com.example.amazon.Model.RefreshToken;
import com.example.amazon.Util.JwtUtil;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthUserService authUserService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private AuthUserTransactionService authUserTransactionService;
    @Mock
    private DistributedTransactionService distributedTransactionsService;
    @InjectMocks
    private AuthenticationService authenticationService;

    @Nested
    class SignInTest {
        @Test
        public void givenUser_whenSignIn_shouldGenerateTokenCookies() {
            // given
            AuthUser user = AuthUser.builder()
                .id(1L)
                .email("abc@gmail.com")
                .password("123456")
                .build();

            String accessToken = "abc";
            RefreshToken refreshToken = RefreshToken.builder()
                .id(1L)
                .token("def")
                .expiration(Instant.now())
                .build();

            when(jwtUtil.generateJwtToken(anyString(), anyLong())).thenReturn(accessToken);
            when(refreshTokenService.createRefreshToken(anyLong())).thenReturn(refreshToken);

            // when
            List<ResponseCookie> cookies = authenticationService.signIn(user);

            // then
            assertEquals(cookies.size(), 2);
        }
    }

    @Nested
    class SignUpTest {
        @Test
        public void givenSignUpRequest_whenSignUp_shouldReturnNewUser() {
            // given
            SignUpRequest request = SignUpRequest.builder()
                .email("abc@gmail.com")
                .password("123456")
                .build();

            AuthUser user = AuthUser.builder()
                .id(1L)
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

            AuthUserTransaction transaction = AuthUserTransaction.builder()
                .id(1L)
                .user(user)
                .build();

            when(authUserService.createNewUser(any())).thenReturn(user);
            when(authUserTransactionService.createNewTransaction(any(), any())).thenReturn(transaction);

            // when
            AuthUser newUser = authenticationService.signUp(request);

            // then
            assertEquals(newUser, user);
        }
    }

    @Nested
    class SignOutTest {
        @Test
        public void givenUser_whenSignOut_shouldGenerateTokenCookiesWith0MaxAge() {
            // given
            AuthUser user = AuthUser.builder()
                    .id(1L)
                    .email("abc@gmail.com")
                    .password("123456")
                    .build();

            // when
            List<ResponseCookie> cookies = authenticationService.signOut(user);

            // then
            assertEquals(cookies.size(), 2);
            assertTrue(
                cookies.stream().allMatch(
                    cookie -> cookie.getMaxAge() == Duration.ZERO
                )
            );
        }
    }
}
