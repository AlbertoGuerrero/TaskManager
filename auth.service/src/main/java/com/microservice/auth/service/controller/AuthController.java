package com.microservice.auth.service.controller;

import com.microservice.auth.service.entity.RefreshToken;
import com.microservice.auth.service.entity.User;
import com.microservice.auth.service.response.MessageResponse;
import com.microservice.auth.service.security.JwtProvider;
import com.microservice.auth.service.security.exception.TokenRefreshException;
import com.microservice.auth.service.service.RefreshTokenService;
import com.microservice.auth.service.service.UserService;
import com.microservice.commons.dto.RequestDTO;
import com.microservice.commons.dto.TokenDTO;
import com.microservice.commons.dto.UserDTO;
import com.microservice.commons.dto.UserRegistrationDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        ResponseCookie jwtCookie = userService.login(userRegistrationDTO);
        if (jwtCookie == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<User> user = userService.findByUserName(userRegistrationDTO.getUserName());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.get());
        ResponseCookie jwtRefreshCookie = refreshTokenService.generateRefreshJwtCookie(refreshToken.getToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(UserDTO.builder()
                        .userName(user.get().getUserName())
                        .email(user.get().getEmail()).build());
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = refreshTokenService.getJwtRefreshFromCookies(request);

        if ((refreshToken != null) && (refreshToken.length() > 0)) {
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        ResponseCookie jwtCookie = refreshTokenService.generateJwtCookie(user);

                        return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                                .body(new MessageResponse("Token is refreshed successfully!"));
                    })
                    .orElseThrow(() -> new TokenRefreshException(refreshToken,
                            "Refresh token is not in database!"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Refresh Token is empty!"));
    }

    @PostMapping("/validate")
    public ResponseEntity<TokenDTO> validate(@RequestParam String token, @RequestBody RequestDTO requestDTO) {
        TokenDTO tokenDTO = userService.validate(token, requestDTO);
        if (tokenDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
