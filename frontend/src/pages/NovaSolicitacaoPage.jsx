import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api } from '../api/api'
import AppIcon from '../components/AppIcon'
import SidebarLayout from '../components/SidebarLayout'
import { useAuth } from '../context/AuthContext'



export default function NovaSolicitacaoPage() {

  const navigate = useNavigate()

  const { usuario, anonimo } = useAuth()

  const [categorias, setCategorias] = useState([])

  const [prioridades, setPrioridades] = useState([])

  const [form, setForm] = useState({

    categoria: '',

    descricao: '',

    anexo: '',

    localizacao: '',

    bairro: '',

    prioridade: '',

    anonima: anonimo,

  })

  const [erro, setErro] = useState('')

  const [sucesso, setSucesso] = useState(null)

  const [carregando, setCarregando] = useState(false)



  useEffect(() => {

    Promise.all([api.getCategorias(), api.getPrioridades()]).then(([cats, prios]) => {

      setCategorias(cats)

      setPrioridades(prios)

      setForm((f) => ({

        ...f,

        categoria: cats[0]?.nome || '',

        prioridade: prios[1]?.nome || prios[0]?.nome || '',

        anonima: anonimo || f.anonima,

      }))

    })

  }, [anonimo])



  const handleChange = (campo, valor) => {

    setForm((f) => ({ ...f, [campo]: valor }))

  }



  const handleSubmit = async (event) => {

    event.preventDefault()

    setErro('')

    setCarregando(true)

    try {

      const payload = {

        ...form,

        anexo: form.anexo || null,

        usuarioId: anonimo || form.anonima ? null : usuario?.id,

      }

      const criada = await api.criarSolicitacao(payload)

      setSucesso(criada)

    } catch (e) {

      setErro(e.message)

    } finally {

      setCarregando(false)

    }

  }



  if (sucesso) {

    return (

      <SidebarLayout titulo="Solicitação registrada">

        <div className="app-card mx-auto max-w-lg p-8 text-center">

          <div className="mx-auto flex h-14 w-14 items-center justify-center rounded-full bg-emerald-100 text-emerald-700">
            <AppIcon name="check" size={32} />
          </div>

          <h2 className="mt-4 text-xl font-bold text-slate-900">Protocolo gerado</h2>

          <p className="mt-2 font-mono text-lg font-bold text-brand-700">{sucesso.protocolo}</p>

          <p className="mt-3 text-sm text-slate-500">

            Prazo SLA: {new Date(sucesso.prazoSla).toLocaleDateString('pt-BR')}

          </p>

          <div className="mt-6 flex flex-col gap-3 sm:flex-row sm:justify-center">

            <Link

              to={`/acompanhar/${encodeURIComponent(sucesso.protocolo)}`}

              className="rounded-lg bg-brand-600 px-5 py-3 text-sm font-bold text-white"

            >

              Acompanhar agora

            </Link>

            <button

              type="button"

              onClick={() => navigate('/cidadao')}

              className="rounded-lg border border-slate-200 px-5 py-3 text-sm font-semibold text-slate-700"

            >

              Voltar ao início

            </button>

          </div>

        </div>

      </SidebarLayout>

    )

  }



  return (

    <SidebarLayout

      titulo="Nova solicitação"

      subtitulo="Descreva o problema com clareza. Mínimo de 15 caracteres na descrição."

    >

      <form onSubmit={handleSubmit} className="app-card mx-auto max-w-2xl space-y-5 p-6 sm:p-8">

        <div className="grid gap-5 sm:grid-cols-2">

          <div>

            <label className="mb-1.5 block text-sm font-semibold text-slate-700">Categoria</label>

            <select

              value={form.categoria}

              onChange={(e) => handleChange('categoria', e.target.value)}

              className="w-full rounded-lg border border-slate-200 px-4 py-3 text-sm outline-none focus:border-brand-400 focus:ring-4 focus:ring-brand-100"

              required

            >

              {categorias.map((c) => (

                <option key={c.nome} value={c.nome}>{c.valor}</option>

              ))}

            </select>

          </div>

          <div>

            <label className="mb-1.5 block text-sm font-semibold text-slate-700">Prioridade</label>

            <select

              value={form.prioridade}

              onChange={(e) => handleChange('prioridade', e.target.value)}

              className="w-full rounded-lg border border-slate-200 px-4 py-3 text-sm outline-none focus:border-brand-400 focus:ring-4 focus:ring-brand-100"

              required

            >

              {prioridades.map((p) => (

                <option key={p.nome} value={p.nome}>

                  {p.nome} - SLA {p.diasSla} dia(s)

                </option>

              ))}

            </select>

          </div>

        </div>



        <div>

          <label className="mb-1.5 block text-sm font-semibold text-slate-700">Descrição</label>

          <textarea

            value={form.descricao}

            onChange={(e) => handleChange('descricao', e.target.value)}

            rows={4}

            className="w-full rounded-lg border border-slate-200 px-4 py-3 text-sm outline-none focus:border-brand-400 focus:ring-4 focus:ring-brand-100"

            placeholder="Explique o problema com detalhes suficientes para a equipe agir…"

            required

            minLength={15}

          />

        </div>



        <div className="grid gap-5 sm:grid-cols-2">

          <div>

            <label className="mb-1.5 block text-sm font-semibold text-slate-700">Localização</label>

            <input

              value={form.localizacao}

              onChange={(e) => handleChange('localizacao', e.target.value)}

              className="w-full rounded-lg border border-slate-200 px-4 py-3 text-sm outline-none focus:border-brand-400 focus:ring-4 focus:ring-brand-100"

              placeholder="Rua, número, referência…"

              required

            />

          </div>

          <div>

            <label className="mb-1.5 block text-sm font-semibold text-slate-700">Bairro</label>

            <input

              value={form.bairro}

              onChange={(e) => handleChange('bairro', e.target.value)}

              className="w-full rounded-lg border border-slate-200 px-4 py-3 text-sm outline-none focus:border-brand-400 focus:ring-4 focus:ring-brand-100"

              placeholder="Centro, Vila Nova…"

              required

            />

          </div>

        </div>



        <div>

          <label className="mb-1.5 block text-sm font-semibold text-slate-700">Anexo (opcional)</label>

          <input

            value={form.anexo}

            onChange={(e) => handleChange('anexo', e.target.value)}

            className="w-full rounded-lg border border-slate-200 px-4 py-3 text-sm outline-none focus:border-brand-400 focus:ring-4 focus:ring-brand-100"

            placeholder="Nome ou referência do arquivo"

          />

        </div>



        {usuario && !anonimo && (

          <label className="flex cursor-pointer items-center gap-3 rounded-lg border border-slate-100 bg-slate-50 px-4 py-3">

            <input

              type="checkbox"

              checked={form.anonima}

              onChange={(e) => handleChange('anonima', e.target.checked)}

              className="h-4 w-4 rounded border-slate-300 text-brand-600 focus:ring-brand-500"

            />

            <span className="text-sm text-slate-700">

              <span className="font-semibold">Registrar como anônimo</span>, oculta vínculo com sua conta

            </span>

          </label>

        )}



        {erro && (

          <div className="rounded-lg border border-rose-200 bg-rose-50 px-4 py-3 text-sm font-medium text-rose-700">{erro}</div>

        )}



        <button

          type="submit"

          disabled={carregando}

          className="w-full rounded-lg bg-brand-600 py-3.5 text-sm font-bold text-white disabled:opacity-60"

        >

          {carregando ? 'Enviando…' : 'Registrar solicitação'}

        </button>

      </form>

    </SidebarLayout>

  )

}


