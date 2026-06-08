package com.example.springbootai.service;

import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.UrlResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    private static final String RAG_TEMPLATE = """
            Usa il seguente contesto per rispondere alla domanda.
            Se non trovi la risposta nel contesto, rispondi con le tue conoscenze generali dicendolo chiaramente.

            CONTESTO:
            %s

            DOMANDA:
            %s
            """;

    private static final int MAX_HISTORY = 20;

    @Value("${rag.similarity-threshold:0.65}")
    private double similarityThreshold;

    @Value("${rag.chunk-size:512}")
    private int chunkSize;

    @Value("${rag.chunk-overlap:64}")
    private int chunkOverlap;

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final JdbcTemplate jdbc;

    private final Map<String, List<Message>> sessions = new ConcurrentHashMap<>();

    // #10 — job tracking per ingestion asincrona
    public record IngestionJob(String jobId, String status, String source, int chunks, int replaced, String error) {}
    private final Map<String, IngestionJob> jobs = new ConcurrentHashMap<>();

    /** Risultato di un'operazione di ingestion (sincrona). */
    public record IngestionResult(String source, int chunks, int replaced) {}

    public AiService(ChatClient.Builder chatClientBuilder,
                     VectorStore vectorStore,
                     JdbcTemplate jdbc) {
        this.chatClient  = chatClientBuilder.build();
        this.vectorStore = vectorStore;
        this.jdbc        = jdbc;
    }

    // ──────────────────────────────────────────────────────────────
    // CHAT SINCRONA (con memoria)
    // ──────────────────────────────────────────────────────────────

    @Timed(value = "ai.chat", description = "Latenza chat semplice")
    public String chat(String question, String sessionId) {
        List<Message> history = getSession(sessionId);
        history.add(new UserMessage(question));

        String answer = chatClient.prompt()
                .messages(new ArrayList<>(history))
                .call()
                .content();

        history.add(new AssistantMessage(answer));
        trimHistory(history);
        return answer;
    }

    @Timed(value = "ai.chat.rag", description = "Latenza chat RAG")
    public String chatWithContext(String question, int topK, String sessionId, String collection) {
        List<Document> context = searchSimilar(question, topK, collection);
        List<Message> history  = getSession(sessionId);

        log.info("RAG: trovati {} chunk per la domanda: '{}'", context.size(), question);

        List<Message> snapshot = buildSnapshot(history, question, context);
        history.add(new UserMessage(question));

        String answer = chatClient.prompt()
                .messages(snapshot)
                .call()
                .content();

        history.add(new AssistantMessage(answer));
        trimHistory(history);
        return answer;
    }

    // ──────────────────────────────────────────────────────────────
    // CHAT STREAMING (con memoria) — risposta token per token
    // ──────────────────────────────────────────────────────────────

    public Flux<String> chatStream(String question, String sessionId) {
        List<Message> history = getSession(sessionId);
        history.add(new UserMessage(question));

        List<Message> snapshot = new ArrayList<>(history);
        StringBuilder full     = new StringBuilder();

        return chatClient.prompt()
                .messages(snapshot)
                .stream()
                .content()
                .doOnNext(full::append)
                .doOnComplete(() -> {
                    history.add(new AssistantMessage(full.toString()));
                    trimHistory(history);
                });
    }

    public Flux<String> chatWithContextStream(String question, int topK, String sessionId, String collection) {
        List<Document> context = searchSimilar(question, topK, collection);
        List<Message> history  = getSession(sessionId);

        log.info("RAG stream: trovati {} chunk per la domanda: '{}'", context.size(), question);

        List<Message> snapshot = buildSnapshot(history, question, context);
        history.add(new UserMessage(question));

        StringBuilder full = new StringBuilder();

        return chatClient.prompt()
                .messages(snapshot)
                .stream()
                .content()
                .doOnNext(full::append)
                .doOnComplete(() -> {
                    history.add(new AssistantMessage(full.toString()));
                    trimHistory(history);
                });
    }

    // ──────────────────────────────────────────────────────────────
    // SESSIONI
    // ──────────────────────────────────────────────────────────────

    public void clearSession(String sessionId) {
        sessions.remove(sessionId);
        log.info("Sessione eliminata: {}", sessionId);
    }

    // ──────────────────────────────────────────────────────────────
    // INGESTION SINCRONA → VECTOR STORE
    // ──────────────────────────────────────────────────────────────

    @Timed(value = "ai.ingest.file", description = "Latenza ingestion file sincrona")
    public IngestionResult ingestFile(MultipartFile file, String collection) {
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown";
        log.info("Ingesting file: {} (collection={})", filename, collection);

        int replaced = deleteBySource(filename);
        if (replaced > 0) log.info("Replaced {} existing chunks for: {}", replaced, filename);

        final byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException("Errore lettura file: " + filename, e);
        }

        ByteArrayResource resource = new ByteArrayResource(bytes) {
            @Override public String getFilename() { return filename; }
        };

        List<Document> chunks = splitAndTag(new TikaDocumentReader(resource).get(), filename, collection);
        vectorStore.add(chunks);
        log.info("Ingested {} chunks from file: {}", chunks.size(), filename);
        return new IngestionResult(filename, chunks.size(), replaced);
    }

    // #10 — ingestion asincrona con tracking job
    @Async("ingestionExecutor")
    public CompletableFuture<IngestionResult> ingestFileAsync(String jobId, String filename, byte[] bytes, String collection) {
        jobs.put(jobId, new IngestionJob(jobId, "RUNNING", filename, 0, 0, null));
        try {
            int replaced = deleteBySource(filename);
            ByteArrayResource resource = new ByteArrayResource(bytes) {
                @Override public String getFilename() { return filename; }
            };
            List<Document> chunks = splitAndTag(new TikaDocumentReader(resource).get(), filename, collection);
            vectorStore.add(chunks);
            log.info("Async ingested {} chunks from file: {}", chunks.size(), filename);
            IngestionResult result = new IngestionResult(filename, chunks.size(), replaced);
            jobs.put(jobId, new IngestionJob(jobId, "DONE", result.source(), result.chunks(), result.replaced(), null));
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("Async ingestion failed for {}: {}", filename, e.getMessage());
            jobs.put(jobId, new IngestionJob(jobId, "ERROR", filename, 0, 0, e.getMessage()));
            return CompletableFuture.failedFuture(e);
        }
    }

    public IngestionJob getJob(String jobId) {
        return jobs.get(jobId);
    }

    public String startAsyncJob(String filename) {
        String jobId = UUID.randomUUID().toString();
        jobs.put(jobId, new IngestionJob(jobId, "PENDING", filename, 0, 0, null));
        return jobId;
    }

    @Timed(value = "ai.ingest.url", description = "Latenza ingestion URL")
    public IngestionResult ingestUrl(String url, Map<String, Object> metadata, String collection) {
        String sourceName = metadata.containsKey("source")
                ? metadata.get("source").toString()
                : url;
        log.info("Ingesting URL: {} (source={}, collection={})", url, sourceName, collection);

        int replaced = deleteBySource(sourceName);
        if (replaced > 0) log.info("Replaced {} existing chunks for: {}", replaced, sourceName);

        UrlResource resource;
        try {
            resource = new UrlResource(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("URL non valido: " + url, e);
        }

        List<Document> rawDocs = new TikaDocumentReader(resource).get();

        Map<String, Object> enriched = new HashMap<>(metadata);
        enriched.put("source", sourceName);
        enriched.put("url", url);
        if (collection != null && !collection.isBlank()) {
            enriched.put("collection", collection);
        }
        rawDocs.forEach(d -> d.getMetadata().putAll(enriched));

        List<Document> chunks = splitDocs(rawDocs);
        vectorStore.add(chunks);
        log.info("Ingested {} chunks from URL: {}", chunks.size(), url);
        return new IngestionResult(sourceName, chunks.size(), replaced);
    }

    public String addDocument(String content, Map<String, Object> metadata, String collection) {
        Map<String, Object> enriched = new HashMap<>(metadata);
        if (collection != null && !collection.isBlank()) {
            enriched.put("collection", collection);
        }
        Document doc = new Document(content, enriched);
        vectorStore.add(List.of(doc));
        return doc.getId();
    }

    // ──────────────────────────────────────────────────────────────
    // RICERCA + LISTING + DELETE
    // ──────────────────────────────────────────────────────────────

    @Timed(value = "ai.search", description = "Latenza similarity search")
    public List<Document> searchSimilar(String query, int topK, String collection) {
        SearchRequest.Builder builder = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .similarityThreshold(similarityThreshold);
        if (collection != null && !collection.isBlank()) {
            builder.filterExpression("collection == '" + collection + "'");
        }
        return vectorStore.similaritySearch(builder.build());
    }

    // #11 — lista con colonna collection
    public List<Map<String, Object>> listDocuments() {
        return jdbc.queryForList(
            "SELECT metadata->>'source' AS source, " +
            "       metadata->>'collection' AS collection, " +
            "       COUNT(*) AS chunks " +
            "FROM vector_store " +
            "WHERE metadata->>'source' IS NOT NULL " +
            "GROUP BY metadata->>'source', metadata->>'collection' " +
            "ORDER BY source"
        );
    }

    public int deleteBySource(String source) {
        log.info("Deleting chunks with source: {}", source);
        int deleted = jdbc.update(
            "DELETE FROM vector_store WHERE metadata->>'source' = ?", source
        );
        log.info("Deleted {} chunks for source: {}", deleted, source);
        return deleted;
    }

    public boolean deleteById(String id) {
        log.info("Deleting chunk id: {}", id);
        return jdbc.update("DELETE FROM vector_store WHERE id = ?::uuid", id) > 0;
    }

    // ──────────────────────────────────────────────────────────────
    // HELPERS PRIVATI
    // ──────────────────────────────────────────────────────────────

    private List<Message> getSession(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) return new ArrayList<>();
        return sessions.computeIfAbsent(sessionId, k -> new ArrayList<>());
    }

    private void trimHistory(List<Message> history) {
        while (history.size() > MAX_HISTORY) history.remove(0);
    }

    /** Costruisce la lista di messaggi da inviare al LLM:
     *  storico (senza ultima domanda) + domanda corrente aumentata con contesto RAG. */
    private List<Message> buildSnapshot(List<Message> history, String question, List<Document> context) {
        List<Message> snapshot = new ArrayList<>(history);
        if (context.isEmpty()) {
            snapshot.add(new UserMessage(question));
        } else {
            String contextText = context.stream()
                    .map(Document::getText)
                    .collect(java.util.stream.Collectors.joining("\n---\n"));
            snapshot.add(new UserMessage(RAG_TEMPLATE.formatted(contextText, question)));
        }
        return snapshot;
    }

    private List<Document> splitAndTag(List<Document> rawDocs, String source, String collection) {
        rawDocs.forEach(d -> {
            d.getMetadata().put("source", source);
            if (collection != null && !collection.isBlank()) {
                d.getMetadata().put("collection", collection);
            }
        });
        return splitDocs(rawDocs);
    }

    private List<Document> splitDocs(List<Document> rawDocs) {
        return new TokenTextSplitter(chunkSize, chunkOverlap, 5, 10000, true).apply(rawDocs);
    }
}
