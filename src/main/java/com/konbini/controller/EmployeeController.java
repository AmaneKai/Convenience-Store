package com.konbini.controller;

import com.konbini.model.Employee;
import com.konbini.service.EmployeeService;
import com.konbini.util.IdGenerator;

import java.util.*;

public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController (EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public void addEmployee (String name, String password) {
       String id = IdGenerator.getInstance().generateId("employee");
       Employee employee = new Employee (id, name, password);
       employeeService.addEmployee(employee);
    }

    public Optional<Employee> getEmployeeById (String id) {
        return employeeService.getEmployeeById(id);
    }

    public List<Employee> getAllEmployees () {
        return employeeService.getAllEmployee();
    }

    public void updateEmployee (Employee employee) {
        employeeService.updateEmployee(employee);
    }

    public void deleteEmployee (String id) {
        employeeService.deleteEmployee(id);
    }

    public boolean authenticate (String id, String password) {
        return employeeService.authenticate(id, password);
    }

    public boolean loadData () {
        return employeeService.loadEmployees();
    }

    public boolean saveData () {
        return employeeService.saveEmployees();
    }
}
