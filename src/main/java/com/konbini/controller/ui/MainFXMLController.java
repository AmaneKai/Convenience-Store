package com.konbini.controller.ui;

import com.konbini.controller.CartManagementController;
import com.konbini.controller.CustomerManagementController;
import com.konbini.controller.DataManagementController;
import com.konbini.controller.ProductManagementController;
import com.konbini.controller.TransactionManagementController;
import com.konbini.view.JavaFXStoreView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.application.Platform;

public class MainFXMLController {
    private final ProductManagementController productManagementController;
    private final CustomerManagementController customerManagementController;
    private final CartManagementController cartManagementController;
    private final TransactionManagementController transactionManagementController;
    private final DataManagementController dataManagementController;

    private JavaFXStoreView view;

    public MainFXMLController(
        ProductManagementController productManagementController,
        CustomerManagementController customerManagementController,
        CartManagementController cartManagementController,
        TransactionManagementController transactionManagementController,
        DataManagementController dataManagementController,
        JavaFXStoreView view){

            this.productManagementController = productManagementController;
            this.customerManagementController = customerManagementController;
            this.cartManagementController = cartManagementController;
            this.transactionManagementController = transactionManagementController;
            this.dataManagementController = dataManagementController;
            this.view = view;
    }

    @FXML
    private void clickProductManagement(ActionEvent e){
        view.showProductManagement();
    }

    @FXML
    private void clickCustomerManagement(ActionEvent e){
        // customerManagementController.handleCustomerManagement();
    }

    @FXML
    private void clickShoppingCart(ActionEvent e){
        //cartManagementController.handleCartManagement();
    }

    @FXML
    private void clickTransactionManagement(ActionEvent e){
        //transactionManagementController.handleTransactionManagement();
    }

    @FXML
    private void clickSaveData(ActionEvent e){
        dataManagementController.handleSaveData();
    }

    @FXML
    private void clickLoadData(ActionEvent e){
        dataManagementController.handleLoadData();
    }

    @FXML
    private void clickExit(ActionEvent e){
        Platform.exit();
    }
}
