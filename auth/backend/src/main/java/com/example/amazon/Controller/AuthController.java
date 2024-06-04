package com.example.amazon.Controller;

import com.example.amazon.DTO.AuthUser.SignUpRequest;
import com.example.amazon.Model.AuthUser;
import com.example.amazon.Controller.Payload.Response.CustomResponse;
import com.example.amazon.DTO.AuthUser.SignInRequest;
import com.example.amazon.Service.AuthenticationService;
import com.example.amazon.Util.CookieUtil;
import com.example.amazon.Util.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private final AuthenticationService authenticationService;

    @PostMapping("/sign-in")
    public ResponseEntity<CustomResponse> signIn(
        @Valid @RequestBody SignInRequest request
    ) {
        // authenticate
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        AuthUser user = (AuthUser) authentication.getPrincipal();

        log.info(String.format("User %s is authenticated successfully.", request.getEmail()));

        // perform sign in operations
        List<ResponseCookie> cookies = authenticationService.signIn(user);

        HttpHeaders headers = CookieUtil.setCookieHeader(cookies);

        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            headers
        );
    }

    @PostMapping("/sign-up")
    public ResponseEntity<CustomResponse> signUp(
        @Valid @RequestBody SignUpRequest request
    ) {
        AuthUser user = authenticationService.signUp(request);

        return ResponseHandler.generateResponse(
            HttpStatus.CREATED,
            user
        );
    }

//    for front-end authentication purpose due to HttpOnly cookies
//    @GetMapping("/status")
//    public ResponseEntity<CustomResponse> checkStatus(
//        HttpServletRequest request,
//        Principal principal
//    ) {
//        String accessToken = null;
//        String refreshToken = null;
//
//        // get access token from cookie
//        if (request.getCookies() != null) {
//            for (Cookie cookie : request.getCookies()) {
//                if (cookie.getName().equals("accessToken")) {
//                    accessToken = cookie.getValue();
//                    log.debug("Access token exists, token = " + accessToken);
//
//                    // validate access token
//                    if (!jwtUtil.validateJwtToken(accessToken)) {
//                        log.error("Access token expired.");
//                        throw new UnauthenticatedException("Access token expired.");
//                    }
//                    else {
//                        log.info("User authenticated.");
////                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
////                        AuthUser user = (AuthUser) authentication.getPrincipal();
//                        AuthUser user = (AuthUser) principal;
//
//                        return ResponseHandler.generateResponse(
//                            HttpStatus.OK,
//                            "User is authenticated.",
//                            modelMapper.map(user, AuthUserPayload.class)
//                        );
//                    }
//                }
//                if (cookie.getName().equals("refreshToken")) {
//                    refreshToken = cookie.getValue();
//                }
//            }
//        }
//
//        log.error("Access token does not exist.");
//        HttpHeaders headers = new HttpHeaders();
//
//        if (refreshToken != null) {
//            ResponseCookie refreshTokenCookie = ResponseCookie
//                    .from("refreshToken", "")
//                    .httpOnly(true)
//                    .secure(false)
//                    .path("/")
//                    .maxAge(0)
//                    .build();
//
//            refreshTokenService.deleteByToken(refreshToken);
//            headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
//        }
//
//        return ResponseHandler.generateResponse(
//            HttpStatus.UNAUTHORIZED,
//            "Unauthorized.",
//            headers
//        );
//    }

    @GetMapping("/sign-out")
    public ResponseEntity<CustomResponse> signOut(
        Authentication authentication
    ) {
        AuthUser user = (AuthUser) authentication.getPrincipal();

        // perform sign out operations
        List<ResponseCookie> cookies = authenticationService.signOut(user);

        HttpHeaders headers = CookieUtil.setCookieHeader(cookies);

        return ResponseHandler.generateResponse(
            HttpStatus.OK,
            headers
        );
    }
}
