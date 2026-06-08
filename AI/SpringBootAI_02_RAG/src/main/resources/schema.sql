-- Questo file è per riferimento: con spring.ai.vectorstore.pgvector.initialize-schema=true
-- Spring AI crea automaticamente la tabella. Puoi usarlo per un'inizializzazione manuale.

-- Abilita l'estensione pgvector (richiede PostgreSQL >= 14 + pgvector installato)
CREATE EXTENSION IF NOT EXISTS vector;

-- Tabella usata da Spring AI PGVector store
CREATE TABLE IF NOT EXISTS vector_store (
    id        uuid NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    content   text,
    metadata  json,
    embedding vector(768)      -- deve corrispondere a spring.ai.vectorstore.pgvector.dimensions
);

-- Indice HNSW per ricerche per similarità coseno veloci
CREATE INDEX IF NOT EXISTS vector_store_embedding_idx
    ON vector_store
    USING hnsw (embedding vector_cosine_ops);
