package com.konbini.infrastructure.id;

import com.konbini.domain.common.IdentifierGenerator;
import com.konbini.infrastructure.config.StoreConfig;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * File-backed identifier generator that persists per-entity counters to a CSV
 * file, mirroring the legacy {@code IdGenerator} behavior without a global
 * static singleton. Counter state is loaded on construction and flushed on
 * every generation.
 */
public class FileIdentifierGenerator implements IdentifierGenerator {

    private final Path counterFile;
    private final Map<String, Integer> counters = new ConcurrentHashMap<>();

    /**
     * Constructs the generator and loads any existing counter state.
     *
     * @param config the store configuration
     */
    public FileIdentifierGenerator(StoreConfig config) {
        this.counterFile = config.idCountersFile();
        loadCounters();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized String generate(String entityType) {
        if (entityType == null || entityType.length() < 3) {
            throw new IllegalArgumentException(
                    "Entity type must be at least 3 characters, got: " + entityType);
        }

        int counter = counters.getOrDefault(entityType, 1);
        counters.put(entityType, counter + 1);
        saveCounters();

        return entityType.substring(0, 3).toUpperCase() + String.format("%04d", counter);
    }

    /**
     * Seeds the counter for an entity type to a specific value.
     * Used by the legacy data migrator so new IDs do not collide with migrated ones.
     *
     * @param entityType the entity type
     * @param nextValue the next value the generator should produce
     */
    public synchronized void seed(String entityType, int nextValue) {
        counters.put(entityType, nextValue);
    }

    /**
     * Loads counters from the counter file, if present.
     */
    private void loadCounters() {
        if (!Files.exists(counterFile)) {
            return;
        }
        try {
            List<String> lines = Files.readAllLines(counterFile, StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split(",", -1);
                if (parts.length == 2) {
                    counters.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                }
            }
        } catch (IOException | NumberFormatException exception) {
            counters.clear();
        }
    }

    /**
     * Persists the current counters to the counter file.
     */
    private void saveCounters() {
        try {
            Path parent = counterFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            StringBuilder content = new StringBuilder();
            counters.forEach((key, value) ->
                    content.append(key).append(',').append(value).append(System.lineSeparator()));
            Files.writeString(counterFile, content, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to persist ID counters: " + exception.getMessage(), exception);
        }
    }

    /**
     * Returns a copy of the current counters for seeding purposes.
     *
     * @return the counters map
     */
    public Map<String, Integer> snapshotCounters() {
        return new HashMap<>(counters);
    }
}
