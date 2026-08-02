package com.konbini.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.konbini.application.command.AuthenticateCustomerCommand;
import com.konbini.application.command.CustomerSignUpCommand;
import com.konbini.application.command.SetCustomerPasswordCommand;
import com.konbini.application.dto.CustomerDTO;
import com.konbini.application.handler.AuthenticateCustomerCommandHandler;
import com.konbini.application.handler.CustomerSignUpCommandHandler;
import com.konbini.application.handler.SetCustomerPasswordCommandHandler;
import com.konbini.application.mediator.Mediator;
import com.konbini.application.mediator.SimpleMediator;
import com.konbini.application.session.CustomerSessionContext;
import com.konbini.application.validation.CustomerValidator;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.common.IdentifierGenerator;
import com.konbini.domain.customer.Customer;
import com.konbini.domain.customer.CustomerRepository;
import com.konbini.domain.employee.PasswordHasher;
import com.konbini.domain.unitofwork.UnitOfWork;
import com.konbini.infrastructure.config.StoreConfig;
import com.konbini.infrastructure.csv.CsvStore;
import com.konbini.infrastructure.id.FileIdentifierGenerator;
import com.konbini.infrastructure.repository.CsvCustomerRepository;
import com.konbini.infrastructure.repository.CsvEmployeeRepository;
import com.konbini.infrastructure.repository.CsvProductRepository;
import com.konbini.infrastructure.repository.CsvTransactionRepository;
import com.konbini.infrastructure.security.BCryptPasswordHasher;
import com.konbini.infrastructure.session.DefaultCustomerSessionContext;
import com.konbini.infrastructure.unitofwork.JacksonCsvUnitOfWork;
import io.vavr.control.Either;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/**
 * Covers the customer self-service login surface: sign-up hashes the
 * password, authentication accepts only a matching password against a
 * customer that has one set, and staff can enable login for a legacy
 * customer via {@link SetCustomerPasswordCommand}.
 */
class CustomerAuthenticationTest {

    @Test
    void signUpHashesPasswordAndAllowsLogin() throws Exception {
        TestContext context = TestContext.create();

        Either<DomainError, CustomerDTO> signUp = context.mediator.send(
                new CustomerSignUpCommand("New Customer", false, "secret1"));
        assertTrue(signUp.isRight(), () -> "Expected success, got: " + signUp.getLeft());
        CustomerDTO created = signUp.get();
        assertTrue(created.hasPassword());

        Customer stored = context.customerRepository.findById(created.id()).orElseThrow();
        assertNotEquals("secret1", stored.getPasswordHash(),
                "Password must never be stored in plaintext");

        Either<DomainError, CustomerDTO> login = context.mediator.send(
                new AuthenticateCustomerCommand(created.id(), "secret1"));
        assertTrue(login.isRight(), () -> "Expected success, got: " + login.getLeft());
        assertTrue(context.sessionContext.isAuthenticated());
    }

    @Test
    void signUpRejectsShortPassword() throws Exception {
        TestContext context = TestContext.create();

        Either<DomainError, CustomerDTO> result = context.mediator.send(
                new CustomerSignUpCommand("Short Password", false, "abc"));

        assertTrue(result.isLeft());
        assertEquals(DomainError.ErrorCode.VALIDATION, result.getLeft().code());
    }

    @Test
    void authenticateRejectsWrongPassword() throws Exception {
        TestContext context = TestContext.create();
        CustomerDTO created = context.mediator
                .<CustomerSignUpCommand, CustomerDTO>send(
                        new CustomerSignUpCommand("Owner", false, "correct1"))
                .get();

        Either<DomainError, CustomerDTO> result = context.mediator.send(
                new AuthenticateCustomerCommand(created.id(), "wrong-password"));

        assertTrue(result.isLeft());
        assertEquals(DomainError.ErrorCode.UNAUTHORIZED, result.getLeft().code());
    }

    @Test
    void authenticateRejectsUnknownCustomer() throws Exception {
        TestContext context = TestContext.create();

        Either<DomainError, CustomerDTO> result = context.mediator.send(
                new AuthenticateCustomerCommand("CUS9999", "whatever1"));

        assertTrue(result.isLeft());
        assertEquals(DomainError.ErrorCode.UNAUTHORIZED, result.getLeft().code());
    }

