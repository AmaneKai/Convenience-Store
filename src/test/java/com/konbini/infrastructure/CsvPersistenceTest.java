package com.konbini.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.konbini.infrastructure.config.StoreConfig;
import com.konbini.infrastructure.repository.CsvCustomerRepository;
import com.konbini.infrastructure.repository.CsvProductRepository;
import com.konbini.infrastructure.repository.CsvTransactionRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * Verifies that the CSV-backed repositories round-trip state through disk.
 */
class CsvPersistenceTest {

    @Test
    void productsSurviveSaveAndReload() throws Exception {
        StoreConfig config = CsvTestFixtures.tempConfig();
        CsvProductRepository repository = CsvTestFixtures.productRepositoryWith(config);

        repository.save();

        CsvProductRepository reloaded =
                CsvTestFixtures.reload(config, CsvTestFixtures.RepositoryType.PRODUCTS);

        assertEquals(2, reloaded.findAll().size());
        assertTrue(reloaded.findById("PRO0001").isPresent());
        assertEquals("Sandwich", reloaded.findById("PRO0001").orElseThrow().getName());
        assertEquals(0, new BigDecimal("75.00")
                .compareTo(reloaded.findById("PRO0001").orElseThrow().getPrice()));
    }

    @Test
    void customersSurviveSaveAndReload() throws Exception {
        StoreConfig config = CsvTestFixtures.tempConfig();
        CsvCustomerRepository repository = CsvTestFixtures.customerRepositoryWith(config);

        repository.save();

        CsvCustomerRepository reloaded =
                CsvTestFixtures.reload(config, CsvTestFixtures.RepositoryType.CUSTOMERS);

        assertEquals(1, reloaded.findAll().size());
        assertEquals("Juan Dela Cruz", reloaded.findById("CUS0001").orElseThrow().getName());
    }

    @Test
    void customerPasswordHashSurvivesSaveAndReload() throws Exception {
        StoreConfig config = CsvTestFixtures.tempConfig();
        CsvCustomerRepository repository = CsvTestFixtures.customerRepositoryWith(config);
        repository.add(com.konbini.domain.customer.Customer.builder()
                .id("CUS0099")
                .name("Login Customer")
                .seniorCitizen(false)
                .passwordHash("hashed-value")
                .build());

        repository.save();

        CsvCustomerRepository reloaded =
                CsvTestFixtures.reload(config, CsvTestFixtures.RepositoryType.CUSTOMERS);

        var withPassword = reloaded.findById("CUS0099").orElseThrow();
        assertTrue(withPassword.hasPassword());
        assertEquals("hashed-value", withPassword.getPasswordHash());

        var withoutPassword = reloaded.findById("CUS0001").orElseThrow();
        assertTrue(!withoutPassword.hasPassword());
    }

    @Test
    void transactionsSurviveSaveAndReloadWithItems() throws Exception {
        StoreConfig config = CsvTestFixtures.tempConfig();
        CsvTransactionRepository repository = CsvTestFixtures.transactionRepositoryWith(config);

        repository.save();

        CsvTransactionRepository reloaded =
                CsvTestFixtures.reload(config, CsvTestFixtures.RepositoryType.TRANSACTIONS);

        assertEquals(1, reloaded.findAll().size());
        var transaction = reloaded.findById("TRA0001").orElseThrow();
        assertEquals(0, new BigDecimal("150.00").compareTo(transaction.getSubtotal()));
        assertEquals(0, new BigDecimal("18.00").compareTo(transaction.getTax()));
        assertEquals(0, new BigDecimal("168.00").compareTo(transaction.getTotal()));
        assertEquals(1, transaction.getItems().size());
        assertEquals(2, transaction.getItems().get(0).getQuantity());
        assertEquals("Sandwich", transaction.getItems().get(0).getProduct().getName());
    }

    @Test
    void emptyRepositoryReloadsAsEmpty() throws Exception {
        StoreConfig config = CsvTestFixtures.tempConfig();
        CsvProductRepository repository = new CsvProductRepository(
                new com.konbini.infrastructure.csv.CsvStore(), config);

        repository.save();

        CsvProductRepository reloaded =
                CsvTestFixtures.reload(config, CsvTestFixtures.RepositoryType.PRODUCTS);

        assertTrue(reloaded.findAll().isEmpty());
    }
}
