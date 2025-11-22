package com.konbini.service.impl;

import com.konbini.model.Employee;
import com.konbini.model.repository.EmployeeRepository;
import com.konbini.service.EmployeeService;

import java.util.List;
import java.util.Optional;

public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl (EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void addEmployee (Employee employee) {
        employeeRepository.save(employee);
    }

    @Override
    public Optional<Employee> getEmployeeById (String id) {
        return employeeRepository.findById(id);
    }

    @Override
    public List<Employee> getAllEmployee () {
        return employeeRepository.findAll();
    }

    @Override
    public void updateEmployee (Employee employee) {
        employeeRepository.update(employee);
    }

    @Override
    public void deleteEmployee (String id) {
        employeeRepository.delete(id);
    }

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
    @Override
    public boolean loadEmployees () {
        return employeeRepository.load();
    }

    @Override
    public boolean saveEmployees () {
        return employeeRepository.saveAll();
    }
}
