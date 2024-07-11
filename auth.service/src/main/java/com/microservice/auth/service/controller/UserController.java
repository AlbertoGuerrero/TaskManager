package com.microservice.auth.service.controller;

import com.microservice.auth.service.entity.User;
import com.microservice.auth.service.service.UserService;
import com.microservice.commons.dto.RequestDTO;
import com.microservice.commons.dto.TokenDTO;
import com.microservice.commons.dto.UserDTO;
import com.microservice.commons.dto.UserRegistrationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        TokenDTO tokenDTO = userService.login(userRegistrationDTO);
        if (tokenDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(tokenDTO);
    }

    @PostMapping("/validate")
    public ResponseEntity<TokenDTO> validate(@RequestParam String token, @RequestBody RequestDTO requestDTO) {
        TokenDTO tokenDTO = userService.validate(token, requestDTO);
        if (tokenDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(tokenDTO);
    }

    @PostMapping("/create")
    public ResponseEntity<UserDTO> create(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        User user = userService.save(userRegistrationDTO);
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(convertToDTO(user));
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity<User> assignRolesToUser(@PathVariable Long userId, @RequestBody List<String> rolesToAdd) {
        User user =  userService.assignRolesToUser(userId, rolesToAdd);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.badRequest().build();
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .userName(user.getUserName())
                .email(user.getEmail())
                .build();
    }
}
