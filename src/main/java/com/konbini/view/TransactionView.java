package com.konbini.view;

import java.time.LocalDate;
import java.util.List;

import com.konbini.dto.TransactionDTO;

public interface TransactionView extends BaseView {

    void displayTransactionMenu();

    int getTransactionMenuChoice();

    void displayTransactions(List<TransactionDTO> transactions);

    void displayTransaction(TransactionDTO transaction);

    void displayReceipt(String receipt);

    void displayTotalSales(double totalSales);

    void displayTotalSalesByDate(LocalDate date, double totalSales);

    void displayTotalSalesByDateRange(LocalDate startDate, LocalDate endDate, double totalSales);
}