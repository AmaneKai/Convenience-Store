package com.konbini.domain.common;

/**
 * Monadic error boundary used by all application-layer operations.
 * Encapsulates a structured failure so that services never leak exceptions
 * across architectural boundaries.
 */
public record DomainError(ErrorCode code, String message) {

    /**
     * Categorizes failures that can occur across the application.
     */
    public enum ErrorCode {
        /**
         * The request payload violated one or more validation rules.
         */
        VALIDATION,
        /**
         * A referenced entity could not be found.
         */
        NOT_FOUND,
        /**
         * The operation violated a business rule or invariant.
         */
        BUSINESS_RULE,
        /**
         * A persistence or file input/output failure occurred.
         */
        PERSISTENCE,
        /**
         * The current session does not permit the requested operation.
         */
        UNAUTHORIZED,
        /**
         * An unexpected failure occurred.
         */
        UNKNOWN
    }

    /**
     * Creates a validation error.
     *
     * @param message the validation failure message
     * @return a DomainError with code VALIDATION
     */
    public static DomainError validation(String message) {
        return new DomainError(ErrorCode.VALIDATION, message);
    }

    /**
     * Creates a not-found error.
     *
     * @param message the not-found message
     * @return a DomainError with code NOT_FOUND
     */
    public static DomainError notFound(String message) {
        return new DomainError(ErrorCode.NOT_FOUND, message);
    }

    /**
     * Creates a business-rule error.
     *
     * @param message the business-rule violation message
     * @return a DomainError with code BUSINESS_RULE
     */
    public static DomainError businessRule(String message) {
        return new DomainError(ErrorCode.BUSINESS_RULE, message);
    }

    /**
     * Creates a persistence error.
     *
     * @param message the persistence failure message
     * @return a DomainError with code PERSISTENCE
     */
    public static DomainError persistence(String message) {
        return new DomainError(ErrorCode.PERSISTENCE, message);
    }

    /**
     * Creates an unauthorized error.
     *
     * @param message the authorization failure message
     * @return a DomainError with code UNAUTHORIZED
     */
    public static DomainError unauthorized(String message) {
        return new DomainError(ErrorCode.UNAUTHORIZED, message);
    }

    /**
     * Creates an unexpected/unknown error.
     *
     * @param message the failure message
     * @return a DomainError with code UNKNOWN
     */
    public static DomainError unknown(String message) {
        return new DomainError(ErrorCode.UNKNOWN, message);
    }
}
