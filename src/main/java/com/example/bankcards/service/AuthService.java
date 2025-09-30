package com.example.bankcards.service;

import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.dto.LoginResponse;
import com.example.bankcards.dto.RegisterRequest;
import com.example.bankcards.dto.RegisterResponse;
import jakarta.validation.Valid;

public interface AuthService {
    LoginResponse login(@Valid LoginRequest request);
    RegisterResponse register(@Valid RegisterRequest request);
}
