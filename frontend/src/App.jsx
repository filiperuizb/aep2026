import { Navigate, Route, Routes } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import AcompanharPage from './pages/AcompanharPage'
import CidadaoPage from './pages/CidadaoPage'
import GestorPage from './pages/GestorPage'
import GestorProtocoloPage from './pages/GestorProtocoloPage'
import LoginPage from './pages/LoginPage'
import MinhasSolicitacoesPage from './pages/MinhasSolicitacoesPage'
import NovaSolicitacaoPage from './pages/NovaSolicitacaoPage'

function RotaCidadao({ children }) {
  const { usuario, anonimo, isGestor } = useAuth()
  if (isGestor) return <Navigate to="/gestor" replace />
  if (!usuario && !anonimo) return <Navigate to="/" replace />
  return children
}

function RotaGestor({ children }) {
  const { isGestor } = useAuth()
  if (!isGestor) return <Navigate to="/" replace />
  return children
}

function RotaLogado({ children }) {
  const { usuario, anonimo } = useAuth()
  if (!usuario || anonimo) return <Navigate to="/cidadao" replace />
  return children
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />
      <Route path="/acompanhar" element={<AcompanharPage />} />
      <Route path="/acompanhar/:protocolo" element={<AcompanharPage />} />
      <Route
        path="/cidadao"
        element={
          <RotaCidadao>
            <CidadaoPage />
          </RotaCidadao>
        }
      />
      <Route
        path="/cidadao/nova"
        element={
          <RotaCidadao>
            <NovaSolicitacaoPage />
          </RotaCidadao>
        }
      />
      <Route
        path="/cidadao/minhas"
        element={
          <RotaCidadao>
            <RotaLogado>
              <MinhasSolicitacoesPage />
            </RotaLogado>
          </RotaCidadao>
        }
      />
      <Route
        path="/gestor/:protocolo"
        element={
          <RotaGestor>
            <GestorProtocoloPage />
          </RotaGestor>
        }
      />
      <Route
        path="/gestor"
        element={
          <RotaGestor>
            <GestorPage />
          </RotaGestor>
        }
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
