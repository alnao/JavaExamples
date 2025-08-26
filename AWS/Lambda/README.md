# Java Examples - AWS Lambda 🚀
Java AWS Lambda Examples by [AlNao](https://www.alnao.it/aws)

Raccolta completa di esempi pratici per sviluppare e deployare AWS Lambda Functions in Java utilizzando Maven, AWS SAM e diverse modalità di rilascio.

## 📋 Prerequisiti

- **Software richiesti**
  - Java Development Kit (JDK) 8 o superiore
  - Maven 3.6 o superiore per la gestione delle dipendenze
  - Docker per il testing locale con SAM
  - AWS CLI v2 - [Guida installazione](https://docs.aws.amazon.com/it_it/cli/v2/userguide/install-cliv2.html)
  - AWS SAM CLI - [Guida installazione](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
- **Configurazione AWS**
  1. **Account AWS attivo** con credenziali programmatiche
  2. **Utenza IAM** con permessi per:
      - AWS Lambda (creazione, aggiornamento, eliminazione)
      - AWS CloudFormation (per SAM deployments)
      - Amazon S3 (per upload artifacts)
      - Amazon SQS (per esempi specifici)
      - Amazon CloudWatch (per logging)
  - **Configurazione AWS CLI**:
    ```bash
    aws configure
    # Inserire: Access Key, Secret Key, Region (es. eu-west-1), Output format (json)
    ```


## 🛠️ Comandi base
- Creazione progetto da archetype Maven
  ```bash
  mvn archetype:generate \
    -DarchetypeGroupId=software.amazon.awssdk \
    -DarchetypeArtifactId=archetype-lambda \
    -DarchetypeVersion=2.20.x
  ```
- Build e packaging
  ```bash
  # Compilazione standard
  mvn clean install

  # Test locale con SAM
  sam local invoke

  # Build per deployment
  sam build
  ```
- **Testa localmente** (opzionale):
  ```bash
  sam local invoke
  ```
- Deploy con SAM
  ```bash
  # Deploy guidato (prima volta)
  sam deploy --guided

  # Deploy successivi
  sam deploy

  # Rimozione stack
  sam delete
  ```
- Test e debugging
  ```bash
  # Logs CloudWatch
  aws logs describe-log-groups --log-group-name-prefix "/aws/lambda/"

  # Invoke diretta
  aws lambda invoke --function-name <function-name> output.json

  # Lista funzioni
  aws lambda list-functions
  ```


## 📁 Esempi disponibili
- **example01-console**
  - ✅ Deployment via Console Web AWS
  - ✅ Deployment via AWS SAM
  - ✅ Deployment via AWS CLI puro
  - 📝 Handler: `it.alnao.App::handleRequest`
  - 🎯 Caso d'uso: Introduzione alle Lambda functions
- **example02-eclipse**
  - ✅ Creazione tramite Eclipse IDE
  - ✅ Plugin AWS per Eclipse
  - ⚠️ Note: Possibili conflitti con versioni Java/JAXB
  - 🎯 Caso d'uso: Sviluppo integrato in Eclipse
- **example03-vscode**
  - ✅ AWS Toolkit per VS Code
  - ✅ Template SAM HelloWorld
  - ✅ API Gateway integration
  - ✅ Testing locale con Docker
  - 🎯 Caso d'uso: Sviluppo moderno con VS Code
- **example04-s3signature**
  - ✅ Generazione pre-signed URLs
  - ✅ Accesso sicuro a bucket S3
  - ✅ Gestione permessi IAM per S3
  - 📦 Dipendenza: `aws-java-sdk-s3`
  - 🎯 Caso d'uso: Download sicuro file da S3
- **example05-sqs**
  - ✅ Lettura messaggi da coda SQS
  - ✅ Gestione eventi asincroni
  - ✅ Permessi IAM per SQS
  - 📦 Dipendenza: AWS SDK SQS
  - 🎯 Caso d'uso: Processamento messaggi asincroni
- **example06-excel2csv**
  - ✅ Processing file Excel
  - ✅ Trigger automatico su upload S3
  - ✅ EventBridge integration
  - ✅ Output CSV su S3
  - 📦 Dipendenze: Apache POI per Excel
  - 🎯 Caso d'uso: Elaborazione batch documenti


## 📚 Riferimenti utili
- [AWS Lambda Java Documentation](https://docs.aws.amazon.com/lambda/latest/dg/java-package.html)
- [AWS SAM Developer Guide](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/)
- [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/lambda/)
- [Bootstrapping Java Lambda Applications](https://aws.amazon.com/it/blogs/developer/bootstrapping-a-java-lambda-application-with-minimal-aws-java-sdk-startup-time-using-maven/)


## 🏆 Best Practices & Pattern Comuni

### 📋 **Configurazione AWS Multi-Ambiente**
```bash
# Parametrizzazione regioni e ambienti
# template.yaml - Parametri CloudFormation
Parameters:
  Environment:
    Type: String
    Default: dev
    AllowedValues: [dev, test, prod]
  BucketName:
    Type: String
    Default: "my-bucket"
  Region:
    Type: String
    Default: "eu-central-1"

# samconfig.toml - Configurazioni per ambiente
[dev.deploy.parameters]
region = "eu-central-1"
parameter_overrides = ["Environment=dev", "BucketName=my-dev-bucket"]

[prod.deploy.parameters]  
region = "eu-west-1"
parameter_overrides = ["Environment=prod", "BucketName=my-prod-bucket"]
```

### 🔐 **Gestione Credenziali AWS Sicura**
```java
// Pattern per credenziali flessibili
@Configuration
public class AwsConfig {
    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        // 1. Credenziali esplicite (development)
        if (hasExplicitCredentials()) {
            return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey));
        }
        // 2. Profilo AWS (development)
        else if (hasProfile()) {
            return ProfileCredentialsProvider.create(profileName);
        }
        // 3. Default chain (produzione - IAM roles, instance profiles)
        else {
            return DefaultCredentialsProvider.create();
        }
    }
}
```

### 🌍 **Configurazione Regioni Parametrica**
```java
// Lettura regione da variabili d'ambiente o parametri
@Component
public class RegionConfig {
    @Value("${aws.region:#{environment.AWS_REGION ?: 'eu-central-1'}}")
    private String region;
    
    @Bean
    public Region awsRegion() {
        return Region.of(region);
    }
}
```

### 🏷️ **Tagging e Naming Convention**
```yaml
# template.yaml - Tag standardizzati
Tags:
  Environment: !Ref Environment
  Project: !Ref "AWS::StackName"
  ManagedBy: "SAM"
  Owner: "AlNao"

# Naming convention per risorse
Resources:
  MyFunction:
    Type: AWS::Serverless::Function
    Properties:
      FunctionName: !Sub "${AWS::StackName}-${Environment}-my-function"
```

### 🛡️ **Sicurezza IAM - Principio del Privilegio Minimo**
```yaml
# Role con permessi minimi necessari
MyFunctionRole:
  Type: AWS::IAM::Role
  Properties:
    RoleName: !Sub "${AWS::StackName}-lambda-role"  # Nome abbreviato
    AssumeRolePolicyDocument:
      Version: '2012-10-17'
      Statement:
        - Effect: Allow
          Principal:
            Service: lambda.amazonaws.com
          Action: sts:AssumeRole
    ManagedPolicyArns:
      - arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole
    Policies:
      - PolicyName: S3SpecificAccess
        PolicyDocument:
          Version: '2012-10-17'
          Statement:
            - Effect: Allow
              Action:
                - s3:GetObject
                - s3:PutObject
              Resource: !Sub "${MyBucket}/*"
```

### 📦 **Deploy Script Automatizzati**
```bash
#!/bin/bash
# Script deploy multi-ambiente con validazione
set -e

ENVIRONMENT=${1:-dev}
REGION=${2:-eu-central-1}
STACK_NAME="my-project-${ENVIRONMENT}"

# Validazione prerequisiti
command -v sam >/dev/null 2>&1 || { echo "SAM CLI richiesto"; exit 1; }
aws sts get-caller-identity >/dev/null || { echo "AWS non configurato"; exit 1; }

# Build e deploy
sam build
sam deploy \
  --config-env $ENVIRONMENT \
  --region $REGION \
  --stack-name $STACK_NAME \
  --parameter-overrides Environment=$ENVIRONMENT Region=$REGION \
  --tags Environment=$ENVIRONMENT Project=$STACK_NAME
```

### 🧪 **Testing e Validazione**
```bash
# Test automatizzato con recupero dinamico endpoints
#!/bin/bash
get_stack_output() {
    aws cloudformation describe-stacks \
        --stack-name "$1" \
        --query "Stacks[0].Outputs[?OutputKey=='$2'].OutputValue" \
        --output text
}

API_URL=$(get_stack_output "my-stack" "ApiUrl")
test_endpoint() {
    response=$(curl -s -w "%{http_code}" "$1")
    [[ "${response: -3}" == "200" ]] && echo "✅ $1" || echo "❌ $1"
}

test_endpoint "$API_URL/health"
```

### 🔧 **Troubleshooting Comune**

| **Errore** | **Causa** | **Soluzione** |
|------------|-----------|---------------|
| `Access Denied` su S3 | Bucket inesistente/regione sbagliata | Usare `--resolve-s3` o bucket esistente |
| `PermanentRedirect` | Regione S3 non configurata | Forzare regione nel client S3 |
| `IAM role name too long` | Nome stack troppo lungo | Abbreviare nome o rimuovere `RoleName` |
| `Region mismatch` | CLI/template regioni diverse | Sincronizzare tutte le configurazioni |

### 📊 **Monitoraggio e Logging**
```yaml
# CloudWatch Logs retention e structured logging
Globals:
  Function:
    Environment:
      Variables:
        LOG_LEVEL: INFO
        STRUCTURED_LOGGING: true
    LoggingConfig:
      LogFormat: JSON
      ApplicationLogLevel: INFO
      SystemLogLevel: WARN
```

### 🚀 **Pattern di Deployment**
- **Sviluppo**: `sam local` per test locale
- **Testing**: Deploy su ambiente dedicato con dati di test
- **Produzione**: Deploy con parametri di produzione e monitoring abilitato
- **Rollback**: Mantenere versioni precedenti per rollback rapido

### 📁 **Struttura Progetto Standardizzata**
```
project/
├── src/main/java/           # Codice sorgente
├── src/test/java/           # Test unitari
├── events/                  # Event samples per testing
├── template.yaml            # SAM template
├── samconfig.toml          # Configurazioni deploy
├── pom.xml                 # Dipendenze Maven
├── README.md               # Documentazione progetto
└── scripts/
    ├── deploy.sh           # Script deploy automatizzato
    ├── test.sh             # Script test automatizzato
    └── cleanup.sh          # Script pulizia risorse
```


# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l'obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.