package com.example.amazon.Controller;

import com.example.amazon.Controller.Payload.Request.EmailVerificationRequest;
import com.example.amazon.Controller.Payload.Response.CustomResponse;
import com.example.amazon.DTO.AuthUser.UpdateEmailRequest;
import com.example.amazon.DTO.AuthUser.UpdatePasswordRequest;
import com.example.amazon.Service.AuthUserService;
import com.example.amazon.Service.DistributedTransactionService;
import com.example.amazon.Service.EmailVerificationService;
import com.example.amazon.Util.CookieUtil;
import com.example.amazon.Util.ResponseHandler;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/user")
@Slf4j
@PreAuthorize("@authUtil.hasPermission(#id)")
public class UserController {
    private final AuthUserService authUserService;
    private final EmailVerificationService emailVerificationService;
    private final DistributedTransactionService distributedTransactionService;

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse> getUser(
        @PathVariable("id") Long id
    ) {
        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            authUserService.getUserById(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteAccount(
        @PathVariable("id") Long id
    ) {
        authUserService.deleteUser(id);

        return ResponseHandler.generateResponse(
            HttpStatus.OK
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CustomResponse> deactivateAccount(
        @PathVariable("id") Long id
    ) {
        List<ResponseCookie> cookies = authUserService.setUserActiveStatus(id, false);

        HttpHeaders headers = new HttpHeaders();

        if (!cookies.isEmpty()) {
            headers = CookieUtil.setCookieHeader(cookies);
        }

        return ResponseHandler.generateResponse(
            HttpStatus.ACCEPTED,
            "Account deactivated. Please sign in again to reactivate.",
            headers
        );
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<CustomResponse> updatePassword(
        @PathVariable("id") Long id,
        @Valid @RequestBody UpdatePasswordRequest request
    ) {
        authUserService.updatePassword(id, request);

        return ResponseHandler.generateResponse(
            HttpStatus.OK
        );
    }

    @PatchMapping("/{id}/email")
    public ResponseEntity<CustomResponse> updateEmail(
        @PathVariable("id") Long id,
        @Valid @RequestBody UpdateEmailRequest request
    ) {
        Map<String, String> newToken = authUserService.updateEmail(id, request);

        // update access token cookie with new value
        ResponseCookie newAccessTokenCookie = CookieUtil.createTokenCookie(
            "accessToken",
            newToken.get("value"),
            Duration.parse(newToken.get("expiration"))
        );

        HttpHeaders headers = CookieUtil.setCookieHeader(List.of(newAccessTokenCookie));

        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            headers
        );
    }

    @GetMapping("/{id}/email/verify")
    public ResponseEntity<CustomResponse> sendVerificationEmail(
        @PathVariable("id") Long id
    ) {
        distributedTransactionService.distributeEmailVerificationEvent(id);

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
        emailVerificationService.verifyEmail(id, request);

        return ResponseHandler.generateResponse(
            HttpStatus.OK
        );
    }
}
