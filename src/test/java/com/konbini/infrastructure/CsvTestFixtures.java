package com.konbini.infrastructure;

import com.konbini.domain.customer.Customer;
import com.konbini.domain.customer.CustomerRepository;
import com.konbini.domain.product.Product;
import com.konbini.domain.product.ProductRepository;
import com.konbini.domain.transaction.CartItem;
import com.konbini.domain.transaction.Transaction;
import com.konbini.domain.transaction.TransactionRepository;
import com.konbini.infrastructure.config.StoreConfig;
import com.konbini.infrastructure.csv.CsvStore;
import com.konbini.infrastructure.repository.CsvCustomerRepository;
import com.konbini.infrastructure.repository.CsvProductRepository;
import com.konbini.infrastructure.repository.CsvTransactionRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Builds isolated CSV-backed repositories on a temporary directory so tests
 * never touch the real {@code data/} directory.
 */
public final class CsvTestFixtures {

    private CsvTestFixtures() {
    }

    /**
     * Creates a temporary store configuration.
     *
     * @return a config pointing at a fresh temporary directory
     * @throws IOException if the directory cannot be created
     */
    public static StoreConfig tempConfig() throws IOException {
        Path dir = Files.createTempDirectory("konbini-test");
        return new StoreConfig(dir);
    }

    /**
     * Builds a product repository seeded with a known product.
     *
     * @param config the store configuration
     * @return the product repository
     */
    public static CsvProductRepository productRepositoryWith(StoreConfig config) {
        CsvProductRepository repository = new CsvProductRepository(new CsvStore(), config);
        repository.add(Product.builder()
                .id("PRO0001")
                .name("Sandwich")
                .price(new BigDecimal("75.00"))
                .quantity(10)
                .category("Food")
                .brand("Konbini")
                .variant("Ready to Eat")
                .build());
        repository.add(Product.builder()
                .id("PRO0002")
                .name("Potato Chips")
                .price(new BigDecimal("45.00"))
                .quantity(20)
                .category("Food")
                .brand("Lays")
                .variant("Snack")
                .build());
        return repository;
    }

    /**
     * Builds a customer repository seeded with a known customer.
     *
     * @param config the store configuration
     * @return the customer repository
     */
    public static CsvCustomerRepository customerRepositoryWith(StoreConfig config) {
        CsvCustomerRepository repository = new CsvCustomerRepository(new CsvStore(), config);
        repository.add(Customer.builder()
                .id("CUS0001")
                .name("Juan Dela Cruz")
                .seniorCitizen(false)
                .build());
        return repository;
    }

    /**
     * Builds a transaction repository seeded with a single transaction.
     *
     * @param config the store configuration
     * @return the transaction repository
     */
    public static CsvTransactionRepository transactionRepositoryWith(StoreConfig config) {
        CsvProductRepository productRepository = productRepositoryWith(config);
        Product product = productRepository.findById("PRO0001").orElseThrow();
        Customer customer = customerRepositoryWith(config).findById("CUS0001").orElseThrow();

        Transaction transaction = Transaction.builder()
                .id("TRA0001")
                .customer(customer)
                .items(List.of(new CartItem(product, 2)))
                .timestamp(LocalDateTime.of(2026, 8, 2, 10, 0))
                .subtotal(new BigDecimal("150.00"))
                .tax(new BigDecimal("18.00"))
                .discount(BigDecimal.ZERO)
                .total(new BigDecimal("168.00"))
                .amountPaid(new BigDecimal("200.00"))
                .change(new BigDecimal("32.00"))
                .pointsEarned(3)
                .pointsRedeemed(0)
                .appliedDiscounts(List.of())
                .taxName("Value Added Tax (VAT)")
                .build();

        CsvTransactionRepository repository = new CsvTransactionRepository(new CsvStore(), config);
        repository.add(transaction);
        return repository;
    }

    /**
     * Reloads a repository from disk to verify persisted state.
     *
     * @param config the store configuration
     * @param type the repository type
     * @return the reloaded repository
     */
    public static <T> T reload(StoreConfig config, RepositoryType type) {
        CsvStore store = new CsvStore();
        switch (type) {
            case PRODUCTS:
                return type.cast(new CsvProductRepository(store, config));
            case CUSTOMERS:
                return type.cast(new CsvCustomerRepository(store, config));
            case TRANSACTIONS:
                return type.cast(new CsvTransactionRepository(store, config));
            default:
                throw new IllegalArgumentException("Unsupported repository type: " + type);
        }
    }

    /**
     * Supported repository kinds for reloading.
     */
    public enum RepositoryType {
        /** Products repository. */
        PRODUCTS(ProductRepository.class),
        /** Customers repository. */
        CUSTOMERS(CustomerRepository.class),
        /** Transactions repository. */
        TRANSACTIONS(TransactionRepository.class);

        private final Class<?> type;

        RepositoryType(Class<?> type) {
            this.type = type;
        }

        @SuppressWarnings("unchecked")
        private <T> T cast(Object value) {
            return (T) value;
        }
    }
}
