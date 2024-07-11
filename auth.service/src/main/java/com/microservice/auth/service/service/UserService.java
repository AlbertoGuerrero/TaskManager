package com.microservice.auth.service.service;

import com.microservice.auth.service.entity.User;
import com.microservice.commons.dto.RequestDTO;
import com.microservice.commons.dto.TokenDTO;
import com.microservice.commons.dto.UserRegistrationDTO;

import java.util.List;

public interface UserService {
    User save(UserRegistrationDTO userRegistrationDTO);
    User assignRolesToUser(Long userId, List<String> rolesToAdd);
    TokenDTO login(UserRegistrationDTO userRegistrationDTO);
    TokenDTO validate(String token, RequestDTO requestDTO);
}
