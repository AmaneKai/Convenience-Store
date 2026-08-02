package com.konbini.application.handler;

import com.konbini.application.dto.CheckoutPreviewDTO;
import com.konbini.application.mediator.RequestHandler;
import com.konbini.application.query.PreviewCheckoutQuery;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.customer.Customer;
import com.konbini.domain.customer.CustomerRepository;
import com.konbini.domain.product.Product;
import com.konbini.domain.product.ProductRepository;
import com.konbini.domain.transaction.Cart;
import com.konbini.domain.transaction.CheckoutCalculation;
import com.konbini.domain.transaction.CheckoutCalculator;
import io.vavr.control.Either;
import java.util.Map;
import java.util.Optional;

/**
 * Single-purpose handler that computes the financial breakdown of a
 * prospective checkout — subtotal, tax, applicable discounts and points to
 * be earned — without mutating inventory, loyalty points, or persisting
 * anything. Mirrors the calculation in {@link ProcessCheckoutCommandHandler}
 * so a customer can review a total before committing to an order.
 */
public class PreviewCheckoutQueryHandler implements RequestHandler<PreviewCheckoutQuery, CheckoutPreviewDTO> {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    /**
     * Constructs the preview handler.
     *
     * @param customerRepository the customer repository
     * @param productRepository the product repository
     */
    public PreviewCheckoutQueryHandler(CustomerRepository customerRepository,
                                       ProductRepository productRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<DomainError, CheckoutPreviewDTO> handle(PreviewCheckoutQuery query) {
        if (query.items() == null || query.items().isEmpty()) {
            return Either.left(DomainError.businessRule("Cart is empty"));
        }

        Optional<Customer> customerOption = customerRepository.findById(query.customerId());
        if (customerOption.isEmpty()) {
            return Either.left(DomainError.notFound("Customer not found: " + query.customerId()));
        }
        Customer customer = customerOption.get();

        Cart cart = new Cart(customer);
        for (Map.Entry<String, Integer> entry : query.items().entrySet()) {
            String productId = entry.getKey();
            int quantity = entry.getValue();

            if (quantity <= 0) {
                return Either.left(DomainError.businessRule(
                        "Quantity must be greater than 0 for product: " + productId));
            }

            Optional<Product> productOption = productRepository.findById(productId);
            if (productOption.isEmpty()) {
                return Either.left(DomainError.notFound("Product not found: " + productId));
            }

            Product product = productOption.get();
            if (product.getQuantity() < quantity) {
                return Either.left(DomainError.businessRule(
                        "Insufficient quantity for product: " + product.getName()
                                + ". Available: " + product.getQuantity()
                                + ", Requested: " + quantity));
            }

            try {
                cart.addItem(product, quantity);
            } catch (IllegalArgumentException exception) {
                return Either.left(DomainError.businessRule(exception.getMessage()));
            }
        }

        try {
            CheckoutCalculation calculation = CheckoutCalculator.calculate(cart, query.pointsToRedeem());
            return Either.right(new CheckoutPreviewDTO(calculation.subtotal(), calculation.tax(),
                    calculation.taxName(), calculation.discount(), calculation.appliedDiscounts(),
                    calculation.total(), calculation.pointsEarned()));
        } catch (IllegalArgumentException exception) {
            return Either.left(DomainError.businessRule(exception.getMessage()));
        }
    }
}
