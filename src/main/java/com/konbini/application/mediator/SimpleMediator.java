package com.konbini.application.mediator;

import io.vavr.control.Either;
import com.konbini.domain.common.DomainError;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory implementation of the {@link Mediator}. Handlers are keyed
 * by request type and invoked via a single unsafe cast that is safe by
 * construction (request types are registered with exactly one handler).
 */
public class SimpleMediator implements Mediator {

    private final Map<Class<?>, RequestHandler<?, ?>> handlers = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <TRequest, TResult> Either<DomainError, TResult> send(TRequest request) {
        if (request == null) {
            return Either.left(DomainError.validation("Request cannot be null"));
        }

        RequestHandler<TRequest, TResult> handler =
                (RequestHandler<TRequest, TResult>) handlers.get(request.getClass());

        if (handler == null) {
            return Either.left(DomainError.unknown(
                    "No handler registered for request type: " + request.getClass().getSimpleName()));
        }

        try {
            return handler.handle(request);
        } catch (Exception exception) {
            return Either.left(DomainError.unknown(
                    "Unexpected error handling request: " + exception.getMessage()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <TRequest, TResult> void register(Class<TRequest> requestType,
                                             RequestHandler<TRequest, TResult> handler) {
        if (requestType == null || handler == null) {
            throw new IllegalArgumentException("Request type and handler must not be null");
        }
        handlers.put(requestType, handler);
    }
}
