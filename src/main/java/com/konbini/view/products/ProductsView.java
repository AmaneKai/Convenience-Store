package com.konbini.view.products;

import com.google.inject.Injector;
import com.konbini.application.command.AddProductCommand;
import com.konbini.application.command.RemoveProductCommand;
import com.konbini.application.command.RestockProductCommand;
import com.konbini.application.command.UpdateProductCommand;
import com.konbini.application.dto.ProductDTO;
import com.konbini.application.mediator.Mediator;
import com.konbini.application.query.GetProductsQuery;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.product.ProductCategory;
import com.konbini.domain.product.ProductSubcategory;
import com.konbini.view.Fx;
import io.vavr.control.Either;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Products view for browsing, adding, updating, restocking and removing
 * inventory items.
 */
public class ProductsView {

    private final Mediator mediator;
    private final ObservableList<ProductDTO> products = FXCollections.observableArrayList();
    private final TableView<ProductDTO> table = new TableView<>(products);

    private final TextField nameField = new TextField();
    private final TextField priceField = new TextField();
    private final TextField quantityField = new TextField();
    private final ComboBox<ProductCategory> categoryBox = new ComboBox<>();
    private final ComboBox<ProductSubcategory> subcategoryBox = new ComboBox<>();
    private final TextField brandField = new TextField();
    private final TextField variantField = new TextField();
    private final TextField expirationField = new TextField();
    private final TextField searchField = new TextField();
    private final Label statusLabel = new Label(" ");

    private ProductDTO selected;

    /**
     * Constructs the products view.
     *
     * @param injector the Guice injector
     */
    public ProductsView(Injector injector) {
        this.mediator = injector.getInstance(Mediator.class);
        categoryBox.getItems().setAll(ProductCategory.values());
        categoryBox.valueProperty().addListener((obs, old, value) ->
                populateSubcategories());
        subcategoryBox.setDisable(true);
        refresh();
    }

    /**
     * Returns the products view root node.
     *
     * @return the root node
     */
    public Node getRoot() {
        return build();
    }

