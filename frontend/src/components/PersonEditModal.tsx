import { useEffect, useCallback } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useTranslation } from 'react-i18next'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Checkbox } from '@/components/ui/checkbox'
import { personsApi } from '@/api/persons'
import type { Person, PersonCreateRequest } from '@/types'

const approximateDateSchema = z.object({
  year: z.coerce.number().min(1).max(9999).optional().nullable(),
  month: z.coerce.number().min(1).max(12).optional().nullable(),
  day: z.coerce.number().min(1).max(31).optional().nullable(),
  isApproximate: z.boolean().optional(),
})

const personEditSchema = z.object({
  fullName: z.string().min(1, 'Full name is required'),
  gender: z.enum(['MALE', 'FEMALE', 'OTHER', 'UNKNOWN']).optional(),
  biography: z.string().optional(),
  birthDate: approximateDateSchema,
  deathDate: approximateDateSchema,
  locationBirth: z.string().optional(),
  locationDeath: z.string().optional(),
  locationBurial: z.string().optional(),
})

type PersonEditFormData = z.infer<typeof personEditSchema>

interface PersonEditModalProps {
  person: Person
  onClose: () => void
}

export function PersonEditModal({ person, onClose }: PersonEditModalProps) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()

  const handleKeyDown = useCallback(
    (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        onClose()
      }
    },
    [onClose]
  )

  useEffect(() => {
    document.addEventListener('keydown', handleKeyDown)
    return () => {
      document.removeEventListener('keydown', handleKeyDown)
    }
  }, [handleKeyDown])

  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    watch,
  } = useForm<PersonEditFormData>({
    resolver: zodResolver(personEditSchema),
    defaultValues: {
      fullName: person.fullName,
      gender: person.gender,
      biography: person.biography || '',
      birthDate: {
        year: person.birthDate?.year ?? null,
        month: person.birthDate?.month ?? null,
        day: person.birthDate?.day ?? null,
        isApproximate: person.birthDate?.isApproximate ?? false,
      },
      deathDate: {
        year: person.deathDate?.year ?? null,
        month: person.deathDate?.month ?? null,
        day: person.deathDate?.day ?? null,
        isApproximate: person.deathDate?.isApproximate ?? false,
      },
      locationBirth: person.locationBirth || '',
      locationDeath: person.locationDeath || '',
      locationBurial: person.locationBurial || '',
    },
  })

  const updateMutation = useMutation({
    mutationFn: (data: Partial<PersonCreateRequest>) =>
      personsApi.update(person.id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['person', person.id] })
      // Also invalidate tree query so graph/list views show updated name
      if (person.treeId) {
        queryClient.invalidateQueries({ queryKey: ['tree', person.treeId] })
      }
      onClose()
    },
  })

  const onSubmit = (data: PersonEditFormData) => {
    const updateData: Partial<PersonCreateRequest> = {
      fullName: data.fullName,
      gender: data.gender,
      biography: data.biography || undefined,
      birthDate: data.birthDate.year
        ? {
            year: data.birthDate.year,
            month: data.birthDate.month ?? undefined,
            day: data.birthDate.day ?? undefined,
            isApproximate: data.birthDate.isApproximate,
          }
        : undefined,
      deathDate: data.deathDate.year
        ? {
            year: data.deathDate.year,
            month: data.deathDate.month ?? undefined,
            day: data.deathDate.day ?? undefined,
            isApproximate: data.deathDate.isApproximate,
          }
        : undefined,
      locationBirth: data.locationBirth || undefined,
      locationDeath: data.locationDeath || undefined,
      locationBurial: data.locationBurial || undefined,
    }
    updateMutation.mutate(updateData)
  }

  const birthDateIsApproximate = watch('birthDate.isApproximate')
  const deathDateIsApproximate = watch('deathDate.isApproximate')

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-label={t('person.editPerson', 'Edit Person')}
      className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4"
      onClick={onClose}
    >
      <div
        className="bg-card rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto"
        onClick={(e) => e.stopPropagation()}
      >
        <form onSubmit={handleSubmit(onSubmit)} className="p-6">
          <h2 className="text-2xl font-bold text-foreground mb-6">
            {t('person.editPerson', 'Edit Person')}
          </h2>

          {updateMutation.isError && (
            <div className="mb-4 p-3 rounded-md bg-destructive/10 text-destructive">
              {t('person.updateError', 'Failed to update person. Please try again.')}
            </div>
          )}

          <div className="space-y-6">
            {/* Full Name */}
            <div>
              <Label htmlFor="fullName">{t('person.fullName', 'Full Name')}</Label>
              <Input
                id="fullName"
                {...register('fullName')}
                className="mt-1"
                aria-invalid={errors.fullName ? 'true' : 'false'}
              />
              {errors.fullName && (
                <p className="mt-1 text-sm text-destructive">{errors.fullName.message}</p>
              )}
            </div>

            {/* Gender */}
            <div>
              <Label htmlFor="gender">{t('person.gender', 'Gender')}</Label>
              <select
                id="gender"
                {...register('gender')}
                className="mt-1 flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
              >
                <option value="">{t('person.selectGender', 'Select gender...')}</option>
                <option value="MALE">{t('person.male', 'Male')}</option>
                <option value="FEMALE">{t('person.female', 'Female')}</option>
                <option value="OTHER">{t('person.other', 'Other')}</option>
                <option value="UNKNOWN">{t('person.unknown', 'Unknown')}</option>
              </select>
            </div>

            {/* Biography */}
            <div>
              <Label htmlFor="biography">{t('person.biography', 'Biography')}</Label>
              <textarea
                id="biography"
                {...register('biography')}
                rows={4}
                className="mt-1 flex w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
              />
            </div>

            {/* Birth Date */}
            <fieldset className="border border-border rounded-md p-4">
              <legend className="text-sm font-medium text-muted-foreground px-2">
                {t('person.birthDate', 'Birth Date')}
              </legend>
              <div className="grid grid-cols-3 gap-3 mb-3">
                <div>
                  <Label htmlFor="birthDate.year">{t('person.year', 'Year')}</Label>
                  <Input
                    id="birthDate.year"
                    type="number"
                    {...register('birthDate.year')}
                    className="mt-1"
                    placeholder="YYYY"
                  />
                </div>
                <div>
                  <Label htmlFor="birthDate.month">{t('person.month', 'Month')}</Label>
                  <Input
                    id="birthDate.month"
                    type="number"
                    {...register('birthDate.month')}
                    className="mt-1"
                    placeholder="1-12"
                    min={1}
                    max={12}
                  />
                </div>
                <div>
                  <Label htmlFor="birthDate.day">{t('person.day', 'Day')}</Label>
                  <Input
                    id="birthDate.day"
                    type="number"
                    {...register('birthDate.day')}
                    className="mt-1"
                    placeholder="1-31"
                    min={1}
                    max={31}
                  />
                </div>
              </div>
              <Checkbox
                id="birthDate.isApproximate"
                checked={birthDateIsApproximate}
                onChange={(e) => setValue('birthDate.isApproximate', e.target.checked)}
                label={t('person.approximateDate', 'Approximate date')}
              />
            </fieldset>

            {/* Birth Location */}
            <div>
              <Label htmlFor="locationBirth">{t('person.birthPlace', 'Birth Place')}</Label>
              <Input
                id="locationBirth"
                {...register('locationBirth')}
                className="mt-1"
              />
            </div>

            {/* Death Date */}
            <fieldset className="border border-border rounded-md p-4">
              <legend className="text-sm font-medium text-muted-foreground px-2">
                {t('person.deathDate', 'Death Date')}
              </legend>
              <div className="grid grid-cols-3 gap-3 mb-3">
                <div>
                  <Label htmlFor="deathDate.year">{t('person.year', 'Year')}</Label>
                  <Input
                    id="deathDate.year"
                    type="number"
                    {...register('deathDate.year')}
                    className="mt-1"
                    placeholder="YYYY"
                  />
                </div>
                <div>
                  <Label htmlFor="deathDate.month">{t('person.month', 'Month')}</Label>
                  <Input
                    id="deathDate.month"
                    type="number"
                    {...register('deathDate.month')}
                    className="mt-1"
                    placeholder="1-12"
                    min={1}
                    max={12}
                  />
                </div>
                <div>
                  <Label htmlFor="deathDate.day">{t('person.day', 'Day')}</Label>
                  <Input
                    id="deathDate.day"
                    type="number"
                    {...register('deathDate.day')}
                    className="mt-1"
                    placeholder="1-31"
                    min={1}
                    max={31}
                  />
                </div>
              </div>
              <Checkbox
                id="deathDate.isApproximate"
                checked={deathDateIsApproximate}
                onChange={(e) => setValue('deathDate.isApproximate', e.target.checked)}
                label={t('person.approximateDate', 'Approximate date')}
              />
            </fieldset>

            {/* Death Location */}
            <div>
              <Label htmlFor="locationDeath">{t('person.deathPlace', 'Death Place')}</Label>
              <Input
                id="locationDeath"
                {...register('locationDeath')}
                className="mt-1"
              />
            </div>

            {/* Burial Location */}
            <div>
              <Label htmlFor="locationBurial">{t('person.burialPlace', 'Burial Place')}</Label>
              <Input
                id="locationBurial"
                {...register('locationBurial')}
                className="mt-1"
              />
            </div>
          </div>

          {/* Action Buttons */}
          <div className="mt-8 flex justify-end gap-3">
            <Button type="button" variant="outline" onClick={onClose}>
              {t('common.cancel', 'Cancel')}
            </Button>
            <Button type="submit" disabled={updateMutation.isPending}>
              {updateMutation.isPending
                ? t('common.saving', 'Saving...')
                : t('common.save', 'Save')}
            </Button>
          </div>
        </form>
      </div>
    </div>
  )
}
