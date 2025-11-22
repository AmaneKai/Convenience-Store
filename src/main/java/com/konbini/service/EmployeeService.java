package com.konbini.service;

import com.konbini.model.Employee;
import java.util.*;

public interface EmployeeService {
    void addEmployee (Employee employee);
    Optional<Employee> getEmployeeById (String id);
    List<Employee> getAllEmployee ();
    void updateEmployee (Employee employee);
    void deleteEmployee (String id);
    boolean authenticate (String id, String password);
    boolean loadEmployees ();
    boolean saveEmployees ();
}
