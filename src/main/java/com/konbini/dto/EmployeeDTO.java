package com.konbini.dto;

import com.konbini.model.Employee;

import java.util.List;
import java.util.ArrayList;

/**
 * Data Transfer Object (DTO) for representing employee information.
 * Used to transfer employee data between layers without exposing the domain model.
 * Contains employee identification and authentication details.
 */
public class EmployeeDTO {
    private String id;
    private String name;
    private String password;

    /**
     * Default constructor for creating an empty EmployeeDTO.
     */
    public EmployeeDTO() {}

    /**
     * Constructs an EmployeeDTO from an Employee domain model object.
     * Extracts employee identification and authentication information.
     *
     * @param employee the Employee domain model to convert to DTO
     * @throws IllegalArgumentException if employee is null
     */
    public EmployeeDTO (Employee employee) {
        if (employee == null)
            throw new IllegalArgumentException("Employee cannot be null");

        this.id = employee.getId();
        this.name = employee.getName();
        this.password = employee.getPassword();
    }

    /**
     * Static factory method to create an EmployeeDTO from an Employee domain model.
     *
     * @param employee the Employee domain model to convert
     * @return a new EmployeeDTO instance representing the employee
     */
    public static EmployeeDTO fromModel(Employee employee) {
        return new EmployeeDTO(employee);
    }

    /**
     * Converts a list of Employee domain models to a list of EmployeeDTOs.
     * Safely handles null lists and null employee objects.
     *
     * @param employees the list of Employee domain models to convert
     * @return a list of EmployeeDTO objects, empty if input is null
     */
    public static List<EmployeeDTO> fromModelList(List<Employee> employees) {
        List<EmployeeDTO> dtos = new ArrayList<>();

        if (employees != null) {
            for (Employee employee : employees) {
                if (employee != null) {
                    dtos.add(fromModel(employee));
                }
            }
        }

        return dtos;
    }

    /**
     * Converts this DTO back to an Employee domain model.
     * Creates a new Employee instance with the DTO's data.
     *
     * @return a new Employee domain model with the DTO's data
     */
    public Employee toModel() {
        return new Employee(id, name, password);
    }

    /**
     * Gets the employee's unique identifier.
     *
     * @return the employee ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the employee's unique identifier.
     *
     * @param id the employee ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the employee's name.
     *
     * @return the employee name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the employee's name.
     *
     * @param name the employee name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the employee's password.
     * Note: In a production system, consider security implications of exposing passwords.
     *
     * @return the employee password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the employee's password.
     *
     * @param password the employee password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns a string representation of the EmployeeDTO.
     * Note: Password is intentionally excluded from the string representation for security.
     *
     * @return a string containing employee ID and name
     */
    @Override
    public String toString() {
        return "EmployeeDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}