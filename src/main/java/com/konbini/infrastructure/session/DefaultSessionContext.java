package com.konbini.infrastructure.session;

import com.konbini.application.dto.EmployeeDTO;
import com.konbini.application.session.SessionContext;
import io.vavr.control.Option;
import java.util.concurrent.atomic.AtomicReference;

/**
 * In-memory {@link SessionContext} holding the current authenticated employee.
 * Managed as a singleton by the IoC container.
 */
public class DefaultSessionContext implements SessionContext {

    private final AtomicReference<EmployeeDTO> currentEmployee = new AtomicReference<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Option<EmployeeDTO> getCurrentEmployee() {
        return Option.of(currentEmployee.get());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCurrentEmployee(EmployeeDTO employee) {
        currentEmployee.set(employee);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAuthenticated() {
        return currentEmployee.get() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logout() {
        currentEmployee.set(null);
    }
}
