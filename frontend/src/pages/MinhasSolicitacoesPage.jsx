import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/api'
import SidebarLayout from '../components/SidebarLayout'
import PriorityBadge from '../components/PriorityBadge'
import StatusBadge from '../components/StatusBadge'
import { useAuth } from '../context/AuthContext'
import { formatarData } from '../utils/labels'

export default function MinhasSolicitacoesPage() {
  const { usuario } = useAuth()
  const [lista, setLista] = useState([])
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(true)

  useEffect(() => {
    if (!usuario?.id) return
    api
      .listarPorUsuario(usuario.id)
      .then(setLista)
      .catch((e) => setErro(e.message))
      .finally(() => setCarregando(false))
  }, [usuario])

  return (
    <SidebarLayout
      titulo="Minhas solicitações"
      subtitulo="Demandas registradas com sua conta identificada."
    >
      {carregando && <p className="text-sm text-slate-500">Carregando…</p>}
      {erro && <div className="app-card border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">{erro}</div>}

      {!carregando && !erro && lista.length === 0 && (
        <div className="app-card border-dashed px-6 py-16 text-center">
          <p className="font-bold text-slate-800">Nenhuma solicitação ainda</p>
          <p className="mt-1 text-sm text-slate-500">Registre sua primeira demanda para acompanhar aqui.</p>
          <Link to="/cidadao/nova" className="mt-5 inline-block rounded-lg bg-brand-600 px-5 py-3 text-sm font-bold text-white">
            Nova solicitação
          </Link>
        </div>
      )}

      <div className="space-y-3">
        {lista.map((item) => (
          <Link
            key={item.id}
            to={`/acompanhar/${encodeURIComponent(item.protocolo)}`}
            className="app-card app-card-hover block p-5"
          >
            <div className="flex flex-wrap items-start justify-between gap-3">
              <div>
                <p className="font-mono text-sm font-bold text-brand-800">{item.protocolo}</p>
                <p className="mt-1 line-clamp-2 text-sm text-slate-600">{item.descricao}</p>
              </div>
              <div className="flex flex-wrap gap-2">
                <StatusBadge status={item.status} />
                <PriorityBadge prioridade={item.prioridade} />
              </div>
            </div>
            <p className="mt-3 text-xs text-slate-400">
              {item.bairro} · {formatarData(item.dataCriacao)}
            </p>
          </Link>
        ))}
      </div>
    </SidebarLayout>
  )
}
