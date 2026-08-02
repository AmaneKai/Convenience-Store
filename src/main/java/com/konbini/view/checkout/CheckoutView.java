package com.konbini.view.checkout;

import com.google.inject.Injector;
import com.konbini.application.command.ProcessCheckoutCommand;
import com.konbini.application.dto.CustomerDTO;
import com.konbini.application.dto.ProductDTO;
import com.konbini.application.dto.TransactionDTO;
import com.konbini.application.mediator.Mediator;
import com.konbini.application.query.GetCustomersQuery;
import com.konbini.application.query.GetProductsQuery;
import com.konbini.domain.common.DomainError;
import com.konbini.view.Fx;
import io.vavr.control.Either;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Checkout view: select a customer, scan products, redeem loyalty points and
 * process payment through the mediator.
 */
public class CheckoutView {

    private final Mediator mediator;
    private final ObservableList<CustomerDTO> customers = FXCollections.observableArrayList();
    private final ObservableList<ProductDTO> products = FXCollections.observableArrayList();
    private final Map<String, Integer> cart = new LinkedHashMap<>();
    private final ObservableList<CartLine> cartLines = FXCollections.observableArrayList();

    private final ComboBox<CustomerDTO> customerBox = new ComboBox<>(customers);
    private final ComboBox<ProductDTO> productBox = new ComboBox<>(products);
    private final TextField quantityField = new TextField("1");
    private final TableView<CartLine> cartTable = new TableView<>(cartLines);
    private final TextField pointsField = new TextField("0");
    private final TextField paymentField = new TextField();
    private final Label summaryLabel = new Label();
    private final Label statusLabel = new Label(" ");

    /**
     * Constructs the checkout view.
     *
     * @param injector the Guice injector
     */
    public CheckoutView(Injector injector) {
        this.mediator = injector.getInstance(Mediator.class);
        customerBox.setConverter(new CustomerStringConverter());
        productBox.setConverter(new ProductStringConverter());
        customerBox.setPromptText("Select customer");
        productBox.setPromptText("Select product");
        refresh();
    }

    /**
     * Returns the checkout view root node.
     *
     * @return the root node
     */
    public Node getRoot() {
        Label title = new Label("Checkout");
        title.getStyleClass().add("title-label");

        Label subtitle = new Label("Point of sale");
        subtitle.getStyleClass().add("subtitle-label");

        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(event -> refresh());

        Label customerLabel = new Label("Customer");
        Label productLabel = new Label("Product");
        Label qtyLabel = new Label("Qty");

        Button addButton = new Button("Add to Cart");
        addButton.setOnAction(event -> addToCart());

        HBox scanRow = new HBox(10, customerLabel, customerBox,
                productLabel, productBox, qtyLabel, quantityField, addButton);
        scanRow.setAlignment(Pos.CENTER_LEFT);

        HBox.setHgrow(customerBox, Priority.ALWAYS);
        HBox.setHgrow(productBox, Priority.ALWAYS);

        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        configureCartColumns();

        Button removeButton = new Button("Remove Selected");
        removeButton.getStyleClass().add("secondary-button");
        removeButton.setOnAction(event -> removeSelected());

        Button clearButton = new Button("Clear Cart");
        clearButton.getStyleClass().add("secondary-button");
        clearButton.setOnAction(event -> clearCart());

        HBox cartActions = new HBox(10, removeButton, clearButton);

        Label pointsLabel = new Label("Points to redeem");
        Label paymentLabel = new Label("Cash received");
        Button payButton = new Button("Process Payment");
        payButton.setOnAction(event -> processPayment());

        summaryLabel.getStyleClass().add("money");
        statusLabel.getStyleClass().add("status-label");

        HBox paymentRow = new HBox(10, pointsLabel, pointsField,
                paymentLabel, paymentField, payButton);
        paymentRow.setAlignment(Pos.CENTER_LEFT);
        pointsField.setPrefWidth(70);
        paymentField.setPrefWidth(110);

        VBox root = new VBox(16, title, subtitle, refreshButton, scanRow,
                cartTable, cartActions, paymentRow, summaryLabel, statusLabel);
        root.getStyleClass().add("card");
        VBox.setVgrow(cartTable, Priority.ALWAYS);
        return root;
    }

    /**
     * Configures the cart table columns.
     */
    private void configureCartColumns() {
        cartTable.getColumns().setAll(
                Fx.stringColumn("Product", CartLine::name),
                Fx.stringColumn("Price", line -> Fx.money(line.price())),
                Fx.stringColumn("Qty", line -> String.valueOf(line.quantity())),
                Fx.stringColumn("Subtotal", line -> Fx.money(line.subtotal())));
    }

