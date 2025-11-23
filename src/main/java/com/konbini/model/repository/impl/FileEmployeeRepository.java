package com.konbini.model.repository.impl;

import com.konbini.model.*;
import com.konbini.model.repository.*;
import com.konbini.util.FileUtil;

import java.io.*;
import java.util.*;

/**
 * FileEmployeeRepository provides a file-based implementation of the EmployeeRepository interface.
 * This implementation stores employee data in a serialized file format and maintains an in-memory
 * list of employee objects. It supports basic CRUD operations and automatic file persistence.
 */
public class FileEmployeeRepository implements EmployeeRepository {
    /** The filename where employee data is stored within the data directory */
    private final String filename;

    /** In-memory list of employees */
    private List<Employee> employees;

    /**
     * Constructs a new FileEmployeeRepository with the specified filename.
     * Initializes the in-memory employee list and uses the application's data directory.
     *
     * @param filename the filename where employee data will be stored and loaded from
     * @throws IllegalArgumentException if filename is null or empty
     */
    public FileEmployeeRepository(String filename) {
        if (filename == null || filename.trim().isEmpty())
            throw new IllegalArgumentException("Filename cannot be null or empty");

        this.filename = filename;
        this.employees = new ArrayList<>();
    }

    /**
     * Saves an employee to the repository.
     * If the employee already exists (based on ID), it updates the existing record.
     * If the employee is new, it adds them to the repository.
     *
     * @param employee the Employee object to save
     * @throws IllegalArgumentException if employee is null
     */
    @Override
    public void save(Employee employee) {
        if (employees == null)
            throw new IllegalArgumentException();

        Optional<Employee> existing = findById(employee.getId());

        if (existing.isPresent())
            update(employee);
        else
            employees.add(employee);
    }

    /**
     * Finds an employee by their ID.
     *
     * @param id the ID of the employee to find
     * @return an Optional containing the Employee if found, empty Optional otherwise
     */
    @Override
    public Optional<Employee> findById(String id) {
        Optional<Employee> temp = Optional.empty();

        if (id != null && !id.trim().isEmpty()) {
            temp = employees.stream()
                    .filter(emp -> emp.getId().equals(id))
                    .findFirst();
        }

        return temp;
    }

    /**
     * Retrieves all employees from the repository.
     *
     * @return a List containing all Employee objects in the repository
     */
    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(employees);
    }

    /**
     * Updates an existing employee in the repository.
     * Replaces the employee with the same ID in the in-memory list.
     *
     * @param employee the Employee object with updated information
     * @throws IllegalArgumentException if employee is null or not found in repository
     */
    @Override
    public void update(Employee employee) {
        if (employee == null)
            throw new IllegalArgumentException("Employee cannot be null");

        int j = 0;
        boolean found = false;

        while (j < employees.size() && !found) {
            Employee emp = employees.get(j);
            if (emp != null && emp.getId() != null && emp.getId().equals(employee.getId())) {
                employees.set(j, employee);
                found = true;
            }
            j++;
        }

        if (!found)
            throw new IllegalArgumentException("Employee not found: " + employee.getId());
    }

    /**
     * Deletes an employee from the repository by ID.
     * Removes the employee from the in-memory list.
     *
     * @param id the ID of the employee to delete
     * @throws IllegalArgumentException if id is null, empty, or employee not found
     */
    @Override
    public void delete(String id) {
        if (id == null || id.trim().isEmpty())
            throw new IllegalArgumentException("Employee ID cannot be null or empty");

        boolean removed = employees.removeIf(emp -> emp.getId().equals(id));
        if (!removed)
            throw new IllegalArgumentException("Employee not found: " + id);
    }

    /**
     * Saves all employee data to the file system.
     * Serializes the current in-memory employee list to the data directory.
     * Ensures the data directory exists before attempting to save.
     *
     * @return true if the save operation was successful, false otherwise
     */
    @Override
    public boolean saveAll() {
        boolean temp = false;
        String directoryPath = FileUtil.ensureDataDirectory();
        String filePath = directoryPath + File.separator + filename;

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath))) {
            oos.writeObject(employees);
            System.out.println("Employees saved successfully to: " + filePath);
            temp = true;
        } catch (IOException e) {
            System.err.println("Error saving employees: " + e.getMessage());
            e.printStackTrace();
        }

        return temp;
    }

    /**
     * Loads employee data from the file system.
     * Deserializes employee data from the data directory into the in-memory list.
     * If the file doesn't exist, the operation fails silently and returns false.
     *
     * @return true if the load operation was successful, false otherwise
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean load() {
        boolean temp = false;
        String directoryPath = FileUtil.ensureDataDirectory();
        String filePath = directoryPath + File.separator + filename;
        File file = new File(filePath);

        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(file))) {
                employees = (List<Employee>) ois.readObject();
                System.out.println("Loaded " + employees.size() + " employees from: " + filePath);
                temp = true;
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading employees: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Employee file not found: " + filePath);
        }

        return temp;
    }
}