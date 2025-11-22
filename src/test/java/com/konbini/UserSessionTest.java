package com.konbini;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import com.konbini.util.UserSession;

public class UserSessionTest {

    @BeforeEach
    public void setUp() {
        // Ensure we start with a logged-out state
        UserSession.getInstance().logout();
    }

    @Test
    public void testGetInstance() {
        UserSession session1 = UserSession.getInstance();
        UserSession session2 = UserSession.getInstance();

        assertNotNull(session1);
        assertNotNull(session2);
        assertSame(session1, session2, "getInstance should return the same instance (singleton)");
    }

    @Test
    public void testInitialState() {
        UserSession session = UserSession.getInstance();

        assertFalse(session.isLoggedIn());
        assertNull(session.getUserId());
        assertNull(session.getUserType());
        assertFalse(session.isCustomer());
        assertFalse(session.isEmployee());
    }

    @Test
    public void testLoginAsCustomer() {
        UserSession session = UserSession.getInstance();

        session.login("CUSTOMER001", "CUSTOMER");

        assertTrue(session.isLoggedIn());
        assertEquals("CUSTOMER001", session.getUserId());
        assertEquals("CUSTOMER", session.getUserType());
        assertTrue(session.isCustomer());
        assertFalse(session.isEmployee());
    }

    @Test
    public void testLoginAsEmployee() {
        UserSession session = UserSession.getInstance();

        session.login("EMP001", "EMPLOYEE");

        assertTrue(session.isLoggedIn());
        assertEquals("EMP001", session.getUserId());
        assertEquals("EMPLOYEE", session.getUserType());
        assertFalse(session.isCustomer());
        assertTrue(session.isEmployee());
    }

    @Test
    public void testLogout() {
        UserSession session = UserSession.getInstance();

        // Login first
        session.login("EMP001", "EMPLOYEE");
        assertTrue(session.isLoggedIn());

        // Logout
        session.logout();

        assertFalse(session.isLoggedIn());
        assertNull(session.getUserId());
        assertNull(session.getUserType());
        assertFalse(session.isCustomer());
        assertFalse(session.isEmployee());
    }

    @Test
    public void testMultipleLogins() {
        UserSession session = UserSession.getInstance();

        // Login as customer
        session.login("CUSTOMER001", "CUSTOMER");
        assertTrue(session.isCustomer());
        assertFalse(session.isEmployee());

        // Login as different user (employee)
        session.login("EMP001", "EMPLOYEE");
        assertTrue(session.isEmployee());
        assertFalse(session.isCustomer());
        assertEquals("EMP001", session.getUserId());
        assertEquals("EMPLOYEE", session.getUserType());
    }

    @Test
    public void testIsCustomerWhenLoggedOut() {
        UserSession session = UserSession.getInstance();

        session.logout();
        assertFalse(session.isCustomer(), "isCustomer should return false when logged out");
    }

    @Test
    public void testIsEmployeeWhenLoggedOut() {
        UserSession session = UserSession.getInstance();

        session.logout();
        assertFalse(session.isEmployee(), "isEmployee should return false when logged out");
    }

    @Test
    public void testLoginWithNullValues() {
        UserSession session = UserSession.getInstance();

        session.login(null, null);

        assertTrue(session.isLoggedIn(), "Login should succeed even with null values");
        assertNull(session.getUserId());
        assertNull(session.getUserType());
        assertFalse(session.isCustomer());
        assertFalse(session.isEmployee());
    }

    @Test
    public void testCaseSensitiveUserType() {
        UserSession session = UserSession.getInstance();

        // Login with lowercase "customer"
        session.login("CUSTOMER001", "customer");

        assertTrue(session.isLoggedIn());
        assertEquals("customer", session.getUserType());
        assertFalse(session.isCustomer(), "isCustomer should be false for lowercase 'customer'");
        assertFalse(session.isEmployee());

        // Login with uppercase "EMPLOYEE"
        session.login("EMP001", "EMPLOYEE");
        assertTrue(session.isEmployee(), "isEmployee should be true for uppercase 'EMPLOYEE'");
    }

    @Test
    public void testLoginWithEmptyStrings() {
        UserSession session = UserSession.getInstance();

        session.login("", "");

        assertTrue(session.isLoggedIn());
        assertEquals("", session.getUserId());
        assertEquals("", session.getUserType());
        assertFalse(session.isCustomer());
        assertFalse(session.isEmployee());
    }

    @Test
    public void testSessionPersistence() {
        UserSession session1 = UserSession.getInstance();
        session1.login("EMP001", "EMPLOYEE");

        UserSession session2 = UserSession.getInstance();

        // Verify session state persists across getInstance calls
        assertTrue(session2.isLoggedIn());
        assertEquals("EMP001", session2.getUserId());
        assertEquals("EMPLOYEE", session2.getUserType());
        assertTrue(session2.isEmployee());
    }
}
