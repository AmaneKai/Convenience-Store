package com.konbini.infrastructure.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.konbini.domain.customer.Customer;
import com.konbini.domain.product.Product;
import com.konbini.domain.transaction.CartItem;
import com.konbini.domain.transaction.Transaction;
import com.konbini.domain.transaction.TransactionRepository;
import com.konbini.infrastructure.config.StoreConfig;
import com.konbini.infrastructure.csv.CsvStore;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CSV-backed implementation of {@link TransactionRepository}. Line items and
 * applied discount names are serialized as JSON columns because they contain
 * variable-length data; product snapshots are embedded so historical
 * transactions remain readable even if the product is later removed or restocked.
 */
public class CsvTransactionRepository implements TransactionRepository {

    private static final List<String> COLUMNS = List.of(
            "id", "customerId", "customerName", "timestamp", "subtotal", "tax",
            "discount", "total", "amountPaid", "change", "pointsEarned",
            "pointsRedeemed", "taxName", "appliedDiscounts", "items");

    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {
    };
    private static final TypeReference<List<Map<String, String>>> ITEM_LIST_TYPE = new TypeReference<>() {
    };

    private final CsvStore csvStore;
    private final java.nio.file.Path file;
    private final ObjectMapper objectMapper;
    private final Map<String, Transaction> transactions = new LinkedHashMap<>();

    /**
     * Constructs the repository and loads existing data.
     *
     * @param csvStore the CSV store
     * @param config the store configuration
     */
    public CsvTransactionRepository(CsvStore csvStore, StoreConfig config) {
        this.csvStore = csvStore;
        this.file = config.transactionsFile();
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        load();
    }

