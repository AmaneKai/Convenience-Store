package com.konbini.application.handler;

import com.konbini.application.command.ProcessCheckoutCommand;
import com.konbini.application.dto.TransactionDTO;
import com.konbini.application.mediator.RequestHandler;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.common.IdentifierGenerator;
import com.konbini.domain.customer.Customer;
import com.konbini.domain.customer.CustomerRepository;
import com.konbini.domain.product.Product;
import com.konbini.domain.product.ProductRepository;
import com.konbini.domain.transaction.Cart;
import com.konbini.domain.transaction.CartItem;
import com.konbini.domain.transaction.CheckoutCalculation;
import com.konbini.domain.transaction.CheckoutCalculator;
import com.konbini.domain.transaction.Transaction;
import com.konbini.domain.transaction.TransactionRepository;
import com.konbini.domain.unitofwork.UnitOfWork;
import io.vavr.control.Either;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

/**
 * Single-purpose handler that processes a checkout: it builds the cart, applies
 * VAT and eligible discounts, verifies payment, decrements inventory, updates
 * loyalty points and persists the transaction atomically.
 */
public class ProcessCheckoutCommandHandler implements RequestHandler<ProcessCheckoutCommand, TransactionDTO> {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private final IdentifierGenerator identifierGenerator;
    private final UnitOfWork unitOfWork;

    /**
     * Constructs the checkout handler.
     *
     * @param customerRepository the customer repository
     * @param productRepository the product repository
     * @param transactionRepository the transaction repository
     * @param identifierGenerator the ID generator
     * @param unitOfWork the atomic persistence unit
     */
    public ProcessCheckoutCommandHandler(CustomerRepository customerRepository,
                                         ProductRepository productRepository,
                                         TransactionRepository transactionRepository,
                                         IdentifierGenerator identifierGenerator,
                                         UnitOfWork unitOfWork) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
        this.identifierGenerator = identifierGenerator;
        this.unitOfWork = unitOfWork;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<DomainError, TransactionDTO> handle(ProcessCheckoutCommand command) {
        if (command.items() == null || command.items().isEmpty()) {
            return Either.left(DomainError.businessRule("Cart is empty"));
        }

        Either<DomainError, Customer> customerResult = findCustomer(command.customerId());
        if (customerResult.isLeft()) {
            return Either.left(customerResult.getLeft());
        }
        Customer customer = customerResult.get();

        Either<DomainError, Cart> cartResult = buildCart(customer, command.items());
        if (cartResult.isLeft()) {
            return Either.left(cartResult.getLeft());
        }

        try {
            Transaction transaction = completeCheckout(
                    customer, cartResult.get(), command.paymentAmount(), command.pointsToRedeem());

            transactionRepository.add(transaction);
            boolean committed = unitOfWork.commit();
            if (!committed) {
                return Either.left(DomainError.persistence("Failed to persist transaction"));
            }
            return Either.right(TransactionDTO.fromDomain(transaction));
        } catch (IllegalArgumentException exception) {
            return Either.left(DomainError.businessRule(exception.getMessage()));
        } catch (Exception exception) {
            return Either.left(DomainError.persistence(
                    "Failed to persist transaction: " + exception.getMessage()));
        }
    }

    /**
     * Looks up the purchasing customer.
     *
     * @param customerId the customer ID
     * @return the customer or a not-found error
     */
    private Either<DomainError, Customer> findCustomer(String customerId) {
        Optional<Customer> customerOption = customerRepository.findById(customerId);
        if (customerOption.isEmpty()) {
            return Either.left(DomainError.notFound("Customer not found: " + customerId));
        }
        return Either.right(customerOption.get());
    }

    /**
     * Builds a cart from the requested product quantities, validating stock.
     *
     * @param customer the purchasing customer
     * @param items the product ID to quantity map
     * @return a right containing the cart, or a left with the first error
     */
    private Either<DomainError, Cart> buildCart(Customer customer, Map<String, Integer> items) {
        Cart cart = new Cart(customer);
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
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
        return Either.right(cart);
    }

    /**
     * Computes the financial breakdown, mutates inventory and loyalty points,
     * and constructs the completed transaction.
     *
     * @param customer the purchasing customer
     * @param cart the validated cart
     * @param paymentAmount the amount tendered
     * @param pointsToRedeem the loyalty points to redeem
     * @return the completed transaction
     * @throws IllegalArgumentException if payment is insufficient or invalid
     */
    private Transaction completeCheckout(Customer customer, Cart cart,
                                         BigDecimal paymentAmount, int pointsToRedeem) {
        if (paymentAmount == null || paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than 0");
        }

        CheckoutCalculation calculation = CheckoutCalculator.calculate(cart, pointsToRedeem);
        if (paymentAmount.compareTo(calculation.total()) < 0) {
            throw new IllegalArgumentException("Payment amount is insufficient");
        }

        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            product.decreaseQuantity(item.getQuantity());
            productRepository.update(product);
        }

        if (calculation.pointsRedeemed() > 0) {
            customer.getMembershipCard().deductPoints(calculation.pointsRedeemed());
        }
        if (calculation.pointsEarned() > 0) {
            customer.getMembershipCard().addPoints(calculation.pointsEarned());
        }
        if (calculation.pointsRedeemed() > 0 || calculation.pointsEarned() > 0) {
            customerRepository.update(customer);
        }

        String transactionId = identifierGenerator.generate("transaction");

        return Transaction.builder()
                .id(transactionId)
                .customer(customer)
                .items(new ArrayList<>(cart.getItems()))
                .timestamp(LocalDateTime.now())
                .subtotal(calculation.subtotal())
                .tax(calculation.tax())
                .discount(calculation.discount())
                .total(calculation.total())
                .amountPaid(paymentAmount)
                .change(paymentAmount.subtract(calculation.total()))
                .pointsEarned(calculation.pointsEarned())
                .pointsRedeemed(calculation.pointsRedeemed())
                .appliedDiscounts(calculation.appliedDiscounts())
                .taxName(calculation.taxName())
                .build();
    }
}
