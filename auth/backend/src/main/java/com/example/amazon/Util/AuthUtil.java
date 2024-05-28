package com.example.amazon.Util;

import com.example.amazon.Exception.Authentication.UnauthenticatedException;
import com.example.amazon.Model.AuthUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    public long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            AuthUser user = (AuthUser) authentication.getPrincipal();
            return user.getId();
        }

        return 0;
    }

    public boolean hasPermission(Long id) {
        return getCurrentUserId() == id;
    }
}
