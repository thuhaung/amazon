package com.example.amazon.Service;

import com.example.amazon.DTO.AuthUser.SignUpRequest;
import com.example.amazon.DTO.AuthUser.UpdateEmailRequest;
import com.example.amazon.DTO.AuthUser.UpdatePasswordRequest;
import com.example.amazon.Exception.Data.EmailExistsException;
import com.example.amazon.Exception.Data.IncorrectPasswordException;
import com.example.amazon.Exception.Data.ResourceNotFoundException;
import com.example.amazon.Exception.Data.SamePasswordException;
import com.example.amazon.Model.AuthUser;
import com.example.amazon.Repository.AuthUserRepository;
import com.example.amazon.Repository.RoleRepository;
import com.example.amazon.Util.JwtUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class AuthUserServiceTest {
    @Mock
    private AuthUserRepository authUserRepository;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthUserService authUserService;

    @Nested
    class GetUserByIdTest {

        @Test
        public void givenExistentUserId_whenGetUserById_thenReturnUser() {
            // given
            AuthUser expectedUser = AuthUser.builder()
                .id(1L)
                .email("abc@gmail.com")
                .password("123456")
                .build();

            when(authUserRepository.findById(anyLong())).thenReturn(Optional.of(expectedUser));

            // when
            AuthUser resultUser = authUserService.getUserById(1L);

            // then
            assertEquals(expectedUser, resultUser);
        }

        @Test
        public void givenNonExistentUserId_whenGetUserById_thenReturnUser() {
            // given
            when(authUserRepository.findById(anyLong())).thenReturn(Optional.empty());

            // then
            assertThrows(ResourceNotFoundException.class, () -> {
                authUserService.getUserById(anyLong());
            });
        }
    }

    @Nested
    class LoadByUsername {
        @Test
        public void givenExistentEmail_whenLoadByUsername_thenReturnAssociatedUserDetails() {
            // given
            AuthUser user = AuthUser.builder()
                .id(1L)
                .email("abc@gmail.com")
                .password("123456")
                .build();

            when(authUserRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

            // when
            UserDetails userDetails = authUserService.loadUserByUsername(anyString());

            // then
            assertEquals(user, userDetails);
        }

        @Test
        public void givenNonExistentEmail_whenLoadByUsername_thenThrowException() {
            // given
            when(authUserRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            // then
            assertThrows(UsernameNotFoundException.class, () -> authUserService.loadUserByUsername(anyString()));
        }
    }

    @Nested
    class SetUserActiveStatus {
        @Test
        public void givenUserId_whenSetUserActiveStatusToFalse_thenReturnTokenCookieList() {
            // given
            List<ResponseCookie> expectedCookies = Arrays.asList(new ResponseCookie[2]);
            AuthUser user = AuthUser.builder()
                .id(1L)
                .email("abc@gmail.com")
                .password("123456")
                .build();

            when(authUserRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(authenticationService.signOut(any())).thenReturn(expectedCookies);

            // when
            List<ResponseCookie> resultCookies = authUserService.setUserActiveStatus(anyLong(), false);

            // then
            assertEquals(expectedCookies, resultCookies);
        }

        @Test
        public void givenUserId_whenSetUserActiveStatusToTrue_thenReturnEmptyCookieList() {
            // given
            // when
            List<ResponseCookie> resultCookies = authUserService.setUserActiveStatus(1L, true);

            // then
            assertTrue(resultCookies.isEmpty());
        }
    }

    @Nested
    class CreateNewUser {
        @Test
        public void givenRequestWithUniqueEmail_whenSignUp_thenCreateAndReturnNewUser() {
            // given
            SignUpRequest request = SignUpRequest.builder()
                .email("abc@gmail.com")
                .password("123456")
                .build();

            AuthUser expectedUser = AuthUser.builder()
                .id(1L)
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

            when(passwordEncoder.encode(anyString())).thenReturn(request.getPassword());
            when(authUserRepository.save(any(AuthUser.class))).thenReturn(expectedUser);

            // when
            AuthUser resultUser = authUserService.createNewUser(request);

            // then
            assertEquals(expectedUser, resultUser);
        }

        @Test
        public void givenRequestWithExistentEmail_whenSignUp_thenThrowException() {
            // given
            SignUpRequest request = SignUpRequest.builder()
                    .email("abc@gmail.com")
                    .password("123456")
                    .build();

            when(authUserRepository.existsByEmail(anyString())).thenReturn(true);

            // then
            assertThrows(EmailExistsException.class, () -> authUserService.createNewUser(request));
        }
    }

    @Nested
    class UpdatePassword {
        @Test
        public void givenNewPasswordSimilarToCurrentPassword_whenUpdatePassword_thenThrowException() {
            // given
            UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .oldPassword("a")
                .newPassword("a")
                .build();

            when(authUserRepository.getPasswordById(anyLong())).thenReturn("a");
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

            // then
            assertThrows(SamePasswordException.class, () -> authUserService.updatePassword(anyLong(), request));
        }

        @Test
        public void givenIncorrectInputPassword_whenUpdatePassword_thenThrowException() {
            // given
            UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                    .oldPassword("a")
                    .newPassword("b")
                    .build();

            when(authUserRepository.getPasswordById(anyLong())).thenReturn("c");
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            // then
            assertThrows(IncorrectPasswordException.class, () -> authUserService.updatePassword(anyLong(), request));
        }

        @Test
        public void givenCorrectCurrentPasswordAndDifferentNewPassword_whenUpdatePassword_vUpdatePassword() {
            // given
            UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                    .oldPassword("a")
                    .newPassword("b")
                    .build();

            when(authUserRepository.getPasswordById(anyLong())).thenReturn("a");
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(passwordEncoder.encode(anyString())).thenReturn("");

            // when
            authUserService.updatePassword(anyLong(), request);

            // then
            verify(authUserRepository, times(1)).updatePassword(anyLong(), anyString());
        }
    }

    @Nested
    class UpdateEmail {
        private UpdateEmailRequest request;

        @BeforeEach
        public void init() {
            this.request = UpdateEmailRequest.builder()
                .email("abc@gmail.com")
                .build();
        }

        @Test
        public void givenNewEmailSimilarToOldEmail_whenUpdateEmail_thenThrowException() {
            // given
            when(authUserRepository.getEmailById(anyLong())).thenReturn(request.getEmail());

            // then
            assertThrows(EmailExistsException.class, () -> authUserService.updateEmail(anyLong(), request));
        }

        @Test
        public void givenExistentEmailDifferentFromOldEmail_whenUpdateEmail_thenThrowException() {
            // given
            when(authUserRepository.getEmailById(anyLong())).thenReturn("cba@gmail.com");
            when(authUserRepository.existsByEmail(request.getEmail())).thenReturn(true);

            // then
            assertThrows(EmailExistsException.class, () -> authUserService.updateEmail(anyLong(), request));
        }

        @Test
        public void givenUniqueEmailDifferentFromOldEmail_whenUpdateEmail_thenUpdateEmailAndReturnNewAccessTokenCookieInformation() {
            // given
            when(authUserRepository.getEmailById(anyLong())).thenReturn("cba@gmail.com");
            when(authUserRepository.existsByEmail(request.getEmail())).thenReturn(false);

            String accessToken = "abc";
            Duration expiration = Duration.ofMillis(0);

            when(jwtUtil.generateJwtToken(anyString(), anyLong())).thenReturn(accessToken);
            when(refreshTokenService.getTokenExpirationByUserId(anyLong())).thenReturn(Instant.now());

            try (MockedStatic<Duration> durationMock = mockStatic(Duration.class)) {
                durationMock.when(() -> Duration.between(any(), any())).thenReturn(expiration);

                // when
                Map<String, String> accessTokenCookieInformation =  authUserService.updateEmail(anyLong(), request);

                // then
                assertEquals(accessTokenCookieInformation.get("value"), accessToken);
                assertEquals(accessTokenCookieInformation.get("expiration"), expiration.toString());
            }
        }
    }
}
