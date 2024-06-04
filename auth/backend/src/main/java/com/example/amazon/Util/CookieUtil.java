package com.example.amazon.Util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

public class CookieUtil {

    /**
     * creates a cookie that stores authentication token
     * @param tokenType - type of token to store in cookie
     * @param value - string value of token
     * @param maxAge - duration in seconds until cookie expires
     * @return cookie that stores authentication token
     */
    public static ResponseCookie createTokenCookie(
        String tokenType,
        String value,
        Duration maxAge
    ) {
        ResponseCookie cookie = ResponseCookie
            .from(tokenType, value)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(maxAge)
            .build();

        return cookie;
    }

    /**
     * includes cookies in response headers
     * @param cookies - cookies to be set in headers
     * @return - response headers with cookies
     */
    public static HttpHeaders setCookieHeader(List<ResponseCookie> cookies) {
        HttpHeaders headers = new HttpHeaders();

        cookies.forEach(cookie -> {
            headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        });

        return headers;
    }
}
