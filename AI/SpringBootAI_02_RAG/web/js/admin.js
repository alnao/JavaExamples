// ── Toast ──
function showToast(message, type = 'success') {
  const container = document.getElementById('toast-container');
  const icon = type === 'success'
    ? '<i class="fa-solid fa-circle-check text-success me-2"></i>'
    : '<i class="fa-solid fa-circle-xmark text-danger me-2"></i>';

  const el = document.createElement('div');
  el.className = 'toast toast-dark align-items-center show mb-2';
  el.setAttribute('role', 'alert');
  el.innerHTML = `
    <div class="d-flex align-items-center p-3 gap-2">
      ${icon}
      <div class="flex-grow-1" style="font-size:.88rem">${message}</div>
      <button type="button" class="btn-close btn-close-white ms-2" onclick="this.closest('.toast').remove()"></button>
    </div>`;
  container.appendChild(el);
  setTimeout(() => el.remove(), 4500);
}

function escAdmin(str) {
  return String(str).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
}

// ── Documents list ──  (#11: mostra collection)
async function loadDocuments() {
  const tbody   = document.getElementById('docs-tbody');
  const counter = document.getElementById('docs-count');
  tbody.innerHTML = `<tr><td colspan="5" class="text-center py-4 text-muted">
    <i class="fa-solid fa-circle-notch fa-spin me-2"></i>Caricamento…</td></tr>`;

  try {
    const docs = await api.listDocuments();
    counter.textContent = docs.length;

    if (!docs.length) {
      tbody.innerHTML = `<tr><td colspan="5">
        <div class="empty-state"><i class="fa-solid fa-inbox"></i>Nessun documento indicizzato</div>
      </td></tr>`;
      return;
    }

    tbody.innerHTML = docs.map((d, i) => `
      <tr>
        <td class="text-muted" style="width:40px">${i + 1}</td>
        <td><i class="fa-solid fa-file-lines me-2" style="color:var(--accent);opacity:.7"></i>${escAdmin(d.source || '—')}</td>
        <td>
          ${d.collection
            ? `<span class="badge" style="background:rgba(16,185,129,.15);color:#10b981;border:1px solid rgba(16,185,129,.3)">${escAdmin(d.collection)}</span>`
            : '<span style="color:var(--text-muted);font-size:.8rem">—</span>'}
        </td>
        <td><span class="badge" style="background:rgba(99,102,241,.18);color:var(--accent);border:1px solid rgba(99,102,241,.3)">${d.chunks} chunk</span></td>
        <td style="width:60px">
          <button class="btn btn-sm btn-delete"
                  title="Elimina documento"
                  data-source="${escAdmin(d.source)}"
                  style="background:rgba(239,68,68,.12);color:#ef4444;border:1px solid rgba(239,68,68,.3);border-radius:7px;padding:.25rem .55rem">
            <i class="fa-solid fa-trash-can"></i>
          </button>
        </td>
      </tr>`).join('');

    tbody.querySelectorAll('.btn-delete').forEach(btn => {
      btn.addEventListener('click', () => confirmDelete(btn.dataset.source));
    });

  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="5" class="text-center text-danger py-3">
      <i class="fa-solid fa-triangle-exclamation me-2"></i>${err.message}</td></tr>`;
  }
}

// ── Delete con conferma ──
function confirmDelete(source) {
  const modal = document.getElementById('confirm-modal');
  document.getElementById('confirm-source').textContent = source;
  document.getElementById('confirm-delete-btn').onclick = async () => {
    bootstrap.Modal.getInstance(modal).hide();
    try {
      const res = await api.deleteBySource(source);
      showToast(`<strong>${escAdmin(source)}</strong> eliminato — ${res.deleted} chunk rimossi`);
      await loadDocuments();
    } catch (err) {
      showToast(`Errore eliminazione: ${err.message}`, 'error');
    }
  };
  new bootstrap.Modal(modal).show();
}

// ── Upload file ──
const dropZone        = document.getElementById('drop-zone');
const fileInput       = document.getElementById('file-input');
const uploadBtn       = document.getElementById('upload-btn');
const uploadAsyncBtn  = document.getElementById('upload-async-btn');
const uploadProg      = document.getElementById('upload-progress');
const jobStatusEl     = document.getElementById('job-status');
const uploadColInput  = document.getElementById('upload-collection');
let selectedFile = null;

function setSelectedFile(file) {
  selectedFile = file;
  dropZone.innerHTML = `
    <i class="fa-solid fa-file-arrow-up"></i>
    <div style="font-size:.9rem;color:var(--text)">${escAdmin(file.name)}</div>
    <div style="font-size:.78rem;margin-top:.3rem">${(file.size / 1024).toFixed(1)} KB</div>`;
  uploadBtn.disabled = false;
  if (uploadAsyncBtn) uploadAsyncBtn.disabled = false;
}

dropZone.addEventListener('click', () => fileInput.click());
fileInput.addEventListener('change', () => { if (fileInput.files[0]) setSelectedFile(fileInput.files[0]); });
dropZone.addEventListener('dragover',  e => { e.preventDefault(); dropZone.classList.add('dragover'); });
dropZone.addEventListener('dragleave', ()  => dropZone.classList.remove('dragover'));
dropZone.addEventListener('drop', e => {
  e.preventDefault();
  dropZone.classList.remove('dragover');
  if (e.dataTransfer.files[0]) setSelectedFile(e.dataTransfer.files[0]);
});

// Upload sincrono
uploadBtn.addEventListener('click', async () => {
  if (!selectedFile) return;
  const collection = uploadColInput?.value.trim() || null;
  uploadBtn.disabled = true;
  uploadBtn.innerHTML = '<i class="fa-solid fa-circle-notch fa-spin me-2"></i>Caricamento…';
  uploadProg.classList.remove('d-none');
  try {
    const res = await api.uploadFile(selectedFile, collection);
    const extra = res.replaced > 0 ? ` · sostituiti ${res.replaced} chunk precedenti` : '';
    showToast(`<strong>${escAdmin(res.source)}</strong> — ${res.chunks} chunk${extra}`);
    resetDropZone();
    await loadDocuments();
  } catch (err) {
    showToast(`Errore upload: ${err.message}`, 'error');
  } finally {
    uploadBtn.disabled = false;
    uploadBtn.innerHTML = '<i class="fa-solid fa-upload me-2"></i>Indicizza';
    uploadProg.classList.add('d-none');
  }
});

// #10 — Upload asincrono con polling
uploadAsyncBtn?.addEventListener('click', async () => {
  if (!selectedFile) return;
  const collection = uploadColInput?.value.trim() || null;
  uploadAsyncBtn.disabled = true;
  uploadAsyncBtn.innerHTML = '<i class="fa-solid fa-circle-notch fa-spin me-1"></i>Avvio…';
  jobStatusEl.classList.remove('d-none');
  jobStatusEl.innerHTML = '<i class="fa-solid fa-circle-notch fa-spin me-1"></i>In coda…';

  try {
    const job = await api.uploadFileAsync(selectedFile, collection);
    resetDropZone();
    pollJob(job.jobId, job.source);
  } catch (err) {
    showToast(`Errore avvio async: ${err.message}`, 'error');
    uploadAsyncBtn.disabled = false;
    uploadAsyncBtn.innerHTML = '<i class="fa-solid fa-bolt me-1"></i>Asincrono';
    jobStatusEl.classList.add('d-none');
  }
});

async function pollJob(jobId, source) {
  const icons = { PENDING: 'fa-clock', RUNNING: 'fa-circle-notch fa-spin', DONE: 'fa-circle-check', ERROR: 'fa-circle-xmark' };
  const colors = { PENDING: 'var(--text-muted)', RUNNING: 'var(--accent)', DONE: '#10b981', ERROR: '#ef4444' };

  const update = (job) => {
    const icon  = icons[job.status]  || 'fa-question';
    const color = colors[job.status] || 'var(--text-muted)';
    jobStatusEl.innerHTML =
      `<i class="fa-solid ${icon} me-1" style="color:${color}"></i>` +
      `<strong>${escAdmin(source)}</strong> — ${job.status}` +
      (job.chunks  ? ` · ${job.chunks} chunk` : '') +
      (job.error   ? ` · ${escAdmin(job.error)}` : '');
  };

  const check = async () => {
    try {
      const job = await api.getJob(jobId);
      update(job);
      if (job.status === 'DONE') {
        const extra = job.replaced > 0 ? ` · sostituiti ${job.replaced} chunk` : '';
        showToast(`<strong>${escAdmin(source)}</strong> — ${job.chunks} chunk${extra}`);
        await loadDocuments();
        uploadAsyncBtn.disabled = true;
        uploadAsyncBtn.innerHTML = '<i class="fa-solid fa-bolt me-1"></i>Asincrono';
      } else if (job.status === 'ERROR') {
        showToast(`Errore indicizzazione: ${job.error}`, 'error');
        uploadAsyncBtn.disabled = true;
        uploadAsyncBtn.innerHTML = '<i class="fa-solid fa-bolt me-1"></i>Asincrono';
      } else {
        setTimeout(check, 1500);
      }
    } catch (err) {
      jobStatusEl.innerHTML = `<i class="fa-solid fa-circle-xmark me-1" style="color:#ef4444"></i>Polling fallito: ${escAdmin(err.message)}`;
    }
  };

  setTimeout(check, 800);
}

function resetDropZone() {
  selectedFile = null;
  fileInput.value = '';
  uploadBtn.disabled = true;
  if (uploadAsyncBtn) uploadAsyncBtn.disabled = true;
  dropZone.innerHTML = `
    <i class="fa-solid fa-cloud-arrow-up"></i>
    <div style="font-size:.9rem">Trascina un file qui o <span style="color:var(--accent);text-decoration:underline">sfoglia</span></div>
    <div style="font-size:.77rem;margin-top:.4rem">PDF · DOCX · TXT · HTML — max 50 MB</div>`;
}

// ── Insert text ──
const textForm       = document.getElementById('text-form');
const textContent    = document.getElementById('text-content');
const textMeta       = document.getElementById('text-meta');
const textCollection = document.getElementById('text-collection');
const insertBtn      = document.getElementById('insert-btn');
const charCount      = document.getElementById('char-count');

textContent.addEventListener('input', () => {
  charCount.textContent = textContent.value.length;
  insertBtn.disabled = !textContent.value.trim();
});

textForm.addEventListener('submit', async e => {
  e.preventDefault();
  const content = textContent.value.trim();
  if (!content) return;

  let metadata = {};
  const raw = textMeta.value.trim();
  if (raw) {
    try { metadata = JSON.parse(raw); }
    catch { showToast('Metadata non valido: deve essere JSON (es. {"fonte":"wiki"})', 'error'); return; }
  }
  const collection = textCollection?.value.trim() || null;

  insertBtn.disabled = true;
  insertBtn.innerHTML = '<i class="fa-solid fa-circle-notch fa-spin me-2"></i>Inserimento…';
  try {
    const res = await api.addDocument(content, metadata, collection);
    showToast(`Documento inserito — ID: <code>${res.id}</code>`);
    textContent.value = '';
    textMeta.value = '';
    if (textCollection) textCollection.value = '';
    charCount.textContent = '0';
    await loadDocuments();
  } catch (err) {
    showToast(`Errore: ${err.message}`, 'error');
  } finally {
    insertBtn.disabled = true;
    insertBtn.innerHTML = '<i class="fa-solid fa-plus me-2"></i>Inserisci documento';
  }
});

// ── Ingest URL ──
const urlForm       = document.getElementById('url-form');
const urlInput      = document.getElementById('url-input');
const urlMeta       = document.getElementById('url-meta');
const urlSource     = document.getElementById('url-source');
const urlCollection = document.getElementById('url-collection');
const urlBtn        = document.getElementById('url-btn');

urlInput.addEventListener('input', () => {
  urlBtn.disabled = !urlInput.value.trim();
});

urlForm.addEventListener('submit', async e => {
  e.preventDefault();
  const url = urlInput.value.trim();
  if (!url) return;

  let metadata = {};
  const rawMeta = urlMeta.value.trim();
  if (rawMeta) {
    try { metadata = JSON.parse(rawMeta); }
    catch { showToast('Metadata non valido: deve essere JSON', 'error'); return; }
  }
  const src = urlSource.value.trim();
  if (src) metadata.source = src;

  const collection = urlCollection?.value.trim() || null;

  urlBtn.disabled = true;
  urlBtn.innerHTML = '<i class="fa-solid fa-circle-notch fa-spin me-2"></i>Indicizzazione…';

  try {
    const res = await api.ingestUrl(url, metadata, collection);
    const extra = res.replaced > 0 ? ` · sostituiti ${res.replaced} chunk precedenti` : '';
    showToast(`<strong>${escAdmin(res.source)}</strong> — ${res.chunks} chunk${extra}`);
    urlForm.reset();
    urlBtn.disabled = true;
    await loadDocuments();
  } catch (err) {
    showToast(`Errore URL: ${err.message}`, 'error');
  } finally {
    urlBtn.innerHTML = '<i class="fa-solid fa-link me-2"></i>Indicizza URL';
    if (urlInput.value.trim()) urlBtn.disabled = false;
  }
});

// ── Settings modal ──
document.getElementById('settings-form').addEventListener('submit', e => {
  e.preventDefault();
  const base = document.getElementById('settings-api-base').value.trim();
  const key  = document.getElementById('settings-api-key').value.trim();
  saveConfig(base, key);
});

// ── Init ──
document.addEventListener('DOMContentLoaded', () => {
  loadDocuments();
  document.getElementById('refresh-docs').addEventListener('click', loadDocuments);
  document.getElementById('settings-api-base').value = CONFIG.API_BASE;
  document.getElementById('settings-api-key').value  = CONFIG.API_KEY;
});
