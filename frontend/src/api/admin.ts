import apiClient from './client'
import type { Page } from '@/types'

export interface AdminUser {
  id: string
  username: string
  role: string
  status: 'PENDING_APPROVAL' | 'ACTIVE' | 'SUSPENDED'
  createdAt: string
  lastLogin?: string
}

export interface AuditLog {
  id: string
  userId: string
  userUsername: string
  action: string
  entityType: string
  entityId: string
  oldValue?: Record<string, unknown>
  newValue?: Record<string, unknown>
  timestamp: string
  ipAddress?: string
}

export interface AdminStats {
  pendingApprovals: number
}

export const adminApi = {
  getUsers: async (page = 0, size = 20): Promise<Page<AdminUser>> => {
    return apiClient.get<Page<AdminUser>>('/admin/users', {
      params: { page, size },
    })
  },

  approveUser: async (id: string): Promise<void> => {
    await apiClient.patch(`/admin/users/${id}/approve`)
  },

  suspendUser: async (id: string): Promise<void> => {
    await apiClient.patch(`/admin/users/${id}/suspend`)
  },

  deleteUser: async (id: string): Promise<void> => {
    await apiClient.delete(`/admin/users/${id}`)
  },

  getAuditLogs: async (page = 0, size = 20): Promise<Page<AuditLog>> => {
    return apiClient.get<Page<AuditLog>>('/admin/audit-logs', {
      params: { page, size },
    })
  },

  getStats: async (): Promise<AdminStats> => {
    return apiClient.get<AdminStats>('/admin/stats')
  },
}
