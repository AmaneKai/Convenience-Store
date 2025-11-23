package com.konbini.view;

import java.time.LocalDate;
import java.util.List;

import com.konbini.dto.TransactionDTO;

/**
 * View interface for transaction management and sales reporting operations.
 * Extends BaseView to provide transaction-specific user interactions
 * including transaction viewing, receipt display, and sales analytics.
 */
public interface TransactionView extends BaseView {

    /**
     * Displays the transaction management menu to the user.
     * Typically includes options for viewing transactions, generating receipts,
     * and accessing sales reports and analytics.
     */
    void displayTransactionMenu();

    /**
     * Gets the user's selection from the transaction management menu.
     *
     * @return the user's menu choice as an integer
     */
    int getTransactionMenuChoice();

    /**
     * Displays a list of transactions to the user.
     * Shows transaction information in a list format, typically with
     * summary details for each transaction.
     *
     * @param transactions the list of TransactionDTO objects to display
     */
    void displayTransactions(List<TransactionDTO> transactions);

    /**
     * Displays detailed information for a single transaction.
     * Shows comprehensive transaction details including customer information,
     * items purchased, financial breakdown, and loyalty points.
     *
     * @param transaction the TransactionDTO containing detailed transaction information to display
     */
    void displayTransaction(TransactionDTO transaction);

    /**
     * Displays a formatted receipt for a transaction.
     * Shows a professionally formatted receipt suitable for printing
     * or customer presentation.
     *
     * @param receipt the formatted receipt string to display
     */
    void displayReceipt(String receipt);

    /**
     * Displays the total sales amount across all transactions.
     *
     * @param totalSales the total sales amount to display
     */
    void displayTotalSales(double totalSales);

    /**
     * Displays the total sales amount for a specific date.
     *
     * @param date the date for which sales are being displayed
     * @param totalSales the total sales amount for the specified date
     */
    void displayTotalSalesByDate(LocalDate date, double totalSales);

    /**
     * Displays the total sales amount within a specified date range.
     *
     * @param startDate the start date of the sales period
     * @param endDate the end date of the sales period
     * @param totalSales the total sales amount for the specified date range
     */
    void displayTotalSalesByDateRange(LocalDate startDate, LocalDate endDate, double totalSales);
}