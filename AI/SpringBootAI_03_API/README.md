# SpringBootAI – 03 AI API

Questo progetto dimostra un esempio pratico di agente AI in Spring Boot con Spring AI e Ollama.

L’idea non è solo fare RAG, ma far sì che il sistema:

- riceve un messaggio utente;
- decide se usare uno strumento;
- esegue una piccola azione locale (calcolo, lettura file, ricerca conoscenza);
- passa il risultato al modello per generare una risposta finale.

Questa è la naturale evoluzione del pattern RAG: da semplice recupero contesto a orchestrazione di tool e azioni.

## Stack

- Java 21
- Spring Boot 3.5.11
- Spring AI 1.1.2
- Ollama (modello `llama3.2`)
- Docker-ready

## Funzionalità implementate

- chat AI con sessione opzionale
- scelta automatica di tool in base al prompt
- calcolo matematico
- lettura di file e directory locale
- knowledge base inline per domande comuni
- risposta finale costruita con il risultato del tool
- endpoint REST documentato con Swagger

## Struttura del progetto

```text
src/main/java/com/example/springbootai/
├── SpringBootAiAgentApplication.java
├── controller/
│   └── AgentController.java
├── dto/
│   ├── AgentRequest.java
│   └── AgentResponse.java
└── service/
    └── AgentService.java

src/main/resources/
└── application.yml

web/
├── index.html
├── css/style.css
└── js/app.js
```

## Requisiti

1. Java 21
2. Maven
3. Ollama installato e in esecuzione
4. Modello scaricato:

```bash
ollama pull llama3.2
```

## Avvio


```bash
cd AI/SpringBootAI_03_Agent
mvn spring-boot:run
```

L’app si avvia di default su:

- http://localhost:8082

## Endpoint principali

### Chat agent

```bash
curl -X POST http://localhost:8082/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"calcola 12*7 e riassumilo","sessionId":"demo-1"}'
```

### Esempi di prompt

```bash
curl -X POST http://localhost:8082/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"dimmi cos'è un agent AI","sessionId":"demo-2"}'

curl -X POST http://localhost:8082/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"lista i file nella cartella di lavoro","sessionId":"demo-3"}'
```

## Web UI

Apri la cartella `web/` con un semplice server statico:

```bash
cd AI/SpringBootAI_03_Agent/web
python3 -m http.server 3000
```

Poi visita:

- http://localhost:3000

## Architettura del comportamento

```text
Utente ──► /api/agent/chat
              │
              ├─ 1. Analisi del prompt
              │
              ├─ 2. Sceglie lo strumento:
              │      • calculator
              │      • filesystem
              │      • knowledge
              │      • plain chat
              │
              ├─ 3. Esegue l'azione locale
              │
              └─ 4. Chiama LLM con il contesto ottenuto
                         └─ risposta finale
```

## Idea di evoluzione

Questo esempio è una base perfetta per estensioni future:

- tool calling reali con MCP
- chiamate HTTP esterne
- database query
- file PDF / DOCX ingestion
- orchestrazione multi-step
- agent planner con più tool

---





# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.
