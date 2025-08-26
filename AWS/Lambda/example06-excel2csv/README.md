# 📊 Java Maven Example06 - Excel to CSV 🚀

**Conversione Excel a CSV** - Lambda Function per l'elaborazione batch di documenti Excel con trigger automatico su upload S3 ed EventBridge integration.

## 🎯 Descrizione
Lambda Function specializzata nella conversione automatica di file Excel (.xlsx, .xls) in formato CSV. Si attiva automaticamente quando viene caricato un file Excel su S3 e salva il risultato nella stessa posizione.

**Caratteristiche principali**:
- ✅ **Processing file Excel** (XLSX, XLS)
- ✅ **Trigger automatico** su upload S3
- ✅ **EventBridge integration** per eventi S3
- ✅ **Output CSV** salvato su S3
- ✅ **Apache POI** per manipolazione Excel
- ✅ **Gestione errori** robusta

## 📋 Prerequisiti
- **Bucket S3** per input e output files
- **EventBridge** configurazione (automatica con SAM)
- **Permessi IAM** per S3 (Read/Write)
- Vedere i prerequisiti generali nel [README principale](../README.md)

## 🛠️ Setup del progetto

### 1️⃣ Creazione da VS Code
```
Ctrl+Shift+P → AWS: Create Lambda SAM Application
Runtime: Java 11
Template: HelloWorld
Name: example06-excel2csv
```

> ⚠️ **Nota Windows**: Se hai problemi con path lunghi, abilita il supporto in PowerShell come amministratore:
> ```powershell
> New-ItemProperty -Path "HKLM:\SYSTEM\CurrentControlSet\Control\FileSystem" -Name "LongPathsEnabled" -Value 1 -PropertyType DWORD -Force
> ```

### 2️⃣ Configurazione dipendenze
Aggiungere al `pom.xml`:
```xml
<!-- Apache POI per Excel -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.2.4</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.4</version>
</dependency>

<!-- AWS SDK -->
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

### 3️⃣ Configurazione IAM e EventBridge
Template SAM completo (`template.yaml`):
```yaml
AWSTemplateFormatVersion: '2010-09-09'
Transform: AWS::Serverless-2016-10-31

Parameters:
  S3BucketName:
    Type: String
    Default: "excel-converter-bucket"
    Description: Nome del bucket S3

Globals:
  Function:
    Timeout: 300  # 5 minuti per file grandi
    MemorySize: 1024

Resources:
  ExcelConverterFunction:
    Type: AWS::Serverless::Function
    Properties:
      CodeUri: HelloWorldFunction
      Handler: exc2csv.App::handleRequest
      Runtime: java11
      Policies:
        - S3ReadPolicy:
            BucketName: !Ref S3BucketName
        - S3WritePolicy:
            BucketName: !Ref S3BucketName
      Events:
        S3ExcelUpload:
          Type: EventBridgeRule
          Properties:
            Pattern:
              source: ["aws.s3"]
              detail-type: ["Object Created"]
              detail:
                bucket:
                  name: [!Ref S3BucketName]
                object:
                  key:
                    - suffix: ".xlsx"
                    - suffix: ".xls"

  # Bucket S3 per file Excel e CSV
  ExcelBucket:
    Type: AWS::S3::Bucket
    Properties:
      BucketName: !Ref S3BucketName
      NotificationConfiguration:
        EventBridgeConfiguration:
          EventBridgeEnabled: true
      PublicAccessBlockConfiguration:
        BlockPublicAcls: true
        BlockPublicPolicy: true
        IgnorePublicAcls: true
        RestrictPublicBuckets: true

Outputs:
  S3BucketName:
    Description: "Nome del bucket S3"
    Value: !Ref ExcelBucket
  FunctionName:
    Description: "Nome della Lambda Function"
    Value: !Ref ExcelConverterFunction
```

## 🚀 Implementazione

### Handler principale
```java
package exc2csv;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.EventBridgeEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.*;
import java.util.Map;

public class App implements RequestHandler<EventBridgeEvent<Map<String, Object>>, String> {
    
    private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
    
    @Override
    public String handleRequest(EventBridgeEvent<Map<String, Object>> event, Context context) {
        try {
            // Estrae informazioni dall'evento EventBridge
            Map<String, Object> detail = event.getDetail();
            Map<String, Object> bucket = (Map<String, Object>) detail.get("bucket");
            Map<String, Object> object = (Map<String, Object>) detail.get("object");
            
            String bucketName = (String) bucket.get("name");
            String objectKey = (String) object.get("key");
            
            context.getLogger().log("Processing file: " + objectKey + " from bucket: " + bucketName);
            
            // Converte Excel a CSV
            String csvKey = convertExcelToCsv(bucketName, objectKey, context);
            
            return "Successfully converted " + objectKey + " to " + csvKey;
            
        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            throw new RuntimeException("Failed to process Excel file", e);
        }
    }
    
    private String convertExcelToCsv(String bucketName, String excelKey, Context context) throws Exception {
        // Download del file Excel da S3
        S3Object s3Object = s3Client.getObject(bucketName, excelKey);
        InputStream inputStream = s3Object.getObjectContent();
        
        // Determina il tipo di file Excel
        Workbook workbook;
        if (excelKey.toLowerCase().endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        } else if (excelKey.toLowerCase().endsWith(".xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("Unsupported file format: " + excelKey);
        }
        
        // Genera contenuto CSV
        StringBuilder csvContent = new StringBuilder();
        Sheet sheet = workbook.getSheetAt(0); // Prima sheet
        
        for (Row row : sheet) {
            boolean firstCell = true;
            for (Cell cell : row) {
                if (!firstCell) {
                    csvContent.append(",");
                }
                csvContent.append(getCellValueAsString(cell));
                firstCell = false;
            }
            csvContent.append("\n");
        }
        
        workbook.close();
        inputStream.close();
        
        // Genera nome file CSV
        String csvKey = excelKey.replaceAll("\\.(xlsx|xls)$", ".csv");
        
        // Upload CSV su S3
        ByteArrayInputStream csvInputStream = new ByteArrayInputStream(
            csvContent.toString().getBytes("UTF-8"));
        
        s3Client.putObject(bucketName, csvKey, csvInputStream, null);
        
        context.getLogger().log("CSV file created: " + csvKey);
        return csvKey;
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return escapeCSV(cell.getStringCellValue());
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return escapeCSV(cell.getCellFormula());
            default:
                return "";
        }
    }
    
