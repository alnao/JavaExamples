# AWS Lambda + DynamoDB

Funzioni **AWS Lambda** in Java realizzate durante un corso, quasi tutte basate su **Spring Cloud Function** con adapter AWS e tabelle **DynamoDB** (clienti e carte fedeltà con bollini). Progetto *molto datato*, mantenuto per *nostalgia*: dipendenze e versioni non aggiornate.

| Funzione | Descrizione |
|---|---|
| [hello](hello/) | Lambda minimale di saluto con template SAM |
| [jwt-authsrv](jwt-authsrv/) | Generazione del token JWT |
| [auth-user](auth-user/) | Authorizer custom API Gateway che valida il JWT |
| [insert-user](insert-user/) | Inserimento cliente su DynamoDB |
| [selectbol-user](selectbol-user/) | Ricerca clienti per bollini |
| [selectcode-user](selectcode-user/) | Ricerca cliente per codice |
| [selectname-user](selectname-user/) | Ricerca clienti per nome |
| [update-bollini](update-bollini/) | Aggiornamento del monte bollini da file su S3 |

> ⚠️ Comandi che agiscono su un account AWS reale: possono creare risorse e generare costi.

## Tecnologie
- Java 8
- Spring Boot 2
- Spring Cloud Function
- AWS Lambda
- DynamoDB
- S3

## Credenziali e tag
- Le Access Key non sono più scritte negli `application.properties`/`.yml`: le funzioni usano la default credential provider chain e la variabile `AWS_REGION` (vedi `.env` nella root del repository).
- I template SAM/CloudFormation (`hello`, `insert-user`) applicano i tag standard con `Descrizione = CorsiVari.AwsLambdaDynamoExamples.<funzione>`.


## Changelog
| Data | Descrizione |
|------|-------------|
| 2026-11-23 | Creato README del progetto |
| 2026-11-23 | Tag standard nei template, chiavi AWS spostate nel .env |


# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.
