package com.konbini.service.impl;

import com.konbini.service.EmployeeAuthService;

/**
 * Implementation of the EmployeeAuthService for basic authentication.
 * 
 * This service provides a simple authentication mechanism with a hardcoded password
 * and basic logging of authentication attempts. Note: This is a basic implementation
 * and should be replaced with a more secure authentication method in production.
 */
public class EmployeeAuthServiceImpl implements EmployeeAuthService {
    
    /**
     * The hardcoded employee password for authentication.
     * 
     * WARNING: In a real-world application, this should be replaced with a secure
     * password storage and verification mechanism (e.g., hashed passwords, 
     * database lookup, secure authentication service).
     */
    private static final String EMPLOYEE_PASSWORD = "password";

    /**
     * Authenticates an employee by comparing the provided password with the 
     * predefined employee password.
     * 
     * @param password The password submitted for authentication
     * @return {@code true} if the password matches the predefined password, 
     *         {@code false} otherwise
     */
    @Override
    public boolean authenticate(String password) {
        return EMPLOYEE_PASSWORD.equals(password);
    }

    /**
     * Logs the result of an authentication attempt.
     * 
     * This method prints the authentication result to the console. In a production
     * environment, this would typically be replaced with a proper logging mechanism
     * that writes to a log file or sends logs to a centralized logging system.
     * 
     * @param success {@code true} if the authentication was successful, 
     *                {@code false} if the authentication failed
     */
    @Override
    public void logAuthenticationAttempt(boolean success) {
        System.out.println("Authentication attempt: " + 
        (success ? "Successful" : "Failed"));
    }
}