import { useAuthStore } from '@/store/authStore'

const BASE_URL = '/api'

class ApiError extends Error {
  constructor(
    public status: number,
    public statusText: string,
    public data?: unknown
  ) {
    super(`${status} ${statusText}`)
    this.name = 'ApiError'
  }
}

interface RequestOptions {
  params?: Record<string, string | number | undefined>
  headers?: Record<string, string>
}

type RequestBody = Record<string, unknown> | object | unknown[] | FormData | null

let isRefreshing = false
let refreshPromise: Promise<boolean> | null = null

function getCsrfToken(): string | null {
  const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/)
  return match ? decodeURIComponent(match[1]) : null
}

async function refreshAccessToken(): Promise<boolean> {
  try {
    const response = await fetch(`${BASE_URL}/auth/refresh`, {
      method: 'POST',
      credentials: 'include',
    })

    if (!response.ok) {
      throw new Error('Refresh failed')
    }

    return true
  } catch {
    useAuthStore.getState().clearAuth()
    window.location.href = '/login'
    return false
  }
}

async function request<T>(
  method: string,
  path: string,
  body?: RequestBody,
  options?: RequestOptions
): Promise<T> {
  const url = new URL(`${BASE_URL}${path}`, window.location.origin)

  if (options?.params) {
    Object.entries(options.params).forEach(([key, value]) => {
      if (value !== undefined) {
        url.searchParams.set(key, String(value))
      }
    })
  }

  const headers: Record<string, string> = {
    ...options?.headers,
  }

  // Add CSRF token for mutating requests
  if (['POST', 'PUT', 'PATCH', 'DELETE'].includes(method)) {
    const csrfToken = getCsrfToken()
    if (csrfToken) {
      headers['X-XSRF-TOKEN'] = csrfToken
    }
  }

  const isFormData = body instanceof FormData
  if (!isFormData && body !== undefined) {
    headers['Content-Type'] = 'application/json'
  }

  const response = await fetch(url.toString(), {
    method,
    headers,
    credentials: 'include',
    body: body === undefined
      ? undefined
      : isFormData
        ? body
        : JSON.stringify(body),
  })

  if (response.status === 401) {
    if (!isRefreshing) {
      isRefreshing = true
      refreshPromise = refreshAccessToken()
    }

    const success = await refreshPromise
    isRefreshing = false
    refreshPromise = null

    if (success) {
      // Retry with new cookies (automatically included)
      const retryHeaders: Record<string, string> = { ...options?.headers }
      if (['POST', 'PUT', 'PATCH', 'DELETE'].includes(method)) {
        const csrfToken = getCsrfToken()
        if (csrfToken) {
          retryHeaders['X-XSRF-TOKEN'] = csrfToken
        }
      }
      if (!isFormData && body !== undefined) {
        retryHeaders['Content-Type'] = 'application/json'
      }

      const retryResponse = await fetch(url.toString(), {
        method,
        headers: retryHeaders,
        credentials: 'include',
        body: body === undefined
          ? undefined
          : isFormData
            ? body
            : JSON.stringify(body),
      })

      if (!retryResponse.ok) {
        const errorData = await retryResponse.text().catch(() => null)
        throw new ApiError(retryResponse.status, retryResponse.statusText, errorData)
      }

      if (retryResponse.status === 204) {
        return undefined as T
      }

      return retryResponse.json()
    }
  }

  if (!response.ok) {
    const errorData = await response.text().catch(() => null)
    throw new ApiError(response.status, response.statusText, errorData)
  }

  if (response.status === 204) {
    return undefined as T
  }

  return response.json()
}

const apiClient = {
  get: <T>(path: string, options?: RequestOptions): Promise<T> =>
    request<T>('GET', path, undefined, options),

  post: <T>(path: string, body?: RequestBody, options?: RequestOptions): Promise<T> =>
    request<T>('POST', path, body, options),

  put: <T>(path: string, body?: RequestBody, options?: RequestOptions): Promise<T> =>
    request<T>('PUT', path, body, options),

  patch: <T>(path: string, body?: RequestBody, options?: RequestOptions): Promise<T> =>
    request<T>('PATCH', path, body, options),

  delete: <T>(path: string, options?: RequestOptions): Promise<T> =>
    request<T>('DELETE', path, undefined, options),
}

export default apiClient
export { ApiError }
