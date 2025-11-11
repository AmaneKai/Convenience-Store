package com.konbini.view;

import java.time.LocalDate;
import java.util.List;

import com.konbini.dto.TransactionDTO;

/**
 * Defines the user interface contract specifically for managing and displaying transaction and sales data.
 * It extends BaseView to inherit fundamental display and input capabilities.
 * 
 * IMPORTANT: This interface uses TransactionDTO exclusively - no model imports.
 * Controllers are responsible for converting Transaction models to TransactionDTOs.
 */
public interface TransactionView extends BaseView {
    /**
     * Displays the primary menu options available within the transaction management section
     * (e.g., view all transactions, view sales reports).
     */
    void displayTransactionMenu();

    /**
     * Prompts the user for and retrieves the selection from the transaction management menu.
     * The implementation must ensure the input is a valid menu option.
     *
     * @return The integer corresponding to the user's selected menu item.
     */
    int getTransactionMenuChoice();

    /**
     * Displays a formatted summary list of multiple transactions.
     * This typically includes key details like Transaction ID, customer name, date, and total.
     *
     * @param transactions The list of TransactionDTO objects to be displayed.
     */
    void displayTransactions(List<TransactionDTO> transactions);

    /**
     * Displays the full, detailed summary of a single transaction, including itemized list,
     * payment details, discounts, and loyalty points.
     *
     * @param transaction The TransactionDTO object whose details are to be displayed.
     */
    void displayTransaction(TransactionDTO transaction);

    /**
     * Displays a pre-formatted receipt string, typically immediately after a successful checkout.
     *
     * @param receipt The generated receipt text.
     */
    void displayReceipt(String receipt);

    /**
     * Displays the total accumulated sales across all recorded transactions.
     *
     * @param totalSales The calculated total sales amount.
     */
    void displayTotalSales(double totalSales);

    /**
     * Displays the total sales amount achieved on a specific date.
     *
     * @param date The date for which the sales were calculated.
     * @param totalSales The total sales amount for that date.
     */
    void displayTotalSalesByDate(LocalDate date, double totalSales);

    /**
     * Displays the total sales amount accumulated within a specific date range.
     *
     * @param startDate The start date of the range (inclusive).
     * @param endDate The end date of the range (inclusive).
     * @param totalSales The total sales amount for the date range.
     */
    void displayTotalSalesByDateRange(LocalDate startDate, LocalDate endDate, double totalSales);
}