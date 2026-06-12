export default function SolicitanteInfo({ item }) {
  if (item.anonima || !item.nomeSolicitante) {
    return (
      <span className="inline-flex rounded-full bg-slate-100 px-2.5 py-1 text-xs font-semibold text-slate-600 ring-1 ring-slate-200 ring-inset">
        Solicitação anônima
      </span>
    )
  }

  return (
    <div>
      <p className="font-semibold text-slate-800">{item.nomeSolicitante}</p>
      {item.emailSolicitante && (
        <p className="text-sm text-slate-500">{item.emailSolicitante}</p>
      )}
    </div>
  )
}
