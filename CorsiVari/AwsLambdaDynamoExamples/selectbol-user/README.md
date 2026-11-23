# selectbol-user

Funzione Lambda che cerca i clienti su **DynamoDB** filtrando per numero di bollini.

## Tecnologie
- Java 8
- Spring Cloud Function
- DynamoDB

## Esecuzione
```bash
# compila il jar da caricare su AWS Lambda
mvn clean package
```

> ⚠️ Comandi che agiscono su un account AWS reale: possono creare risorse e generare costi. Il rilascio va eseguito solo dopo conferma esplicita.

## Credenziali AWS
Nessuna chiave nei file del progetto: il client DynamoDB usa la *default credential provider chain* (in locale le variabili del `.env` nella root del repository, su AWS Lambda il ruolo di esecuzione) e la regione da `AWS_REGION`.
```bash
set -a; source ../../../.env; set +a
```


## Changelog
| Data | Descrizione |
|------|-------------|
| 2026-11-23 | Creato README del progetto |
| 2026-11-23 | Chiavi AWS rimosse, credenziali dal .env generale |


# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.
