# Progetto 07 - Interactive Dashboard

## 📊 Descrizione
Dashboard interattiva con diversi tipi di grafici (PieChart, LineChart, BarChart) per visualizzare dati statistici aziendali.

## 🎯 Concetto Principale: Charts API

### Cos'è Charts API?
JavaFX fornisce componenti pronti per creare grafici professionali senza librerie esterne: **PieChart**, **LineChart**, **BarChart**, **AreaChart**, **ScatterChart**, **BubbleChart**.

### Tipi di Grafici Implementati

#### 1. PieChart
Grafico a torta per percentuali:
```java
PieChart pieChart = new PieChart(FXCollections.observableArrayList(
    new PieChart.Data("Categoria A", 35),
    new PieChart.Data("Categoria B", 25)
));
```

#### 2. LineChart
Grafico a linee per trend temporali:
```java
CategoryAxis xAxis = new CategoryAxis();
NumberAxis yAxis = new NumberAxis();
LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

XYChart.Series<String, Number> series = new XYChart.Series<>();
series.setName("2024");
series.getData().add(new XYChart.Data<>("Gen", 15000));
lineChart.getData().add(series);
```

#### 3. BarChart
Grafico a barre per confronti:
```java
BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
XYChart.Series<String, Number> series = new XYChart.Series<>();
series.setName("Nord Italia");
series.getData().add(new XYChart.Data<>("Q1", 85000));
barChart.getData().add(series);
```

## 🆚 Confronto con i Progetti Precedenti

| Progetto | Concetto | Visualizzazione Dati |
|----------|----------|---------------------|
| 03 - To-Do List | ObservableList | Lista testuale |
| 04 - Employee Table | TableView | Tabella |
| **07 - Dashboard** | **Charts API** | **Grafici statistici** |

### Evoluzione Visualizzazione Dati
- **Progetto 03**: Lista semplice (ListView)
- **Progetto 04**: Tabella strutturata (TableView)
- **Progetto 07**: Grafici professionali (Charts)

## 🏗️ Componenti JavaFX Utilizzati

### PieChart
```java
PieChart pieChart = new PieChart();
pieChart.setLegendVisible(true);
pieChart.setLabelsVisible(true);
pieChart.getData().add(new PieChart.Data("Label", value));
```

### XYChart (LineChart, BarChart)
```java
// Assi
CategoryAxis xAxis = new CategoryAxis();
NumberAxis yAxis = new NumberAxis();

// Serie di dati
XYChart.Series<String, Number> series = new XYChart.Series<>();
series.setName("Serie 1");
series.getData().add(new XYChart.Data<>("X", yValue));

// Grafico
LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
chart.getData().add(series);
```

### GridPane per Layout Grafici
```java
GridPane grid = new GridPane();
grid.add(chart1, 0, 0);  // colonna 0, riga 0
grid.add(chart2, 1, 0);  // colonna 1, riga 0
grid.add(chart3, 0, 1, 2, 1); // span 2 colonne
```

## 🎨 Funzionalità Implementate

1. **PieChart**: Vendite per categoria (5 categorie)
2. **LineChart**: Trend mensile 2023 vs 2024 (2 serie, 6 mesi)
3. **BarChart**: Confronto trimestrale per area geografica (3 serie, 4 trimestri)
4. **Card Layout**: Ogni grafico in una card con titolo
5. **GridPane**: Layout responsive a griglia (2 colonne)

## 📊 Dati Visualizzati

### PieChart - Vendite per Categoria
- Elettronica: 35%
- Abbigliamento: 25%
- Alimentari: 20%
- Libri: 12%
- Altri: 8%

### LineChart - Trend Mensile
- 2023: Gen (15k) → Giu (28k)
- 2024: Gen (18k) → Giu (35k)

### BarChart - Confronto Trimestrale
- Nord Italia: Q1 (85k) → Q4 (105k)
- Centro Italia: Q1 (65k) → Q4 (78k)
- Sud Italia: Q1 (48k) → Q4 (61k)

## 📦 Compilazione ed Esecuzione

```bash
mvn clean compile
mvn javafx:run
```

## 🎓 Concetti Appresi

1. **PieChart**: Grafico a torta
2. **PieChart.Data**: Dati per fette della torta
3. **LineChart**: Grafico a linee
4. **BarChart**: Grafico a barre
5. **XYChart.Series**: Serie di dati per grafici XY
6. **XYChart.Data**: Singolo punto dati (x, y)
7. **CategoryAxis**: Asse categorie (stringhe)
8. **NumberAxis**: Asse numerico
9. **GridPane**: Layout a griglia
10. **setName()**: Etichetta per legenda grafici



# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.