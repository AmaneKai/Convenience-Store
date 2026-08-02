package com.konbini.application.session;

import com.konbini.application.dto.CustomerDTO;
import io.vavr.control.Option;

/**
 * Port for the current authenticated customer session. The concrete
 * implementation lives in the infrastructure layer and is managed as a
 * singleton by the IoC container. Separate from {@link SessionContext},
 * which tracks the authenticated staff session.
 */
public interface CustomerSessionContext {

    /**
     * Returns the currently authenticated customer, if any.
     *
     * @return the current customer or None
     */
    Option<CustomerDTO> getCurrentCustomer();

    /**
     * Establishes the session for the given customer.
     *
     * @param customer the customer to authenticate
     */
    void setCurrentCustomer(CustomerDTO customer);

    /**
     * Returns whether a customer is currently authenticated.
     *
     * @return true if a session is active
     */
    boolean isAuthenticated();

    /**
     * Terminates the current session.
     */
    void logout();
}
