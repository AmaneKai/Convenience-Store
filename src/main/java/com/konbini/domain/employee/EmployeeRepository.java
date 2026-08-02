package com.konbini.domain.employee;

import java.util.List;
import java.util.Optional;

/**
 * Persistence contract for employees.
 */
public interface EmployeeRepository {

    /**
     * Persists a new employee.
     *
     * @param employee the employee to save
     */
    void add(Employee employee);

    /**
     * Finds an employee by ID.
     *
     * @param id the employee ID
     * @return an Optional containing the employee if found
     */
    Optional<Employee> findById(String id);

    /**
     * Returns all employees.
     *
     * @return all employees
     */
    List<Employee> findAll();

    /**
     * Updates an existing employee.
     *
     * @param employee the employee with updated values
     */
    void update(Employee employee);

    /**
     * Removes an employee by ID.
     *
     * @param id the employee ID
     */
    void remove(String id);
}
