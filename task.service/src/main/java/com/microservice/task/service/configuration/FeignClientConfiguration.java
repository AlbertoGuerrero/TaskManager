package com.microservice.task.service.configuration;

import feign.RequestInterceptor;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfiguration {

    @Value("${jwt.cookie.name}")
    private String jwtCookie;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String cookieString = extractCookie(request);
            if (cookieString != null) {
                template.header("Cookie", cookieString);
            }
        };
    }

    private String extractCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (jwtCookie.equals(cookie.getName())) {
                    return cookie.getName() + "=" + cookie.getValue();
                }
            }
        }
        return null;
    }
}
