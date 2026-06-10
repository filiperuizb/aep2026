import { Link } from 'react-router-dom'
import AppIcon from '../components/AppIcon'
import SidebarLayout from '../components/SidebarLayout'
import { useAuth } from '../context/AuthContext'

const cards = [
  {
    to: '/cidadao/nova',
    titulo: 'Nova solicitação',
    desc: 'Registre iluminação, buraco, zeladoria, saúde e mais.',
    icon: 'file-plus',
  },
  {
    to: '/acompanhar',
    titulo: 'Acompanhar protocolo',
    desc: 'Consulte status, prazo de SLA e histórico de movimentações.',
    icon: 'search',
  },
  {
    to: '/cidadao/minhas',
    titulo: 'Minhas solicitações',
    desc: 'Veja tudo o que você registrou identificado.',
    icon: 'folder',
    requerLogin: true,
  },
]

export default function CidadaoPage() {
  const { usuario, anonimo } = useAuth()

  return (
    <SidebarLayout
      titulo={usuario ? `Olá, ${usuario.nome.split(' ')[0]}` : 'Modo anônimo'}
      subtitulo={
        anonimo
          ? 'Você pode registrar demandas protegidas e acompanhar pelo protocolo gerado.'
          : 'Gerencie suas solicitações e acompanhe o andamento com transparência.'
      }
    >
      {anonimo && (
        <div className="app-card mb-6 border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-900">
          <span className="font-bold">Identidade protegida.</span> Suas solicitações serão anônimas. Guarde o protocolo para acompanhar.
        </div>
      )}

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {cards
          .filter((c) => !c.requerLogin || (usuario && !anonimo))
          .map((card) => (
            <Link key={card.to} to={card.to} className="app-card app-card-hover block p-6">
              <div className="flex h-11 w-11 items-center justify-center rounded-lg bg-brand-600 text-white">
                <AppIcon name={card.icon} size={22} />
              </div>
              <h2 className="mt-4 text-lg font-bold text-slate-900">{card.titulo}</h2>
              <p className="mt-2 text-sm leading-relaxed text-slate-500">{card.desc}</p>
              <span className="mt-4 inline-flex items-center gap-1.5 text-sm font-semibold text-brand-600">
                Acessar
                <AppIcon name="chevron-right" size={14} />
              </span>
            </Link>
          ))}
      </div>
    </SidebarLayout>
  )
}
