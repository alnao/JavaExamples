# Progetto 06 - Drawing Canvas

## 🎨 Descrizione
Applicazione di disegno con canvas per disegnare liberamente usando penna, gomma e forme geometriche con diversi colori.

## 🎯 Concetto Principale: Canvas e GraphicsContext

### Cos'è un Canvas?
Un **Canvas** è una superficie bitmap per disegnare contenuti 2D usando un'API imperativa (GraphicsContext), diversa dall'approccio Scene Graph di JavaFX.

### Canvas vs Scene Graph
```
Scene Graph (Button, Label, etc.):
- Nodi trattenuti in memoria
- Modifica diretta dei nodi
- Più pesante per molti elementi
- Interattività automatica

Canvas:
- Pixel buffer "immediato"
- Disegno imperativo con GraphicsContext
- Più leggero per molti elementi
- Interattività manuale con coordinate
```

### GraphicsContext
L'API per disegnare sul Canvas:
```java
Canvas canvas = new Canvas(800, 600);
GraphicsContext gc = canvas.getGraphicsContext2D();

// Disegno forme
gc.setFill(Color.BLUE);
gc.fillRect(x, y, width, height);
gc.fillOval(x, y, width, height);

// Disegno linee
gc.setStroke(Color.RED);
gc.setLineWidth(5);
gc.strokeLine(x1, y1, x2, y2);

// Disegno path
gc.beginPath();
gc.moveTo(x, y);
gc.lineTo(x2, y2);
gc.stroke();
```

## 🆚 Confronto con i Progetti Precedenti

| Progetto | Concetto | Approccio Rendering |
|----------|----------|-------------------|
| 01-05 | Vari | Scene Graph (nodi trattenuti) |
| **06 - Drawing Canvas** | **Canvas** | **Immediate Mode (buffer pixel)** |

### Scene Graph vs Canvas
- **Scene Graph**: Ideale per UI tradizionali (bottoni, form, etc.)
- **Canvas**: Ideale per grafici, giochi, visualizzazioni custom

## 🏗️ Componenti JavaFX Utilizzati

### Canvas
```java
Canvas canvas = new Canvas(width, height);
GraphicsContext gc = canvas.getGraphicsContext2D();
```

### Mouse Events su Canvas
```java
canvas.setOnMousePressed(e -> {
    double x = e.getX();
    double y = e.getY();
    // Inizia disegno
});

canvas.setOnMouseDragged(e -> {
    // Continua disegno
});

canvas.setOnMouseReleased(e -> {
    // Fine disegno
});
```

### ColorPicker
Controllo per selezione colore:
```java
ColorPicker colorPicker = new ColorPicker(Color.BLUE);
colorPicker.setOnAction(e -> currentColor = colorPicker.getValue());
```

### Slider
Controllo per valori numerici:
```java
Slider sizeSlider = new Slider(1, 20, 5);
sizeSlider.setShowTickMarks(true);
sizeSlider.valueProperty().addListener((obs, old, newVal) -> 
    brushSize = newVal.doubleValue()
);
```

## 🎨 Funzionalità Implementate

1. **Penna**: Disegno libero con drag del mouse
2. **Gomma**: Cancella con `clearRect()`
3. **Cerchio**: Click per disegnare cerchio fisso
4. **Rettangolo**: Click per disegnare rettangolo fisso
5. **ColorPicker**: Selezione colore personalizzato
6. **Slider Dimensione**: Regola spessore pennello (1-20px)
7. **Cancella Tutto**: Ripulisce il canvas completamente

## 📦 Compilazione ed Esecuzione

```bash
mvn clean compile
mvn javafx:run
```

## 🎓 Concetti Appresi

1. **Canvas**: Superficie bitmap per disegno 2D
2. **GraphicsContext**: API imperativa per disegno
3. **beginPath/moveTo/lineTo/stroke**: Disegno path
4. **fillRect/fillOval**: Forme piene
5. **clearRect**: Cancellazione area
6. **setStroke/setFill/setLineWidth**: Stili grafici
7. **ColorPicker**: Selezione colore interattiva
8. **Slider**: Controllo valori numerici
9. **Mouse Events su Canvas**: Cattura coordinate
10. **Immediate Mode Rendering**: Disegno diretto su buffer pixel


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