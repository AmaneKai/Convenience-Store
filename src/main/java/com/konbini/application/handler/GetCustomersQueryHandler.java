package com.konbini.application.handler;

import com.konbini.application.dto.CustomerDTO;
import com.konbini.application.mediator.RequestHandler;
import com.konbini.application.query.GetCustomersQuery;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.customer.CustomerRepository;
import io.vavr.control.Either;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Single-purpose handler that retrieves all registered customers.
 */
public class GetCustomersQueryHandler implements RequestHandler<GetCustomersQuery, List<CustomerDTO>> {

    private final CustomerRepository customerRepository;

    /**
     * Constructs the customers query handler.
     *
     * @param customerRepository the customer repository
     */
    public GetCustomersQueryHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<DomainError, List<CustomerDTO>> handle(GetCustomersQuery query) {
        return Either.right(customerRepository.findAll().stream()
                .map(CustomerDTO::fromDomain)
                .collect(Collectors.toList()));
    }
}
