package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.mapper.UserMapper;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.exception.UserEmailOrUserNameExists;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private static final Long USER_ID = 1L;
    private static final String USER_EMAIL = "user1@test.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(USER_ID);
        user.setUsername("user1");
        user.setEmail(USER_EMAIL);
        user.setPassword("password");
    }

    @Test
    void testCreateUser_success() {
        when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto expectedDto = new UserDto();
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        UserDto result = userService.createUser(user);

        assertNotNull(result);
        assertEquals(expectedDto, result);

        verify(userRepository).existsByEmail(USER_EMAIL);
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    void testCreateUser_emailExists() {
        when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(true);

        assertThrows(UserEmailOrUserNameExists.class, () -> userService.createUser(user));

        verify(userRepository).existsByEmail(USER_EMAIL);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testGetUserById_notFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(USER_ID));

        verify(userRepository).findById(USER_ID);
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void testGetUserById_success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        UserDto expectedDto = new UserDto();
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        UserDto result = userService.getUserById(USER_ID);

        assertNotNull(result);
        assertEquals(expectedDto, result);

        verify(userRepository).findById(USER_ID);
        verify(userMapper).toDto(user);
    }

    @Test
    void testChangeUserPassword_success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        userService.changeUserPassword(USER_ID, "newPassword");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("encodedPassword", savedUser.getPassword());

        verify(userRepository).findById(USER_ID);
        verify(passwordEncoder).encode("newPassword");
    }

    @Test
    void testChangeUserPassword_userNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.changeUserPassword(USER_ID, "newPassword"));

        verify(userRepository).findById(USER_ID);
        verify(userRepository, never()).save(any());
    }
}