package com.konbini.view.dashboard;

import com.google.inject.Injector;
import com.konbini.application.dto.DashboardDTO;
import com.konbini.application.dto.TransactionDTO;
import com.konbini.application.dto.TransactionItemDTO;
import com.konbini.application.mediator.Mediator;
import com.konbini.application.query.GetDashboardQuery;
import com.konbini.application.query.GetTransactionsQuery;
import com.konbini.domain.common.DomainError;
import com.konbini.view.Fx;
import io.vavr.control.Either;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Dashboard view showing summary stat cards plus sales charts built live
 * from the current transaction data.
 */
public class DashboardView {

    private static final DateTimeFormatter DAY_LABEL = DateTimeFormatter.ofPattern("MM/dd");
    private static final int TREND_DAYS = 7;

    private final Mediator mediator;
    private final VBox root;

    private Label productsValue;
    private Label lowStockValue;
    private Label expiredValue;
    private Label customersValue;
    private Label transactionsValue;
    private Label salesValue;

    private LineChart<String, Number> salesTrendChart;
    private BarChart<String, Number> categoryChart;

    /**
     * Constructs the dashboard view.
     *
     * @param injector the Guice injector
     */
    public DashboardView(Injector injector) {
        this.mediator = injector.getInstance(Mediator.class);
        this.root = build();
        refresh();
    }

    /**
     * Returns the dashboard root node.
     *
     * @return the root node
     */
    public Node getRoot() {
        return root;
    }

    /**
     * Builds the layout.
     *
     * @return the root VBox
     */
    private VBox build() {
        Label title = new Label("Dashboard");
        title.getStyleClass().add("title-label");

        Label subtitle = new Label("Store overview");
        subtitle.getStyleClass().add("subtitle-label");

        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(event -> refresh());

        VBox header = new VBox(4, title, subtitle);
        header.setAlignment(Pos.CENTER_LEFT);
        HBox headerRow = new HBox(16, header, refreshButton);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        productsValue = statValue();
        lowStockValue = statValue();
        expiredValue = statValue();
        customersValue = statValue();
        transactionsValue = statValue();
        salesValue = statValue();

        grid.add(statCard("Total Products", productsValue), 0, 0);
        grid.add(statCard("Low Stock", lowStockValue), 1, 0);
        grid.add(statCard("Expired", expiredValue), 2, 0);
        grid.add(statCard("Customers", customersValue), 0, 1);
        grid.add(statCard("Transactions", transactionsValue), 1, 1);
        grid.add(statCard("Total Sales", salesValue), 2, 1);

        for (Node node : grid.getChildren()) {
            GridPane.setHgrow(node, Priority.ALWAYS);
            GridPane.setVgrow(node, Priority.ALWAYS);
        }

        salesTrendChart = buildSalesTrendChart();
        categoryChart = buildCategoryChart();

        VBox trendCard = chartCard("Sales — Last " + TREND_DAYS + " Days", salesTrendChart);
        VBox categoryCard = chartCard("Sales by Category", categoryChart);
        HBox.setHgrow(trendCard, Priority.ALWAYS);
        HBox.setHgrow(categoryCard, Priority.ALWAYS);
        HBox charts = new HBox(16, trendCard, categoryCard);

        StackPane content = new StackPane(grid);
        VBox rootBox = new VBox(20, headerRow, content, charts);
        rootBox.getStyleClass().add("card");
        VBox.setVgrow(charts, Priority.ALWAYS);
        return rootBox;
    }

    /**
     * Builds the empty daily sales trend line chart.
     *
     * @return the chart
     */
    private LineChart<String, Number> buildSalesTrendChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(true);
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setCreateSymbols(true);
        return chart;
    }

    /**
     * Builds the empty sales-by-category bar chart.
     *
     * @return the chart
     */
    private BarChart<String, Number> buildCategoryChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(true);
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        return chart;
    }

    /**
     * Wraps a chart in a titled card.
     *
     * @param title the card title
     * @param chart the chart node
     * @return the card
     */
    private VBox chartCard(String title, Node chart) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-label");
        VBox card = new VBox(10, titleLabel, chart);
        card.getStyleClass().add("card");
        VBox.setVgrow(chart, Priority.ALWAYS);
        return card;
    }

    /**
     * Builds an empty stat value label.
     *
     * @return the value label
     */
    private Label statValue() {
        Label label = new Label("–");
        label.getStyleClass().add("stat-value");
        return label;
    }

    /**
     * Builds a stat card containing the given value label.
     *
     * @param name the stat name
     * @param value the stat value label
     * @return the card node
     */
    private VBox statCard(String name, Label value) {
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("stat-label");
        VBox card = new VBox(6, value, nameLabel);
        card.getStyleClass().add("stat-card");
        card.setPrefSize(210, 120);
        return card;
    }

    /**
     * Reloads the dashboard statistics and chart data.
     */
    private void refresh() {
        Either<DomainError, DashboardDTO> statsResult =
                mediator.send(new GetDashboardQuery());
        if (statsResult.isRight()) {
            DashboardDTO dto = statsResult.get();
            productsValue.setText(String.valueOf(dto.totalProducts()));
            lowStockValue.setText(String.valueOf(dto.lowStockCount()));
            expiredValue.setText(String.valueOf(dto.expiredCount()));
            customersValue.setText(String.valueOf(dto.totalCustomers()));
            transactionsValue.setText(String.valueOf(dto.totalTransactions()));
            salesValue.setText(Fx.money(dto.totalSales()));
        }

        Either<DomainError, List<TransactionDTO>> transactionsResult =
                mediator.send(new GetTransactionsQuery());
        if (transactionsResult.isRight()) {
            List<TransactionDTO> transactions = transactionsResult.get();
            refreshSalesTrend(transactions);
            refreshCategoryBreakdown(transactions);
        }
    }

    /**
     * Rebuilds the daily sales trend series for the most recent days present
     * in the transaction history.
     *
     * @param transactions all transactions
     */
    private void refreshSalesTrend(List<TransactionDTO> transactions) {
        Map<LocalDate, BigDecimal> byDay = new TreeMap<>();
        for (TransactionDTO transaction : transactions) {
            LocalDate day = transaction.timestamp().toLocalDate();
            byDay.merge(day, transaction.total(), BigDecimal::add);
        }

        List<LocalDate> days = byDay.keySet().stream().toList();
        List<LocalDate> recentDays = days.size() > TREND_DAYS
                ? days.subList(days.size() - TREND_DAYS, days.size())
                : days;

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (LocalDate day : recentDays) {
            series.getData().add(new XYChart.Data<>(day.format(DAY_LABEL),
                    byDay.get(day).doubleValue()));
        }
        salesTrendChart.getData().setAll(series);
    }

    /**
     * Rebuilds the sales-by-category series from every line item across all
     * transactions.
     *
     * @param transactions all transactions
     */
    private void refreshCategoryBreakdown(List<TransactionDTO> transactions) {
        Map<String, BigDecimal> byCategory = new LinkedHashMap<>();
        for (TransactionDTO transaction : transactions) {
            for (TransactionItemDTO item : transaction.items()) {
                byCategory.merge(item.productCategory(), item.subtotal(), BigDecimal::add);
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        byCategory.forEach((category, total) ->
                series.getData().add(new XYChart.Data<>(category, total.doubleValue())));
        categoryChart.getData().setAll(series);
    }
}
