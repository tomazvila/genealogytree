import { useTranslation } from 'react-i18next'
import { Checkbox } from '@/components/ui/checkbox'
import { useUIStore } from '@/store/uiStore'
import type { RelationshipFilterType } from '@/types'

const FILTER_ORDER: RelationshipFilterType[] = ['PARENT', 'SPOUSE', 'SIBLING', 'CHILD', 'COUSIN']

export function RelationshipFilterControls() {
  const { t } = useTranslation()
  const { relationshipFilters, setRelationshipFilter } = useUIStore()

  function getFilterLabel(type: RelationshipFilterType): string {
    switch (type) {
      case 'PARENT':
        return t('person.parents', 'Parents')
      case 'SPOUSE':
        return t('person.spouses', 'Spouses')
      case 'SIBLING':
        return t('person.siblings', 'Siblings')
      case 'CHILD':
        return t('person.children', 'Children')
      case 'COUSIN':
        return t('person.cousins', 'Cousins')
      default:
        return type
    }
  }

  return (
    <div className="flex flex-wrap items-center gap-4 mb-4 p-3 bg-secondary rounded-lg">
      <span className="text-sm font-medium text-muted-foreground">
        {t('person.filterRelatives', 'Show:')}
      </span>
      {FILTER_ORDER.map((type) => (
        <Checkbox
          key={type}
          label={getFilterLabel(type)}
          checked={relationshipFilters[type]}
          onChange={(e) => setRelationshipFilter(type, e.target.checked)}
          aria-label={getFilterLabel(type)}
        />
      ))}
    </div>
  )
}
