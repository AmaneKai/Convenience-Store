package com.konbini.domain.employee;

/**
 * Abstraction over password hashing so the domain layer stays framework-free.
 * The concrete BCrypt implementation lives in the infrastructure layer.
 */
public interface PasswordHasher {

    /**
     * Hashes a plaintext password using a salt.
     *
     * @param plainTextPassword the raw password
     * @return the salted hash string
     */
    String hash(String plainTextPassword);

    /**
     * Verifies a plaintext password against a stored hash.
     *
     * @param plainTextPassword the raw password to check
     * @param hashedPassword the stored hash
     * @return true if the password matches the hash
     */
    boolean verify(String plainTextPassword, String hashedPassword);
}
