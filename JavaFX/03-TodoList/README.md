# Progetto 03 - To-Do List App

## 📝 Descrizione
Applicazione JavaFX per gestire una lista di task con funzionalità complete di aggiunta, rimozione, completamento e conteggio.

## 🎯 Concetto Principale: ObservableList

### Cos'è un'ObservableList?
Un'**ObservableList** è una collezione speciale di JavaFX che notifica automaticamente tutti i suoi observer quando il contenuto cambia (aggiunta, rimozione, modifica di elementi).

### Vantaggi Rispetto a List Normale
```java
// List normale - ListView NON si aggiorna automaticamente
List<String> normalList = new ArrayList<>();
normalList.add("Nuovo task");
listView.refresh(); // NECESSARIO refresh manuale ❌

// ObservableList - ListView si aggiorna AUTOMATICAMENTE
ObservableList<String> observableList = FXCollections.observableArrayList();
observableList.add("Nuovo task"); // Aggiornamento automatico! ✅
```

### Come Funziona l'Aggiornamento Automatico
Quando colleghi un'ObservableList a un controllo UI come ListView:
```java
ObservableList<String> taskList = FXCollections.observableArrayList();
ListView<String> listView = new ListView<>(taskList);
```

Qualsiasi modifica alla lista viene propagata automaticamente alla UI:
- `taskList.add("Task")` → ListView mostra immediatamente il nuovo elemento
- `taskList.remove(index)` → ListView rimuove immediatamente l'elemento
- `taskList.set(index, "Nuovo valore")` → ListView aggiorna immediatamente

### ListChangeListener
Puoi anche ascoltare i cambiamenti per eseguire logica personalizzata:
```java
taskList.addListener((ListChangeListener.Change<? extends String> c) -> {
    System.out.println("La lista è cambiata!");
    updateCounter(); // Aggiorna contatore task
});
```

## 🆚 Confronto con i Progetti Precedenti

| Progetto | Concetto | Meccanismo di Aggiornamento |
|----------|----------|----------------------------|
| 01 - Click Counter | Event Handling | Manuale con `setText()` |
| 02 - Unit Converter | Property Binding | Automatico tramite binding |
| **03 - To-Do List** | **ObservableList** | **Automatico tramite Observer Pattern** |

### Evoluzione della Gestione Dati
1. **Progetto 01**: Aggiornamento manuale esplicito
   ```java
   button.setOnAction(e -> label.setText(String.valueOf(++counter)));
   ```

2. **Progetto 02**: Binding bidirezionale tra proprietà
   ```java
   Bindings.bindBidirectional(textField.textProperty(), doubleProperty, converter);
   ```

3. **Progetto 03**: Collezione osservabile che notifica cambiamenti
   ```java
   taskList.add("Task"); // UI si aggiorna automaticamente!
   ```

## 🏗️ Componenti JavaFX Utilizzati

### ListView
Controllo per visualizzare liste scrollabili di elementi:
```java
ListView<String> listView = new ListView<>(observableList);
listView.getSelectionModel().getSelectedIndex(); // Ottiene indice selezionato
```

### TextField con Azione
```java
textField.setOnAction(e -> addTask()); // Invio aggiunge task
```

### Alert e Confirmation Dialog
```java
Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
alert.showAndWait().ifPresent(response -> {
    if (response == ButtonType.OK) {
        // Azione confermata
    }
});
```

## 🎨 Funzionalità Implementate

1. **Aggiunta Task**: TextField + pulsante "Aggiungi" (anche con tasto Invio)
2. **Rimozione Task**: Seleziona e clicca "Rimuovi"
3. **Completa Task**: Aggiunge check mark (✓) al task selezionato
4. **Cancella Tutto**: Dialog di conferma prima di svuotare la lista
5. **Contatore Dinamico**: Aggiornato automaticamente tramite listener
   - Task totali
   - Task completati (con ✓)
   - Task da fare

## 📦 Compilazione ed Esecuzione

```bash
# Compila il progetto
mvn clean compile

# Esegui l'applicazione
mvn javafx:run
```

## 🎓 Concetti Appresi

1. **ObservableList**: Collezioni reattive che notificano cambiamenti
2. **FXCollections**: Factory per creare ObservableList e ObservableMap
3. **ListView**: Controllo per visualizzare liste scrollabili
4. **ListChangeListener**: Observer per reagire a modifiche nella lista
5. **SelectionModel**: Gestione della selezione in ListView
6. **Alert Dialogs**: Dialog standard di conferma/avviso
7. **Stream API**: Filtraggio e conteggio task completati
8. **HBox Layout**: Organizzazione orizzontale con `setHgrow()`



# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.