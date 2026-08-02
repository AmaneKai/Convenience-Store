package com.konbini.application.handler;

import com.konbini.application.dto.DashboardDTO;
import com.konbini.application.mediator.RequestHandler;
import com.konbini.application.query.GetDashboardQuery;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.customer.CustomerRepository;
import com.konbini.domain.product.ProductRepository;
import com.konbini.domain.transaction.CartItem;
import com.konbini.domain.transaction.Transaction;
import com.konbini.domain.transaction.TransactionRepository;
import io.vavr.control.Either;
import java.math.BigDecimal;
import java.util.List;

/**
 * Single-purpose handler that computes dashboard summary statistics from the
 * current state of all repositories.
 */
public class GetDashboardQueryHandler implements RequestHandler<GetDashboardQuery, DashboardDTO> {

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Constructs the dashboard query handler.
     *
     * @param productRepository the product repository
     * @param customerRepository the customer repository
     * @param transactionRepository the transaction repository
     */
    public GetDashboardQueryHandler(ProductRepository productRepository,
                                    CustomerRepository customerRepository,
                                    TransactionRepository transactionRepository) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<DomainError, DashboardDTO> handle(GetDashboardQuery query) {
        List<Transaction> transactions = transactionRepository.findAll();

        BigDecimal totalSales = transactions.stream()
                .map(Transaction::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long totalItemsSold = transactions.stream()
                .flatMap(transaction -> transaction.getItems().stream())
                .mapToLong(CartItem::getQuantity)
                .sum();

        return Either.right(new DashboardDTO(
                productRepository.findAll().size(),
                productRepository.findLowStock().size(),
                productRepository.findExpired().size(),
                customerRepository.findAll().size(),
                transactions.size(),
                totalSales,
                totalItemsSold));
    }
}
