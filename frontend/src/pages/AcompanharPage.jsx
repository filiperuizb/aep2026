import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { api } from '../api/api'
import SidebarLayout from '../components/SidebarLayout'
import PublicLayout from '../components/PublicLayout'
import PriorityBadge from '../components/PriorityBadge'
import StatusBadge from '../components/StatusBadge'
import TimelineHistorico from '../components/TimelineHistorico'
import { useAuth } from '../context/AuthContext'
import { formatarData } from '../utils/labels'

function ConteudoAcompanhar({ protocolo, setProtocolo, detalhe, erro, carregando, buscar }) {
  const s = detalhe?.solicitacao

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <form
        onSubmit={(e) => {
          e.preventDefault()
          buscar()
        }}
        className="app-card flex flex-col gap-3 p-4 sm:flex-row sm:p-5"
      >
        <input
          value={protocolo}
          onChange={(e) => setProtocolo(e.target.value)}
          className="flex-1 rounded-lg border border-slate-200 px-4 py-3 text-sm outline-none focus:border-brand-400 focus:ring-4 focus:ring-brand-100"
          placeholder="Ex: OBS - 20260609-12345"
          required
        />
        <button
          type="submit"
          disabled={carregando}
          className="rounded-lg bg-brand-600 px-6 py-3 text-sm font-bold text-white disabled:opacity-60"
        >
          {carregando ? 'Buscando…' : 'Consultar'}
        </button>
      </form>

      {erro && (
        <div className="app-card border-rose-200 bg-rose-50 px-4 py-3 text-sm font-medium text-rose-700">{erro}</div>
      )}

      {s && (
        <div className="space-y-4">
          <div className="app-card p-6">
            <div className="flex flex-wrap items-start justify-between gap-3">
              <div>
                <p className="text-xs font-bold uppercase tracking-wider text-slate-400">Protocolo</p>
                <p className="mt-1 font-mono text-lg font-bold text-brand-800">{s.protocolo}</p>
              </div>
              <div className="flex flex-wrap gap-2">
                <StatusBadge status={s.status} />
                <PriorityBadge prioridade={s.prioridade} />
                {s.anonima && (
                  <span className="rounded-full bg-slate-100 px-2.5 py-1 text-xs font-semibold text-slate-600 ring-1 ring-slate-200 ring-inset">
                    Anônima
                  </span>
                )}
              </div>
            </div>

            <p className="mt-5 text-sm leading-relaxed text-slate-700">{s.descricao}</p>

            <div className="mt-6 grid gap-3 sm:grid-cols-2">
              <Info label="Categoria" valor={s.categoria?.replace('_', ' ')} />
              <Info label="Bairro" valor={s.bairro} />
              <Info label="Localização" valor={s.localizacao} />
              <Info label="Abertura" valor={formatarData(s.dataCriacao)} />
              <Info label="Prazo SLA" valor={formatarData(s.prazoSla)} />
              <Info
                label="Situação do prazo"
                valor={s.foraDoPrazoSla ? 'Fora do prazo' : 'Dentro do prazo'}
                destaque={s.foraDoPrazoSla}
              />
            </div>

            {s.justificativaAtraso && (
              <div className="mt-4 rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-900">
                <span className="font-bold">Justificativa de atraso:</span> {s.justificativaAtraso}
              </div>
            )}
          </div>

          <div className="app-card p-6">
            <h3 className="text-lg font-bold text-slate-900">Histórico de movimentações</h3>
            <div className="mt-5">
              <TimelineHistorico historico={detalhe.historico} />
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default function AcompanharPage() {
  const { usuario, anonimo } = useAuth()
  const [params] = useSearchParams()
  const [protocolo, setProtocolo] = useState(params.get('protocolo') || '')
  const [detalhe, setDetalhe] = useState(null)
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)

  const buscar = async (valor) => {
    const termo = (valor ?? protocolo).trim()
    if (!termo) return
    setErro('')
    setCarregando(true)
    setDetalhe(null)
    try {
      const dados = await api.buscarPorProtocolo(termo)
      setDetalhe(dados)
    } catch (e) {
      setErro(e.message)
    } finally {
      setCarregando(false)
    }
  }

  useEffect(() => {
    const inicial = params.get('protocolo')
    if (inicial) buscar(inicial)
  }, [])

  const logado = usuario || anonimo

  if (logado) {
    return (
      <SidebarLayout
        titulo="Acompanhar protocolo"
        subtitulo="Consulte status, prazo de SLA, justificativas e histórico de movimentações."
      >
        <ConteudoAcompanhar
          protocolo={protocolo}
          setProtocolo={setProtocolo}
          detalhe={detalhe}
          erro={erro}
          carregando={carregando}
          buscar={buscar}
        />
      </SidebarLayout>
    )
  }

  return (
    <PublicLayout
      titulo="Acompanhar protocolo"
      subtitulo="Consulte status, prazo de SLA, justificativas e histórico de movimentações."
    >
      <ConteudoAcompanhar
        protocolo={protocolo}
        setProtocolo={setProtocolo}
        detalhe={detalhe}
        erro={erro}
        carregando={carregando}
        buscar={buscar}
      />
    </PublicLayout>
  )
}

function Info({ label, valor, destaque }) {
  return (
    <div className={`rounded-lg px-3 py-2 ${destaque ? 'bg-amber-50' : 'bg-slate-50'}`}>
      <p className="text-xs font-semibold text-slate-400">{label}</p>
      <p className="mt-0.5 text-sm font-semibold text-slate-800">{valor}</p>
    </div>
  )
}
