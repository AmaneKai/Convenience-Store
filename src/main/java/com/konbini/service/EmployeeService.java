package com.konbini.service;

import com.konbini.model.Employee;
import java.util.*;

/**
 * Service interface for employee management and authentication operations.
 * Handles employee data management, authentication, and persistence.
 */
public interface EmployeeService {

    /**
     * Adds a new employee to the system.
     *
     * @param employee the employee to add
     */
    void addEmployee (Employee employee);

    /**
     * Retrieves an employee by their unique identifier.
     *
     * @param id the employee ID to search for
     * @return an Optional containing the employee if found, empty otherwise
     */
    Optional<Employee> getEmployeeById (String id);

    /**
     * Retrieves all employees in the system.
     *
     * @return a list of all employees, empty list if no employees exist
     */
    List<Employee> getAllEmployee ();

    /**
     * Updates an existing employee's information.
     *
     * @param employee the employee with updated information
     */
    void updateEmployee (Employee employee);

    /**
     * Deletes an employee from the system.
     *
     * @param id the ID of the employee to delete
     */
    void deleteEmployee (String id);

    /**
     * Authenticates an employee using their ID and password.
     *
     * @param id the employee's ID
     * @param password the employee's password
     * @return true if authentication is successful, false otherwise
     */
    boolean authenticate (String id, String password);

    /**
     * Loads employee data from persistent storage.
     *
     * @return true if the load operation was successful, false otherwise
     */
    boolean loadEmployees ();

    /**
     * Saves all employee data to persistent storage.
     *
     * @return true if the save operation was successful, false otherwise
     */
    boolean saveEmployees ();
}