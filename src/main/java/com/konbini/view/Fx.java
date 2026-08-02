package com.konbini.view;

import com.konbini.application.mediator.Mediator;
import com.konbini.domain.common.DomainError;
import io.vavr.control.Either;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;

/**
 * Shared helpers for the JavaFX presentation layer. Presentation code talks
 * exclusively to the {@link Mediator} port; it never touches repositories or
 * services directly.
 */
public final class Fx {

    private Fx() {
    }

    /**
     * Extracts the error message from a left side, or returns a fallback.
     *
     * @param result the Either result
     * @param fallback the fallback message
     * @return the error message
     */
    public static String errorMessage(Either<DomainError, ?> result, String fallback) {
        return result.getLeft() == null
                ? fallback
                : result.getLeft().message();
    }

    /**
     * Shows an error dialog.
     *
     * @param owner owner window (may be null)
     * @param title the dialog title
     * @param message the error message
     */
    public static void showError(javafx.stage.Window owner, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows an informational dialog.
     *
     * @param owner owner window (may be null)
     * @param title the dialog title
     * @param message the info message
     */
    public static void showInfo(javafx.stage.Window owner, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Creates a read-only string table column from a value extractor.
     *
     * @param header the column header
     * @param extractor the extractor function
     * @param <T> the row type
     * @return the column
     */
    public static <T> TableColumn<T, String> stringColumn(String header, Function<T, String> extractor) {
        TableColumn<T, String> column = new TableColumn<>(header);
        column.setCellValueFactory(data -> new SimpleStringProperty(extractor.apply(data.getValue())));
        return column;
    }

    /**
     * Formats a currency amount for display.
     *
     * @param amount the amount
     * @return the formatted string
     */
    public static String money(BigDecimal amount) {
        return "₱" + amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    /**
     * Builds the CSS resource URL for the glassmorphism theme.
     *
     * @return the stylesheet URL string
     */
    public static String stylesheet() {
        return Fx.class.getResource("/com/konbini/view/styles.css").toExternalForm();
    }
}
