package com.example.springbootai.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
        @NotBlank(message = "Il campo 'question' è obbligatorio") String question,
        String sessionId   // null o assente = nessuna memoria conversazionale
) {}
