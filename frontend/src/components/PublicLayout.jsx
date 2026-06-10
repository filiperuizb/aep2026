import { Link } from 'react-router-dom'
import LogoObserva from './LogoObserva'

export default function PublicLayout({ titulo, subtitulo, children, voltarPara = '/' }) {
  return (
    <div className="app-shell-public">
      <div className="main-slot main-slot--solo">
        <header className="topbar">
          <Link to={voltarPara}>
            <LogoObserva tamanho="padrao" className="!h-10" />
          </Link>
          <div className="topbar-actions">
            <Link to="/" className="topbar-link-back">
              Voltar ao login
            </Link>
          </div>
        </header>
        <main className="main-content">
          {(titulo || subtitulo) && (
            <div className="page-heading">
              {titulo && <h1>{titulo}</h1>}
              {subtitulo && <p>{subtitulo}</p>}
            </div>
          )}
          {children}
        </main>
      </div>
    </div>
  )
}
