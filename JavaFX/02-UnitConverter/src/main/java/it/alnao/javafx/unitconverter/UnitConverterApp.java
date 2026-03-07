package it.alnao.javafx.unitconverter;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

/**
 * Unit Converter - Progetto JavaFX 02
 * Convertitore di unità con Property Binding automatico
 * 
 * Concetto chiave: Property Binding bidirezionale
 * 
 * @author AlNao
 */
public class UnitConverterApp extends Application {
    
    // Properties per binding bidirezionale
    private final DoubleProperty value1 = new SimpleDoubleProperty(0);
    private final DoubleProperty value2 = new SimpleDoubleProperty(0);
    
    @Override
    public void start(Stage primaryStage) {
        // Titolo
        Label titleLabel = new Label("Convertitore di Valuta");
        titleLabel.setStyle(
            "-fx-font-size: 24px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #212529;"
        );
        
        // ComboBox per selezione valuta
        ComboBox<String> currencyCombo = new ComboBox<>();
        currencyCombo.getItems().addAll("EUR → USD", "USD → EUR", "EUR → GBP", "GBP → EUR");
        currencyCombo.setValue("EUR → USD");
        currencyCombo.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-padding: 8; " +
            "-fx-background-color: white; " +
            "-fx-border-color: #ced4da; " +
            "-fx-border-radius: 5; " +
            "-fx-background-radius: 5;"
        );
        
        // GridPane per i campi
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(20));
        grid.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #dee2e6; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        // Campo 1
        Label label1 = new Label("Valore:");
        label1.setStyle("-fx-font-size: 14px; -fx-text-fill: #495057;");
        TextField field1 = new TextField("0");
        field1.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-padding: 10; " +
            "-fx-border-color: #ced4da; " +
            "-fx-border-radius: 5; " +
            "-fx-background-radius: 5;"
        );
        field1.setPrefWidth(200);
        
        // Campo 2
        Label label2 = new Label("Convertito:");
        label2.setStyle("-fx-font-size: 14px; -fx-text-fill: #495057;");
        TextField field2 = new TextField("0");
        field2.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-padding: 10; " +
            "-fx-border-color: #ced4da; " +
            "-fx-border-radius: 5; " +
            "-fx-background-radius: 5; " +
            "-fx-background-color: #e9ecef;"
        );
        field2.setEditable(false);
        field2.setPrefWidth(200);
        
        grid.add(label1, 0, 0);
        grid.add(field1, 1, 0);
        grid.add(label2, 0, 1);
        grid.add(field2, 1, 1);
        
        // Binding bidirezionale tra TextField e Property
        Bindings.bindBidirectional(field1.textProperty(), value1, new NumberStringConverter());
        Bindings.bindBidirectional(field2.textProperty(), value2, new NumberStringConverter());
        
        // Binding unidirezionale con conversione
        currencyCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateConversion(newVal);
        });
        
        // Inizializza conversione
        updateConversion(currencyCombo.getValue());
        
        // Info label
        Label infoLabel = new Label("💡 Modifica il valore per vedere la conversione automatica");
        infoLabel.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-text-fill: #6c757d; " +
            "-fx-padding: 10;"
        );
        
        // Layout principale
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f8f9fa;");
        root.getChildren().addAll(titleLabel, currencyCombo, grid, infoLabel);
        
        Scene scene = new Scene(root, 500, 400);
        primaryStage.setTitle("Unit Converter - JavaFX 02");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Aggiorna il binding di conversione in base alla valuta selezionata
     */
    private void updateConversion(String conversion) {
        value2.unbind();
        
        switch (conversion) {
            case "EUR → USD":
                value2.bind(value1.multiply(1.10)); // Tasso fittizio
                break;
            case "USD → EUR":
                value2.bind(value1.multiply(0.91));
                break;
            case "EUR → GBP":
                value2.bind(value1.multiply(0.86));
                break;
            case "GBP → EUR":
                value2.bind(value1.multiply(1.16));
                break;
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
