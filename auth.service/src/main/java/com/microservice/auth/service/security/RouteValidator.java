package com.microservice.auth.service.security;

import com.microservice.commons.dto.RequestDTO;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
@Data
@ConfigurationProperties(prefix = "admin-paths")
public class RouteValidator {
    private List<RequestDTO> paths;

    public boolean isAdminPath(RequestDTO requestDTO) {
        return paths.stream().anyMatch(
                p -> Pattern.matches(p.getUri(), requestDTO.getUri())
                        && p.getMethod().equals(requestDTO.getMethod()));
    }
}
