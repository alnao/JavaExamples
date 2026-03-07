# AlNao Editor

Editor di testo multi-file realizzato con JavaFX.

## Compilazione ed esecuzione

```bash
# Compila il progetto
mvn clean compile

# Esegui l'applicazione
mvn javafx:run
```

### Requisiti

- Java 17 o superiore
- Maven
- JavaFX 21.0.1


## Funzionalità

### Menu File
- **Nuovo**: Crea un nuovo file vuoto
- **Apri**: Apre un file esistente (se già aperto, passa a quel file)
- **Apri tutti i file di una cartella**: Apre tutti i file di testo (txt, java, xml, md, json, properties, yml, yaml) da una cartella selezionata
- **Chiudi file corrente**: Chiude il file corrente (con conferma se modificato)
- **Salva**: Salva il file corrente
- **Salva con nome**: Salva il file corrente con un nuovo nome
- **Salva tutti**: Salva tutti i file aperti modificati
- **Esci**: Chiude l'applicazione (con conferma se ci sono file modificati)

### Menu Modifica
- **Taglia**, **Copia**, **Incolla**: Operazioni standard di editing
- **Seleziona tutto**: Seleziona tutto il testo

### Menu Aiuto
- **Info**: Informazioni sull'applicazione

### Gestione Multi-File
- **Bottone "Salva tutti"**: Posizionato nella barra del menu, permette di salvare rapidamente tutti i file modificati
- **ComboBox di selezione file**: Sotto il menu, una tendina mostra tutti i file aperti **in ordine alfabetico**
  - Formato visualizzato: `NomeFile (path/completo) *` (l'asterisco indica modifiche non salvate)
  - I file sono ordinati alfabeticamente per nome file (case-insensitive)
- **Apertura cartella**: Possibilità di aprire tutti i file di testo da una cartella in un colpo solo
  - Supporta i formati: .txt, .java, .xml, .md, .json, .properties, .yml, .yaml
  - I file già aperti vengono automaticamente saltati
- **Persistenza dei file**: I file aperti vengono salvati automaticamente alla chiusura e ricaricati all'apertura
- **Nessun margine**: Il ComboBox è posizionato direttamente sotto il menu, senza spazi

## Configurazione

L'applicazione salva le sue impostazioni nella directory:
```
~/.alnaoEditor/
```

### File di configurazione

#### window.txt
Contiene le impostazioni della finestra:
- `window.width`: larghezza della finestra
- `window.height`: altezza della finestra
- `window.x`: posizione X della finestra
- `window.y`: posizione Y della finestra

#### fileList.txt
Contiene la lista dei file aperti (uno per riga):
```
/home/user/documento1.txt
/home/user/progetti/readme.md
/home/user/notes.txt
```


## Note d'uso

- I file modificati sono contrassegnati con un asterisco (*) nel selettore
- Il bottone verde "💾 Salva tutti" è sempre visibile nella barra del menu per un accesso rapido
- Quando si apre una cartella, vengono aperti solo i file con estensioni supportate (max profondità: 1 livello)
- **Chiusura intelligente**: Alla chiusura dell'applicazione, se ci sono file modificati non salvati, viene mostrato un dialogo con tre opzioni:
  - **Salva tutto**: Salva tutti i file modificati e chiude
  - **Chiudi senza salvare**: Chiude senza salvare le modifiche
  - **Non chiudere**: Annulla la chiusura e torna all'editor
- Alla chiusura dell'applicazione, tutti i file aperti vengono salvati nella lista
- All'apertura, l'applicazione ricarica automaticamente tutti i file dalla lista
- Se un file nella lista non esiste più, viene ignorato silenziosamente





# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.
