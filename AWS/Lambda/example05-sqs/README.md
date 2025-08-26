# 📨 Java Maven Example05 - SQS Integration 🚀

**Integrazione con Amazon SQS** - Lambda Function per il processamento di messaggi da code Amazon SQS con gestione eventi asincroni.

## 🎯 Descrizione
Lambda Function specializzata nell'integrazione con Amazon SQS (Simple Queue Service) per il processamento asincrono di messaggi. Implementa pattern di messaging robusti per architetture serverless.

**Caratteristiche principali**:
- ✅ **Lettura messaggi** da code SQS
- ✅ **Processamento asincrono** eventi
- ✅ **Dead Letter Queue** per messaggi falliti
- ✅ **Batch processing** configurabile
- ✅ **Retry logic** integrata

## 📋 Prerequisiti
- **Coda SQS** esistente (standard o FIFO)
- **Permessi IAM** per SQS (ReceiveMessage, DeleteMessage)
- **Dead Letter Queue** (opzionale ma consigliata)
- Vedere i prerequisiti generali nel [README principale](../README.md)

## 🛠️ Setup del progetto

### 1️⃣ Creazione da VS Code
```
Ctrl+Shift+P → AWS: Create Lambda SAM Application
Runtime: Java 11
Template: HelloWorld
Name: example05-sqs
```

### 2️⃣ Configurazione dipendenze
Aggiungere al `pom.xml`:
```xml
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-lambda-java-events</artifactId>
    <version>3.11.0</version>
</dependency>
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk-sqs</artifactId>
    <version>1.12.500</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.2</version>
</dependency>
```

### 3️⃣ Configurazione IAM
Aggiungere al `template.yaml`:
```yaml
Policies:
  - SQSPollerPolicy:
      QueueName: !Ref SQSQueueName
  - Version: '2012-10-17'
    Statement:
      - Effect: Allow
        Action:
          - sqs:ReceiveMessage
          - sqs:DeleteMessage
          - sqs:GetQueueAttributes
        Resource: !Sub 'arn:aws:sqs:${AWS::Region}:${AWS::AccountId}:${SQSQueueName}'
```

## 🚀 Implementazione

### Handler per eventi SQS
```java
package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class App implements RequestHandler<SQSEvent, String> {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public String handleRequest(SQSEvent event, Context context) {
        context.getLogger().log("Received " + event.getRecords().size() + " messages");
        
        for (SQSEvent.SQSMessage message : event.getRecords()) {
            try {
                processMessage(message, context);
            } catch (Exception e) {
                context.getLogger().log("Error processing message: " + e.getMessage());
                // In caso di errore, il messaggio verrà rimesso in coda
                throw new RuntimeException("Failed to process message", e);
            }
        }
        
        return "Successfully processed " + event.getRecords().size() + " messages";
    }
    
    private void processMessage(SQSEvent.SQSMessage message, Context context) throws Exception {
        String messageBody = message.getBody();
        context.getLogger().log("Processing message: " + messageBody);
        
        // Deserializza il messaggio JSON
        MessageData data = objectMapper.readValue(messageBody, MessageData.class);
        
        // La tua logica di business qui
        processBusinessLogic(data, context);
        
        context.getLogger().log("Message processed successfully");
    }
    
    private void processBusinessLogic(MessageData data, Context context) {
        // Esempio: processamento dei dati
        context.getLogger().log("Processing data: " + data.toString());
        
        // Simula elaborazione
        try {
            Thread.sleep(100); // Simula lavoro
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // Classe per deserializzazione messaggi
    public static class MessageData {
        private String id;
        private String type;
        private String payload;
        
        // Getters e setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getPayload() { return payload; }
        public void setPayload(String payload) { this.payload = payload; }
        
        @Override
        public String toString() {
            return String.format("MessageData{id='%s', type='%s', payload='%s'}", id, type, payload);
        }
    }
}
```

## 🏗️ Configurazione SAM

### Template completo (template.yaml)
```yaml
AWSTemplateFormatVersion: '2010-09-09'
Transform: AWS::Serverless-2016-10-31

Parameters:
  SQSQueueName:
    Type: String
    Default: "example-queue"
    Description: Nome della coda SQS

Globals:
  Function:
    Timeout: 30
    MemorySize: 512

Resources:
  SQSProcessorFunction:
    Type: AWS::Serverless::Function
    Properties:
      CodeUri: HelloWorldFunction
      Handler: helloworld.App::handleRequest
      Runtime: java11
      ReservedConcurrencyLimit: 10
      Policies:
        - SQSPollerPolicy:
            QueueName: !Ref SQSQueueName
      Events:
        SQSEvent:
          Type: SQS
          Properties:
            Queue: !Sub 'arn:aws:sqs:${AWS::Region}:${AWS::AccountId}:${SQSQueueName}'
            BatchSize: 10
            MaximumBatchingWindowInSeconds: 5

  # Coda di esempio (opzionale, se non esiste già)
  ExampleQueue:
    Type: AWS::SQS::Queue
    Properties:
      QueueName: !Ref SQSQueueName
      VisibilityTimeoutSeconds: 180
      MessageRetentionPeriod: 1209600  # 14 giorni
      ReddrivePolicy:
        deadLetterTargetArn: !GetAtt DeadLetterQueue.Arn
        maxReceiveCount: 3

  # Dead Letter Queue per messaggi falliti
  DeadLetterQueue:
    Type: AWS::SQS::Queue
    Properties:
      QueueName: !Sub '${SQSQueueName}-dlq'
      MessageRetentionPeriod: 1209600  # 14 giorni

Outputs:
  SQSQueueUrl:
    Description: "URL della coda SQS"
    Value: !Ref ExampleQueue
  DeadLetterQueueUrl:
    Description: "URL della Dead Letter Queue"
    Value: !Ref DeadLetterQueue
```

