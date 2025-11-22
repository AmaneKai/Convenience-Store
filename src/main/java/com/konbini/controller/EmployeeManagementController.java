package com.konbini.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.konbini.dto.EmployeeDTO;
import com.konbini.model.Employee;
import com.konbini.service.EmployeeService;
import com.konbini.view.StoreView;

public class EmployeeManagementController {
    private final StoreView view;
    private final EmployeeController employeeController;
    private final EmployeeService employeeService;

    public EmployeeManagementController(
            StoreView view,
            EmployeeController employeeController,
            EmployeeService employeeService) {
        if (view == null || employeeController == null || employeeService == null) {
            throw new IllegalArgumentException("All dependencies must be provided");
        }
        this.view = view;
        this.employeeController = employeeController;
        this.employeeService = employeeService;
    }

    // ==================== PUBLIC HANDLERS ====================

    public void handleViewAllEmployees() {
        try {
            List<Employee> employees = employeeController.getAllEmployees();
            displayEmployeeList(employees);
        } catch (Exception e) {
            handleGenericException(e, "loading employees", "Failed to load employees. Please try again.");
        }
    }

    public void handleViewEmployeeDetails() {
        try {
            Optional<String> employeeId = promptForEmployeeId("view details");
            employeeId.ifPresent(this::showEmployeeDetails);
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "viewing employee details");
        } catch (Exception e) {
            handleGenericException(e, "viewing employee details", "Failed to view employee details. Please try again.");
        }
    }

    public void handleAddEmployee() {
        try {
            String name = view.getStringInput("Enter employee name: ");

            if (name != null && !name.trim().isEmpty()) {
                addNewEmployee(name.trim());
            } else {
                view.displayErrorMessage("Employee name cannot be empty.");
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "adding employee");
        } catch (Exception e) {
            handleGenericException(e, "adding employee", "Failed to add employee. Please try again.");
        }
    }

    public void handleUpdateEmployee() {
        try {
            Optional<String> employeeId = promptForEmployeeId("update");
            employeeId.ifPresent(this::updateExistingEmployee);
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "updating employee");
        } catch (Exception e) {
            handleGenericException(e, "updating employee", "Failed to update employee. Please try again.");
        }
    }

    public void handleRemoveEmployee() {
        try {
            Optional<String> employeeId = promptForEmployeeId("remove");
            employeeId.ifPresent(this::confirmAndRemoveEmployee);
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "removing employee");
        } catch (Exception e) {
            handleGenericException(e, "removing employee", "Failed to remove employee. Please try again.");
        }
    }

    public void handleChangePassword() {
        try {
            Optional<String> employeeId = promptForEmployeeId("change password");
            employeeId.ifPresent(this::changeEmployeePassword);
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "changing password");
        } catch (Exception e) {
            handleGenericException(e, "changing password", "Failed to change password. Please try again.");
        }
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private void displayEmployeeList(List<Employee> employees) {
        try {
            List<EmployeeDTO> employeeDTOs = employees.stream()
                    .map(EmployeeDTO::fromModel)
                    .collect(Collectors.toList());
            view.displayEmployees(employeeDTOs);
        } catch (Exception e) {
            handleGenericException(e, "displaying employee list", "Error displaying employees.");
        }
    }

    private void showEmployeeDetails(String employeeId) {
        try {
            validateEmployeeId(employeeId);
            Optional<Employee> employee = employeeController.getEmployeeById(employeeId);

            if (employee.isPresent()) {
                view.displayEmployee(EmployeeDTO.fromModel(employee.get()));
            } else {
                view.displayErrorMessage("Employee not found.");
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "showing employee details");
        } catch (Exception e) {
            handleGenericException(e, "showing employee details", "Error displaying employee details.");
        }
    }

    private void addNewEmployee(String name) {
        try {
            validateEmployeeName(name);
            String password = view.getStringInput("Enter employee password: ");

            if (password == null || password.trim().isEmpty()) {
                view.displayErrorMessage("Password cannot be empty.");
            } else {
                employeeController.addEmployee(name, password.trim());
                employeeController.saveData();
                view.displaySuccessMessage("Employee added successfully.");
                handleViewAllEmployees();
            }

        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "adding new employee");
        } catch (Exception e) {
            handleGenericException(e, "adding new employee", "Failed to complete employee addition.");
        }
    }

    private void updateExistingEmployee(String employeeId) {
        try {
            validateEmployeeId(employeeId);
            Optional<Employee> employee = employeeController.getEmployeeById(employeeId);

            if (employee.isPresent()) {
                String name = view.getStringInput("Enter new name (leave empty to keep current): ");
                name = (name == null || name.trim().isEmpty()) ? employee.get().getName() : name.trim();

                validateEmployeeName(name);

                Employee updatedEmployee = new Employee(employeeId, name, employee.get().getPassword());
                employeeController.updateEmployee(updatedEmployee);
                employeeController.saveData();
                view.displaySuccessMessage("Employee updated successfully.");
                handleViewAllEmployees();
            } else {
                view.displayErrorMessage("Employee not found.");
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "updating existing employee");
        } catch (Exception e) {
            handleGenericException(e, "updating existing employee", "Failed to update employee.");
        }
    }

    private void confirmAndRemoveEmployee(String employeeId) {
        try {
            validateEmployeeId(employeeId);

            if (view.getBooleanInput("Are you sure you want to remove this employee?")) {
                employeeController.deleteEmployee(employeeId);
                employeeController.saveData();
                view.displaySuccessMessage("Employee removed successfully.");
                handleViewAllEmployees();
            } else {
                view.displayInfoMessage("Operation cancelled.");
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "removing employee");
        } catch (Exception e) {
            handleGenericException(e, "removing employee", "Failed to remove employee.");
        }
    }

    private void changeEmployeePassword(String employeeId) {
        try {
            validateEmployeeId(employeeId);
            Optional<Employee> employee = employeeController.getEmployeeById(employeeId);

            if (employee.isPresent()) {
                String newPassword = view.getStringInput("Enter new password: ");

                if (newPassword == null || newPassword.trim().isEmpty()) {
                    view.displayErrorMessage("Password cannot be empty.");
                } else {
                    Employee updatedEmployee = new Employee(
                            employeeId,
                            employee.get().getName(),
                            newPassword.trim()
                    );
                    employeeController.updateEmployee(updatedEmployee);
                    employeeController.saveData();
                    view.displaySuccessMessage("Password changed successfully.");
                }
            } else {
                view.displayErrorMessage("Employee not found.");
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "changing password");
        } catch (Exception e) {
            handleGenericException(e, "changing password", "Failed to change password.");
        }
    }

    // ==================== VALIDATION & UTILITY METHODS ====================

    private Optional<String> promptForEmployeeId(String operation) {
        Optional<String> temp = Optional.empty();

        try {
            List<Employee> employees = employeeController.getAllEmployees();

            if (employees.isEmpty()) {
                view.displayInfoMessage("No employees available.");
            } else {
                displayEmployeeList(employees);
                String employeeId = view.getStringInput("Enter employee ID: ");

                if (employeeId != null && !employeeId.trim().isEmpty()) {
                    temp = Optional.of(employeeId.trim());
                } else {
                    view.displayInfoMessage("No employee ID provided for " + operation + ".");
                }
            }
        } catch (Exception e) {
            handleGenericException(e, "prompting for employee ID", "Failed to load employee list.");
        }

        return temp;
    }
    private void validateEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }
    }

    private void validateEmployeeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name cannot be null or empty");
        }
    }

    // ==================== ERROR HANDLING HELPERS ====================

    private void handleArgumentException(IllegalArgumentException e, String context) {
        System.err.println("Invalid argument " + context + ": " +
                (e.getMessage() != null ? e.getMessage() : "Unknown"));
        view.displayErrorMessage("Invalid input: " +
                (e.getMessage() != null ? e.getMessage() : "Please check your input and try again."));
    }

    private void handleGenericException(Exception e, String context, String userMessage) {
        System.err.println("Error " + context + ": " + e.getMessage());
        view.displayErrorMessage(userMessage);
    }
}