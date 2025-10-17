package com.konbini.util;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class responsible for generating unique, formatted sequential IDs
 * for different entity types (e.g., Customer, Product, Transaction).
 * <p>
 * This class implements the **Singleton pattern** to ensure only one instance
 * manages the ID counters and uses Java serialization to **persist** the
 * current counter values across application restarts.
 * </p>
 */
public class IdGenerator implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * The file path where the ID counter data is persistently stored using serialization.
     */
    private static final String ID_FILE = "data/id_counters.dat";
    
    /**
     * The single instance of the IdGenerator (Singleton pattern).
     */
    private static IdGenerator instance;
    
    /**
     * A map storing the next sequential ID counter for each registered entity type (prefix).
     * The map is synchronized for thread-safe access.
     */
    private Map<String, Integer> counters = Collections
        .synchronizedMap(new HashMap<>());
    
    /**
     * Private constructor to enforce the Singleton pattern.
     * Initializes the counters map.
     */
    private IdGenerator() {
        counters = new HashMap<>();
    }
    
    /**
     * Retrieves the single instance of the IdGenerator.
     * If the instance does not exist, it attempts to load it from the persistent file.
     * If loading fails or the file does not exist, a new instance is created and saved.
     *
     * @return The thread-safe Singleton instance of IdGenerator.
     */
    public static synchronized IdGenerator getInstance() {
        if (instance == null) {
            instance = loadFromFile();
        }
        return instance;
    }
    
    /**
     * Attempts to load the saved IdGenerator state from the data file using deserialization.
     *
     * @return The loaded IdGenerator instance if successful, or a new default instance if the file is missing or loading fails.
     */
    private static IdGenerator loadFromFile() {
        File file = new File(ID_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream
                (new FileInputStream(file))) {
                return (IdGenerator) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading ID generator: "
                    + e.getMessage());
            }
        }
        
        IdGenerator generator = new IdGenerator();
        generator.save(); // Save the newly created default state
        return generator;
    }
    
    /**
     * Persists the current state of all ID counters to the data file using serialization.
     * Ensures the data directory exists before saving.
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
     * Generates a new, unique, and formatted ID for a given entity type.
     * The ID format is based on the first three uppercase characters of the entity type,
     * followed by a four-digit zero-padded sequence number (e.g., "Customer" -> "CUS0001").
     * The internal counter for the entity type is incremented, and the state is immediately persisted.
     *
     * @param entityType The descriptive type of the entity (e.g., "Customer", "Product", "Transaction").
     * @return The newly generated, unique ID string.
     */
    public synchronized String generateId(String entityType) {
        int counter = counters.getOrDefault(entityType, 1);
        counters.put(entityType, counter + 1);
        save();
        return entityType.substring(0, 3).toUpperCase()
            + String.format("%04d", counter);
    }
}
