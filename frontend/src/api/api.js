const BASE = 'http://localhost:8080/api'

async function request(path, options = {}) {
  const response = await fetch(`${BASE}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  })

  if (!response.ok) {
    const body = await response.json().catch(() => ({}))
    throw new Error(body.mensagem || 'Erro ao processar requisição.')
  }

  if (response.status === 204) {
    return null
  }

  const text = await response.text()
  return text ? JSON.parse(text) : null
}

export const api = {
  login: (email, senha) =>
    request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, senha }),
    }),

  getCategorias: () => request('/enums/categorias'),
  getPrioridades: () => request('/enums/prioridades'),
  getStatus: () => request('/enums/status'),

  criarSolicitacao: (payload) =>
    request('/solicitacoes', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),

  buscarPorProtocolo: (protocolo) =>
    request(`/solicitacoes/protocolo/${encodeURIComponent(protocolo.trim())}`),

  listarPorUsuario: (usuarioId) => request(`/solicitacoes/usuario/${usuarioId}`),

  obterFila: () => request('/solicitacoes/fila'),

  filtrar: (filtro) =>
    request('/solicitacoes/filtro', {
      method: 'POST',
      body: JSON.stringify(filtro),
    }),

  atualizarStatus: (payload) =>
    request('/solicitacoes/status', {
      method: 'PUT',
      body: JSON.stringify(payload),
    }),
}
