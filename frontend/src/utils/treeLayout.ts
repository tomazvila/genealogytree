import { Node, Edge, Position, MarkerType } from '@xyflow/react'
import dagre from '@dagrejs/dagre'
import type { TreeStructure } from '@/types'

// Node dimensions used by dagre for layout calculation
const NODE_WIDTH = 180
const NODE_HEIGHT = 80

/**
 * Converts a tree structure (persons and relationships) into React Flow nodes and edges.
 * Uses dagre for hierarchical layout to position children under their parents.
 */
export function convertToFlowData(structure: TreeStructure): { nodes: Node[]; edges: Edge[] } {
  const relationships = structure.relationships || []
  const persons = structure.persons || []

  if (persons.length === 0) {
    return { nodes: [], edges: [] }
  }

  // Create a dagre graph for layout calculation
  const dagreGraph = new dagre.graphlib.Graph()
  dagreGraph.setDefaultEdgeLabel(() => ({}))
  dagreGraph.setGraph({
    rankdir: 'TB', // Top to bottom
    nodesep: 50, // Horizontal spacing between nodes
    ranksep: 100, // Vertical spacing between ranks/levels
    align: 'UL', // Align nodes to upper left
  })

  // Build adjacency maps to understand relationships
  const spouseOf = new Map<string, string[]>()

  relationships.forEach((rel) => {
    if (rel.relationshipType === 'SPOUSE') {
      const from = spouseOf.get(rel.personFromId) || []
      spouseOf.set(rel.personFromId, [...from, rel.personToId])

      const to = spouseOf.get(rel.personToId) || []
      spouseOf.set(rel.personToId, [...to, rel.personFromId])
    }
  })

  // Add all persons as nodes to the dagre graph
  persons.forEach((person) => {
    dagreGraph.setNode(person.id, { width: NODE_WIDTH, height: NODE_HEIGHT })
  })

  // Add PARENT and CHILD relationships as edges (these define the hierarchy)
  // Dagre uses these to determine rank/level and horizontal positioning
  // Convention: relationship type describes what personTo is TO personFrom
  relationships.forEach((rel) => {
    if (rel.relationshipType === 'PARENT') {
      // PARENT: personTo IS THE PARENT OF personFrom
      // Edge goes from parent (personTo, above) -> child (personFrom, below)
      dagreGraph.setEdge(rel.personToId, rel.personFromId)
    } else if (rel.relationshipType === 'CHILD') {
      // CHILD: personTo IS THE CHILD OF personFrom
      // Edge goes from parent (personFrom) -> child (personTo)
      dagreGraph.setEdge(rel.personFromId, rel.personToId)
    }
  })

  // For spouses, we want them at the same rank level
  // We do this by adding "invisible" rank constraints via setNode with minRank/maxRank
  // Actually, dagre handles this differently - spouses without parent-child edges
  // will be placed based on their own hierarchy, so we need a different approach

  // Group spouses: find spouse pairs where we need to ensure same rank
  const processedSpouses = new Set<string>()
  relationships.forEach((rel) => {
    if (rel.relationshipType === 'SPOUSE') {
      if (!processedSpouses.has(rel.personFromId) && !processedSpouses.has(rel.personToId)) {
        // For spouse pairs, we don't add edges to dagre - they'll be on the same rank
        // if they have children together (both connect to same children)
        processedSpouses.add(rel.personFromId)
        processedSpouses.add(rel.personToId)
      }
    }
  })

  // Run the dagre layout algorithm
  dagre.layout(dagreGraph)

  // Convert dagre nodes to React Flow nodes
  const nodes: Node[] = persons.map((person) => {
    const nodeWithPosition = dagreGraph.node(person.id)
    return {
      id: person.id,
      type: 'person',
      // Dagre positions are centered, React Flow uses top-left
      position: {
        x: nodeWithPosition.x - NODE_WIDTH / 2,
        y: nodeWithPosition.y - NODE_HEIGHT / 2,
      },
      data: person as unknown as Record<string, unknown>,
      sourcePosition: Position.Bottom,
      targetPosition: Position.Top,
    }
  })

  // Create edges from relationships
  // Convention: relationship type describes what personTo is TO personFrom
  // Arrows should flow from parent (above) to child (below)
  const edges: Edge[] = relationships.map((rel, index) => {
    const isSpouse = rel.relationshipType === 'SPOUSE'
    const isParent = rel.relationshipType === 'PARENT'
    const isChild = rel.relationshipType === 'CHILD'

    // Determine edge direction so arrows flow from parent to child
    let source = rel.personFromId
    let target = rel.personToId

    if (isParent) {
      // PARENT: personTo IS THE PARENT OF personFrom
      // Arrow: parent (personTo) -> child (personFrom)
      source = rel.personToId
      target = rel.personFromId
    } else if (isChild) {
      // CHILD: personTo IS THE CHILD OF personFrom
      // Arrow: parent (personFrom) -> child (personTo)
      source = rel.personFromId
      target = rel.personToId
    }

    return {
      id: `edge-${rel.id || index}`,
      source,
      target,
      type: isSpouse ? 'straight' : 'smoothstep',
      animated: isSpouse,
      style: {
        stroke: isSpouse ? '#ec4899' : '#3b82f6',
        strokeWidth: 2,
      },
      markerEnd: isSpouse
        ? undefined
        : {
            type: MarkerType.ArrowClosed,
            color: '#3b82f6',
          },
      label: isSpouse ? (rel.isDivorced ? 'Divorced' : '') : '',
      labelStyle: { fill: '#6b7280', fontSize: 10 },
    }
  })

  return { nodes, edges }
}
