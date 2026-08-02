package com.konbini.view.portal;

import com.google.inject.Injector;
import com.konbini.application.command.ProcessCheckoutCommand;
import com.konbini.application.dto.CheckoutPreviewDTO;
import com.konbini.application.dto.CustomerDTO;
import com.konbini.application.dto.ProductDTO;
import com.konbini.application.dto.TransactionDTO;
import com.konbini.application.mediator.Mediator;
import com.konbini.application.query.GetProductsQuery;
import com.konbini.application.query.PreviewCheckoutQuery;
import com.konbini.domain.common.DomainError;
import com.konbini.view.Fx;
import io.vavr.control.Either;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * Self-checkout shop: a customer browses a table of in-stock products and
 * presses "Add" on a row to build a cart, reviews the total (with their own
 * senior/points discounts already applied) and places the order themselves.
 */
public class CustomerShopView {

    private final Mediator mediator;
    private final CustomerDTO customer;
    private final ObservableList<ProductDTO> allProducts = FXCollections.observableArrayList();
    private final ObservableList<ProductDTO> catalog = FXCollections.observableArrayList();
    private final Map<String, Integer> cart = new LinkedHashMap<>();
    private final Map<String, Spinner<Integer>> quantitySpinners = new LinkedHashMap<>();
    private final ObservableList<CartLine> cartLines = FXCollections.observableArrayList();

    private final ComboBox<String> categoryFilter = new ComboBox<>();
    private final TableView<ProductDTO> catalogTable = new TableView<>(catalog);
    private final TableView<CartLine> cartTable = new TableView<>(cartLines);
    private final TextField pointsField = new TextField("0");
    private final Label summaryLabel = new Label();
    private final Label statusLabel = new Label(" ");

    private static final String ALL_CATEGORIES = "All Categories";

    /**
     * Constructs the shop view for the given customer.
     *
     * @param injector the Guice injector
     * @param customer the signed-in customer
     */
    public CustomerShopView(Injector injector, CustomerDTO customer) {
        this.mediator = injector.getInstance(Mediator.class);
        this.customer = customer;
        categoryFilter.setValue(ALL_CATEGORIES);
        categoryFilter.setOnAction(event -> applyCategoryFilter());
        refresh();
    }

    /**
     * Returns the shop view root node.
     *
     * @return the root node
     */
    public Node getRoot() {
        Label title = new Label("Shop");
        title.getStyleClass().add("title-label");

        Label subtitle = new Label("Browse products and check out");
        subtitle.getStyleClass().add("subtitle-label");

        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(event -> refresh());

        Label filterLabel = new Label("Category");
        HBox filterRow = new HBox(10, filterLabel, categoryFilter, refreshButton);
        filterRow.setAlignment(Pos.CENTER_LEFT);

        catalogTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        configureCatalogColumns();
        VBox.setVgrow(catalogTable, Priority.ALWAYS);

        Label productsTitle = new Label("Available Products");
        productsTitle.getStyleClass().add("stat-label");

        VBox productsPane = new VBox(10, productsTitle, filterRow, catalogTable);
        productsPane.setMinWidth(420);
        HBox.setHgrow(productsPane, Priority.ALWAYS);

        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        configureCartColumns();
        VBox.setVgrow(cartTable, Priority.ALWAYS);

        Button removeButton = new Button("Remove Selected");
        removeButton.getStyleClass().add("secondary-button");
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction(event -> removeSelected());

        Button clearButton = new Button("Clear Cart");
        clearButton.getStyleClass().add("secondary-button");
        clearButton.setMaxWidth(Double.MAX_VALUE);
        clearButton.setOnAction(event -> clearCart());

        HBox cartActions = new HBox(10, removeButton, clearButton);

        Label pointsLabel = new Label("Points to redeem"
                + (customer.hasMembershipCard() ? " (you have " + customer.points() + ")" : ""));
        pointsLabel.setWrapText(true);
        pointsField.setMaxWidth(Double.MAX_VALUE);
        pointsField.setDisable(!customer.hasMembershipCard());

        Button checkoutButton = new Button("Review & Pay");
        checkoutButton.setMaxWidth(Double.MAX_VALUE);
        checkoutButton.setOnAction(event -> reviewAndPay());

        VBox payRow = new VBox(8, pointsLabel, pointsField, checkoutButton);

        summaryLabel.getStyleClass().add("money");
        statusLabel.getStyleClass().add("status-label");

        Label cartTitle = new Label("Your Cart");
        cartTitle.getStyleClass().add("stat-label");

        Region filterRowSpacer = new Region();
        filterRowSpacer.setPrefHeight(34);
        filterRowSpacer.setMinHeight(34);
        filterRowSpacer.setMaxHeight(34);

        VBox cartPane = new VBox(10, cartTitle, filterRowSpacer, cartTable,
                cartActions, summaryLabel, payRow, statusLabel);
        cartPane.setPrefWidth(280);
        cartPane.setMinWidth(240);
        cartPane.setMaxWidth(300);

        HBox lower = new HBox(20, productsPane, cartPane);
        VBox.setVgrow(lower, Priority.ALWAYS);

        VBox root = new VBox(16, title, subtitle, lower);
        root.getStyleClass().add("card");
        return root;
    }

