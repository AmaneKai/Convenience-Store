package com.konbini.application.session;

import com.konbini.application.dto.EmployeeDTO;
import io.vavr.control.Option;

/**
 * Port for the current authenticated employee session. The concrete
 * implementation lives in the infrastructure layer and is managed as a
 * singleton by the IoC container, replacing the legacy static session.
 */
public interface SessionContext {

    /**
     * Returns the currently authenticated employee, if any.
     *
     * @return the current employee or None
     */
    Option<EmployeeDTO> getCurrentEmployee();

    /**
     * Establishes the session for the given employee.
     *
     * @param employee the employee to authenticate
     */
    void setCurrentEmployee(EmployeeDTO employee);

    /**
     * Returns whether an employee is currently authenticated.
     *
     * @return true if a session is active
     */
    boolean isAuthenticated();

    /**
     * Terminates the current session.
     */
    void logout();
}
