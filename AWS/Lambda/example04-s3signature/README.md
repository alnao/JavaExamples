# 🔐 Java Maven Example04 - S3 Signature 🚀

**Download file S3 con URL firmati** - Lambda Function per generare pre-signed URLs sicuri per il download di file da Amazon S3.

## 🎯 Descrizione
Lambda Function specializzata nella generazione di URL firmati (pre-signed URLs) per il download sicuro di file da bucket S3. Implementa le best practices di sicurezza AWS per l'accesso controllato ai file.

**Caratteristiche principali**:
- ✅ **Pre-signed URLs** per download temporanei
- ✅ **Bucket e file parametrici** (configurabili via template e query params)
- ✅ **Accesso sicuro** senza credenziali pubbliche
- ✅ **Controllo scadenza** degli URL generati (configurabile)
- ✅ **Permessi IAM** specifici per S3
- ✅ **API Gateway** integration
- ✅ **Validazione parametri** robusta

## 📋 Prerequisiti
- **Bucket S3** esistente con file di test
- **Permessi IAM** per S3 GetObject
- Vedere i prerequisiti generali nel [README principale](../README.md)

## ⚙️ Configurazione parametrica

### Parametri del template SAM
Il template supporta questi parametri configurabili:

```bash
# Deploy con parametri personalizzati
sam deploy --parameter-overrides \
  "S3BucketName=my-prod-bucket" \
  "Environment=prod"
```

**Parametri disponibili**:
- `S3BucketName`: Nome del bucket S3 (default: "my-s3-bucket")
- `Environment`: Ambiente di deployment (dev/test/prod, default: "dev")

### Parametri API runtime
L'API accetta questi query parameters:

- `key` (obbligatorio): Nome del file nel bucket S3
- `expires` (opzionale): Durata in ore prima della scadenza (default: 1, max: 24)

**Esempio chiamata**:
```bash
curl "https://your-api-id.execute-api.eu-west-1.amazonaws.com/Prod/signature?key=document.pdf&expires=6"
```

## 🛠️ Setup del progetto

### 1️⃣ Creazione da VS Code
```
Ctrl+Shift+P → AWS: Create Lambda SAM Application
Runtime: Java 11
Template: HelloWorld
Name: example04-s3signature
```

### 2️⃣ Configurazione dipendenze
Aggiungere al `pom.xml`:
```xml
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk-s3</artifactId>
    <version>1.12.500</version>
</dependency>
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-lambda-java-events</artifactId>
    <version>3.11.0</version>
</dependency>
```

### 3️⃣ Configurazione IAM
Aggiungere al `template.yaml`:
```yaml
Policies:
  - S3ReadPolicy:
      BucketName: !Ref S3BucketName
  - Version: '2012-10-17'
    Statement:
      - Effect: Allow
        Action:
          - s3:GetObject
          - s3:GetObjectVersion
        Resource: !Sub 'arn:aws:s3:::${S3BucketName}/*'
```

## 🚀 Implementazione

### Handler Lambda
```java
package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.net.URL;
import java.util.Date;

public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
    private final String BUCKET_NAME = System.getenv("BUCKET_NAME");
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        try {
            String objectKey = input.getQueryStringParameters().get("key");
            int expirationMinutes = Integer.parseInt(
                input.getQueryStringParameters().getOrDefault("expires", "60")
            );
            
            // Genera URL pre-firmato
            Date expiration = new Date(System.currentTimeMillis() + (expirationMinutes * 60 * 1000));
            GeneratePresignedUrlRequest generatePresignedUrlRequest = 
                new GeneratePresignedUrlRequest(BUCKET_NAME, objectKey)
                    .withMethod(com.amazonaws.HttpMethod.GET)
                    .withExpiration(expiration);
            
            URL presignedUrl = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
            
            return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(String.format("{\"presignedUrl\": \"%s\", \"expires\": \"%s\"}", 
                    presignedUrl.toString(), expiration.toString()));
                    
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                .withStatusCode(500)
                .withBody("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
```

## 🏗️ Build e Deploy

### 1️⃣ Compilazione
```bash
sam build
```

### 2️⃣ Test locale con parametri
```bash
# Test con file specifico
sam local invoke S3SignatureFunction --event events/test-signature.json

# Test API locale
sam local start-api
curl "http://localhost:3000/signature?key=test-document.pdf&expires=3"
```

### 3️⃣ Deploy parametrico

#### Deploy rapido (usa samconfig.toml)
```bash
sam deploy
```

