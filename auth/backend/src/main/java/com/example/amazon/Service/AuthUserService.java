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
import com.example.amazon.Util.DateTimeUtil;
import com.example.amazon.Util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthUserService implements UserDetailsService {
    private final AuthUserRepository authUserRepository;
    @Lazy
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly=true)
    public AuthUser getUserById(Long id) {
        return authUserRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException(String.format(
                "No user can be found with the id %d", id
            ))
        );
    }

    @Transactional
    public List<ResponseCookie> setUserActiveStatus(Long userId, boolean isActive) {
        List<ResponseCookie> cookies = new ArrayList<>();

        if (!isActive) {
            cookies = authenticationService.signOut(getUserById(userId));
        }

        authUserRepository.updateActiveStatusById(userId, isActive, null);

        return cookies;
    }

    /**
     * deactivates user account for 30 days
     * if no sign in attempts are made during the period, account will be removed by scheduler
     * @param userId - identifier of the currently signed in user
     */
    @Transactional
    public void deleteUser(Long userId) {
        Date deletionDate = DateTimeUtil.getDate30DaysFromNow();

        authUserRepository.updateActiveStatusById(
            userId,
            false,
            deletionDate
        );
    }

    @Transactional
    public AuthUser createNewUser(SignUpRequest request) {
        if (authUserRepository.existsByEmail(request.getEmail())) {
            throw new EmailExistsException("An account already exists with this email.");
        }

        AuthUser user = AuthUser.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .isVerified(false)
            .isActive(true)
            .build();

        user.addRole(roleRepository.findByType("User"));

        user = authUserRepository.save(user);
        log.info(String.format("New user created with email %s.", request.getEmail()));

        return user;
    }

    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        String password = authUserRepository.getPasswordById(userId);
        String newPassword = request.getNewPassword();
        String oldPassword = request.getOldPassword();

        // verifies if old password matches current password
        if (passwordEncoder.matches(oldPassword, password)) {
            // verifies if new password matches old password
            if (newPassword.equals(oldPassword)) {
                throw new SamePasswordException("New password cannot be the same as current password.");
            }
            else {
                // new password k có match
                authUserRepository.updatePassword(userId, passwordEncoder.encode(newPassword));
            }
        }
        else {
            // old password given is incorrect
            throw new IncorrectPasswordException("Current password is incorrect.");
        }
    }

    @Transactional
    public void setUserVerificationStatus(Long userId, boolean isVerified) {
        authUserRepository.updateVerificationStatusById(userId, isVerified);
    }

    @Transactional
    public Map<String, String> updateEmail(Long userId, UpdateEmailRequest request) {
        String oldEmail = authUserRepository.getEmailById(userId);
        String newEmail = request.getEmail();

        // update email if new email is different from old
        if (!newEmail.equals(oldEmail)) {
            if (authUserRepository.existsByEmail(newEmail)) {
                throw new EmailExistsException("Email already exists. Please enter a different email.");
            }

            authUserRepository.updateEmail(userId, newEmail);

            setUserVerificationStatus(userId, false);

            // update access token to contain new email
            String accessToken = jwtUtil.generateJwtToken(newEmail, userId);
            log.info("Generated new access token.");

            Instant expiration = refreshTokenService.getTokenExpirationByUserId(userId);
            Duration maxAge = Duration.between(Instant.now(), expiration);

            return Map.ofEntries(
                Map.entry("value", accessToken),
                Map.entry("expiration", maxAge.toString())
            );
        } else {
            throw new EmailExistsException("New email cannot be the same as old email.");
        }
    }

    @Transactional
    public int removeInactiveUsers() {
        // remove users who have deleted their profiles
        return authUserRepository.removeInactiveUsers();
    }

    @Override
    @Transactional(readOnly=true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return authUserRepository.findByEmail(username).orElseThrow(
            () -> {
                log.error(String.format("No user can be found with email %s.", username));
                throw new UsernameNotFoundException("Email or password are incorrect.");
            }
        );
    }
}
