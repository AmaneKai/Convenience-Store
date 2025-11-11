package com.konbini.controller.ui;

import com.konbini.controller.ProductManagementController;
import com.konbini.view.JavaFXStoreView;
import com.konbini.dto.ProductDTO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import java.util.List;

public class ProductManagementFXMLController {
    private final ProductManagementController productManagementController;

    @FXML
    private TableView<ProductDTO> productTable;

    private JavaFXStoreView view;

    public ProductManagementFXMLController(
        ProductManagementController productManagementController,
        JavaFXStoreView view){
        this.productManagementController = productManagementController;
        this.view = view;
    }

    @FXML
    private void clickViewProducts(ActionEvent e){
        List<ProductDTO> products = productManagementController.handleGetAllProducts();
        ObservableList<ProductDTO> items = FXCollections.observableArrayList(products);

        productTable.setItems(items);
    }

    @FXML
    private void clickViewProductsCategory(ActionEvent e){

    }

    @FXML
    private void clickViewProductsSubcategory(ActionEvent e){

    }

    @FXML
    private void clickSearchProducts(ActionEvent e){

    }

    @FXML
    private void clickViewLowStockProducts(ActionEvent e){

    }

    @FXML
    private void clickViewExpiredProducts(ActionEvent e){

    }

    @FXML
    private void clickAddProduct(ActionEvent e){

    }

    @FXML
    private void clickUpdateProduct(ActionEvent e){

    }

    @FXML
    private void clickRemoveProduct(ActionEvent e){

    }

    @FXML
    private void clickRestockProduct(ActionEvent e){

    }

    @FXML
    private void clickBackToMain(ActionEvent e){
        view.showMainMenu();
    }
}
