import apiClient from './client'
import type { Photo, Page } from '@/types'

export const photosApi = {
  getMyPhotos: async (page = 0, size = 20): Promise<Page<Photo>> => {
    return apiClient.get<Page<Photo>>('/photos', {
      params: { page, size },
    })
  },

  upload: async (file: File): Promise<{ photoId: string; originalUrl: string }> => {
    const formData = new FormData()
    formData.append('file', file)

    return apiClient.post('/photos/upload', formData)
  },

  getById: async (id: string): Promise<Photo> => {
    return apiClient.get<Photo>(`/photos/${id}`)
  },

  getByPersonId: async (personId: string, page = 0, size = 20): Promise<Page<Photo>> => {
    return apiClient.get<Page<Photo>>(`/persons/${personId}/photos`, {
      params: { page, size },
    })
  },

  linkToPersons: async (photoId: string, personIds: string[], primaryPersonId?: string): Promise<void> => {
    await apiClient.post(`/photos/${photoId}/persons`, personIds, {
      params: { primaryPersonId },
    })
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/photos/${id}`)
  },

  setAsPrimary: async (photoId: string, personId: string): Promise<void> => {
    await apiClient.post(`/photos/${photoId}/set-primary`, null, {
      params: { personId },
    })
  },
}
