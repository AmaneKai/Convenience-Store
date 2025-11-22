package com.konbini.view;

import java.util.List;
import com.konbini.dto.EmployeeDTO;

public interface EmployeeView extends BaseView {
    void displayEmployeeMenu();
    int getEmployeeMenuChoice();
    void displayEmployees(List<EmployeeDTO> employees);
    void displayEmployee(EmployeeDTO employee);
}