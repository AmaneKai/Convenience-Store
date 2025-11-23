package com.konbini.util;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class for generating unique identifiers for various entities.
 * Maintains persistent counters for different entity types and generates
 * formatted IDs with entity prefixes and sequential numbers.
 * Implements Serializable to support persistence of counter state.
 */
public class IdGenerator implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * File path for storing ID counter state.
     */
    private static final String ID_FILE = "data/id_counters.dat";

    private static IdGenerator instance;

    /**
     * Synchronized map storing counters for different entity types.
     */
    private Map<String, Integer> counters;

    /**
     * Private constructor for singleton pattern.
     * Initializes synchronized counters map.
     */
    private IdGenerator() {
        counters = Collections.synchronizedMap(new HashMap<>());
    }

    /**
     * Gets the singleton instance of IdGenerator.
     * Loads from file if available, otherwise creates new instance.
     *
     * @return the singleton IdGenerator instance
     */
    public static synchronized IdGenerator getInstance() {
        if (instance == null) {
            instance = loadFromFile();
        }
        return instance;
    }

    /**
     * Loads IdGenerator state from file or creates new instance if file doesn't exist.
     *
     * @return loaded or new IdGenerator instance
     */
    private static IdGenerator loadFromFile() {
        IdGenerator temp;
        File file = new File(ID_FILE);

        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream
                    (new FileInputStream(file))) {
                temp = (IdGenerator) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading ID generator: "
                        + e.getMessage());
                temp = new IdGenerator();
                temp.save(); // Save the newly created default state
            }
        } else {
            temp = new IdGenerator();
            temp.save(); // Save the newly created default state
        }

        return temp;
    }

    /**
     * Saves the current IdGenerator state to file.
     * Ensures data directory exists before saving.
     */
    public void save() {
        FileUtil.ensureDataDirectory();
        try (ObjectOutputStream oos = new ObjectOutputStream
                (new FileOutputStream(ID_FILE))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Error saving ID generator: " + e.getMessage());
        }
    }

    /**
     * Generates a unique identifier for the specified entity type.
     * Format: [3-letter prefix][4-digit sequential number] (e.g., "CUS0001")
     *
     * @param entityType the type of entity (must be at least 3 characters)
     * @return the generated unique ID
     * @throws IllegalArgumentException if entityType is null or less than 3 characters
     */
    public synchronized String generateId(String entityType) {

        if (entityType == null || entityType.length() < 3) {
            throw new IllegalArgumentException(
                    "Entity type must be at least 3 characters, got: " + entityType);
        }

        int counter = counters.getOrDefault(entityType, 1);
        counters.put(entityType, counter + 1);
        save();
        return entityType.substring(0, 3).toUpperCase()
                + String.format("%04d", counter);
    }
}