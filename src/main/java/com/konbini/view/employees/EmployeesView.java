package com.konbini.view.employees;

import com.google.inject.Injector;
import com.konbini.application.command.AddEmployeeCommand;
import com.konbini.application.command.RemoveEmployeeCommand;
import com.konbini.application.command.UpdateEmployeeCommand;
import com.konbini.application.dto.EmployeeDTO;
import com.konbini.application.mediator.Mediator;
import com.konbini.application.query.GetEmployeesQuery;
import com.konbini.domain.common.DomainError;
import com.konbini.view.Fx;
import io.vavr.control.Either;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Employees view for managing staff accounts.
 */
public class EmployeesView {

    private final Mediator mediator;
    private final ObservableList<EmployeeDTO> employees = FXCollections.observableArrayList();
    private final TableView<EmployeeDTO> table = new TableView<>(employees);

    private final TextField nameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Label statusLabel = new Label(" ");
    private EmployeeDTO selected;

    /**
     * Constructs the employees view.
     *
     * @param injector the Guice injector
     */
    public EmployeesView(Injector injector) {
        this.mediator = injector.getInstance(Mediator.class);
        refresh();
    }

    /**
     * Returns the employees view root node.
     *
     * @return the root node
     */
    public Node getRoot() {
        Label title = new Label("Employees");
        title.getStyleClass().add("title-label");

        Label subtitle = new Label("Staff accounts");
        subtitle.getStyleClass().add("subtitle-label");

        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(event -> refresh());

        Button addButton = new Button("Add Employee");
        addButton.setOnAction(event -> add());

        Button updateButton = new Button("Rename");
        updateButton.getStyleClass().add("secondary-button");
        updateButton.setOnAction(event -> update());

        Button removeButton = new Button("Remove");
        removeButton.getStyleClass().add("danger-button");
        removeButton.setOnAction(event -> remove());

        HBox actions = new HBox(10, refreshButton, addButton, updateButton, removeButton);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.getColumns().setAll(
                Fx.stringColumn("ID", EmployeeDTO::id),
                Fx.stringColumn("Name", EmployeeDTO::name));
        table.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, value) -> this.selected = value);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        nameField.setPromptText("Full name");
        passwordField.setPromptText("Password");
        grid.addRow(0, new Label("Name"), nameField);
        grid.addRow(1, new Label("Password"), passwordField);
        for (Node node : grid.getChildren()) {
            if (node instanceof javafx.scene.control.Control control) {
                control.setMaxWidth(Double.MAX_VALUE);
            }
        }
        grid.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(90));
        grid.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(180));

        Label formTitle = new Label("New staff account");
        formTitle.getStyleClass().add("stat-label");

        VBox form = new VBox(12, formTitle, grid, addButton);
        form.setPrefWidth(300);

        HBox lower = new HBox(20, table, form);
        HBox.setHgrow(table, Priority.ALWAYS);

        statusLabel.getStyleClass().add("status-label");

        VBox root = new VBox(16, title, subtitle, actions, lower, statusLabel);
        root.getStyleClass().add("card");
        VBox.setVgrow(lower, Priority.ALWAYS);
        return root;
    }

    /**
     * Adds a new employee.
     */
    private void add() {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();
        Either<DomainError, ?> result = mediator.send(new AddEmployeeCommand(name, password));
        if (result.isRight()) {
            statusLabel.getStyleClass().setAll("status-label");
            statusLabel.setText("Employee added");
            nameField.clear();
            passwordField.clear();
            refresh();
        } else {
            showError(Fx.errorMessage(result, "Failed to add employee"));
        }
    }

    /**
     * Renames the selected employee.
     */
    private void update() {
        if (selected == null) {
            showError("Select an employee to rename");
            return;
        }
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        Either<DomainError, ?> result =
                mediator.send(new UpdateEmployeeCommand(selected.id(), name));
        if (result.isRight()) {
            statusLabel.getStyleClass().setAll("status-label");
            statusLabel.setText("Employee renamed");
            nameField.clear();
            refresh();
        } else {
            showError(Fx.errorMessage(result, "Rename failed"));
        }
    }

    /**
     * Removes the selected employee.
     */
    private void remove() {
        if (selected == null) {
            showError("Select an employee to remove");
            return;
        }
        Either<DomainError, ?> result =
                mediator.send(new RemoveEmployeeCommand(selected.id()));
        if (result.isRight()) {
            statusLabel.getStyleClass().setAll("status-label");
            statusLabel.setText("Employee removed");
            refresh();
        } else {
            showError(Fx.errorMessage(result, "Remove failed"));
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
     * Reloads the employee list.
     */
    private void refresh() {
        Either<DomainError, List<EmployeeDTO>> result = mediator.send(new GetEmployeesQuery());
        if (result.isRight()) {
            employees.setAll(result.get());
        }
    }
}
