package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;

import java.util.List;

public interface UserService {
    UserDto createUser(User user);
    UserDto updateUser(Long id, User user);
    void deleteUser(Long id);
    UserDto getUserById(Long id);
    List<UserDto> getAllUsers();
    UserDto getUserByEmail(String email);
    void changeUserPassword(Long userId, String newPassword);
}
