# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Hard rules (set by the repository owner)

- **Stay inside the workspace.** Never run commands outside `/mnt/Dati4/Workspace/JavaExamples`: no reading, writing or executing anything in other paths. The only exception is the session scratchpad provided by Claude Code for temporary files.
- **Cloud commands only after explicit confirmation.** Before running `aws`, `az`, `cdk`, `sam`, `serverless` (or any other tool that acts on a cloud account, e.g. `gcloud`), show the exact command, say what it does (and whether it costs money or changes resources) and wait for the user's confirmation. Each command needs its own confirmation: a previous "yes" does not cover the next one. The same applies to running scripts or apps from this repo that call real cloud APIs through an SDK (AWS SDK for Java, Azure SDK), e.g. starting `AWS/ManagerFx` or `Azure/ManagerFx`, or invoking/deploying the `AWS/Lambda` and `AWS/CDK` projects.
- **Never run git commands**, read-only ones included (`git status`, `git diff`, `git log`, ...). To know what changed, read the files; commits are made by the user.
- **Every README.md must end with two sections, in this order:**
  1. `## Changelog` — add a row every time the user asks for a code change inside that folder; the description must be **max 10 words** (in Italian, like the rest of the README). Every folder with a build file (`pom.xml`, `package.json`, `template.yaml`, `cdk.json`), sub-modules included, and every section folder has its own README: update the README of the folder that actually contains the changed code. Format:
     ```markdown
     ## Changelog
     | Data | Descrizione |
     |------|-------------|
     | 2026-11-23 | Breve descrizione della modifica |
     ```
  2. **"alnao footer"** — always the same text (below). Never modify it; if a README does not have it, add it exactly as written.

     ```markdown
     # AlNao.it
     Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


     Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


     Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


     ## License
     Public projects 
     <a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
     *Free Software!*

     E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.
     ```

## AWS tagging convention (applies to every resource on AWS cloud)

Every resource created/managed in AWS that supports tags carries the standard tags of its example. `<Descrizione>` = the folder path after the `AWS` level, with `.` between subfolders: `AWS/CDK/CDK01bucketS3` becomes `CDK.CDK01bucketS3`, so Project = `Development.aws.j-CDK.CDK01bucketS3`. For folders outside `AWS` the first level is kept: `SpringBootOldVersions/Esempio04dbNoSql` becomes `SpringBootOldVersions.Esempio04dbNoSql`, `CorsiVari/AwsLambdaDynamoExamples/insert-user` becomes `CorsiVari.AwsLambdaDynamoExamples.insert-user`.

```
Name        = "aws-j-<Descrizione>"
Environment = "dev"
Project     = "Development.aws.j-<Descrizione>"
Owner       = "AlNao"
CostCenter  = "dev"
ManagedBy   = "Manual/Terraform/CloudFormation/Sh/CDK"
```

