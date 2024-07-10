package com.microservice.auth.service.service;

import com.microservice.auth.service.entity.User;
import com.microservice.commons.dto.TokenDTO;
import com.microservice.commons.dto.UserRegistrationDTO;

public interface UserService {
    User save(UserRegistrationDTO userRegistrationDTO);
    TokenDTO login(UserRegistrationDTO userRegistrationDTO);
    TokenDTO validate(String token);
}
