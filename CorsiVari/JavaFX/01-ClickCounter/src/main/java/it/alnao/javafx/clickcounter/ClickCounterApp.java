package it.alnao.javafx.clickcounter;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Click Counter - Progetto JavaFX 01
 * Un semplice contatore che incrementa ad ogni click
 * 
 * Concetto chiave: Event Handling con setOnAction
 * 
 * @author AlNao
 */
public class ClickCounterApp extends Application {
    
    private int counter = 0;
    
    @Override
    public void start(Stage primaryStage) {
        // Label per visualizzare il contatore
        Label counterLabel = new Label("0");
        counterLabel.setStyle(
            "-fx-font-size: 48px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #0d6efd;"
        );
        
        // Label descrittiva
        Label descLabel = new Label("Click sul pulsante per incrementare");
        descLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #6c757d;"
        );
        
        // Pulsante principale
        Button incrementButton = new Button("Incrementa");
        incrementButton.setStyle(
            "-fx-background-color: #0d6efd; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-padding: 10 30 10 30; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        
        // Event Handler: incrementa il contatore
        incrementButton.setOnAction(e -> {
            counter++;
            counterLabel.setText(String.valueOf(counter));
        });
        
        // Hover effect
        incrementButton.setOnMouseEntered(e -> 
            incrementButton.setStyle(
                "-fx-background-color: #0b5ed7; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-padding: 10 30 10 30; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;"
            )
        );
        incrementButton.setOnMouseExited(e -> 
            incrementButton.setStyle(
                "-fx-background-color: #0d6efd; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-padding: 10 30 10 30; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;"
            )
        );
        
        // Pulsante Reset
        Button resetButton = new Button("Reset");
        resetButton.setStyle(
            "-fx-background-color: #6c757d; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 8 20 8 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        
        resetButton.setOnAction(e -> {
            counter = 0;
            counterLabel.setText("0");
        });
        
        // Layout VBox (Bootstrap-like)
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #f8f9fa;");
        root.getChildren().addAll(descLabel, counterLabel, incrementButton, resetButton);
        
        // Scene e Stage
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Click Counter - JavaFX 01");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
