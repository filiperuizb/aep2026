import {
  CheckCircle2,
  ChevronRight,
  FilePen,
  FolderOpen,
  Home,
  LayoutDashboard,
  List,
  LogOut,
  PlusCircle,
  Search,
  X,
} from 'lucide-react'

const icones = {
  home: Home,
  edit: FilePen,
  search: Search,
  list: List,
  layout: LayoutDashboard,
  'log-out': LogOut,
  x: X,
  check: CheckCircle2,
  'file-plus': PlusCircle,
  folder: FolderOpen,
  'chevron-right': ChevronRight,
}

export default function AppIcon({ name, size = 16, className = '' }) {
  const Icone = icones[name] || Home
  return <Icone size={size} className={className} strokeWidth={2} aria-hidden />
}
