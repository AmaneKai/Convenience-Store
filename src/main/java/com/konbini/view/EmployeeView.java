package com.konbini.view;

import java.util.List;
import com.konbini.dto.EmployeeDTO;

/**
 * View interface for employee management operations and display.
 * Extends BaseView to provide employee-specific user interactions
 * including employee menu display and employee information visualization.
 */
public interface EmployeeView extends BaseView {

    /**
     * Displays the employee management menu to the user.
     * Typically includes options for viewing, adding, updating,
     * removing employees, and managing authentication.
     */
    void displayEmployeeMenu();

    /**
     * Gets the user's selection from the employee management menu.
     *
     * @return the user's menu choice as an integer
     */
    int getEmployeeMenuChoice();

    /**
     * Displays a list of employees to the user.
     * Shows employee information in a list format, typically with
     * summary details for each employee.
     *
     * @param employees the list of EmployeeDTO objects to display
     */
    void displayEmployees(List<EmployeeDTO> employees);

    /**
     * Displays detailed information for a single employee.
     * Shows comprehensive employee details including identification
     * information and authentication status.
     *
     * @param employee the EmployeeDTO containing detailed employee information to display
     */
    void displayEmployee(EmployeeDTO employee);
}