package com.konbini.view;

import com.konbini.model.Transaction;

import java.time.LocalDate;
import java.util.List;

public interface TransactionView extends BaseView {
    void displayTransactionMenu();
    int getTransactionMenuChoice();
    void displayTransactions(List<Transaction> transactions);
    void displayTransaction(Transaction transaction);
    void displayReceipt(String receipt);
    void displayTotalSales(double totalSales);
    void displayTotalSalesByDate(LocalDate date, double totalSales);
    void displayTotalSalesByDateRange(LocalDate startDate, LocalDate endDate, double totalSales);
}
