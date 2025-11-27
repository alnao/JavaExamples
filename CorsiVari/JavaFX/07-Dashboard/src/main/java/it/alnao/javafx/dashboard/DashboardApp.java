package it.alnao.javafx.dashboard;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Progetto 07 - Interactive Dashboard
 * Concetto principale: Charts API (PieChart, LineChart, BarChart)
 * 
 * Dashboard interattiva con diversi tipi di grafici per
 * visualizzare dati statistici.
 */
public class DashboardApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Titolo
        Label titleLabel = new Label("📊 Interactive Dashboard");
        titleLabel.setStyle(
            "-fx-font-size: 32px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #0d6efd;"
        );

        // Crea i grafici
        PieChart pieChart = createPieChart();
        LineChart<String, Number> lineChart = createLineChart();
        BarChart<String, Number> barChart = createBarChart();

        // Layout grafici
        GridPane chartsGrid = new GridPane();
        chartsGrid.setHgap(20);
        chartsGrid.setVgap(20);
        chartsGrid.setPadding(new Insets(20));
        chartsGrid.setAlignment(Pos.CENTER);

        chartsGrid.add(createChartCard("Vendite per Categoria", pieChart), 0, 0);
        chartsGrid.add(createChartCard("Trend Mensile", lineChart), 1, 0);
        chartsGrid.add(createChartCard("Confronto Trimestrale", barChart), 0, 1, 2, 1);

        // Layout principale
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f8f9fa;");
        root.getChildren().addAll(titleLabel, chartsGrid);

        Scene scene = new Scene(root, 1100, 800);
        primaryStage.setTitle("Interactive Dashboard - Charts API Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Crea un PieChart con dati di vendita
     */
    private PieChart createPieChart() {
        PieChart pieChart = new PieChart(FXCollections.observableArrayList(
            new PieChart.Data("Elettronica", 35),
            new PieChart.Data("Abbigliamento", 25),
            new PieChart.Data("Alimentari", 20),
            new PieChart.Data("Libri", 12),
            new PieChart.Data("Altri", 8)
        ));
        
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(true);
        pieChart.setPrefSize(450, 350);
        
        return pieChart;
    }

    /**
     * Crea un LineChart con trend mensile
     */
    private LineChart<String, Number> createLineChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Mese");
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Vendite (€)");

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        
        XYChart.Series<String, Number> series2023 = new XYChart.Series<>();
        series2023.setName("2023");
        series2023.getData().add(new XYChart.Data<>("Gen", 15000));
        series2023.getData().add(new XYChart.Data<>("Feb", 18000));
        series2023.getData().add(new XYChart.Data<>("Mar", 22000));
        series2023.getData().add(new XYChart.Data<>("Apr", 19000));
        series2023.getData().add(new XYChart.Data<>("Mag", 25000));
        series2023.getData().add(new XYChart.Data<>("Giu", 28000));

        XYChart.Series<String, Number> series2024 = new XYChart.Series<>();
        series2024.setName("2024");
        series2024.getData().add(new XYChart.Data<>("Gen", 18000));
        series2024.getData().add(new XYChart.Data<>("Feb", 21000));
        series2024.getData().add(new XYChart.Data<>("Mar", 27000));
        series2024.getData().add(new XYChart.Data<>("Apr", 24000));
        series2024.getData().add(new XYChart.Data<>("Mag", 31000));
        series2024.getData().add(new XYChart.Data<>("Giu", 35000));

        lineChart.getData().addAll(series2023, series2024);
        lineChart.setPrefSize(450, 350);
        
        return lineChart;
    }

    /**
     * Crea un BarChart con confronto trimestrale
     */
    private BarChart<String, Number> createBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Trimestre");
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Fatturato (€)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        
        XYChart.Series<String, Number> seriesNord = new XYChart.Series<>();
        seriesNord.setName("Nord Italia");
        seriesNord.getData().add(new XYChart.Data<>("Q1", 85000));
        seriesNord.getData().add(new XYChart.Data<>("Q2", 92000));
        seriesNord.getData().add(new XYChart.Data<>("Q3", 88000));
        seriesNord.getData().add(new XYChart.Data<>("Q4", 105000));

        XYChart.Series<String, Number> seriesCentro = new XYChart.Series<>();
        seriesCentro.setName("Centro Italia");
        seriesCentro.getData().add(new XYChart.Data<>("Q1", 65000));
        seriesCentro.getData().add(new XYChart.Data<>("Q2", 71000));
        seriesCentro.getData().add(new XYChart.Data<>("Q3", 68000));
        seriesCentro.getData().add(new XYChart.Data<>("Q4", 78000));

        XYChart.Series<String, Number> seriesSud = new XYChart.Series<>();
        seriesSud.setName("Sud Italia");
        seriesSud.getData().add(new XYChart.Data<>("Q1", 48000));
        seriesSud.getData().add(new XYChart.Data<>("Q2", 52000));
        seriesSud.getData().add(new XYChart.Data<>("Q3", 55000));
        seriesSud.getData().add(new XYChart.Data<>("Q4", 61000));

        barChart.getData().addAll(seriesNord, seriesCentro, seriesSud);
        barChart.setPrefSize(920, 350);
        
        return barChart;
    }

    /**
     * Crea una card per contenere un grafico
     */
    private VBox createChartCard(String title, javafx.scene.Node chart) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #0d6efd;"
        );

        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        card.getChildren().addAll(titleLabel, chart);
        
        return card;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
