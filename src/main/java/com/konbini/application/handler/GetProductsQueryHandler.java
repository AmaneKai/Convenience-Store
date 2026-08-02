package com.konbini.application.handler;

import com.konbini.application.dto.ProductDTO;
import com.konbini.application.mediator.RequestHandler;
import com.konbini.application.query.GetProductsQuery;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.product.Product;
import com.konbini.domain.product.ProductRepository;
import io.vavr.control.Either;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Single-purpose handler that retrieves products, optionally filtered by a
 * search term and category display name.
 */
public class GetProductsQueryHandler implements RequestHandler<GetProductsQuery, List<ProductDTO>> {

    private final ProductRepository productRepository;

    /**
     * Constructs the products query handler.
     *
     * @param productRepository the product repository
     */
    public GetProductsQueryHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<DomainError, List<ProductDTO>> handle(GetProductsQuery query) {
        List<Product> products = productRepository.findAll();

        if (query.categoryDisplayName() != null
                && !query.categoryDisplayName().trim().isEmpty()) {
            String category = query.categoryDisplayName().trim();
            products = products.stream()
                    .filter(product -> category.equalsIgnoreCase(product.getCategory()))
                    .collect(Collectors.toList());
        }

        if (query.searchTerm() != null && !query.searchTerm().trim().isEmpty()) {
            String term = query.searchTerm().trim().toLowerCase();
            products = products.stream()
                    .filter(product -> product.getName().toLowerCase().contains(term))
                    .collect(Collectors.toList());
        }

        return Either.right(products.stream()
                .map(ProductDTO::fromDomain)
                .collect(Collectors.toList()));
    }
}
