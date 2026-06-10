export default function PriorityBadge({ prioridade }) {
  const map = {
    ALTA: 'bg-rose-100 text-rose-800 ring-rose-200',
    MEDIA: 'bg-orange-100 text-orange-800 ring-orange-200',
    BAIXA: 'bg-teal-100 text-teal-800 ring-teal-200',
  }
  const labels = { ALTA: 'Alta', MEDIA: 'Média', BAIXA: 'Baixa' }

  return (
    <span className={`inline-flex items-center rounded-full px-2.5 py-1 text-xs font-semibold ring-1 ring-inset ${map[prioridade] || map.BAIXA}`}>
      {labels[prioridade] || prioridade}
    </span>
  )
}
