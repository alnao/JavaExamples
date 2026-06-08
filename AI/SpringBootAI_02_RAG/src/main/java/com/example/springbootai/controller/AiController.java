package com.example.springbootai.controller;

import com.example.springbootai.dto.ChatRequest;
import com.example.springbootai.dto.DocumentRequest;
import com.example.springbootai.dto.RagRequest;
import com.example.springbootai.dto.UrlRequest;
import com.example.springbootai.service.AiService;
import com.example.springbootai.service.AiService.IngestionJob;
import com.example.springbootai.service.AiService.IngestionResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.ai.document.Document;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI", description = "Chat, RAG, streaming e gestione documenti nel vector store")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    // ──────────────────────────────────────────────────────────────
    // CHAT SINCRONA
    // ──────────────────────────────────────────────────────────────

    @Operation(summary = "Chat", description = "Risposta LLM senza contesto RAG. Supporta memoria multi-turn via sessionId.")
    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@Valid @RequestBody ChatRequest req) {
        return ResponseEntity.ok(Map.of("answer", aiService.chat(req.question(), req.sessionId())));
    }

    @Operation(summary = "Chat RAG", description = "QuestionAnswerAdvisor: recupera i topK chunk più simili e li inietta nel prompt. Supporta collection e memoria via sessionId.")
    @PostMapping("/chat/rag")
    public ResponseEntity<Map<String, String>> chatWithRag(@Valid @RequestBody RagRequest req) {
        return ResponseEntity.ok(Map.of("answer",
                aiService.chatWithContext(req.question(), req.topK(), req.sessionId(), req.collection())));
    }

    // ──────────────────────────────────────────────────────────────
    // CHAT STREAMING (NDJSON — token per token)
    // ──────────────────────────────────────────────────────────────

    @Operation(summary = "Chat streaming",
               description = "Risposta in streaming token per token (application/x-ndjson). Ogni riga è un JSON {\"t\":\"token\"}.")
    @PostMapping(value = "/chat/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Map<String, String>> chatStream(@Valid @RequestBody ChatRequest req) {
        return aiService.chatStream(req.question(), req.sessionId())
                .map(token -> Map.of("t", token));
    }

    @Operation(summary = "Chat RAG streaming",
               description = "RAG in streaming token per token (application/x-ndjson). Ogni riga è un JSON {\"t\":\"token\"}. Supporta collection.")
    @PostMapping(value = "/chat/rag/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Map<String, String>> chatRagStream(@Valid @RequestBody RagRequest req) {
        return aiService.chatWithContextStream(req.question(), req.topK(), req.sessionId(), req.collection())
                .map(token -> Map.of("t", token));
    }

    // ──────────────────────────────────────────────────────────────
    // SESSIONI
    // ──────────────────────────────────────────────────────────────

    @Operation(summary = "Cancella sessione", description = "Elimina la memoria conversazionale di una sessione")
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> clearSession(
            @Parameter(description = "ID della sessione da cancellare") @PathVariable String sessionId) {
        aiService.clearSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    // ──────────────────────────────────────────────────────────────
    // DOCUMENTI / VECTOR STORE
    // ──────────────────────────────────────────────────────────────

    @Operation(summary = "Upload file (sincrono)",
               description = "Indicizza un file via Apache Tika. Deduplication automatica. Accetta collection opzionale.")
    @PostMapping("/documents/upload")
    public ResponseEntity<Map<String, Object>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "collection", required = false) String collection) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File vuoto"));
        }
        IngestionResult r = aiService.ingestFile(file, collection);
        String msg = r.replaced() > 0
                ? "File re-indicizzato (sostituiti " + r.replaced() + " chunk precedenti)"
                : "File indicizzato con successo";
        return ResponseEntity.ok(Map.of(
                "source",   r.source(),
                "chunks",   r.chunks(),
                "replaced", r.replaced(),
                "message",  msg
        ));
    }

    // #10 — upload asincrono: restituisce subito il jobId, l'ingestion avviene in background
    @Operation(summary = "Upload file (asincrono)",
               description = "Avvia l'indicizzazione in background. Usa GET /jobs/{jobId} per monitorare lo stato.")
    @PostMapping("/documents/upload/async")
    public ResponseEntity<Map<String, Object>> uploadDocumentAsync(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "collection", required = false) String collection) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File vuoto"));
        }
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown";
        String jobId = aiService.startAsyncJob(filename);
        final byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (java.io.IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Errore lettura file"));
        }
        aiService.ingestFileAsync(jobId, filename, bytes, collection);
        return ResponseEntity.accepted().body(Map.of(
                "jobId",  jobId,
                "source", filename,
                "status", "PENDING"
        ));
    }

    // #10 — stato del job di ingestion
    @Operation(summary = "Stato job ingestion", description = "Restituisce lo stato di un job di ingestion asincrono")
    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<Map<String, Object>> getJobStatus(
            @Parameter(description = "ID del job restituito da /upload/async") @PathVariable String jobId) {
        IngestionJob job = aiService.getJob(jobId);
        if (job == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of(
                "jobId",    job.jobId(),
                "status",   job.status(),
                "source",   job.source(),
                "chunks",   job.chunks(),
                "replaced", job.replaced(),
                "error",    job.error() != null ? job.error() : ""
        ));
    }

    @Operation(summary = "Indicizza URL",
               description = "Scarica e indicizza una risorsa remota via Apache Tika. Deduplication automatica. Supporta collection.")
    @PostMapping("/documents/url")
    public ResponseEntity<Map<String, Object>> ingestUrl(@Valid @RequestBody UrlRequest req) {
        IngestionResult r = aiService.ingestUrl(req.url(), req.metadata(), req.collection());
        String msg = r.replaced() > 0
                ? "URL re-indicizzato (sostituiti " + r.replaced() + " chunk precedenti)"
                : "URL indicizzato con successo";
        return ResponseEntity.ok(Map.of(
                "source",   r.source(),
                "url",      req.url(),
                "chunks",   r.chunks(),
                "replaced", r.replaced(),
                "message",  msg
        ));
    }

    @Operation(summary = "Lista documenti", description = "Elenco dei file indicizzati con conteggio chunk e collection")
    @GetMapping("/documents")
    public ResponseEntity<List<Map<String, Object>>> listDocuments() {
        return ResponseEntity.ok(aiService.listDocuments());
    }

    @Operation(summary = "Inserisci testo", description = "Aggiunge testo libero al vector store. Supporta collection.")
    @PostMapping("/documents")
    public ResponseEntity<Map<String, String>> addDocument(@Valid @RequestBody DocumentRequest req) {
        String id = aiService.addDocument(req.content(), req.metadata(), req.collection());
        return ResponseEntity.ok(Map.of("id", id, "message", "Documento inserito"));
    }

    @Operation(summary = "Elimina documento", description = "Rimuove tutti i chunk di un file (per source)")
    @DeleteMapping("/documents")
    public ResponseEntity<Map<String, Object>> deleteBySource(
            @Parameter(description = "Nome sorgente (es. nomefile.pdf)") @RequestParam String source) {
        int deleted = aiService.deleteBySource(source);
        if (deleted == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("source", source, "deleted", deleted));
    }

    @Operation(summary = "Elimina chunk", description = "Rimuove un singolo chunk per UUID")
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "UUID del chunk") @PathVariable String id) {
        return aiService.deleteById(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Ricerca semantica", description = "Ricerca per similarità nel vector store. Supporta filtro collection.")
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int topK,
            @RequestParam(required = false) String collection) {

        List<Document> results = aiService.searchSimilar(query, topK, collection);
        List<Map<String, Object>> response = results.stream()
                .map(doc -> Map.<String, Object>of(
                        "id",         doc.getId(),
                        "content",    doc.getText(),
                        "metadata",   doc.getMetadata(),
                        "score",      doc.getScore() != null ? doc.getScore() : 0.0
                ))
                .toList();
        return ResponseEntity.ok(response);
    }
}
