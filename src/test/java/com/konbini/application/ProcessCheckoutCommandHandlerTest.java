package com.konbini.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.konbini.application.command.ProcessCheckoutCommand;
import com.konbini.application.command.RegisterCustomerCommand;
import com.konbini.application.dto.CustomerDTO;
import com.konbini.application.dto.TransactionDTO;
import com.konbini.application.mediator.Mediator;
import com.konbini.application.mediator.SimpleMediator;
import com.konbini.application.validation.CustomerValidator;
import com.konbini.application.validation.ProductValidator;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.common.IdentifierGenerator;
import com.konbini.domain.customer.CustomerRepository;
import com.konbini.domain.employee.EmployeeRepository;
import com.konbini.domain.employee.PasswordHasher;
import com.konbini.domain.product.Product;
import com.konbini.domain.product.ProductRepository;
import com.konbini.application.session.SessionContext;
import com.konbini.domain.transaction.TransactionRepository;
import com.konbini.domain.unitofwork.UnitOfWork;
import com.konbini.infrastructure.config.StoreConfig;
import com.konbini.infrastructure.csv.CsvStore;
import com.konbini.infrastructure.id.FileIdentifierGenerator;
import com.konbini.infrastructure.repository.CsvCustomerRepository;
import com.konbini.infrastructure.repository.CsvProductRepository;
import com.konbini.infrastructure.repository.CsvTransactionRepository;
import com.konbini.infrastructure.security.BCryptPasswordHasher;
import com.konbini.infrastructure.session.DefaultSessionContext;
import com.konbini.infrastructure.unitofwork.JacksonCsvUnitOfWork;
import io.vavr.control.Either;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * End-to-end test of the checkout handler: builds a Guice-free wiring of the
 * full pipeline (repositories, unit of work, mediator) and verifies the
 * financial breakdown — VAT, senior discount and loyalty points — plus stock
 * decrementing and persistence.
 */
class ProcessCheckoutCommandHandlerTest {

    @Test
    void regularCustomerPaysSubtotalPlusVat() throws Exception {
        TestContext context = TestContext.create();

        Either<DomainError, TransactionDTO> result = context.mediator.send(
                new ProcessCheckoutCommand("CUS0001", Map.of("PRO0001", 2),
                        new BigDecimal("200.00"), 0));

        assertTrue(result.isRight(), () -> "Expected success, got: " + result.getLeft());
        TransactionDTO transaction = result.get();

        assertEquals(0, new BigDecimal("150.00").compareTo(transaction.subtotal()));
        assertEquals(0, new BigDecimal("18.00").compareTo(transaction.tax()));
        assertEquals(0, new BigDecimal("168.00").compareTo(transaction.total()));
        assertEquals(0, new BigDecimal("32.00").compareTo(transaction.change()));

        assertEquals(8, context.productRepository
                .findById("PRO0001").orElseThrow().getQuantity(),
                "Stock should be decremented by the purchased quantity");
    }

    @Test
    void seniorCitizenReceivesTwentyPercentDiscount() throws Exception {
        TestContext context = TestContext.create();

        Either<DomainError, TransactionDTO> result = context.mediator.send(
                new ProcessCheckoutCommand("CUS0002", Map.of("PRO0001", 2),
                        new BigDecimal("200.00"), 0));

        assertTrue(result.isRight(), () -> "Expected success, got: " + result.getLeft());
        TransactionDTO transaction = result.get();

        BigDecimal subtotal = new BigDecimal("150.00");
        BigDecimal discount = subtotal.multiply(new BigDecimal("0.20"));
        BigDecimal expectedTotal = subtotal
                .add(subtotal.multiply(new BigDecimal("0.12")))
                .subtract(discount);

        assertEquals(0, discount.compareTo(transaction.discount()));
        assertEquals(0, expectedTotal.compareTo(transaction.total()));
        assertEquals(1, transaction.appliedDiscounts().size());
        assertEquals("Senior Citizen Discount", transaction.appliedDiscounts().get(0));
    }

    @Test
    void pointsAreEarnedAndCanBeRedeemed() throws Exception {
        TestContext context = TestContext.create();

        Either<DomainError, TransactionDTO> first = context.mediator.send(
                new ProcessCheckoutCommand("CUS0003", Map.of("PRO0001", 2),
                        new BigDecimal("200.00"), 0));
        assertTrue(first.isRight(), () -> "Expected success, got: " + first.getLeft());
        assertEquals(3, first.get().pointsEarned(), "168 / 50 = 3 points");

        Either<DomainError, TransactionDTO> second = context.mediator.send(
                new ProcessCheckoutCommand("CUS0003", Map.of("PRO0001", 1),
                        new BigDecimal("200.00"), 3));
        assertTrue(second.isRight(), () -> "Expected success, got: " + second.getLeft());

        TransactionDTO transaction = second.get();
        assertEquals(3, transaction.pointsRedeemed());
        assertEquals(0, new BigDecimal("3.00").compareTo(transaction.discount()));
        assertEquals(1, transaction.pointsEarned(),
                "Points earned on 81 total is 81/50 = 1");
    }

