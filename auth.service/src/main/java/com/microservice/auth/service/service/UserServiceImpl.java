package com.microservice.auth.service.service;

import com.microservice.auth.service.entity.User;
import com.microservice.auth.service.repository.UserRepository;
import com.microservice.auth.service.security.JwtProvider;
import com.microservice.commons.dto.TokenDTO;
import com.microservice.commons.dto.UserRegistrationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public User save(UserRegistrationDTO userRegistrationDTO) {
        List<User> userList = userRepository.findByUserNameOrEmail(userRegistrationDTO.getUserName(),
                userRegistrationDTO.getEmail());
        if (!userList.isEmpty()) {
            return null;
        }
        String password = passwordEncoder.encode(userRegistrationDTO.getPassword());
        User user = User.builder()
                .userName(userRegistrationDTO.getUserName())
                .password(password)
                .email(userRegistrationDTO.getEmail()).build();
        return userRepository.save(user);
    }

    @Override
    public TokenDTO login(UserRegistrationDTO userRegistrationDTO) {
        Optional<User> user = userRepository.findByUserName(userRegistrationDTO.getUserName());
        if (!user.isPresent()) {
            return null;
        }
        if (passwordEncoder.matches(userRegistrationDTO.getPassword(), user.get().getPassword())) {
            return new TokenDTO(jwtProvider.createToken(user.get()));
        }
        return null;
    }

    @Override
    public TokenDTO validate(String token) {
        if (!jwtProvider.validate(token)) {
            return null;
        }
        String username = jwtProvider.getUserNameFromToken(token);
        if (!userRepository.findByUserName(username).isPresent()) {
            return null;
        }
        return new TokenDTO(token);
    }
}
