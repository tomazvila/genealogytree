import { describe, it, expect, beforeEach } from 'vitest'
import { renderHook, act } from '@testing-library/react'
import { useAuthStore } from '@/store/authStore'

describe('useAuthStore', () => {
  beforeEach(() => {
    // Reset store state before each test
    useAuthStore.setState({
      user: null,
      accessToken: null,
      refreshToken: null,
      isAuthenticated: false,
      isAdmin: false,
    })
  })

  it('should have initial unauthenticated state', () => {
    const { result } = renderHook(() => useAuthStore())

    expect(result.current.isAuthenticated).toBe(false)
    expect(result.current.user).toBeNull()
    expect(result.current.accessToken).toBeNull()
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
      result.current.setAuth(mockUser, 'access_token', 'refresh_token')
    })

    expect(result.current.isAuthenticated).toBe(true)
    expect(result.current.user?.email).toBe('test@example.com')
    expect(result.current.accessToken).toBe('access_token')
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
      result.current.setAuth(adminUser, 'access_token', 'refresh_token')
    })

    expect(result.current.isAdmin).toBe(true)
  })

  it('should clear auth state', () => {
    const { result } = renderHook(() => useAuthStore())

    // First set auth
    const mockUser = {
      id: '123',
      email: 'test@example.com',
      displayName: 'Test User',
      role: 'USER' as const,
      status: 'ACTIVE' as const,
      createdAt: new Date().toISOString(),
    }

    act(() => {
      result.current.setAuth(mockUser, 'access_token', 'refresh_token')
    })

    // Then clear
    act(() => {
      result.current.clearAuth()
    })

    expect(result.current.isAuthenticated).toBe(false)
    expect(result.current.user).toBeNull()
    expect(result.current.accessToken).toBeNull()
  })

  it('should update tokens', () => {
    const { result } = renderHook(() => useAuthStore())

    act(() => {
      result.current.updateTokens('new_access_token', 'new_refresh_token')
    })

    expect(result.current.accessToken).toBe('new_access_token')
    expect(result.current.refreshToken).toBe('new_refresh_token')
  })
})
