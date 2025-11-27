# Click Counter - Progetto JavaFX 01

## Descrizione
Il primo progetto della serie JavaFX: un semplice contatore che incrementa ogni volta che si clicca sul pulsante.

È l'evoluzione del classico "Hello World", introducendo i concetti fondamentali di **Event Handling** in JavaFX.

## Concetti Chiave

### 1. Event Handling
Il cuore di qualsiasi applicazione interattiva è la gestione degli eventi. In JavaFX:

```java
button.setOnAction(e -> {
    // Codice eseguito quando il pulsante viene cliccato
    counter++;
    label.setText(String.valueOf(counter));
});
```

- **setOnAction()**: Collega un'azione al click del pulsante
- **Lambda Expression** (`e -> { ... }`): Sintassi moderna per definire il comportamento
- **Event Parameter** (`e`): Contiene informazioni sull'evento (opzionale qui)

### 2. Layout di Base (VBox)
```java
VBox root = new VBox(20); // 20px di spacing tra gli elementi
root.setAlignment(Pos.CENTER);
root.getChildren().addAll(label, button);
```

- **VBox**: Layout verticale, gli elementi sono impilati dall'alto verso il basso
- **Spacing**: Distanza automatica tra gli elementi
- **Alignment**: Posizionamento (CENTER, TOP_LEFT, etc.)

### 3. Stile Bootstrap-like
I colori e le dimensioni seguono la palette di Bootstrap 5:
- `#0d6efd` - Blu primario (btn-primary)
- `#6c757d` - Grigio secondario (btn-secondary)
- `#f8f9fa` - Grigio chiaro (background)

## Struttura del Progetto

```
01-ClickCounter/
├── pom.xml                          # Configurazione Maven
├── README.md                        # Questa documentazione
└── src/main/java/
    └── it/alnao/javafx/clickcounter/
        └── ClickCounterApp.java     # Classe principale
```

## Come Eseguire

### 1. Compilare
```bash
cd CorsiVari/JavaFX/01-ClickCounter
mvn clean compile
```

### 2. Eseguire
```bash
mvn javafx:run
```

### 3. Creare JAR eseguibile
```bash
mvn clean package
```

## Funzionalità

- **Incrementa**: Click sul pulsante blu per aumentare il contatore
- **Reset**: Click sul pulsante grigio per azzerare il contatore
- **Hover Effect**: Il pulsante cambia colore al passaggio del mouse

## Cosa Impari

1. **Creare una finestra JavaFX** con `Stage` e `Scene`
2. **Gestire eventi** con lambda expressions
3. **Usare VBox** per layout verticali
4. **Applicare stili inline** (in futuro useremo CSS esterni)
5. **Interazione base** utente-interfaccia



# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.