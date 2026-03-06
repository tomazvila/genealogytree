import { useRef, useState, useEffect, useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import { useParams, Link } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { personsApi } from '@/api/persons'
import { photosApi } from '@/api/photos'
import { treesApi } from '@/api/trees'
import { useUIStore } from '@/store/uiStore'
import { useAuthStore } from '@/store/authStore'
import { RelationshipFilterControls } from '@/components/RelationshipFilterControls'
import { PersonEditModal } from '@/components/PersonEditModal'
import RelationshipCreateModal from '@/components/RelationshipCreateModal'
import { Button } from '@/components/ui/button'
import type { Person, ApproximateDate, Relative, RelationshipFilterType, Photo, Page, TreeStructure } from '@/types'

// Helper to format approximate date
function formatApproximateDate(date?: ApproximateDate): string {
  if (!date || !date.year) return '-'
  const parts = []
  if (date.day) parts.push(String(date.day).padStart(2, '0'))
  if (date.month) parts.push(String(date.month).padStart(2, '0'))
  parts.push(String(date.year))
  const dateStr = parts.join('/')
  return date.isApproximate ? `~${dateStr}` : dateStr
}

// Get gender display color classes
function getGenderColorClasses(gender?: string): string {
  switch (gender) {
    case 'MALE':
      return 'bg-blue-900/30 text-blue-300'
    case 'FEMALE':
      return 'bg-pink-900/30 text-pink-300'
    default:
      return 'bg-secondary text-muted-foreground'
  }
}

// Info row component
function InfoRow({ label, value }: { label: string; value?: string }) {
  if (!value || value === '-') return null
  return (
    <div className="py-3 border-b border-border last:border-0">
      <dt className="text-sm font-medium text-muted-foreground">{label}</dt>
      <dd className="mt-1 text-sm text-foreground">{value}</dd>
    </div>
  )
}

// Order for displaying relationship types
const RELATIONSHIP_TYPE_ORDER: RelationshipFilterType[] = ['PARENT', 'SPOUSE', 'SIBLING', 'CHILD', 'COUSIN']

// Relatives section component
function RelativesSection({
  relatives,
  getRelationshipTypeLabel,
  getRelationshipTypeSingular,
  t,
}: {
  relatives?: Relative[]
  getRelationshipTypeLabel: (type: string) => string
  getRelationshipTypeSingular: (type: string) => string
  t: (key: string, fallback: string) => string
}) {
  const { relationshipFilters } = useUIStore()

  if (!relatives || relatives.length === 0) return null

  // Filter relatives based on active filters
  const filteredRelatives = relatives.filter(
    (r) => relationshipFilters[r.relationshipType as RelationshipFilterType]
  )

  // Re-group filtered relatives
  const filteredGrouped = filteredRelatives.reduce(
    (acc, relative) => {
      const type = relative.relationshipType
      if (!acc[type]) {
        acc[type] = []
      }
      acc[type].push(relative)
      return acc
    },
    {} as Record<string, Relative[]>
  )

  return (
    <div className="bg-card rounded-lg shadow-sm border border-border p-6">
      <h2 className="text-xl font-semibold text-foreground mb-4">
        {t('person.familyMembers', 'Family Members')}
      </h2>

      <RelationshipFilterControls />

      {/* Grouped sections by relationship type */}
      {RELATIONSHIP_TYPE_ORDER.map((type) => {
        const relativesOfType = filteredGrouped[type]
        if (!relativesOfType || relativesOfType.length === 0) return null

        return (
          <div key={type} className="mb-6 last:mb-0">
            <h3 className="text-lg font-medium text-muted-foreground mb-3">
              {getRelationshipTypeLabel(type)}
            </h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              {relativesOfType.map((relative) => (
                <Link
                  key={relative.id}
                  to={`/person/${relative.id}`}
                  className="flex items-center gap-3 p-3 rounded-lg border border-border hover:bg-secondary/50 transition-colors"
                >
                  {relative.primaryPhotoUrl ? (
                    <img
                      src={relative.primaryPhotoUrl}
                      alt={relative.fullName}
                      className="w-10 h-10 rounded-full object-cover"
                    />
                  ) : (
                    <div
                      className={`w-10 h-10 rounded-full flex items-center justify-center text-white text-sm font-bold ${
                        relative.gender === 'MALE'
                          ? 'bg-blue-500'
                          : relative.gender === 'FEMALE'
                          ? 'bg-pink-500'
                          : 'bg-muted-foreground'
                      }`}
                    >
                      {relative.fullName.charAt(0)}
                    </div>
                  )}
                  <div className="flex-1">
                    <div className="flex items-center gap-2">
                      <span className="font-medium text-foreground">
                        {relative.fullName}
                      </span>
                      <span className="text-xs px-2 py-0.5 rounded-full bg-secondary text-muted-foreground">
                        {getRelationshipTypeSingular(relative.relationshipType)}
                      </span>
                    </div>
                    {relative.birthDate?.year && (
                      <div className="text-xs text-muted-foreground">
                        {relative.birthDate.year}
                        {relative.deathDate?.year && ` - ${relative.deathDate.year}`}
                      </div>
                    )}
                  </div>
                </Link>
              ))}
            </div>
          </div>
        )
      })}
    </div>
  )
}

// Photos section component
function PhotosSection({
  personId,
  treeId,
  canEdit,
  t,
}: {
  personId: string
  treeId?: string
  canEdit: boolean
  t: (key: string, fallback: string) => string
}) {
  const queryClient = useQueryClient()
  const fileInputRef = useRef<HTMLInputElement>(null)
  const [selectedPhoto, setSelectedPhoto] = useState<Photo | null>(null)

  const { data: photosPage, isLoading } = useQuery<Page<Photo>>({
    queryKey: ['person', personId, 'photos'],
    queryFn: () => photosApi.getByPersonId(personId),
    // Poll every 3 seconds if any photo is still processing
    refetchInterval: (query) => {
      const photos = query.state.data?.content || []
      const hasProcessing = photos.some(
        (p) => p.processingStatus === 'PENDING' || p.processingStatus === 'PROCESSING'
      )
      return hasProcessing ? 3000 : false
    },
  })

  const uploadMutation = useMutation({
    mutationFn: async (file: File) => {
      // First upload the photo
      const response = await photosApi.upload(file)
      // Then link it to the person (as primary)
      await photosApi.linkToPersons(response.photoId, [personId], personId)
      return response
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['person', personId, 'photos'] })
    },
  })

  const handleUploadClick = () => {
    fileInputRef.current?.click()
  }

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (file) {
      uploadMutation.mutate(file)
    }
    event.target.value = ''
  }

  const photos = photosPage?.content || []

  return (
    <div className="bg-card rounded-lg shadow-sm border border-border p-6 mb-6">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-xl font-semibold text-foreground">
          {t('person.photos', 'Photos')}
        </h2>
        <div className="flex items-center gap-2">
          {uploadMutation.isError && (
            <span className="text-sm text-destructive">
              {t('photos.uploadFailed', 'Upload failed')}
            </span>
          )}
          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            onChange={handleFileChange}
            className="hidden"
            data-testid="person-photo-upload-input"
          />
          <Button
            variant="outline"
            size="sm"
            onClick={handleUploadClick}
            disabled={uploadMutation.isPending}
          >
            {uploadMutation.isPending
              ? t('photos.uploading', 'Uploading...')
              : t('photos.addPhoto', 'Add Photo')}
          </Button>
        </div>
      </div>

      {isLoading ? (
        <div className="text-muted-foreground text-sm">{t('common.loading', 'Loading...')}</div>
      ) : photos.length === 0 ? (
        <div className="text-center py-8 text-muted-foreground">
          <p>{t('photos.noPhotosForPerson', 'No photos for this person yet')}</p>
        </div>
      ) : (
        <div className="grid grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-3">
          {photos.map((photo) => (
            <PhotoThumbnail
              key={photo.id}
              photo={photo}
              onClick={() => setSelectedPhoto(photo)}
            />
          ))}
        </div>
      )}

      {selectedPhoto && (
        <PhotoLightbox
          photo={selectedPhoto}
          personId={personId}
          treeId={treeId}
          canEdit={canEdit}
          onClose={() => setSelectedPhoto(null)}
          t={t}
        />
      )}
    </div>
  )
}

