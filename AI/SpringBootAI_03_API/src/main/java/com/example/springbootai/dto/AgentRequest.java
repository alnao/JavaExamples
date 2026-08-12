package com.example.springbootai.dto;

import jakarta.validation.constraints.NotBlank;

public record AgentRequest(
        @NotBlank(message = "Il messaggio non può essere vuoto") String message,
        String sessionId
) {
}
