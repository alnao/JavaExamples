# LibGDX 01 base - Mini Gioco

Mini progetto LibGDX con un gioco semplice dove un omino si muove a destra o sinistra.

Creato con il prompt IA:
> Ciao, nella cartella LibGDX creami un mini progetto "01-base" dove all'utente viene mostrato un "start game" bottone , allo start compare un omino 2d con due possiblità "vai a destra" e "vai a sinistra" con due bottoni, al click l'omino si sposta verso quella direzione e il gioco termina con messaggio di conferma "hai completato le tue scelte"


## Descrizione

Il gioco è composto da tre schermate:

1. **Schermata iniziale**: Mostra un bottone "Start Game"
2. **Schermata di gioco**: Mostra un omino 2D al centro dello schermo con due bottoni:
   - "Vai a sinistra" - sposta l'omino verso sinistra
   - "Vai a destra" - sposta l'omino verso destra
3. **Schermata finale**: Dopo aver scelto una direzione, appare il messaggio "Hai completato le tue scelte!" con possibilità di rigiocare

## Struttura del Progetto

```
01-base/
├── pom.xml                         # Configurazione Maven con dipendenze LibGDX
├── README.md                       # Questo file
└── src/
    └── main/
        ├── java/
        │   └── it/alnao/libgdx/
        │       ├── BaseGame.java           # Classe principale del gioco
        │       └── DesktopLauncher.java    # Launcher desktop
        └── resources/
            ├── uiskin.json         # Skin UI (non utilizzato, skin creata programmaticamente)
            └── default.fnt         # Font (non utilizzato)
```

## Requisiti

- Java 11 o superiore
- Maven 3.x

## Come Compilare

```bash
cd ../JavaExamples/CorsiVari/LibGDX/01-base
mvn clean package
```

## Come Eseguire

```bash
mvn exec:java
```

Oppure:

```bash
mvn clean compile exec:java
```

## Caratteristiche Tecniche

- **Framework**: LibGDX 1.12.1
- **Backend**: LWJGL3
- **Risoluzione finestra**: 800x600 pixel
- **FPS**: 60
- **Linguaggio**: Java 11

## Funzionalità Implementate

- ✅ Schermata iniziale con bottone "Start Game"
- ✅ Omino 2D disegnato con forme geometriche (cerchio per la testa, rettangoli per corpo, braccia e gambe)
- ✅ Due bottoni per il movimento ("Vai a sinistra" e "Vai a destra")
- ✅ Animazione del movimento dell'omino
- ✅ Schermata finale con messaggio di conferma
- ✅ Possibilità di rigiocare
- ✅ Skin UI creata programmaticamente (non richiede file immagine esterni)

## Note

Il progetto usa LibGDX con il backend LWJGL3 per il rendering grafico. La UI è gestita con il sistema Scene2D di LibGDX, mentre l'omino è disegnato usando ShapeRenderer per creare forme geometriche semplici.

La skin per i bottoni e le label è creata programmaticamente nel codice, non richiede file esterni come texture o font bitmap.


# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.
