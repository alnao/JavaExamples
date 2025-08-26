# 💻 Java Maven Example03 - Visual Studio Code 🚀

**Progetto VS Code con AWS Toolkit** - Sviluppo moderno di Lambda Functions con template SAM HelloWorld e API Gateway integration.

## 🎯 Descrizione
Lambda Function completa creata con Visual Studio Code e AWS Toolkit che dimostra:
- ✅ **Template SAM HelloWorld** con API Gateway
- ✅ **Testing locale** con Docker
- ✅ **Deploy automatizzato** con CloudFormation
- ✅ **Check IP service** - Ritorna l'IP del chiamante
- ✅ **Debugging integrato** in VS Code

## 📋 Prerequisiti
- **Visual Studio Code**
- **AWS Toolkit for VS Code** extension
- **Docker** (per testing locale)
- Vedere i prerequisiti generali nel [README principale](../README.md)

## 🛠️ Setup VS Code

### 1️⃣ Installazione AWS Toolkit
1. Apri VS Code
2. Vai in Extensions (`Ctrl+Shift+X`)
3. Cerca "AWS Toolkit"
4. Installa l'extension ufficiale AWS
5. Riavvia VS Code

### 2️⃣ Configurazione credenziali
1. Apri Command Palette (`Ctrl+Shift+P`)
2. `AWS: Create Credentials Profile`
3. Inserisci Access Key, Secret Key, Region

## 🚀 Creazione progetto

### Nuovo progetto SAM
1. **Command Palette** (`Ctrl+Shift+P`)
2. `AWS: Create Lambda SAM Application`
3. Seleziona:
   - **Runtime**: `Java 11` (o superiore)
   - **Package type**: `Zip`
   - **Template**: `HelloWorld`
   - **Folder**: Cartella di destinazione
   - **Name**: `example03-vscode`

### Struttura generata
```
├── events/
│   └── event.json              # Evento di test
├── HelloWorldFunction/
│   ├── pom.xml                 # Dipendenze Maven
│   └── src/
│       ├── main/java/helloworld/
│       │   └── App.java        # Handler Lambda
│       └── test/java/helloworld/
│           └── AppTest.java    # Unit tests
├── template.yaml               # Template SAM
└── samconfig.toml             # Configurazioni deploy
```

## 🏗️ Build e Test

### Build del progetto
```bash
sam build
```

### Test locale
```bash
# Test singola funzione
sam local invoke HelloWorldFunction --event events/event.json

# Test API locale (porta 3000)
sam local start-api
curl http://localhost:3000/hello
```

### Test in VS Code
1. Apri `App.java`
2. Click destro → `AWS: Test Lambda Function (Local)`
3. Seleziona evento di test da `events/event.json`

## 📤 Deploy

### Deploy con SAM CLI
```bash
# Deploy guidato (prima volta)
sam deploy --guided

# Deploy successivi
sam deploy
```

**Parametri richiesti**:
- **Stack Name**: `example03-vscode`
- **AWS Region**: `eu-west-1`
- **Confirm changes**: `y`
- **Allow IAM creation**: `y`
- **Save to samconfig.toml**: `y`

### Deploy con VS Code Toolkit
1. **Command Palette** (`Ctrl+Shift+P`)
2. `AWS: Deploy SAM Application`
3. Seleziona `template.yaml`
4. Configura parametri deployment

**Output del deploy**:
```
Key                 HelloWorldApi
Description         API Gateway endpoint URL
Value               https://xxxxxxxxx.execute-api.eu-west-1.amazonaws.com/Prod/hello/
```

## 🧪 Testing

### Test API Gateway
```bash
# Test endpoint pubblico (dopo deploy)
curl https://your-api-id.execute-api.eu-west-1.amazonaws.com/Prod/hello/
```

**Response attesa**:
```json
{
  "message": "hello world",
  "location": "xxx.xxx.xxx.xxx"
}
```

### Unit tests
```bash
cd HelloWorldFunction
mvn test
```

### Debugging in VS Code
1. Imposta breakpoint in `App.java`
2. `Run and Debug` view (`Ctrl+Shift+D`)
3. Select `Debug SAM Local`
4. Avvia debugging

