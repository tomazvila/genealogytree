import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Link, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { authApi } from '@/api/auth'
import type { ApiError } from '@/types'

const registerSchema = z.object({
  email: z.string().min(1, 'Email is required').email('Invalid email address'),
  password: z.string().min(8, 'Password must be at least 8 characters'),
  displayName: z.string().min(1, 'Display name is required'),
})

type RegisterFormData = z.infer<typeof registerSchema>

export default function RegisterPage() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const [error, setError] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(false)

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  })

  const onSubmit = async (data: RegisterFormData) => {
    setIsLoading(true)
    setError(null)

    try {
      await authApi.register(data)
      navigate('/login', { state: { message: 'Registration successful! Please sign in.' } })
    } catch (err: unknown) {
      const apiError = err as { response?: { data?: ApiError } }
      if (apiError.response?.data?.message?.includes('already exists')) {
        setError('Email already exists. Please use a different email.')
      } else {
        setError(apiError.response?.data?.message || 'Registration failed. Please try again.')
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
            {t('register.title', 'Create Account')}
          </h2>
          <p className="mt-2 text-center text-sm text-muted-foreground">
            {t('register.subtitle', 'Join Geneinator to start building your family tree')}
          </p>
        </div>

        <form className="mt-8 space-y-6" onSubmit={handleSubmit(onSubmit)}>
          {error && (
            <div className="rounded-md bg-destructive/10 p-4">
              <p className="text-sm text-destructive">{error}</p>
            </div>
          )}

          <div className="space-y-4">
            <div>
              <Label htmlFor="email">{t('register.email', 'Email')}</Label>
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
              <Label htmlFor="password">{t('register.password', 'Password')}</Label>
              <Input
                id="password"
                type="password"
                autoComplete="new-password"
                {...register('password')}
                className="mt-1"
                aria-invalid={errors.password ? 'true' : 'false'}
              />
              {errors.password && (
                <p className="mt-1 text-sm text-destructive">{errors.password.message}</p>
              )}
            </div>

            <div>
              <Label htmlFor="displayName">{t('register.displayName', 'Display Name')}</Label>
              <Input
                id="displayName"
                type="text"
                autoComplete="name"
                {...register('displayName')}
                className="mt-1"
                aria-invalid={errors.displayName ? 'true' : 'false'}
              />
              {errors.displayName && (
                <p className="mt-1 text-sm text-destructive">{errors.displayName.message}</p>
              )}
            </div>
          </div>

          <Button type="submit" className="w-full" disabled={isLoading}>
            {isLoading ? t('common.loading', 'Loading...') : t('register.submit', 'Register')}
          </Button>

          <p className="text-center text-sm text-muted-foreground">
            {t('register.hasAccount', 'Already have an account?')}{' '}
            <Link to="/login" className="font-medium text-primary hover:text-primary/80">
              {t('register.signIn', 'Sign in')}
            </Link>
          </p>
        </form>
      </div>
    </div>
  )
}
