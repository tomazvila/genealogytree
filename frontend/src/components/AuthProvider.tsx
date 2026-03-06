import { useEffect } from 'react'
import { useAuthStore } from '@/store/authStore'
import { authApi } from '@/api/auth'

export default function AuthProvider({ children }: { children: React.ReactNode }) {
  const { user, setAuth, clearAuth, setLoading, isLoading } = useAuthStore()

  useEffect(() => {
    if (!user) {
      setLoading(false)
      return
    }

    authApi.me()
      .then((info) => {
        setAuth({
          id: info.userId,
          username: info.username,
          role: info.role as 'ADMIN' | 'USER',
          status: 'ACTIVE',
          createdAt: new Date().toISOString(),
        })
      })
      .catch(() => {
        clearAuth()
      })
      .finally(() => {
        setLoading(false)
      })
  }, []) // eslint-disable-line react-hooks/exhaustive-deps

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary" />
      </div>
    )
  }

  return <>{children}</>
}
