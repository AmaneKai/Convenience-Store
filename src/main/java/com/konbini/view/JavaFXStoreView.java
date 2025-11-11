package com.konbini.view;

import com.konbini.controller.ProductManagementController;
import com.konbini.controller.CustomerManagementController;
import com.konbini.controller.CartManagementController;
import com.konbini.controller.TransactionManagementController;
import com.konbini.controller.DataManagementController;

import com.konbini.controller.ui.MainFXMLController;
import com.konbini.controller.ui.ProductManagementFXMLController;

import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class JavaFXStoreView {
    private final ProductManagementController productManagementController;
    private final CustomerManagementController customerManagementController;
    private final CartManagementController cartManagementController;
    private final TransactionManagementController transactionManagementController;
    private final DataManagementController dataManagementController;

    private Pane root;
    private Stage primaryStage;

    public JavaFXStoreView(
        ProductManagementController productManagementController,
        CustomerManagementController customerManagementController,
        CartManagementController cartManagementController,
        TransactionManagementController transactionManagementController,
        DataManagementController dataManagementController){

            this.productManagementController = productManagementController;
            this.customerManagementController = customerManagementController;
            this.cartManagementController = cartManagementController;
            this.transactionManagementController = transactionManagementController;
            this.dataManagementController = dataManagementController;
    }

    public void show(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainContainer.fxml"));

        MainFXMLController mainController = new MainFXMLController(
            productManagementController,
            customerManagementController,
            cartManagementController,
            transactionManagementController,
            dataManagementController,
            this
        );

        loader.setController(mainController);
        root = loader.load();
        Scene scene = new Scene(root);
        Image konbiniIcon = new Image("/assets/icon.png");

        primaryStage.getIcons().add(konbiniIcon);
        primaryStage.setTitle("Konbini Store");
        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);
        primaryStage.setResizable(false);
        primaryStage.setFullScreen(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showProductManagement(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/productManagementContainer.fxml"));

            ProductManagementFXMLController productManagementFXMLController = new ProductManagementFXMLController(
                productManagementController,
                this
            );

            loader.setController(productManagementFXMLController);
            root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        }
        catch(Exception e){
            this.displayErrorMessage("Failed to load Product Management menu: " + e.getMessage());
        }
    }

    public void showMainMenu(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainContainer.fxml"));

            MainFXMLController mainController = new MainFXMLController(
                productManagementController,
                customerManagementController,
                cartManagementController,
                transactionManagementController,
                dataManagementController,
                this
            );

            loader.setController(mainController);
            root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        }
        catch(Exception e){
            this.displayErrorMessage("Failed to load Main Menu: " + e.getMessage());
        }
    }

    // public void showCustomerManagement(){
    //     FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/productManagementContainer.fxml"));

    //     ProductManagementFXMLController productManagementFXMLController = new ProductManagementFXMLController(productManagementController);

    //     loader.setController(productManagementFXMLController);
    //     root = loader.load();
    //     Scene scene = new Scene(root);
    // }

    // public void showCartManagement(){
    //     FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/productManagementContainer.fxml"));

    //     ProductManagementFXMLController productManagementFXMLController = new ProductManagementFXMLController(productManagementController);

    //     loader.setController(productManagementFXMLController);
    //     root = loader.load();
    //     Scene scene = new Scene(root);
    // }

    // public void showTransactionManagement(){
    //     FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/productManagementContainer.fxml"));

    //     ProductManagementFXMLController productManagementFXMLController = new ProductManagementFXMLController(productManagementController);

    //     loader.setController(productManagementFXMLController);
    //     root = loader.load();
    //     Scene scene = new Scene(root);
    // }

    public void displayInfoMessage(String message){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void displayErrorMessage(String message){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void displaySuccessMessage(String message){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