## 🧪 Testing

### 1️⃣ Build e deploy
```bash
sam build
sam deploy --guided
```

### 2️⃣ Invio messaggio di test
```bash
# Ottieni URL della coda
QUEUE_URL=$(aws sqs get-queue-url --queue-name example-queue --query 'QueueUrl' --output text)

# Invia messaggio di test
aws sqs send-message --queue-url $QUEUE_URL --message-body '{
  "id": "test-001",
  "type": "order",
  "payload": "Test message payload"
}'
```

### 3️⃣ Test locale
Event di test (`events/sqs-event.json`):
```json
{
  "Records": [
    {
      "messageId": "test-message-id",
      "receiptHandle": "test-receipt-handle",
      "body": "{\"id\":\"test-001\",\"type\":\"order\",\"payload\":\"Test payload\"}",
      "attributes": {
        "ApproximateReceiveCount": "1",
        "SentTimestamp": "1645123456789",
        "SenderId": "AIDAIT2UOQQY3AUEKVGXU",
        "ApproximateFirstReceiveTimestamp": "1645123456789"
      },
      "messageAttributes": {},
      "md5OfBody": "test-md5",
      "eventSource": "aws:sqs",
      "eventSourceARN": "arn:aws:sqs:eu-west-1:123456789012:example-queue",
      "awsRegion": "eu-west-1"
    }
  ]
}
```

```bash
sam local invoke SQSProcessorFunction --event events/sqs-event.json
```

## 📊 Monitoraggio

### CloudWatch Metrics
- **Duration**: Tempo di processamento
- **Invocations**: Numero di elaborazioni
- **Errors**: Messaggi falliti
- **IteratorAge**: Età dei messaggi nella coda

### Allarmi consigliati
```bash
# Allarme per messaggi in DLQ
aws cloudwatch put-metric-alarm \
  --alarm-name "SQS-DLQ-Messages" \
  --alarm-description "Messages in Dead Letter Queue" \
  --metric-name ApproximateNumberOfVisibleMessages \
  --namespace AWS/SQS \
  --statistic Sum \
  --period 300 \
  --threshold 1 \
  --comparison-operator GreaterThanOrEqualToThreshold \
  --dimensions Name=QueueName,Value=example-queue-dlq
```

### Logs analysis
```bash
# Logs in tempo reale
sam logs -n SQSProcessorFunction --stack-name example05-sqs --tail

# Cerca errori
sam logs -n SQSProcessorFunction --stack-name example05-sqs --filter "ERROR"
```

## ⚙️ Configurazioni avanzate

### Batch processing ottimizzato
```yaml
Events:
  SQSEvent:
    Type: SQS
    Properties:
      Queue: !Ref ExampleQueue
      BatchSize: 100          # Max messaggi per invocazione
      MaximumBatchingWindowInSeconds: 20  # Attesa per batch parziali
      FunctionResponseTypes:
        - ReportBatchItemFailures  # Gestione errori granulare
```

### Gestione errori per singolo messaggio
```java
@Override
public SQSBatchResponse handleRequest(SQSEvent event, Context context) {
    List<SQSBatchResponse.BatchItemFailure> failures = new ArrayList<>();
    
    for (SQSEvent.SQSMessage message : event.getRecords()) {
        try {
            processMessage(message, context);
        } catch (Exception e) {
            context.getLogger().log("Failed to process message: " + message.getMessageId());
            failures.add(new SQSBatchResponse.BatchItemFailure(message.getMessageId()));
        }
    }
    
    return new SQSBatchResponse(failures);
}
```

## 🧹 Cleanup
```bash
# Rimozione stack (include code SQS)
sam delete

# Solo funzione Lambda (mantiene code)
aws cloudformation delete-stack --stack-name example05-sqs
```

## 🔗 Riferimenti
- [AWS SQS Developer Guide](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/)
- [Lambda SQS Integration](https://docs.aws.amazon.com/lambda/latest/dg/with-sqs.html)
- [SQS Best Practices](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-best-practices.html)


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
* Java11 - [Install the Java 11](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html)
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
example05-sqs-get$ sam build
```

The SAM CLI installs dependencies defined in `HelloWorldFunction/pom.xml`, creates a deployment package, and saves it in the `.aws-sam/build` folder.

Test a single function by invoking it directly with a test event. An event is a JSON document that represents the input that the function receives from the event source. Test events are included in the `events` folder in this project.

Run functions locally and invoke them with the `sam local invoke` command.

```bash
example05-sqs-get$ sam local invoke HelloWorldFunction --event events/event.json
```

The SAM CLI can also emulate your application's API. Use the `sam local start-api` to run the API locally on port 3000.

```bash
example05-sqs-get$ sam local start-api
example05-sqs-get$ curl http://localhost:3000/
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
example05-sqs-get$ sam logs -n HelloWorldFunction --stack-name example05-sqs-get --tail
```

You can find more information and examples about filtering Lambda function logs in the [SAM CLI Documentation](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-logging.html).

## Unit tests

Tests are defined in the `HelloWorldFunction/src/test` folder in this project.

```bash
example05-sqs-get$ cd HelloWorldFunction
HelloWorldFunction$ mvn test
```

## Cleanup

To delete the sample application that you created, use the AWS CLI. Assuming you used your project name for the stack name, you can run the following:

```bash
aws cloudformation delete-stack --stack-name example05-sqs-get
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