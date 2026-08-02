package com.konbini.view.portal;

import com.google.inject.Injector;
import com.konbini.application.dto.CustomerDTO;
import com.konbini.application.session.CustomerSessionContext;
import com.konbini.view.login.LoginView;
import io.vavr.control.Option;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Customer-facing application shell. The content area swaps between the
 * shop (self-checkout) and the customer's own account (profile, membership
 * card and purchase history).
 */
public class CustomerPortalView {

    private final Injector injector;
    private final CustomerSessionContext sessionContext;
    private final CustomerDTO customer;
    private final StackPane contentPane;
    private final BorderPane root;

    /**
     * Constructs the customer portal and wires the navigation.
     *
     * @param injector the Guice injector
     * @param stage the primary stage
     */
    public CustomerPortalView(Injector injector, Stage stage) {
        this.injector = injector;
        this.sessionContext = injector.getInstance(CustomerSessionContext.class);

        Option<CustomerDTO> current = sessionContext.getCurrentCustomer();
        this.customer = current.getOrElseThrow(
                () -> new IllegalStateException("No authenticated customer session"));

        Label brand = new Label("Konbini");
        brand.getStyleClass().add("title-label");

        Label welcome = new Label("Welcome, " + customer.name());
        welcome.getStyleClass().add("subtitle-label");
        welcome.setWrapText(true);

        VBox nav = new VBox(8,
                brand,
                welcome,
                navButton("Shop", () -> show(new CustomerShopView(injector, customer).getRoot())),
                navButton("My Account", () -> show(new CustomerAccountView(injector, customer).getRoot())));

        VBox spacer = new VBox(nav);
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button signOutButton = new Button("Sign Out");
        signOutButton.getStyleClass().add("secondary-button");
        signOutButton.setMaxWidth(Double.MAX_VALUE);
        signOutButton.setOnAction(event -> signOut(stage));

        VBox sidebar = new VBox(spacer, signOutButton);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.getStyleClass().add("nav-pane");
        sidebar.setPrefWidth(220);

        contentPane = new StackPane();
        contentPane.getChildren().add(new CustomerShopView(injector, customer).getRoot());
        contentPane.setPadding(new Insets(20));

        root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(contentPane);
    }

    /**
     * Returns the portal root node.
     *
     * @return the root node
     */
    public javafx.scene.Parent getRoot() {
        return root;
    }

    /**
     * Builds a navigation button.
     *
     * @param label the button label
     * @param action the action to run on selection
     * @return the styled button
     */
    private Button navButton(String label, Runnable action) {
        Button button = new Button(label);
        button.setMaxWidth(Double.MAX_VALUE);
        button.getStyleClass().add("nav-button");
        button.setOnAction(event -> action.run());
        return button;
    }

    /**
     * Swaps the content pane to the given node.
     *
     * @param node the new content
     */
    private void show(Node node) {
        contentPane.getChildren().clear();
        contentPane.getChildren().add(node);
    }

    /**
     * Ends the session and returns to the login screen.
     *
     * @param stage the primary stage
     */
    private void signOut(Stage stage) {
        sessionContext.logout();
        LoginView login = new LoginView(injector, stage);
        stage.getScene().setRoot(login.getRoot());
    }
}
