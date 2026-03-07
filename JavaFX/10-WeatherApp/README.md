# Progetto 10 - Weather App MVC

## 🌤️ Descrizione
Applicazione meteo completa con architettura MVC professionale: FXML per la View, CSS per lo styling, Controller separato e Service layer per la logica business.

## 🎯 Concetto Principale: Full MVC Architecture

### Cos'è MVC?
**MVC (Model-View-Controller)** è un pattern architetturale che separa:
- **Model**: Dati dell'applicazione (`Weather.java`)
- **View**: Interfaccia utente (`weather.fxml` + `style.css`)
- **Controller**: Logica di presentazione (`WeatherController.java`)
- **Service** *(bonus)*: Logica business (`WeatherService.java`)

### Architettura del Progetto
```
WeatherApp (Main)
    │
    ├── View (FXML + CSS)
    │    ├── weather.fxml       (struttura UI)
    │    └── style.css          (stili Bootstrap-like)
    │
    ├── Controller
    │    └── WeatherController.java (gestione eventi UI)
    │
    ├── Model
    │    └── Weather.java       (POJO dati meteo)
    │
    └── Service
         └── WeatherService.java (logica business/API)
```

### FXML
Linguaggio dichiarativo XML per definire UI:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<VBox xmlns:fx="http://javafx.com/fxml"
      fx:controller="it.alnao.javafx.weatherapp.controller.WeatherController">
    <TextField fx:id="cityField"/>
    <Button fx:id="searchButton" onAction="#handleSearch"/>
</VBox>
```

### Annotazione @FXML
Collega elementi FXML al Controller:
```java
@FXML private TextField cityField;     // fx:id="cityField"
@FXML private Button searchButton;     // fx:id="searchButton"

@FXML
private void handleSearch() {          // onAction="#handleSearch"
    String city = cityField.getText();
}
```

### CSS Esterno
Stili separati dalla logica:
```css
.title {
    -fx-font-size: 36px;
    -fx-font-weight: bold;
    -fx-text-fill: #0d6efd;
}

.search-button {
    -fx-background-color: #0d6efd;
    -fx-text-fill: white;
}
```

## 🆚 Confronto con i Progetti Precedenti

| Progetto | UI Definition | Styling | Architecture |
|----------|--------------|---------|--------------|
| 01-09 | Codice Java | Inline CSS | Monolitica |
| **10 - Weather App** | **FXML** | **CSS Esterno** | **MVC Separato** |

### Evoluzione Architetturale
1. **Progetti 01-09**: UI, logica e dati mescolati in una classe
2. **Progetto 10**: Separazione completa in Model/View/Controller/Service

### Vantaggi MVC
✅ **Manutenibilità**: Modifiche isolate per layer  
✅ **Testabilità**: Controller e Service testabili separatamente  
✅ **Scalabilità**: Aggiunta feature senza toccare altre parti  
✅ **Team Work**: Designer lavora su FXML/CSS, developer su Controller  

## 🏗️ Struttura File Progetto

```
10-WeatherApp/
├── pom.xml                              (Maven + javafx-fxml + Gson)
├── src/main/java/it/alnao/javafx/weatherapp/
│   ├── WeatherApp.java                  (Main - carica FXML)
│   ├── model/
│   │   └── Weather.java                 (POJO dati)
│   ├── controller/
│   │   └── WeatherController.java       (logica UI)
│   └── service/
│       └── WeatherService.java          (chiamata API simulata)
└── src/main/resources/it/alnao/javafx/weatherapp/
    ├── weather.fxml                     (struttura UI)
    └── style.css                        (stili Bootstrap 5)
```

## 🎨 Funzionalità Implementate

1. **Ricerca Città**: TextField + pulsante "Cerca"
2. **Loading Indicator**: ProgressIndicator durante chiamata API
3. **Dati Meteo Visualizzati**:
   - Nome città
   - Temperatura (°C)
   - Condizione (Soleggiato, Nuvoloso, etc.)
   - Umidità (%)
   - Velocità vento (km/h)
4. **Gestione Errori**: Label errore per città vuota
5. **Task Asincrono**: WeatherService chiamato in background
6. **REST API Simulation**: Dati casuali con latenza simulata (1.5s)

## 🌐 Simulazione REST API

In un caso reale, `WeatherService` chiamerebbe una vera API REST:
```java
// REAL WORLD EXAMPLE (non implementato):
String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city;
HttpClient client = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
Weather weather = gson.fromJson(response.body(), Weather.class);
```

Nel progetto è simulato con:
```java
Thread.sleep(1500); // Simula latenza rete
double temp = 10 + (random.nextDouble() * 20); // Dati casuali
```

## 📦 Compilazione ed Esecuzione

```bash
mvn clean compile
mvn javafx:run
```

Inserisci una città (es. "Milano") e clicca "Cerca" per vedere i dati meteo simulati.

## 🎓 Concetti Appresi

1. **MVC Pattern**: Separazione Model/View/Controller
2. **FXML**: Definizione UI dichiarativa XML
3. **FXMLLoader**: Caricamento file FXML
4. **@FXML Annotation**: Binding elementi UI a Controller
5. **fx:id**: ID univoco per riferimento in Controller
6. **fx:controller**: Specifica classe Controller
7. **onAction="#methodName"**: Binding eventi a metodi Controller
8. **CSS Esterno**: Stili separati con `scene.getStylesheets().add()`
9. **styleClass**: Classi CSS per elementi UI
10. **Service Layer**: Separazione logica business da Controller
11. **Task in Controller**: Chiamate asincrone senza bloccare UI
12. **POJO (Weather)**: Oggetto Plain Old Java per dati
13. **initialize()**: Metodo chiamato dopo caricamento FXML
14. **getResource()**: Caricamento risorse da classpath

## 🏆 Riepilogo Serie Completa

Hai completato tutti i 10 progetti JavaFX! Ecco cosa hai imparato:

| # | Progetto | Concetto Chiave |
|---|----------|----------------|
| 01 | Click Counter | Event Handling |
| 02 | Unit Converter | Property Binding |
| 03 | To-Do List | ObservableList |
| 04 | Employee Table | TableView + CRUD |
| 05 | Text Editor | MenuBar + FileChooser |
| 06 | Drawing Canvas | Canvas + GraphicsContext |
| 07 | Dashboard | Charts API |
| 08 | File Downloader | Task + Concurrency |
| 09 | Music Player | MediaPlayer |
| 10 | Weather App | **MVC + FXML + CSS** |

## 🚀 Prossimi Passi

Ora sei pronto per:
1. **Progetti Personali**: Combina i concetti appresi
2. **REST API Reali**: Integra OpenWeatherMap, GitHub API, etc.
3. **Database**: Connetti a MySQL/PostgreSQL con JDBC
4. **JavaFX + Spring Boot**: Backend REST + Frontend JavaFX
5. **Packaging**: Crea installer con jpackage
6. **SceneBuilder**: Tool visuale per FXML




# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.