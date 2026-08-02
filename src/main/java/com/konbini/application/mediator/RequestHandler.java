package com.konbini.application.mediator;

/**
 * Contract implemented by every CQRS request handler. Handlers execute exactly
 * one business operation, satisfying the Single Responsibility Principle.
 *
 * @param <TRequest> the request type handled
 * @param <TResult> the success result type
 */
public interface RequestHandler<TRequest, TResult> {

    /**
     * Handles the request and returns a monadic result.
     *
     * @param request the request to handle
     * @return an Either containing a DomainError on the left or the result on the right
     */
    io.vavr.control.Either<com.konbini.domain.common.DomainError, TResult> handle(TRequest request);
}
