import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/api'
import SidebarLayout from '../components/SidebarLayout'
import PriorityBadge from '../components/PriorityBadge'
import StatusBadge from '../components/StatusBadge'
import { formatarData } from '../utils/labels'
import { urlGestorProtocolo } from './GestorProtocoloPage'

export default function GestorPage() {
  const [fila, setFila] = useState([])
  const [statusLista, setStatusLista] = useState([])
  const [filtro, setFiltro] = useState({ prioridade: '', categoria: '', status: '', bairro: '' })
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(true)

  const carregarFila = () => {
    setCarregando(true)
    api
      .obterFila()
      .then(setFila)
      .catch((e) => setErro(e.message))
      .finally(() => setCarregando(false))
  }

  useEffect(() => {
    carregarFila()
    api.getStatus().then(setStatusLista)
  }, [])

  const aplicarFiltro = async () => {
    setErro('')
    setCarregando(true)
    const payload = {}
    if (filtro.prioridade) payload.prioridade = filtro.prioridade
    if (filtro.categoria) payload.categoria = filtro.categoria
    if (filtro.status) payload.status = filtro.status
    if (filtro.bairro.trim()) payload.bairro = filtro.bairro.trim()

    try {
      if (Object.keys(payload).length === 0) {
        const todos = await api.obterFila()
        setFila(todos)
      } else {
        const filtrados = await api.filtrar(payload)
        setFila(filtrados)
      }
    } catch (e) {
      setErro(e.message)
    } finally {
      setCarregando(false)
    }
  }

  const limparFiltro = () => {
    setFiltro({ prioridade: '', categoria: '', status: '', bairro: '' })
    carregarFila()
  }

  return (
    <SidebarLayout
      titulo="Painel do gestor"
      subtitulo="Fila ordenada por prioridade. Clique em uma solicitação para ver os detalhes."
    >
      <div className="app-card mb-6 grid gap-3 p-4 sm:grid-cols-5 sm:p-5">
        <select
          value={filtro.prioridade}
          onChange={(e) => setFiltro((f) => ({ ...f, prioridade: e.target.value }))}
          className="rounded-lg border border-slate-200 px-3 py-2.5 text-sm"
        >
          <option value="">Prioridade</option>
          <option value="ALTA">Alta</option>
          <option value="MEDIA">Média</option>
          <option value="BAIXA">Baixa</option>
        </select>
        <select
          value={filtro.status}
          onChange={(e) => setFiltro((f) => ({ ...f, status: e.target.value }))}
          className="rounded-lg border border-slate-200 px-3 py-2.5 text-sm"
        >
          <option value="">Status</option>
          {statusLista.map((s) => (
            <option key={s} value={s}>{s.replace('_', ' ')}</option>
          ))}
        </select>
        <input
          value={filtro.bairro}
          onChange={(e) => setFiltro((f) => ({ ...f, bairro: e.target.value }))}
          placeholder="Bairro"
          className="rounded-lg border border-slate-200 px-3 py-2.5 text-sm sm:col-span-2"
        />
        <div className="flex gap-2">
          <button type="button" onClick={aplicarFiltro} className="flex-1 rounded-lg bg-brand-600 py-2.5 text-sm font-bold text-white">
            Filtrar
          </button>
          <button type="button" onClick={limparFiltro} className="rounded-lg border border-slate-200 px-3 text-sm font-semibold text-slate-600">
            Limpar
          </button>
        </div>
      </div>

      {erro && (
        <div className="app-card mb-4 border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">{erro}</div>
      )}

      {carregando ? (
        <p className="text-sm text-slate-500">Carregando fila…</p>
      ) : fila.length === 0 ? (
        <div className="app-card border-dashed py-16 text-center text-sm text-slate-500">
          Nenhuma demanda na fila com os filtros aplicados.
        </div>
      ) : (
        <div className="app-card overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full min-w-[760px] text-left text-sm">
              <thead className="border-b border-slate-100 bg-slate-50/80 text-xs font-bold uppercase tracking-wide text-slate-500">
                <tr>
                  <th className="px-4 py-3">Protocolo</th>
                  <th className="px-4 py-3">Descrição</th>
                  <th className="px-4 py-3">Bairro</th>
                  <th className="px-4 py-3">Prioridade</th>
                  <th className="px-4 py-3">Status</th>
                  <th className="px-4 py-3">SLA</th>
                  <th className="px-4 py-3"></th>
                </tr>
              </thead>
              <tbody>
                {fila.map((item) => (
                  <tr key={item.id} className="border-b border-slate-50 transition hover:bg-brand-50/40">
                    <td className="px-4 py-4 font-mono text-xs font-bold text-brand-800">{item.protocolo}</td>
                    <td className="max-w-xs truncate px-4 py-4 text-slate-600">{item.descricao}</td>
                    <td className="px-4 py-4 text-slate-600">{item.bairro}</td>
                    <td className="px-4 py-4"><PriorityBadge prioridade={item.prioridade} /></td>
                    <td className="px-4 py-4"><StatusBadge status={item.status} /></td>
                    <td className="px-4 py-4">
                      <span className={item.foraDoPrazoSla ? 'font-bold text-rose-600' : 'text-slate-600'}>
                        {formatarData(item.prazoSla)}
                      </span>
                    </td>
                    <td className="px-4 py-4">
                      <Link
                        to={urlGestorProtocolo(item.protocolo)}
                        className="cursor-pointer rounded-lg bg-brand-600 px-3 py-1.5 text-xs font-bold text-white hover:bg-brand-700"
                      >
                        Abrir
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </SidebarLayout>
  )
}
