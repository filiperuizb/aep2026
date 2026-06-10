export default function StatusBadge({ status }) {
  const map = {
    ABERTO: 'bg-sky-100 text-sky-800 ring-sky-200',
    TRIAGEM: 'bg-amber-100 text-amber-800 ring-amber-200',
    EM_EXECUCAO: 'bg-violet-100 text-violet-800 ring-violet-200',
    RESOLVIDO: 'bg-emerald-100 text-emerald-800 ring-emerald-200',
    ENCERRADO: 'bg-slate-100 text-slate-700 ring-slate-200',
  }
  const labels = {
    ABERTO: 'Aberto',
    TRIAGEM: 'Triagem',
    EM_EXECUCAO: 'Em execução',
    RESOLVIDO: 'Resolvido',
    ENCERRADO: 'Encerrado',
  }

  return (
    <span className={`inline-flex items-center rounded-full px-2.5 py-1 text-xs font-semibold ring-1 ring-inset ${map[status] || map.ENCERRADO}`}>
      {labels[status] || status}
    </span>
  )
}
