package com.konbini.view.login;

import com.google.inject.Injector;
import com.konbini.application.command.CustomerSignUpCommand;
import com.konbini.application.dto.CustomerDTO;
import com.konbini.application.mediator.Mediator;
import com.konbini.domain.common.DomainError;
import com.konbini.view.Fx;
import io.vavr.control.Either;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Customer self-registration card. Creates a new loyalty account with a
 * login password via the mediator, then returns to sign-in.
 */
public class CustomerSignUpView {

    private final Mediator mediator;
    private final Injector injector;
    private final Stage stage;
    private final TextField nameField;
    private final CheckBox seniorBox;
    private final PasswordField passwordField;
    private final PasswordField confirmField;
    private final Label statusLabel;
    private final StackPane root;

    /**
     * Constructs the customer sign-up screen.
     *
     * @param injector the Guice injector
     * @param stage the primary stage
     */
    public CustomerSignUpView(Injector injector, Stage stage) {
        this.injector = injector;
        this.mediator = injector.getInstance(Mediator.class);
        this.stage = stage;

        nameField = new TextField();
        nameField.setPromptText("Full name");
        nameField.setMaxWidth(320);

        seniorBox = new CheckBox("Senior citizen");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password (min 6 characters)");
        passwordField.setMaxWidth(320);

        confirmField = new PasswordField();
        confirmField.setPromptText("Confirm password");
        confirmField.setMaxWidth(320);

        Button createButton = new Button("Create Account");
        createButton.setMaxWidth(320);
        createButton.setOnAction(event -> signUp());

        Button backButton = new Button("Already have an account? Sign in");
        backButton.getStyleClass().add("link-button");
        backButton.setOnAction(event ->
                stage.getScene().setRoot(new LoginView(injector, stage).getRoot()));

        statusLabel = new Label(" ");
        statusLabel.setWrapText(true);

        Label title = new Label("Konbini Store");
        title.getStyleClass().add("title-label");

        Label subtitle = new Label("Create your customer account");
        subtitle.getStyleClass().add("subtitle-label");

        VBox card = new VBox(14, title, subtitle, nameField, seniorBox, passwordField,
                confirmField, createButton, statusLabel, backButton);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(360);
        card.getStyleClass().add("card");

        VBox centered = new VBox(card);
        centered.setAlignment(Pos.CENTER);

        root = new StackPane(centered);
    }

    /**
     * Returns the sign-up root node.
     *
     * @return the root node
     */
    public javafx.scene.Parent getRoot() {
        return root;
    }

    /**
     * Validates input, dispatches the sign-up command and shows the created
     * customer ID on success.
     */
    private void signUp() {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();
        String confirm = confirmField.getText() == null ? "" : confirmField.getText();

        if (!password.equals(confirm)) {
            statusLabel.getStyleClass().setAll("error-label");
            statusLabel.setText("Passwords do not match");
            return;
        }

        Either<DomainError, CustomerDTO> result = mediator.send(
                new CustomerSignUpCommand(name, seniorBox.isSelected(), password));
        if (result.isRight()) {
            CustomerDTO customer = result.get();
            Fx.showInfo(stage, "Account created",
                    "Your customer ID is " + customer.id() + ". Use it with your password to sign in.");
            stage.getScene().setRoot(new LoginView(injector, stage).getRoot());
        } else {
            statusLabel.getStyleClass().setAll("error-label");
            statusLabel.setText(Fx.errorMessage(result, "Could not create account"));
        }
    }
}