function PhotoThumbnail({ photo, onClick }: { photo: Photo; onClick: () => void }) {
  const thumbnailUrl = photo.thumbnailMediumUrl || photo.thumbnailSmallUrl || photo.originalUrl
  const isPending = photo.processingStatus === 'PENDING'
  const isProcessing = photo.processingStatus === 'PROCESSING'
  const isFailed = photo.processingStatus === 'FAILED'

  return (
    <button
      type="button"
      onClick={onClick}
      className="relative aspect-square bg-secondary rounded-lg overflow-hidden cursor-pointer hover:ring-2 hover:ring-primary transition-all"
    >
      <img
        src={thumbnailUrl}
        alt="Photo"
        className="w-full h-full object-cover"
      />
      {(isPending || isProcessing) && (
        <div className="absolute inset-0 bg-black/60 flex flex-col items-center justify-center gap-2">
          <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
          <div className="text-white text-xs">
            {isPending ? 'Queued...' : 'Processing...'}
          </div>
        </div>
      )}
      {isFailed && (
        <div className="absolute inset-0 bg-red-900/60 flex flex-col items-center justify-center gap-1">
          <div className="text-white text-lg">!</div>
          <div className="text-white text-xs">Failed</div>
        </div>
      )}
    </button>
  )
}

function PhotoLightbox({
  photo,
  personId,
  treeId,
  canEdit,
  onClose,
  t,
}: {
  photo: Photo
  personId: string
  treeId?: string
  canEdit: boolean
  onClose: () => void
  t: (key: string, fallback: string) => string
}) {
  const queryClient = useQueryClient()

  const setAsPrimaryMutation = useMutation({
    mutationFn: () => photosApi.setAsPrimary(photo.id, personId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['person', personId] })
      queryClient.invalidateQueries({ queryKey: ['person', personId, 'photos'] })
      // Also invalidate tree query so graph/list views show updated photo
      if (treeId) {
        queryClient.invalidateQueries({ queryKey: ['tree', treeId] })
      }
      onClose()
    },
  })

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

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-label={t('photos.lightbox', 'Photo viewer')}
      className="fixed inset-0 z-50 bg-black/90 flex items-center justify-center"
      onClick={onClose}
    >
      <div className="absolute top-4 right-4 flex items-center gap-2">
        {canEdit && (
          <Button
            variant="outline"
            size="sm"
            onClick={(e) => {
              e.stopPropagation()
              setAsPrimaryMutation.mutate()
            }}
            disabled={setAsPrimaryMutation.isPending}
            className="bg-white/10 text-white border-white/30 hover:bg-white/20"
          >
            {setAsPrimaryMutation.isPending
              ? t('photos.setting', 'Setting...')
              : t('photos.setAsProfilePhoto', 'Set as Profile Photo')}
          </Button>
        )}
        <button
          type="button"
          onClick={onClose}
          className="text-white hover:text-white/70 p-2"
          aria-label={t('common.close', 'Close')}
        >
          <svg className="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
      <img
        src={photo.originalUrl}
        alt="Photo"
        data-testid="lightbox-image"
        className="max-w-[90vw] max-h-[90vh] object-contain"
        onClick={(e) => e.stopPropagation()}
      />
    </div>
  )
}

