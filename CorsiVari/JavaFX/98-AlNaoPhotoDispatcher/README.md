# AlNao Photo Dispatcher

Applicazione JavaFX per organizzare rapidamente le foto spostandole da una cartella sorgente a varie sottocartelle di destinazione.

## Funzionalità

- **Visualizzazione Foto**: Mostra le immagini dalla cartella sorgente una alla volta.
- **Navigazione**: Pulsanti per passare all'immagine successiva o precedente.
- **Spostamento Rapido**: Elenco dinamico di tutte le sottocartelle della destinazione principale. Un click sposta la foto corrente.
- **Configurazione**: File di configurazione persistente per salvare i percorsi.

## Configurazione

L'applicazione salva le impostazioni in:
```
~/.alnaoPhotoDispatcher/config.properties
```

Default:
- Sorgente: `/home/alnao/images/source/`
- Destinazione: `/home/alnao/images/destination/`

## Compilazione ed Esecuzione

```bash
# Compila
mvn clean compile

# Esegui
mvn javafx:run
```



# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.
