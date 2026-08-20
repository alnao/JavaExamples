# AlNao Photo Carousel

Applicazione JavaFX per la riproduzione automatica e fluida di caroselli fotografici a schermo intero con transizioni animate.

## Funzionalità

- **Schermo Intero Automatico**: L'applicazione si avvia direttamente in modalità fullscreen.
- **Configurazione Persistente JSON**: Salva l'elenco delle cartelle configurate e l'intervallo di tempo `S` in un file JSON dedicato (`~/.alnaoPhotoCarousel/config.json`).
- **Gestione Cartelle**: Finestra modale integrata per aggiungere o rimuovere le cartelle di immagini in qualsiasi momento.
- **Top Bar dei Controlli**:
  - Select (dropdown) per selezionare la cartella attiva.
  - Tasto **Play** e **Stop** per avviare o mettere in pausa la riproduzione.
  - Campo numerico per modificare in tempo reale l'intervallo `S` (in secondi).
  - Tasto per alternare lo schermo intero (⛶).
- **Galleria Dinamica e Responsive**:
  - Calcola dinamicamente il numero di foto visibili `N` (fino a un massimo di 4) in base alla larghezza della finestra.
  - Garantisce che ogni immagine abbia una larghezza minima di 300px o al massimo 1/4 dello schermo.
  - Preserva il rapporto d'aspetto (aspect ratio) originale di ciascuna foto.
- **Transizioni Animate**: All'attivazione del Play, ogni `S` secondi la prima foto viene rimossa con animazione di sfumatura/scorrimento ed una nuova immagine (scelta a caso tra quelle presenti nella cartella) viene aggiunta in coda.

## File di Configurazione JSON

L'applicazione salva e carica automaticamente i dati dal file:
```
~/.alnaoPhotoCarousel/config.json
```

Esempio di struttura:
```json
{
  "folders" : [
    "/home/user/Immagini/Vacanze",
    "/home/user/Immagini/Natura"
  ],
  "intervalSeconds" : 3
}
```

## Compilazione ed Esecuzione

```bash
# Entra nella cartella
cd JavaFX/96-PhotoCarousel

# Compila ed impacchetta
mvn clean package -DskipTests

# Esegui con lo script dedicato
./run.sh

# Oppure esegui tramite plugin JavaFX
mvn javafx:run
```

## Esecuzione con Icona Desktop

Puoi creare un lanciatore `.desktop` in `~/.local/share/applications/alnaoPhotoCarousel.desktop`:
```ini
[Desktop Entry]
Encoding=UTF-8
Name=AlNao Photo Carousel
Exec=/mnt/Dati4/Workspace/JavaExamples/JavaFX/96-PhotoCarousel/run.sh
Icon=/usr/share/icons/hicolor/scalable/status/weather-snow-large.svg
Terminal=false
Type=Application
Categories=Graphics;Multimedia;
```

# IA
Progetto creato con i prompt:
- Ciao, voglio che mi crei un nuovo esempio JavaFX/96-PhotoCarousel. una mini-applicazione in java fx prendendo spunto dagli altri esempi come il 98 photo dispatcher. questa applicazione ha un menu con configurazione di una lista di cartelle, crea un file json specifico dedicato per questa applicazione dove salvare la lista delle cartelle. fai in modo che quando si apre l'applicazione sia grande tutto lo schermo. In cima una select dove selezionare una cartelle tra quelle presenti nella configurazione, un tasto play e un tasto stop e un input text con S secondi (S parametro nel file di configurazione modificabile). Nel corpo deve mostrare N foto presenti nella cartella selezionata, se il tasto play è selezionato ogni S secondi la prima immagine viene rimossa e viene aggiunta come ultima una nuova immagine (selelzionata a random tra quelle presenti). se riesci una trasazione animazione tipo scorrimento fade. Le immagini sono N dove è è calcolato in base alla larghezza della finestra,  voglio che le immagini sia minimo 300px di larghezza o al massimo un quarto (quindi N max è 4). l'altezza delle immagini visualizzate è calcolata per mantenere il rapporto dell'immagine originale. ti è tutto chiaro o hai domande prima di iniziare lo sviluppo? 
- ciao togli l'animazione perchè è molto brutta
- fai in modo che se nella cartella selezionata ci sono sottocartelle, prenda le foto anche delle sottocartelle presenti
- elimina padding e margin tra le foto e dentro le card delle foto tanto non serve a nulla, inoltre aumenta la dimensione delle immaigni dopo aver calcolato il numero di immagini presenti, fai in modo che le immagini siano una sopra l'altra (con quella più a destra sia sopra) e che si sovrappongono del 10%. let's go
- fai un flag nello header di default disabilitato, se attivo fai la logica del 10% in overflow, altrimenti lascia le foto grandi originali come erano prima dell'ultimo messaggio con il margin/padding
- ora aggiunti un altro flag "centrale grande" , default attivo, se attivo devono essere massimo 3, se dovrebbe essere 4 ma sono 3, quella centrale deve essere larga il dobbio mantenendo sempre la propozione dell'altezza se sborda in altezza non importa. let's go
- fai una modifica: l'altezza massima della immagine centrale non può superare l'altezza del corpo della applicazione , se la supera rimpicciolisci l'immagine centrale 
- non hai risolto il problema: l'immagine centrale è ancora troppo alta, voglio che sia alta al massimo l'altezza della finestra
- torniamo indietro, cioè nel caso di centrale grande può sbordare dai borti ma deve essere centata verticalmente cioè deve sbordare della stessa % sia sopra che sotto (così da essere centrata) ce la fai?
- torniamo indietro, cioè nel caso di centrale grande può sbordare dai borti ma deve essere centata verticalmente cioè deve sbordare della stessa % sia sopra che sotto (così da essere centrata) ce la fai? fai un flag "permetti sbordo" che abilita questa cosa oppure imposta altezza massima come ora





# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.
