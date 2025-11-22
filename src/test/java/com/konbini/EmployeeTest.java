package com.konbini;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import com.konbini.model.Employee;
import com.konbini.model.repository.EmployeeRepository;
import com.konbini.model.repository.impl.FileEmployeeRepository;
import com.konbini.service.EmployeeService;
import com.konbini.service.impl.EmployeeServiceImpl;

import java.util.List;
import java.util.Optional;

public class EmployeeTest {

    private EmployeeService employeeService;
    private EmployeeRepository employeeRepository;

    @BeforeEach
    public void setUp() {
        // Create a fresh repository for each test with a test-specific filename
        employeeRepository = new FileEmployeeRepository("employees_test.dat");
        employeeService = new EmployeeServiceImpl(employeeRepository);
    }

    @Test
    public void testEmployeeCreation() {
        Employee employee = new Employee("EMP001", "John Doe", "password123");

        assertNotNull(employee);
        assertEquals("EMP001", employee.getId());
        assertEquals("John Doe", employee.getName());
        assertEquals("password123", employee.getPassword());
    }

    @Test
    public void testEmployeeSetters() {
        Employee employee = new Employee();

        employee.setId("EMP002");
        employee.setName("Jane Smith");
        employee.setPassword("securepass");

        assertEquals("EMP002", employee.getId());
        assertEquals("Jane Smith", employee.getName());
        assertEquals("securepass", employee.getPassword());
    }

    @Test
    public void testEmployeeToString() {
        Employee employee = new Employee("EMP003", "Bob Johnson", "mypassword");
        String result = employee.toString();

        assertTrue(result.contains("EMP003"));
        assertTrue(result.contains("Bob Johnson"));
        assertFalse(result.contains("mypassword"), "Password should not be in toString()");
    }

    @Test
    public void testAuthenticationWithValidCredentials() {
        // Add an employee
        Employee employee = new Employee("EMP004", "Alice Brown", "correctpassword");
        employeeService.addEmployee(employee);

        // Test authentication with correct password
        boolean authenticated = employeeService.authenticate("EMP004", "correctpassword");
        assertTrue(authenticated, "Authentication should succeed with correct credentials");
    }

    @Test
    public void testAuthenticationWithInvalidPassword() {
        // Add an employee
        Employee employee = new Employee("EMP005", "Charlie Davis", "correctpassword");
        employeeService.addEmployee(employee);

        // Test authentication with incorrect password
        boolean authenticated = employeeService.authenticate("EMP005", "wrongpassword");
        assertFalse(authenticated, "Authentication should fail with incorrect password");
    }

    @Test
    public void testAuthenticationWithNonExistentEmployee() {
        // Test authentication for employee that doesn't exist
        boolean authenticated = employeeService.authenticate("NONEXISTENT", "anypassword");
        assertFalse(authenticated, "Authentication should fail for non-existent employee");
    }

    @Test
    public void testAuthenticationWithNullPassword() {
        // Add an employee with null password
        Employee employee = new Employee("EMP006", "David Lee", null);
        employeeService.addEmployee(employee);

        // Test authentication with null password in employee record
        boolean authenticated = employeeService.authenticate("EMP006", "anypassword");
        assertFalse(authenticated, "Authentication should fail when employee has null password");
    }

    @Test
    public void testAuthenticationWithEmptyPassword() {
        // Add an employee with empty password
        Employee employee = new Employee("EMP007", "Eva Martinez", "");
        employeeService.addEmployee(employee);

        // Test authentication with empty password
        boolean authenticated = employeeService.authenticate("EMP007", "");
        assertTrue(authenticated, "Authentication should succeed with matching empty password");

        // Test authentication with non-empty password when employee has empty password
        boolean authenticatedWrong = employeeService.authenticate("EMP007", "somepassword");
        assertFalse(authenticatedWrong, "Authentication should fail with non-matching password");
    }

    @Test
    public void testAddEmployee() {
        Employee employee = new Employee("EMP008", "Frank Wilson", "password123");
        employeeService.addEmployee(employee);

        // Verify employee was added
        Optional<Employee> retrieved = employeeService.getEmployeeById("EMP008");
        assertTrue(retrieved.isPresent(), "Employee should be found after adding");
        assertEquals("Frank Wilson", retrieved.get().getName());
    }