## 📊 Monitoraggio con VS Code

### AWS Explorer
1. Apri **AWS Explorer** nel sidebar
2. Espandi **Lambda** per vedere le funzioni
3. Click destro su funzione:
   - `Invoke on AWS` - Test diretto
   - `View Logs` - CloudWatch logs
   - `Download` - Scarica codice

### CloudWatch Logs
```bash
# Logs in tempo reale
sam logs -n HelloWorldFunction --stack-name example03-vscode --tail

# Logs filtrati
sam logs -n HelloWorldFunction --stack-name example03-vscode --filter "ERROR"
```

## 🔧 Configurazione avanzata

### Template SAM (template.yaml)
```yaml
AWSTemplateFormatVersion: '2010-09-09'
Transform: AWS::Serverless-2016-10-31

Globals:
  Function:
    Timeout: 20
    MemorySize: 512

Resources:
  HelloWorldFunction:
    Type: AWS::Serverless::Function
    Properties:
      CodeUri: HelloWorldFunction
      Handler: helloworld.App::handleRequest
      Runtime: java11
      Events:
        HelloWorld:
          Type: Api
          Properties:
            Path: /hello
            Method: get
```

### Handler personalizzato
```java
package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        // La tua logica qui
        return new APIGatewayProxyResponseEvent()
            .withStatusCode(200)
            .withBody("{\"message\": \"hello world\"}");
    }
}
```

## 🧹 Cleanup

### Rimozione stack
```bash
# Con SAM
sam delete

# Con AWS CLI
aws cloudformation delete-stack --stack-name example03-vscode

# Con VS Code
# AWS Explorer → CloudFormation → Right click → Delete Stack
```

## 💡 Tips per VS Code

### Shortcuts utili
- `Ctrl+Shift+P` - Command Palette
- `Ctrl+Shift+E` - Explorer
- `Ctrl+Shift+D` - Debug view
- `Ctrl+`` ` - Terminal integrato

### Extensions consigliate
- **AWS Toolkit** (essenziale)
- **Java Extension Pack**
- **Maven for Java**
- **Docker** (per container management)

### Configurazione workspace
Crea `.vscode/settings.json`:
```json
{
    "java.compile.nullAnalysis.mode": "automatic",
    "java.configuration.updateBuildConfiguration": "interactive",
    "aws.samcli.location": "/usr/local/bin/sam"
}
```

## 🔗 Riferimenti
- [AWS Toolkit for VS Code Guide](https://docs.aws.amazon.com/toolkit-for-vscode/latest/userguide/welcome.html)
- [SAM CLI Documentation](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
- [VS Code AWS Extensions](https://marketplace.visualstudio.com/items?itemName=AmazonWebServices.aws-toolkit-vscode)



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
example3-vscode$ sam build
```

The SAM CLI installs dependencies defined in `HelloWorldFunction/pom.xml`, creates a deployment package, and saves it in the `.aws-sam/build` folder.

Test a single function by invoking it directly with a test event. An event is a JSON document that represents the input that the function receives from the event source. Test events are included in the `events` folder in this project.

Run functions locally and invoke them with the `sam local invoke` command.

```bash
example3-vscode$ sam local invoke HelloWorldFunction --event events/event.json
```

The SAM CLI can also emulate your application's API. Use the `sam local start-api` to run the API locally on port 3000.

```bash
example3-vscode$ sam local start-api
example3-vscode$ curl http://localhost:3000/
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
example3-vscode$ sam logs -n HelloWorldFunction --stack-name example3-vscode --tail
```

You can find more information and examples about filtering Lambda function logs in the [SAM CLI Documentation](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-logging.html).

## Unit tests

Tests are defined in the `HelloWorldFunction/src/test` folder in this project.

```bash
example3-vscode$ cd HelloWorldFunction
HelloWorldFunction$ mvn test
```

## Cleanup

To delete the sample application that you created, use the AWS CLI. Assuming you used your project name for the stack name, you can run the following:

```bash
aws cloudformation delete-stack --stack-name example3-vscode
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