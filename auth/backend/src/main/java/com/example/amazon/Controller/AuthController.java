package com.example.amazon.Controller;

import com.example.amazon.DTO.AuthUser.AuthUserPayload;
import com.example.amazon.DTO.AuthUser.SignUpRequest;
import com.example.amazon.Exception.Authentication.UnauthenticatedException;
import com.example.amazon.Exception.Data.UserCreationException;
import com.example.amazon.Kafka.Payload.UserEvent;
import com.example.amazon.Kafka.Producer;
import com.example.amazon.Model.AuthUser;
import com.example.amazon.Controller.Payload.Response.CustomResponse;
import com.example.amazon.Model.Enum.TransactionStatusEnum;
import com.example.amazon.Model.RefreshToken;
import com.example.amazon.DTO.AuthUser.LoginRequest;
import com.example.amazon.Config.Security.JWT.JwtUtil;
import com.example.amazon.Service.AuthUser.AuthUserServiceImpl;
import com.example.amazon.Service.AuthUserTransaction.AuthUserTransactionServiceImpl;
import com.example.amazon.Service.RefreshToken.RefreshTokenServiceImpl;
import com.example.amazon.Util.ResponseHandler;
import com.example.amazon.Util.SignOutHandler;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@CrossOrigin(
        origins="http://localhost:3000",
        allowCredentials="true",
        allowedHeaders="*"
)
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthUserServiceImpl authUserService;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final AuthUserTransactionServiceImpl authUserTransactionService;
    private final ModelMapper modelMapper;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;
    @Value("${uri.user-service}")
    private String uri;

    @PostMapping("/sign-in")
    public ResponseEntity<CustomResponse> signIn(
        @Valid @RequestBody LoginRequest request
    ) {
        // authenticate
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // set security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AuthUser user = (AuthUser) authentication.getPrincipal();

        // reactivate user if inactive
        if (!user.isActive()) {
            authUserService.reactivateAuthUser(user.getId());
        }

        // remove previous refresh token
        refreshTokenService.deleteByUserId(user.getId());

        // generate new access and refresh tokens
        String accessToken = jwtUtil.generateJwtToken(user.getEmail(), user.getId());
        log.info("New access token generated, token = " + accessToken);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        // set access and refresh token cookies
        ResponseCookie accessTokenCookie = ResponseCookie
                .from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Math.abs(Instant.now().getEpochSecond() - refreshToken.getExpiration().getEpochSecond()))
                .build();
        ResponseCookie refreshTokenCookie = ResponseCookie
                .from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Math.abs(Instant.now().getEpochSecond() - refreshToken.getExpiration().getEpochSecond()))
                .build();

        // set header
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            "Signed in successfully.",
            headers
        );
    }

    @PostMapping("/sign-up")
    public ResponseEntity<CustomResponse> signUp(
        @Valid @RequestBody SignUpRequest request
    ) {
        AuthUserPayload user = authUserService.signUp(request);
        Long userId = user.getId();

        authUserTransactionService.updateAuthUserTransactionStatusByUserId(userId, TransactionStatusEnum.PROCESSING);

        HttpStatusCode statusCode = null;
        int count = 0;

        while ((statusCode == null || statusCode.isError()) && count < 10) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                authUserTransactionService.updateAuthUserTransactionStatusByUserId(userId, TransactionStatusEnum.FAILED);
                authUserService.deleteAuthUserImmediately(userId);

                log.error("Consumer failed to create user.");
                throw new UserCreationException();
            }

            try {
                ResponseEntity<String> result = restTemplate.getForEntity(uri + "/poll/user/" + userId, String.class);
                statusCode = result.getStatusCode();
            } catch (HttpClientErrorException e) {
                statusCode = e.getStatusCode();
            }

            count++;
        }


        if (statusCode == null || statusCode.isError()) {
            authUserTransactionService.updateAuthUserTransactionStatusByUserId(userId, TransactionStatusEnum.FAILED);
            authUserService.deleteAuthUserImmediately(userId);

            log.error("Consumer failed to create user.");
            throw new UserCreationException();
        }
        else {
            authUserTransactionService.updateAuthUserTransactionStatusByUserId(userId, TransactionStatusEnum.DONE);
        }

        return ResponseHandler.generateResponse(
            HttpStatus.CREATED,
            "Signed up successfully.",
            user
        );
    }

    // for front-end authentication purpose due to HttpOnly cookies
    @GetMapping("/status")
    public ResponseEntity<CustomResponse> checkStatus(HttpServletRequest request) {
        String accessToken = null;
        String refreshToken = null;

        // get access token from cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("accessToken")) {
                    accessToken = cookie.getValue();
                    log.debug("Access token exists, token = " + accessToken);

                    // validate access token
                    if (!jwtUtil.validateJwtToken(accessToken)) {
                        log.error("Access token expired.");
                        throw new UnauthenticatedException("Access token expired.");
                    }
                    else {
                        log.info("User authenticated.");
                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                        AuthUser user = (AuthUser) authentication.getPrincipal();

                        return ResponseHandler.generateResponse(
                            HttpStatus.OK,
                            "User is authenticated.",
                            modelMapper.map(user, AuthUserPayload.class)
                        );
                    }
                }
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        log.error("Access token does not exist.");
        HttpHeaders headers = new HttpHeaders();

        if (refreshToken != null) {
            ResponseCookie refreshTokenCookie = ResponseCookie
                    .from("refreshToken", "")
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(0)
                    .build();

            refreshTokenService.deleteByToken(refreshToken);
            headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        }

        return ResponseHandler.generateResponse(
            HttpStatus.UNAUTHORIZED,
            "Unauthorized.",
            headers
        );
    }

    @GetMapping("/sign-out")
    public ResponseEntity<CustomResponse> signOut() {
        // get current principal
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser user = (AuthUser) authentication.getPrincipal();

        HttpHeaders headers = SignOutHandler.signUserOut();

        // remove refresh tokens from db
        refreshTokenService.deleteByUserId(user.getId());

        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            "Signed out.",
            headers
        );
    }
}
