# JavaFX Tutorial - 10 Progetti Progressivi

Benvenuto nella serie completa di tutorial JavaFX! Questa raccolta di 10 progetti ti guiderà dall'apprendimento dei concetti base fino all'implementazione di architetture MVC complete.

## 📚 Panoramica

Ogni progetto introduce un **concetto chiave** di JavaFX, costruendo progressivamente competenze su quelli precedenti. Tutti i progetti utilizzano:
- **Java 17**
- **JavaFX 21.0.1**
- **Maven 3.8+**
- **Stile Bootstrap 5** (colori e design)

## 🎯 Elenco Progetti

| # | Nome | Concetto Chiave | Difficoltà |
|---|------|----------------|------------|
| [01](01-ClickCounter/) | Click Counter | Event Handling | ⭐ |
| [02](02-UnitConverter/) | Unit Converter | Property Binding | ⭐⭐ |
| [03](03-TodoList/) | To-Do List | ObservableList | ⭐⭐ |
| [04](04-EmployeeTable/) | Employee Table | TableView + CRUD | ⭐⭐⭐ |
| [05](05-TextEditor/) | Text Editor | MenuBar + FileChooser | ⭐⭐⭐ |
| [06](06-DrawingCanvas/) | Drawing Canvas | Canvas + GraphicsContext | ⭐⭐⭐ |
| [07](07-Dashboard/) | Interactive Dashboard | Charts API | ⭐⭐⭐⭐ |
| [08](08-FileDownloader/) | File Downloader | Task + Concurrency | ⭐⭐⭐⭐ |
| [09](09-MusicPlayer/) | Music Player | MediaPlayer | ⭐⭐⭐⭐ |
| [10](10-WeatherApp/) | Weather App | MVC + FXML + CSS | ⭐⭐⭐⭐⭐ |

## 🚀 Come Iniziare

### Prerequisiti
```bash
# Verifica Java 17+
java -version

# Verifica Maven 3.8+
mvn -version
```

### Esecuzione di un Progetto
```bash
# Entra nella cartella del progetto
cd 01-ClickCounter

# Compila
mvn clean compile

# Esegui
mvn javafx:run
```

## 📖 Percorso di Apprendimento

### Livello 1: Fondamenti (Progetti 01-03)
- **01 - Click Counter**: Impara l'**Event Handling** base
- **02 - Unit Converter**: Scopri il **Property Binding** bidirezionale
- **03 - To-Do List**: Gestisci liste reattive con **ObservableList**

**Concetti Appresi**: Event listeners, lambda expressions, Property, Binding, Layout (VBox, GridPane, HBox)

### Livello 2: Componenti Avanzati (Progetti 04-06)
- **04 - Employee Table**: Master **TableView** con operazioni CRUD
- **05 - Text Editor**: Usa **MenuBar** e **FileChooser** per File I/O
- **06 - Drawing Canvas**: Disegna con **Canvas** e **GraphicsContext**

**Concetti Appresi**: TableView, PropertyValueFactory, POJO, MenuBar, FileChooser, Canvas API, BorderPane

### Livello 3: Visualizzazione e Media (Progetti 07-09)
- **07 - Dashboard**: Crea grafici con **Charts API** (Pie, Line, Bar)
- **08 - File Downloader**: Gestisci **Task asincroni** e **Concurrency**
- **09 - Music Player**: Riproduci audio con **MediaPlayer**

**Concetti Appresi**: PieChart, LineChart, BarChart, Task, Platform.runLater(), ProgressBar, MediaPlayer, Duration

### Livello 4: Architettura Professionale (Progetto 10)
- **10 - Weather App**: Implementa **Full MVC** con **FXML** e **CSS**

**Concetti Appresi**: MVC Pattern, FXML, FXMLLoader, @FXML, CSS esterno, Service Layer, Controller separation

## 🎨 Stile Grafico

Tutti i progetti utilizzano un design coerente ispirato a **Bootstrap 5**:

### Palette Colori
```
Primary Blue:   #0d6efd  (pulsanti, titoli)
Secondary Gray: #6c757d  (testi secondari)
Light Gray:     #f8f9fa  (sfondi)
Border Gray:    #ced4da  (bordi input)
Success Green:  #198754  (successo)
Danger Red:     #dc3545  (errori)
```

### Stile Card
Ogni progetto usa card con:
- Background bianco
- Border-radius 10px
- Drop shadow (gaussian blur)
- Padding interno 25-30px

## 📦 Struttura Progetto Standard

Ogni progetto segue questa struttura Maven:
```
XX-NomeProgetto/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── it/alnao/javafx/nomeprogetto/
        │       ├── NomeProgettoApp.java
        │       └── (altre classi se necessario)
        └── resources/ (solo Progetto 10)
            └── it/alnao/javafx/weatherapp/
                ├── weather.fxml
                └── style.css
```

## 🎓 Concetti JavaFX Coperti

### Layout Managers
- ✅ VBox / HBox
- ✅ GridPane
- ✅ BorderPane
- ✅ StackPane (implicito in alcuni progetti)

### Controls
- ✅ Button, Label, TextField, TextArea
- ✅ ComboBox, ListView, TableView
- ✅ Slider, ProgressBar, ProgressIndicator
- ✅ ColorPicker, FileChooser
- ✅ MenuBar, Menu, MenuItem

### Property & Binding
- ✅ StringProperty, IntegerProperty, DoubleProperty
- ✅ Binding unidirezionale e bidirezionale
- ✅ ObservableList, ObservableValue
- ✅ ChangeListener, ListChangeListener

### Charts
- ✅ PieChart
- ✅ LineChart
- ✅ BarChart

### Graphics
- ✅ Canvas
- ✅ GraphicsContext
- ✅ Immediate Mode Rendering

### Media
- ✅ Media
- ✅ MediaPlayer
- ✅ Media controls

### Concurrency
- ✅ Task
- ✅ Platform.runLater()
- ✅ Background threading

### MVC & FXML
- ✅ FXML definition
- ✅ Controller separation
- ✅ @FXML annotation
- ✅ CSS external styling
- ✅ FXMLLoader

## 🔧 Tips & Best Practices

### 1. Esecuzione da IDE
Puoi importare i progetti in Eclipse/IntelliJ:
- **Eclipse**: File → Import → Existing Maven Projects
- **IntelliJ IDEA**: File → Open → Seleziona cartella progetto

### 2. Debugging
Aggiungi breakpoint nei listener/handler:
```java
button.setOnAction(e -> {
    System.out.println("Button clicked!"); // Debug print
    handleAction();
});
```

### 3. Package non riconosciuto
Se vedi errori "package does not match expected package":
- Assicurati di importare il progetto come Maven Project
- Fai refresh Maven: `mvn clean install`

### 4. JavaFX Runtime Missing
Se ottieni errore "JavaFX runtime components are missing":
```bash
# Usa sempre il plugin javafx-maven-plugin
mvn javafx:run

# NON usare:
java -jar target/app.jar  ❌
```

## 📚 Risorse Aggiuntive

### Documentazione Ufficiale
- [JavaFX Documentation](https://openjfx.io/)
- [JavaFX API Javadoc](https://openjfx.io/javadoc/21/)

### Tool Utili
- [Scene Builder](https://gluonhq.com/products/scene-builder/) - Visual FXML editor
- [ControlsFX](https://github.com/controlsfx/controlsfx) - Extended controls library

### Esempi Avanzati
- [JFoenix](https://github.com/sshahine/JFoenix) - Material Design for JavaFX
- [TilesFX](https://github.com/HanSolo/tilesfx) - Dashboard tiles library




# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.