    @Test
    void authenticateRejectsCustomerWithoutPasswordSet() throws Exception {
        TestContext context = TestContext.create();
        context.customerRepository.add(Customer.builder()
                .id("CUS0001")
                .name("Legacy Customer")
                .seniorCitizen(false)
                .build());

        Either<DomainError, CustomerDTO> result = context.mediator.send(
                new AuthenticateCustomerCommand("CUS0001", "anything1"));

        assertTrue(result.isLeft());
        assertEquals(DomainError.ErrorCode.UNAUTHORIZED, result.getLeft().code());
    }

    @Test
    void staffCanEnableLoginForLegacyCustomer() throws Exception {
        TestContext context = TestContext.create();
        context.customerRepository.add(Customer.builder()
                .id("CUS0001")
                .name("Legacy Customer")
                .seniorCitizen(false)
                .build());

        Either<DomainError, CustomerDTO> setPassword = context.mediator.send(
                new SetCustomerPasswordCommand("CUS0001", "newpass1"));
        assertTrue(setPassword.isRight(), () -> "Expected success, got: " + setPassword.getLeft());
        assertTrue(setPassword.get().hasPassword());

        Either<DomainError, CustomerDTO> login = context.mediator.send(
                new AuthenticateCustomerCommand("CUS0001", "newpass1"));
        assertTrue(login.isRight(), () -> "Expected success, got: " + login.getLeft());
    }

    @Test
    void setPasswordRejectsUnknownCustomer() throws Exception {
        TestContext context = TestContext.create();

        Either<DomainError, CustomerDTO> result = context.mediator.send(
                new SetCustomerPasswordCommand("CUS9999", "validpass"));

        assertTrue(result.isLeft());
        assertEquals(DomainError.ErrorCode.NOT_FOUND, result.getLeft().code());
    }

    /**
     * Minimal wiring of the customer authentication handlers backed by
     * temporary CSV stores.
     */
    private static final class TestContext {

        final Mediator mediator;
        final CustomerRepository customerRepository;
        final CustomerSessionContext sessionContext;

        TestContext(Mediator mediator, CustomerRepository customerRepository,
                    CustomerSessionContext sessionContext) {
            this.mediator = mediator;
            this.customerRepository = customerRepository;
            this.sessionContext = sessionContext;
        }

        static TestContext create() throws Exception {
            Path dir = Files.createTempDirectory("konbini-customer-auth");
            StoreConfig config = new StoreConfig(dir);
            CsvStore csvStore = new CsvStore();

            CsvCustomerRepository customers = new CsvCustomerRepository(csvStore, config);
            CsvProductRepository products = new CsvProductRepository(csvStore, config);
            CsvEmployeeRepository employees = new CsvEmployeeRepository(csvStore, config);
            CsvTransactionRepository transactions = new CsvTransactionRepository(csvStore, config);

            IdentifierGenerator identifierGenerator = new FileIdentifierGenerator(config);
            UnitOfWork unitOfWork = new JacksonCsvUnitOfWork(products, customers, employees, transactions);
            PasswordHasher passwordHasher = new BCryptPasswordHasher();
            CustomerSessionContext sessionContext = new DefaultCustomerSessionContext();
            CustomerValidator validator = new CustomerValidator();

            SimpleMediator mediator = new SimpleMediator();
            mediator.register(CustomerSignUpCommand.class,
                    new CustomerSignUpCommandHandler(customers, identifierGenerator,
                            passwordHasher, unitOfWork, validator));
            mediator.register(AuthenticateCustomerCommand.class,
                    new AuthenticateCustomerCommandHandler(customers, passwordHasher,
                            sessionContext, validator));
            mediator.register(SetCustomerPasswordCommand.class,
                    new SetCustomerPasswordCommandHandler(customers, passwordHasher,
                            unitOfWork, validator));

            return new TestContext(mediator, customers, sessionContext);
        }
    }
}
