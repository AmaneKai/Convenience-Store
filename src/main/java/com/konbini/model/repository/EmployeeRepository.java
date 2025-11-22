package com.konbini.model.repository;

import com.konbini.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    void save (Employee employees);
    Optional<Employee> findById (String id);
    List<Employee> findAll();
    void update (Employee employee);
    void delete (String id);
    boolean saveAll ();
    boolean load ();
}
