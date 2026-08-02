package com.konbini.infrastructure.session;

import com.konbini.application.dto.CustomerDTO;
import com.konbini.application.session.CustomerSessionContext;
import io.vavr.control.Option;
import java.util.concurrent.atomic.AtomicReference;

/**
 * In-memory {@link CustomerSessionContext} holding the current authenticated
 * customer. Managed as a singleton by the IoC container.
 */
public class DefaultCustomerSessionContext implements CustomerSessionContext {

    private final AtomicReference<CustomerDTO> currentCustomer = new AtomicReference<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Option<CustomerDTO> getCurrentCustomer() {
        return Option.of(currentCustomer.get());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCurrentCustomer(CustomerDTO customer) {
        currentCustomer.set(customer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAuthenticated() {
        return currentCustomer.get() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logout() {
        currentCustomer.set(null);
    }
}
