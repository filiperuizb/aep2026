export default function LogoObserva({ tamanho = 'padrao', className = '' }) {
  const classes = {
    padrao: 'h-10 w-auto max-w-full object-contain',
    sidebar: 'brand-logo-full',
    login: 'logo-login',
    loginMobile: 'logo-login-mobile',
  }

  return (
    <img
      src="/logo.svg"
      alt="ObservaAção"
      className={`${classes[tamanho] || classes.padrao} ${className}`.trim()}
    />
  )
}
