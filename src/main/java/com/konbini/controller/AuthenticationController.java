package com.konbini.controller;

import com.konbini.service.EmployeeAuthService;

/**
 * Controller responsible for handling authentication processes for employees.
 * This class manages the authentication workflow by delegating to the authentication service.
 */
public class AuthenticationController {
    
    /** 
     * The service used for performing authentication and logging authentication attempts. 
     */
    private final EmployeeAuthService authService;

    /**
     * Constructs an AuthenticationController with the specified authentication service.
     * 
     * @param authService The service responsible for authentication and logging
     */
    public AuthenticationController(EmployeeAuthService authService) {
        this.authService = authService;
    }

    /**
     * Attempts to authenticate an employee using the provided password.
     * 
     * This method does two things:
     * 1. Attempts to authenticate the employee through the authentication service
     * 2. Logs the authentication attempt (whether successful or not)
     * 
     * @param password The password to be validated
     * @return {@code true} if authentication is successful, {@code false} otherwise
     */
    public boolean authenticate(String password) {
        boolean authenticated = authService.authenticate(password);
        authService.logAuthenticationAttempt(authenticated);
        return authenticated;
    }
}