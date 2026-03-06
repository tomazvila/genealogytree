import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'

export default function WelcomePage() {
  const { t } = useTranslation()

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-background">
      <div className="text-center max-w-2xl px-4">
        <h1 className="text-5xl font-bold text-foreground mb-6">
          {t('welcome.title', 'Geneinator')}
        </h1>
        <p className="text-xl text-muted-foreground mb-8">
          {t('welcome.subtitle', 'Preserve your family history. Connect generations.')}
        </p>
        <div className="space-x-4">
          <Link
            to="/login"
            className="inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-md bg-primary text-primary-foreground hover:bg-primary/90"
          >
            {t('welcome.login', 'Sign In')}
          </Link>
          <Link
            to="/register"
            className="inline-flex items-center px-6 py-3 border border-border text-base font-medium rounded-md text-foreground bg-secondary hover:bg-secondary/80"
          >
            {t('welcome.register', 'Register')}
          </Link>
        </div>
      </div>
    </div>
  )
}