    @Test
    void insufficientPaymentIsRejected() throws Exception {
        TestContext context = TestContext.create();

        Either<DomainError, TransactionDTO> result = context.mediator.send(
                new ProcessCheckoutCommand("CUS0001", Map.of("PRO0001", 2),
                        new BigDecimal("100.00"), 0));

        assertTrue(result.isLeft());
        assertEquals(DomainError.ErrorCode.BUSINESS_RULE, result.getLeft().code());
    }

    @Test
    void registrationCreatesCustomerWithMembershipCard() throws Exception {
        TestContext context = TestContext.create();

        Either<DomainError, CustomerDTO> result = context.mediator.send(
                new RegisterCustomerCommand("New Customer", false,
                        "MEM-0999", LocalDate.now().plusYears(2)));

        assertTrue(result.isRight(), () -> "Expected success, got: " + result.getLeft());
        CustomerDTO customer = result.get();
        assertTrue(customer.hasMembershipCard());
        assertEquals("MEM-0999", customer.cardNumber());
    }

    /**
     * Minimal wiring of the application layer backed by temporary CSV stores.
     */
    private static final class TestContext {

        final Mediator mediator;
        final ProductRepository productRepository;
        final CustomerRepository customerRepository;
        final TransactionRepository transactionRepository;

        TestContext(Mediator mediator, ProductRepository productRepository,
                    CustomerRepository customerRepository,
                    TransactionRepository transactionRepository) {
            this.mediator = mediator;
            this.productRepository = productRepository;
            this.customerRepository = customerRepository;
            this.transactionRepository = transactionRepository;
        }

        static TestContext create() throws Exception {
            Path dir = Files.createTempDirectory("konbini-checkout");
            StoreConfig config = new StoreConfig(dir);
            CsvStore csvStore = new CsvStore();

            CsvProductRepository products = new CsvProductRepository(csvStore, config);
            products.add(Product.builder()
                    .id("PRO0001")
                    .name("Sandwich")
                    .price(new BigDecimal("75.00"))
                    .quantity(10)
                    .category("Food")
                    .brand("Konbini")
                    .variant("Ready to Eat")
                    .build());

            CsvCustomerRepository customers = new CsvCustomerRepository(csvStore, config);
            customers.add(com.konbini.domain.customer.Customer.builder()
                    .id("CUS0001")
                    .name("Juan Dela Cruz")
                    .seniorCitizen(false)
                    .build());
            customers.add(com.konbini.domain.customer.Customer.builder()
                    .id("CUS0002")
                    .name("Maria Santos")
                    .seniorCitizen(true)
                    .build());
            customers.add(com.konbini.domain.customer.Customer.builder()
                    .id("CUS0003")
                    .name("Pedro Reyes")
                    .seniorCitizen(false)
                    .membershipCard(com.konbini.domain.customer.MembershipCard.builder()
                            .id("CAR0001")
                            .cardNumber("MEM-0001")
                            .expiryDate(LocalDate.now().plusYears(1))
                            .points(100)
                            .build())
                    .build());

            CsvTransactionRepository transactions = new CsvTransactionRepository(csvStore, config);
            IdentifierGenerator identifierGenerator = new FileIdentifierGenerator(config);
            UnitOfWork unitOfWork = new JacksonCsvUnitOfWork(products, customers,
                    new com.konbini.infrastructure.repository.CsvEmployeeRepository(csvStore, config),
                    transactions);
            PasswordHasher passwordHasher = new BCryptPasswordHasher();
            SessionContext sessionContext = new DefaultSessionContext();

            SimpleMediator mediator = new SimpleMediator();
            mediator.register(ProcessCheckoutCommand.class,
                    new com.konbini.application.handler.ProcessCheckoutCommandHandler(
                            customers, products, transactions, identifierGenerator, unitOfWork));
            mediator.register(RegisterCustomerCommand.class,
                    new com.konbini.application.handler.RegisterCustomerCommandHandler(
                            customers, identifierGenerator, unitOfWork,
                            new CustomerValidator()));

            return new TestContext(mediator, products, customers, transactions);
        }
    }
}
