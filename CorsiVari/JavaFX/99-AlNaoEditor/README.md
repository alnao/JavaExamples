# AlNao Editor

Editor di testo multi-file realizzato con JavaFX.

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

## Compilazione ed esecuzione

```bash
# Compila il progetto
mvn clean compile

# Esegui l'applicazione
mvn javafx:run
```

## Requisiti

- Java 17 o superiore
- Maven
- JavaFX 21.0.1

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
