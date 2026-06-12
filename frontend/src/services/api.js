const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080/api';

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {})
    },
    ...options
  });

  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`);
  }

  return response.json();
}

export function fetchQuestions() {
  return request('/placement/questions');
}

export function evaluatePlacement(payload) {
  return request('/placement/evaluate', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function recordStudySession(payload) {
  return request('/study-sessions', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function fetchModules() {
  return request('/training/modules');
}

export function evaluateShadowing(payload) {
  return request('/training/shadowing/evaluate', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}
