# 11 - Paths Book Cards

Prototipo JavaFX di un gioco carte in formato "libro aperto":
- pagina sinistra con card grande corrente;
- pagina destra con anteprima card grande e lista di card piccole selezionabili;
- icona `(i)` per mostrare i dettagli della card;
- bottone `Esegui` per rendere la card selezionata la nuova card grande a sinistra.

## Requisiti
- Java 17+
- Maven 3.8+

## Esecuzione
```bash
cd 11-PathsBookCards
mvn clean compile
mvn javafx:run
```

## Note UI
- Palette brown personalizzata definita in CSS variables.
- Hover card: zoom + ombra + glow.
- Selezione card: bordo evidenziato.
- Cambio card sinistra: animazione fade + slide.
