package com.example.amazon.Config.Security.JWT;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
public class AuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException e)
        throws IOException, ServletException {
        log.error("Unauthorized error: {}", e.getMessage());

        String timestamp = new SimpleDateFormat("hh.mm:ssa dd-MM-yyyy")
                                .format(new Date())
                                .toUpperCase();

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getOutputStream().println(
            "{\"message\": \"Unauthorized.\", \"status_code\": \"401 Unauthorized\", \"time_stamp\": \"" + timestamp + "\"}"
        );
    }
}