    private String escapeCSV(String value) {
        if (value == null) return "";
        
        // Escape virgolette e aggiungi virgolette se necessario
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
```

## 🧪 Testing

### 1️⃣ Build e deploy
```bash
sam build
sam deploy --guided
```

### 2️⃣ Test con file Excel
```bash
# Upload di un file Excel di test
aws s3 cp test-file.xlsx s3://excel-converter-bucket/input/test-file.xlsx
```

### 3️⃣ Verifica risultato
```bash
# Lista file nel bucket
aws s3 ls s3://excel-converter-bucket/ --recursive

# Download del CSV generato
aws s3 cp s3://excel-converter-bucket/input/test-file.csv ./output/
```

### 4️⃣ Test locale
Event di test (`events/s3-event.json`):
```json
{
  "version": "0",
  "id": "test-event-id",
  "detail-type": "Object Created",
  "source": "aws.s3",
  "time": "2025-08-26T10:00:00Z",
  "region": "eu-west-1",
  "detail": {
    "bucket": {
      "name": "excel-converter-bucket"
    },
    "object": {
      "key": "test-file.xlsx",
      "size": 12345
    }
  }
}
```

```bash
sam local invoke ExcelConverterFunction --event events/s3-event.json
```

## 📊 Monitoraggio

### CloudWatch Metrics
- **Duration**: Tempo di conversione
- **Memory Used**: Memoria utilizzata (importante per file grandi)
- **Errors**: Conversioni fallite

### Logs analysis
```bash
# Logs in tempo reale
sam logs -n ExcelConverterFunction --stack-name example06-excel2csv --tail

# Cerca errori di conversione
sam logs -n ExcelConverterFunction --stack-name example06-excel2csv --filter "Error"
```

## ⚙️ Configurazioni avanzate

### Gestione file grandi
```yaml
Globals:
  Function:
    Timeout: 900      # 15 minuti max
    MemorySize: 3008  # Memoria massima
    EphemeralStorage:
      Size: 10240     # 10GB storage temporaneo
```

### Elaborazione multi-sheet
```java
// Processa tutte le sheet del workbook
for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
    Sheet sheet = workbook.getSheetAt(i);
    String sheetCsvKey = excelKey.replaceAll("\\.(xlsx|xls)$", "_sheet" + i + ".csv");
    // ... logica conversione per ogni sheet
}
```

### Notifiche di completamento
```yaml
Resources:
  CompletionTopic:
    Type: AWS::SNS::Topic
    Properties:
      TopicName: excel-conversion-notifications
      
  # Aggiungi alla Lambda
  Environment:
    Variables:
      SNS_TOPIC_ARN: !Ref CompletionTopic
```

## 🧹 Cleanup
```bash
# Rimozione stack completo (include bucket S3)
sam delete

# ⚠️ Attenzione: il bucket S3 potrebbe non essere eliminato se contiene file
# Svuota il bucket prima della rimozione se necessario
aws s3 rm s3://excel-converter-bucket --recursive
```

## 💡 Tips e Best Practices

### Ottimizzazioni
- **Stream processing** per file molto grandi
- **Chunked reading** per limitare memoria
- **Parallel processing** per multi-sheet

### Validazioni
- **Controllo formato** file prima del processing
- **Limite dimensioni** file (es. max 100MB)
- **Timeout** appropriati per file grandi

## 🔗 Riferimenti
- [Apache POI Documentation](https://poi.apache.org/components/spreadsheet/)
- [EventBridge S3 Integration](https://docs.aws.amazon.com/eventbridge/latest/userguide/eb-s3.html)
- [S3 Event Notifications](https://docs.aws.amazon.com/AmazonS3/latest/userguide/event-notifications.html)


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
example06-excel2csv$ sam build
```

The SAM CLI installs dependencies defined in `HelloWorldFunction/pom.xml`, creates a deployment package, and saves it in the `.aws-sam/build` folder.

Test a single function by invoking it directly with a test event. An event is a JSON document that represents the input that the function receives from the event source. Test events are included in the `events` folder in this project.

Run functions locally and invoke them with the `sam local invoke` command.

```bash
example06-excel2csv$ sam local invoke HelloWorldFunction --event events/event.json
```

The SAM CLI can also emulate your application's API. Use the `sam local start-api` to run the API locally on port 3000.

```bash
example06-excel2csv$ sam local start-api
example06-excel2csv$ curl http://localhost:3000/
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
example06-excel2csv$ sam logs -n HelloWorldFunction --stack-name example06-excel2csv --tail
```

You can find more information and examples about filtering Lambda function logs in the [SAM CLI Documentation](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-logging.html).

## Unit tests

Tests are defined in the `HelloWorldFunction/src/test` folder in this project.

```bash
example06-excel2csv$ cd HelloWorldFunction
HelloWorldFunction$ mvn test
```

## Cleanup

To delete the sample application that you created, use the AWS CLI. Assuming you used your project name for the stack name, you can run the following:

```bash
aws cloudformation delete-stack --stack-name example06-excel2csv
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