import { Link, useLocation, useNavigate } from 'react-router-dom'
import LogoObserva from './LogoObserva'
import NavIcon from './NavIcon'
import { useAuth } from '../context/AuthContext'

function NavItem({ to, label, icon, ativo }) {
  return (
    <Link to={to} className={`nav-link ${ativo ? 'active' : ''}`}>
      <NavIcon name={icon} />
      <span className="nav-link-label">{label}</span>
    </Link>
  )
}

function roleLabel(isGestor, anonimo) {
  if (isGestor) return 'Gestor'
  if (anonimo) return 'Acesso público'
  return 'Cidadão'
}

export default function SidebarLayout({ titulo, subtitulo, children }) {
  const { pathname } = useLocation()
  const navigate = useNavigate()
  const { usuario, anonimo, isGestor, logout } = useAuth()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  const navCidadao = [
    { to: '/cidadao', label: 'Início', icon: 'home', show: true },
    { to: '/cidadao/nova', label: 'Nova solicitação', icon: 'edit', show: true },
    { to: '/acompanhar', label: 'Acompanhar protocolo', icon: 'search', show: true },
    { to: '/cidadao/minhas', label: 'Minhas solicitações', icon: 'list', show: usuario && !anonimo },
  ].filter((item) => item.show)

  const navGestor = [
    { to: '/gestor', label: 'Painel de atendimento', icon: 'layout' },
  ]

  const navItems = isGestor ? navGestor : navCidadao
  const nomeExibicao = usuario?.nome || 'Acesso anônimo'
  const inicial = nomeExibicao.charAt(0).toUpperCase()

  return (
    <div className="app-shell">
      <div className="sidebar-slot">
        <aside className="app-sidebar">
          <div className="brand-block">
            <div className="brand-expanded">
              <LogoObserva tamanho="sidebar" />
              <p className="brand-subtitle">Atendimento público</p>
            </div>
          </div>

          <nav className="nav-list" aria-label="Navegação principal">
            <p className="nav-section-title">Principal</p>
            {navItems.map((item) => (
              <NavItem
                key={item.to}
                to={item.to}
                label={item.label}
                icon={item.icon}
                ativo={pathname === item.to}
              />
            ))}
          </nav>

          <footer className="sidebar-footer">
            <button type="button" className="sidebar-logout-btn" onClick={handleLogout}>
              <NavIcon name="log-out" size={15} />
              <span>Sair</span>
            </button>
          </footer>
        </aside>
      </div>

      <div className="main-slot">
        <header className="topbar">
          <div className="topbar-text">
            {titulo && <h1>{titulo}</h1>}
            {subtitulo && (
              <>
                <span className="topbar-divider" />
                <p>{subtitulo}</p>
              </>
            )}
          </div>

          <div className="topbar-actions">
            <div className="topbar-user">
              <span className="topbar-avatar">{inicial}</span>
              <div className="topbar-user-info">
                <span className="topbar-user-name">{nomeExibicao}</span>
                <span className="topbar-user-role">{roleLabel(isGestor, anonimo)}</span>
              </div>
            </div>
            <span className="topbar-actions-divider" />
            <button
              type="button"
              className="topbar-action-btn topbar-action-btn--logout"
              onClick={handleLogout}
              title="Sair"
              aria-label="Sair da conta"
            >
              <NavIcon name="log-out" size={15} />
            </button>
          </div>
        </header>

        <main className="main-content">
          {children}
        </main>
      </div>
    </div>
  )
}
