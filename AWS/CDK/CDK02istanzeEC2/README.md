# Welcome to your CDK Java project!

This is a blank project for CDK development with Java.

The `cdk.json` file tells the CDK Toolkit how to execute your app.

It is a [Maven](https://maven.apache.org/) based project, so you can open this project with any Maven compatible Java IDE to build and run tests.

## Useful commands

 * `mvn package`     compile and run tests
 * `cdk ls`          list all stacks in the app
 * `cdk synth`       emits the synthesized CloudFormation template
 * `cdk deploy`      deploy this stack to your default AWS account/region
 * `cdk diff`        compare deployed stack with current state
 * `cdk docs`        open CDK documentation

Enjoy!



## Coomand
cdk init app --language java
mvn package
cdk synth -> per generare il template CloudFormation
cdk deploy -> per distribuire lo stack su AWS

## Parametri e deploy
L'istanza viene creata in una subnet pubblica della **VPC di default** dell'account/regione: il lookup avviene al `cdk synth` (serve l'`env` dello stack, preso da `CDK_DEFAULT_ACCOUNT`/`CDK_DEFAULT_REGION`) e il risultato viene salvato in `cdk.context.json`. AMI: ultima Amazon Linux 2.

| Nome | Tipo | Default | Descrizione |
|---|---|---|---|
| `instanceType` | context (`-c`) | `t2.micro` | tipo di istanza |
| `KeyName` | parametro CloudFormation | — (obbligatorio) | key pair EC2 esistente |
| `AllowedCIDR` | parametro CloudFormation | `0.0.0.0/0` | IP ammessi su SSH e HTTP |

```bash
cdk deploy -c instanceType=t2.micro --parameters KeyName=<nome-key-pair> --parameters AllowedCIDR=<mio-ip>/32
```

## Tag
Tutte le risorse dello stack hanno i tag standard (vedi [AWS/README.md](../../README.md#convenzione-dei-tag)): i tag comuni sono applicati nell'App con `Tags.of(app)`, il tag `Name` su ogni risorsa (`aws-j-cdkXX-<servizio>`).

Credenziali e regione dal `.env` nella root del repository:
```bash
set -a; source ../../../.env; set +a
cdk synth
```


## Changelog
| Data | Descrizione |
|------|-------------|
| 2026-11-23 | Aggiunti changelog e footer standard |
| 2026-11-23 | Aggiunti tag standard a stack e risorse |
| 2026-11-23 | Synthesizer e cdk.json per bootstrap CDKToolkitJava |
| 2026-11-23 | VPC di default, instanceType da context, AMI Amazon Linux 2 |


# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.
