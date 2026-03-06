import { useTranslation } from 'react-i18next'
import { Link } from 'react-router-dom'
import { useAuthStore } from '@/store/authStore'
import { Button } from '@/components/ui/button'

export default function DashboardPage() {
  const { t } = useTranslation()
  const user = useAuthStore((state) => state.user)

  return (
    <div className="p-6">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-foreground">
          {t('dashboard.title', 'Dashboard')}
        </h1>
        <p className="mt-2 text-muted-foreground">
          {t('dashboard.welcome', 'Welcome back')}, {user?.displayName}!
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {/* Quick Actions */}
        <div className="bg-card rounded-lg shadow border border-border p-6">
          <h2 className="text-lg font-semibold text-foreground mb-4">
            {t('dashboard.quickActions', 'Quick Actions')}
          </h2>
          <div className="space-y-3">
            <Link to="/tree">
              <Button variant="outline" className="w-full justify-start">
                {t('dashboard.viewTree', 'View Family Tree')}
              </Button>
            </Link>
          </div>
        </div>

        {/* Stats */}
        <div className="bg-card rounded-lg shadow border border-border p-6">
          <h2 className="text-lg font-semibold text-foreground mb-4">
            {t('dashboard.statistics', 'Statistics')}
          </h2>
          <div className="space-y-4">
            <div className="flex justify-between items-center">
              <span className="text-muted-foreground">{t('dashboard.familyMembers', 'Family Members')}</span>
              <span className="text-2xl font-bold text-primary">-</span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-muted-foreground">{t('dashboard.photos', 'Photos')}</span>
              <span className="text-2xl font-bold text-primary">-</span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-muted-foreground">{t('dashboard.events', 'Events')}</span>
              <span className="text-2xl font-bold text-primary">-</span>
            </div>
          </div>
        </div>

        {/* Recent Activity */}
        <div className="bg-card rounded-lg shadow border border-border p-6">
          <h2 className="text-lg font-semibold text-foreground mb-4">
            {t('dashboard.recentActivity', 'Recent Activity')}
          </h2>
          <p className="text-muted-foreground text-sm">
            {t('dashboard.noActivity', 'No recent activity')}
          </p>
        </div>
      </div>
    </div>
  )
}
