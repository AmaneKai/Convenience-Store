package com.konbini.util;

/**
 * Singleton class for managing user session state throughout the application.
 * Tracks currently logged-in user information including user ID, type, and authentication status.
 * Supports both customer and employee user types.
 */
public class UserSession {
    private static UserSession instance;

    private String userId;
    private String userType; // "CUSTOMER" or "EMPLOYEE"
    private boolean loggedIn;

    /**
     * Private constructor for singleton pattern.
     * Initializes session with logged-out state.
     */
    private UserSession() {
        this.loggedIn = false;
    }

    /**
     * Gets the singleton instance of UserSession.
     * Creates new instance if one doesn't exist.
     *
     * @return the singleton UserSession instance
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Logs a user into the system.
     *
     * @param userId the unique identifier of the user
     * @param userType the type of user ("CUSTOMER" or "EMPLOYEE")
     */
    public void login(String userId, String userType) {
        this.userId = userId;
        this.userType = userType;
        this.loggedIn = true;
    }

    /**
     * Logs the current user out of the system.
     * Clears all user information and sets logged-in status to false.
     */
    public void logout() {
        this.userId = null;
        this.userType = null;
        this.loggedIn = false;
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * Gets the ID of the currently logged-in user.
     *
     * @return the user ID, or null if no user is logged in
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the type of the currently logged-in user.
     *
     * @return the user type ("CUSTOMER" or "EMPLOYEE"), or null if no user is logged in
     */
    public String getUserType() {
        return userType;
    }

    /**
     * Checks if the currently logged-in user is a customer.
     *
     * @return true if a customer is logged in, false otherwise
     */
    public boolean isCustomer() {
        return loggedIn && "CUSTOMER".equals(userType);
    }

    /**
     * Checks if the currently logged-in user is an employee.
     *
     * @return true if an employee is logged in, false otherwise
     */
    public boolean isEmployee() {
        return loggedIn && "EMPLOYEE".equals(userType);
    }
}