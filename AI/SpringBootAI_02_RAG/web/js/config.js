// Configurazione client — modifica API_KEY per corrispondere al valore in .env
const CONFIG = (() => {
  const stored = localStorage.getItem('rag_config');
  const defaults = {
    API_BASE: 'http://localhost:8081',
    API_KEY:  'dev-secret-key',   // deve corrispondere ad API_KEY nel .env
  };
  return stored ? { ...defaults, ...JSON.parse(stored) } : defaults;
})();

function saveConfig(apiBase, apiKey) {
  localStorage.setItem('rag_config', JSON.stringify({ API_BASE: apiBase, API_KEY: apiKey }));
  location.reload();
}
