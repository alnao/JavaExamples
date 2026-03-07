# Progetto 08 - Async File Downloader

## ⬇️ Descrizione
Simulatore di download file con Task asincroni, ProgressBar e gestione concorrenza per non bloccare l'interfaccia utente.

## 🎯 Concetto Principale: Task e Concurrency

### Cos'è un Task?
Un **Task** è una classe JavaFX per eseguire operazioni pesanti in background senza bloccare il thread UI (JavaFX Application Thread).

### Problema del Thread UI
```java
// ❌ SBAGLIATO - Blocca l'UI per 5 secondi
button.setOnAction(e -> {
    Thread.sleep(5000); // UI CONGELATA!
    label.setText("Fatto");
});

// ✅ CORRETTO - Task asincrono
button.setOnAction(e -> {
    Task<Void> task = new Task<>() {
        protected Void call() {
            Thread.sleep(5000); // Non blocca l'UI
            return null;
        }
    };
    new Thread(task).start();
});
```

### Platform.runLater()
Per aggiornare la UI da un thread non-UI:
```java
Task<Void> task = new Task<>() {
    protected Void call() {
        // Thread separato
        Thread.sleep(1000);
        Platform.runLater(() -> {
            label.setText("Aggiornato!"); // Thread UI
        });
        return null;
    }
};
```

### Task Lifecycle
```
Task Lifecycle:
1. call()       → Esecuzione operazione (thread separato)
2. updateProgress() → Aggiorna progresso
3. succeeded()  → Successo (thread UI)
4. failed()     → Errore (thread UI)
5. cancelled()  → Cancellato (thread UI)
```

## 🆚 Confronto con i Progetti Precedenti

| Progetto | Threading | UI Responsiveness |
|----------|-----------|------------------|
| 01-07 | Single-threaded | Sempre responsiva (operazioni veloci) |
| **08 - File Downloader** | **Multi-threaded (Task)** | **Responsiva durante operazioni pesanti** |

### Quando Usare Task
- ❌ **NON serve**: Operazioni istantanee (< 100ms)
- ✅ **NECESSARIO**: 
  - Download file
  - Calcoli pesanti
  - Accesso database
  - Chiamate REST API

## 🏗️ Componenti JavaFX Utilizzati

### Task<V>
```java
Task<String> task = new Task<String>() {
    @Override
    protected String call() throws Exception {
        // Lavoro pesante qui
        updateProgress(50, 100); // Aggiorna progresso
        return "Risultato";
    }
    
    @Override
    protected void succeeded() {
        String result = getValue();
        label.setText(result); // Aggiorna UI
    }
};
```

### ProgressBar
```java
ProgressBar progressBar = new ProgressBar();
progressBar.progressProperty().bind(task.progressProperty());
// Si aggiorna automaticamente con il Task!
```

### Platform.runLater()
```java
Platform.runLater(() -> {
    // Codice eseguito nel thread UI
    label.setText("Aggiornato!");
});
```

### Thread Daemon
```java
Thread thread = new Thread(task);
thread.setDaemon(true); // Termina con l'app
thread.start();
```

## 🎨 Funzionalità Implementate

1. **Simulazione Download**: Task con 100 step da 50ms (5 secondi totali)
2. **ProgressBar**: Binding automatico a `task.progressProperty()`
3. **Status Label**: Aggiornato con Platform.runLater()
4. **Succeeded Handler**: Messaggio di successo al termine
5. **Failed Handler**: Gestione errori
6. **Cancelled Handler**: Gestione cancellazione
7. **Button Disable**: Disabilita durante download

## 📦 Compilazione ed Esecuzione

```bash
mvn clean compile
mvn javafx:run
```

## 🎓 Concetti Appresi

1. **Task<V>**: Esecuzione asincrona operazioni pesanti
2. **call()**: Metodo eseguito in thread separato
3. **updateProgress()**: Aggiorna progresso del Task
4. **succeeded()/failed()/cancelled()**: Handler lifecycle
5. **Platform.runLater()**: Esegue codice nel thread UI
6. **ProgressBar Binding**: `progressProperty().bind()`
7. **Thread Daemon**: Thread che termina con l'app
8. **JavaFX Application Thread**: Thread unico per UI
9. **Multi-threading in JavaFX**: Best practice
10. **UI Responsiveness**: Mantenere UI non bloccata



# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.