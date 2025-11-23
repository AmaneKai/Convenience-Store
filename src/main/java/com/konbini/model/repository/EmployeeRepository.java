package com.konbini.model.repository;

import com.konbini.model.Employee;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for employee data persistence operations.
 * Defines the contract for storing, retrieving, updating, and deleting employee records.
 * Supports both individual employee operations and bulk data persistence.
 */
public interface EmployeeRepository {

    /**
     * Saves a single employee to the repository.
     *
     * @param employee the employee to save
     */
    void save (Employee employee);

    /**
     * Finds an employee by their unique identifier.
     *
     * @param id the employee ID to search for
     * @return an Optional containing the employee if found, empty otherwise
     */
    Optional<Employee> findById (String id);

    /**
     * Retrieves all employees from the repository.
     *
     * @return a list of all employees, empty list if no employees exist
     */
    List<Employee> findAll();

    /**
     * Updates an existing employee's information in the repository.
     *
     * @param employee the employee with updated information
     */
    void update (Employee employee);

    /**
     * Deletes an employee from the repository by their ID.
     *
     * @param id the ID of the employee to delete
     */
    void delete (String id);

    /**
     * Saves all current employee data to persistent storage.
     * Typically used for batch operations or application shutdown.
     *
     * @return true if the save operation was successful, false otherwise
     */
    boolean saveAll ();

    /**
     * Loads employee data from persistent storage into the repository.
     * Typically used during application startup to restore saved state.
     *
     * @return true if the load operation was successful, false otherwise
     */
    boolean load ();
}