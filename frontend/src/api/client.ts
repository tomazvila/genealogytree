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
let refreshPromise: Promise<string | null> | null = null

async function refreshAccessToken(): Promise<string | null> {
  const refreshToken = useAuthStore.getState().refreshToken
  if (!refreshToken) return null

  try {
    const response = await fetch(`${BASE_URL}/auth/refresh`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken }),
    })

    if (!response.ok) {
      throw new Error('Refresh failed')
    }

    const { accessToken, refreshToken: newRefreshToken } = await response.json()
    useAuthStore.getState().updateTokens(accessToken, newRefreshToken)
    return accessToken
  } catch {
    useAuthStore.getState().clearAuth()
    window.location.href = '/login'
    return null
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

  const accessToken = useAuthStore.getState().accessToken
  if (accessToken) {
    headers['Authorization'] = `Bearer ${accessToken}`
  }

  const isFormData = body instanceof FormData
  if (!isFormData && body !== undefined) {
    headers['Content-Type'] = 'application/json'
  }

  const response = await fetch(url.toString(), {
    method,
    headers,
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

    const newAccessToken = await refreshPromise
    isRefreshing = false
    refreshPromise = null

    if (newAccessToken) {
      headers['Authorization'] = `Bearer ${newAccessToken}`
      const retryResponse = await fetch(url.toString(), {
        method,
        headers,
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
