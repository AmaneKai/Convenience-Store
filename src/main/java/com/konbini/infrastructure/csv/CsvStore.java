package com.konbini.infrastructure.csv;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal CSV reader/writer built on Jackson, mapping each row to a
 * {@code Map<String, String>} keyed by the header column name. Missing files
 * are treated as empty, and a header is always written on save.
 */
public class CsvStore {

    private final CsvMapper mapper;

    /**
     * Constructs a CSV store.
     */
    public CsvStore() {
        this.mapper = CsvMapper.builder()
                .enable(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE)
                .build();
    }

    /**
     * Reads all rows from a CSV file.
     *
     * @param file the file to read
     * @param columns the expected header columns
     * @return the rows, or an empty list if the file does not exist
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, String>> readAll(Path file, List<String> columns) {
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }
        try {
            if (Files.size(file) == 0) {
                return new ArrayList<>();
            }
        } catch (IOException exception) {
            return new ArrayList<>();
        }

        try {
            CsvSchema schema = CsvSchema.builder().setUseHeader(true).build();
            ObjectReader reader = mapper.readerFor(Map.class).with(schema);
            try (MappingIterator<Map<String, String>> iterator = reader.readValues(
                    Files.newBufferedReader(file, StandardCharsets.UTF_8))) {
                List<Map<String, String>> rows = new ArrayList<>();
                while (iterator.hasNext()) {
                    rows.add(iterator.next());
                }
                return rows;
            }
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to read CSV file: " + file + " - " + exception.getMessage(), exception);
        }
    }

    /**
     * Writes rows to a CSV file, always emitting the given columns as the header.
     *
     * @param file the file to write
     * @param columns the header columns
     * @param rows the rows to write
     */
    public void writeAll(Path file, List<String> columns, List<Map<String, String>> rows) {
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            CsvSchema.Builder schemaBuilder = CsvSchema.builder();
            for (String column : columns) {
                schemaBuilder.addColumn(column);
            }
            CsvSchema schema = schemaBuilder.build().withHeader();

            ObjectWriter writer = mapper.writer(schema);
            List<Map<String, String>> normalized = new ArrayList<>();
            for (Map<String, String> row : rows) {
                Map<String, String> normalizedRow = new LinkedHashMap<>();
                for (String column : columns) {
                    normalizedRow.put(column, row.getOrDefault(column, ""));
                }
                normalized.add(normalizedRow);
            }

            writer.writeValue(file.toFile(), normalized);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to write CSV file: " + file + " - " + exception.getMessage(), exception);
        }
    }
}
