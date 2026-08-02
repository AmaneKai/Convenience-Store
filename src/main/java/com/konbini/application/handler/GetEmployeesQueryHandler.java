package com.konbini.application.handler;

import com.konbini.application.dto.EmployeeDTO;
import com.konbini.application.mediator.RequestHandler;
import com.konbini.application.query.GetEmployeesQuery;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.employee.EmployeeRepository;
import io.vavr.control.Either;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Single-purpose handler that retrieves all employees.
 */
public class GetEmployeesQueryHandler implements RequestHandler<GetEmployeesQuery, List<EmployeeDTO>> {

    private final EmployeeRepository employeeRepository;

    /**
     * Constructs the employees query handler.
     *
     * @param employeeRepository the employee repository
     */
    public GetEmployeesQueryHandler(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<DomainError, List<EmployeeDTO>> handle(GetEmployeesQuery query) {
        return Either.right(employeeRepository.findAll().stream()
                .map(EmployeeDTO::fromDomain)
                .collect(Collectors.toList()));
    }
}
