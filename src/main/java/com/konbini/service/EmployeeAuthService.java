package com.konbini.service;

public interface EmployeeAuthService {
    /**
     * Authenticates an employee
     * @param password Employee password
     * @return true if authentication is successful, false otherwise
     */
    boolean authenticate(String password);

    /**
     * Logs authentication attempts
     * @param success Whether the authentication was successful
     */
    void logAuthenticationAttempt(boolean success);
}
