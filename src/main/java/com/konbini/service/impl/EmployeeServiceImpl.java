package com.konbini.service.impl;

import com.konbini.model.Employee;
import com.konbini.model.repository.EmployeeRepository;
import com.konbini.service.EmployeeService;

import java.util.List;
import java.util.Optional;

/**
 * EmployeeServiceImpl provides business logic implementation for employee management operations.
 * This service handles employee CRUD operations, authentication, and data persistence
 * while delegating data access to the employee repository.
 */
public class EmployeeServiceImpl implements EmployeeService {
    /** Repository for employee data persistence operations */
    private final EmployeeRepository employeeRepository;

    /**
     * Constructs a new EmployeeServiceImpl with the specified employee repository.
     *
     * @param employeeRepository the EmployeeRepository for data access operations
     */
    public EmployeeServiceImpl (EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Adds a new employee to the system.
     * Delegates the save operation to the repository.
     *
     * @param employee the Employee object to add
     */
    @Override
    public void addEmployee (Employee employee) {
        employeeRepository.save(employee);
    }

    /**
     * Retrieves an employee by their ID.
     *
     * @param id the ID of the employee to find
     * @return an Optional containing the Employee if found, empty Optional otherwise
     */
    @Override
    public Optional<Employee> getEmployeeById (String id) {
        return employeeRepository.findById(id);
    }

    /**
     * Retrieves all employees from the system.
     *
     * @return a List containing all Employee objects
     */
    @Override
    public List<Employee> getAllEmployee () {
        return employeeRepository.findAll();
    }

    /**
     * Updates an existing employee's information.
     * Delegates the update operation to the repository.
     *
     * @param employee the Employee object with updated information
     */
    @Override
    public void updateEmployee (Employee employee) {
        employeeRepository.update(employee);
    }

    /**
     * Deletes an employee from the system by ID.
     * Delegates the delete operation to the repository.
     *
     * @param id the ID of the employee to delete
     */
    @Override
    public void deleteEmployee (String id) {
        employeeRepository.delete(id);
    }

    /**
     * Authenticates an employee using their ID and password.
     * Compares the provided password with the stored employee password.
     *
     * @param id the employee ID to authenticate
     * @param password the password to verify
     * @return true if authentication is successful, false otherwise
     */
    @Override
    public boolean authenticate(String id, String password) {
        boolean temp = false;
        Optional<Employee> employee = employeeRepository.findById(id);

        if (employee.isPresent()) {
            String employeePassword = employee.get().getPassword();

            if (employeePassword != null) {
                temp = employeePassword.equals(password);
            }
        }

        return temp;
    }

    /**
     * Loads all employee data from persistent storage.
     * Delegates the load operation to the repository.
     *
     * @return true if the load operation was successful, false otherwise
     */
    @Override
    public boolean loadEmployees () {
        return employeeRepository.load();
    }

    /**
     * Saves all employee data to persistent storage.
     * Delegates the save operation to the repository.
     *
     * @return true if the save operation was successful, false otherwise
     */
    @Override
    public boolean saveEmployees () {
        return employeeRepository.saveAll();
    }
}