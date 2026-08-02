package com.konbini.infrastructure.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.konbini.application.command.AddEmployeeCommand;
import com.konbini.application.command.AddProductCommand;
import com.konbini.application.command.AuthenticateCustomerCommand;
import com.konbini.application.command.AuthenticateEmployeeCommand;
import com.konbini.application.command.CustomerSignUpCommand;
import com.konbini.application.command.ProcessCheckoutCommand;
import com.konbini.application.command.RegisterCustomerCommand;
import com.konbini.application.command.RemoveEmployeeCommand;
import com.konbini.application.command.RemoveProductCommand;
import com.konbini.application.command.RestockProductCommand;
import com.konbini.application.command.SetCustomerPasswordCommand;
import com.konbini.application.command.UpdateEmployeeCommand;
import com.konbini.application.command.UpdateProductCommand;
import com.konbini.application.handler.AddEmployeeCommandHandler;
import com.konbini.application.handler.AddProductCommandHandler;
import com.konbini.application.handler.AuthenticateCustomerCommandHandler;
import com.konbini.application.handler.AuthenticateEmployeeCommandHandler;
import com.konbini.application.handler.CustomerSignUpCommandHandler;
import com.konbini.application.handler.GetCustomersQueryHandler;
import com.konbini.application.handler.GetDashboardQueryHandler;
import com.konbini.application.handler.GetEmployeesQueryHandler;
import com.konbini.application.handler.GetProductsQueryHandler;
import com.konbini.application.handler.GetTransactionsQueryHandler;
import com.konbini.application.handler.PreviewCheckoutQueryHandler;
import com.konbini.application.handler.ProcessCheckoutCommandHandler;
import com.konbini.application.handler.RegisterCustomerCommandHandler;
import com.konbini.application.handler.RemoveEmployeeCommandHandler;
import com.konbini.application.handler.RemoveProductCommandHandler;
import com.konbini.application.handler.RestockProductCommandHandler;
import com.konbini.application.handler.SetCustomerPasswordCommandHandler;
import com.konbini.application.handler.UpdateEmployeeCommandHandler;
import com.konbini.application.handler.UpdateProductCommandHandler;
import com.konbini.application.mediator.Mediator;
import com.konbini.application.mediator.SimpleMediator;
import com.konbini.application.query.GetCustomersQuery;
import com.konbini.application.query.GetDashboardQuery;
import com.konbini.application.query.GetEmployeesQuery;
import com.konbini.application.query.GetProductsQuery;
import com.konbini.application.query.GetTransactionsQuery;
import com.konbini.application.query.PreviewCheckoutQuery;
import com.konbini.application.session.CustomerSessionContext;
import com.konbini.application.session.SessionContext;
import com.konbini.application.validation.CustomerValidator;
import com.konbini.application.validation.EmployeeValidator;
import com.konbini.application.validation.ProductValidator;
import com.konbini.domain.common.IdentifierGenerator;
import com.konbini.domain.customer.CustomerRepository;
import com.konbini.domain.employee.EmployeeRepository;
import com.konbini.domain.employee.PasswordHasher;
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
import com.konbini.infrastructure.security.BCryptPasswordHasher;
import com.konbini.infrastructure.session.DefaultCustomerSessionContext;
import com.konbini.infrastructure.session.DefaultSessionContext;
import com.konbini.infrastructure.unitofwork.JacksonCsvUnitOfWork;

/**
 * Guice module wiring the clean-architecture layers together: infrastructure
 * implementations are bound to domain ports via {@link Provides} methods, and
 * all CQRS handlers are registered against the mediator with exactly one
 * handler per request type.
 */
public class StoreModule extends AbstractModule {

    private final StoreConfig config;

    /**
     * Constructs the module with the given store configuration.
     *
     * @param config the store configuration
     */
    public StoreModule(StoreConfig config) {
        this.config = config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        bind(StoreConfig.class).toInstance(config);
    }

    /**
     * Provides the CSV store.
     *
     * @return the CSV store
     */
    @Provides
    @Singleton
    CsvStore provideCsvStore() {
        return new CsvStore();
    }

    /**
     * Provides the identifier generator.
     *
     * @return the identifier generator
     */
    @Provides
    @Singleton
    IdentifierGenerator provideIdentifierGenerator() {
        return new FileIdentifierGenerator(config);
    }

    /**
     * Provides the password hasher.
     *
     * @return the password hasher
     */
    @Provides
    @Singleton
    PasswordHasher providePasswordHasher() {
        return new BCryptPasswordHasher();
    }

    /**
     * Provides the session context.
     *
     * @return the session context
     */
    @Provides
    @Singleton
    SessionContext provideSessionContext() {
        return new DefaultSessionContext();
    }

    /**
     * Provides the customer session context.
     *
     * @return the customer session context
     */
    @Provides
    @Singleton
    CustomerSessionContext provideCustomerSessionContext() {
        return new DefaultCustomerSessionContext();
    }

    /**
     * Provides the product repository.
     *
     * @param csvStore the CSV store
     * @return the product repository
     */
    @Provides
    @Singleton
    ProductRepository provideProductRepository(CsvStore csvStore) {
        return new CsvProductRepository(csvStore, config);
    }

    /**
     * Provides the customer repository.
     *
     * @param csvStore the CSV store
     * @return the customer repository
     */
    @Provides
    @Singleton
    CustomerRepository provideCustomerRepository(CsvStore csvStore) {
        return new CsvCustomerRepository(csvStore, config);
    }

