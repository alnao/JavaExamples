# SpringBootAI – Ollama + PGVector RAG Demo

Progetto Spring Boot **3.5.11** che dimostra come integrare:

- **Ollama** (LLM locale) tramite `spring-ai-ollama-spring-boot-starter`
- **PGVector** (ricerca semantica su PostgreSQL) tramite `spring-ai-pgvector-store-spring-boot-starter`

L'architettura implementa un pattern **RAG** (Retrieval-Augmented Generation) base.

---

## Stack tecnologico

| Componente | Versione |
|---|---|
| Java | 21 |
| Spring Boot | 3.5.11 |
| Spring AI BOM | 1.1.2 |
| PostgreSQL | 16 + pgvector |
| Ollama | latest |

---

## Prerequisiti

### 1. Ollama

Installa ed avvia Ollama: <https://ollama.com/download>

```bash
# Scarica i modelli necessari
ollama pull llama3.2          # modello chat
ollama pull nomic-embed-text  # modello embedding (768 dim)

# Verifica che Ollama giri su localhost:11434
ollama list
```

### 2. PostgreSQL + pgvector

#### Opzione A – Docker (consigliato)

```bash
docker compose up -d postgres
```

#### Opzione B – Installazione locale

```bash
# Ubuntu/Debian
sudo apt install postgresql postgresql-contrib
# Estensione pgvector
sudo apt install postgresql-16-pgvector

# Crea il database
psql -U postgres -c "CREATE DATABASE vectordb;"
```

---

## Configurazione

Il file di configurazione è [`src/main/resources/application.yml`](src/main/resources/application.yml).

I parametri principali da adattare al proprio ambiente:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/vectordb
    username: postgres
    password: postgres
  ai:
    ollama:
      base-url: http://localhost:11434
      chat.options.model: llama3.2
      embedding.options.model: nomic-embed-text
    vectorstore:
      pgvector:
        dimensions: 768        # deve corrispondere al modello embedding
```

> **Nota sulla versione Spring AI**: se Spring Boot 3.5.x richiede una versione Spring AI
> più recente di `1.0.0`, aggiorna la proprietà `spring-ai.version` nel `pom.xml` e consulta
> il [changelog di Spring AI](https://github.com/spring-projects/spring-ai/releases).

---

## Avvio

```bash
# 1. Avvia PostgreSQL (se non già attivo)
docker compose up -d postgres

# 2. Avvia l'applicazione
./mvnw spring-boot:run
```

L'app parte su `http://localhost:8081`.

---

## API REST

### Chat semplice
```bash
curl -s -X POST http://localhost:8080/api/ai/chat \
     -H "Content-Type: application/json" \
     -d '{"question": "Cos'\''è il machine learning?"}' | jq
```

### Chat con RAG (usa i documenti inseriti come contesto)
```bash
curl -s -X POST http://localhost:8080/api/ai/chat/rag \
     -H "Content-Type: application/json" \
     -d '{"question": "Dimmi qualcosa su Java", "topK": 4}' | jq
```

### Inserimento documento nel Vector Store
```bash
curl -s -X POST http://localhost:8080/api/ai/documents \
     -H "Content-Type: application/json" \
     -d '{
           "content": "Java è un linguaggio di programmazione ad oggetti creato da James Gosling nel 1995.",
           "metadata": {"fonte": "wikipedia", "categoria": "programmazione"}
         }' | jq
```

### Ricerca per similarità semantica
```bash
curl -s "http://localhost:8080/api/ai/search?query=linguaggio+programmazione&topK=3" | jq
```

### Health check
```bash
curl -s http://localhost:8080/actuator/health | jq
```

---

## Struttura del progetto

```
src/main/java/com/example/springbootai/
├── SpringBootAiApplication.java          # Entry point
├── controller/
│   └── AiController.java                 # REST endpoints /api/ai/*
└── service/
    └── AiService.java                    # Logica chat + RAG + vector store

src/main/resources/
├── application.yml                       # Configurazione Spring Boot + Spring AI
└── schema.sql                            # Schema SQL di riferimento (auto-init abilitato)

docker-compose.yml                        # PostgreSQL 16 + pgvector
```

---

## Architettura RAG

```
Utente ──► POST /chat/rag
              │
              ├─ 1. Embedding della domanda  (Ollama nomic-embed-text)
              │
              ├─ 2. Similarity search        (PGVector – HNSW coseno)
              │         └── top-K documenti rilevanti
              │
              ├─ 3. Costruisce prompt arricchito con il contesto
              │
              └─ 4. Invoca LLM              (Ollama llama3.2)
                         └── risposta finale
```







# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.
