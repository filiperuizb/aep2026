export default function TimelineHistorico({ historico }) {
  if (!historico?.length) {
    return (
      <div className="rounded-2xl border border-dashed border-slate-200 bg-slate-50 px-4 py-8 text-center text-sm text-slate-500">
        Nenhuma movimentação registrada ainda.
      </div>
    )
  }

  return (
    <div className="space-y-0">
      {historico.map((item, index) => (
        <div key={item.id} className="relative flex gap-4 pb-6 last:pb-0">
          {index < historico.length - 1 && (
            <div className="absolute left-[11px] top-6 h-[calc(100%-12px)] w-0.5 timeline-line" />
          )}
          <div className="relative z-10 mt-1 h-6 w-6 shrink-0 rounded-full border-4 border-white bg-brand-500 shadow-md shadow-brand-500/30" />
          <div className="min-w-0 flex-1 rounded-2xl border border-slate-100 bg-white p-4 shadow-sm">
            <div className="flex flex-wrap items-center gap-2">
              <span className="text-xs font-bold uppercase tracking-wide text-brand-700">
                {item.statusNovo?.replace('_', ' ')}
              </span>
              <span className="text-xs text-slate-400">•</span>
              <span className="text-xs text-slate-500">{new Date(item.dataMovimentacao).toLocaleString('pt-BR')}</span>
            </div>
            <p className="mt-2 text-sm text-slate-700">{item.comentario}</p>
            <p className="mt-2 text-xs font-medium text-slate-400">Responsável: {item.nomeResponsavel}</p>
          </div>
        </div>
      ))}
    </div>
  )
}
