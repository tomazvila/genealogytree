import { useCallback, useEffect, useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useParams, Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import {
  ReactFlow,
  Background,
  Controls,
  MiniMap,
  useNodesState,
  useEdgesState,
  Position,
  NodeProps,
  Handle,
  Node,
} from '@xyflow/react'
import '@xyflow/react/dist/style.css'
import { treesApi } from '@/api/trees'
import { convertToFlowData } from '@/utils/treeLayout'
import type { TreeStructure, Person } from '@/types'

// Custom node component for person
function PersonNodeComponent({ data }: NodeProps) {
  // Data is typed as Record<string, unknown> by React Flow
  const person = data as unknown as Person
  const isDeceased = !!person.deathDate

  return (
    <>
      <Handle type="target" position={Position.Top} className="!bg-muted-foreground !w-2 !h-2" />
      <Link to={`/person/${person.id}`}>
        <div
          className={`px-4 py-3 rounded-lg shadow-lg border-2 min-w-[150px] text-center cursor-pointer transition-all hover:shadow-xl ${
            isDeceased
              ? 'bg-muted border-muted-foreground'
              : person.gender === 'MALE'
              ? 'bg-blue-900/30 border-blue-400'
              : person.gender === 'FEMALE'
              ? 'bg-pink-900/30 border-pink-400'
              : 'bg-secondary border-muted-foreground'
          }`}
        >
          {person.primaryPhotoUrl && (
            <img
              src={person.primaryPhotoUrl}
              alt={person.fullName}
              className="w-12 h-12 rounded-full mx-auto mb-2 object-cover"
            />
          )}
          <div className="font-semibold text-foreground text-sm">
            {person.fullName}
          </div>
          {person.birthDate?.year && (
            <div className="text-xs text-muted-foreground mt-1">
              {person.birthDate.year}
              {person.deathDate?.year && ` - ${person.deathDate.year}`}
            </div>
          )}
        </div>
      </Link>
      <Handle type="source" position={Position.Bottom} className="!bg-muted-foreground !w-2 !h-2" />
    </>
  )
}

const nodeTypes = {
  person: PersonNodeComponent,
}

type ViewMode = 'graph' | 'list'

// Helper to format approximate date
function formatApproximateDate(date?: { year?: number; month?: number; day?: number; isApproximate?: boolean }): string {
  if (!date || !date.year) return '-'
  const parts = []
  if (date.day) parts.push(String(date.day).padStart(2, '0'))
  if (date.month) parts.push(String(date.month).padStart(2, '0'))
  parts.push(String(date.year))
  const dateStr = parts.join('/')
  return date.isApproximate ? `~${dateStr}` : dateStr
}

// List view component
function PersonListView({ persons, relationships }: { persons: Person[], relationships: { personFromId: string; personToId: string; relationshipType: string }[] }) {
  const { t } = useTranslation()

  // Build parent lookup for each person
  const parentLookup = useMemo(() => {
    const lookup = new Map<string, string[]>()
    relationships.forEach(rel => {
      if (rel.relationshipType === 'PARENT') {
        const parents = lookup.get(rel.personToId) || []
        lookup.set(rel.personToId, [...parents, rel.personFromId])
      }
    })
    return lookup
  }, [relationships])

  // Create a name lookup
  const nameLookup = useMemo(() => {
    const lookup = new Map<string, string>()
    persons.forEach(p => lookup.set(p.id, p.fullName))
    return lookup
  }, [persons])

  // Sort persons by name
  const sortedPersons = useMemo(() => {
    return [...persons].sort((a, b) => a.fullName.localeCompare(b.fullName))
  }, [persons])

  return (
    <div className="bg-card rounded-lg border border-border overflow-hidden">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-border">
          <thead className="bg-secondary">
            <tr>
              <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
                {t('person.name', 'Name')}
              </th>
              <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
                {t('person.birthDate', 'Birth Date')}
              </th>
              <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
                {t('person.deathDate', 'Death Date')}
              </th>
              <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
                {t('person.birthPlace', 'Birth Place')}
              </th>
              <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
                {t('person.parents', 'Parents')}
              </th>
            </tr>
          </thead>
          <tbody className="bg-card divide-y divide-border">
            {sortedPersons.map((person) => {
              const parents = parentLookup.get(person.id) || []
              const parentNames = parents
                .map(pid => nameLookup.get(pid))
                .filter(Boolean)
                .join(', ')

              return (
                <tr key={person.id} className="hover:bg-secondary/50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <Link
                      to={`/person/${person.id}`}
                      className="flex items-center group"
                    >
                      {person.primaryPhotoUrl ? (
                        <img
                          src={person.primaryPhotoUrl}
                          alt={person.fullName}
                          className="w-8 h-8 rounded-full mr-3 object-cover"
                        />
                      ) : (
                        <div className={`w-8 h-8 rounded-full mr-3 flex items-center justify-center text-white text-xs font-bold ${
                          person.gender === 'MALE' ? 'bg-blue-500' :
                          person.gender === 'FEMALE' ? 'bg-pink-500' : 'bg-muted-foreground'
                        }`}>
                          {person.fullName.charAt(0)}
                        </div>
                      )}
                      <span className="text-sm font-medium text-foreground group-hover:text-primary">
                        {person.fullName}
                      </span>
                    </Link>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-muted-foreground">
                    {formatApproximateDate(person.birthDate)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-muted-foreground">
                    {formatApproximateDate(person.deathDate)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-muted-foreground">
                    {person.locationBirth || '-'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-muted-foreground">
                    {parentNames || '-'}
                  </td>
                </tr>
              )
            })}
          </tbody>
        </table>
      </div>
    </div>
  )
}

export default function TreePage() {
  const { t } = useTranslation()
  const { treeId } = useParams()
  const [viewMode, setViewMode] = useState<ViewMode>('graph')

  const { data: structure, isLoading, error } = useQuery<TreeStructure>({
    queryKey: ['tree', treeId],
    queryFn: () => {
      if (!treeId) {
        throw new Error('No tree ID provided')
      }
      return treesApi.getById(treeId)
    },
    enabled: !!treeId,
  })

  const { nodes: initialNodes, edges: initialEdges } = useMemo(() => {
    if (!structure) return { nodes: [], edges: [] }
    return convertToFlowData(structure)
  }, [structure])

  const [nodes, setNodes, onNodesChange] = useNodesState(initialNodes)
  const [edges, setEdges, onEdgesChange] = useEdgesState(initialEdges)

  // Update nodes/edges when structure changes
  useEffect(() => {
    if (structure) {
      const { nodes: newNodes, edges: newEdges } = convertToFlowData(structure)
      setNodes(newNodes)
      setEdges(newEdges)
    }
  }, [structure, setNodes, setEdges])

  const onNodeClick = useCallback((_event: React.MouseEvent, _node: Node) => {
    // Navigate to person page or show details
    // TODO: Implement node click handling
  }, [])

  // Show tree selection if no tree ID
  if (!treeId) {
    return (
      <div className="p-6 h-full">
        <h1 className="text-3xl font-bold text-foreground mb-6">
          {t('tree.title', 'Family Tree')}
        </h1>
        <div className="bg-card rounded-lg shadow p-6">
          <p className="text-muted-foreground mb-4">
            {t('tree.selectTree', 'Please select a family tree to view.')}
          </p>
          <TreeSelector />
        </div>
      </div>
    )
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
      <div className="p-6 h-full">
        <h1 className="text-3xl font-bold text-foreground mb-6">
          {t('tree.title', 'Family Tree')}
        </h1>
        <div className="bg-destructive/10 border border-destructive/30 rounded-lg p-6">
          <p className="text-destructive">
            {t('tree.error', 'Failed to load family tree')}
          </p>
        </div>
      </div>
    )
  }

  if (!structure || !structure.persons || structure.persons.length === 0) {
    return (
      <div className="p-6 h-full">
        <h1 className="text-3xl font-bold text-foreground mb-6">
          {structure?.treeName || t('tree.title', 'Family Tree')}
        </h1>
        <div className="bg-secondary rounded-lg h-[calc(100vh-200px)] flex items-center justify-center">
          <div className="text-center">
            <p className="text-muted-foreground mb-4">
              {t('tree.empty', 'This family tree is empty.')}
            </p>
            <Link
              to={`/tree/${treeId}/person/new`}
              className="inline-flex items-center px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90"
            >
              {t('tree.addFirstPerson', 'Add First Person')}
            </Link>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div
      data-testid="tree-page-root"
      className="p-6 h-[calc(100vh-8rem)] flex flex-col"
    >
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-3xl font-bold text-foreground">
          {structure.treeName}
        </h1>
        <div className="flex items-center gap-4">
          <Link
            to={`/tree/${treeId}/person/new`}
            className="inline-flex items-center gap-2 px-4 py-2 bg-primary text-primary-foreground text-sm font-medium rounded-lg hover:bg-primary/90 transition-colors"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
            </svg>
            {t('tree.addPerson', 'Add Person')}
          </Link>
          <div className="text-sm text-muted-foreground">
            {structure.persons?.length || 0} {t('tree.members', 'members')}
          </div>
          <div className="flex rounded-lg border border-border overflow-hidden">
            <button
              onClick={() => setViewMode('graph')}
              className={`px-3 py-1.5 text-sm font-medium transition-colors ${
                viewMode === 'graph'
                  ? 'bg-primary text-primary-foreground'
                  : 'bg-card text-foreground hover:bg-secondary'
              }`}
            >
              <span className="flex items-center gap-1.5">
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13.828 10.172a4 4 0 00-5.656 0l-4 4a4 4 0 105.656 5.656l1.102-1.101m-.758-4.899a4 4 0 005.656 0l4-4a4 4 0 00-5.656-5.656l-1.1 1.1" />
                </svg>
                {t('tree.graphView', 'Graph')}
              </span>
            </button>
            <button
              onClick={() => setViewMode('list')}
              className={`px-3 py-1.5 text-sm font-medium border-l border-border transition-colors ${
                viewMode === 'list'
                  ? 'bg-primary text-primary-foreground'
                  : 'bg-card text-foreground hover:bg-secondary'
              }`}
            >
              <span className="flex items-center gap-1.5">
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 10h16M4 14h16M4 18h16" />
                </svg>
                {t('tree.listView', 'List')}
              </span>
            </button>
          </div>
        </div>
      </div>

      {viewMode === 'graph' ? (
        <div
          data-testid="react-flow-container"
          className="flex-1 min-h-[400px] bg-secondary rounded-lg overflow-hidden border border-border"
        >
          <ReactFlow
            style={{ width: '100%', height: '100%' }}
            nodes={nodes}
            edges={edges}
            onNodesChange={onNodesChange}
            onEdgesChange={onEdgesChange}
            onNodeClick={onNodeClick}
            nodeTypes={nodeTypes}
            fitView
            fitViewOptions={{ padding: 0.2 }}
            minZoom={0.1}
            maxZoom={2}
          >
            <Background color="hsl(var(--border))" gap={20} />
            <Controls />
            <MiniMap
              nodeColor={(node) => {
                const person = node.data as unknown as Person
                if (person.deathDate) return 'hsl(var(--muted-foreground))'
                if (person.gender === 'MALE') return 'hsl(218, 89%, 72%)'
                if (person.gender === 'FEMALE') return 'hsl(351, 87%, 70%)'
                return 'hsl(var(--muted-foreground))'
              }}
            />
          </ReactFlow>
        </div>
      ) : (
        <div className="flex-1 overflow-auto">
          <PersonListView persons={structure.persons || []} relationships={structure.relationships || []} />
        </div>
      )}
    </div>
  )
}

// Tree selector component for when no tree is selected
function TreeSelector() {
  const { t } = useTranslation()

  const { data: treesPage, isLoading } = useQuery({
    queryKey: ['trees'],
    queryFn: () => treesApi.getAll(),
  })

  if (isLoading) {
    return <div className="text-muted-foreground">{t('common.loading', 'Loading...')}</div>
  }

  const trees = treesPage?.content || []

  if (trees.length === 0) {
    return (
      <div className="text-center py-6">
        <p className="text-muted-foreground mb-4">
          {t('tree.noTrees', 'No family trees found.')}
        </p>
        <Link
          to="/tree/new"
          className="inline-flex items-center px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90"
        >
          {t('tree.createFirst', 'Create Your First Tree')}
        </Link>
      </div>
    )
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      {trees.map((tree) => (
        <Link
          key={tree.id}
          to={`/tree/${tree.id}`}
          className="block p-4 bg-secondary rounded-lg hover:bg-secondary/80 transition-colors border border-border"
        >
          <h3 className="font-semibold text-foreground">{tree.name}</h3>
          {tree.description && (
            <p className="text-sm text-muted-foreground mt-1">{tree.description}</p>
          )}
          <div className="flex items-center gap-4 mt-2 text-xs text-muted-foreground">
            <span>{tree.personCount} members</span>
            {tree.rootPersonName && (
              <span>Root: {tree.rootPersonName}</span>
            )}
          </div>
        </Link>
      ))}
    </div>
  )
}
