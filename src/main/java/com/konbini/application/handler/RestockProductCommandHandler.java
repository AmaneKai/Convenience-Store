package com.konbini.application.handler;

import com.konbini.application.command.RestockProductCommand;
import com.konbini.application.dto.ProductDTO;
import com.konbini.application.mediator.RequestHandler;
import com.konbini.application.validation.ProductValidator;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.product.Product;
import com.konbini.domain.product.ProductRepository;
import com.konbini.domain.unitofwork.UnitOfWork;
import io.vavr.control.Either;
import io.vavr.control.Option;
import java.util.Optional;

/**
 * Single-purpose handler that restocks an existing product.
 */
public class RestockProductCommandHandler implements RequestHandler<RestockProductCommand, ProductDTO> {

    private final ProductRepository productRepository;
    private final UnitOfWork unitOfWork;
    private final ProductValidator validator;

    /**
     * Constructs the restock handler.
     *
     * @param productRepository the product repository
     * @param unitOfWork the atomic persistence unit
     * @param validator the product validator
     */
    public RestockProductCommandHandler(ProductRepository productRepository,
                                        UnitOfWork unitOfWork,
                                        ProductValidator validator) {
        this.productRepository = productRepository;
        this.unitOfWork = unitOfWork;
        this.validator = validator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<DomainError, ProductDTO> handle(RestockProductCommand command) {
        Option<DomainError> validationError = validator.validateRestock(command);
        if (validationError.isDefined()) {
            return Either.left(validationError.get());
        }

        Optional<Product> productOption = productRepository.findById(command.productId());
        if (productOption.isEmpty()) {
            return Either.left(DomainError.notFound("Product not found: " + command.productId()));
        }

        try {
            Product product = productOption.get();
            product.increaseQuantity(command.quantity());

            productRepository.update(product);
            boolean committed = unitOfWork.commit();
            if (!committed) {
                return Either.left(DomainError.persistence("Failed to persist restock"));
            }
            return Either.right(ProductDTO.fromDomain(product));
        } catch (IllegalArgumentException exception) {
            return Either.left(DomainError.validation(exception.getMessage()));
        } catch (Exception exception) {
            return Either.left(DomainError.persistence(
                    "Failed to restock product: " + exception.getMessage()));
        }
    }
}