    /**
     * Persists all transactions to the CSV file.
     */
    public void save() {
        List<Map<String, String>> rows = transactions.values().stream()
                .map(this::toRow)
                .collect(Collectors.toList());
        csvStore.writeAll(file, COLUMNS, rows);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Transaction> findById(String transactionId) {
        return Optional.ofNullable(transactions.get(transactionId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Transaction> findByCustomerId(String customerId) {
        return transactions.values().stream()
                .filter(transaction -> transaction.getCustomer().getId().equals(customerId))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Transaction> findByDate(LocalDate date) {
        return transactions.values().stream()
                .filter(transaction -> transaction.getTimestamp().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return transactions.values().stream()
                .filter(transaction -> {
                    LocalDate date = transaction.getTimestamp().toLocalDate();
                    return !date.isBefore(startDate) && !date.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    /**
     * Loads all transactions from the CSV file.
     */
    private void load() {
        transactions.clear();
        for (Map<String, String> row : csvStore.readAll(file, COLUMNS)) {
            String id = row.get("id");
            if (id == null || id.trim().isEmpty()) {
                continue;
            }
            try {
                Transaction transaction = fromRow(row);
                if (transaction != null) {
                    transactions.put(transaction.getId(), transaction);
                }
            } catch (Exception exception) {
                System.err.println("Skipping invalid transaction row " + id
                        + ": " + exception.getMessage());
            }
        }
    }

    /**
     * Converts a row map to a transaction.
     *
     * @param row the row
     * @return the transaction, or null for an empty row
     */
    private Transaction fromRow(Map<String, String> row) {
        String id = row.get("id");
        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        Customer customer = Customer.builder()
                .id(row.getOrDefault("customerId", ""))
                .name(row.getOrDefault("customerName", "Unknown"))
                .build();

        List<CartItem> items = parseItems(row.getOrDefault("items", "[]"));

        return Transaction.builder()
                .id(id)
                .customer(customer)
                .items(items)
                .timestamp(LocalDateTime.parse(row.getOrDefault("timestamp",
                        LocalDateTime.now().toString())))
                .subtotal(parseAmount(row.get("subtotal")))
                .tax(parseAmount(row.get("tax")))
                .discount(parseAmount(row.get("discount")))
                .total(parseAmount(row.get("total")))
                .amountPaid(parseAmount(row.get("amountPaid")))
                .change(parseAmount(row.get("change")))
                .pointsEarned(parseInt(row.get("pointsEarned")))
                .pointsRedeemed(parseInt(row.get("pointsRedeemed")))
                .appliedDiscounts(parseStrings(row.getOrDefault("appliedDiscounts", "[]")))
                .taxName(row.getOrDefault("taxName", "Tax"))
                .build();
    }

    /**
     * Converts a transaction to a row map.
     *
     * @param transaction the transaction
     * @return the row
     */
    private Map<String, String> toRow(Transaction transaction) {
        Map<String, String> row = new LinkedHashMap<>();
        row.put("id", transaction.getId());
        row.put("customerId", transaction.getCustomer().getId());
        row.put("customerName", transaction.getCustomer().getName());
        row.put("timestamp", transaction.getTimestamp().toString());
        row.put("subtotal", transaction.getSubtotal().toPlainString());
        row.put("tax", transaction.getTax().toPlainString());
        row.put("discount", transaction.getDiscount().toPlainString());
        row.put("total", transaction.getTotal().toPlainString());
        row.put("amountPaid", transaction.getAmountPaid().toPlainString());
        row.put("change", transaction.getChange().toPlainString());
        row.put("pointsEarned", String.valueOf(transaction.getPointsEarned()));
        row.put("pointsRedeemed", String.valueOf(transaction.getPointsRedeemed()));
        row.put("taxName", transaction.getTaxName());
        row.put("appliedDiscounts", writeJson(transaction.getAppliedDiscounts()));
        row.put("items", writeItems(transaction.getItems()));
        return row;
    }

    /**
     * Serializes the items to a JSON array of flat product snapshots.
     *
     * @param items the items
     * @return the JSON string
     */
    private String writeItems(List<CartItem> items) {
        List<Map<String, String>> serialized = items.stream()
                .map(item -> {
                    Product product = item.getProduct();
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("productId", product.getId());
                    map.put("name", product.getName());
                    map.put("price", product.getPrice().toPlainString());
                    map.put("category", product.getCategory());
                    map.put("brand", product.getBrand());
                    map.put("variant", product.getVariant());
                    map.put("quantity", String.valueOf(item.getQuantity()));
                    return map;
                })
                .collect(Collectors.toList());
        return writeJson(serialized);
    }

    /**
     * Rehydrates items from their JSON representation, reconstructing a product
     * snapshot with sufficient stock so the {@link CartItem} invariants hold.
     *
     * @param json the JSON string
     * @return the items
     */
    private List<CartItem> parseItems(String json) {
        try {
            List<Map<String, String>> entries = objectMapper.readValue(json, ITEM_LIST_TYPE);
            List<CartItem> items = new ArrayList<>();
            for (Map<String, String> entry : entries) {
                int quantity = parseInt(entry.get("quantity"));
                Product product = Product.builder()
                        .id(entry.getOrDefault("productId", ""))
                        .name(entry.getOrDefault("name", "Unknown"))
                        .price(parseAmount(entry.get("price")))
                        .quantity(quantity)
                        .category(entry.getOrDefault("category", ""))
                        .brand(entry.getOrDefault("brand", ""))
                        .variant(entry.getOrDefault("variant", ""))
                        .build();
                items.add(new CartItem(product, quantity));
            }
            return items;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to parse items: " + exception.getMessage(), exception);
        }
    }

    /**
     * Serializes an object to JSON.
     *
     * @param value the value
     * @return the JSON string
     */
    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to serialize JSON: " + exception.getMessage(), exception);
        }
    }

    /**
     * Deserializes a JSON string array.
     *
     * @param json the JSON string
     * @return the list
     */
    private List<String> parseStrings(String json) {
        try {
            return objectMapper.readValue(json, STRING_LIST_TYPE);
        } catch (Exception exception) {
            return new ArrayList<>();
        }
    }

    /**
     * Parses a monetary amount from a possibly empty string.
     *
     * @param value the string value
     * @return the amount, or zero if empty
     */
    private BigDecimal parseAmount(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.trim());
    }

    /**
     * Parses an integer from a possibly empty string.
     *
     * @param value the string value
     * @return the integer, or 0 if empty
     */
    private int parseInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(value);
    }
}
