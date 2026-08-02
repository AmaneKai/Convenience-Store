package com.konbini.infrastructure.config;

import java.nio.file.Path;

/**
 * Central configuration for the file-backed store: the data directory and the
 * CSV file locations derived from it.
 */
public class StoreConfig {

    private final Path dataDir;

    /**
     * Constructs the store configuration.
     *
     * @param dataDir the directory holding all data files
     */
    public StoreConfig(Path dataDir) {
        this.dataDir = dataDir;
    }

    /**
     * Returns the data directory.
     *
     * @return the data directory path
     */
    public Path getDataDir() {
        return dataDir;
    }

    /**
     * Returns the products CSV file.
     *
     * @return the products file path
     */
    public Path productsFile() {
        return dataDir.resolve("products.csv");
    }

    /**
     * Returns the customers CSV file.
     *
     * @return the customers file path
     */
    public Path customersFile() {
        return dataDir.resolve("customers.csv");
    }

    /**
     * Returns the employees CSV file.
     *
     * @return the employees file path
     */
    public Path employeesFile() {
        return dataDir.resolve("employees.csv");
    }

    /**
     * Returns the transactions CSV file.
     *
     * @return the transactions file path
     */
    public Path transactionsFile() {
        return dataDir.resolve("transactions.csv");
    }

    /**
     * Returns the identifier counter file.
     *
     * @return the id counters file path
     */
    public Path idCountersFile() {
        return dataDir.resolve("id_counters.csv");
    }
}
