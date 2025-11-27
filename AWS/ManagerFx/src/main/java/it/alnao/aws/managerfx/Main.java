package it.alnao.aws.managerfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principale dell'applicazione AWS Manager JavaFX
 * 
 * @author AlNao
 * @version 1.0
 * @since 2025-11-27
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Carica il file FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            
            // Carica il CSS
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            
            // Configura la finestra principale
            primaryStage.setTitle("AWS Manager - Dashboard Risorse");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(600);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore durante l'avvio dell'applicazione: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
