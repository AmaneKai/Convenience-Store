package com.konbini.view.login;

import com.google.inject.Injector;
import com.konbini.application.command.AuthenticateCustomerCommand;
import com.konbini.application.command.AuthenticateEmployeeCommand;
import com.konbini.application.dto.CustomerDTO;
import com.konbini.application.dto.EmployeeDTO;
import com.konbini.application.mediator.Mediator;
import com.konbini.domain.common.DomainError;
import com.konbini.view.portal.CustomerPortalView;
import com.konbini.view.shell.MainShellView;
import io.vavr.control.Either;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Single sign-in screen for everyone — staff and customers share one ID and
 * password form. On submit, staff credentials are tried first and customer
 * credentials second; whichever matches determines where the user lands, so
 * nobody has to pick a role up front.
 */
public class LoginView {

    private final Mediator mediator;
    private final Injector injector;
    private final Stage stage;
    private final TextField idField;
    private final PasswordField passwordField;
    private final Label statusLabel;
    private final StackPane root;

    /**
     * Constructs the login screen.
     *
     * @param injector the Guice injector
     * @param stage the primary stage
     */
    public LoginView(Injector injector, Stage stage) {
        this.injector = injector;
        this.mediator = injector.getInstance(Mediator.class);
        this.stage = stage;

        idField = new TextField();
        idField.setPromptText("Employee or Customer ID");
        idField.setMaxWidth(320);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(320);

        Button signInButton = new Button("Sign In");
        signInButton.setMaxWidth(320);
        signInButton.setOnAction(event -> authenticate());

        Button signUpButton = new Button("New here? Create a customer account");
        signUpButton.getStyleClass().add("link-button");
        signUpButton.setOnAction(event ->
                stage.getScene().setRoot(new CustomerSignUpView(injector, stage).getRoot()));

        statusLabel = new Label(" ");
        statusLabel.setWrapText(true);

        Label title = new Label("Konbini Store");
        title.getStyleClass().add("title-label");

        Label subtitle = new Label("Sign in to continue");
        subtitle.getStyleClass().add("subtitle-label");

        VBox card = new VBox(14, title, subtitle, idField, passwordField,
                signInButton, statusLabel, signUpButton);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(360);
        card.getStyleClass().add("card");

        VBox centered = new VBox(card);
        centered.setAlignment(Pos.CENTER);

        root = new StackPane(centered);

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                authenticate();
            }
        });
    }

    /**
     * Returns the login root node.
     *
     * @return the root node
     */
    public javafx.scene.Parent getRoot() {
        return root;
    }

    /**
     * Releases resources used by the view.
     */
    public void shutdown() {
        // no background resources to release
    }

    /**
     * Validates input, then tries staff authentication before falling back to
     * customer authentication, navigating to the matching shell on success.
     */
    private void authenticate() {
        String id = idField.getText() == null ? "" : idField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (id.isEmpty() || password.isEmpty()) {
            statusLabel.getStyleClass().setAll("error-label");
            statusLabel.setText("Enter your ID and password");
            return;
        }

        Either<DomainError, EmployeeDTO> employeeResult =
                mediator.send(new AuthenticateEmployeeCommand(id, password));
        if (employeeResult.isRight()) {
            stage.getScene().setRoot(new MainShellView(injector, stage).getRoot());
            return;
        }

        Either<DomainError, CustomerDTO> customerResult =
                mediator.send(new AuthenticateCustomerCommand(id, password));
        if (customerResult.isRight()) {
            stage.getScene().setRoot(new CustomerPortalView(injector, stage).getRoot());
            return;
        }

        statusLabel.getStyleClass().setAll("error-label");
        statusLabel.setText("Invalid ID or password");
    }
}
