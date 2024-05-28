package com.example.amazon.Config.APIGateway;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
    @Value("${uri.user-service}")
    private String userServiceURI;

    @Bean
    public RouterFunction<ServerResponse> gatewayRouterFunctionsPath(AuthHeaderFilter filter) {
        return route("user-service")
                .route(path("/user/**"), http(userServiceURI))
                .before(filter.addRequestHeader())
                .build();
    }
}