    /**
     * Adds the selected product to the cart.
     */
    private void addToCart() {
        ProductDTO product = productBox.getValue();
        if (product == null) {
            showError("Select a product");
            return;
        }
        try {
            int quantity = Integer.parseInt(quantityField.getText());
            if (quantity <= 0) {
                showError("Quantity must be greater than 0");
                return;
            }
            cart.merge(product.id(), quantity, Integer::sum);
            rebuildCartLines();
            productBox.setValue(null);
            quantityField.setText("1");
        } catch (NumberFormatException exception) {
            showError("Enter a valid quantity");
        }
    }

    /**
     * Removes the selected cart line.
     */
    private void removeSelected() {
        CartLine line = cartTable.getSelectionModel().getSelectedItem();
        if (line != null) {
            cart.remove(line.productId());
            rebuildCartLines();
        }
    }

    /**
     * Clears the entire cart.
     */
    private void clearCart() {
        cart.clear();
        rebuildCartLines();
    }

    /**
     * Rebuilds the cart line list from the cart map.
     */
    private void rebuildCartLines() {
        cartLines.clear();
        Map<String, ProductDTO> byId = new java.util.HashMap<>();
        for (ProductDTO product : products) {
            byId.put(product.id(), product);
        }
        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            ProductDTO product = byId.get(entry.getKey());
            if (product != null) {
                cartLines.add(new CartLine(product, entry.getValue()));
            }
        }
        updateSummary();
    }

    /**
     * Updates the running total label.
     */
    private void updateSummary() {
        java.math.BigDecimal subtotal = cartLines.stream()
                .map(CartLine::subtotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        summaryLabel.setText("Items: " + cartLines.size()
                + "   Subtotal: " + Fx.money(subtotal));
    }

    /**
     * Processes the payment and completes the checkout.
     */
    private void processPayment() {
        CustomerDTO customer = customerBox.getValue();
        if (customer == null) {
            showError("Select a customer");
            return;
        }
        if (cart.isEmpty()) {
            showError("Cart is empty");
            return;
        }
        try {
            java.math.BigDecimal payment =
                    new java.math.BigDecimal(paymentField.getText());
            int points = pointsField.getText().isBlank()
                    ? 0 : Integer.parseInt(pointsField.getText());
            Either<DomainError, TransactionDTO> result =
                    mediator.send(new ProcessCheckoutCommand(
                            customer.id(), new LinkedHashMap<>(cart), payment, points));
            if (result.isRight()) {
                TransactionDTO transaction = result.get();
                statusLabel.getStyleClass().setAll("status-label");
                statusLabel.setText("Receipt " + transaction.id() + " — change "
                        + Fx.money(transaction.change()));
                clearCart();
                paymentField.clear();
                pointsField.setText("0");
                refresh();
            } else {
                showError(Fx.errorMessage(result, "Checkout failed"));
            }
        } catch (NumberFormatException exception) {
            showError("Enter a valid payment and points value");
        }
    }

    /**
     * Shows an error message.
     *
     * @param message the message
     */
    private void showError(String message) {
        statusLabel.getStyleClass().setAll("error-label");
        statusLabel.setText(message);
    }

    /**
     * Reloads customers and products.
     */
    private void refresh() {
        Either<DomainError, List<CustomerDTO>> customerResult =
                mediator.send(new GetCustomersQuery());
        if (customerResult.isRight()) {
            customers.setAll(customerResult.get());
        }
        Either<DomainError, List<ProductDTO>> productResult =
                mediator.send(new GetProductsQuery(null, null));
        if (productResult.isRight()) {
            products.setAll(productResult.get());
        }
    }

    /**
     * Presentation line for the cart table.
     */
    private static final class CartLine {
        private final String productId;
        private final String name;
        private final java.math.BigDecimal price;
        private final int quantity;

        private CartLine(ProductDTO product, int quantity) {
            this.productId = product.id();
            this.name = product.name();
            this.price = product.price();
            this.quantity = quantity;
        }

        String productId() {
            return productId;
        }

        String name() {
            return name;
        }

        java.math.BigDecimal price() {
            return price;
        }

        int quantity() {
            return quantity;
        }

        java.math.BigDecimal subtotal() {
            return price.multiply(java.math.BigDecimal.valueOf(quantity));
        }
    }
}
