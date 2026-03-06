import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { RelationshipFilterType } from '@/types'

type Theme = 'light' | 'dark' | 'system'

type RelationshipFilters = Record<RelationshipFilterType, boolean>

const DEFAULT_RELATIONSHIP_FILTERS: RelationshipFilters = {
  PARENT: true,
  SPOUSE: true,
  SIBLING: true,
  CHILD: true,
  COUSIN: true,
}

interface UIState {
  sidebarOpen: boolean
  language: 'en' | 'lt'
  theme: Theme
  relationshipFilters: RelationshipFilters
  toggleSidebar: () => void
  setSidebarOpen: (open: boolean) => void
  setLanguage: (lang: 'en' | 'lt') => void
  setTheme: (theme: Theme) => void
  setRelationshipFilter: (type: RelationshipFilterType, enabled: boolean) => void
  resetRelationshipFilters: () => void
}

function applyTheme(theme: Theme) {
  const root = document.documentElement
  const systemDark = window.matchMedia('(prefers-color-scheme: dark)').matches

  if (theme === 'dark' || (theme === 'system' && systemDark)) {
    root.classList.add('dark')
  } else {
    root.classList.remove('dark')
  }
}

export const useUIStore = create<UIState>()(
  persist(
    (set) => ({
      sidebarOpen: true,
      language: 'en',
      theme: 'system',
      relationshipFilters: { ...DEFAULT_RELATIONSHIP_FILTERS },

      toggleSidebar: () => set((state) => ({ sidebarOpen: !state.sidebarOpen })),
      setSidebarOpen: (open) => set({ sidebarOpen: open }),
      setLanguage: (lang) => set({ language: lang }),
      setTheme: (theme) => {
        applyTheme(theme)
        set({ theme })
      },
      setRelationshipFilter: (type, enabled) =>
        set((state) => ({
          relationshipFilters: {
            ...state.relationshipFilters,
            [type]: enabled,
          },
        })),
      resetRelationshipFilters: () =>
        set({ relationshipFilters: { ...DEFAULT_RELATIONSHIP_FILTERS } }),
    }),
    {
      name: 'ui-storage',
      onRehydrateStorage: () => (state) => {
        if (state?.theme) {
          applyTheme(state.theme)
        }
      },
    }
  )
)

// Listen for system theme changes
if (typeof window !== 'undefined') {
  window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', () => {
    const { theme } = useUIStore.getState()
    if (theme === 'system') {
      applyTheme('system')
    }
  })
}
