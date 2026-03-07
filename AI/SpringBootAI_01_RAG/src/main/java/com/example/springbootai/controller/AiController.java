package com.example.springbootai.controller;

import com.example.springbootai.service.AiService;
import org.springframework.ai.document.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * REST Controller che espone le API di AI (chat, RAG, document ingestion).
 *
 * Base URL: /api/ai
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    // ──────────────────────────────────────────────────────────────
    // CHAT
    // ──────────────────────────────────────────────────────────────

    /** POST /api/ai/chat – Chat semplice senza contesto RAG. */
    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> body) {
        String question = body.getOrDefault("question", "");
        if (question.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Il campo 'question' è obbligatorio"));
        }
        return ResponseEntity.ok(Map.of("answer", aiService.chat(question)));
    }

    /** POST /api/ai/chat/rag – Chat con contesto RAG dal vector store. */
    @PostMapping("/chat/rag")
    public ResponseEntity<Map<String, String>> chatWithRag(@RequestBody Map<String, Object> body) {
        String question = (String) body.getOrDefault("question", "");
        if (question.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Il campo 'question' è obbligatorio"));
        }
        int topK = body.containsKey("topK") ? ((Number) body.get("topK")).intValue() : 4;
        return ResponseEntity.ok(Map.of("answer", aiService.chatWithContext(question, topK)));
    }

    // ──────────────────────────────────────────────────────────────
    // DOCUMENTI / VECTOR STORE
    // ──────────────────────────────────────────────────────────────

    /**
     * POST /api/ai/documents/upload
     * Upload di un file (PDF, DOCX, TXT…) che viene letto con Tika,
     * spezzato in chunk e salvato nel PGVector store.
     */
    @PostMapping("/documents/upload")
    public ResponseEntity<Map<String, Object>> uploadDocument(
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "File vuoto"));
        }
        try {
            int chunks = aiService.ingestFile(file);
            return ResponseEntity.ok(Map.of(
                    "filename", file.getOriginalFilename(),
                    "chunks",   chunks,
                    "message",  "File indicizzato con successo"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /api/ai/documents – Lista dei file indicizzati nel vector store. */
    @GetMapping("/documents")
    public ResponseEntity<List<Map<String, Object>>> listDocuments() {
        return ResponseEntity.ok(aiService.listDocuments());
    }

    /** POST /api/ai/documents – Inserisce testo libero nel vector store. */
    @PostMapping("/documents")
    public ResponseEntity<Map<String, String>> addDocument(@RequestBody Map<String, Object> body) {
        String content = (String) body.getOrDefault("content", "");
        if (content.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Il campo 'content' è obbligatorio"));
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = body.containsKey("metadata")
                ? (Map<String, Object>) body.get("metadata")
                : Map.of();
        String id = aiService.addDocument(content, metadata);
        return ResponseEntity.ok(Map.of("id", id, "message", "Documento inserito"));
    }

    /** GET /api/ai/search – Ricerca semantica nel vector store. */
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int topK) {

        List<Document> results = aiService.searchSimilar(query, topK);
        List<Map<String, Object>> response = results.stream()
                .map(doc -> Map.<String, Object>of(
                        "id",       doc.getId(),
                        "content",  doc.getText(),
                        "metadata", doc.getMetadata(),
                        "score",    doc.getScore() != null ? doc.getScore() : 0.0
                ))
                .toList();
        return ResponseEntity.ok(response);
    }
}
