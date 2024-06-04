package com.example.amazon.Config.Security.JWT;

import com.example.amazon.Model.AuthUser;
import com.example.amazon.Service.AuthUserService;
import com.example.amazon.Util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AuthUserService authUserService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

        String token = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("accessToken")) {
                    token = cookie.getValue();
                    log.debug("Access token cookie exists, token = " + token);
                }
            }
        }

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtUtil.validateJwtToken(token)) {
                log.info("User authenticated.");

                String email = jwtUtil.getEmailFromJwtToken(token);
                AuthUser user = (AuthUser) authUserService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
            }
        } catch (ExpiredJwtException e) {
            log.error("JWT is expired: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("JWT signature is invalid: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claim is missing: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
