package com.konbini.model;

import java.io.Serializable;

/**
 * Represents an employee in the store system with authentication credentials.
 * Contains employee identification information and password for system access.
 * Implements Serializable to support persistence.
 */
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String password;

    /**
     * Default constructor for creating an empty Employee.
     * Useful for serialization and dependency injection frameworks.
     */
    public Employee() {}

    /**
     * Constructs a new Employee with the specified ID, name, and password.
     *
     * @param id the employee's unique identifier
     * @param name the employee's name
     * @param password the employee's password for authentication
     */
    public Employee (String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    /**
     * Gets the employee's name.
     *
     * @return the employee name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the employee's name.
     *
     * @param name the new name for the employee
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the employee's password.
     * Note: In a production system, consider security implications of storing plain text passwords.
     *
     * @return the employee password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the employee's password.
     *
     * @param password the new password for the employee
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the employee's unique identifier.
     *
     * @return the employee ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the employee's unique identifier.
     *
     * @param id the new ID for the employee
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns a string representation of the employee.
     * Note: Password is intentionally excluded from the string representation for security.
     *
     * @return a string containing employee ID and name
     */
    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}