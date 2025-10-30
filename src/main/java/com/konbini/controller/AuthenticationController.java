package com.konbini.controller;

import com.konbini.service.EmployeeAuthService;

public class AuthenticationController {
    private final EmployeeAuthService authService;

    public AuthenticationController(EmployeeAuthService authService) {
        this.authService = authService;
    }

    public boolean authenticate(String password) {
        boolean authenticated = authService.authenticate(password);
        authService.logAuthenticationAttempt(authenticated);
        return authenticated;
    }
}
