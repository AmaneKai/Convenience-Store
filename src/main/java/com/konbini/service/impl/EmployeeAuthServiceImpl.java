package com.konbini.service.impl;

import com.konbini.service.EmployeeAuthService;

public class EmployeeAuthServiceImpl implements EmployeeAuthService {
    private static final String EMPLOYEE_PASSWORD = "password";

    @Override
    public boolean authenticate(String password) {
        return EMPLOYEE_PASSWORD.equals(password);
    }

    @Override
    public void logAuthenticationAttempt(boolean success) {
        System.out.println("Authentication attempt: " + 
        (success ? "Successful" : "Failed"));
    }
}
