import apiClient from './client'
import type { LoginRequest, RegisterRequest, AuthResponse } from '@/types'

export const authApi = {
  login: async (data: LoginRequest): Promise<AuthResponse> => {
    return apiClient.post<AuthResponse>('/auth/login', data)
  },

  register: async (data: RegisterRequest): Promise<{ userId: string; message: string }> => {
    return apiClient.post('/auth/register', data)
  },

  refresh: async (refreshToken: string): Promise<AuthResponse> => {
    return apiClient.post<AuthResponse>('/auth/refresh', { refreshToken })
  },

  logout: async (): Promise<void> => {
    await apiClient.post('/auth/logout')
  },
}
