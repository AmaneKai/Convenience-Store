package com.konbini.application.handler;

import com.konbini.application.command.RemoveProductCommand;
import com.konbini.application.mediator.RequestHandler;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.product.ProductRepository;
import com.konbini.domain.unitofwork.UnitOfWork;
import io.vavr.control.Either;
import java.util.Optional;

/**
 * Single-purpose handler that removes a product from the inventory.
 */
public class RemoveProductCommandHandler implements RequestHandler<RemoveProductCommand, Boolean> {

    private final ProductRepository productRepository;
    private final UnitOfWork unitOfWork;

    /**
     * Constructs the remove-product handler.
     *
     * @param productRepository the product repository
     * @param unitOfWork the atomic persistence unit
     */
    public RemoveProductCommandHandler(ProductRepository productRepository, UnitOfWork unitOfWork) {
        this.productRepository = productRepository;
        this.unitOfWork = unitOfWork;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<DomainError, Boolean> handle(RemoveProductCommand command) {
        if (command.productId() == null || command.productId().trim().isEmpty()) {
            return Either.left(DomainError.validation("Product ID cannot be empty"));
        }

        Optional<?> existing = productRepository.findById(command.productId());
        if (existing.isEmpty()) {
            return Either.left(DomainError.notFound("Product not found: " + command.productId()));
        }

        try {
            productRepository.remove(command.productId());
            boolean committed = unitOfWork.commit();
            if (!committed) {
                return Either.left(DomainError.persistence("Failed to persist product removal"));
            }
            return Either.right(true);
        } catch (Exception exception) {
            return Either.left(DomainError.persistence(
                    "Failed to remove product: " + exception.getMessage()));
        }
    }
}