#### Deploy personalizzato
```bash
# Deploy con bucket personalizzato
sam deploy --parameter-overrides \
  "S3BucketName=my-production-bucket" \
  "Environment=prod" \
  --capabilities CAPABILITY_NAMED_IAM \
  --region eu-central-1

# Deploy per ambiente di test a Francoforte
sam deploy --resolve-s3 --capabilities CAPABILITY_NAMED_IAM \
  --parameter-overrides "S3BucketName=cloudformation-alnao" "Environment=test" \
  --stack-name "java-aws-lambda-example04-signature" \
  --region eu-central-1
```

#### Deploy guidato (prima volta)
```bash
sam deploy --guided
```

**Parametri richiesti nel deploy guidato**:
- **Stack name**: `example04-s3signature`
- **AWS Region**: `eu-west-1`
- **Parameter S3BucketName**: `your-bucket-name`
- **Parameter Environment**: `dev`/`test`/`prod`
- **Confirm changes**: `y`
- **Allow IAM creation**: `y`
- **Save parameters**: `y`

## 🧪 Testing completo

### 🔍 Recupero informazioni dallo stack
```bash
# Recupera tutti gli output dello stack
aws cloudformation describe-stacks \
  --stack-name "java-aws-lambda-example04-signature" \
  --region eu-central-1 \
  --query 'Stacks[0].Outputs' \
  --output table

# Recupera solo l'URL dell'API
API_URL=$(aws cloudformation describe-stacks \
  --stack-name "java-aws-lambda-example04-signature" \
  --region eu-central-1 \
  --query 'Stacks[0].Outputs[?OutputKey==`S3SignatureApiUrl`].OutputValue' \
  --output text)

# Recupera l'ARN della Lambda Function
FUNCTION_ARN=$(aws cloudformation describe-stacks \
  --stack-name "java-aws-lambda-example04-signature" \
  --region eu-central-1 \
  --query 'Stacks[0].Outputs[?OutputKey==`S3SignatureFunctionArn`].OutputValue' \
  --output text)

# Recupera il nome del bucket configurato
BUCKET_NAME=$(aws cloudformation describe-stacks \
  --stack-name "java-aws-lambda-example04-signature" \
  --region eu-central-1 \
  --query 'Stacks[0].Outputs[?OutputKey==`S3BucketNameOutput`].OutputValue' \
  --output text)

echo "API URL: $API_URL"
echo "Function ARN: $FUNCTION_ARN"
echo "Bucket Name: $BUCKET_NAME"
```

### 1️⃣ Test con diversi parametri
```bash
# Recupera l'URL dell'API dagli output dello stack CloudFormation
API_URL=$(aws cloudformation describe-stacks \
  --stack-name "java-aws-lambda-example04-signature" \
  --region eu-central-1 \
  --query 'Stacks[0].Outputs[?OutputKey==`S3SignatureApiUrl`].OutputValue' \
  --output text)

echo "API URL: $API_URL"

# Test file PDF con scadenza di 2 ore
curl "${API_URL}?key=TEST/test.pdf&expires=2"

# Test file immagine con scadenza di 1 ora (default)
curl "${API_URL}?key=images/photo.jpg"

# Test file con scadenza massima (24 ore)
curl "${API_URL}?key=files/archive.zip&expires=24"
```

### 2️⃣ Response strutturata
```json
{
  "presignedUrl": "https://cloudformation-alnao.s3.eu-central-1.amazonaws.com/TEST/test.pdf?X-Amz-Algorithm=...",
  "bucket": "cloudformation-alnao",
  "key": "TEST/test.pdf",
  "region": "eu-central-1",
  "expiresInHours": 2,
  "expirationTime": "Mon Aug 26 17:30:00 UTC 2025"
}
```

### 3️⃣ Test errori
```bash
# Test senza parametro key (errore 400)
curl "${API_URL}"

# Test con file inesistente (genera URL comunque, errore sarà al download)
curl "${API_URL}?key=nonexistent-file.txt"
```

### 4️⃣ Utilizzo dell'URL generato
```bash
# Recupera l'URL dell'API dinamicamente
API_URL=$(aws cloudformation describe-stacks \
  --stack-name "java-aws-lambda-example04-signature" \
  --region eu-central-1 \
  --query 'Stacks[0].Outputs[?OutputKey==`S3SignatureApiUrl`].OutputValue' \
  --output text)

# Download del file usando l'URL pre-firmato
PRESIGNED_URL=$(curl -s "${API_URL}?key=TEST/test.pdf" | jq -r '.presignedUrl')
wget "$PRESIGNED_URL" -O downloaded-file.pdf
```

