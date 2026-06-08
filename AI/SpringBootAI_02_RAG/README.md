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

Copia `.env.example` in `.env` e modifica i valori:

```bash
cp .env.example .env
```

| Variabile | Default | Descrizione |
|-----------|---------|-------------|
| `SERVER_PORT` | `8081` | Porta HTTP dell'app |
| `DB_URL` | `jdbc:postgresql://localhost:5432/vectordb` | JDBC URL PostgreSQL |
| `DB_USERNAME` | `postgres` | Utente database |
| `DB_PASSWORD` | `postgres` | Password database |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5000,http://127.0.0.1:5500,http://localhost:3000` | Origini CORS ammesse (separate da virgola) |

> `.env` è in `.gitignore` e non viene committato. Solo `.env.example` (senza segreti reali) va nel repo.

Il file `application.yml` legge tutte le variabili con il pattern `${VAR:default}`, quindi l'app funziona anche senza `.env` per lo sviluppo locale.

---

## Avvio

```bash
# 1. Avvia PostgreSQL (se non già attivo)
docker compose up -d postgres

# 2. Carica le variabili d'ambiente e avvia l'app
export $(cat .env | grep -v '^#' | xargs) && ./mvnw spring-boot:run
# oppure con un IDE che supporta .env direttamente (IntelliJ, VS Code)
./mvnw spring-boot:run
```

L'app parte su `http://localhost:8081`.

---

## API REST

### Chat semplice
```bash
curl -s -X POST http://localhost:8081/api/ai/chat \
     -H "Content-Type: application/json" \
     -d '{"question": "Cos'\''è il machine learning?"}' | jq
```

### Chat con RAG (usa i documenti inseriti come contesto)
```bash
curl -s -X POST http://localhost:8081/api/ai/chat/rag \
     -H "Content-Type: application/json" \
     -d '{"question": "Dimmi qualcosa su Java", "topK": 4}' | jq
```

### Upload di un file nel Vector Store (PDF, DOCX, TXT, HTML…)
```bash
curl -s -X POST http://localhost:8081/api/ai/documents/upload \
     -F "file=@/percorso/al/file.pdf" | jq
```

### Inserimento documento testuale nel Vector Store
```bash
curl -s -X POST http://localhost:8081/api/ai/documents \
     -H "Content-Type: application/json" \
     -d '{
           "content": "Java è un linguaggio di programmazione ad oggetti creato da James Gosling nel 1995.",
           "metadata": {"fonte": "wikipedia", "categoria": "programmazione"}
         }' | jq
```

### Lista documenti indicizzati
```bash
curl -s http://localhost:8081/api/ai/documents | jq
```

### Ricerca per similarità semantica
```bash
curl -s "http://localhost:8081/api/ai/search?query=linguaggio+programmazione&topK=3" | jq
```

### Health check
```bash
curl -s http://localhost:8081/actuator/health | jq
```

---

## Web UI

La cartella `web/` contiene un client statico (HTML + Bootstrap 5 + FontAwesome) che non richiede build tool: basta aprire i file nel browser o servirli con qualsiasi server HTTP.

```bash
# Avvio rapido con Python
cd web && python3 -m http.server 3000
# oppure con Node
cd web && npx serve .
```

Poi apri `http://localhost:3000`.

| Pagina | File | Funzionalità |
|--------|------|--------------|
| Chat | `index.html` | Chat normale / RAG con toggle, slider topK, storico messaggi |
| Admin | `admin.html` | Lista documenti, upload file (drag&drop), inserimento testo libero |

Entrambe le pagine mostrano in navbar il badge **health** con polling ogni 30 s verso `/actuator/health`.

---

## Struttura del progetto

```
src/main/java/com/example/springbootai/
├── SpringBootAiApplication.java          # Entry point
├── config/
│   └── CorsConfig.java                   # CORS filter (origini da env var)
├── controller/
│   └── AiController.java                 # REST endpoints /api/ai/*
├── dto/
│   ├── ChatRequest.java                  # DTO chat semplice
│   ├── RagRequest.java                   # DTO chat RAG (question + topK)
│   └── DocumentRequest.java              # DTO inserimento documento
├── exception/
│   └── GlobalExceptionHandler.java       # Gestione errori centralizzata
└── service/
    └── AiService.java                    # Logica chat + RAG + vector store

src/main/resources/
├── application.yml                       # Configurazione Spring Boot + Spring AI
└── schema.sql                            # Schema SQL di riferimento (auto-init abilitato)

web/
├── index.html                            # Chat (normale + RAG)
├── admin.html                            # Admin: documenti, upload, testo
├── css/style.css                         # Tema scuro custom
└── js/
    ├── api.js                            # Wrapper fetch verso /api/ai/*
    ├── health.js                         # Polling /actuator/health
    ├── chat.js                           # Logica chat
    └── admin.js                          # Logica admin

docker-compose.yml                        # PostgreSQL 16 + pgvector
.env.example                              # Template variabili d'ambiente (committato)
.env                                      # Variabili locali con segreti (in .gitignore)
```

