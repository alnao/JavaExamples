package com.example.springbootai.controller;

import com.example.springbootai.dto.AgentRequest;
import com.example.springbootai.dto.AgentResponse;
import com.example.springbootai.service.AgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent")
@Tag(name = "Agent", description = "AI Agent con tool selection")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @Operation(summary = "Chat con agent", description = "Analizza il prompt, sceglie eventualmente uno strumento, e genera la risposta finale")
    @PostMapping("/chat")
    public ResponseEntity<AgentResponse> chat(@Valid @RequestBody AgentRequest request) {
        AgentResponse response = agentService.process(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Health", description = "Controllo semplice del servizio")
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
