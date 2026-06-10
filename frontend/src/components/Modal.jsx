import AppIcon from './AppIcon'

export default function Modal({ aberto, titulo, onFechar, children, largo }) {
  if (!aberto) return null

  return (
    <div className="fixed inset-0 z-50 flex items-end justify-center p-4 sm:items-center">
      <button
        type="button"
        aria-label="Fechar"
        className="modal-backdrop absolute inset-0 bg-slate-900/40 backdrop-blur-sm"
        onClick={onFechar}
      />
      <div
        className={`relative max-h-[90vh] w-full overflow-y-auto rounded-3xl bg-white p-6 shadow-2xl shadow-slate-900/20 animate-fade-up ${largo ? 'max-w-2xl' : 'max-w-lg'}`}
      >
        <div className="mb-5 flex items-start justify-between gap-4">
          <h2 className="text-lg font-bold text-slate-900">{titulo}</h2>
          <button type="button" onClick={onFechar} className="modal-close-btn rounded-lg p-1 text-slate-400 transition hover:bg-slate-100 hover:text-slate-700">
            <AppIcon name="x" size={18} />
          </button>
        </div>
        {children}
      </div>
    </div>
  )
}
