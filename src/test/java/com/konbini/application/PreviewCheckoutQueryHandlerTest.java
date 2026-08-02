package com.konbini.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.konbini.application.command.ProcessCheckoutCommand;
import com.konbini.application.dto.CheckoutPreviewDTO;
import com.konbini.application.dto.TransactionDTO;
import com.konbini.application.handler.PreviewCheckoutQueryHandler;
import com.konbini.application.handler.ProcessCheckoutCommandHandler;
import com.konbini.application.mediator.Mediator;
import com.konbini.application.mediator.SimpleMediator;
import com.konbini.application.query.PreviewCheckoutQuery;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.common.IdentifierGenerator;
import com.konbini.domain.customer.Customer;
import com.konbini.domain.customer.CustomerRepository;
import com.konbini.domain.customer.MembershipCard;
import com.konbini.domain.product.Product;
import com.konbini.domain.product.ProductRepository;
import com.konbini.domain.transaction.TransactionRepository;
import com.konbini.domain.unitofwork.UnitOfWork;
import com.konbini.infrastructure.config.StoreConfig;
import com.konbini.infrastructure.csv.CsvStore;
import com.konbini.infrastructure.id.FileIdentifierGenerator;
import com.konbini.infrastructure.repository.CsvCustomerRepository;
import com.konbini.infrastructure.repository.CsvEmployeeRepository;
import com.konbini.infrastructure.repository.CsvProductRepository;
import com.konbini.infrastructure.repository.CsvTransactionRepository;
import com.konbini.infrastructure.unitofwork.JacksonCsvUnitOfWork;
import io.vavr.control.Either;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Verifies that {@link PreviewCheckoutQueryHandler} reports the exact same
 * financial breakdown {@link ProcessCheckoutCommandHandler} would charge,
 * without mutating stock or loyalty points.
 */
class PreviewCheckoutQueryHandlerTest {

    @Test
    void previewMatchesActualCheckoutForSeniorWithPointsRedemption() throws Exception {
        TestContext context = TestContext.create();

        int stockBeforePreview = context.productRepository.findById("PRO0001").orElseThrow().getQuantity();
        int pointsBeforePreview = context.customerRepository.findById("CUS0002")
                .orElseThrow().getMembershipCard().getPoints();

        Either<DomainError, CheckoutPreviewDTO> previewResult = context.mediator.send(
                new PreviewCheckoutQuery("CUS0002", Map.of("PRO0001", 2), 10));
        assertTrue(previewResult.isRight(), () -> "Expected success, got: " + previewResult.getLeft());
        CheckoutPreviewDTO preview = previewResult.get();

        assertEquals(stockBeforePreview, context.productRepository.findById("PRO0001").orElseThrow().getQuantity(),
                "Preview must not decrement stock");
        assertEquals(pointsBeforePreview, context.customerRepository.findById("CUS0002")
                        .orElseThrow().getMembershipCard().getPoints(),
                "Preview must not mutate loyalty points");

        Either<DomainError, TransactionDTO> checkoutResult = context.mediator.send(
                new ProcessCheckoutCommand("CUS0002", Map.of("PRO0001", 2), preview.total(), 10));
        assertTrue(checkoutResult.isRight(), () -> "Expected success, got: " + checkoutResult.getLeft());
        TransactionDTO transaction = checkoutResult.get();

        assertEquals(0, preview.subtotal().compareTo(transaction.subtotal()));
        assertEquals(0, preview.tax().compareTo(transaction.tax()));
        assertEquals(0, preview.discount().compareTo(transaction.discount()));
        assertEquals(0, preview.total().compareTo(transaction.total()));
        assertEquals(preview.pointsToEarn(), transaction.pointsEarned());
        assertEquals(0, transaction.change().compareTo(BigDecimal.ZERO),
                "Self-checkout pays the previewed total exactly, so change is zero");

        assertEquals(stockBeforePreview - 2,
                context.productRepository.findById("PRO0001").orElseThrow().getQuantity(),
                "The real checkout should decrement stock by the purchased quantity");
    }

    @Test
    void previewRejectsInsufficientStock() throws Exception {
        TestContext context = TestContext.create();

        Either<DomainError, CheckoutPreviewDTO> result = context.mediator.send(
                new PreviewCheckoutQuery("CUS0001", Map.of("PRO0001", 999), 0));

        assertTrue(result.isLeft());
        assertEquals(DomainError.ErrorCode.BUSINESS_RULE, result.getLeft().code());
    }

    @Test
    void previewRejectsEmptyCart() throws Exception {
        TestContext context = TestContext.create();

        Either<DomainError, CheckoutPreviewDTO> result = context.mediator.send(
                new PreviewCheckoutQuery("CUS0001", Map.of(), 0));

        assertTrue(result.isLeft());
        assertEquals(DomainError.ErrorCode.BUSINESS_RULE, result.getLeft().code());
    }

    /**
     * Minimal wiring of preview + real checkout, backed by temporary CSV
     * stores, so the two can be directly compared.
     */
    private static final class TestContext {

        final Mediator mediator;
        final ProductRepository productRepository;
        final CustomerRepository customerRepository;

        TestContext(Mediator mediator, ProductRepository productRepository,
                    CustomerRepository customerRepository) {
            this.mediator = mediator;
            this.productRepository = productRepository;
            this.customerRepository = customerRepository;
        }

        static TestContext create() throws Exception {
            Path dir = Files.createTempDirectory("konbini-preview-checkout");
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
            customers.add(Customer.builder()
                    .id("CUS0001")
                    .name("Juan Dela Cruz")
                    .seniorCitizen(false)
                    .build());
            customers.add(Customer.builder()
                    .id("CUS0002")
                    .name("Maria Santos")
                    .seniorCitizen(true)
                    .membershipCard(MembershipCard.builder()
                            .id("CAR0001")
                            .cardNumber("MEM-0002")
                            .expiryDate(LocalDate.now().plusYears(1))
                            .points(100)
                            .build())
                    .build());

            CsvTransactionRepository transactions = new CsvTransactionRepository(csvStore, config);
            CsvEmployeeRepository employees = new CsvEmployeeRepository(csvStore, config);
            IdentifierGenerator identifierGenerator = new FileIdentifierGenerator(config);
            UnitOfWork unitOfWork = new JacksonCsvUnitOfWork(products, customers, employees, transactions);

            SimpleMediator mediator = new SimpleMediator();
            mediator.register(PreviewCheckoutQuery.class,
                    new PreviewCheckoutQueryHandler(customers, products));
            mediator.register(ProcessCheckoutCommand.class,
                    new ProcessCheckoutCommandHandler(customers, products, transactions,
                            identifierGenerator, unitOfWork));

            return new TestContext(mediator, products, customers);
        }
    }
}
