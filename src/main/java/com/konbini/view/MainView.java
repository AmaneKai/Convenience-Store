package com.konbini.view;

public interface MainView extends BaseView {

    void displayWelcomeMessage();

    void displayMainMenu();

    int getMainMenuChoice();
}
