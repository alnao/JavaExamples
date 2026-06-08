package com.example.springbootai.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record UrlRequest(
        @NotBlank(message = "Il campo 'url' è obbligatorio") String url,
        Map<String, Object> metadata,  // opzionale; metti "source" per sovrascrivere il nome
        String collection              // opzionale — namespace (#11)
) {
    public UrlRequest {
        if (metadata == null) metadata = Map.of();
    }
}
