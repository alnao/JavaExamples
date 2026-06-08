async function updateHealth() {
  const badge = document.getElementById('health-badge');
  const label = document.getElementById('health-label');
  if (!badge) return;

  badge.className = 'checking';
  label.textContent = '…';

  try {
    const data = await api.health();
    const up = data.status === 'UP';
    badge.className = up ? 'up' : 'down';
    label.textContent = up ? 'UP' : data.status || 'DOWN';
  } catch {
    badge.className = 'down';
    label.textContent = 'OFFLINE';
  }
}

document.addEventListener('DOMContentLoaded', () => {
  updateHealth();
  setInterval(updateHealth, 30_000);
});
