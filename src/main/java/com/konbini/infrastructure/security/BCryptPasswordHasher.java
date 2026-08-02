package com.konbini.infrastructure.security;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.konbini.domain.employee.PasswordHasher;

/**
 * BCrypt-based {@link PasswordHasher} using a cost factor of 12.
 */
public class BCryptPasswordHasher implements PasswordHasher {

    private static final int COST = 12;

    /**
     * {@inheritDoc}
     */
    @Override
    public String hash(String plainTextPassword) {
        return BCrypt.withDefaults()
                .hashToString(COST, plainTextPassword.toCharArray());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean verify(String plainTextPassword, String hashedPassword) {
        return BCrypt.verifyer()
                .verify(plainTextPassword.toCharArray(), hashedPassword.toCharArray())
                .verified;
    }
}