    /**
     * Provides the employee repository.
     *
     * @param csvStore the CSV store
     * @return the employee repository
     */
    @Provides
    @Singleton
    EmployeeRepository provideEmployeeRepository(CsvStore csvStore) {
        return new CsvEmployeeRepository(csvStore, config);
    }

    /**
     * Provides the transaction repository.
     *
     * @param csvStore the CSV store
     * @return the transaction repository
     */
    @Provides
    @Singleton
    TransactionRepository provideTransactionRepository(CsvStore csvStore) {
        return new CsvTransactionRepository(csvStore, config);
    }

    /**
     * Provides the unit of work.
     *
     * @param productRepository the product repository
     * @param customerRepository the customer repository
     * @param employeeRepository the employee repository
     * @param transactionRepository the transaction repository
     * @return the unit of work
     */
    @Provides
    @Singleton
    UnitOfWork provideUnitOfWork(ProductRepository productRepository,
                                 CustomerRepository customerRepository,
                                 EmployeeRepository employeeRepository,
                                 TransactionRepository transactionRepository) {
        return new JacksonCsvUnitOfWork((CsvProductRepository) productRepository,
                (CsvCustomerRepository) customerRepository,
                (CsvEmployeeRepository) employeeRepository,
                (CsvTransactionRepository) transactionRepository);
    }

    /**
     * Builds the mediator and registers every command and query handler.
     *
     * @param productRepository the product repository
     * @param customerRepository the customer repository
     * @param employeeRepository the employee repository
     * @param transactionRepository the transaction repository
     * @param identifierGenerator the identifier generator
     * @param unitOfWork the unit of work
     * @param passwordHasher the password hasher
     * @param sessionContext the session context
     * @param customerSessionContext the customer session context
     * @return the fully wired mediator
     */
    @Provides
    @Singleton
    Mediator provideMediator(ProductRepository productRepository,
                             CustomerRepository customerRepository,
                             EmployeeRepository employeeRepository,
                             TransactionRepository transactionRepository,
                             IdentifierGenerator identifierGenerator,
                             UnitOfWork unitOfWork,
                             PasswordHasher passwordHasher,
                             SessionContext sessionContext,
                             CustomerSessionContext customerSessionContext) {
        ProductValidator productValidator = new ProductValidator();
        CustomerValidator customerValidator = new CustomerValidator();
        EmployeeValidator employeeValidator = new EmployeeValidator();

        SimpleMediator mediator = new SimpleMediator();

        mediator.register(AddProductCommand.class,
                new AddProductCommandHandler(productRepository, identifierGenerator,
                        unitOfWork, productValidator));
        mediator.register(UpdateProductCommand.class,
                new UpdateProductCommandHandler(productRepository, unitOfWork));
        mediator.register(RemoveProductCommand.class,
                new RemoveProductCommandHandler(productRepository, unitOfWork));
        mediator.register(RestockProductCommand.class,
                new RestockProductCommandHandler(productRepository, unitOfWork, productValidator));
        mediator.register(ProcessCheckoutCommand.class,
                new ProcessCheckoutCommandHandler(customerRepository, productRepository,
                        transactionRepository, identifierGenerator, unitOfWork));
        mediator.register(RegisterCustomerCommand.class,
                new RegisterCustomerCommandHandler(customerRepository, identifierGenerator,
                        unitOfWork, customerValidator));
        mediator.register(CustomerSignUpCommand.class,
                new CustomerSignUpCommandHandler(customerRepository, identifierGenerator,
                        passwordHasher, unitOfWork, customerValidator));
        mediator.register(AuthenticateCustomerCommand.class,
                new AuthenticateCustomerCommandHandler(customerRepository, passwordHasher,
                        customerSessionContext, customerValidator));
        mediator.register(SetCustomerPasswordCommand.class,
                new SetCustomerPasswordCommandHandler(customerRepository, passwordHasher,
                        unitOfWork, customerValidator));
        mediator.register(AuthenticateEmployeeCommand.class,
                new AuthenticateEmployeeCommandHandler(employeeRepository, passwordHasher,
                        sessionContext, employeeValidator));
        mediator.register(AddEmployeeCommand.class,
                new AddEmployeeCommandHandler(employeeRepository, identifierGenerator,
                        passwordHasher, unitOfWork, employeeValidator));
        mediator.register(UpdateEmployeeCommand.class,
                new UpdateEmployeeCommandHandler(employeeRepository, unitOfWork));
        mediator.register(RemoveEmployeeCommand.class,
                new RemoveEmployeeCommandHandler(employeeRepository, unitOfWork));

        mediator.register(GetProductsQuery.class,
                new GetProductsQueryHandler(productRepository));
        mediator.register(GetCustomersQuery.class,
                new GetCustomersQueryHandler(customerRepository));
        mediator.register(GetEmployeesQuery.class,
                new GetEmployeesQueryHandler(employeeRepository));
        mediator.register(GetTransactionsQuery.class,
                new GetTransactionsQueryHandler(transactionRepository));
        mediator.register(GetDashboardQuery.class,
                new GetDashboardQueryHandler(productRepository, customerRepository,
                        transactionRepository));
        mediator.register(PreviewCheckoutQuery.class,
                new PreviewCheckoutQueryHandler(customerRepository, productRepository));

        return mediator;
    }
}
