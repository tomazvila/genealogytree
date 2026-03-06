import apiClient from './client'
import type { Relationship, RelationshipCreateRequest } from '@/types'

export const relationshipsApi = {
  create: async (data: RelationshipCreateRequest): Promise<Relationship> => {
    return apiClient.post<Relationship>('/relationships', data)
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/relationships/${id}`)
  },

  getByPersonId: async (personId: string): Promise<Relationship[]> => {
    return apiClient.get<Relationship[]>(`/relationships/person/${personId}`)
  },

  checkRelated: async (personId1: string, personId2: string, maxHops = 3): Promise<boolean> => {
    return apiClient.get<boolean>('/relationships/check', {
      params: { personId1, personId2, maxHops },
    })
  },
}
