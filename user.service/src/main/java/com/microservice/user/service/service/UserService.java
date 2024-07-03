package com.microservice.user.service.service;

import com.microservice.user.service.entity.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User saveUser(User user);
    User getUserByUsername(String username);
    User updateUser(Long id, User updatedUser);
    void deleteUserById(Long id);
}
