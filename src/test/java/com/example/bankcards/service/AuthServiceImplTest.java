package com.example.bankcards.service;

import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.dto.LoginResponse;
import com.example.bankcards.dto.RegisterRequest;
import com.example.bankcards.dto.RegisterResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.exception.AuthenticationException;
import com.example.bankcards.exception.UserExistException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_EMAIL = "newuser@example.com";
    private static final String ENCODED_PASSWORD = "encoded-password";
    private static final String JWT_TOKEN = "jwt-token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword(TEST_PASSWORD);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(TEST_USERNAME);

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(JWT_TOKEN);

        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setRole(Role.USER);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));

        LoginResponse response = authService.login(request);

        assertEquals(JWT_TOKEN, response.getToken());
        assertEquals(TEST_USERNAME, response.getUsername());
        assertEquals("USER", response.getRole());

        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateToken(userDetails);
        verify(userRepository).findByUsername(TEST_USERNAME);
    }

    @Test
    void login_Failure_BadCredentials() {
        LoginRequest request = new LoginRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> authService.login(request));
        assertTrue(exception.getMessage().contains("Authentication failed"));

        verify(authenticationManager).authenticate(any());
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(TEST_USERNAME);
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        RegisterResponse response = authService.register(request);

        assertEquals("Registration was successful", response.getMessage());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals(TEST_USERNAME, savedUser.getUsername());
        assertEquals(ENCODED_PASSWORD, savedUser.getPassword());
        assertEquals(TEST_EMAIL, savedUser.getEmail());
        assertEquals(Role.USER, savedUser.getRole());

        verify(userRepository).findByUsername(TEST_USERNAME);
        verify(userRepository).findByEmail(TEST_EMAIL);
        verify(passwordEncoder).encode(TEST_PASSWORD);
    }

    @Test
    void register_Failure_UserExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setEmail("existinguser@example.com");

        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(new User()));

        UserExistException exception = assertThrows(UserExistException.class,
                () -> authService.register(request));
        assertTrue(exception.getMessage().contains("already exists"));

        verify(userRepository).findByUsername("existinguser");
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_Failure_EmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("existing@example.com");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(new User()));

        UserExistException exception = assertThrows(UserExistException.class,
                () -> authService.register(request));
        assertTrue(exception.getMessage().contains("already exists"));

        verify(userRepository).findByUsername("newuser");
        verify(userRepository).findByEmail("existing@example.com");
        verify(userRepository, never()).save(any());
    }
}