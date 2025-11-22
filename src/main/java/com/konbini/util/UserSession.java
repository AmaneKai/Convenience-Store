package com.konbini.util;

public class UserSession {
    private static UserSession instance;

    private String userId;
    private String userType; // "CUSTOMER" or "EMPLOYEE"
    private boolean loggedIn;

    private UserSession() {
        this.loggedIn = false;
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void login(String userId, String userType) {
        this.userId = userId;
        this.userType = userType;
        this.loggedIn = true;
    }

    public void logout() {
        this.userId = null;
        this.userType = null;
        this.loggedIn = false;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserType() {
        return userType;
    }

    public boolean isCustomer() {
        return loggedIn && "CUSTOMER".equals(userType);
    }

    public boolean isEmployee() {
        return loggedIn && "EMPLOYEE".equals(userType);
    }
}