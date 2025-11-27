package it.alnao.javafx.weatherapp.service;

import it.alnao.javafx.weatherapp.model.Weather;
import java.util.Random;

/**
 * Service per recuperare dati meteorologici
 * In un caso reale chiamerebbe una REST API (es. OpenWeatherMap)
 */
public class WeatherService {
    
    private Random random = new Random();
    private String[] conditions = {"Soleggiato", "Nuvoloso", "Piovoso", "Nevoso", "Temporalesco"};

    /**
     * Simula una chiamata REST API per ottenere il meteo
     * In un caso reale userebbe HttpClient per chiamare una vera API
     */
    public Weather getWeatherByCity(String city) throws Exception {
        // Simula latenza di rete
        Thread.sleep(1500);
        
        // Genera dati casuali
        double temperature = 10 + (random.nextDouble() * 20); // 10-30°C
        String condition = conditions[random.nextInt(conditions.length)];
        int humidity = 40 + random.nextInt(40); // 40-80%
        double windSpeed = 5 + (random.nextDouble() * 20); // 5-25 km/h
        
        return new Weather(city, temperature, condition, humidity, windSpeed);
    }
}
