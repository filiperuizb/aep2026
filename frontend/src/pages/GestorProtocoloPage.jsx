import { useCallback, useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { api } from '../api/api'
import PriorityBadge from '../components/PriorityBadge'
import SidebarLayout from '../components/SidebarLayout'
import SolicitanteInfo from '../components/SolicitanteInfo'
import StatusBadge from '../components/StatusBadge'
import TimelineHistorico from '../components/TimelineHistorico'
import { useAuth } from '../context/AuthContext'
import { formatarData } from '../utils/labels'

function urlGestorProtocolo(protocolo) {
  return `/gestor/${encodeURIComponent(protocolo.trim())}`
}

function Info({ label, valor, destaque }) {
  return (
    <div className={`rounded-lg px-4 py-3 ${destaque ? 'bg-amber-50' : 'bg-slate-50'}`}>
      <p className="text-xs font-semibold uppercase tracking-wide text-slate-400">{label}</p>
      <p className="mt-1 text-sm font-semibold text-slate-800">{valor}</p>
    </div>
  )
}

export default function GestorProtocoloPage() {
  const { protocolo: protocoloUrl } = useParams()
  const navigate = useNavigate()
  const { usuario } = useAuth()
  const [detalhe, setDetalhe] = useState(null)
  const [comentario, setComentario] = useState('')
  const [justificativa, setJustificativa] = useState('')
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(true)
  const [salvando, setSalvando] = useState(false)

  const carregar = useCallback(async (protocolo) => {
    setErro('')
    setCarregando(true)
    setDetalhe(null)

    try {
      const dados = await api.buscarPorProtocolo(protocolo)
      setDetalhe(dados)
    } catch (e) {
      setErro(e.message)
    } finally {
      setCarregando(false)
    }
  }, [])

  useEffect(() => {
    if (!protocoloUrl) {
      navigate('/gestor', { replace: true })
      return
    }
    carregar(decodeURIComponent(protocoloUrl))
  }, [protocoloUrl, carregar, navigate])

  const confirmarStatus = async () => {
    const s = detalhe?.solicitacao
    if (!s?.proximoStatus) return

    setSalvando(true)
    setErro('')

    try {
      await api.atualizarStatus({
        solicitacaoId: s.id,
        statusNovo: s.proximoStatus,
        comentario,
        responsavelId: usuario.id,
        justificativaAtraso: s.foraDoPrazoSla ? justificativa : null,
      })
      setComentario('')
      setJustificativa('')
      await carregar(s.protocolo)
    } catch (e) {
      setErro(e.message)
    } finally {
      setSalvando(false)
    }
  }

  const s = detalhe?.solicitacao

  return (
    <SidebarLayout
      titulo={s ? s.protocolo : 'Solicitação'}
      subtitulo="Visualização completa da demanda e movimentação de status."
    >
      <div className="mb-6">
        <Link
          to="/gestor"
          className="inline-flex cursor-pointer items-center text-sm font-semibold text-brand-700 hover:underline"
        >
          ← Voltar para a fila
        </Link>
      </div>

      {carregando && (
        <p className="text-sm text-slate-500">Carregando solicitação…</p>
      )}

      {erro && !s && (
        <div className="app-card border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">{erro}</div>
      )}

      {s && (
        <div className="mx-auto max-w-4xl space-y-6">
          <div className="app-card p-6 sm:p-8">
            <div className="flex flex-wrap items-start justify-between gap-4">
              <div>
                <p className="text-xs font-bold uppercase tracking-wider text-slate-400">Protocolo</p>
                <p className="mt-1 font-mono text-xl font-bold text-brand-800">{s.protocolo}</p>
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

            <div className="mt-8 grid gap-4 sm:grid-cols-2">
              <div className="sm:col-span-2 rounded-lg bg-slate-50 p-4">
                <p className="text-xs font-bold uppercase tracking-wide text-slate-400">Solicitante</p>
                <div className="mt-2">
                  <SolicitanteInfo item={s} />
                </div>
              </div>

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

            <div className="mt-6">
              <p className="text-xs font-bold uppercase tracking-wide text-slate-400">Descrição</p>
              <p className="mt-2 text-sm leading-relaxed text-slate-700">{s.descricao}</p>
            </div>

            {s.temAnexo && (
              <div className="mt-6 rounded-lg border border-brand-100 bg-brand-50/50 p-4">
                <p className="text-xs font-bold uppercase tracking-wide text-slate-400">Anexo</p>
                <a
                  href={api.anexoUrl(s.protocolo)}
                  target="_blank"
                  rel="noreferrer"
                  className="mt-2 inline-flex cursor-pointer rounded-lg border border-brand-200 bg-white px-4 py-2 text-sm font-semibold text-brand-700 hover:bg-brand-50"
                >
                  Baixar {s.anexo || 'arquivo'}
                </a>
              </div>
            )}

            {s.justificativaAtraso && (
              <div className="mt-4 rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-900">
                <span className="font-bold">Justificativa de atraso:</span> {s.justificativaAtraso}
              </div>
            )}
          </div>

          <div className="app-card p-6 sm:p-8">
            <h3 className="text-lg font-bold text-slate-900">Histórico de movimentações</h3>
            <div className="mt-5">
              <TimelineHistorico historico={detalhe.historico} />
            </div>
          </div>

          {s.proximoStatus ? (
            <div className="app-card p-6 sm:p-8">
              <h3 className="text-lg font-bold text-slate-900">Avançar status</h3>
              <p className="mt-2 text-sm text-slate-600">
                {s.status?.replace('_', ' ')} → <span className="font-semibold text-slate-800">{s.proximoStatus?.replace('_', ' ')}</span>
              </p>

              <div className="mt-6 space-y-4">
                <div>
                  <label htmlFor="comentario" className="mb-1.5 block text-sm font-semibold text-slate-700">
                    Comentário *
                  </label>
                  <textarea
                    id="comentario"
                    value={comentario}
                    onChange={(e) => setComentario(e.target.value)}
                    rows={4}
                    className="w-full rounded-lg border border-slate-200 px-4 py-3 text-sm outline-none focus:border-brand-400 focus:ring-4 focus:ring-brand-100"
                    placeholder="Descreva o que foi feito ou o próximo passo…"
                    required
                  />
                </div>

                {s.foraDoPrazoSla && (
                  <div>
                    <label htmlFor="justificativa" className="mb-1.5 block text-sm font-semibold text-amber-800">
                      Justificativa de atraso *
                    </label>
                    <textarea
                      id="justificativa"
                      value={justificativa}
                      onChange={(e) => setJustificativa(e.target.value)}
                      rows={3}
                      className="w-full rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-sm outline-none focus:border-amber-400 focus:ring-4 focus:ring-amber-100"
                      placeholder="Explique o motivo do atraso no SLA…"
                      required
                    />
                  </div>
                )}

                {erro && (
                  <div className="rounded-lg border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">{erro}</div>
                )}

                <button
                  type="button"
                  disabled={salvando || !comentario.trim() || (s.foraDoPrazoSla && !justificativa.trim())}
                  onClick={confirmarStatus}
                  className="w-full cursor-pointer rounded-lg bg-brand-600 py-3.5 text-sm font-bold text-white disabled:opacity-50 sm:w-auto sm:px-8"
                >
                  {salvando ? 'Salvando…' : 'Confirmar movimentação'}
                </button>
              </div>
            </div>
          ) : (
            <div className="app-card border-dashed py-8 text-center text-sm text-slate-500">
              Esta solicitação já foi encerrada — não há próximo status.
            </div>
          )}
        </div>
      )}
    </SidebarLayout>
  )
}

export { urlGestorProtocolo }
