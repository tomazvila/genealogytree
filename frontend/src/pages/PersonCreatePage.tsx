import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useTranslation } from 'react-i18next'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useNavigate, useParams } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Checkbox } from '@/components/ui/checkbox'
import { personsApi } from '@/api/persons'
import { treesApi } from '@/api/trees'
import type { PersonCreateRequest } from '@/types'

const optionalNumber = (min: number, max: number) =>
  z.preprocess(
    (v) => (v === '' || v === null || v === undefined ? undefined : Number(v)),
    z.number().min(min).max(max).optional()
  )

const approximateDateSchema = z.object({
  year: optionalNumber(1, 9999),
  month: optionalNumber(1, 12),
  day: optionalNumber(1, 31),
  isApproximate: z.boolean().optional(),
})

const personCreateSchema = z.object({
  fullName: z.string().min(1, 'Full name is required'),
  gender: z.preprocess(
    (v) => (v === '' ? undefined : v),
    z.enum(['MALE', 'FEMALE', 'OTHER', 'UNKNOWN']).optional()
  ),
  biography: z.string().optional(),
  birthDate: approximateDateSchema.refine(
    (data) => data.year !== null && data.year !== undefined,
    { message: 'Birth year is required' }
  ),
  deathDate: approximateDateSchema,
  locationBirth: z.string().optional(),
  locationDeath: z.string().optional(),
  locationBurial: z.string().optional(),
})

type PersonCreateFormData = z.infer<typeof personCreateSchema>

export default function PersonCreatePage() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const { treeId } = useParams<{ treeId: string }>()
  const queryClient = useQueryClient()

  // Fetch tree info for context
  const { data: tree } = useQuery({
    queryKey: ['tree', treeId],
    queryFn: () => {
      if (!treeId) throw new Error('No tree ID')
      return treesApi.getById(treeId)
    },
    enabled: !!treeId,
  })

  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    watch,
  } = useForm<PersonCreateFormData>({
    resolver: zodResolver(personCreateSchema),
    defaultValues: {
      fullName: '',
      gender: undefined,
      biography: '',
      birthDate: {
        year: undefined,
        month: undefined,
        day: undefined,
        isApproximate: false,
      },
      deathDate: {
        year: undefined,
        month: undefined,
        day: undefined,
        isApproximate: false,
      },
      locationBirth: '',
      locationDeath: '',
      locationBurial: '',
    },
  })

  const createMutation = useMutation({
    mutationFn: (data: PersonCreateRequest) => personsApi.create(data),
    onSuccess: (createdPerson) => {
      // Invalidate tree query to refresh person list
      if (treeId) {
        queryClient.invalidateQueries({ queryKey: ['tree', treeId] })
      }
      // Navigate to the created person's page
      navigate(`/person/${createdPerson.id}`)
    },
  })

  const onSubmit = (data: PersonCreateFormData) => {
    const createData: PersonCreateRequest = {
      fullName: data.fullName,
      gender: data.gender,
      biography: data.biography || undefined,
      birthDate: {
        year: data.birthDate.year!,
        month: data.birthDate.month ?? undefined,
        day: data.birthDate.day ?? undefined,
        isApproximate: data.birthDate.isApproximate,
      },
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
      treeId: treeId,
    }
    createMutation.mutate(createData)
  }

  const handleCancel = () => {
    navigate(`/tree/${treeId}`)
  }

  const birthDateIsApproximate = watch('birthDate.isApproximate')
  const deathDateIsApproximate = watch('deathDate.isApproximate')

  return (
    <div className="p-6 max-w-2xl mx-auto">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-foreground">
          {t('person.addNewPerson', 'Add New Person')}
        </h1>
        {tree && (
          <p className="text-muted-foreground mt-1">
            {t('person.addingTo', 'Adding to')} {tree.treeName}
          </p>
        )}
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="bg-card rounded-lg shadow-lg p-6">
        {createMutation.isError && (
          <div className="mb-4 p-3 rounded-md bg-destructive/10 text-destructive">
            {t('person.createError', 'Failed to create person. Please try again.')}
          </div>
        )}

        <div className="space-y-6">
          {/* Full Name */}
          <div>
            <Label htmlFor="fullName">{t('person.fullName', 'Full Name')} *</Label>
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
              {t('person.birthDate', 'Birth Date')} *
            </legend>
            <div className="grid grid-cols-3 gap-3 mb-3">
              <div>
                <Label htmlFor="birthDate.year">{t('person.year', 'Year')} *</Label>
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
            {errors.birthDate?.root && (
              <p className="mb-2 text-sm text-destructive">{errors.birthDate.root.message}</p>
            )}
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
          <Button type="button" variant="outline" onClick={handleCancel}>
            {t('common.cancel', 'Cancel')}
          </Button>
          <Button type="submit" disabled={createMutation.isPending}>
            {createMutation.isPending
              ? t('common.creating', 'Creating...')
              : t('person.createPerson', 'Create Person')}
          </Button>
        </div>
      </form>
    </div>
  )
}
