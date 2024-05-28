package com.example.amazon.Controller;

import com.example.amazon.Controller.Payload.Request.EmailVerificationRequest;
import com.example.amazon.Controller.Payload.Response.CustomResponse;
import com.example.amazon.DTO.AuthUser.UpdateEmailRequest;
import com.example.amazon.DTO.AuthUser.UpdatePasswordRequest;
import com.example.amazon.Exception.Data.EmailExistsException;
import com.example.amazon.Exception.Kafka.KafkaException;
import com.example.amazon.Kafka.Payload.EmailPayload;
import com.example.amazon.Kafka.Producer;
import com.example.amazon.Model.*;
import com.example.amazon.Config.Security.JWT.JwtUtil;
import com.example.amazon.Service.AuthUser.AuthUserServiceImpl;
import com.example.amazon.Service.EmailVerificationToken.EmailVerificationTokenServiceImpl;
import com.example.amazon.Service.RefreshToken.RefreshTokenServiceImpl;
import com.example.amazon.Util.ResponseHandler;
import com.example.amazon.Util.SignOutHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/user")
@Slf4j
@PreAuthorize("@authUtil.hasPermission(#id)")
public class UserController {

    private final JwtUtil jwtUtil;
    private final AuthUserServiceImpl authUserService;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final EmailVerificationTokenServiceImpl emailVerificationTokenService;
    private final Producer producer;

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse> getUser(@PathVariable("id") Long id) {
        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            authUserService.getAuthUserById(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteAccount(
        @PathVariable("id") Long id
    ) {
        authUserService.deleteAuthUser(id);
        HttpHeaders headers = SignOutHandler.signUserOut();

        // remove refresh token from db
        refreshTokenService.deleteByUserId(id);

        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            "Account will be removed in 30 days. Please sign in again to cancel.",
            headers
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CustomResponse> deactivateAccount(
        @PathVariable("id") Long id
    ) {
        authUserService.deactivateAuthUser(id);
        HttpHeaders headers = SignOutHandler.signUserOut();

        // remove refresh token from db
        refreshTokenService.deleteByUserId(id);

        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            "Account deactivated. Please sign in again to reactivate.",
            headers
        );
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<CustomResponse> updatePassword(
        @PathVariable("id") Long id,
        @Valid @RequestBody UpdatePasswordRequest request
    ) {
        authUserService.updatePassword(request, id);

        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            "Password updated successfully."
        );
    }

    @PatchMapping("/{id}/email")
    public ResponseEntity<CustomResponse> updateEmail(
        @PathVariable("id") Long id,
        @Valid @RequestBody UpdateEmailRequest request
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser user = (AuthUser) authentication.getPrincipal();

        String newEmail = request.getEmail();
        String oldEmail = user.getEmail();

        HttpHeaders headers = new HttpHeaders();

        // update access token if email changed
        if (!newEmail.equals(oldEmail)) {
            // persist
            authUserService.updateEmail(request, id);

            String accessToken = jwtUtil.generateJwtToken(newEmail, id);
            log.info("New access token generated, token = " + accessToken);

            // find refresh token
            Instant refreshTokenExpiration = refreshTokenService.getTokenExpirationByUserId(id);
            //RefreshToken refreshToken = refreshTokenService.getTokenByUserId(id);
            log.info("Refresh token exists");

            ResponseCookie accessTokenCookie = ResponseCookie
                    .from("accessToken", accessToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(Math.abs(Instant.now().getEpochSecond() - refreshTokenExpiration.getEpochSecond()))
                    .build();

            headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        }
        else {
            throw new EmailExistsException("New email cannot be the same as old email.");
        }

        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            "Email updated.",
            headers
        );
    }

    @GetMapping("/{id}/email/verify")
    public ResponseEntity<CustomResponse> sendVerificationEmail(
        @PathVariable("id") Long id
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser user = (AuthUser) authentication.getPrincipal();

        EmailVerificationToken token = emailVerificationTokenService.generateToken(user);
        EmailPayload payload = EmailPayload.builder()
                .recipient(user.getEmail())
                .subject("Amazon | Verify your email")
                .text("Your email verification code is " + token.getToken())
                .build();

        producer.sendEmailVerificationToken(payload);

        return ResponseHandler.generateResponse(
            HttpStatus.ACCEPTED,
            "Sending verification token. Please check your email."
        );
    }

    @PostMapping("/{id}/email/verify")
    public ResponseEntity<CustomResponse> verifyEmail(
        @PathVariable("id") Long id,
        @Valid @RequestBody EmailVerificationRequest request
    ) {
        emailVerificationTokenService.verifyEmail(request.getToken(), id);

        return ResponseHandler.generateResponse(
            HttpStatus.OK,
        "Email has been successfully verified."
        );
    }
}
