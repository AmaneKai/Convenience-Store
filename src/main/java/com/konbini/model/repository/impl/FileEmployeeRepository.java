package com.konbini.model.repository.impl;

import com.konbini.model.*;
import com.konbini.model.repository.*;
import com.konbini.util.FileUtil;

import java.io.*;
import java.util.*;

public class FileEmployeeRepository implements  EmployeeRepository {
    private final String filename;
    private List <Employee> employees;

    public FileEmployeeRepository (String filename) {
        if (filename == null || filename.trim().isEmpty())
            throw new IllegalArgumentException("Filename cannot be null or empty");

        this.filename = filename;
        this.employees = new ArrayList<>();
    }

    @Override
    public void save (Employee employee) {
        if (employees == null)
            throw new IllegalArgumentException();

        Optional<Employee> existing = findById(employee.getId());

        if (existing.isPresent())
            update(employee);
        else
            employees.add(employee);
    }

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

    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(employees);
    }

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

    @Override
    public void delete(String id) {
        if (id == null || id.trim().isEmpty())
            throw new IllegalArgumentException("Employee ID cannot be null or empty");

        boolean removed = employees.removeIf(emp -> emp.getId().equals(id));
        if (!removed)
            throw new IllegalArgumentException("Employee not found: " + id);
    }

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

