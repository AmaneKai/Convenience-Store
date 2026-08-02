package com.konbini.application.handler;

import com.konbini.application.command.UpdateProductCommand;
import com.konbini.application.dto.ProductDTO;
import com.konbini.application.mediator.RequestHandler;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.product.Product;
import com.konbini.domain.product.ProductRepository;
import com.konbini.domain.unitofwork.UnitOfWork;
import io.vavr.control.Either;
import java.util.Optional;

/**
 * Single-purpose handler that updates an existing product's details.
 */
public class UpdateProductCommandHandler implements RequestHandler<UpdateProductCommand, ProductDTO> {

    private final ProductRepository productRepository;
    private final UnitOfWork unitOfWork;

    /**
     * Constructs the update-product handler.
     *
     * @param productRepository the product repository
     * @param unitOfWork the atomic persistence unit
     */
    public UpdateProductCommandHandler(ProductRepository productRepository, UnitOfWork unitOfWork) {
        this.productRepository = productRepository;
        this.unitOfWork = unitOfWork;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<DomainError, ProductDTO> handle(UpdateProductCommand command) {
        if (command.productId() == null || command.productId().trim().isEmpty()) {
            return Either.left(DomainError.validation("Product ID cannot be empty"));
        }

        Optional<Product> productOption = productRepository.findById(command.productId());
        if (productOption.isEmpty()) {
            return Either.left(DomainError.notFound(
                    "Product not found: " + command.productId()));
        }

        try {
            Product product = productOption.get();
            product.updateName(command.name());
            product.updatePrice(command.price());
            product.updateCategory(command.category());
            product.updateBrand(command.brand());
            product.updateVariant(command.variant());
            product.updateExpirationDate(command.expirationDate());
            product.setQuantity(command.quantity());

            productRepository.update(product);
            boolean committed = unitOfWork.commit();
            if (!committed) {
                return Either.left(DomainError.persistence("Failed to persist product"));
            }
            return Either.right(ProductDTO.fromDomain(product));
        } catch (IllegalArgumentException exception) {
            return Either.left(DomainError.validation(exception.getMessage()));
        } catch (Exception exception) {
            return Either.left(DomainError.persistence(
                    "Failed to update product: " + exception.getMessage()));
        }
    }
}
