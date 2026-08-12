package com.example.springbootai.service;

import com.example.springbootai.dto.AgentRequest;
import com.example.springbootai.dto.AgentResponse;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AgentService {

    private final ChatModel chatModel;
    private final Map<String, List<String>> sessionHistory = new ConcurrentHashMap<>();

    public AgentService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public AgentResponse process(AgentRequest request) {
        String sessionId = request.sessionId() != null ? request.sessionId() : "default";
        String normalized = request.message().trim();

        ToolResult toolResult = selectToolAndExecute(normalized);

        List<String> history = sessionHistory.computeIfAbsent(sessionId, k -> new ArrayList<>());
        history.add("User: " + normalized);

        String prompt = buildPrompt(normalized, toolResult);
        String answer = chatModel.call(new Prompt(prompt)).getResult().getOutput().getText();

        history.add("Assistant: " + answer);
        trimHistory(history);

        return new AgentResponse(answer, toolResult.toolName(), sessionId);
    }

    private ToolResult selectToolAndExecute(String message) {
        String lower = message.toLowerCase(Locale.ROOT);

        if (lower.contains("calcola") || lower.contains("somma") || lower.contains("moltiplica") || lower.contains("dividi") || lower.contains("+")) {
            return new ToolResult("calculator", runCalculator(message));
        }

        if (lower.contains("lista") && (lower.contains("file") || lower.contains("cartella") || lower.contains("directory"))) {
            return new ToolResult("filesystem", listWorkspace());
        }

        if (lower.contains("agent ai") || lower.contains("cos'è un agent") || lower.contains("definizione") || lower.contains("ai agent")) {
            return new ToolResult("knowledge", knowledgeBase());
        }

        return new ToolResult("chat", "Nessuno strumento richiesto. Risposta generica dal modello.");
    }

    private String runCalculator(String message) {
        String sanitized = message.replace(',', '.');
        sanitized = sanitized.replaceAll("[^0-9+\\-*/().\\s]", " ");
        String expression = sanitized.replaceAll("\\s+", "").trim();

        if (expression.isBlank()) {
            return "Nessuna espressione matematica trovata.";
        }

        try {
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
            if (engine == null) {
                return "Motore JavaScript non disponibile nel runtime.";
            }

            Object result = engine.eval(expression);
            return "Espressione: " + expression + "\nRisultato: " + result;
        } catch (Exception e) {
            return "Non ho potuto valutare l'espressione: " + message + "\nMotivo: " + e.getMessage();
        }
    }

    private String listWorkspace() {
        try {
            Path workspace = Path.of(System.getProperty("user.dir"));
            List<String> names = Files.list(workspace)
                    .limit(20)
                    .map(path -> path.getFileName().toString())
                    .sorted()
                    .toList();

            return "Directory: " + workspace + "\nElementi: " + names;
        } catch (IOException e) {
            return "Errore durante la lettura della cartella: " + e.getMessage();
        }
    }

    private String knowledgeBase() {
        return "Un AI agent è un sistema che riceve un compito, usa strumenti o dati esterni, decide quale azione fare, esegue l'azione e infine produce una risposta utile. " +
                "In pratica, è più di un semplice chatbot: combina ragionamento, strumenti e contesto esterno.";
    }

    private String buildPrompt(String userMessage, ToolResult toolResult) {
        return "Sei un agente AI esperto. " +
                "Rispondi in italiano. " +
                "Usa il contesto fornito dal tool se presente. " +
                "Se non è presente, rispondi normalmente. " +
                "\n\nDomanda dell'utente: " + userMessage + "\n" +
                "\nContesto tool: " + toolResult.output() + "\n" +
                "\nIstruzione: rispondi in modo chiaro, pratico e conciso.";
    }

    private void trimHistory(List<String> history) {
        if (history.size() > 12) {
            history.subList(0, history.size() - 12).clear();
        }
    }

    private record ToolResult(String toolName, String output) {
    }
}
