import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api/api'
import { useAuth } from '../context/AuthContext'
import LogoObserva from '../components/LogoObserva'
import loginImagem from '../../public/teste.png'

const LOGIN_IMAGEM = loginImagem

export default function LoginPage() {
  const navigate = useNavigate()
  const { login, entrarAnonimo } = useAuth()
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)

  const handleLogin = async (event) => {
    event.preventDefault()
    setErro('')
    setCarregando(true)
    try {
      const usuario = await api.login(email, senha)
      login(usuario)
      navigate(usuario.perfil === 'GESTOR' ? '/gestor' : '/cidadao')
    } catch (e) {
      setErro(e.message)
    } finally {
      setCarregando(false)
    }
  }

  const handleAnonimo = () => {
    entrarAnonimo()
    navigate('/cidadao/nova')
  }

  const entrarComTeste = async (tipo) => {
    setErro('')
    setCarregando(true)
    const credenciais =
      tipo === 'gestor'
        ? { email: 'admin@admin.com', senha: '123' }
        : { email: 'cidadao@test.com', senha: '123' }
    try {
      const usuario = await api.login(credenciais.email, credenciais.senha)
      login(usuario)
      navigate(usuario.perfil === 'GESTOR' ? '/gestor' : '/cidadao')
    } catch (e) {
      setErro(e.message)
    } finally {
      setCarregando(false)
    }
  }

  return (
    <div
      className="login-page relative flex min-h-screen items-center justify-center p-4 sm:p-8"
      style={{ '--login-bg': `url(${LOGIN_IMAGEM})` }}
    >
      <div className="login-page-bg" aria-hidden />
      <div className="login-page-overlay" aria-hidden />

      <div className="login-card relative z-10 w-full max-w-[880px] overflow-hidden rounded-2xl shadow-2xl">
        <img src={LOGIN_IMAGEM} alt="" className="login-card-bg" aria-hidden />

        <div className="relative z-10 flex min-h-[520px] flex-col md:flex-row">
          <aside className="login-panel-left relative hidden md:flex md:w-[43%] md:flex-col md:justify-end md:p-8">
            <div className="login-panel-left-overlay absolute inset-0" aria-hidden />
            <div className="login-panel-brand relative z-10">
              <LogoObserva tamanho="login" />
              <p className="login-panel-tagline">
                Transparência no atendimento público.
              </p>
            </div>
          </aside>

          <div className="login-panel-form flex flex-1 flex-col justify-center px-8 py-9 sm:px-10 sm:py-10">
            <div className="mb-6 md:hidden">
              <LogoObserva tamanho="loginMobile" />
            </div>

            <div>
              <h1 className="font-display text-4xl leading-none text-white sm:text-5xl">BEM-VINDO</h1>
              <p className="mt-1 text-sm text-blue-100/75">Entre para continuar no sistema</p>
            </div>

            <form onSubmit={handleLogin} className="mt-7 space-y-4">
              <div className="login-field">
                <span className="login-field-icon login-field-icon-dark" aria-hidden>
                  <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M20 21a8 8 0 1 0-16 0" />
                    <circle cx="12" cy="7" r="4" />
                  </svg>
                </span>
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="E-mail"
                  className="login-input login-input-dark"
                  required
                />
              </div>

              <div className="login-field">
                <span className="login-field-icon login-field-icon-dark" aria-hidden>
                  <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <rect x="3" y="11" width="18" height="11" rx="2" />
                    <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                  </svg>
                </span>
                <input
                  type="password"
                  value={senha}
                  onChange={(e) => setSenha(e.target.value)}
                  placeholder="Senha"
                  className="login-input login-input-dark"
                  required
                />
              </div>

              {erro && (
                <p className="rounded-lg bg-red-950/50 px-3 py-2 text-sm text-red-200 ring-1 ring-red-400/40">{erro}</p>
              )}

              <button type="submit" disabled={carregando} className="login-btn">
                {carregando ? 'Entrando…' : 'Entrar'}
              </button>
            </form>

            <button
              type="button"
              onClick={handleAnonimo}
              disabled={carregando}
              className="login-btn-outline mt-4"
            >
              Acesso público anônimo
            </button>

            <div className="login-divider">
              <span>Atalhos para apresentação</span>
            </div>

            <div className="space-y-2.5">
              <button
                type="button"
                disabled={carregando}
                onClick={() => entrarComTeste('gestor')}
                className="login-btn-secondary"
              >
                Gestor teste
              </button>
              <button
                type="button"
                disabled={carregando}
                onClick={() => entrarComTeste('cidadao')}
                className="login-btn-secondary"
              >
                Cidadão teste
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
