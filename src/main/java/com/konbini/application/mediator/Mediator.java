package com.konbini.application.mediator;

import io.vavr.control.Either;
import com.konbini.domain.common.DomainError;

/**
 * Dispatches requests to their registered handler. The core entry point of the
 * CQRS pipeline: the presentation layer talks exclusively to this mediator.
 *
 * @param <TRequest> the request type
 * @param <TResult> the success result type
 */
public interface Mediator {

    /**
     * Dispatches a request to its handler and returns a monadic result.
     *
     * @param request the request to execute
     * @param <TRequest> the request type
     * @param <TResult> the result type
     * @return an Either containing a DomainError on the left or the result on the right
     * @throws IllegalArgumentException if request is null or has no registered handler
     */
    <TRequest, TResult> Either<DomainError, TResult> send(TRequest request);

    /**
     * Registers a handler for a request type.
     *
     * @param requestType the request class
     * @param handler the handler to register
     * @param <TRequest> the request type
     * @param <TResult> the result type
     */
    <TRequest, TResult> void register(Class<TRequest> requestType, RequestHandler<TRequest, TResult> handler);
}
