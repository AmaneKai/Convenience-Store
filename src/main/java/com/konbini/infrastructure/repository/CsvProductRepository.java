package com.konbini.infrastructure.repository;

import com.konbini.domain.product.Product;
import com.konbini.domain.product.ProductRepository;
import com.konbini.infrastructure.config.StoreConfig;
import com.konbini.infrastructure.csv.CsvStore;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CSV-backed implementation of {@link ProductRepository}. Loads the full
 * product set into memory on construction and persists it back to disk via
 * {@link #save()}, which the unit of work invokes on commit.
 */
public class CsvProductRepository implements ProductRepository {

    private static final List<String> COLUMNS = List.of(
            "id", "name", "price", "quantity", "category", "brand",
            "variant", "expirationDate", "lowStockThreshold");

    private final CsvStore csvStore;
    private final java.nio.file.Path file;
    private final Map<String, Product> products = new LinkedHashMap<>();

    /**
     * Constructs the repository and loads existing data.
     *
     * @param csvStore the CSV store
     * @param config the store configuration
     */
    public CsvProductRepository(CsvStore csvStore, StoreConfig config) {
        this.csvStore = csvStore;
        this.file = config.productsFile();
        load();
    }

    /**
     * Persists all products to the CSV file.
     */
    public void save() {
        List<Map<String, String>> rows = products.values().stream()
                .map(this::toRow)
                .collect(Collectors.toList());
        csvStore.writeAll(file, COLUMNS, rows);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(Product product) {
        products.put(product.getId(), product);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Product product) {
        products.put(product.getId(), product);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(String productId) {
        products.remove(productId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Product> findById(String productId) {
        return Optional.ofNullable(products.get(productId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findByCategory(String categoryDisplayName) {
        return products.values().stream()
                .filter(product -> product.getCategory().equalsIgnoreCase(categoryDisplayName))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findByName(String name) {
        return products.values().stream()
                .filter(product -> product.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findLowStock() {
        return products.values().stream()
                .filter(Product::isLowStock)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findExpired() {
        return products.values().stream()
                .filter(Product::isExpired)
                .collect(Collectors.toList());
    }

    /**
     * Loads all products from the CSV file.
     */
    private void load() {
        products.clear();
        for (Map<String, String> row : csvStore.readAll(file, COLUMNS)) {
            try {
                Product product = fromRow(row);
                if (product != null) {
                    products.put(product.getId(), product);
                }
            } catch (IllegalArgumentException exception) {
                System.err.println("Skipping invalid product row: " + exception.getMessage());
            }
        }
    }

    /**
     * Converts a row map to a product.
     *
     * @param row the row
     * @return the product, or null for an empty row
     */
    private Product fromRow(Map<String, String> row) {
        String id = row.get("id");
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return Product.builder()
                .id(id)
                .name(row.get("name"))
                .price(new BigDecimal(row.getOrDefault("price", "0")))
                .quantity(Integer.parseInt(row.getOrDefault("quantity", "0")))
                .category(row.getOrDefault("category", ""))
                .brand(row.getOrDefault("brand", ""))
                .variant(row.getOrDefault("variant", ""))
                .expirationDate(parseDate(row.get("expirationDate")))
                .lowStockThreshold(parseInt(row.get("lowStockThreshold")))
                .build();
    }

    /**
     * Converts a product to a row map.
     *
     * @param product the product
     * @return the row
     */
    private Map<String, String> toRow(Product product) {
        Map<String, String> row = new LinkedHashMap<>();
        row.put("id", product.getId());
        row.put("name", product.getName());
        row.put("price", product.getPrice().toPlainString());
        row.put("quantity", String.valueOf(product.getQuantity()));
        row.put("category", product.getCategory());
        row.put("brand", product.getBrand());
        row.put("variant", product.getVariant());
        row.put("expirationDate", product.getExpirationDate() != null
                ? product.getExpirationDate().toString() : "");
        row.put("lowStockThreshold", String.valueOf(product.getLowStockThreshold()));
        return row;
    }

    /**
     * Parses a local date from a possibly empty string.
     *
     * @param value the string value
     * @return the date, or null if empty
     */
    private LocalDate parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(value);
    }

    /**
     * Parses an integer from a possibly empty string.
     *
     * @param value the string value
     * @return the integer, or null if empty
     */
    private Integer parseInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Integer.parseInt(value);
    }
}
