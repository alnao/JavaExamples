# AWS CDK

Esempi di infrastruttura come codice con **AWS CDK in Java** (*in revisione*).

| Progetto | Descrizione |
|---|---|
| [CDK01bucketS3](CDK01bucketS3/) | Stack con un bucket S3 |
| [CDK02istanzeEC2](CDK02istanzeEC2/) | Stack con istanze EC2 |
| [CDK02istanzeEC2metadata](CDK02istanzeEC2metadata/) | Istanze EC2 con metadata/user-data |

Ogni risorsa deve riportare i tag standard (vedi [AWS/README.md](../README.md#convenzione-dei-tag)), con `ManagedBy = CDK` e `Name = aws-j-cdkXX-<servizio>`.

> ⚠️ Comandi che agiscono su un account AWS reale: possono creare risorse e generare costi.

## Tecnologie
- AWS CDK v2 (Java)
- Maven

## Bootstrap CDK (stack `CDKToolkitJava`)
Il bootstrap prepara l'account/regione per i deploy CDK e va eseguito **una sola volta per account e regione**. Nell'account esiste già lo stack `CDKToolkit` (qualifier di default `hnb659fds`) creato dal repository Python, quindi gli esempi Java usano un bootstrap separato:

| Risorsa | Nome |
|---|---|
| Stack CloudFormation | `CDKToolkitJava` |
| Qualifier (ruoli IAM `cdk-alnaojava-*`) | `alnaojava` |
| Bucket S3 degli asset | `alnao-dev-cdk-java` |
| Repository ECR | `alnao-dev-cdk-java` |
| Parametro SSM della versione | `/cdk-bootstrap-java/alnao/dev/version` |

> ⚠️ Comandi che agiscono su un account AWS reale. Il bootstrap crea bucket S3, repository ECR, ruoli IAM e un parametro SSM: costo quasi nullo (solo lo spazio degli asset su S3/ECR), ma le risorse restano finché non viene rimosso.

### 1. Template di bootstrap personalizzato
Il nome del repository ECR e del parametro SSM non si possono impostare da riga di comando, serve un template modificato:
```bash
cd AWS/CDK
set -a; source ../../.env; set +a
cdk bootstrap --show-template > bootstrap-template-java.yaml
# parametro SSM: /cdk-bootstrap/${Qualifier}/version -> /cdk-bootstrap-java/alnao/dev/version
sed -i 's#/cdk-bootstrap/${Qualifier}/version#/cdk-bootstrap-java/alnao/dev/version#' bootstrap-template-java.yaml
```
Poi nel file `bootstrap-template-java.yaml`, nella sezione `Parameters`, impostare il default del nome del repository ECR:
```yaml
  ContainerAssetsRepositoryName:
    ...
    Default: alnao-dev-cdk-java
```

### 2. Bootstrap con i tag standard
I tag dello stack vengono propagati da CloudFormation a tutte le risorse del bootstrap (`<Descrizione>` = `CDK`, risorse condivise da tutti gli esempi):
```bash
cdk bootstrap aws://$(aws sts get-caller-identity --query Account --output text)/eu-central-1 \
  --toolkit-stack-name CDKToolkitJava \
  --qualifier alnaojava \
  --bootstrap-bucket-name alnao-dev-cdk-java \
  --template bootstrap-template-java.yaml \
  --tags Name=aws-j-CDK --tags Environment=dev --tags Project=Development.aws.j-CDK \
  --tags Owner=AlNao --tags CostCenter=dev --tags ManagedBy=CDK


aws cloudformation describe-stacks --stack-name CDKToolkitJava --query 'Stacks[0].StackStatus'
aws ssm get-parameter --name /cdk-bootstrap-java/alnao/dev/version --query Parameter.Value
aws s3api get-bucket-tagging --bucket alnao-dev-cdk-java

```
I nomi dei bucket S3 sono globali: `alnao-dev-cdk-java` può esistere in **una sola regione**.

### 3. Configurazione degli stack
Ogni App deve usare lo stesso qualifier e gli stessi nomi, altrimenti CDK cerca il bootstrap di default (`hnb659fds`):
```java
new Cdk01BucketS3Stack(app, "Cdk01BucketS3Stack", StackProps.builder()
        .synthesizer(DefaultStackSynthesizer.Builder.create()
                .qualifier("alnaojava")
                .fileAssetsBucketName("alnao-dev-cdk-java")
                .imageAssetsRepositoryName("alnao-dev-cdk-java")
                .bootstrapStackVersionSsmParameter("/cdk-bootstrap-java/alnao/dev/version")
                .build())
        .build());
```
Negli esempi il synthesizer è già configurato nell'App e il `cdk.json` contiene `"toolkitStackName": "CDKToolkitJava"` e il context `@aws-cdk/core:bootstrapQualifier = alnaojava`, quindi basta:
```bash
cd CDK01bucketS3
cdk deploy
```

### 4. Rimozione del bootstrap
Prima distruggere tutti gli stack degli esempi (`cdk destroy` in ogni progetto), poi:
```bash
# svuota il bucket degli asset (versionato: oggetti e delete marker, max 1000 per chiamata)
aws s3api delete-objects --bucket alnao-dev-cdk-java --delete "$(aws s3api list-object-versions --bucket alnao-dev-cdk-java --query '{Objects: Versions[].{Key:Key,VersionId:VersionId}}' --output json)"
aws s3api delete-objects --bucket alnao-dev-cdk-java --delete "$(aws s3api list-object-versions --bucket alnao-dev-cdk-java --query '{Objects: DeleteMarkers[].{Key:Key,VersionId:VersionId}}' --output json)"
# elimina lo stack di bootstrap
aws cloudformation delete-stack --stack-name CDKToolkitJava
aws cloudformation wait stack-delete-complete --stack-name CDKToolkitJava
# risorse con DeletionPolicy Retain eventualmente rimaste
aws s3api delete-bucket --bucket alnao-dev-cdk-java
aws ecr delete-repository --repository-name alnao-dev-cdk-java --force
```
Se un comando `delete-objects` risponde che la lista è vuota, il bucket non ha versioni o delete marker da eliminare e si può proseguire.


## Changelog
| Data | Descrizione |
|------|-------------|
| 2026-11-23 | Creato README del progetto |
| 2026-11-23 | Aggiunta sezione bootstrap CDKToolkitJava con tag e rimozione |
| 2026-11-23 | Esempi configurati per il bootstrap CDKToolkitJava |


# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.
