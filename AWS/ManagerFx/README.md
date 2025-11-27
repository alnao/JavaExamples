# AWS Manager JavaFX

Console AWS sviluppata con JavaFX e AWS SDK per Java che permette di visualizzare e gestire risorse AWS.

## Caratteristiche

- **Dashboard grafica** con menu laterale fisso per navigare tra i servizi AWS
- **Interfaccia intuitiva** con sidebar menu per accesso rapido ai servizi
- **Supporto multi-regione e multi-profilo** AWS
- **Visualizzazione di diverse risorse AWS**:
  - VPC e Subnets
  - Istanze EC2
  - Bucket S3
  - Istanze RDS
  - Security Groups
  - Stack CloudFormation
  - Funzioni Lambda
  - Distribuzioni CloudFront
  - Tabelle DynamoDB
  - Regole EventBridge
  - Topic SNS
  - Code SQS
  - Parametri SSM
  - Step Functions
  - API Gateway
  - **IAM (Groups, Users, Roles)**

- **Operazioni sulle istanze EC2** (in sviluppo):
  - Start
  - Stop
  - Terminate

## Prerequisiti

- **Java 17** o superiore
- **Maven 3.8+**
- **Credenziali AWS** configurate in `~/.aws/credentials`
- **JavaFX** (gestito automaticamente da Maven)

## Struttura del Progetto

```
AWS/ManagerFx/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── it/alnao/aws/managerfx/
        │       ├── Main.java                    # Classe principale
        │       ├── controller/
        │       │   └── MainController.java      # Controller JavaFX
        │       ├── service/
        │       │   ├── AwsResourceService.java  # Servizio centrale AWS
        │       │   └── impl/                    # Servizi specifici per prodotto
        │       │       ├── Ec2Service.java
        │       │       ├── S3Service.java
        │       │       ├── RdsService.java
        │       │       ├── IamService.java
        │       │       ├── CloudFormationService.java
        │       │       ├── LambdaService.java
        │       │       ├── DynamoDbService.java
        │       │       ├── CloudFrontService.java
        │       │       ├── EventBridgeService.java
        │       │       ├── SnsService.java
        │       │       ├── SqsService.java
        │       │       ├── SsmService.java
        │       │       ├── StepFunctionsService.java
        │       │       └── ApiGatewayService.java
        │       └── model/
        │           └── AwsResource.java         # Modello dati
        └── resources/
            ├── fxml/
            │   └── main.fxml                    # Layout UI
            └── css/
                └── style.css                    # Stili CSS
```

## Configurazione AWS

### Configurare le credenziali AWS

1. Creare il file `~/.aws/credentials`:

```ini
[default]
aws_access_key_id = YOUR_ACCESS_KEY
aws_secret_access_key = YOUR_SECRET_KEY

[profile-name]
aws_access_key_id = YOUR_ACCESS_KEY
aws_secret_access_key = YOUR_SECRET_KEY
```

2. Opzionalmente, configurare la regione di default in `~/.aws/config`:

```ini
[default]
region = eu-central-1

[profile profile-name]
region = us-east-1
```

## Compilazione e Esecuzione

### Compilare il progetto

```bash
cd AWS/ManagerFx
mvn clean compile
```

### Eseguire l'applicazione

#### Metodo 1: Con Maven e JavaFX Plugin

```bash
mvn javafx:run
```

#### Metodo 2: Creare un JAR eseguibile

```bash
# Crea il JAR
mvn clean package

# Esegui il JAR (richiede JavaFX nel classpath)
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/manager-fx-1.0-SNAPSHOT.jar
```

#### Metodo 3: Con il JAR Shade (consigliato)

```bash
# Crea il JAR con tutte le dipendenze
mvn clean package

# Esegui il JAR
java -jar target/manager-fx-1.0-SNAPSHOT.jar
```

## Utilizzo

