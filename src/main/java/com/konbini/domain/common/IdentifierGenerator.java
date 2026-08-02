package com.konbini.domain.common;

/**
 * Contract for generating sequential, human-readable identifiers.
 * Implementations are responsible for persistence of the underlying counters.
 * Injected via Google Guice rather than accessed as a static singleton.
 */
public interface IdentifierGenerator {

    /**
     * Generates a unique identifier for the given entity type.
     * Format follows the legacy convention: a three-letter uppercase prefix
     * followed by a zero-padded four-digit sequence, e.g. {@code PRO0001}.
     *
     * @param entityType the entity type; its first three characters become the prefix
     * @return a newly allocated unique identifier
     * @throws IllegalArgumentException if entityType is null or shorter than three characters
     */
    String generate(String entityType);
}
