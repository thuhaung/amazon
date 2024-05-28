package com.example.amazon.Util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

public class SignOutHandler {

    public static HttpHeaders signUserOut() {
        // remove cookies from client, logs user out
        ResponseCookie accessTokenCookie = ResponseCookie
                .from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();
        ResponseCookie refreshTokenCookie = ResponseCookie
                .from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        // set headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return headers;
    }
}
