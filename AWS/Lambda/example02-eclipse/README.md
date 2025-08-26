# 🌙 Java Maven Example02 - Eclipse IDE 🚀

**Progetto Eclipse con AWS Toolkit** - Sviluppo integrato di Lambda Functions utilizzando Eclipse IDE con plugin AWS.

## 🎯 Descrizione
Progetto Lambda Function in Java creato utilizzando Eclipse IDE con il plugin AWS Toolkit. Dimostra il workflow di sviluppo integrato nell'ambiente Eclipse.

> ⚠️ **Stato del progetto**: Esempio in fase di revisione - Presenta alcuni problemi di compatibilità con versioni Java/JAXB

## 📋 Prerequisiti
- **Eclipse IDE** (versione recente)
- **AWS Toolkit for Eclipse** - [Download qui](https://aws.amazon.com/eclipse)
- Vedere i prerequisiti generali nel [README principale](../README.md)

## 🛠️ Setup Environment

### 1️⃣ Installazione AWS Toolkit for Eclipse
1. Apri Eclipse IDE
2. Vai su `Help` → `Eclipse Marketplace`
3. Cerca "AWS Toolkit for Eclipse"
4. Installa il plugin ufficiale AWS
5. Riavvia Eclipse

### 2️⃣ Configurazione credenziali AWS
1. `Window` → `Preferences` → `AWS Toolkit` → `AWS Preferences`
2. Configura le credenziali AWS (Access Key, Secret Key)
3. Seleziona la regione di default

## 🚀 Creazione progetto

### Nuovo progetto Lambda
1. `File` → `New` → `Other`
2. Espandi `AWS` → `AWS Lambda Java Project`
3. Inserisci:
   - **Project name**: `example02-eclipse`
   - **Group ID**: `it.alnao`
   - **Artifact ID**: `example02-eclipse`
   - **Class name**: `LambdaHandler`
   - **Input type**: `String` (o personalizzato)

### Struttura generata
```
├── pom.xml                    # Configurazione Maven
├── src/main/java/
│   └── it/alnao/
│       └── LambdaHandler.java # Handler principale
└── src/test/java/
    └── it/alnao/
        └── LambdaHandlerTest.java # Test unitari
```

## 🏗️ Build del progetto

### Tramite Eclipse
1. Click destro sul progetto
2. `Run As` → `Maven build...`
3. **Goals**: `package`
4. Click `Run`

### Tramite command line
```bash
mvn clean package
```

Il JAR viene generato in `target/example02-eclipse-1.0-SNAPSHOT.jar`

## 📤 Deploy tramite Eclipse

### Upload diretto su AWS
1. Click destro sulla classe Handler (`LambdaHandler.java`)
2. `Amazon Web Services` → `Upload function to AWS Lambda`
3. Configura:
   - **Function name**: `example02-eclipse`
   - **Region**: `eu-west-1` (o preferita)
   - **IAM Role**: Crea o seleziona ruolo esistente
   - **S3 Bucket**: Bucket per upload temporaneo
4. Click `Finish`

### Configurazione IAM Role
Il ruolo deve avere almeno:
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "logs:CreateLogGroup",
                "logs:CreateLogStream",
                "logs:PutLogEvents"
            ],
            "Resource": "arn:aws:logs:*:*:*"
        }
    ]
}
```

## 🧪 Test in Eclipse

### Test locale
1. Click destro sulla classe Handler
2. `Run As` → `Run on AWS Lambda`
3. Configura input di test
4. Visualizza output nel console Eclipse

### Test su AWS
1. Vai nella vista `AWS Explorer`
2. Espandi `AWS Lambda`
3. Click destro sulla funzione → `Run on AWS Lambda`

## ⚠️ Problemi noti e soluzioni

### Errore JAXB
**Problema**: Conflitto tra Java 11+ (richiesto da Eclipse) e JAXB 1.8

**Soluzioni possibili**:
1. **Aggiungere dipendenza JAXB** al `pom.xml`:
   ```xml
   <dependency>
       <groupId>javax.xml.bind</groupId>
       <artifactId>jaxb-api</artifactId>
       <version>2.3.1</version>
   </dependency>
   <dependency>
       <groupId>org.glassfish.jaxb</groupId>
       <artifactId>jaxb-runtime</artifactId>
       <version>2.3.1</version>
   </dependency>
   ```

2. **Usare Java 8** per il progetto:
   - Click destro progetto → `Properties`
   - `Java Build Path` → `Libraries`
   - Rimuovi JRE System Library attuale
   - `Add Library` → `JRE System Library` → Java 8

3. **Alternativa SAM CLI**:
   ```bash
   # Se Eclipse non funziona, usa SAM
   sam build
   sam deploy --guided
   ```

### Errore creazione JAR
**Problema**: Maven package fallisce

**Soluzioni**:
1. **Pulisci workspace Eclipse**: `Project` → `Clean`
2. **Refresh progetto**: F5 o click destro → `Refresh`
3. **Rebuild Maven**: Click destro → `Maven` → `Reload Projects`

## 🔧 Deploy alternativo

Se Eclipse presenta problemi, usa il deploy standard:

```bash
# Build
mvn clean package

# Deploy con SAM (se presente template.yaml)
sam deploy --guided

# O upload manuale su S3 + Console AWS
aws s3 cp target/example02-eclipse-1.0-SNAPSHOT.jar s3://your-bucket/
```

## 💡 Tips per Eclipse

### Plugin consigliati
- **AWS Toolkit for Eclipse** (essenziale)
- **Maven Integration** (m2eclipse)
- **Git Integration** (EGit)

### Configurazione ottimale
1. **Increase memoria**: `eclipse.ini` → `-Xmx2048m`
2. **Auto-refresh**: `Preferences` → `General` → `Workspace` → `Refresh using native hooks`
3. **Maven offline**: Solo se necessario per problemi di rete

## 🔗 Riferimenti
- [AWS Toolkit for Eclipse Guide](https://docs.aws.amazon.com/toolkit-for-eclipse/v1/user-guide/)
- [Eclipse Marketplace - AWS Toolkit](https://marketplace.eclipse.org/content/aws-toolkit-eclipse)
- [AWS Lambda Java Development](https://docs.aws.amazon.com/lambda/latest/dg/java-package.html)




# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.