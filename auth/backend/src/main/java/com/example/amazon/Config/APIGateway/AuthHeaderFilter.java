package com.example.amazon.Config.APIGateway;

import com.example.amazon.Util.AuthUtil;
import com.example.amazon.Exception.Authentication.UnauthenticatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;

import java.util.function.Consumer;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class AuthHeaderFilter {

    private final AuthUtil authUtil;

    public Function<ServerRequest, ServerRequest> addRequestHeader() {
        return request -> {
            Long authId =  authUtil.getCurrentUserId();

            if (authId > 0) {
                Consumer<HttpHeaders> headers = httpHeaders -> {
                    httpHeaders.add("authId", Long.toString(authId));
                };

                return ServerRequest.from(request)
                        .headers(headers)
                        .build();
            }

            throw new UnauthenticatedException();
        };
    }
}
