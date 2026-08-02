package com.konbini.view.portal;

import com.google.inject.Injector;
import com.konbini.application.dto.CustomerDTO;
import com.konbini.application.dto.TransactionDTO;
import com.konbini.application.mediator.Mediator;
import com.konbini.application.query.GetTransactionsQuery;
import com.konbini.domain.common.DomainError;
import com.konbini.view.Fx;
import io.vavr.control.Either;
import java.util.Comparator;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Read-only view of the signed-in customer's own profile, membership card
 * and purchase history.
 */
public class CustomerAccountView {

    private final Mediator mediator;
    private final CustomerDTO customer;
    private final ObservableList<TransactionDTO> transactions = FXCollections.observableArrayList();
    private final TableView<TransactionDTO> table = new TableView<>(transactions);

    /**
     * Constructs the account view for the given customer.
     *
     * @param injector the Guice injector
     * @param customer the signed-in customer
     */
    public CustomerAccountView(Injector injector, CustomerDTO customer) {
        this.mediator = injector.getInstance(Mediator.class);
        this.customer = customer;
        loadTransactions(customer.id());
    }

    /**
     * Returns the account view root node.
     *
     * @return the root node
     */
    public Node getRoot() {
        Label title = new Label("My Account");
        title.getStyleClass().add("title-label");

        Label subtitle = new Label("Your profile, membership and purchase history");
        subtitle.getStyleClass().add("subtitle-label");

        VBox content = new VBox(16, title, subtitle, summaryCard(), historyCard());
        VBox.setVgrow(content, Priority.ALWAYS);
        return content;
    }

    /**
     * Builds a single summary card combining profile details and membership
     * stats side by side.
     *
     * @return the card node
     */
    private HBox summaryCard() {
        Label profileTitle = new Label("Profile");
        profileTitle.getStyleClass().add("stat-label");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.addRow(0, formLabel("Customer ID"), new Label(customer.id()));
        grid.addRow(1, formLabel("Name"), new Label(customer.name()));
        grid.addRow(2, formLabel("Senior citizen"), new Label(customer.seniorCitizen() ? "Yes" : "No"));

        VBox profileColumn = new VBox(12, profileTitle, grid);
        profileColumn.setPrefWidth(260);
        profileColumn.setMinWidth(220);

        Label membershipTitle = new Label("Membership card");
        membershipTitle.getStyleClass().add("stat-label");

        VBox membershipColumn = new VBox(12, membershipTitle);
        HBox.setHgrow(membershipColumn, Priority.ALWAYS);

        if (!customer.hasMembershipCard()) {
            Label none = new Label("You don't have a membership card yet. Ask staff to add one.");
            none.getStyleClass().add("subtitle-label");
            none.setWrapText(true);
            membershipColumn.getChildren().add(none);
        } else {
            HBox stats = new HBox(28, stat(String.valueOf(customer.points()), "Points balance"),
                    stat(customer.cardNumber(), "Card number"),
                    stat(String.valueOf(customer.cardExpiryDate()), "Expires"));
            membershipColumn.getChildren().add(stats);
        }

        Region divider = new Region();
        divider.getStyleClass().add("vertical-divider");
        divider.setPrefWidth(1);
        divider.setMaxHeight(Double.MAX_VALUE);

        HBox card = new HBox(20, profileColumn, divider, membershipColumn);
        card.setAlignment(Pos.TOP_LEFT);
        card.getStyleClass().add("card");
        return card;
    }

    /**
     * Builds a single labeled stat block.
     *
     * @param value the stat value
     * @param label the stat label
     * @return the stat node
     */
    private VBox stat(String value, String label) {
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");
        Label textLabel = new Label(label);
        textLabel.getStyleClass().add("stat-label");
        return new VBox(4, valueLabel, textLabel);
    }

    /**
     * Builds a form field label.
     *
     * @param text the label text
     * @return the label node
     */
    private Label formLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    /**
     * Builds the purchase history table card.
     *
     * @return the card node
     */
    private VBox historyCard() {
        Label title = new Label("Purchase history");
        title.getStyleClass().add("stat-label");

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.getColumns().setAll(
                Fx.stringColumn("Date", dto -> String.valueOf(dto.timestamp())),
                Fx.stringColumn("Items", dto -> String.valueOf(dto.items().size())),
                Fx.stringColumn("Total", dto -> Fx.money(dto.total())),
                Fx.stringColumn("Points earned", dto -> String.valueOf(dto.pointsEarned())));
        VBox.setVgrow(table, Priority.ALWAYS);

        VBox card = new VBox(12, title, table);
        card.getStyleClass().add("card");
        VBox.setVgrow(card, Priority.ALWAYS);
        return card;
    }

    /**
     * Loads and filters transactions belonging to this customer.
     *
     * @param customerId the customer ID to filter by
     */
    private void loadTransactions(String customerId) {
        Either<DomainError, List<TransactionDTO>> result =
                mediator.send(new GetTransactionsQuery());
        if (result.isRight()) {
            List<TransactionDTO> own = result.get().stream()
                    .filter(dto -> customerId.equals(dto.customerId()))
                    .sorted(Comparator.comparing(TransactionDTO::timestamp).reversed())
                    .toList();
            transactions.setAll(own);
        }
    }
}
