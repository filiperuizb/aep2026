import AppIcon from './AppIcon'

export default function NavIcon({ name, size = 16 }) {
  return (
    <span className="nav-link-icon" style={{ width: size, height: size }}>
      <AppIcon name={name} size={size} />
    </span>
  )
}
