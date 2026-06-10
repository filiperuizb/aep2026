export const statusLabel = {
  ABERTO: 'Aberto',
  TRIAGEM: 'Triagem',
  EM_EXECUCAO: 'Em execução',
  RESOLVIDO: 'Resolvido',
  ENCERRADO: 'Encerrado',
}

export const statusColor = {
  ABERTO: 'bg-sky-100 text-sky-800 ring-sky-200',
  TRIAGEM: 'bg-amber-100 text-amber-800 ring-amber-200',
  EM_EXECUCAO: 'bg-violet-100 text-violet-800 ring-violet-200',
  RESOLVIDO: 'bg-emerald-100 text-emerald-800 ring-emerald-200',
  ENCERRADO: 'bg-slate-100 text-slate-700 ring-slate-200',
}

export const prioridadeLabel = {
  ALTA: 'Alta',
  MEDIA: 'Média',
  BAIXA: 'Baixa',
}

export const prioridadeColor = {
  ALTA: 'bg-rose-100 text-rose-800 ring-rose-200',
  MEDIA: 'bg-orange-100 text-orange-800 ring-orange-200',
  BAIXA: 'bg-teal-100 text-teal-800 ring-teal-200',
}

export function formatarData(valor) {
  if (!valor) return '—'
  const data = new Date(valor)
  return data.toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}
