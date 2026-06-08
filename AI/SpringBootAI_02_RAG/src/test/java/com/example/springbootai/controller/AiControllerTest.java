package com.example.springbootai.controller;

import com.example.springbootai.dto.ChatRequest;
import com.example.springbootai.dto.DocumentRequest;
import com.example.springbootai.dto.RagRequest;
import com.example.springbootai.service.AiService;
import com.example.springbootai.service.AiService.IngestionJob;
import com.example.springbootai.service.AiService.IngestionResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AiController.class)
@TestPropertySource(properties = "security.api-key=")   // disabilita ApiKeyFilter nei test
class AiControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    AiService aiService;

    // ── /chat ──────────────────────────────────────────────────────

    @Test
    void chat_returnsAnswer() throws Exception {
        when(aiService.chat(eq("ciao"), any())).thenReturn("risposta");

        mvc.perform(post("/api/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new ChatRequest("ciao", null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("risposta"));
    }

    @Test
    void chat_blankQuestion_returns400() throws Exception {
        mvc.perform(post("/api/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"question\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    // ── /chat/rag ──────────────────────────────────────────────────

    @Test
    void chatRag_returnsAnswer() throws Exception {
        when(aiService.chatWithContext(eq("chi è Dante?"), eq(4), any(), any()))
                .thenReturn("Dante Alighieri è un poeta italiano.");

        mvc.perform(post("/api/ai/chat/rag")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new RagRequest("chi è Dante?", 4, null, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("Dante Alighieri è un poeta italiano."));
    }

    @Test
    void chatRag_topKAboveMax_returns400() throws Exception {
        mvc.perform(post("/api/ai/chat/rag")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"question\":\"test\",\"topK\":99}"))
                .andExpect(status().isBadRequest());
    }

    // ── /chat/stream ───────────────────────────────────────────────

    @Test
    void chatStream_returnsNdjson() throws Exception {
        when(aiService.chatStream(eq("ciao"), any()))
                .thenReturn(Flux.just("tok1", "tok2"));

        mvc.perform(post("/api/ai/chat/stream")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new ChatRequest("ciao", null))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON));
    }

    // ── /documents ─────────────────────────────────────────────────

    @Test
    void listDocuments_returnsEmptyList() throws Exception {
        when(aiService.listDocuments()).thenReturn(List.of());

        mvc.perform(get("/api/ai/documents"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void listDocuments_returnsRows() throws Exception {
        when(aiService.listDocuments()).thenReturn(List.of(
                Map.of("source", "test.pdf", "chunks", 5L)
        ));

        mvc.perform(get("/api/ai/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].source").value("test.pdf"))
                .andExpect(jsonPath("$[0].chunks").value(5));
    }

    @Test
    void addDocument_returnsId() throws Exception {
        when(aiService.addDocument(eq("contenuto"), any(), any())).thenReturn("abc-123");

        mvc.perform(post("/api/ai/documents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new DocumentRequest("contenuto", null, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("abc-123"));
    }

    @Test
    void deleteBySource_found_returns200() throws Exception {
        when(aiService.deleteBySource("doc.pdf")).thenReturn(3);

        mvc.perform(delete("/api/ai/documents").param("source", "doc.pdf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(3));
    }

    @Test
    void deleteBySource_notFound_returns404() throws Exception {
        when(aiService.deleteBySource("nope.pdf")).thenReturn(0);

        mvc.perform(delete("/api/ai/documents").param("source", "nope.pdf"))
                .andExpect(status().isNotFound());
    }

    // ── /jobs ──────────────────────────────────────────────────────

    @Test
    void getJob_found_returnsStatus() throws Exception {
        when(aiService.getJob("job-1"))
                .thenReturn(new IngestionJob("job-1", "DONE", "file.pdf", 10, 0, null));

        mvc.perform(get("/api/ai/jobs/job-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.chunks").value(10));
    }

    @Test
    void getJob_notFound_returns404() throws Exception {
        when(aiService.getJob("missing")).thenReturn(null);

        mvc.perform(get("/api/ai/jobs/missing"))
                .andExpect(status().isNotFound());
    }

    // ── /search ────────────────────────────────────────────────────

    @Test
    void search_returnsResults() throws Exception {
        Document doc = new Document("testo di prova", Map.of("source", "doc.pdf"));
        when(aiService.searchSimilar(eq("query"), eq(3), any()))
                .thenReturn(List.of(doc));

        mvc.perform(get("/api/ai/search").param("query", "query").param("topK", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("testo di prova"));
    }

    // ── /sessions ──────────────────────────────────────────────────

    @Test
    void clearSession_returns204() throws Exception {
        mvc.perform(delete("/api/ai/sessions/sess-abc"))
                .andExpect(status().isNoContent());
    }
}
