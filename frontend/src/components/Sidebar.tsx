import { Link, useLocation } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useUIStore } from '@/store/uiStore'
import { useAuthStore } from '@/store/authStore'
import { Home, TreePine, Settings } from 'lucide-react'

export default function Sidebar() {
  const { t } = useTranslation()
  const { sidebarOpen } = useUIStore()
  const { isAdmin } = useAuthStore()
  const location = useLocation()

  const navItems = [
    { path: '/dashboard', label: t('nav.dashboard', 'Dashboard'), icon: Home },
    { path: '/tree', label: t('nav.tree', 'Family Tree'), icon: TreePine },
  ]

  if (isAdmin) {
    navItems.push({ path: '/admin', label: t('nav.admin', 'Admin'), icon: Settings })
  }

  return (
    <aside
      className={`fixed left-0 top-0 h-full bg-card border-r border-border transition-all duration-300 z-40 ${
        sidebarOpen ? 'w-64' : 'w-16'
      }`}
    >
      <div className="p-4 border-b border-border">
        <h1 className={`font-bold text-xl text-primary ${!sidebarOpen && 'text-center'}`}>
          {sidebarOpen ? 'Geneinator' : 'G'}
        </h1>
      </div>
      <nav className="p-2">
        {navItems.map((item) => {
          const Icon = item.icon
          return (
            <Link
              key={item.path}
              to={item.path}
              className={`flex items-center px-3 py-2 rounded-md mb-1 transition-colors ${
                location.pathname === item.path
                  ? 'bg-primary/10 text-primary'
                  : 'text-muted-foreground hover:bg-muted hover:text-foreground'
              }`}
            >
              <Icon className="w-5 h-5" />
              {sidebarOpen && <span className="ml-3">{item.label}</span>}
            </Link>
          )
        })}
      </nav>
    </aside>
  )
}
