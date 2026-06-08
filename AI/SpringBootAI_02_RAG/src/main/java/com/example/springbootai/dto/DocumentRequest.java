package com.example.springbootai.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record DocumentRequest(
        @NotBlank(message = "Il campo 'content' è obbligatorio") String content,
        Map<String, Object> metadata,
        String collection   // opzionale — namespace (#11)
) {
    public DocumentRequest {
        if (metadata == null) metadata = Map.of();
    }
}
