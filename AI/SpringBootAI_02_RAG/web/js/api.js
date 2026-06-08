// CONFIG definito in config.js

function authHeaders(extra = {}) {
  const h = { ...extra };
  if (CONFIG.API_KEY) h['X-Api-Key'] = CONFIG.API_KEY;
  return h;
}

// Timeout di INATTIVITÀ per lo streaming: lo stream viene abortito solo se
// non arriva nessun dato per questo numero di ms. Una generazione lenta ma
// viva (es. llama3.2 su CPU con un PDF in RAG) non viene uccisa.
const STREAM_IDLE_TIMEOUT_MS = 90_000;   // 90s senza alcun token = abort
const STREAM_FIRST_TOKEN_MS  = 180_000;  // 180s per il primo token (Ollama deve caricare il modello)

// Esegue una fetch streaming con AbortController + timer di inattività.
// Ritorna il testo completo accumulato.
async function fetchNdjsonStream(url, payload, onToken) {
  const controller = new AbortController();
  let idleTimer = null;
  let firstTokenSeen = false;

  const armTimer = (ms) => {
    clearTimeout(idleTimer);
    idleTimer = setTimeout(() => controller.abort(new DOMException(
      firstTokenSeen
        ? 'Nessuna risposta dal modello negli ultimi 90s — generazione interrotta.'
        : 'Il modello non ha risposto entro 180s. Ollama potrebbe star caricando il modello: riprova.',
      'TimeoutError'
    )), ms);
  };

  let r;
  try {
    armTimer(STREAM_FIRST_TOKEN_MS);
    r = await fetch(url, {
      method: 'POST',
      headers: authHeaders({ 'Content-Type': 'application/json' }),
      body: JSON.stringify(payload),
      signal: controller.signal,
    });
  } catch (err) {
    clearTimeout(idleTimer);
    if (err.name === 'TimeoutError' || err.name === 'AbortError') throw new Error(err.message);
    throw err;
  }

  if (!r.ok) {
    clearTimeout(idleTimer);
    let msg = `HTTP ${r.status}`;
    try { msg = (await r.json()).error || msg; } catch {}
    throw new Error(msg);
  }

  const reader  = r.body.getReader();
  const decoder = new TextDecoder();
  let buffer = '';
  let full   = '';

  const handleLine = (line) => {
    if (!line.trim()) return;
    try {
      const obj = JSON.parse(line);
      if (obj.t !== undefined) {
        if (!firstTokenSeen) firstTokenSeen = true;
        full += obj.t;
        onToken(obj.t, full);
      }
    } catch { /* riga malformata — ignora */ }
  };

  try {
    while (true) {
      const { done, value } = await reader.read();
      if (done) break;
      armTimer(STREAM_IDLE_TIMEOUT_MS);   // ogni chunk ricevuto resetta il timer di inattività
      buffer += decoder.decode(value, { stream: true });

      const lines = buffer.split('\n');
      buffer = lines.pop() ?? '';
      for (const line of lines) handleLine(line);
    }
    if (buffer.trim()) handleLine(buffer);
  } catch (err) {
    if (controller.signal.aborted) {
      throw new Error(controller.signal.reason?.message || 'Streaming interrotto per timeout.');
    }
    throw err;
  } finally {
    clearTimeout(idleTimer);
    reader.releaseLock();
  }
  return full;
}

