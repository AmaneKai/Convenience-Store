package com.konbini.util;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class IdGenerator implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String ID_FILE = "data/id_counters.dat";

    private static IdGenerator instance;

    private Map<String, Integer> counters;

    private IdGenerator() {
        counters = Collections.synchronizedMap(new HashMap<>());
    }

    public static synchronized IdGenerator getInstance() {
        if (instance == null) {
            instance = loadFromFile();
        }
        return instance;
    }

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

    public void save() {
        FileUtil.ensureDataDirectory();
        try (ObjectOutputStream oos = new ObjectOutputStream
            (new FileOutputStream(ID_FILE))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Error saving ID generator: " + e.getMessage());
        }
    }

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
