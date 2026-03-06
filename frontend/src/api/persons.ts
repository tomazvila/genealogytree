import apiClient from './client'
import type { Person, PersonCreateRequest, Page, Relative } from '@/types'

export const personsApi = {
  getAll: async (page = 0, size = 20): Promise<Page<Person>> => {
    return apiClient.get<Page<Person>>('/persons', {
      params: { page, size },
    })
  },

  getById: async (id: string): Promise<Person> => {
    return apiClient.get<Person>(`/persons/${id}`)
  },

  create: async (data: PersonCreateRequest): Promise<Person> => {
    return apiClient.post<Person>('/persons', data)
  },

  update: async (id: string, data: Partial<PersonCreateRequest>): Promise<Person> => {
    return apiClient.put<Person>(`/persons/${id}`, data)
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/persons/${id}`)
  },

  getRelatives: async (id: string): Promise<Relative[]> => {
    return apiClient.get<Relative[]>(`/persons/${id}/relatives`)
  },

  search: async (query: string, page = 0, size = 20): Promise<Page<Person>> => {
    return apiClient.get<Page<Person>>('/search', {
      params: { name: query, page, size },
    })
  },
}
