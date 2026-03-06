import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/store/authStore'
import type { ApiError } from '@/types'

const loginSchema = z.object({
  email: z.string().min(1, 'Email is required').email('Invalid email address'),
  password: z.string().min(1, 'Password is required'),
})

type LoginFormData = z.infer<typeof loginSchema>

export default function LoginPage() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const location = useLocation()
  const setAuth = useAuthStore((state) => state.setAuth)
  const [error, setError] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(false)

  const successMessage = (location.state as { message?: string })?.message

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  })

  const onSubmit = async (data: LoginFormData) => {
    setIsLoading(true)
    setError(null)

    try {
      const response = await authApi.login(data)

      setAuth(
        {
          id: response.userId,
          email: response.email,
          displayName: response.displayName,
          role: response.role as 'ADMIN' | 'USER',
          status: 'ACTIVE',
          createdAt: new Date().toISOString(),
        },
        response.accessToken,
        response.refreshToken
      )

      navigate('/dashboard')
    } catch (err: unknown) {
      const apiError = err as { response?: { data?: ApiError; status?: number } }
      if (apiError.response?.status === 401) {
        setError('Invalid credentials. Please check your email and password.')
      } else if (apiError.response?.status === 403) {
        setError('Your account is pending approval or has been suspended.')
      } else {
        setError(apiError.response?.data?.message || 'Login failed. Please try again.')
      }
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-background py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-foreground">
            {t('login.title', 'Sign In')}
          </h2>
          <p className="mt-2 text-center text-sm text-muted-foreground">
            {t('login.subtitle', 'Welcome back to Geneinator')}
          </p>
        </div>

        <form className="mt-8 space-y-6" onSubmit={handleSubmit(onSubmit)}>
          {successMessage && (
            <div className="rounded-md bg-green-900/20 p-4">
              <p className="text-sm text-green-400">{successMessage}</p>
            </div>
          )}

          {error && (
            <div className="rounded-md bg-destructive/10 p-4">
              <p className="text-sm text-destructive">{error}</p>
            </div>
          )}

          <div className="space-y-4">
            <div>
              <Label htmlFor="email">{t('login.email', 'Email')}</Label>
              <Input
                id="email"
                type="email"
                autoComplete="email"
                {...register('email')}
                className="mt-1"
                aria-invalid={errors.email ? 'true' : 'false'}
              />
              {errors.email && (
                <p className="mt-1 text-sm text-destructive">{errors.email.message}</p>
              )}
            </div>

            <div>
              <Label htmlFor="password">{t('login.password', 'Password')}</Label>
              <Input
                id="password"
                type="password"
                autoComplete="current-password"
                {...register('password')}
                className="mt-1"
                aria-invalid={errors.password ? 'true' : 'false'}
              />
              {errors.password && (
                <p className="mt-1 text-sm text-destructive">{errors.password.message}</p>
              )}
            </div>
          </div>

          <Button type="submit" className="w-full" disabled={isLoading}>
            {isLoading ? t('common.loading', 'Loading...') : t('login.submit', 'Sign in')}
          </Button>

          <p className="text-center text-sm text-muted-foreground">
            {t('login.noAccount', "Don't have an account?")}{' '}
            <Link to="/register" className="font-medium text-primary hover:text-primary/80">
              {t('login.createAccount', 'Create account')}
            </Link>
          </p>
        </form>
      </div>
    </div>
  )
}
