import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'

export default function NotFoundPage() {
  const { t } = useTranslation()

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="text-center">
        <h1 className="text-9xl font-bold text-gray-200">404</h1>
        <h2 className="text-2xl font-semibold text-gray-900 mt-4">
          {t('notFound.title', 'Page Not Found')}
        </h2>
        <p className="text-gray-500 mt-2">
          {t('notFound.message', "The page you're looking for doesn't exist.")}
        </p>
        <Link
          to="/"
          className="inline-block mt-6 px-6 py-3 bg-blue-600 text-white rounded-md hover:bg-blue-700"
        >
          {t('notFound.goHome', 'Go Home')}
        </Link>
      </div>
    </div>
  )
}
