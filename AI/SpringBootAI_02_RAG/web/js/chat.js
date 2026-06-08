// ── Elementi DOM ──
const messagesEl    = document.getElementById('messages');
const inputEl       = document.getElementById('chat-input');
const sendBtn       = document.getElementById('send-btn');
const ragToggle     = document.getElementById('rag-toggle');
const modeBadge     = document.getElementById('mode-badge');
const topkWrap      = document.getElementById('topk-wrap');
const topkSlider    = document.getElementById('topk-slider');
const topkValue     = document.getElementById('topk-value');
const collectionEl  = document.getElementById('collection-input'); // #11
const clearBtn      = document.getElementById('clear-btn');
const newSessBtn    = document.getElementById('new-session-btn');
const sessLabel     = document.getElementById('session-label');

// ── Sessione ──
function generateId() {
  return crypto.randomUUID ? crypto.randomUUID()
       : Math.random().toString(36).slice(2) + Date.now().toString(36);
}

let sessionId = sessionStorage.getItem('chat_session_id');
if (!sessionId) {
  sessionId = generateId();
  sessionStorage.setItem('chat_session_id', sessionId);
}
updateSessionLabel();

function updateSessionLabel() {
  if (sessLabel) sessLabel.textContent = sessionId.slice(0, 8) + '…';
}

newSessBtn?.addEventListener('click', async () => {
  try { await api.clearSession(sessionId); } catch {}
  sessionId = generateId();
  sessionStorage.setItem('chat_session_id', sessionId);
  updateSessionLabel();
  messagesEl.innerHTML = '';
  appendWelcome();
});

// ── Mode toggle ──
ragToggle.addEventListener('change', () => {
  const rag = ragToggle.checked;
  modeBadge.textContent   = rag ? 'RAG' : 'Normal';
  modeBadge.className     = 'mode-badge ' + (rag ? 'rag' : 'normal');
  topkWrap.style.display  = rag ? 'flex' : 'none';
});

topkSlider.addEventListener('input', () => { topkValue.textContent = topkSlider.value; });

// ── Helpers ──
function timestamp() {
  return new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

function escapeHtml(str) {
  return String(str)
    .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
    .replace(/\n/g, '<br>');
}

function appendMessage(role, text, extra = '') {
  const isUser = role === 'user';
  const div    = document.createElement('div');
  div.className = `msg ${role}`;
  div.innerHTML = `
    <div class="msg-avatar"><i class="fa-solid ${isUser ? 'fa-user' : 'fa-robot'}"></i></div>
    <div>
      <div class="msg-bubble">${escapeHtml(text)}</div>
      <div class="msg-meta">${timestamp()}${extra}</div>
    </div>`;
  messagesEl.appendChild(div);
  messagesEl.scrollTop = messagesEl.scrollHeight;
  return div;
}

// Crea una bolla bot vuota per la risposta in streaming
function appendStreamBubble() {
  const div = document.createElement('div');
  div.className = 'msg bot';
  div.innerHTML = `
    <div class="msg-avatar"><i class="fa-solid fa-robot"></i></div>
    <div>
      <div class="msg-bubble streaming-cursor" id="stream-bubble"></div>
      <div class="msg-meta" id="stream-meta">${timestamp()}</div>
    </div>`;
  messagesEl.appendChild(div);
  messagesEl.scrollTop = messagesEl.scrollHeight;
  return {
    bubble: div.querySelector('#stream-bubble'),
    meta:   div.querySelector('#stream-meta'),
  };
}

function finalizeStreamBubble(bubble, meta, text, extra) {
  bubble.classList.remove('streaming-cursor');
  bubble.innerHTML = escapeHtml(text);
  if (extra) meta.innerHTML += extra;
  messagesEl.scrollTop = messagesEl.scrollHeight;
}

function setLoading(on) {
  sendBtn.disabled  = on;
  inputEl.disabled  = on;
  sendBtn.innerHTML = on
    ? '<i class="fa-solid fa-circle-notch fa-spin"></i>'
    : '<i class="fa-solid fa-paper-plane"></i>';
}

// ── Send ──
async function send() {
  const question = inputEl.value.trim();
  if (!question) return;

  inputEl.value = '';
  inputEl.style.height = 'auto';
  appendMessage('user', question);
  setLoading(true);

  const rag        = ragToggle.checked;
  const topK       = parseInt(topkSlider.value, 10);
  const collection = collectionEl?.value.trim() || null; // #11
  const { bubble, meta } = appendStreamBubble();

  try {
    let fullText = '';

    const onToken = (token, full) => {
      fullText = full;
      bubble.innerHTML = escapeHtml(full);
      messagesEl.scrollTop = messagesEl.scrollHeight;
    };

    if (rag) {
      await api.chatRagStream(question, topK, sessionId, collection, onToken);
    } else {
      await api.chatStream(question, sessionId, onToken);
    }

    const collLabel = rag && collection ? ` · <i class="fa-solid fa-folder-open" title="Collection"></i> ${collection}` : '';
    const extra = rag
      ? ` &nbsp;·&nbsp; <i class="fa-solid fa-database" title="RAG attivo"></i> topK=${topK}${collLabel}`
      : '';
    finalizeStreamBubble(bubble, meta, fullText, extra);

  } catch (err) {
    finalizeStreamBubble(bubble, meta, `Errore: ${err.message}`, '');
  } finally {
    setLoading(false);
    inputEl.focus();
  }
}

sendBtn.addEventListener('click', send);
inputEl.addEventListener('keydown', e => {
  if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); send(); }
});
inputEl.addEventListener('input', () => {
  inputEl.style.height = 'auto';
  inputEl.style.height = Math.min(inputEl.scrollHeight, 120) + 'px';
});

// ── Clear ──
clearBtn.addEventListener('click', () => {
  messagesEl.innerHTML = '';
  appendWelcome();
});

// ── Welcome ──
function appendWelcome() {
  const div = document.createElement('div');
  div.className = 'msg bot';
  div.innerHTML = `
    <div class="msg-avatar"><i class="fa-solid fa-robot"></i></div>
    <div>
      <div class="msg-bubble">
        Ciao! Sono il tuo assistente AI locale con <strong>memoria conversazionale</strong>.<br>
        Attiva <strong>RAG</strong> per rispondere usando i documenti indicizzati.
        La sessione attuale viene ricordata — usa <em>Nuova sessione</em> per ripartire da zero.
      </div>
      <div class="msg-meta">${timestamp()}</div>
    </div>`;
  messagesEl.appendChild(div);
}

document.addEventListener('DOMContentLoaded', appendWelcome);