`ManagedBy` follows the tool that creates the resource: `Sh` for AWS CLI scripts/commands and SAM CLI (`sam deploy`, e.g. `AWS/Lambda`), `CDK` for CDK stacks, `CloudFormation` for templates deployed directly with CloudFormation, `Manual` for the console and for Java code using the AWS SDK (`AWS/Sdk`, `AWS/ManagerFx`). For the CDK examples (`AWS/CDK/CDK*`) `Name` is different for every resource and equals its name, `aws-j-cdkXX-<servizio>` (e.g. `aws-j-cdk01-bucket`); `Project` and the other tags stay as above. The convention is also documented for humans in [AWS/README.md](AWS/README.md#convenzione-dei-tag).

## AWS credentials

AWS credentials live only in the root `.env` (git-ignored; template in `.env.example`) as `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_REGION`, `AWS_DEFAULT_REGION`; load them with `set -a; source .env; set +a`. Never write keys into `application.properties`/`.yml` or code: Java code uses the SDK default credential provider chain (env vars locally, execution role on Lambda) and reads the region from `AWS_REGION`. Do not read `AWS_ACCESS_KEY_ID` into static/basic credentials inside Lambda code: on Lambda those env vars are temporary role credentials that also need `AWS_SESSION_TOKEN`.

## Repository overview

A collection of **independent** Java example projects by AlNao (docs and comments mostly in Italian). There is no root build: every project has its own `pom.xml` (~100 Maven projects), so always `cd` into the specific project folder before building. The root [README.md](README.md) holds the "Table of examples"; [ROADMAP.md](ROADMAP.md) tracks ongoing work and a "Completed" list — update both when adding a new example.

Top-level sections:
- `JavaFX/` — the actively developed area. `01`–`11` are progressive tutorials (single-class apps, `10` introduces FXML+CSS MVC). `96`–`99` are real tools: `96-PhotoCarousel`, `97-AlNaoShControlRoom`, `98-AlNaoPhotoDispatcher`, `99-AlNaoEditor`. Shared conventions in [JavaFX/README.md](JavaFX/README.md): JavaFX 21.0.1, Bootstrap 5 palette/card styling, package `it.alnao.javafx.<projectname>`.
- `AI/` — Spring Boot 3.5 + Spring AI with a local **Ollama** (`llama3.2`, `nomic-embed-text`). `01`/`02` are RAG demos backed by PostgreSQL+pgvector (via `docker compose`), `03` is an agent API; `02`/`03` include a static `web/` UI.
- `AWS/` — `ManagerFx` (JavaFX console using AWS SDK, one service class per AWS product in `service/impl/`, aggregated by `AwsResourceService`), `Lambda` (SAM projects), `CDK`, `Sdk`. `Azure/ManagerFx` mirrors the AWS console structure.
- `Gaming/`, `GenericJava/`, `CorsiVari/`, `SpringBootOldVersions/` — older examples kept "for nostalgia"; avoid modernizing them unless asked.

## Architecture of the larger JavaFX apps (96–98)

They share a layered layout under `it.alnao.javafx.<app>/`: `<App>App.java` (Application entry) → `controller/` (UI built in code, not FXML) → `service/` (`ConfigService`, I/O, process handling) → `model/`. Configuration is persisted in the user's home, not in the repo:
- `97-AlNaoShControlRoom`: `~/.alnaoShControlRoom/config.json` (Jackson → `AppConfig` with `MonitorEntry` URLs for the header status indicators and `TabConfig`/`ScriptEntry` for the per-tab shell-script buttons). `StatusChecker` polls URLs, `ScriptRunner` runs/stops one `.sh` per tab; styling in `control-room-bootstrap.css`.
- `98-AlNaoPhotoDispatcher`: properties under `~/.alnaoPhotoDispatcher/`.
- `96-PhotoCarousel`: JSON config via `ConfigService`.

## Common commands

```bash
# JavaFX projects (never run with plain `java -jar`: JavaFX runtime would be missing)
mvn clean compile
mvn javafx:run

# Projects with run.sh (96, 97, 98): builds the jar on first run (target/lib holds JavaFX modules), then launches it
./run.sh

# Spring Boot projects (use ./mvnw when present)
mvn spring-boot:run
# AI/SpringBootAI_02_RAG: needs Ollama on localhost:11434 and Postgres
docker compose up -d postgres
export $(cat .env | grep -v '^#' | xargs) && ./mvnw spring-boot:run

# Tests (only where src/test exists, e.g. AI/SpringBootAI_0{1,2}_RAG, SpringBootOldVersions/*, GenericJava/14testJUnit5)
mvn test
mvn test -Dtest=AiControllerTest            # single class
mvn test -Dtest=AiControllerTest#methodName # single method
```

Java versions vary per project (17 for most JavaFX/ManagerFx, 21 for `97` and the `AI` projects): check `maven.compiler.source` / `java.version` in the project's `pom.xml`. There is no linter configured.
