package com.microservice.auth.service.service;

import com.microservice.auth.service.entity.User;
import com.microservice.commons.dto.RequestDTO;
import com.microservice.commons.dto.TokenDTO;
import com.microservice.commons.dto.UserRegistrationDTO;
import org.springframework.http.ResponseCookie;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User save(UserRegistrationDTO userRegistrationDTO);
    User assignRolesToUser(Long userId, List<String> rolesToAdd);
    ResponseCookie login(UserRegistrationDTO userRegistrationDTO);
    TokenDTO validate(String token, RequestDTO requestDTO);
    Optional<User> findByUserName(String userName);
}