const api = {
  async health() {
    const r = await fetch(`${CONFIG.API_BASE}/actuator/health`);
    return r.json();
  },

  // ── Chat sincrona ──
  async chat(question, sessionId) {
    const r = await fetch(`${CONFIG.API_BASE}/api/ai/chat`, {
      method: 'POST',
      headers: authHeaders({ 'Content-Type': 'application/json' }),
      body: JSON.stringify({ question, sessionId }),
    });
    if (!r.ok) throw new Error((await r.json()).error || `HTTP ${r.status}`);
    return r.json();
  },

  async chatRag(question, topK = 4, sessionId, collection) {
    const r = await fetch(`${CONFIG.API_BASE}/api/ai/chat/rag`, {
      method: 'POST',
      headers: authHeaders({ 'Content-Type': 'application/json' }),
      body: JSON.stringify({ question, topK, sessionId, collection }),
    });
    if (!r.ok) throw new Error((await r.json()).error || `HTTP ${r.status}`);
    return r.json();
  },

  // ── Chat streaming ──
  async chatStream(question, sessionId, onToken) {
    return fetchNdjsonStream(`${CONFIG.API_BASE}/api/ai/chat/stream`,
      { question, sessionId }, onToken);
  },

  async chatRagStream(question, topK = 4, sessionId, collection, onToken) {
    return fetchNdjsonStream(`${CONFIG.API_BASE}/api/ai/chat/rag/stream`,
      { question, topK, sessionId, collection }, onToken);
  },

  // ── Sessioni ──
  async clearSession(sessionId) {
    const r = await fetch(`${CONFIG.API_BASE}/api/ai/sessions/${encodeURIComponent(sessionId)}`, {
      method: 'DELETE',
      headers: authHeaders(),
    });
    if (!r.ok && r.status !== 404) throw new Error(`HTTP ${r.status}`);
  },

  // ── Documenti ──
  async listDocuments() {
    const r = await fetch(`${CONFIG.API_BASE}/api/ai/documents`, {
      headers: authHeaders(),
    });
    if (!r.ok) throw new Error(`HTTP ${r.status}`);
    return r.json();
  },

  async addDocument(content, metadata = {}, collection) {
    const r = await fetch(`${CONFIG.API_BASE}/api/ai/documents`, {
      method: 'POST',
      headers: authHeaders({ 'Content-Type': 'application/json' }),
      body: JSON.stringify({ content, metadata, collection }),
    });
    if (!r.ok) throw new Error((await r.json()).error || `HTTP ${r.status}`);
    return r.json();
  },

  // Upload sincrono
  async uploadFile(file, collection) {
    const form = new FormData();
    form.append('file', file);
    if (collection) form.append('collection', collection);
    const r = await fetch(`${CONFIG.API_BASE}/api/ai/documents/upload`, {
      method: 'POST',
      headers: authHeaders(),
      body: form,
    });
    if (!r.ok) throw new Error((await r.json()).error || `HTTP ${r.status}`);
    return r.json();
  },

  // #10 — Upload asincrono: ritorna {jobId, source, status}
  async uploadFileAsync(file, collection) {
    const form = new FormData();
    form.append('file', file);
    if (collection) form.append('collection', collection);
    const r = await fetch(`${CONFIG.API_BASE}/api/ai/documents/upload/async`, {
      method: 'POST',
      headers: authHeaders(),
      body: form,
    });
    if (!r.ok) throw new Error((await r.json()).error || `HTTP ${r.status}`);
    return r.json();
  },

  // #10 — Polling stato job
  async getJob(jobId) {
    const r = await fetch(`${CONFIG.API_BASE}/api/ai/jobs/${encodeURIComponent(jobId)}`, {
      headers: authHeaders(),
    });
    if (!r.ok) throw new Error(`HTTP ${r.status}`);
    return r.json();
  },

  async ingestUrl(url, metadata = {}, collection) {
    const r = await fetch(`${CONFIG.API_BASE}/api/ai/documents/url`, {
      method: 'POST',
      headers: authHeaders({ 'Content-Type': 'application/json' }),
      body: JSON.stringify({ url, metadata, collection }),
    });
    if (!r.ok) throw new Error((await r.json()).error || `HTTP ${r.status}`);
    return r.json();
  },

  async deleteBySource(source) {
    const r = await fetch(
      `${CONFIG.API_BASE}/api/ai/documents?source=${encodeURIComponent(source)}`,
      { method: 'DELETE', headers: authHeaders() }
    );
    if (r.status === 404) throw new Error('Sorgente non trovata');
    if (!r.ok) throw new Error(`HTTP ${r.status}`);
    return r.json();
  },

  async search(query, topK = 5, collection) {
    let url = `${CONFIG.API_BASE}/api/ai/search?query=${encodeURIComponent(query)}&topK=${topK}`;
    if (collection) url += `&collection=${encodeURIComponent(collection)}`;
    const r = await fetch(url, { headers: authHeaders() });
    if (!r.ok) throw new Error(`HTTP ${r.status}`);
    return r.json();
  },
};
