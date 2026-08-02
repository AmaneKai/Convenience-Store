package com.konbini.application.validation;

import com.konbini.domain.common.DomainError;
import io.vavr.control.Option;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Supplier;

/**
 * Fluent building block for composing validators. Each rule appends a failure
 * message; the first failure short-circuits the chain, keeping validators free
 * of nested control flow.
 */
public class ValidationRule<TRequest> {

    private final TRequest request;
    private Option<DomainError> failure = Option.none();

    /**
     * Constructs a rule chain for a request.
     *
     * @param request the request being validated
     */
    public ValidationRule(TRequest request) {
        this.request = request;
    }

    /**
     * Requires a condition to hold, otherwise records a validation error.
     *
     * @param condition the condition that must be true
     * @param message the failure message
     * @return this chain
     */
    public ValidationRule<TRequest> require(boolean condition, String message) {
        if (failure.isEmpty() && !condition) {
            failure = Option.some(DomainError.validation(message));
        }
        return this;
    }

    /**
     * Requires a non-blank string value.
     *
     * @param value the value to check
     * @param message the failure message
     * @return this chain
     */
    public ValidationRule<TRequest> requiredText(String value, String message) {
        return require(value != null && !value.trim().isEmpty(), message);
    }

    /**
     * Requires a non-null value.
     *
     * @param value the value to check
     * @param message the failure message
     * @return this chain
     */
    public ValidationRule<TRequest> required(Object value, String message) {
        return require(value != null, message);
    }

    /**
     * Requires a value greater than zero.
     *
     * @param value the value to check
     * @param message the failure message
     * @return this chain
     */
    public ValidationRule<TRequest> greaterThanZero(BigDecimal value, String message) {
        return require(value != null && value.compareTo(BigDecimal.ZERO) > 0, message);
    }

    /**
     * Requires a value greater than zero.
     *
     * @param value the value to check
     * @param message the failure message
     * @return this chain
     */
    public ValidationRule<TRequest> greaterThanZero(int value, String message) {
        return require(value > 0, message);
    }

    /**
     * Requires a non-negative value.
     *
     * @param value the value to check
     * @param message the failure message
     * @return this chain
     */
    public ValidationRule<TRequest> notNegative(BigDecimal value, String message) {
        return require(value == null || value.compareTo(BigDecimal.ZERO) >= 0, message);
    }

    /**
     * Requires a non-negative value.
     *
     * @param value the value to check
     * @param message the failure message
     * @return this chain
     */
    public ValidationRule<TRequest> notNegative(int value, String message) {
        return require(value >= 0, message);
    }

    /**
     * Requires a date that is not in the past.
     *
     * @param date the date to check
     * @param message the failure message
     * @return this chain
     */
    public ValidationRule<TRequest> notInPast(LocalDate date, String message) {
        return require(date == null || !date.isBefore(LocalDate.now()), message);
    }

    /**
     * Requires an additional arbitrary condition.
     *
     * @param supplier the condition supplier
     * @param message the failure message
     * @return this chain
     */
    public ValidationRule<TRequest> check(Supplier<Boolean> supplier, String message) {
        return require(supplier.get(), message);
    }

    /**
     * Returns the recorded failure, if any.
     *
     * @return None if valid, otherwise Some containing the error
     */
    public Option<DomainError> result() {
        return failure;
    }

    /**
     * Returns the request under validation.
     *
     * @return the request
     */
    public TRequest getRequest() {
        return request;
    }
}