### 5️⃣ Script di test automatico
```bash
# Esegue tutti i test automaticamente recuperando l'URL dallo stack
./test-s3-signature.sh

# Con parametri personalizzati
./test-s3-signature.sh "java-aws-lambda-example04-signature" "eu-central-1"

# Oppure con stack name diverso
./test-s3-signature.sh "my-custom-stack-name" "us-east-1"
```

## ⚙️ Configurazione avanzata

### Template SAM completo
```yaml
AWSTemplateFormatVersion: '2010-09-09'
Transform: AWS::Serverless-2016-10-31

Parameters:
  S3BucketName:
    Type: String
    Default: "your-bucket-name"
    Description: Nome del bucket S3

Globals:
  Function:
    Timeout: 20
    MemorySize: 512
    Environment:
      Variables:
        BUCKET_NAME: !Ref S3BucketName

Resources:
  S3SignatureFunction:
    Type: AWS::Serverless::Function
    Properties:
      CodeUri: HelloWorldFunction
      Handler: helloworld.App::handleRequest
      Runtime: java11
      Policies:
        - S3ReadPolicy:
            BucketName: !Ref S3BucketName
      Events:
        S3Signature:
          Type: Api
          Properties:
            Path: /signature
            Method: get

Outputs:
  S3SignatureApi:
    Description: "API Gateway endpoint URL"
    Value: !Sub "https://${ServerlessRestApi}.execute-api.${AWS::Region}.amazonaws.com/Prod/signature/"
```

### Event di test (events/event.json)
```json
{
  "queryStringParameters": {
    "key": "test-file.pdf",
    "expires": "60"
  },
  "httpMethod": "GET",
  "path": "/signature"
}
```

## 🔒 Sicurezza

### Best practices implementate
1. **Least privilege**: IAM policy minima necessaria
2. **Expire URLs**: URL con scadenza configurable
3. **Validation**: Controllo parametri input
4. **Error handling**: Gestione errori senza leak informazioni

### Controlli aggiuntivi
```java
// Validazione key file
if (objectKey == null || objectKey.contains("..") || objectKey.startsWith("/")) {
    throw new IllegalArgumentException("Invalid object key");
}

// Limitazione tempo massimo
if (expirationMinutes > 1440) { // max 24 ore
    expirationMinutes = 1440;
}
```

## 📊 Monitoraggio

### CloudWatch Metrics
- **Invocations**: Numero di richieste
- **Duration**: Tempo di esecuzione
- **Errors**: Errori generati

### Log analysis
```bash
# Logs in tempo reale
sam logs -n S3SignatureFunction --stack-name example04-s3signature --tail

# Cerca errori
sam logs -n S3SignatureFunction --stack-name example04-s3signature --filter "ERROR"
```

## 🧹 Cleanup
```bash
# Rimozione stack
sam delete

# Alternativa AWS CLI
aws cloudformation delete-stack --stack-name java-aws-lambda-example04-signature
```

