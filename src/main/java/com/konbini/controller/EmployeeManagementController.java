package com.konbini.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.konbini.dto.EmployeeDTO;
import com.konbini.model.Employee;
import com.konbini.service.EmployeeService;
import com.konbini.view.EmployeeView;

/**
 * Controller for managing employee operations including viewing, adding, updating,
 * removing employees, and password management. Coordinates between the view
 * and employee service layer.
 */
public class EmployeeManagementController {
    private final EmployeeView view;
    private final EmployeeController employeeController;
    private final EmployeeService employeeService;

    /**
     * Constructs an EmployeeManagementController with all required dependencies.
     *
     * @param view the store view for user interface interactions
     * @param employeeController controller for employee operations
     * @param employeeService service for employee validation and business logic
     * @throws IllegalArgumentException if any dependency is null
     */
    public EmployeeManagementController(
            EmployeeView view,
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

    /**
     * Handles displaying all employees in the system.
     * Catches and handles any exceptions during the loading process.
     */
    public void handleViewAllEmployees() {
        try {
            List<Employee> employees = employeeController.getAllEmployees();
            displayEmployeeList(employees);
        } catch (Exception e) {
            handleGenericException(e, "loading employees", "Failed to load employees. Please try again.");
        }
    }

    /**
     * Handles viewing detailed information for a specific employee.
     * Prompts for employee ID and displays employee details if found.
     */
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

    /**
     * Handles adding a new employee to the system.
     * Prompts for employee name and password.
     */
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

    /**
     * Handles updating an existing employee's information.
     * Prompts for employee ID and allows updating the name.
     */
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

    /**
     * Handles removing an employee from the system.
     * Prompts for employee ID and requires confirmation before removal.
     */
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

    /**
     * Handles changing an employee's password.
     * Prompts for employee ID and new password.
     */
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

    /**
     * Displays a list of employees in the view.
     *
     * @param employees the list of employees to display
     */
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

    /**
     * Shows detailed information for a specific employee.
     *
     * @param employeeId the ID of the employee to display
     */
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

    /**
     * Adds a new employee with the specified name and password.
     *
     * @param name the name of the employee to add
     */
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

    /**
     * Updates an existing employee's information.
     *
     * @param employeeId the ID of the employee to update
     */
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

    /**
     * Confirms and removes an employee from the system.
     *
     * @param employeeId the ID of the employee to remove
     */
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

    /**
     * Changes an employee's password.
     *
     * @param employeeId the ID of the employee whose password to change
     */
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

    /**
     * Prompts the user to select an employee ID for an operation.
     *
     * @param operation the operation being performed (for context in messages)
     * @return an Optional containing the employee ID if provided, empty otherwise
     */
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

    /**
     * Validates that an employee ID is not null or empty.
     *
     * @param employeeId the employee ID to validate
     * @throws IllegalArgumentException if the employee ID is invalid
     */
    private void validateEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }
    }

    /**
     * Validates that an employee name is not null or empty.
     *
     * @param name the employee name to validate
     * @throws IllegalArgumentException if the employee name is invalid
     */
    private void validateEmployeeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name cannot be null or empty");
        }
    }

    // ==================== ERROR HANDLING HELPERS ====================

    /**
     * Handles IllegalArgumentException by logging and displaying user-friendly error message.
     *
     * @param e the exception that occurred
     * @param context the context where the exception occurred
     */
    private void handleArgumentException(IllegalArgumentException e, String context) {
        System.err.println("Invalid argument " + context + ": " +
                (e.getMessage() != null ? e.getMessage() : "Unknown"));
        view.displayErrorMessage("Invalid input: " +
                (e.getMessage() != null ? e.getMessage() : "Please check your input and try again."));
    }

    /**
     * Handles generic exceptions by logging and displaying user-friendly error message.
     *
     * @param e the exception that occurred
     * @param context the context where the exception occurred
     * @param userMessage the message to display to the user
     */
    private void handleGenericException(Exception e, String context, String userMessage) {
        System.err.println("Error " + context + ": " + e.getMessage());
        view.displayErrorMessage(userMessage);
    }
}