---

## Architettura RAG

```
Utente ──► POST /chat/rag
              │
              ├─ 1. QuestionAnswerAdvisor intercetta il prompt
              │         └── embedding della domanda (Ollama nomic-embed-text)
              │
              ├─ 2. Similarity search  (PGVector – HNSW coseno)
              │         └── top-K documenti rilevanti (filtro collection opzionale)
              │
              ├─ 3. Inietta contesto nel prompt con template italiano
              │
              └─ 4. Invoca LLM  (Ollama llama3.2)
                         └── risposta finale
```

---

## Feature implementate

| # | Feature | Descrizione |
|---|---------|-------------|
| 1 | Streaming NDJSON | Token per token via `application/x-ndjson`; ogni riga `{"t":"token"}` |
| 2 | Memoria multi-turn | `ConcurrentHashMap<sessionId, List<Message>>` con trim a 20 msg |
| 3 | Delete documento | `DELETE /documents?source=` rimuove tutti i chunk di un file |
| 4 | Similarity threshold | Configurabile via `RAG_SIMILARITY_THRESHOLD` (default 0.65) |
| 5 | QuestionAnswerAdvisor | Template italiano con `{question_answer_context}` / `{query}` |
| 6 | Re-ingestion / dedup | Prima dell'upload elimina i chunk esistenti, restituisce `replaced` |
| 7 | Chunk size configurabile | `RAG_CHUNK_SIZE` e `RAG_CHUNK_OVERLAP` in `.env` |
| 8 | Ingestion da URL | `POST /documents/url` — Tika legge pagine web e PDF remoti |
| 9 | Sessioni con ID | `sessionId` opzionale in tutte le richieste chat |
| 10 | Ingestion asincrona | `POST /documents/upload/async` → `jobId`; polling `GET /jobs/{id}` |
| 11 | Namespace / collection | Campo `collection` su ingestion, ricerca e chat RAG; filtro pgvector |
| 12 | API Key statica | Header `X-Api-Key` via `OncePerRequestFilter`; disabilitabile con env vuota |
| 13 | Test reali | `@WebMvcTest` + `@MockitoBean` — 14 test su tutti gli endpoint principali |
| 14 | Swagger / OpenAPI | `springdoc-openapi` 2.6.0 — UI su `/swagger-ui.html` |
| 15 | Docker completo | `docker compose up -d` avvia postgres + Ollama + app; override `local-ollama` per Ollama nativo |
| 16 | Metriche Micrometer | `@Timed` su chat/RAG/ingest/search — esposte su `/actuator/metrics` e `/actuator/prometheus` |

---

## Docker

### Avvio completo (Ollama nel container — default)

```bash
docker compose up -d

# Prima esecuzione: scarica i modelli nel container Ollama
docker compose exec ollama ollama pull llama3.2
docker compose exec ollama ollama pull nomic-embed-text
```

### Avvio con Ollama sul PC fisico

```bash
# Assicurati che Ollama giri su localhost:11434
docker compose -f docker-compose.yml -f docker-compose.local-ollama.yml up -d
```

Questa modalità disabilita il container `ollama` e punta l'app verso `host.docker.internal:11434`.

### Struttura dei compose file

| File | Scopo |
|------|-------|
| `docker-compose.yml` | Stack completo: postgres + ollama + app |
| `docker-compose.local-ollama.yml` | Override: disabilita container ollama, usa quello nativo |

---

## Endpoint principali

| Metodo | Path | Descrizione |
|--------|------|-------------|
| `POST` | `/api/ai/chat` | Chat sincrona senza RAG |
| `POST` | `/api/ai/chat/rag` | Chat RAG con QuestionAnswerAdvisor |
| `POST` | `/api/ai/chat/stream` | Chat streaming NDJSON |
| `POST` | `/api/ai/chat/rag/stream` | Chat RAG streaming NDJSON |
| `DELETE` | `/api/ai/sessions/{id}` | Cancella sessione conversazionale |
| `POST` | `/api/ai/documents/upload` | Upload sincrono (multipart/form-data) |
| `POST` | `/api/ai/documents/upload/async` | Upload asincrono → restituisce `jobId` |
| `GET` | `/api/ai/jobs/{jobId}` | Stato job di ingestion |
| `POST` | `/api/ai/documents/url` | Indicizza URL remoto |
| `POST` | `/api/ai/documents` | Inserisce testo libero |
| `GET` | `/api/ai/documents` | Lista documenti indicizzati |
| `DELETE` | `/api/ai/documents?source=` | Elimina documento per nome sorgente |
| `GET` | `/api/ai/search` | Ricerca semantica |