    @Test
    public void testGetEmployeeById() {
        // Add an employee
        Employee employee = new Employee("EMP009", "Grace Taylor", "mypassword");
        employeeService.addEmployee(employee);

        // Retrieve employee
        Optional<Employee> retrieved = employeeService.getEmployeeById("EMP009");

        assertTrue(retrieved.isPresent());
        assertEquals("EMP009", retrieved.get().getId());
        assertEquals("Grace Taylor", retrieved.get().getName());
    }

    @Test
    public void testGetEmployeeByIdNotFound() {
        Optional<Employee> retrieved = employeeService.getEmployeeById("NOTFOUND");
        assertFalse(retrieved.isPresent(), "Should return empty Optional for non-existent employee");
    }

    @Test
    public void testGetAllEmployees() {
        // Add multiple employees
        employeeService.addEmployee(new Employee("EMP010", "Henry Clark", "pass1"));
        employeeService.addEmployee(new Employee("EMP011", "Iris Rodriguez", "pass2"));
        employeeService.addEmployee(new Employee("EMP012", "Jack Anderson", "pass3"));

        // Get all employees
        List<Employee> employees = employeeService.getAllEmployee();

        assertTrue(employees.size() >= 3, "Should have at least 3 employees");

        // Verify our employees are in the list
        boolean hasEMP010 = employees.stream().anyMatch(e -> "EMP010".equals(e.getId()));
        boolean hasEMP011 = employees.stream().anyMatch(e -> "EMP011".equals(e.getId()));
        boolean hasEMP012 = employees.stream().anyMatch(e -> "EMP012".equals(e.getId()));

        assertTrue(hasEMP010);
        assertTrue(hasEMP011);
        assertTrue(hasEMP012);
    }

    @Test
    public void testUpdateEmployee() {
        // Add an employee
        Employee employee = new Employee("EMP013", "Katie White", "oldpassword");
        employeeService.addEmployee(employee);

        // Update employee
        employee.setName("Katie White-Smith");
        employee.setPassword("newpassword");
        employeeService.updateEmployee(employee);

        // Verify update
        Optional<Employee> updated = employeeService.getEmployeeById("EMP013");
        assertTrue(updated.isPresent());
        assertEquals("Katie White-Smith", updated.get().getName());

        // Verify authentication with new password
        assertTrue(employeeService.authenticate("EMP013", "newpassword"));
        assertFalse(employeeService.authenticate("EMP013", "oldpassword"));
    }

    @Test
    public void testDeleteEmployee() {
        // Add an employee
        Employee employee = new Employee("EMP014", "Leo Harris", "password");
        employeeService.addEmployee(employee);

        // Verify employee exists
        assertTrue(employeeService.getEmployeeById("EMP014").isPresent());

        // Delete employee
        employeeService.deleteEmployee("EMP014");

        // Verify employee was deleted
        assertFalse(employeeService.getEmployeeById("EMP014").isPresent());

        // Verify authentication fails after deletion
        assertFalse(employeeService.authenticate("EMP014", "password"));
    }

    @Test
    public void testCaseSensitivePassword() {
        // Add an employee
        Employee employee = new Employee("EMP015", "Mia Thompson", "MyPassword123");
        employeeService.addEmployee(employee);

        // Test case-sensitive password matching
        assertTrue(employeeService.authenticate("EMP015", "MyPassword123"));
        assertFalse(employeeService.authenticate("EMP015", "mypassword123"));
        assertFalse(employeeService.authenticate("EMP015", "MYPASSWORD123"));
    }

    @Test
    public void testPasswordWithSpecialCharacters() {
        // Add an employee with special characters in password
        Employee employee = new Employee("EMP016", "Noah Garcia", "p@ssw0rd!#$%");
        employeeService.addEmployee(employee);

        // Test authentication
        assertTrue(employeeService.authenticate("EMP016", "p@ssw0rd!#$%"));
        assertFalse(employeeService.authenticate("EMP016", "password"));
    }

    @Test
    public void testPasswordWithSpaces() {
        // Add an employee with spaces in password
        Employee employee = new Employee("EMP017", "Olivia Martinez", "my password 123");
        employeeService.addEmployee(employee);

        // Test authentication
        assertTrue(employeeService.authenticate("EMP017", "my password 123"));
        assertFalse(employeeService.authenticate("EMP017", "mypassword123"));
    }
}
