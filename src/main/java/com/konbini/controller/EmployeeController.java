package com.konbini.controller;

import com.konbini.model.Employee;
import com.konbini.service.EmployeeService;
import com.konbini.util.IdGenerator;

import java.util.*;

/**
 * Controller for managing employee operations including adding, retrieving,
 * updating, deleting employees, and handling authentication.
 */
public class EmployeeController {
    private final EmployeeService employeeService;

    /**
     * Constructs an EmployeeController with the specified employee service.
     *
     * @param employeeService the service for employee operations
     */
    public EmployeeController (EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Adds a new employee with auto-generated ID.
     *
     * @param name the employee's name
     * @param password the employee's password
     */
    public void addEmployee (String name, String password) {
       String id = IdGenerator.getInstance().generateId("employee");
       Employee employee = new Employee (id, name, password);
       employeeService.addEmployee(employee);
    }

    /**
     * Retrieves an employee by their unique identifier.
     *
     * @param id the ID of the employee to find
     * @return an Optional containing the employee if found, empty otherwise
     */
    public Optional<Employee> getEmployeeById (String id) {
        return employeeService.getEmployeeById(id);
    }

    /**
     * Retrieves all employees in the system.
     *
     * @return a list of all employees
     */
    public List<Employee> getAllEmployees () {
        return employeeService.getAllEmployee();
    }

    /**
     * Updates an existing employee's information.
     *
     * @param employee the employee object with updated information
     */
    public void updateEmployee (Employee employee) {
        employeeService.updateEmployee(employee);
    }

    /**
     * Deletes an employee from the system.
     *
     * @param id the ID of the employee to delete
     */
    public void deleteEmployee (String id) {
        employeeService.deleteEmployee(id);
    }

    /**
     * Authenticates an employee using their ID and password.
     *
     * @param id the employee's ID
     * @param password the employee's password
     * @return true if authentication is successful, false otherwise
     */
    public boolean authenticate (String id, String password) {
        return employeeService.authenticate(id, password);
    }

    /**
     * Loads all employee data from persistent storage.
     *
     * @return true if load operation was successful, false otherwise
     */
    public boolean loadData () {
        return employeeService.loadEmployees();
    }

    /**
     * Saves all employee data to persistent storage.
     *
     * @return true if save operation was successful, false otherwise
     */
    public boolean saveData () {
        return employeeService.saveEmployees();
    }
}