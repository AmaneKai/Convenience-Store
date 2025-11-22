package com.konbini.dto;

import com.konbini.model.Employee;

import java.util.List;
import java.util.ArrayList;

public class EmployeeDTO {
    private String id;
    private String name;
    private String password;

    public EmployeeDTO() {}

    public EmployeeDTO (Employee employee) {
        if (employee == null)
            throw new IllegalArgumentException("Employee cannot be null");

        this.id = employee.getId();
        this.name = employee.getName();
        this.password = employee.getPassword();
    }

    public static EmployeeDTO fromModel(Employee employee) {
        return new EmployeeDTO(employee);
    }

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
    public Employee toModel() {
        return new Employee(id, name, password);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "EmployeeDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
