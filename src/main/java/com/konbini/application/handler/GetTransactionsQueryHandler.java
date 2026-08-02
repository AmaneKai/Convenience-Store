package com.konbini.application.handler;

import com.konbini.application.dto.TransactionDTO;
import com.konbini.application.mediator.RequestHandler;
import com.konbini.application.query.GetTransactionsQuery;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.transaction.Transaction;
import com.konbini.domain.transaction.TransactionRepository;
import io.vavr.control.Either;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Single-purpose handler that retrieves transactions, optionally filtered by
 * an inclusive date range.
 */
public class GetTransactionsQueryHandler implements RequestHandler<GetTransactionsQuery, List<TransactionDTO>> {

    private final TransactionRepository transactionRepository;

    /**
     * Constructs the transactions query handler.
     *
     * @param transactionRepository the transaction repository
     */
    public GetTransactionsQueryHandler(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<DomainError, List<TransactionDTO>> handle(GetTransactionsQuery query) {
        List<Transaction> transactions;
        if (query.startDate() == null && query.endDate() == null) {
            transactions = transactionRepository.findAll();
        } else if (query.startDate() != null && query.endDate() != null) {
            transactions = transactionRepository.findByDateRange(query.startDate(), query.endDate());
        } else {
            LocalDate date = query.startDate() != null ? query.startDate() : query.endDate();
            transactions = transactionRepository.findByDate(date);
        }

        return Either.right(transactions.stream()
                .map(TransactionDTO::fromDomain)
                .collect(Collectors.toList()));
    }
}
