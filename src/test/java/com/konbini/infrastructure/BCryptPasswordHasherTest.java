package com.konbini.infrastructure;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.konbini.infrastructure.security.BCryptPasswordHasher;
import org.junit.jupiter.api.Test;

/**
 * Verifies the BCrypt password hasher contract: hashes are salted and
 * verifiable, and plaintext passwords are never stored.
 */
class BCryptPasswordHasherTest {

    private final BCryptPasswordHasher hasher = new BCryptPasswordHasher();

    @Test
    void hashesAreSaltedAndNeverEqualPlaintext() {
        String hash = hasher.hash("password");

        assertNotEquals("password", hash);
        assertTrue(hash.startsWith("$2a$"));
    }

    @Test
    void identicalPasswordsProduceDifferentHashes() {
        String hashOne = hasher.hash("password");
        String hashTwo = hasher.hash("password");

        assertNotEquals(hashOne, hashTwo, "Salting should make hashes unique");
    }

    @Test
    void verifyAcceptsMatchingPassword() {
        String hash = hasher.hash("correct horse battery staple");
        assertTrue(hasher.verify("correct horse battery staple", hash));
    }

    @Test
    void verifyRejectsWrongPassword() {
        String hash = hasher.hash("correct");
        assertFalse(hasher.verify("wrong", hash));
    }
}
