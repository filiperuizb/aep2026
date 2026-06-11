import { createContext, useContext, useMemo, useState } from 'react'
import { api } from '../api/api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(() => {
    const salvo = localStorage.getItem('observacao_usuario')
    return salvo ? JSON.parse(salvo) : null
  })
  const [anonimo, setAnonimo] = useState(false)

  const login = (dados) => {
    setUsuario(dados)
    setAnonimo(false)
    localStorage.setItem('observacao_usuario', JSON.stringify(dados))
  }

  const entrarAnonimo = () => {
    setUsuario(null)
    setAnonimo(true)
    localStorage.removeItem('observacao_usuario')
  }

  const logout = async () => {
    try {
      await api.logout()
    } catch {
      // sessão já expirada ou usuário anônimo
    }
    setUsuario(null)
    setAnonimo(false)
    localStorage.removeItem('observacao_usuario')
  }

  const value = useMemo(
    () => ({
      usuario,
      anonimo,
      isGestor: usuario?.perfil === 'GESTOR',
      isCidadao: usuario?.perfil === 'CIDADAO',
      login,
      entrarAnonimo,
      logout,
    }),
    [usuario, anonimo],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) {
    throw new Error('useAuth deve ser usado dentro de AuthProvider')
  }
  return ctx
}
