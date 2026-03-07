package it.alnao.javafx.weatherapp.controller;

import it.alnao.javafx.weatherapp.model.Weather;
import it.alnao.javafx.weatherapp.service.WeatherService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller per la vista Weather
 * Gestisce l'interazione tra UI e Service layer
 */
public class WeatherController {
    
    @FXML private TextField cityField;
    @FXML private Button searchButton;
    @FXML private Label cityLabel;
    @FXML private Label temperatureLabel;
    @FXML private Label conditionLabel;
    @FXML private Label humidityLabel;
    @FXML private Label windLabel;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;

    private WeatherService weatherService;

    /**
     * Inizializza il controller (chiamato automaticamente dopo il caricamento FXML)
     */
    @FXML
    public void initialize() {
        weatherService = new WeatherService();
        loadingIndicator.setVisible(false);
        errorLabel.setVisible(false);
        
        // Permetti ricerca con Invio
        cityField.setOnAction(e -> handleSearch());
    }

    /**
     * Handler per il pulsante di ricerca
     */
    @FXML
    private void handleSearch() {
        String city = cityField.getText().trim();
        if (city.isEmpty()) {
            showError("Inserisci il nome di una città!");
            return;
        }
        
        searchWeather(city);
    }

    /**
     * Cerca il meteo in modo asincrono usando Task
     */
    private void searchWeather(String city) {
        searchButton.setDisable(true);
        loadingIndicator.setVisible(true);
        errorLabel.setVisible(false);
        clearWeatherData();

        Task<Weather> weatherTask = new Task<Weather>() {
            @Override
            protected Weather call() throws Exception {
                return weatherService.getWeatherByCity(city);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                Weather weather = getValue();
                displayWeather(weather);
                searchButton.setDisable(false);
                loadingIndicator.setVisible(false);
            }

            @Override
            protected void failed() {
                super.failed();
                showError("Errore nel recupero dei dati meteo");
                searchButton.setDisable(false);
                loadingIndicator.setVisible(false);
            }
        };

        Thread thread = new Thread(weatherTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Visualizza i dati meteo recuperati
     */
    private void displayWeather(Weather weather) {
        cityLabel.setText(weather.getCity());
        temperatureLabel.setText(String.format("%.1f °C", weather.getTemperature()));
        conditionLabel.setText(weather.getCondition());
        humidityLabel.setText("Umidità: " + weather.getHumidity() + "%");
        windLabel.setText(String.format("Vento: %.1f km/h", weather.getWindSpeed()));
    }

    /**
     * Pulisce i dati meteo visualizzati
     */
    private void clearWeatherData() {
        cityLabel.setText("---");
        temperatureLabel.setText("--°C");
        conditionLabel.setText("---");
        humidityLabel.setText("Umidità: --%");
        windLabel.setText("Vento: -- km/h");
    }

    /**
     * Mostra un messaggio di errore
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
