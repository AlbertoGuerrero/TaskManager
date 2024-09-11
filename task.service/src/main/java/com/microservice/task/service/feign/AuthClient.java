package com.microservice.task.service.feign;

import com.microservice.commons.dto.UserDTO;
import com.microservice.task.service.configuration.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", configuration = FeignClientConfiguration.class)
public interface AuthClient {
    @GetMapping("/auth/{userId}")
    UserDTO getByUserId(@PathVariable("userId") Long userId);
}
