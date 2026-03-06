import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '@/store/authStore'
import { useUIStore } from '@/store/uiStore'
import { authApi } from '@/api/auth'
import { Sun, Moon, Monitor } from 'lucide-react'

export default function Header() {
  const { t, i18n } = useTranslation()
  const navigate = useNavigate()
  const { user, clearAuth } = useAuthStore()
  const { toggleSidebar, language, setLanguage, theme, setTheme } = useUIStore()
  const [themeMenuOpen, setThemeMenuOpen] = useState(false)

  const handleLogout = async () => {
    try {
      await authApi.logout()
    } catch {
      // Clear auth even if server call fails
    }
    clearAuth()
    navigate('/login')
  }

  const toggleLanguage = () => {
    const newLang = language === 'en' ? 'lt' : 'en'
    setLanguage(newLang)
    i18n.changeLanguage(newLang)
  }

  const themeOptions = [
    { value: 'light' as const, label: t('theme.light', 'Light'), icon: Sun },
    { value: 'dark' as const, label: t('theme.dark', 'Dark'), icon: Moon },
    { value: 'system' as const, label: t('theme.system', 'System'), icon: Monitor },
  ]

  const currentThemeOption = themeOptions.find((opt) => opt.value === theme) || themeOptions[2]
  const ThemeIcon = currentThemeOption.icon

  return (
    <header className="h-16 bg-card border-b border-border flex items-center justify-between px-4">
      <button
        onClick={toggleSidebar}
        className="p-2 rounded-md hover:bg-muted text-foreground"
        aria-label="Toggle sidebar"
      >
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
        </svg>
      </button>

      <div className="flex items-center gap-2">
        {/* Theme Toggle */}
        <div className="relative">
          <button
            onClick={() => setThemeMenuOpen(!themeMenuOpen)}
            className="p-2 rounded-md hover:bg-muted text-foreground"
            aria-label="Toggle theme"
          >
            <ThemeIcon className="w-5 h-5" />
          </button>

          {themeMenuOpen && (
            <>
              <div
                className="fixed inset-0 z-40"
                onClick={() => setThemeMenuOpen(false)}
              />
              <div className="absolute right-0 mt-2 w-36 bg-popover border border-border rounded-md shadow-lg z-50">
                {themeOptions.map((option) => (
                  <button
                    key={option.value}
                    onClick={() => {
                      setTheme(option.value)
                      setThemeMenuOpen(false)
                    }}
                    className={`w-full flex items-center gap-2 px-3 py-2 text-sm hover:bg-muted ${
                      theme === option.value ? 'text-primary' : 'text-foreground'
                    }`}
                  >
                    <option.icon className="w-4 h-4" />
                    {option.label}
                  </button>
                ))}
              </div>
            </>
          )}
        </div>

        {/* Language Toggle */}
        <button
          onClick={toggleLanguage}
          className="px-3 py-1.5 text-sm border border-border rounded-md hover:bg-muted text-foreground"
        >
          {language === 'en' ? 'LT' : 'EN'}
        </button>

        <span className="text-muted-foreground">{user?.username}</span>

        <button
          onClick={handleLogout}
          className="px-4 py-2 text-sm text-destructive hover:bg-destructive/10 rounded-md"
        >
          {t('nav.logout', 'Logout')}
        </button>
      </div>
    </header>
  )
}
