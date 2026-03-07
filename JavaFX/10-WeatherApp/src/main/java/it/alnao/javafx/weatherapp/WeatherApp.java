package it.alnao.javafx.weatherapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Progetto 10 - Weather App MVC
 * Concetto principale: Full MVC Architecture con FXML, CSS, Controller
 * 
 * Applicazione completa con:
 * - Model (Weather.java)
 * - View (weather.fxml + style.css)
 * - Controller (WeatherController.java)
 * - Service (WeatherService.java per logica business)
 */
public class WeatherApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carica il file FXML
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/it/alnao/javafx/weatherapp/weather.fxml")
        );
        Parent root = loader.load();

        // Crea la scena e applica il CSS
        Scene scene = new Scene(root, 700, 600);
        scene.getStylesheets().add(
            getClass().getResource("/it/alnao/javafx/weatherapp/style.css").toExternalForm()
        );

        primaryStage.setTitle("Weather App - Full MVC Architecture");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
