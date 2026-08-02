package com.konbini.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.inject.Injector;
import com.konbini.application.command.AuthenticateEmployeeCommand;
import com.konbini.application.command.ProcessCheckoutCommand;
import com.konbini.application.dto.EmployeeDTO;
import com.konbini.application.dto.TransactionDTO;
import com.konbini.application.mediator.Mediator;
import com.konbini.application.query.GetDashboardQuery;
import com.konbini.application.query.GetProductsQuery;
import com.konbini.application.session.SessionContext;
import com.konbini.domain.common.DomainError;
import com.konbini.infrastructure.bootstrap.ApplicationBootstrap;
import io.vavr.control.Either;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * End-to-end test through the full Guice container: seeds a temporary
 * directory from the CSV data files, authenticates with the seeded
 * credentials, runs a checkout and verifies the dashboard reflects the new
 * sale.
 */
class ApplicationBootstrapTest {

    @Test
    void fullApplicationWiringWorksEndToEnd() throws Exception {
        Path dataDir = copyCsvDataToTemp();

        Injector injector = ApplicationBootstrap.createInjector(dataDir);
        Mediator mediator = injector.getInstance(Mediator.class);

        assertTrue(Files.exists(dataDir.resolve("products.csv")));
        assertTrue(Files.exists(dataDir.resolve("customers.csv")));
        assertTrue(Files.exists(dataDir.resolve("employees.csv")));

        Either<DomainError, EmployeeDTO> auth = mediator.send(
                new AuthenticateEmployeeCommand("EMP0001", "password"));
        assertTrue(auth.isRight(), () -> "Manager login should work: " + auth.getLeft());
        assertEquals("Manager", auth.get().name());

        SessionContext session = injector.getInstance(SessionContext.class);
        assertTrue(session.isAuthenticated());

        Either<DomainError, List<com.konbini.application.dto.ProductDTO>> products =
                mediator.send(new GetProductsQuery());
        assertTrue(products.isRight());
        assertEquals(27, products.get().size(), "All seeded products should be loaded");

        Either<DomainError, com.konbini.application.dto.DashboardDTO> dashboardBefore =
                mediator.send(new GetDashboardQuery());
        assertTrue(dashboardBefore.isRight());
        long transactionsBefore = dashboardBefore.get().totalTransactions();

        Either<DomainError, TransactionDTO> checkout = mediator.send(
                new ProcessCheckoutCommand("CUS0001", Map.of("PRO0021", 1),
                        new BigDecimal("100.00"), 0));
        assertTrue(checkout.isRight(), () -> "Checkout should succeed: " + checkout.getLeft());
        assertEquals(0, new BigDecimal("50.00").compareTo(checkout.get().subtotal()));
        assertEquals(0, new BigDecimal("6.00").compareTo(checkout.get().tax()));

        Either<DomainError, com.konbini.application.dto.DashboardDTO> dashboardAfter =
                mediator.send(new GetDashboardQuery());
        assertTrue(dashboardAfter.isRight());
        assertEquals(transactionsBefore + 1, dashboardAfter.get().totalTransactions(),
                "checkout should add exactly one transaction");
    }

    /**
     * Copies the real CSV data files into a temporary directory.
     *
     * @return the temporary directory
     * @throws Exception if copying fails
     */
    private Path copyCsvDataToTemp() throws Exception {
        Path source = Path.of("data");
        Path target = Files.createTempDirectory("konbini-e2e");
        for (String name : List.of("products.csv", "customers.csv", "employees.csv",
                "transactions.csv", "id_counters.csv")) {
            Files.copy(source.resolve(name), target.resolve(name));
        }
        return target;
    }
}
