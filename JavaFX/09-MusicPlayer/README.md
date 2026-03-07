# Progetto 09 - MP3 Music Player

## 🎵 Descrizione
Lettore musicale completo con controlli play/pause/stop, timeline seekable, controllo volume e supporto file audio (MP3, WAV, M4A).

## 🎯 Concetto Principale: MediaPlayer

### Cos'è MediaPlayer?
**MediaPlayer** è l'API JavaFX per riprodurre contenuti audio e video. Si basa su una pipeline: **Media** → **MediaPlayer** → **MediaView** (per video).

### Architettura MediaPlayer
```
File Audio (MP3)
    ↓
Media (wrapper URI)
    ↓
MediaPlayer (controllo riproduzione)
    ↓
Binding UI Controls (volume, timeline, status)
```

### Creazione MediaPlayer
```java
// 1. Crea Media da URI
File file = new File("music.mp3");
Media media = new Media(file.toURI().toString());

// 2. Crea MediaPlayer
MediaPlayer mediaPlayer = new MediaPlayer(media);

// 3. Controlli
mediaPlayer.play();
mediaPlayer.pause();
mediaPlayer.stop();
mediaPlayer.setVolume(0.5); // 0.0 - 1.0
mediaPlayer.seek(Duration.seconds(30)); // Salta a 30 secondi
```

### Lifecycle MediaPlayer
```
Lifecycle Stati:
1. READY      → Media caricato, pronto per play
2. PLAYING    → In riproduzione
3. PAUSED     → In pausa
4. STOPPED    → Fermato
5. DISPOSED   → Rilasciato (non più usabile)
```

## 🆚 Confronto con i Progetti Precedenti

| Progetto | Media Type | API Usata |
|----------|-----------|-----------|
| 01-08 | N/A | JavaFX Controls standard |
| **09 - Music Player** | **Audio** | **javafx-media (MediaPlayer)** |

### Dependency javafx-media
```xml
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-media</artifactId>
    <version>21.0.1</version>
</dependency>
```

## 🏗️ Componenti JavaFX Utilizzati

### Media
```java
Media media = new Media(uri);
Duration duration = media.getDuration(); // Durata totale
```

### MediaPlayer
```java
MediaPlayer mp = new MediaPlayer(media);

// Proprietà osservabili
mp.currentTimeProperty().addListener(...);
mp.statusProperty().addListener(...);
mp.volumeProperty().bind(slider.valueProperty());

// Eventi
mp.setOnReady(() -> { /* Media caricato */ });
mp.setOnEndOfMedia(() -> { /* Fine riproduzione */ });
mp.setOnError(() -> { /* Errore */ });
```

### Slider per Timeline
```java
Slider timeSlider = new Slider();
timeSlider.setMax(media.getDuration().toSeconds());

// Seek quando l'utente trascina
timeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
    if (timeSlider.isValueChanging()) {
        mp.seek(Duration.seconds(newVal.doubleValue()));
    }
});

// Aggiorna durante riproduzione
mp.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
    if (!timeSlider.isValueChanging()) {
        timeSlider.setValue(newTime.toSeconds());
    }
});
```

### FileChooser per Caricamento
```java
FileChooser fileChooser = new FileChooser();
fileChooser.getExtensionFilters().add(
    new FileChooser.ExtensionFilter("File Audio", "*.mp3", "*.wav", "*.m4a")
);
File file = fileChooser.showOpenDialog(stage);
```

## 🎨 Funzionalità Implementate

1. **Carica MP3**: FileChooser con filtri audio (MP3, WAV, M4A)
2. **Play/Pause Toggle**: Pulsante singolo con stato
3. **Stop**: Ferma riproduzione e reset timeline
4. **Timeline Slider**: Seekable (trascina per saltare)
5. **Volume Slider**: Binding diretto a `volumeProperty()`
6. **Time Label**: Formato mm:ss (current / total)
7. **Auto-Stop**: Riproduzione ferma automaticamente a fine traccia
8. **Cleanup**: Stop del MediaPlayer alla chiusura app

## 📦 Compilazione ed Esecuzione

```bash
mvn clean compile
mvn javafx:run
```

**Nota**: Per testare, carica un file MP3 usando il pulsante "📁 Carica MP3".




# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.