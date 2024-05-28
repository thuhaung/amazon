package com.example.amazon.Util;

import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    public boolean isAuthorized(String authId, Long id) {
        return Long.valueOf(getAuthId(authId)).equals(id);
    }

    public String getAuthId(String value) {
        if (value.isEmpty()) {
            return "-1";
        }

        return value;
    }
}
