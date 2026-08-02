package com.konbini.view.transactions;

import com.google.inject.Injector;
import com.konbini.application.dto.TransactionDTO;
import com.konbini.application.dto.TransactionItemDTO;
import com.konbini.application.mediator.Mediator;
import com.konbini.application.query.GetTransactionsQuery;
import com.konbini.domain.common.DomainError;
import com.konbini.view.Fx;
import io.vavr.control.Either;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Transactions view showing purchase history with an optional date filter and
 * line-item detail.
 */
public class TransactionsView {

    private final Mediator mediator;
    private final ObservableList<TransactionDTO> transactions = FXCollections.observableArrayList();
    private final TableView<TransactionDTO> table = new TableView<>(transactions);
    private final TextField startField = new TextField();
    private final TextField endField = new TextField();
    private final Label detailLabel = new Label();

    /**
     * Constructs the transactions view.
     *
     * @param injector the Guice injector
     */
    public TransactionsView(Injector injector) {
        this.mediator = injector.getInstance(Mediator.class);
        refresh();
    }

    /**
     * Returns the transactions view root node.
     *
     * @return the root node
     */
    public Node getRoot() {
        Label title = new Label("Transactions");
        title.getStyleClass().add("title-label");

        Label subtitle = new Label("Purchase history");
        subtitle.getStyleClass().add("subtitle-label");

        Label fromLabel = new Label("From");
        Label toLabel = new Label("To");
        startField.setPromptText("YYYY-MM-DD");
        endField.setPromptText("YYYY-MM-DD");
        startField.setPrefWidth(110);
        endField.setPrefWidth(110);

        Button filterButton = new Button("Filter");
        filterButton.setOnAction(event -> refresh());

        Button clearButton = new Button("Clear");
        clearButton.getStyleClass().add("secondary-button");
        clearButton.setOnAction(event -> {
            startField.clear();
            endField.clear();
            refresh();
        });

        HBox filterRow = new HBox(8, fromLabel, startField, toLabel, endField,
                filterButton, clearButton);
        filterRow.setAlignment(Pos.CENTER_LEFT);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        configureColumns();
        table.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, value) -> showDetail(value));

        detailLabel.setWrapText(true);
        detailLabel.getStyleClass().add("subtitle-label");

        VBox root = new VBox(16, title, subtitle, filterRow, table, detailLabel);
        root.getStyleClass().add("card");
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    /**
     * Configures the transaction table columns.
     */
    private void configureColumns() {
        table.getColumns().setAll(
                Fx.stringColumn("ID", TransactionDTO::id),
                Fx.stringColumn("Customer", TransactionDTO::customerName),
                Fx.stringColumn("Date", transaction -> transaction.timestamp()
                        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))),
                Fx.stringColumn("Total", transaction -> Fx.money(transaction.total())),
                Fx.stringColumn("Paid", transaction -> Fx.money(transaction.amountPaid())),
                Fx.stringColumn("Change", transaction -> Fx.money(transaction.change())),
                Fx.stringColumn("Points", transaction -> transaction.pointsEarned() + "/"
                        + transaction.pointsRedeemed()));
    }

    /**
     * Shows the line items and discounts for the selected transaction.
     *
     * @param transaction the selected transaction or null
     */
    private void showDetail(TransactionDTO transaction) {
        if (transaction == null) {
            detailLabel.setText("");
            return;
        }
        StringBuilder text = new StringBuilder();
        text.append("Tax: ").append(transaction.taxName())
                .append(" ").append(Fx.money(transaction.tax()));
        if (transaction.discount() != null
                && transaction.discount().compareTo(java.math.BigDecimal.ZERO) > 0) {
            text.append("  Discounts: ").append(String.join(", ",
                    transaction.appliedDiscounts()));
        }
        text.append("\n");
        for (TransactionItemDTO item : transaction.items()) {
            text.append(item.productName()).append(" x").append(item.quantity())
                    .append("  ").append(Fx.money(item.subtotal())).append("\n");
        }
        detailLabel.setText(text.toString());
    }

    /**
     * Reloads the transaction list, applying the date filter when both fields
     * are filled.
     */
    private void refresh() {
        GetTransactionsQuery query;
        try {
            if (!startField.getText().isBlank() && !endField.getText().isBlank()) {
                query = new GetTransactionsQuery(
                        java.time.LocalDate.parse(startField.getText().trim()),
                        java.time.LocalDate.parse(endField.getText().trim()));
            } else {
                query = new GetTransactionsQuery();
            }
        } catch (java.time.format.DateTimeParseException exception) {
            query = new GetTransactionsQuery();
        }
        Either<DomainError, List<TransactionDTO>> result = mediator.send(query);
        if (result.isRight()) {
            transactions.setAll(result.get());
        }
    }
}
