package com.konbini.domain.employee;

import lombok.Builder;
import lombok.Getter;

/**
 * Employee aggregate with authentication credentials.
 * Only the salted password hash is stored; plaintext passwords are never persisted.
 */
@Getter
public class Employee {

    private final String id;
    private String name;
    private String passwordHash;

    /**
     * Constructs an employee with a pre-computed password hash.
     *
     * @param id the employee identifier
     * @param name the employee name
     * @param passwordHash the salted hash of the employee password
     * @throws IllegalArgumentException if id is null or empty
     */
    @Builder
    public Employee(String id, String name, String passwordHash) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }
        this.id = id;
        this.name = name;
        this.passwordHash = passwordHash;
    }

    /**
     * Updates the employee name.
     *
     * @param newName the new name
     */
    public void updateName(String newName) {
        this.name = newName;
    }

    /**
     * Replaces the stored password hash.
     *
     * @param newPasswordHash the new salted hash
     */
    public void updatePasswordHash(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    /**
     * Returns the string representation excluding the password hash for security.
     *
     * @return a string with id and name only
     */
    @Override
    public String toString() {
        return "Employee{id='" + id + "', name='" + name + "'}";
    }
}
