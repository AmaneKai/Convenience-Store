package com.konbini.infrastructure.repository;

import com.konbini.domain.employee.Employee;
import com.konbini.domain.employee.EmployeeRepository;
import com.konbini.infrastructure.config.StoreConfig;
import com.konbini.infrastructure.csv.CsvStore;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CSV-backed implementation of {@link EmployeeRepository}. Only the salted
 * password hash is persisted; plaintext passwords are never stored.
 */
public class CsvEmployeeRepository implements EmployeeRepository {

    private static final List<String> COLUMNS = List.of("id", "name", "passwordHash");

    private final CsvStore csvStore;
    private final java.nio.file.Path file;
    private final Map<String, Employee> employees = new LinkedHashMap<>();

    /**
     * Constructs the repository and loads existing data.
     *
     * @param csvStore the CSV store
     * @param config the store configuration
     */
    public CsvEmployeeRepository(CsvStore csvStore, StoreConfig config) {
        this.csvStore = csvStore;
        this.file = config.employeesFile();
        load();
    }

    /**
     * Persists all employees to the CSV file.
     */
    public void save() {
        List<Map<String, String>> rows = employees.values().stream()
                .map(this::toRow)
                .collect(Collectors.toList());
        csvStore.writeAll(file, COLUMNS, rows);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(Employee employee) {
        employees.put(employee.getId(), employee);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Employee> findById(String id) {
        return Optional.ofNullable(employees.get(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(employees.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Employee employee) {
        employees.put(employee.getId(), employee);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(String id) {
        employees.remove(id);
    }

    /**
     * Loads all employees from the CSV file.
     */
    private void load() {
        employees.clear();
        for (Map<String, String> row : csvStore.readAll(file, COLUMNS)) {
            String id = row.get("id");
            if (id == null || id.trim().isEmpty()) {
                continue;
            }
            Employee employee = Employee.builder()
                    .id(id)
                    .name(row.getOrDefault("name", ""))
                    .passwordHash(row.getOrDefault("passwordHash", ""))
                    .build();
            employees.put(employee.getId(), employee);
        }
    }

    /**
     * Converts an employee to a row map.
     *
     * @param employee the employee
     * @return the row
     */
    private Map<String, String> toRow(Employee employee) {
        Map<String, String> row = new LinkedHashMap<>();
        row.put("id", employee.getId());
        row.put("name", employee.getName());
        row.put("passwordHash", employee.getPasswordHash());
        return row;
    }
}