    /**
     * Configures the product catalog table columns, including an inline
     * quantity spinner and an Add button per row.
     */
    private void configureCatalogColumns() {
        TableColumn<ProductDTO, String> nameColumn = Fx.stringColumn("Product", ProductDTO::name);
        TableColumn<ProductDTO, String> categoryColumn = Fx.stringColumn("Category", ProductDTO::category);
        TableColumn<ProductDTO, String> priceColumn = Fx.stringColumn("Price", p -> Fx.money(p.price()));
        TableColumn<ProductDTO, String> stockColumn = Fx.stringColumn("In Stock", p -> String.valueOf(p.quantity()));

        TableColumn<ProductDTO, Void> qtyColumn = new TableColumn<>("Qty");
        qtyColumn.setCellFactory(spinnerCellFactory());
        qtyColumn.setPrefWidth(90);
        qtyColumn.setSortable(false);

        TableColumn<ProductDTO, Void> actionColumn = new TableColumn<>("");
        actionColumn.setCellFactory(addButtonCellFactory());
        actionColumn.setPrefWidth(80);
        actionColumn.setSortable(false);

        catalogTable.getColumns().setAll(
                nameColumn, categoryColumn, priceColumn, stockColumn, qtyColumn, actionColumn);
    }

    /**
     * Builds a cell factory rendering a per-row quantity spinner bounded by
     * the product's available stock.
     *
     * @return the cell factory
     */
    private Callback<TableColumn<ProductDTO, Void>, TableCell<ProductDTO, Void>> spinnerCellFactory() {
        return column -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                ProductDTO product = getTableRow().getItem();
                setGraphic(spinnerFor(product));
            }
        };
    }

    /**
     * Builds a cell factory rendering a per-row "Add" button.
     *
     * @return the cell factory
     */
    private Callback<TableColumn<ProductDTO, Void>, TableCell<ProductDTO, Void>> addButtonCellFactory() {
        return column -> new TableCell<>() {
            private final Button addButton = new Button("Add");

            {
                addButton.setOnAction(event -> {
                    ProductDTO product = getTableRow() == null ? null : getTableRow().getItem();
                    if (product != null) {
                        addToCart(product);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || getTableRow() == null || getTableRow().getItem() == null ? null : addButton);
            }
        };
    }

    /**
     * Returns (creating if needed) the quantity spinner for a product, bounded
     * by its current stock.
     *
     * @param product the product
     * @return the spinner
     */
    private Spinner<Integer> spinnerFor(ProductDTO product) {
        return quantitySpinners.computeIfAbsent(product.id(), id -> {
            Spinner<Integer> spinner = new Spinner<>(1, Math.max(1, product.quantity()), 1);
            spinner.setEditable(false);
            spinner.setPrefWidth(80);
            return spinner;
        });
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
     * Adds a product to the cart at the quantity set on its row spinner.
     *
     * @param product the product to add
     */
    private void addToCart(ProductDTO product) {
        int quantity = spinnerFor(product).getValue();
        cart.merge(product.id(), quantity, Integer::sum);
        rebuildCartLines();
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
        for (ProductDTO product : allProducts) {
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
     * Updates the running subtotal label.
     */
    private void updateSummary() {
        BigDecimal subtotal = cartLines.stream()
                .map(CartLine::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summaryLabel.setText("Items: " + cartLines.size() + "   Subtotal: " + Fx.money(subtotal));
    }

    /**
     * Previews the order total, then on confirmation places it with exact
     * payment (no cash — this is a self-checkout, so change is always zero).
     */
    private void reviewAndPay() {
        if (cart.isEmpty()) {
            showError("Your cart is empty");
            return;
        }
        int points = parsePoints();
        if (points < 0) {
            showError("Enter a valid points amount");
            return;
        }

        Either<DomainError, CheckoutPreviewDTO> previewResult = mediator.send(
                new PreviewCheckoutQuery(customer.id(), new LinkedHashMap<>(cart), points));
        if (previewResult.isLeft()) {
            showError(Fx.errorMessage(previewResult, "Could not calculate total"));
            return;
        }

        CheckoutPreviewDTO preview = previewResult.get();
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm order");
        confirm.setHeaderText("Order total: " + Fx.money(preview.total()));
        confirm.setContentText("Subtotal: " + Fx.money(preview.subtotal())
                + "\n" + preview.taxName() + ": " + Fx.money(preview.tax())
                + (preview.discount().compareTo(BigDecimal.ZERO) > 0
                        ? "\nDiscount (" + String.join(", ", preview.appliedDiscounts()) + "): -"
                                + Fx.money(preview.discount())
                        : "")
                + "\nPoints you'll earn: " + preview.pointsToEarn()
                + "\n\nConfirm purchase?");
        confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        confirm.showAndWait().ifPresent(button -> {
            if (button == ButtonType.YES) {
                placeOrder(preview.total(), points);
            }
        });
    }

    /**
     * Submits the checkout for exactly the previewed total.
     *
     * @param total the previewed order total
     * @param points the points to redeem
     */
    private void placeOrder(BigDecimal total, int points) {
        Either<DomainError, TransactionDTO> result = mediator.send(new ProcessCheckoutCommand(
                customer.id(), new LinkedHashMap<>(cart), total, points));
        if (result.isRight()) {
            TransactionDTO transaction = result.get();
            statusLabel.getStyleClass().setAll("status-label");
            statusLabel.setText("Order placed! Receipt " + transaction.id()
                    + " — total " + Fx.money(transaction.total()));
            clearCart();
            pointsField.setText("0");
            refresh();
        } else {
            showError(Fx.errorMessage(result, "Checkout failed"));
        }
    }

    /**
     * Parses the points-to-redeem field, defaulting to 0 when blank.
     *
     * @return the parsed points, or -1 if invalid
     */
    private int parsePoints() {
        String text = pointsField.getText();
        if (text == null || text.isBlank()) {
            return 0;
        }
        try {
            int value = Integer.parseInt(text.trim());
            return value < 0 ? -1 : value;
        } catch (NumberFormatException exception) {
            return -1;
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
     * Reloads the product catalog and category filter options.
     */
    private void refresh() {
        Either<DomainError, List<ProductDTO>> result = mediator.send(new GetProductsQuery(null, null));
        if (result.isRight()) {
            quantitySpinners.clear();
            allProducts.setAll(result.get().stream().filter(p -> !p.expired() && p.quantity() > 0).toList());

            String previousFilter = categoryFilter.getValue();
            java.util.Set<String> categories = new TreeSet<>();
            allProducts.forEach(p -> categories.add(p.category()));
            categoryFilter.getItems().setAll(ALL_CATEGORIES);
            categoryFilter.getItems().addAll(categories);
            categoryFilter.setValue(
                    previousFilter != null && categoryFilter.getItems().contains(previousFilter)
                            ? previousFilter : ALL_CATEGORIES);

            applyCategoryFilter();
        }
    }

    /**
     * Filters the visible catalog by the selected category.
     */
    private void applyCategoryFilter() {
        String selected = categoryFilter.getValue();
        if (selected == null || ALL_CATEGORIES.equals(selected)) {
            catalog.setAll(allProducts);
        } else {
            catalog.setAll(allProducts.stream().filter(p -> selected.equals(p.category())).toList());
        }
    }

    /**
     * Presentation line for the cart table.
     */
    private static final class CartLine {
        private final String productId;
        private final String name;
        private final BigDecimal price;
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

        BigDecimal price() {
            return price;
        }

        int quantity() {
            return quantity;
        }

        BigDecimal subtotal() {
            return price.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
