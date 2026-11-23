# AWS

Esempi Java per **Amazon Web Services**.

| Cartella | Descrizione |
|---|---|
| [CDK](CDK/) | Stack AWS CDK scritti in Java (*in revisione*) |
| [Lambda](Lambda/) | Funzioni AWS Lambda in Java sviluppate e rilasciate con `sam` |
| [ManagerFx](ManagerFx/) | Console JavaFX per consultare e gestire le risorse di un account AWS |
| [Sdk](Sdk/) | Esempi d'uso dell'AWS SDK for Java (*in revisione*) |

> ⚠️ Comandi che agiscono su un account AWS reale: possono creare risorse e generare costi.

## Convenzione dei tag
Ogni risorsa creata su AWS che supporta i tag riporta i tag standard dell'esempio. `<Descrizione>` è il percorso della cartella dopo il livello `AWS`, con `.` tra le sottocartelle (`AWS/CDK/CDK01bucketS3` → `CDK.CDK01bucketS3`); per le cartelle fuori da `AWS` si mantiene il primo livello (`SpringBootOldVersions/Esempio04dbNoSql` → `SpringBootOldVersions.Esempio04dbNoSql`).

| Tag | Valore |
|---|---|
| Name | `aws-j-<Descrizione>` (negli stack CDK: nome della singola risorsa, `aws-j-cdkXX-<servizio>`) |
| Environment | `dev` |
| Project | `Development.aws.j-<Descrizione>` |
| Owner | `AlNao` |
| CostCenter | `dev` |
| ManagedBy | `Manual` / `Terraform` / `CloudFormation` / `Sh` / `CDK` in base allo strumento che crea la risorsa |

## Tecnologie
- AWS SDK for Java
- AWS CDK
- AWS SAM
- JavaFX

## Changelog
| Data | Descrizione |
|------|-------------|
| 2026-11-23 | Creato README del progetto |


# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.
