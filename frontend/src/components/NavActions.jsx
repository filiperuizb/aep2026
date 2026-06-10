import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function NavActions({ voltarPara }) {
  const navigate = useNavigate()
  const { usuario, anonimo, logout } = useAuth()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  return (
    <div className="flex items-center gap-2 sm:gap-3">
      {voltarPara && (
        <Link
          to={voltarPara}
          className="rounded-xl px-3 py-2 text-sm font-semibold text-slate-600 transition hover:bg-slate-100"
        >
          Voltar
        </Link>
      )}
      {(usuario || anonimo) && (
        <div className="hidden items-center gap-2 rounded-xl bg-slate-50 px-3 py-2 sm:flex">
          <span className="h-2 w-2 rounded-full bg-brand-500" />
          <span className="text-sm font-medium text-slate-700">
            {usuario ? usuario.nome : 'Anônimo'}
          </span>
        </div>
      )}
      {(usuario || anonimo) && (
        <button
          type="button"
          onClick={handleLogout}
          className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm font-semibold text-slate-700 transition hover:border-rose-200 hover:bg-rose-50 hover:text-rose-700"
        >
          Sair
        </button>
      )}
    </div>
  )
}