export default function PersonPage() {
  const { t } = useTranslation()
  const { personId } = useParams()
  const [isEditModalOpen, setIsEditModalOpen] = useState(false)
  const [isRelationshipModalOpen, setIsRelationshipModalOpen] = useState(false)

  const user = useAuthStore((state) => state.user)
  const isAdmin = useAuthStore((state) => state.isAdmin)

  const {
    data: person,
    isLoading,
    error,
  } = useQuery<Person>({
    queryKey: ['person', personId],
    queryFn: () => {
      if (!personId) {
        throw new Error('No person ID provided')
      }
      return personsApi.getById(personId)
    },
    enabled: !!personId,
  })

  const { data: relatives } = useQuery<Relative[]>({
    queryKey: ['person', personId, 'relatives'],
    queryFn: () => {
      if (!personId) {
        throw new Error('No person ID provided')
      }
      return personsApi.getRelatives(personId)
    },
    enabled: !!personId && !!person,
  })

  // Fetch tree to check ownership
  const { data: tree } = useQuery<TreeStructure>({
    queryKey: ['tree', person?.treeId],
    queryFn: () => {
      if (!person?.treeId) {
        throw new Error('No tree ID')
      }
      return treesApi.getById(person.treeId)
    },
    enabled: !!person?.treeId,
  })

  // Check if user can edit: admin or tree owner
  const canEdit = isAdmin || (tree && 'createdBy' in tree && tree.createdBy === user?.id)

  // Helper to get display label for relationship type
  function getRelationshipTypeLabel(type: string): string {
    switch (type) {
      case 'PARENT':
        return t('person.parents', 'Parents')
      case 'CHILD':
        return t('person.children', 'Children')
      case 'SPOUSE':
        return t('person.spouses', 'Spouses')
      case 'SIBLING':
        return t('person.siblings', 'Siblings')
      case 'COUSIN':
        return t('person.cousins', 'Cousins')
      default:
        return type
    }
  }

  // Helper to get singular label for relationship type
  function getRelationshipTypeSingular(type: string): string {
    switch (type) {
      case 'PARENT':
        return t('person.parent', 'Parent')
      case 'CHILD':
        return t('person.child', 'Child')
      case 'SPOUSE':
        return t('person.spouse', 'Spouse')
      case 'SIBLING':
        return t('person.sibling', 'Sibling')
      case 'COUSIN':
        return t('person.cousin', 'Cousin')
      default:
        return type
    }
  }

  if (isLoading) {
    return (
      <div className="p-6 h-full flex items-center justify-center">
        <div className="text-muted-foreground">
          {t('common.loading', 'Loading...')}
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="p-6">
        <h1 className="text-3xl font-bold text-foreground mb-6">
          {t('person.title', 'Person Details')}
        </h1>
        <div className="bg-destructive/10 border border-destructive/30 rounded-lg p-6">
          <p className="text-destructive">
            {t('person.error', 'Failed to load person details')}
          </p>
        </div>
      </div>
    )
  }

  if (!person) {
    return (
      <div className="p-6">
        <h1 className="text-3xl font-bold text-foreground mb-6">
          {t('person.title', 'Person Details')}
        </h1>
        <div className="bg-secondary border border-border rounded-lg p-6">
          <p className="text-muted-foreground">
            {t('person.notFound', 'Person not found')}
          </p>
        </div>
      </div>
    )
  }

  const isDeceased = !!person.deathDate?.year

  return (
    <div className="p-6">
      <div className="max-w-4xl mx-auto">
        {/* Header with photo and basic info */}
        <div className="bg-card rounded-lg shadow-sm border border-border p-6 mb-6">
          <div className="flex flex-col md:flex-row gap-6">
            {/* Photo section */}
            <div className="flex-shrink-0">
              {person.primaryPhotoUrl ? (
                <img
                  src={person.primaryPhotoUrl}
                  alt={person.fullName}
                  className="w-32 h-32 rounded-lg object-cover shadow-md"
                />
              ) : (
                <div
                  className={`w-32 h-32 rounded-lg flex items-center justify-center text-4xl font-bold text-white shadow-md ${
                    person.gender === 'MALE'
                      ? 'bg-blue-500'
                      : person.gender === 'FEMALE'
                      ? 'bg-pink-500'
                      : 'bg-muted-foreground'
                  }`}
                >
                  {person.fullName.charAt(0)}
                </div>
              )}
            </div>

            {/* Basic info */}
            <div className="flex-1">
              <div className="flex items-start justify-between">
                <h1 className="text-3xl font-bold text-foreground mb-2">
                  {person.fullName}
                </h1>
                {canEdit && (
                  <div className="flex items-center gap-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setIsRelationshipModalOpen(true)}
                    >
                      {t('person.addFamilyMember', 'Add Family Member')}
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setIsEditModalOpen(true)}
                    >
                      {t('common.edit', 'Edit')}
                    </Button>
                  </div>
                )}
              </div>

              <div className="flex flex-wrap gap-2 mb-4">
                {person.gender && (
                  <span
                    className={`px-3 py-1 rounded-full text-sm font-medium ${getGenderColorClasses(
                      person.gender
                    )}`}
                  >
                    {person.gender === 'MALE'
                      ? t('person.male', 'Male')
                      : person.gender === 'FEMALE'
                      ? t('person.female', 'Female')
                      : t('person.other', 'Other')}
                  </span>
                )}
                {isDeceased && (
                  <span className="px-3 py-1 rounded-full text-sm font-medium bg-secondary text-muted-foreground">
                    {t('person.deceased', 'Deceased')}
                  </span>
                )}
              </div>

              <div className="text-sm text-muted-foreground">
                <div className="flex items-center gap-2 mb-1">
                  <span className="font-medium">{t('person.born', 'Born')}:</span>
                  <span>{formatApproximateDate(person.birthDate)}</span>
                  {person.locationBirth && (
                    <span className="text-muted-foreground/70">• {person.locationBirth}</span>
                  )}
                </div>
                {isDeceased && (
                  <div className="flex items-center gap-2">
                    <span className="font-medium">{t('person.died', 'Died')}:</span>
                    <span>{formatApproximateDate(person.deathDate)}</span>
                    {person.locationDeath && (
                      <span className="text-muted-foreground/70">• {person.locationDeath}</span>
                    )}
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>

        {/* Biography section */}
        {person.biography && (
          <div className="bg-card rounded-lg shadow-sm border border-border p-6 mb-6">
            <h2 className="text-xl font-semibold text-foreground mb-4">
              {t('person.biography', 'Biography')}
            </h2>
            <p className="text-foreground whitespace-pre-wrap">{person.biography}</p>
          </div>
        )}

        {/* Details section */}
        <div className="bg-card rounded-lg shadow-sm border border-border p-6 mb-6">
          <h2 className="text-xl font-semibold text-foreground mb-4">
            {t('person.details', 'Details')}
          </h2>
          <dl>
            <InfoRow
              label={t('person.birthDate', 'Birth Date')}
              value={formatApproximateDate(person.birthDate)}
            />
            <InfoRow
              label={t('person.birthPlace', 'Birth Place')}
              value={person.locationBirth}
            />
            {isDeceased && (
              <>
                <InfoRow
                  label={t('person.deathDate', 'Death Date')}
                  value={formatApproximateDate(person.deathDate)}
                />
                <InfoRow
                  label={t('person.deathPlace', 'Death Place')}
                  value={person.locationDeath}
                />
                <InfoRow
                  label={t('person.burialPlace', 'Burial Place')}
                  value={person.locationBurial}
                />
              </>
            )}
          </dl>
        </div>

        {/* Photos section */}
        {personId && (
          <PhotosSection personId={personId} treeId={person.treeId} canEdit={!!canEdit} t={t} />
        )}

        {/* Relatives section */}
        <RelativesSection
          relatives={relatives}
          getRelationshipTypeLabel={getRelationshipTypeLabel}
          getRelationshipTypeSingular={getRelationshipTypeSingular}
          t={t}
        />
      </div>

      {/* Edit Modal */}
      {isEditModalOpen && person && (
        <PersonEditModal
          person={person}
          onClose={() => setIsEditModalOpen(false)}
        />
      )}

      {/* Relationship Create Modal */}
      {person && person.treeId && (
        <RelationshipCreateModal
          isOpen={isRelationshipModalOpen}
          onClose={() => setIsRelationshipModalOpen(false)}
          onSuccess={() => setIsRelationshipModalOpen(false)}
          currentPerson={person}
          treeId={person.treeId}
        />
      )}
    </div>
  )
}