    /**
     * Builds the layout.
     *
     * @return the root VBox
     */
    private Node build() {
        Label title = new Label("Products");
        title.getStyleClass().add("title-label");

        Label subtitle = new Label("Inventory management");
        subtitle.getStyleClass().add("subtitle-label");

        Label searchLabel = new Label("Search");
        searchField.setPromptText("Name, brand or variant");
        HBox searchBox = new HBox(8, searchLabel, searchField);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.textProperty().addListener((obs, old, value) -> refresh());

        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(event -> refresh());

        Button addButton = new Button("Add Product");
        addButton.setOnAction(event -> add());

        Button updateButton = new Button("Update");
        updateButton.getStyleClass().add("secondary-button");
        updateButton.setOnAction(event -> update());

        Button restockButton = new Button("Restock");
        restockButton.getStyleClass().add("secondary-button");
        restockButton.setOnAction(event -> restock());

        Button removeButton = new Button("Remove");
        removeButton.getStyleClass().add("danger-button");
        removeButton.setOnAction(event -> remove());

        HBox actions = new HBox(10, refreshButton, addButton, updateButton,
                restockButton, removeButton);

        table.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, value) -> select(value));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        configureColumns();

        VBox form = buildForm();

        HBox lower = new HBox(20, table, form);
        HBox.setHgrow(table, Priority.ALWAYS);
        form.setPrefWidth(300);

        statusLabel.getStyleClass().add("status-label");

        VBox root = new VBox(16, title, subtitle, searchBox, actions,
                lower, statusLabel);
        root.getStyleClass().add("card");
        VBox.setVgrow(lower, Priority.ALWAYS);
        return root;
    }

    /**
     * Configures the product table columns.
     */
    private void configureColumns() {
        TableColumn<ProductDTO, String> idCol = Fx.stringColumn("ID", ProductDTO::id);
        TableColumn<ProductDTO, String> nameCol = Fx.stringColumn("Name", ProductDTO::name);
        TableColumn<ProductDTO, String> brandCol = Fx.stringColumn("Brand", ProductDTO::brand);
        TableColumn<ProductDTO, String> variantCol = Fx.stringColumn("Variant", ProductDTO::variant);
        TableColumn<ProductDTO, String> priceCol = Fx.stringColumn("Price", p -> Fx.money(p.price()));
        TableColumn<ProductDTO, String> qtyCol = Fx.stringColumn("Qty", p -> String.valueOf(p.quantity()));
        TableColumn<ProductDTO, String> catCol = Fx.stringColumn("Category", ProductDTO::category);
        TableColumn<ProductDTO, String> expiryCol = Fx.stringColumn("Expiry", p ->
                p.expirationDate() == null ? "–" : p.expirationDate().toString());

        table.getColumns().setAll(idCol, nameCol, brandCol, variantCol, priceCol,
                qtyCol, catCol, expiryCol);
    }

    /**
     * Builds the product entry form.
     *
     * @return the form VBox
     */
    private VBox buildForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        priceField.setPromptText("Price");
        quantityField.setPromptText("Quantity");
        expirationField.setPromptText("YYYY-MM-DD");
        brandField.setPromptText("Brand");
        variantField.setPromptText("Variant");
        nameField.setPromptText("Product name");

        grid.addRow(0, new Label("Name"), nameField);
        grid.addRow(1, new Label("Price"), priceField);
        grid.addRow(2, new Label("Qty"), quantityField);
        grid.addRow(3, new Label("Category"), categoryBox);
        grid.addRow(4, new Label("Subcategory"), subcategoryBox);
        grid.addRow(5, new Label("Brand"), brandField);
        grid.addRow(6, new Label("Variant"), variantField);
        grid.addRow(7, new Label("Expiry"), expirationField);

        for (javafx.scene.Node node : grid.getChildren()) {
            if (node instanceof javafx.scene.control.Control control) {
                control.setMaxWidth(Double.MAX_VALUE);
            }
        }
        grid.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(90));
        grid.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(180));

        Label formTitle = new Label("Product details");
        formTitle.getStyleClass().add("stat-label");
        return new VBox(12, formTitle, grid);
    }

    /**
     * Populates the subcategory combo box for the selected category.
     */
    private void populateSubcategories() {
        ProductCategory category = categoryBox.getValue();
        if (category == null) {
            subcategoryBox.getItems().clear();
            subcategoryBox.setDisable(true);
            return;
        }
        subcategoryBox.getItems().setAll(ProductSubcategory.getSubcategoriesFor(category));
        subcategoryBox.setDisable(false);
    }

    /**
     * Stores the currently selected product.
     *
     * @param dto the selected product or null
     */
    private void select(ProductDTO dto) {
        this.selected = dto;
        if (dto == null) {
            clearForm();
            return;
        }
        nameField.setText(dto.name());
        priceField.setText(String.valueOf(dto.price()));
        quantityField.setText(String.valueOf(dto.quantity()));
        ProductCategory.fromDisplayName(dto.category()).ifPresent(categoryBox::setValue);
        brandField.setText(dto.brand());
        variantField.setText(dto.variant());
        expirationField.setText(dto.expirationDate() == null ? "" : dto.expirationDate().toString());
    }

    /**
     * Clears the form fields.
     */
    private void clearForm() {
        nameField.clear();
        priceField.clear();
        quantityField.clear();
        categoryBox.setValue(null);
        subcategoryBox.setValue(null);
        brandField.clear();
        variantField.clear();
        expirationField.clear();
    }

    /**
     * Parses the expiry date field.
     *
     * @return the parsed date or null when blank
     */
    private LocalDate parseExpiration() {
        String text = expirationField.getText();
        if (text == null || text.isBlank()) {
            return null;
        }
        return LocalDate.parse(text.trim());
    }

    /**
     * Adds a new product.
     */
    private void add() {
        try {
            Either<DomainError, ?> result = mediator.send(new AddProductCommand(
                    nameField.getText().trim(),
                    new java.math.BigDecimal(priceField.getText()),
                    Integer.parseInt(quantityField.getText()),
                    categoryDisplay(),
                    brandField.getText().trim(),
                    variantField.getText().trim(),
                    parseExpiration()));
            showResult(result, "Product added");
        } catch (NumberFormatException exception) {
            showError("Enter a valid price and quantity");
        }
    }

    /**
     * Updates the selected product.
     */
    private void update() {
        if (selected == null) {
            showError("Select a product to update");
            return;
        }
        try {
            Either<DomainError, ?> result = mediator.send(new UpdateProductCommand(
                    selected.id(),
                    nameField.getText().trim(),
                    new java.math.BigDecimal(priceField.getText()),
                    Integer.parseInt(quantityField.getText()),
                    categoryDisplay(),
                    brandField.getText().trim(),
                    variantField.getText().trim(),
                    parseExpiration()));
            showResult(result, "Product updated");
        } catch (NumberFormatException exception) {
            showError("Enter a valid price and quantity");
        }
    }

    /**
     * Restocks the selected product by the given amount.
     */
    private void restock() {
        if (selected == null) {
            showError("Select a product to restock");
            return;
        }
        try {
            int amount = Integer.parseInt(quantityField.getText());
            Either<DomainError, ?> result =
                    mediator.send(new RestockProductCommand(selected.id(), amount));
            showResult(result, "Stock increased");
        } catch (NumberFormatException exception) {
            showError("Enter a valid quantity to add");
        }
    }

    /**
     * Removes the selected product.
     */
    private void remove() {
        if (selected == null) {
            showError("Select a product to remove");
            return;
        }
        Either<DomainError, ?> result =
                mediator.send(new RemoveProductCommand(selected.id()));
        showResult(result, "Product removed");
    }

    /**
     * Returns the category display name, or a blank string when unset.
     *
     * @return the category display name
     */
    private String categoryDisplay() {
        return categoryBox.getValue() == null
                ? ""
                : categoryBox.getValue().getDisplayName();
    }

    /**
     * Reports the result of a command.
     *
     * @param result the command result
     * @param success the success message
     */
    private void showResult(Either<DomainError, ?> result, String success) {
        if (result.isRight()) {
            statusLabel.getStyleClass().setAll("status-label");
            statusLabel.setText(success);
            clearForm();
            refresh();
        } else {
            showError(Fx.errorMessage(result, "Operation failed"));
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
     * Reloads the product list.
     */
    private void refresh() {
        String search = searchField.getText() == null
                ? "" : searchField.getText().trim();
        Either<DomainError, java.util.List<ProductDTO>> result =
                mediator.send(new GetProductsQuery(search, null));
        if (result.isRight()) {
            products.setAll(result.get());
        }
    }
}
