# Progetto 05 - Simple Text Editor

## 📝 Descrizione
Editor di testo semplice con menu completo e operazioni di File I/O per aprire, modificare e salvare file di testo.

## 🎯 Concetto Principale: MenuBar e FileChooser

### Cos'è una MenuBar?
Una **MenuBar** è un componente per creare menu strutturati (File, Edit, Help, ecc.) con MenuItem e sottomenu.

### MenuBar Struttura
```
MenuBar
 ├── Menu "File"
 │    ├── MenuItem "Nuovo"
 │    ├── MenuItem "Apri..."
 │    ├── MenuItem "Salva"
 │    ├── MenuItem "Salva con nome..."
 │    ├── SeparatorMenuItem
 │    └── MenuItem "Esci"
 ├── Menu "Modifica"
 │    ├── MenuItem "Taglia"
 │    ├── MenuItem "Copia"
 │    └── MenuItem "Incolla"
 └── Menu "Aiuto"
      └── MenuItem "Info"
```

### FileChooser
Dialog standard per selezionare file da aprire o salvare:
```java
FileChooser fileChooser = new FileChooser();
fileChooser.setTitle("Apri File");
fileChooser.getExtensionFilters().add(
    new FileChooser.ExtensionFilter("File di Testo", "*.txt")
);
File file = fileChooser.showOpenDialog(stage);
```

### File I/O con NIO.2
```java
// Lettura file
String content = Files.readString(file.toPath());

// Scrittura file
Files.writeString(file.toPath(), textContent);
```

## 🆚 Confronto con i Progetti Precedenti

| Progetto | Concetto | Componenti Principali |
|----------|----------|----------------------|
| 01 - Click Counter | Event Handling | Button, Label |
| 02 - Unit Converter | Property Binding | TextField, ComboBox |
| 03 - To-Do List | ObservableList | ListView |
| 04 - Employee Table | TableView | TableView, TableColumn |
| **05 - Text Editor** | **MenuBar + FileChooser** | **MenuBar, TextArea, FileChooser** |

## 🏗️ Componenti JavaFX Utilizzati

### BorderPane
Layout manager con 5 regioni (Top, Bottom, Left, Right, Center):
```java
BorderPane root = new BorderPane();
root.setTop(menuBar);   // Menu in alto
root.setCenter(textArea); // TextArea al centro
```

### TextArea
Campo di testo multi-linea:
```java
TextArea textArea = new TextArea();
textArea.cut();    // Taglia
textArea.copy();   // Copia
textArea.paste();  // Incolla
textArea.selectAll(); // Seleziona tutto
```

### MenuItem con Action
```java
MenuItem openItem = new MenuItem("Apri...");
openItem.setOnAction(e -> openFile());
```

## 🎨 Funzionalità Implementate

1. **Nuovo File**: Pulisce il TextArea (con conferma se ci sono modifiche)
2. **Apri File**: FileChooser per selezionare file .txt
3. **Salva**: Salva il file corrente
4. **Salva con Nome**: FileChooser per salvare con nuovo nome
5. **Esci**: Conferma salvataggio prima di chiudere
6. **Operazioni Edit**: Taglia, Copia, Incolla, Seleziona Tutto
7. **Info**: Dialog con informazioni sull'app
8. **Shortcut Invio**: TextArea non supporta Invio per azione (multi-line)

## 📦 Compilazione ed Esecuzione

```bash
mvn clean compile
mvn javafx:run
```

## 🎓 Concetti Appresi

1. **MenuBar**: Creazione menu strutturati
2. **Menu e MenuItem**: Organizzazione voci di menu
3. **SeparatorMenuItem**: Separatori visivi tra gruppi
4. **FileChooser**: Dialog nativo per selezione file
5. **ExtensionFilter**: Filtri per tipi di file
6. **showOpenDialog**: Apri file esistente
7. **showSaveDialog**: Salva con nome
8. **Files (NIO.2)**: Lettura/scrittura file moderna
9. **BorderPane**: Layout con regioni predefinite
10. **Alert Confirmation**: Dialog di conferma azioni


## Changelog
| Data | Descrizione |
|------|-------------|
| 2026-11-23 | Aggiunta sezione changelog |


# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.