1. **Avvia l'applicazione**
2. **Seleziona la regione AWS** dal menu a tendina
3. **Seleziona il profilo AWS** (se ne hai configurati più di uno)
3. **Clicca su "Aggiorna"** per caricare le risorse
5. **Naviga tra i tab sulla sinistra** per visualizzare le diverse risorse:
   - **VPC**: Visualizza la VPC di default e le subnet
   - **EC2**: Lista delle istanze EC2 con stato e IP
   - **S3**: Lista dei bucket S3
   - **RDS**: Istanze di database RDS
   - **Security Groups**: Security groups della VPC
   - **CloudFormation**: Stack CloudFormation
   - **Lambda**: Funzioni Lambda
   - **CloudFront**: Distribuzioni CloudFront
   - **DynamoDB**: Tabelle DynamoDB
   - **EventBridge**: Regole EventBridge
   - **SNS**: Topic SNS
   - **SQS**: Code SQS
   - **SSM**: Parametri SSM
   - **Step Functions**: State machines
   - **API Gateway**: REST APIs
   - **IAM**: Groups, Users e Roles

## Funzionalità Future

- [ ] Operazioni complete su istanze EC2 (start/stop/terminate)
- [ ] Creazione di nuove risorse AWS
- [ ] Visualizzazione dettagliata delle risorse
- [ ] Export dei dati in CSV/JSON
- [ ] Grafici e statistiche
- [ ] Gestione dei tag delle risorse
- [ ] Ricerca e filtri avanzati
- [ ] Notifiche per eventi AWS
- [ ] Supporto per più account AWS

## Dipendenze Principali

- **JavaFX 21.0.1**: Framework UI
- **AWS SDK 2.20.70**: Client per servizi AWS
  - EC2, S3, RDS, EKS, ECR
  - CloudFormation, CloudWatch
  - Lambda, DynamoDB, SQS, SNS
  - API Gateway, IAM, ELB
- **Lombok**: Riduzione del boilerplate code
- **SLF4J**: Logging

## Troubleshooting

### Errore: "Impossibile creare i client AWS"

- Verifica che le credenziali AWS siano configurate correttamente in `~/.aws/credentials`
- Controlla che il profilo selezionato esista
- Verifica di avere i permessi necessari per accedere alle risorse AWS

### Errore: "JavaFX runtime components are missing"

- Assicurati di avere JavaFX installato e configurato
- Usa `mvn javafx:run` invece di eseguire direttamente il JAR
- Oppure specifica il module-path di JavaFX quando esegui il JAR

### Errore di connessione AWS

- Verifica la tua connessione internet
- Controlla che la regione AWS selezionata sia valida
- Verifica i log per dettagli specifici sull'errore

## Licenza

Questo progetto è distribuito sotto licenza MIT.

## Autore

**AlNao** - [www.alnao.it](https://www.alnao.it)

## Riferimenti

- [AWS SDK for Java v2](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/)
- [JavaFX Documentation](https://openjfx.io/)
- [Maven](https://maven.apache.org/)

## Architettura del Codice

Il progetto utilizza un'architettura modulare con separazione delle responsabilità:

- **AwsResourceService**: Servizio centrale che coordina tutti i servizi AWS specifici
- **service.impl**: Package con classi dedicate per ogni servizio AWS (Ec2Service, S3Service, IamService, etc.)
- **MainController**: Controller JavaFX che gestisce l'interfaccia utente e le interazioni
- **Tab laterali**: I tab sono posizionati a sinistra per facilitare la navigazione quando ci sono molti servizi

### Vantaggi dell'architettura

- **Modularità**: Ogni servizio AWS ha la sua classe dedicata
- **Manutenibilità**: Facile aggiungere nuovi servizi AWS
- **Testabilità**: Ogni servizio può essere testato indipendentemente
- **Scalabilità**: Semplice estendere le funzionalità per ogni servizio

## Note di Sviluppo

Questo progetto è stato creato come esempio di integrazione tra JavaFX e AWS SDK.
È ispirato al progetto Python Flask disponibile in `/PythonExamples/AWS/Managers/PanoramicResources/`.

Il progetto è in fase di sviluppo attivo e nuove funzionalità verranno aggiunte nel tempo.





# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.