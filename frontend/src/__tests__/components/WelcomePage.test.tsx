import { render, screen } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import { describe, it, expect } from 'vitest'
import WelcomePage from '@/pages/WelcomePage'

// Wrapper for components that need router
const renderWithRouter = (component: React.ReactNode) => {
  return render(
    <BrowserRouter>
      {component}
    </BrowserRouter>
  )
}

describe('WelcomePage', () => {
  it('should render the title', () => {
    renderWithRouter(<WelcomePage />)

    expect(screen.getByText('Geneinator')).toBeInTheDocument()
  })

  it('should render login and register links', () => {
    renderWithRouter(<WelcomePage />)

    expect(screen.getByRole('link', { name: /sign in/i })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: /register/i })).toBeInTheDocument()
  })

  it('should have correct href for login link', () => {
    renderWithRouter(<WelcomePage />)

    const loginLink = screen.getByRole('link', { name: /sign in/i })
    expect(loginLink).toHaveAttribute('href', '/login')
  })

  it('should have correct href for register link', () => {
    renderWithRouter(<WelcomePage />)

    const registerLink = screen.getByRole('link', { name: /register/i })
    expect(registerLink).toHaveAttribute('href', '/register')
  })
})
