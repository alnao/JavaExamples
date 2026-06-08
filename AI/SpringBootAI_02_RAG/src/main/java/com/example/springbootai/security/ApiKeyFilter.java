package com.example.springbootai.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Verifica l'header X-Api-Key su tutte le richieste /api/**.
 * Se API_KEY è vuota (default dev) il filtro è disabilitato.
 * Actuator, Swagger e preflight OPTIONS sono sempre esclusi.
 *
 * Order(1) garantisce che parta DOPO CorsFilter (HIGHEST_PRECEDENCE = -2147483648),
 * così le risposte 401/403 contengono già gli header CORS aggiunti dal filtro precedente.
 */
@Component
@Order(1)
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-Api-Key";

    @Value("${security.api-key:}")
    private String configuredKey;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // filtro disabilitato se la chiave non è configurata
        if (configuredKey == null || configuredKey.isBlank()) return true;

        String path = request.getRequestURI();
        String method = request.getMethod();

        return method.equalsIgnoreCase("OPTIONS")
                || path.startsWith("/actuator")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String key = request.getHeader(HEADER);
        if (configuredKey.equals(key)) {
            chain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"API key mancante o non valida\"}");
        }
    }
}