## 🔗 Riferimenti
- [S3 Pre-signed URLs Documentation](https://docs.aws.amazon.com/AmazonS3/latest/userguide/presigned-urls.html)
- [AWS SDK for Java S3](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/s3-examples.html)
- [S3 Security Best Practices](https://docs.aws.amazon.com/AmazonS3/latest/userguide/security-best-practices.html)



# Readme originale

This project contains source code and supporting files for a serverless application that you can deploy with the SAM CLI. It includes the following files and folders.

- HelloWorldFunction/src/main - Code for the application's Lambda function.
- events - Invocation events that you can use to invoke the function.
- HelloWorldFunction/src/test - Unit tests for the application code. 
- template.yaml - A template that defines the application's AWS resources.

The application uses several AWS resources, including Lambda functions and an API Gateway API. These resources are defined in the `template.yaml` file in this project. You can update the template to add AWS resources through the same deployment process that updates your application code.

If you prefer to use an integrated development environment (IDE) to build and test your application, you can use the AWS Toolkit.  
The AWS Toolkit is an open source plug-in for popular IDEs that uses the SAM CLI to build and deploy serverless applications on AWS. The AWS Toolkit also adds a simplified step-through debugging experience for Lambda function code. See the following links to get started.

* [CLion](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [GoLand](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [IntelliJ](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [WebStorm](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [Rider](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [PhpStorm](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [PyCharm](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [RubyMine](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [DataGrip](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [VS Code](https://docs.aws.amazon.com/toolkit-for-vscode/latest/userguide/welcome.html)
* [Visual Studio](https://docs.aws.amazon.com/toolkit-for-visual-studio/latest/user-guide/welcome.html)

## Deploy the sample application

The Serverless Application Model Command Line Interface (SAM CLI) is an extension of the AWS CLI that adds functionality for building and testing Lambda applications. It uses Docker to run your functions in an Amazon Linux environment that matches Lambda. It can also emulate your application's build environment and API.

To use the SAM CLI, you need the following tools.

* SAM CLI - [Install the SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
* Java8 - [Install the Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* Maven - [Install Maven](https://maven.apache.org/install.html)
* Docker - [Install Docker community edition](https://hub.docker.com/search/?type=edition&offering=community)

To build and deploy your application for the first time, run the following in your shell:

```bash
sam build
sam deploy --guided
```

The first command will build the source of your application. The second command will package and deploy your application to AWS, with a series of prompts:

* **Stack Name**: The name of the stack to deploy to CloudFormation. This should be unique to your account and region, and a good starting point would be something matching your project name.
* **AWS Region**: The AWS region you want to deploy your app to.
* **Confirm changes before deploy**: If set to yes, any change sets will be shown to you before execution for manual review. If set to no, the AWS SAM CLI will automatically deploy application changes.
* **Allow SAM CLI IAM role creation**: Many AWS SAM templates, including this example, create AWS IAM roles required for the AWS Lambda function(s) included to access AWS services. By default, these are scoped down to minimum required permissions. To deploy an AWS CloudFormation stack which creates or modifies IAM roles, the `CAPABILITY_IAM` value for `capabilities` must be provided. If permission isn't provided through this prompt, to deploy this example you must explicitly pass `--capabilities CAPABILITY_IAM` to the `sam deploy` command.
* **Save arguments to samconfig.toml**: If set to yes, your choices will be saved to a configuration file inside the project, so that in the future you can just re-run `sam deploy` without parameters to deploy changes to your application.

You can find your API Gateway Endpoint URL in the output values displayed after deployment.

## Use the SAM CLI to build and test locally

Build your application with the `sam build` command.

```bash
example04-s3signature$ sam build
```

The SAM CLI installs dependencies defined in `HelloWorldFunction/pom.xml`, creates a deployment package, and saves it in the `.aws-sam/build` folder.

Test a single function by invoking it directly with a test event. An event is a JSON document that represents the input that the function receives from the event source. Test events are included in the `events` folder in this project.

Run functions locally and invoke them with the `sam local invoke` command.

```bash
example04-s3signature$ sam local invoke HelloWorldFunction --event events/event.json
```

The SAM CLI can also emulate your application's API. Use the `sam local start-api` to run the API locally on port 3000.

```bash
example04-s3signature$ sam local start-api
example04-s3signature$ curl http://localhost:3000/
```

The SAM CLI reads the application template to determine the API's routes and the functions that they invoke. The `Events` property on each function's definition includes the route and method for each path.

```yaml
      Events:
        HelloWorld:
          Type: Api
          Properties:
            Path: /hello
            Method: get
```

## Add a resource to your application
The application template uses AWS Serverless Application Model (AWS SAM) to define application resources. AWS SAM is an extension of AWS CloudFormation with a simpler syntax for configuring common serverless application resources such as functions, triggers, and APIs. For resources not included in [the SAM specification](https://github.com/awslabs/serverless-application-model/blob/master/versions/2016-10-31.md), you can use standard [AWS CloudFormation](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-template-resource-type-ref.html) resource types.

## Fetch, tail, and filter Lambda function logs

To simplify troubleshooting, SAM CLI has a command called `sam logs`. `sam logs` lets you fetch logs generated by your deployed Lambda function from the command line. In addition to printing the logs on the terminal, this command has several nifty features to help you quickly find the bug.

`NOTE`: This command works for all AWS Lambda functions; not just the ones you deploy using SAM.

```bash
example04-s3signature$ sam logs -n HelloWorldFunction --stack-name example04-s3signature --tail
```

You can find more information and examples about filtering Lambda function logs in the [SAM CLI Documentation](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-logging.html).

## Unit tests

Tests are defined in the `HelloWorldFunction/src/test` folder in this project.

```bash
example04-s3signature$ cd HelloWorldFunction
HelloWorldFunction$ mvn test
```

## Cleanup

To delete the sample application that you created, use the AWS CLI. Assuming you used your project name for the stack name, you can run the following:

```bash
aws cloudformation delete-stack --stack-name example04-s3signature
```

## Resources

See the [AWS SAM developer guide](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/what-is-sam.html) for an introduction to SAM specification, the SAM CLI, and serverless application concepts.

Next, you can use AWS Serverless Application Repository to deploy ready to use Apps that go beyond hello world samples and learn how authors developed their applications: [AWS Serverless Application Repository main page](https://aws.amazon.com/serverless/serverlessrepo/)




# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.