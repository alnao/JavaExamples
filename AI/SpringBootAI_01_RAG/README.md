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
