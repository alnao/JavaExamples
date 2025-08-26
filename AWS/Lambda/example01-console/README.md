# 📦 Java Maven Example01 - Console Deploy 🚀

**Lambda base con deployment multiplo** - Introduzione completa alle AWS Lambda Functions in Java con diverse modalità di rilascio.

## 🎯 Descrizione
Semplice Lambda Function in Java creata con Maven che dimostra tre modalità di deployment:
- ✅ **Console Web AWS** - Deploy manuale tramite interfaccia web
- ✅ **AWS SAM** - Deploy automatizzato con CloudFormation
- ✅ **AWS CLI puro** - Deploy tramite comandi CLI (modalità avanzata)

## 📋 Prerequisiti
Vedere i prerequisiti generali nel [README principale](../README.md).

## 🚀 Quick Start

### 1️⃣ Compilazione
```bash
mvn clean install
```

### 2️⃣ Test locale (opzionale)
```bash
sam local invoke
```
> ⚠️ **Nota**: Richiede Docker installato e in esecuzione

### 3️⃣ Deploy con SAM (consigliato)
```bash
sam deploy --guided
```

## 🛠️ Modalità di Deploy

### 🌐 Deploy via Console Web AWS

1. **Upload del JAR su S3**:
   ```bash
   aws s3 cp ./target/example01-console-1.0-SNAPSHOT.jar s3://your-bucket-name
   ```

2. **Creazione Lambda dalla Console**:
   - Vai alla Console AWS Lambda
   - Clicca "Create function"
   - Seleziona "Author from scratch"
   - Nome: `example01-console`
   - Runtime: `Java 11` (o superiore)
   - Architettura: `x86_64`

3. **Configurazione Handler**:
   ```
   it.alnao.App::handleRequest
   ```

4. **Upload codice**: 
   - Sezione "Code" → "Upload from" → "Amazon S3"
   - Inserire l'URL S3 del JAR

### ☁️ Deploy con SAM-CloudFormation (Consigliato)

```bash
# Deploy guidato (prima volta)
sam deploy --guided

# Deploy successivi
sam deploy
```

**Output esempio**:
```
Operation  LogicalResourceId    ResourceType            Replacement                                  
+ Add      AppFunctionRole      AWS::IAM::Role          N/A                                          
+ Add      AppFunction          AWS::Lambda::Function   N/A                                          
...
Successfully created/updated stack - example01-sam in eu-west-1
```

**Rimozione stack**:
```bash
sam delete
```

### ⚙️ Deploy con AWS CLI (Modalità avanzata)

> ⚠️ **Attenzione**: Modalità per utenti esperti, preferire SAM per uso normale

1. **Creazione ruolo IAM**:
   ```bash
   aws iam create-role --role-name lambda-role --assume-role-policy-document file://cli_role.json
   ```

2. **Creazione Lambda Function**:
   ```bash
   aws lambda create-function \
     --function-name example01-cli \
     --zip-file fileb://target/example01-console-1.0-SNAPSHOT.jar \
     --runtime java11 \
     --role arn:aws:iam::YOUR-ACCOUNT-ID:role/lambda-role \
     --handler it.alnao.App::handleRequest
   ```

3. **Test Lambda**:
   ```bash
   aws lambda invoke --function-name example01-cli outputfile.txt
   ```

4. **Comandi di gestione**:
   ```bash
   # Aggiornamento codice
   aws lambda update-function-code \
     --function-name example01-cli \
     --zip-file fileb://target/example01-console-1.0-SNAPSHOT.jar 
   
   # Lista funzioni
   aws lambda list-functions --max-items 10
   
   # Dettagli funzione
   aws lambda get-function --function-name example01-cli
   
   # Rimozione
   aws lambda delete-function --function-name example01-cli
   aws iam delete-role --role-name lambda-role
   ```

## 🏗️ Creazione progetto da zero

Per creare un nuovo progetto simile:

```bash
mvn archetype:generate \
  -DarchetypeGroupId=software.amazon.awssdk \
  -DarchetypeArtifactId=archetype-lambda \
  -DarchetypeVersion=2.20.x
```

> 💡 **Tip**: Dopo la generazione, puoi rimuovere i riferimenti alla classe `DependencyFactory` se non necessari.

## 📁 Struttura del progetto
```
├── cli_role.json          # Policy IAM per deployment CLI
├── pom.xml               # Configurazione Maven
├── samconfig.toml        # Configurazione SAM
├── template.yaml         # Template CloudFormation SAM
└── src/
    └── main/java/it/alnao/
        └── App.java      # Handler principale Lambda
```

## 🔧 Configurazione

### Handler Function
```java
package it.alnao;

public class App implements RequestHandler<Object, String> {
    @Override
    public String handleRequest(Object input, Context context) {
        // La tua logica qui
        return "Hello from Lambda!";
    }
}
```

### Template SAM (template.yaml)
Il file template.yaml definisce:
- Funzione Lambda con runtime Java
- Ruolo IAM con permessi CloudWatch
- Timeout e memoria allocati

## 📊 Monitoraggio

### CloudWatch Logs
I log sono automaticamente inviati a CloudWatch:
```bash
# Visualizza log groups
aws logs describe-log-groups --log-group-name-prefix "/aws/lambda/"

# Visualizza log stream
aws logs describe-log-streams --log-group-name "/aws/lambda/example01"
```

### Test dalla Console
1. Vai alla console Lambda
2. Seleziona la funzione
3. Tab "Test" → "Create test event"
4. Esegui il test e visualizza i risultati

## 🔗 Riferimenti
- [AWS Lambda Java Documentation](https://docs.aws.amazon.com/lambda/latest/dg/java-package.html)
- [Bootstrapping Java Lambda](https://aws.amazon.com/it/blogs/developer/bootstrapping-a-java-lambda-application-with-minimal-aws-java-sdk-startup-time-using-maven/)
- [AWS SAM Deploying Guide](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-deploying.html)

# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.