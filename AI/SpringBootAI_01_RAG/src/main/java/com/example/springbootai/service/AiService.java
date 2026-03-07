package com.example.springbootai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Servizio principale per le operazioni AI:
 *  - Chat via Ollama
 *  - Ingestion di file (PDF, DOCX, TXT…) nel PGVector store tramite Apache Tika
 *  - Ricerca per similarità semantica (RAG)
 */
@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final JdbcTemplate jdbc;

    public AiService(ChatClient.Builder chatClientBuilder,
                     VectorStore vectorStore,
                     JdbcTemplate jdbc) {
        this.chatClient  = chatClientBuilder.build();
        this.vectorStore = vectorStore;
        this.jdbc        = jdbc;
    }

    // ──────────────────────────────────────────────────────────────
    // CHAT
    // ──────────────────────────────────────────────────────────────

    /** Chat semplice senza contesto. */
    public String chat(String question) {
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }

    /**
     * Chat RAG: recupera i topK chunk più simili dal vector store
     * e li inietta come contesto nel prompt prima di invocare il LLM.
     */
    public String chatWithContext(String question, int topK) {
        List<Document> context = searchSimilar(question, topK);

        String contextText = context.stream()
                .map(Document::getText)
                .reduce("", (a, b) -> a + "\n---\n" + b);

        String prompt = """
                Usa il seguente contesto per rispondere alla domanda.
                Se non trovi la risposta nel contesto, rispondi con le tue conoscenze generali dicendolo chiaramente.

                CONTESTO:
                %s

                DOMANDA:
                %s
                """.formatted(contextText, question);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    // ──────────────────────────────────────────────────────────────
    // INGESTION FILE → VECTOR STORE
    // ──────────────────────────────────────────────────────────────

    /**
     * Legge un file caricato via MultipartFile, lo processa con Apache Tika
     * (supporta PDF, DOCX, TXT, HTML, …), lo divide in chunk da 512 token
     * con overlap di 64, calcola gli embedding e li salva nel PGVector store.
     *
     * @param file file caricato dall'utente
     * @return numero di chunk salvati nel vector store
     */
    public int ingestFile(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown";
        log.info("Ingesting file: {}", filename);

        // Wrap il MultipartFile in una ByteArrayResource con nome (Tika usa il nome per auto-detect MIME)
        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() { return filename; }
        };

        // Lettura con Apache Tika (estrae testo grezzo da qualsiasi formato supportato)
        TikaDocumentReader reader = new TikaDocumentReader(resource);
        List<Document> rawDocs   = reader.get();

        // Aggiungi metadati sorgente ad ogni documento
        rawDocs.forEach(d -> d.getMetadata().put("source", filename));

        // Splitting in chunk con overlap per non spezzare frasi a metà
        TokenTextSplitter splitter = new TokenTextSplitter(512, 64, 5, 10000, true);
        List<Document> chunks      = splitter.apply(rawDocs);

        // Salva nel vector store (embedding calcolato automaticamente da Ollama)
        vectorStore.add(chunks);

        log.info("Ingested {} chunks from file: {}", chunks.size(), filename);
        return chunks.size();
    }

    /**
     * Aggiunge un singolo documento testuale al vector store (senza chunking).
     */
    public String addDocument(String content, Map<String, Object> metadata) {
        Document doc = new Document(content, metadata);
        vectorStore.add(List.of(doc));
        return doc.getId();
    }

    // ──────────────────────────────────────────────────────────────
    // RICERCA + LISTING
    // ──────────────────────────────────────────────────────────────

    /** Ricerca semantica nel vector store. */
    public List<Document> searchSimilar(String query, int topK) {
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .build()
        );
    }

    /**
     * Restituisce la lista di file (source) distinti presenti nel vector store.
     */
    public List<Map<String, Object>> listDocuments() {
        return jdbc.queryForList(
            "SELECT metadata->>'source' AS source, COUNT(*) AS chunks " +
            "FROM vector_store " +
            "WHERE metadata->>'source' IS NOT NULL " +
            "GROUP BY metadata->>'source' " +
            "ORDER BY source"
        );
    }
}
