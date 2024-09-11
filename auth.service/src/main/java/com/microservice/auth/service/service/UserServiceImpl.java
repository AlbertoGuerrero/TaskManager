package com.microservice.auth.service.service;

import com.microservice.auth.service.entity.Role;
import com.microservice.auth.service.entity.User;
import com.microservice.auth.service.repository.RoleRepository;
import com.microservice.auth.service.repository.UserRepository;
import com.microservice.auth.service.security.JwtProvider;
import com.microservice.commons.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public UserDTO save(UserRegistrationDTO userRegistrationDTO) {
        List<User> userList = userRepository.findByUserNameOrEmail(userRegistrationDTO.getUserName(),
                userRegistrationDTO.getEmail());
        if (!userList.isEmpty()) {
            return null;
        }
        Optional<Role> role = roleRepository.getRoleByName("USER");
        Set<Role> roles = new HashSet<>();
        if (role.isPresent()) {
            roles.add(role.get());
        }
        String password = passwordEncoder.encode(userRegistrationDTO.getPassword());
        User user = User.builder()
                .userName(userRegistrationDTO.getUserName())
                .password(password)
                .email(userRegistrationDTO.getEmail())
                .roles(roles).build();
        return convertToDTO(userRepository.save(user));
    }

    @Override
    public User assignRolesToUser(Long userId, List<String> rolesToAdd) {
        Optional<User> user = userRepository.findById(userId);
        Set<Role> roles = new HashSet<>();
        if (!user.isPresent()) {
            return null;
        }
        if (rolesToAdd.isEmpty()) {
            return null;
        }
        for (String nameRole : rolesToAdd) {
            Optional<Role> role = roleRepository.getRoleByName(nameRole);
            if (role.isPresent()) {
                roles.add(role.get());
            }
        }
        if (roles.isEmpty()) {
            return null;
        }
        user.get().getRoles().addAll(roles);
        return userRepository.save(user.get());
    }

    @Override
    public ResponseCookie login(UserRegistrationDTO userRegistrationDTO) {
        Optional<User> user = userRepository.findByUserName(userRegistrationDTO.getUserName());
        if (!user.isPresent()) {
            return null;
        }
        if (passwordEncoder.matches(userRegistrationDTO.getPassword(), user.get().getPassword())) {
            return jwtProvider.generateJwtCookie(user.get());
        }
        return null;
    }

    @Override
    public ResponseCookie getCleanJwtCookie() {
        return jwtProvider.getCleanJwtCookie();
    }

    @Override
    public ResponseCookie getCleanJwtRefreshCookie() {
       return jwtProvider.getCleanJwtRefreshCookie();
    }

    @Override
    public TokenDTO validate(String token, RequestDTO requestDTO) {
        if (!jwtProvider.validate(token, requestDTO)) {
            return null;
        }
        String username = jwtProvider.getUserNameFromToken(token);
        if (!userRepository.findByUserName(username).isPresent()) {
            return null;
        }
        return new TokenDTO(token);
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return user != null ? convertToDTO(user) : null;
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .userName(user.getUserName())
                .email(user.getEmail())
                .build();
    }
}
