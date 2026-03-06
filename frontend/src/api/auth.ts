import apiClient from './client'
import type { LoginRequest, RegisterRequest, UserInfoResponse } from '@/types'

export const authApi = {
  login: async (data: LoginRequest): Promise<UserInfoResponse> => {
    return apiClient.post<UserInfoResponse>('/auth/login', data)
  },

  register: async (data: RegisterRequest): Promise<{ userId: string; message: string }> => {
    return apiClient.post('/auth/register', data)
  },

  refresh: async (): Promise<UserInfoResponse> => {
    return apiClient.post<UserInfoResponse>('/auth/refresh')
  },

  logout: async (): Promise<void> => {
    await apiClient.post('/auth/logout')
  },

  me: async (): Promise<UserInfoResponse> => {
    return apiClient.get<UserInfoResponse>('/auth/me')
  },
}
