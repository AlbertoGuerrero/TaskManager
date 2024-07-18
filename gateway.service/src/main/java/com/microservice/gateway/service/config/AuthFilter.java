package com.microservice.gateway.service.config;

import com.microservice.commons.dto.RequestDTO;
import com.microservice.commons.dto.TokenDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    @Value("${jwt.cookie.name}")
    private String jwtCookie;
    private WebClient.Builder webClient;

    public AuthFilter(WebClient.Builder webClient) {
        super(Config.class);
        this.webClient = webClient;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            String jwt = getCookieValueByName(exchange.getRequest(), jwtCookie);
            return webClient.build()
                    .post()
                    .uri("http://auth-service/auth/validate?token=" + jwt)
                    .bodyValue(new RequestDTO(exchange.getRequest().getPath().toString(), exchange.getRequest().getMethod().toString()))
                    .retrieve()
                    .onStatus(status -> status.isError(), response -> {
                        if (response.statusCode() == HttpStatus.UNAUTHORIZED) {
                            return Mono.error(new UnauthorizedException("Unauthorized"));
                        }
                        return Mono.error(new RuntimeException("Unexpected client error"));
                    })
                    .bodyToMono(TokenDTO.class)
                    .map(t -> {
                        t.getToken();
                        return exchange;
                    }).flatMap(chain::filter)
                    .onErrorResume(UnauthorizedException.class, ex -> {
                        return onError(exchange, HttpStatus.UNAUTHORIZED);
                    })
                    .onErrorResume(RuntimeException.class, ex -> {
                        return onError(exchange, HttpStatus.INTERNAL_SERVER_ERROR);
                    });
        }));
    }

    private String getCookieValueByName(ServerHttpRequest request, String cookieName) {
        HttpHeaders headers = request.getHeaders();
        List<String> cookies = headers.get(HttpHeaders.COOKIE);
        if (cookies != null) {
            for (String cookie : cookies) {
                List<String> cookiePairs = Arrays.stream(cookie.split(";"))
                        .map(String::trim)
                        .collect(Collectors.toList());
                for (String cookiePair : cookiePairs) {
                    String[] parts = cookiePair.split("=", 2);
                    if (parts.length == 2 && parts[0].equals(cookieName)) {
                        return parts[1];
                    }
                }
            }
        }
        return null;
    }

    public Mono<Void> onError(ServerWebExchange exchange, HttpStatus status){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }

    public static class Config {}
}
