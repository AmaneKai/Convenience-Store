package com.konbini.view.shell;

import com.google.inject.Injector;
import com.konbini.application.session.SessionContext;
import com.konbini.application.dto.EmployeeDTO;
import com.konbini.view.dashboard.DashboardView;
import com.konbini.view.products.ProductsView;
import com.konbini.view.customers.CustomersView;
import com.konbini.view.checkout.CheckoutView;
import com.konbini.view.transactions.TransactionsView;
import com.konbini.view.employees.EmployeesView;
import io.vavr.control.Option;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Application shell with a glassmorphism navigation sidebar. The content area
 * swaps between the dashboard, products, customers, checkout, transactions and
 * employees views.
 */
public class MainShellView {

    private final Injector injector;
    private final SessionContext sessionContext;
    private final StackPane contentPane;
    private final BorderPane root;

    /**
     * Constructs the main shell and wires the navigation.
     *
     * @param injector the Guice injector
     * @param stage the primary stage
     */
    public MainShellView(Injector injector, Stage stage) {
        this.injector = injector;
        this.sessionContext = injector.getInstance(SessionContext.class);

        Label brand = new Label("Konbini");
        brand.getStyleClass().add("title-label");

        Label employeeLabel = new Label(employeeName());
        employeeLabel.getStyleClass().add("subtitle-label");
        employeeLabel.setWrapText(true);

        VBox nav = new VBox(8,
                brand,
                employeeLabel,
                navButton("Dashboard", () -> show(new DashboardView(injector).getRoot())),
                navButton("Products", () -> show(new ProductsView(injector).getRoot())),
                navButton("Customers", () -> show(new CustomersView(injector).getRoot())),
                navButton("Checkout", () -> show(new CheckoutView(injector).getRoot())),
                navButton("Transactions", () -> show(new TransactionsView(injector).getRoot())),
                navButton("Employees", () -> show(new EmployeesView(injector).getRoot())));

        VBox spacer = new VBox(nav);
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutButton = new Button("Sign Out");
        logoutButton.getStyleClass().add("secondary-button");
        logoutButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.setOnAction(event -> logout(stage));

        VBox sidebar = new VBox(spacer, logoutButton);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.getStyleClass().add("nav-pane");
        sidebar.setPrefWidth(220);

        contentPane = new StackPane();
        contentPane.getChildren().add(new DashboardView(injector).getRoot());
        contentPane.setPadding(new Insets(20));

        root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(contentPane);
    }

    /**
     * Returns the shell root node.
     *
     * @return the root node
     */
    public javafx.scene.Parent getRoot() {
        return root;
    }

    /**
     * Returns the greeting for the current session.
     *
     * @return the display name of the logged-in employee
     */
    private String employeeName() {
        Option<EmployeeDTO> current = sessionContext.getCurrentEmployee();
        return current.map(EmployeeDTO::name).getOrElse("Employee");
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
    private void logout(Stage stage) {
        sessionContext.logout();
        com.konbini.view.login.LoginView login =
                new com.konbini.view.login.LoginView(injector, stage);
        stage.getScene().setRoot(login.getRoot());
    }
}
