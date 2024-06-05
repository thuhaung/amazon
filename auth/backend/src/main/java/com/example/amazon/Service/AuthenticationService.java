package com.example.amazon.Service;

import com.example.amazon.DTO.AuthUser.SignUpRequest;
import com.example.amazon.Model.AuthUserTransaction;
import com.example.amazon.Model.Enum.AuthUserActionEnum;
import com.example.amazon.Model.RefreshToken;
import com.example.amazon.Util.CookieUtil;
import com.example.amazon.Util.JwtUtil;
import com.example.amazon.Model.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthUserService authUserService;
    private final RefreshTokenService refreshTokenService;
    private final AuthUserTransactionService authUserTransactionService;
    private final DistributedTransactionService distributedTransactionsService;

    /**
     * performs account reactivation and token generation
     * @param user - authenticated user account
     */
    @Transactional
    public List<ResponseCookie> signIn(AuthUser user) {
        // reactivate if user is inactive/deactivated
        if (!user.isActive()) {
            authUserService.setUserActiveStatus(user.getId(), true);
        }

        // generate new access and refresh tokens
        String accessToken = jwtUtil.generateJwtToken(user.getEmail(), user.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        log.info(String.format("Generated access and refresh tokens for %s.", user.getEmail()));

        // create cookies that store generated tokens
        Duration maxAge = Duration.between(Instant.now(), refreshToken.getExpiration());
        ResponseCookie accessTokenCookie = CookieUtil.createTokenCookie(
            "accessToken",
            accessToken,
            maxAge
        );
        ResponseCookie refreshTokenCookie = CookieUtil.createTokenCookie(
            "refreshToken",
            refreshToken.getToken(),
            maxAge
        );

        return List.of(accessTokenCookie, refreshTokenCookie);
    }

    /**
     * persists new user, transaction and notifies consumers of event
     * @param request - user credentials
     * @return - newly created user
     */
    @Transactional
    public AuthUser signUp(SignUpRequest request) {
        // persist new user
        AuthUser user = authUserService.createNewUser(request);

        // persist new transaction associated with user
        AuthUserTransaction transaction = authUserTransactionService.createNewTransaction(
            user,
            AuthUserActionEnum.CREATE
        );

        // distributed transaction - informs consumer services of new record
        Long userId = user.getId();
        Long transactionId = transaction.getId();
        distributedTransactionsService.distributeSignUpEvent(userId, transactionId);

        return user;
    }

    /**
     * performs token removal
     * @param user - authenticated user account
     */
    @Transactional
    public List<ResponseCookie> signOut(AuthUser user) {
        // removes existing refresh token
        refreshTokenService.removeTokenByUserId(user.getId());

        log.info(String.format("Removed refresh token of user %s from database.", user.getEmail()));

        // set token cookies' max age to 0 to instruct browser to remove cookies
        ResponseCookie accessTokenCookie = CookieUtil.createTokenCookie(
            "accessToken",
            "",
            Duration.ZERO
        );
        ResponseCookie refreshTokenCookie = CookieUtil.createTokenCookie(
            "refreshToken",
            "",
            Duration.ZERO
        );

        return List.of(accessTokenCookie, refreshTokenCookie);
    }
}
