import { describe, it, expect, beforeEach } from 'vitest'
import { renderHook, act } from '@testing-library/react'
import { useAuthStore } from '@/store/authStore'

describe('useAuthStore', () => {
  beforeEach(() => {
    useAuthStore.setState({
      user: null,
      isAuthenticated: false,
      isAdmin: false,
      isLoading: true,
    })
  })

  it('should have initial unauthenticated state', () => {
    const { result } = renderHook(() => useAuthStore())

    expect(result.current.isAuthenticated).toBe(false)
    expect(result.current.user).toBeNull()
  })

  it('should set auth state correctly', () => {
    const { result } = renderHook(() => useAuthStore())

    const mockUser = {
      id: '123',
      email: 'test@example.com',
      displayName: 'Test User',
      role: 'USER' as const,
      status: 'ACTIVE' as const,
      createdAt: new Date().toISOString(),
    }

    act(() => {
      result.current.setAuth(mockUser)
    })

    expect(result.current.isAuthenticated).toBe(true)
    expect(result.current.user?.email).toBe('test@example.com')
    expect(result.current.isAdmin).toBe(false)
  })

  it('should set isAdmin to true for admin users', () => {
    const { result } = renderHook(() => useAuthStore())

    const adminUser = {
      id: '123',
      email: 'admin@example.com',
      displayName: 'Admin',
      role: 'ADMIN' as const,
      status: 'ACTIVE' as const,
      createdAt: new Date().toISOString(),
    }

    act(() => {
      result.current.setAuth(adminUser)
    })

    expect(result.current.isAdmin).toBe(true)
  })

  it('should clear auth state', () => {
    const { result } = renderHook(() => useAuthStore())

    const mockUser = {
      id: '123',
      email: 'test@example.com',
      displayName: 'Test User',
      role: 'USER' as const,
      status: 'ACTIVE' as const,
      createdAt: new Date().toISOString(),
    }

    act(() => {
      result.current.setAuth(mockUser)
    })

    act(() => {
      result.current.clearAuth()
    })

    expect(result.current.isAuthenticated).toBe(false)
    expect(result.current.user).toBeNull()
  })

  it('should manage loading state', () => {
    const { result } = renderHook(() => useAuthStore())

    expect(result.current.isLoading).toBe(true)

    act(() => {
      result.current.setLoading(false)
    })

    expect(result.current.isLoading).toBe(false)
  })
})
