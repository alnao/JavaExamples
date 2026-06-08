package com.example.springbootai.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RagRequest(
        @NotBlank(message = "Il campo 'question' è obbligatorio") String question,
        @Min(1) @Max(20) int topK,
        String sessionId,    // null o assente = nessuna memoria conversazionale
        String collection    // null o assente = nessun filtro collection (#11)
) {
    public RagRequest {
        if (topK == 0) topK = 4;
    }
}
