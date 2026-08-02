package com.konbini.view.customers;

import com.google.inject.Injector;
import com.konbini.application.command.RegisterCustomerCommand;
import com.konbini.application.command.SetCustomerPasswordCommand;
import com.konbini.application.dto.CustomerDTO;
import com.konbini.application.mediator.Mediator;
import com.konbini.application.query.GetCustomersQuery;
import com.konbini.domain.common.DomainError;
import com.konbini.view.Fx;
import io.vavr.control.Either;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Customers view for browsing and registering loyalty program members.
 */
public class CustomersView {

    private final Mediator mediator;
    private final ObservableList<CustomerDTO> customers = FXCollections.observableArrayList();
    private final TableView<CustomerDTO> table = new TableView<>(customers);

    private final TextField nameField = new TextField();
    private final CheckBox seniorBox = new CheckBox("Senior citizen");
    private final TextField cardNumberField = new TextField();
    private final TextField expiryField = new TextField();
    private final Label statusLabel = new Label(" ");

    /**
     * Constructs the customers view.
     *
     * @param injector the Guice injector
     */
    public CustomersView(Injector injector) {
        this.mediator = injector.getInstance(Mediator.class);
        refresh();
    }

    /**
     * Returns the customers view root node.
     *
     * @return the root node
     */
    public Node getRoot() {
        Label title = new Label("Customers");
        title.getStyleClass().add("title-label");

        Label subtitle = new Label("Loyalty members");
        subtitle.getStyleClass().add("subtitle-label");

        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(event -> refresh());

        Button registerButton = new Button("Register Customer");
        registerButton.setOnAction(event -> register());

        Button setPasswordButton = new Button("Set Login Password");
        setPasswordButton.getStyleClass().add("secondary-button");
        setPasswordButton.setOnAction(event -> setPassword());

        HBox actions = new HBox(10, refreshButton, registerButton, setPasswordButton);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        configureColumns();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        nameField.setPromptText("Full name");
        cardNumberField.setPromptText("Card number");
        expiryField.setPromptText("Card expiry YYYY-MM-DD");
        grid.addRow(0, new Label("Name"), nameField);
        grid.addRow(1, new Label("Card No."), cardNumberField);
        grid.addRow(2, new Label("Expiry"), expiryField);
        grid.addRow(3, new Label(""), seniorBox);
        for (Node node : grid.getChildren()) {
            if (node instanceof javafx.scene.control.Control control) {
                control.setMaxWidth(Double.MAX_VALUE);
            }
        }
        javafx.scene.layout.ColumnConstraints labelColumn = new javafx.scene.layout.ColumnConstraints(90);
        javafx.scene.layout.ColumnConstraints fieldColumn = new javafx.scene.layout.ColumnConstraints();
        fieldColumn.setHgrow(Priority.ALWAYS);
        fieldColumn.setMinWidth(220);
        grid.getColumnConstraints().addAll(labelColumn, fieldColumn);

        Label formTitle = new Label("New membership");
        formTitle.getStyleClass().add("stat-label");

        Button clearButton = new Button("Clear");
        clearButton.getStyleClass().add("secondary-button");
        clearButton.setOnAction(event -> clearForm());

        VBox form = new VBox(12, formTitle, grid, registerButton, clearButton);
        form.setPrefWidth(360);

        HBox lower = new HBox(20, table, form);
        HBox.setHgrow(table, Priority.ALWAYS);

        statusLabel.getStyleClass().add("status-label");

        VBox root = new VBox(16, title, subtitle, actions, lower, statusLabel);
        root.getStyleClass().add("card");
        VBox.setVgrow(lower, Priority.ALWAYS);
        return root;
    }

    /**
     * Configures the customer table columns.
     */
    private void configureColumns() {
        table.getColumns().setAll(
                Fx.stringColumn("ID", CustomerDTO::id),
                Fx.stringColumn("Name", CustomerDTO::name),
                Fx.stringColumn("Senior", dto -> dto.seniorCitizen() ? "Yes" : "No"),
                Fx.stringColumn("Card", dto -> dto.hasMembershipCard() ? dto.cardNumber() : "–"),
                Fx.stringColumn("Points", dto -> String.valueOf(dto.points())),
                Fx.stringColumn("Card Expiry", dto -> dto.cardExpiryDate() == null
                        ? "–" : dto.cardExpiryDate().toString()),
                Fx.stringColumn("Has Login", dto -> dto.hasPassword() ? "Yes" : "No"));
    }

    /**
     * Registers a new customer.
     */
    private void register() {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String cardNumber = cardNumberField.getText() == null
                ? "" : cardNumberField.getText().trim();
        LocalDate expiry = null;
        if (!expiryField.getText().isBlank()) {
            try {
                expiry = LocalDate.parse(expiryField.getText().trim());
            } catch (java.time.format.DateTimeParseException exception) {
                showError("Card expiry must be YYYY-MM-DD");
                return;
            }
        }
        RegisterCustomerCommand command = cardNumber.isBlank()
                ? new RegisterCustomerCommand(name, seniorBox.isSelected())
                : new RegisterCustomerCommand(name, seniorBox.isSelected(),
                        cardNumber, expiry);
        Either<DomainError, ?> result = mediator.send(command);
        if (result.isRight()) {
            statusLabel.getStyleClass().setAll("status-label");
            statusLabel.setText("Customer registered");
            clearForm();
            refresh();
        } else {
            showError(Fx.errorMessage(result, "Registration failed"));
        }
    }

    /**
     * Prompts for a new password and enables self-service login for the
     * selected customer.
     */
    private void setPassword() {
        CustomerDTO selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a customer first");
            return;
        }

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("New password (min 6 characters)");
        passwordField.setMaxWidth(240);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Set Login Password");
        dialog.setHeaderText("New password for " + selected.name() + " (" + selected.id() + ")");
        dialog.getDialogPane().setContent(passwordField);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(button -> {
            if (button != ButtonType.OK) {
                return;
            }
            Either<DomainError, ?> result = mediator.send(
                    new SetCustomerPasswordCommand(selected.id(), passwordField.getText()));
            if (result.isRight()) {
                statusLabel.getStyleClass().setAll("status-label");
                statusLabel.setText("Login password set for " + selected.name());
                refresh();
            } else {
                showError(Fx.errorMessage(result, "Failed to set password"));
            }
        });
    }

    /**
     * Clears the registration form.
     */
    private void clearForm() {
        nameField.clear();
        seniorBox.setSelected(false);
        cardNumberField.clear();
        expiryField.clear();
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
     * Reloads the customer list.
     */
    private void refresh() {
        Either<DomainError, java.util.List<CustomerDTO>> result =
                mediator.send(new GetCustomersQuery());
        if (result.isRight()) {
            customers.setAll(result.get());
        }
    }
}
