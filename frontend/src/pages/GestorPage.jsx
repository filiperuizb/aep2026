import { useEffect, useState } from 'react'

import { api } from '../api/api'

import SidebarLayout from '../components/SidebarLayout'

import Modal from '../components/Modal'

import PriorityBadge from '../components/PriorityBadge'

import StatusBadge from '../components/StatusBadge'

import { useAuth } from '../context/AuthContext'

import { formatarData } from '../utils/labels'



export default function GestorPage() {

  const { usuario } = useAuth()

  const [fila, setFila] = useState([])

  const [statusLista, setStatusLista] = useState([])

  const [filtro, setFiltro] = useState({ prioridade: '', categoria: '', status: '', bairro: '' })

  const [selecionada, setSelecionada] = useState(null)

  const [comentario, setComentario] = useState('')

  const [justificativa, setJustificativa] = useState('')

  const [erro, setErro] = useState('')

  const [carregando, setCarregando] = useState(true)

  const [salvando, setSalvando] = useState(false)



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



  const confirmarStatus = async () => {

    if (!selecionada?.proximoStatus) return

    setSalvando(true)

    setErro('')

    try {

      await api.atualizarStatus({

        solicitacaoId: selecionada.id,

        statusNovo: selecionada.proximoStatus,

        comentario,

        responsavelId: usuario.id,

        justificativaAtraso: selecionada.foraDoPrazoSla ? justificativa : null,

      })

      setSelecionada(null)

      setComentario('')

      setJustificativa('')

      carregarFila()

    } catch (e) {

      setErro(e.message)

    } finally {

      setSalvando(false)

    }

  }



  return (

    <SidebarLayout

      titulo="Painel do gestor"

      subtitulo="Fila ordenada por prioridade. Avance o status com comentário obrigatório."

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



      {erro && !selecionada && (

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

                      {item.proximoStatus ? (

                        <button

                          type="button"

                          onClick={() => {

                            setSelecionada(item)

                            setComentario('')

                            setJustificativa('')

                            setErro('')

                          }}

                          className="rounded-lg bg-brand-600 px-3 py-1.5 text-xs font-bold text-white hover:bg-brand-700"

                        >

                          Avançar

                        </button>

                      ) : (

                        <span className="text-xs text-slate-400">Encerrada</span>

                      )}

                    </td>

                  </tr>

                ))}

              </tbody>

            </table>

          </div>

        </div>

      )}



      <Modal

        aberto={!!selecionada}

        titulo="Atualizar status"

        onFechar={() => setSelecionada(null)}

        largo

      >

        {selecionada && (

          <div className="space-y-4">

            <div className="rounded-lg bg-slate-50 p-4 text-sm">

              <p className="font-mono font-bold text-brand-800">{selecionada.protocolo}</p>

              <p className="mt-2 text-slate-600">{selecionada.descricao}</p>

              <p className="mt-3 font-semibold text-slate-800">

                {selecionada.status?.replace('_', ' ')} para {selecionada.proximoStatus?.replace('_', ' ')}

              </p>

            </div>



            <div>

              <label className="mb-1.5 block text-sm font-semibold text-slate-700">Comentário *</label>

              <textarea

                value={comentario}

                onChange={(e) => setComentario(e.target.value)}

                rows={3}

                className="w-full rounded-lg border border-slate-200 px-4 py-3 text-sm outline-none focus:border-brand-400 focus:ring-4 focus:ring-brand-100"

                placeholder="Descreva o que foi feito ou o próximo passo…"

                required

              />

            </div>



            {selecionada.foraDoPrazoSla && (

              <div>

                <label className="mb-1.5 block text-sm font-semibold text-amber-800">Justificativa de atraso *</label>

                <textarea

                  value={justificativa}

                  onChange={(e) => setJustificativa(e.target.value)}

                  rows={2}

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

              disabled={salvando || !comentario.trim() || (selecionada.foraDoPrazoSla && !justificativa.trim())}

              onClick={confirmarStatus}

              className="w-full rounded-lg bg-brand-600 py-3 text-sm font-bold text-white disabled:opacity-50"

            >

              {salvando ? 'Salvando…' : 'Confirmar movimentação'}

            </button>

          </div>

        )}

      </Modal>

    </SidebarLayout>

  )

}


