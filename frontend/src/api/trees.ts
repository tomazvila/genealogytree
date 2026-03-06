import apiClient from './client'
import type { Tree, TreeStructure, Page } from '@/types'

export const treesApi = {
  getAll: async (page = 0, size = 20): Promise<Page<Tree>> => {
    return apiClient.get<Page<Tree>>('/trees', {
      params: { page, size },
    })
  },

  getById: async (id: string): Promise<TreeStructure> => {
    return apiClient.get<TreeStructure>(`/trees/${id}`)
  },

  create: async (data: { name: string; description?: string }): Promise<Tree> => {
    return apiClient.post<Tree>('/trees', data)
  },

  merge: async (sourceTreeId: string, targetTreeId: string): Promise<void> => {
    await apiClient.post('/trees/merge', null, {
      params: { sourceTreeId, targetTreeId },
    })
  },
}
