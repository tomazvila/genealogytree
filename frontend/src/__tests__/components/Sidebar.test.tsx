import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { describe, it, expect, vi } from 'vitest'
import Sidebar from '@/components/Sidebar'

// Mock i18next
vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (_key: string, fallback: string) => fallback,
  }),
}))

// Mock the UI store
vi.mock('@/store/uiStore', () => ({
  useUIStore: () => ({
    sidebarOpen: true,
  }),
}))

// Mock the auth store
vi.mock('@/store/authStore', () => ({
  useAuthStore: () => ({
    isAdmin: false,
  }),
}))

function renderSidebar() {
  return render(
    <MemoryRouter initialEntries={['/dashboard']}>
      <Sidebar />
    </MemoryRouter>
  )
}

describe('Sidebar', () => {
  it('should display Dashboard navigation link', () => {
    renderSidebar()
    expect(screen.getByRole('link', { name: /dashboard/i })).toBeInTheDocument()
  })

  it('should display Family Tree navigation link', () => {
    renderSidebar()
    expect(screen.getByRole('link', { name: /family tree/i })).toBeInTheDocument()
  })

  it('should NOT display Photos navigation link', () => {
    renderSidebar()
    expect(screen.queryByRole('link', { name: /photos/i })).not.toBeInTheDocument()
  })
})
