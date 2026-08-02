package com.konbini.application.dto;

import com.konbini.domain.employee.Employee;

/**
 * Immutable presentation snapshot of an {@link Employee}.
 * The password hash is never exposed.
 */
public record EmployeeDTO(String id, String name) {

    /**
     * Creates a DTO from a domain employee.
     *
     * @param employee the domain employee
     * @return the DTO snapshot
     */
    public static EmployeeDTO fromDomain(Employee employee) {
        return new EmployeeDTO(employee.getId(), employee.getName());
    }
}
