package com.konbini.view;

import java.time.LocalDate;

public interface BaseView {
    void displayErrorMessage(String message);

    void displaySuccessMessage(String message);

    void displayInfoMessage(String message);

    String getStringInput(String prompt);

    int getIntInput(String prompt);

    double getDoubleInput(String prompt);

    boolean getBooleanInput(String prompt);

    LocalDate getDateInput(String prompt);
}
