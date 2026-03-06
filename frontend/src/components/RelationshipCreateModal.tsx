import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
import { Input } from '@/components/ui/input'
import { Checkbox } from '@/components/ui/checkbox'
import { relationshipsApi } from '@/api/relationships'
import { treesApi } from '@/api/trees'
import type { Person, RelationshipCreateRequest } from '@/types'

interface RelationshipCreateModalProps {
  isOpen: boolean
  onClose: () => void
  onSuccess: () => void
  currentPerson: Person
  treeId: string
}

type RelationshipType = 'PARENT' | 'CHILD' | 'SPOUSE' | 'SIBLING'

export default function RelationshipCreateModal({
  isOpen,
  onClose,
  onSuccess,
  currentPerson,
  treeId,
}: RelationshipCreateModalProps) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()

  const [relationshipType, setRelationshipType] = useState<RelationshipType | ''>('')
  const [selectedPersonId, setSelectedPersonId] = useState('')
  const [marriageYear, setMarriageYear] = useState('')
  const [isDivorced, setIsDivorced] = useState(false)
  const [validationError, setValidationError] = useState('')

  // Fetch tree to get list of persons
  const { data: tree } = useQuery({
    queryKey: ['tree', treeId],
    queryFn: () => treesApi.getById(treeId),
    enabled: isOpen && !!treeId,
  })

  // Filter out current person from the list
  const availablePersons = tree?.persons?.filter((p) => p.id !== currentPerson.id) || []

  const createMutation = useMutation({
    mutationFn: (data: RelationshipCreateRequest) => relationshipsApi.create(data),
    onSuccess: () => {
      // Invalidate queries to refresh data
      queryClient.invalidateQueries({ queryKey: ['tree', treeId] })
      queryClient.invalidateQueries({ queryKey: ['person', currentPerson.id] })
      queryClient.invalidateQueries({ queryKey: ['relatives', currentPerson.id] })
      onSuccess()
      resetForm()
    },
  })

  const resetForm = () => {
    setRelationshipType('')
    setSelectedPersonId('')
    setMarriageYear('')
    setIsDivorced(false)
    setValidationError('')
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    setValidationError('')

    // Validation
    if (!relationshipType) {
      setValidationError(t('relationship.selectType', 'Please select a relationship type'))
      return
    }
    if (!selectedPersonId) {
      setValidationError(t('relationship.selectPerson', 'Please select a person'))
      return
    }

    const request: RelationshipCreateRequest = {
      personFromId: currentPerson.id,
      personToId: selectedPersonId,
      relationshipType: relationshipType as RelationshipType,
    }

    // Add spouse-specific fields
    if (relationshipType === 'SPOUSE') {
      if (marriageYear) {
        request.startDate = { year: parseInt(marriageYear, 10) }
      }
      if (isDivorced) {
        request.isDivorced = true
      }
    }

    createMutation.mutate(request)
  }

  const handleCancel = () => {
    resetForm()
    onClose()
  }

  if (!isOpen) return null

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
      role="dialog"
      aria-modal="true"
      aria-labelledby="modal-title"
    >
      <div className="bg-card rounded-lg shadow-xl w-full max-w-md mx-4 p-6">
        <h2 id="modal-title" className="text-xl font-semibold mb-4">
          {t('relationship.addFamilyMember', 'Add Family Member')}
        </h2>

        <p className="text-sm text-muted-foreground mb-4">
          {t('relationship.addingTo', 'Adding relationship to')} <strong>{currentPerson.fullName}</strong>
        </p>

        {createMutation.isError && (
          <div className="mb-4 p-3 rounded-md bg-destructive/10 text-destructive text-sm">
            {t('relationship.createError', 'Failed to create relationship. Please try again.')}
          </div>
        )}

        {validationError && (
          <div className="mb-4 p-3 rounded-md bg-destructive/10 text-destructive text-sm">
            {validationError}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Relationship Type */}
          <div>
            <Label htmlFor="relationshipType">
              {t('relationship.type', 'Relationship Type')} *
            </Label>
            <select
              id="relationshipType"
              value={relationshipType}
              onChange={(e) => setRelationshipType(e.target.value as RelationshipType | '')}
              className="mt-1 flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
            >
              <option value="">{t('relationship.selectType', 'Select relationship type...')}</option>
              <option value="PARENT">{t('relationship.parent', 'Parent')}</option>
              <option value="CHILD">{t('relationship.child', 'Child')}</option>
              <option value="SPOUSE">{t('relationship.spouse', 'Spouse')}</option>
              <option value="SIBLING">{t('relationship.sibling', 'Sibling')}</option>
            </select>
          </div>

          {/* Person Selector */}
          <div>
            <Label htmlFor="personSelect">
              {t('relationship.selectPerson', 'Select Person')} *
            </Label>
            <select
              id="personSelect"
              value={selectedPersonId}
              onChange={(e) => setSelectedPersonId(e.target.value)}
              className="mt-1 flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
            >
              <option value="">{t('relationship.choosePerson', 'Choose a person...')}</option>
              {availablePersons.map((person) => (
                <option key={person.id} value={person.id}>
                  {person.fullName}
                  {person.birthDate?.year && ` (b. ${person.birthDate.year})`}
                </option>
              ))}
            </select>
          </div>

          {/* Spouse-specific fields */}
          {relationshipType === 'SPOUSE' && (
            <>
              <div>
                <Label htmlFor="marriageYear">
                  {t('relationship.marriageDate', 'Marriage Date')} ({t('common.year', 'Year')})
                </Label>
                <Input
                  id="marriageYear"
                  type="number"
                  value={marriageYear}
                  onChange={(e) => setMarriageYear(e.target.value)}
                  className="mt-1"
                  placeholder="YYYY"
                  min={1}
                  max={9999}
                />
              </div>

              <div className="flex items-center gap-2">
                <Checkbox
                  id="isDivorced"
                  checked={isDivorced}
                  onChange={(e) => setIsDivorced(e.target.checked)}
                  label={t('relationship.divorced', 'Divorced')}
                />
              </div>
            </>
          )}

          {/* Actions */}
          <div className="flex justify-end gap-3 pt-4">
            <Button type="button" variant="outline" onClick={handleCancel}>
              {t('common.cancel', 'Cancel')}
            </Button>
            <Button type="submit" disabled={createMutation.isPending}>
              {createMutation.isPending
                ? t('common.adding', 'Adding...')
                : t('relationship.addRelationship', 'Add Relationship')}
            </Button>
          </div>
        </form>
      </div>
    </div>
  )
}
