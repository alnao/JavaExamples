# Unit Converter - Progetto JavaFX 02

## Descrizione
Convertitore di valuta che aggiorna automaticamente il valore convertito mentre digiti.

Introduce il concetto fondamentale di **Property Binding** in JavaFX.

## Concetti Chiave

### 1. Property Binding
In JavaFX, le proprietà possono essere "legate" (bound) tra loro. Quando una cambia, l'altra si aggiorna automaticamente:

```java
DoubleProperty value1 = new SimpleDoubleProperty(0);
DoubleProperty value2 = new SimpleDoubleProperty(0);

// Binding: value2 = value1 * 1.10
value2.bind(value1.multiply(1.10));
```

**Vantaggi**:
- Nessun bisogno di `setText()` manuale
- Sincronizzazione automatica
- Codice più pulito e dichiarativo

### 2. Binding Bidirezionale
```java
Bindings.bindBidirectional(
    textField.textProperty(), 
    doubleProperty, 
    new NumberStringConverter()
);
```

- **Bidirezionale**: Le modifiche si propagano in entrambe le direzioni
- **Converter**: Trasforma String ↔ Number automaticamente

### 3. Property Types
JavaFX offre diverse property:
- `SimpleDoubleProperty` - per numeri decimali
- `SimpleStringProperty` - per stringhe
- `SimpleBooleanProperty` - per booleani
- `SimpleIntegerProperty` - per interi

### 4. GridPane Layout
```java
GridPane grid = new GridPane();
grid.add(label, column, row);
```

Perfetto per form con label e campi allineati.

## Struttura del Progetto

```
02-UnitConverter/
├── pom.xml
├── README.md
└── src/main/java/
    └── it/alnao/javafx/unitconverter/
        └── UnitConverterApp.java
```

## Come Eseguire

```bash
cd CorsiVari/JavaFX/02-UnitConverter
mvn clean compile
mvn javafx:run
```

## Funzionalità

- **Conversioni supportate**:
  - EUR → USD
  - USD → EUR
  - EUR → GBP
  - GBP → EUR
- **Aggiornamento automatico**: Digita nel campo e il convertito si aggiorna in tempo reale
- **Tassi di cambio fissi**: (Per semplicità, in un'app reale useresti API esterne)

## Differenza con Progetto 01

| Progetto 01 | Progetto 02 |
|-------------|-------------|
| Event Handler manuale | Property Binding automatico |
| `label.setText()` esplicito | Binding si aggiorna da solo |
| Imperativo | Dichiarativo |

## Cosa Impari

1. **Property Binding** unidirezionale e bidirezionale
2. **DoubleProperty** e altre property types
3. **NumberStringConverter** per convertire Text ↔ Number
4. **GridPane** per layout a griglia
5. **ComboBox** per selezione multipla




# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.