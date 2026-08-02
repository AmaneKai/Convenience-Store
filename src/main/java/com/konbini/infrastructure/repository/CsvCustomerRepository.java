package com.konbini.infrastructure.repository;

import com.konbini.domain.customer.Customer;
import com.konbini.domain.customer.CustomerRepository;
import com.konbini.domain.customer.MembershipCard;
import com.konbini.infrastructure.config.StoreConfig;
import com.konbini.infrastructure.csv.CsvStore;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CSV-backed implementation of {@link CustomerRepository}. Membership card data
 * is stored inline as nullable columns on the customer row.
 */
public class CsvCustomerRepository implements CustomerRepository {

    private static final List<String> COLUMNS = List.of(
            "id", "name", "seniorCitizen", "cardId", "cardNumber",
            "cardExpiryDate", "cardPoints", "passwordHash");

    private final CsvStore csvStore;
    private final java.nio.file.Path file;
    private final Map<String, Customer> customers = new LinkedHashMap<>();

    /**
     * Constructs the repository and loads existing data.
     *
     * @param csvStore the CSV store
     * @param config the store configuration
     */
    public CsvCustomerRepository(CsvStore csvStore, StoreConfig config) {
        this.csvStore = csvStore;
        this.file = config.customersFile();
        load();
    }

    /**
     * Persists all customers to the CSV file.
     */
    public void save() {
        List<Map<String, String>> rows = customers.values().stream()
                .map(this::toRow)
                .collect(Collectors.toList());
        csvStore.writeAll(file, COLUMNS, rows);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(String customerId) {
        customers.remove(customerId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Customer> findById(String customerId) {
        return Optional.ofNullable(customers.get(customerId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Customer> findByMembershipCardNumber(String cardNumber) {
        return customers.values().stream()
                .filter(customer -> customer.hasMembershipCard()
                        && customer.getMembershipCard().getCardNumber().equals(cardNumber))
                .findFirst();
    }

    /**
     * Loads all customers from the CSV file.
     */
    private void load() {
        customers.clear();
        for (Map<String, String> row : csvStore.readAll(file, COLUMNS)) {
            try {
                Customer customer = fromRow(row);
                if (customer != null) {
                    customers.put(customer.getId(), customer);
                }
            } catch (IllegalArgumentException exception) {
                System.err.println("Skipping invalid customer row: " + exception.getMessage());
            }
        }
    }

    /**
     * Converts a row map to a customer.
     *
     * @param row the row
     * @return the customer, or null for an empty row
     */
    private Customer fromRow(Map<String, String> row) {
        String id = row.get("id");
        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        MembershipCard card = null;
        String cardNumber = row.get("cardNumber");
        String cardExpiry = row.get("cardExpiryDate");
        if (cardNumber != null && !cardNumber.trim().isEmpty()
                && cardExpiry != null && !cardExpiry.trim().isEmpty()) {
            card = MembershipCard.builder()
                    .id(row.getOrDefault("cardId", "CARD0000"))
                    .cardNumber(cardNumber)
                    .expiryDate(LocalDate.parse(cardExpiry))
                    .points(parseInt(row.get("cardPoints")))
                    .build();
        }

        String passwordHash = row.get("passwordHash");
        return Customer.builder()
                .id(id)
                .name(row.getOrDefault("name", ""))
                .seniorCitizen(Boolean.parseBoolean(row.getOrDefault("seniorCitizen", "false")))
                .membershipCard(card)
                .passwordHash(passwordHash == null || passwordHash.isBlank() ? null : passwordHash)
                .build();
    }

    /**
     * Converts a customer to a row map.
     *
     * @param customer the customer
     * @return the row
     */
    private Map<String, String> toRow(Customer customer) {
        Map<String, String> row = new LinkedHashMap<>();
        row.put("id", customer.getId());
        row.put("name", customer.getName());
        row.put("seniorCitizen", String.valueOf(customer.isSeniorCitizen()));

        if (customer.hasMembershipCard()) {
            MembershipCard card = customer.getMembershipCard();
            row.put("cardId", card.getId());
            row.put("cardNumber", card.getCardNumber());
            row.put("cardExpiryDate", card.getExpiryDate().toString());
            row.put("cardPoints", String.valueOf(card.getPoints()));
        } else {
            row.put("cardId", "");
            row.put("cardNumber", "");
            row.put("cardExpiryDate", "");
            row.put("cardPoints", "");
        }
        row.put("passwordHash", customer.hasPassword() ? customer.getPasswordHash() : "");
        return row;
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
