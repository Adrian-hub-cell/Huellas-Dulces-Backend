package com.huellasdulces.controller;

import com.huellasdulces.dto.LoginRequest;
import com.huellasdulces.dto.LoginResponse;
import com.huellasdulces.dto.CustomerRegisterRequest;
import com.huellasdulces.dto.CustomerRegisterResponse;
import com.huellasdulces.service.AuthService;
import com.huellasdulces.service.CustomerRegistrationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final CustomerRegistrationService registrationService;

    public AuthController(
            AuthService authService,
            CustomerRegistrationService registrationService) {
        this.authService = authService;
        this.registrationService = registrationService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerRegisterResponse register(
            @Valid @RequestBody CustomerRegisterRequest request) {
        return registrationService.register(request);
    }
}