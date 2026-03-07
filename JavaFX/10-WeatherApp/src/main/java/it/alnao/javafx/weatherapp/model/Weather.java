package it.alnao.javafx.weatherapp.model;

/**
 * Model per rappresentare i dati meteorologici
 */
public class Weather {
    private String city;
    private double temperature;
    private String condition;
    private int humidity;
    private double windSpeed;

    public Weather(String city, double temperature, String condition, int humidity, double windSpeed) {
        this.city = city;
        this.temperature = temperature;
        this.condition = condition;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    
    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }
    
    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
}
