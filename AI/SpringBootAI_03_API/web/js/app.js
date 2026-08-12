const chatEl = document.getElementById('chat');
const messageEl = document.getElementById('message');
const sendBtn = document.getElementById('sendBtn');
const sessionIdEl = document.getElementById('sessionId');

function addMessage(role, text) {
  const div = document.createElement('div');
  div.className = `message ${role}`;
  div.textContent = text;
  chatEl.appendChild(div);
  chatEl.scrollTop = chatEl.scrollHeight;
}

async function sendMessage() {
  const text = messageEl.value.trim();
  if (!text) return;

  const sessionId = sessionIdEl.value.trim() || 'demo-session';
  addMessage('user', text);
  messageEl.value = '';

  try {
    const response = await fetch('http://localhost:8082/api/agent/chat', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ message: text, sessionId })
    });

    if (!response.ok) {
      throw new Error('Errore HTTP: ' + response.status);
    }

    const data = await response.json();
    addMessage('agent', data.answer || 'Nessuna risposta');
  } catch (error) {
    addMessage('agent', 'Errore: ' + error.message + '\nAssicurati che il backend sia avviato su http://localhost:8082');
  }
}

sendBtn.addEventListener('click', sendMessage);
messageEl.addEventListener('keydown', (event) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault();
    sendMessage();
  }
});

addMessage('agent', 'Ciao! Prova con: "calcola 12*7" oppure "lista i file della cartella"');
