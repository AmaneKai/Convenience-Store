package com.konbini.application.handler;

import com.konbini.application.command.AddProductCommand;
import com.konbini.application.dto.ProductDTO;
import com.konbini.application.mediator.RequestHandler;
import com.konbini.application.validation.ProductValidator;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.common.IdentifierGenerator;
import com.konbini.domain.product.Product;
import com.konbini.domain.product.ProductRepository;
import com.konbini.domain.unitofwork.UnitOfWork;
import io.vavr.control.Either;
import io.vavr.control.Option;

/**
 * Single-purpose handler that adds a new product to the inventory.
 */
public class AddProductCommandHandler implements RequestHandler<AddProductCommand, ProductDTO> {

    private final ProductRepository productRepository;
    private final IdentifierGenerator identifierGenerator;
    private final UnitOfWork unitOfWork;
    private final ProductValidator validator;

    /**
     * Constructs the add-product handler.
     *
     * @param productRepository the product repository
     * @param identifierGenerator the ID generator
     * @param unitOfWork the atomic persistence unit
     * @param validator the product validator
     */
    public AddProductCommandHandler(ProductRepository productRepository,
                                    IdentifierGenerator identifierGenerator,
                                    UnitOfWork unitOfWork,
                                    ProductValidator validator) {
        this.productRepository = productRepository;
        this.identifierGenerator = identifierGenerator;
        this.unitOfWork = unitOfWork;
        this.validator = validator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<DomainError, ProductDTO> handle(AddProductCommand command) {
        Option<DomainError> validationError = validator.validateAdd(command);
        if (validationError.isDefined()) {
            return Either.left(validationError.get());
        }

        try {
            Product product = Product.builder()
                    .id(identifierGenerator.generate("product"))
                    .name(command.name())
                    .price(command.price())
                    .quantity(command.quantity())
                    .category(command.category())
                    .brand(command.brand())
                    .variant(command.variant())
                    .expirationDate(command.expirationDate())
                    .build();

            productRepository.add(product);
            boolean committed = unitOfWork.commit();
            if (!committed) {
                return Either.left(DomainError.persistence("Failed to persist product"));
            }
            return Either.right(ProductDTO.fromDomain(product));
        } catch (IllegalArgumentException exception) {
            return Either.left(DomainError.validation(exception.getMessage()));
        } catch (Exception exception) {
            return Either.left(DomainError.persistence(
                    "Failed to add product: " + exception.getMessage()));
        }
    }
}
