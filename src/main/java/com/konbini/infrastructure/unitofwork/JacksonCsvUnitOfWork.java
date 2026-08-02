package com.konbini.infrastructure.unitofwork;

import com.konbini.domain.unitofwork.UnitOfWork;
import com.konbini.infrastructure.repository.CsvCustomerRepository;
import com.konbini.infrastructure.repository.CsvEmployeeRepository;
import com.konbini.infrastructure.repository.CsvProductRepository;
import com.konbini.infrastructure.repository.CsvTransactionRepository;

/**
 * Coordinates atomic persistence across all CSV repositories. A single commit
 * writes every repository's in-memory state to its CSV file, mirroring a
 * database transaction for the file-backed store.
 */
public class JacksonCsvUnitOfWork implements UnitOfWork {

    private final CsvProductRepository productRepository;
    private final CsvCustomerRepository customerRepository;
    private final CsvEmployeeRepository employeeRepository;
    private final CsvTransactionRepository transactionRepository;

    /**
     * Constructs the unit of work over the four CSV repositories.
     *
     * @param productRepository the product repository
     * @param customerRepository the customer repository
     * @param employeeRepository the employee repository
     * @param transactionRepository the transaction repository
     */
    public JacksonCsvUnitOfWork(CsvProductRepository productRepository,
                                CsvCustomerRepository customerRepository,
                                CsvEmployeeRepository employeeRepository,
                                CsvTransactionRepository transactionRepository) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean commit() {
        try {
            productRepository.save();
            customerRepository.save();
            employeeRepository.save();
            transactionRepository.save();
            return true;
        } catch (Exception exception) {
            System.err.println("Unit of work commit failed: " + exception.getMessage());
            return false;
        }
    }
}
