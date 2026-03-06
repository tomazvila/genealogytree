import { describe, it, expect } from 'vitest'
import { convertToFlowData } from '@/utils/treeLayout'
import type { TreeStructure } from '@/types'

describe('convertToFlowData', () => {
  describe('hierarchical layout', () => {
    it('should cluster children under their parent, keeping families separate', () => {
      // Two families: Parent A with 2 children, Parent B with 3 children
      // Convention: PARENT type means personTo IS THE PARENT OF personFrom
      const structure: TreeStructure = {
        treeId: 'test-tree',
        treeName: 'Test Tree',
        persons: [
          { id: 'parentA', fullName: 'Parent A', gender: 'MALE', birthDate: {}, createdAt: '', updatedAt: '' },
          { id: 'parentB', fullName: 'Parent B', gender: 'MALE', birthDate: {}, createdAt: '', updatedAt: '' },
          { id: 'childA1', fullName: 'Child A1', gender: 'MALE', birthDate: {}, createdAt: '', updatedAt: '' },
          { id: 'childA2', fullName: 'Child A2', gender: 'FEMALE', birthDate: {}, createdAt: '', updatedAt: '' },
          { id: 'childB1', fullName: 'Child B1', gender: 'MALE', birthDate: {}, createdAt: '', updatedAt: '' },
          { id: 'childB2', fullName: 'Child B2', gender: 'FEMALE', birthDate: {}, createdAt: '', updatedAt: '' },
          { id: 'childB3', fullName: 'Child B3', gender: 'MALE', birthDate: {}, createdAt: '', updatedAt: '' },
        ],
        relationships: [
          // PARENT: personTo (parentA) IS THE PARENT OF personFrom (childA1)
          { id: 'r1', personFromId: 'childA1', personToId: 'parentA', personFromName: 'Child A1', personToName: 'Parent A', relationshipType: 'PARENT' },
          { id: 'r2', personFromId: 'childA2', personToId: 'parentA', personFromName: 'Child A2', personToName: 'Parent A', relationshipType: 'PARENT' },
          { id: 'r3', personFromId: 'childB1', personToId: 'parentB', personFromName: 'Child B1', personToName: 'Parent B', relationshipType: 'PARENT' },
          { id: 'r4', personFromId: 'childB2', personToId: 'parentB', personFromName: 'Child B2', personToName: 'Parent B', relationshipType: 'PARENT' },
          { id: 'r5', personFromId: 'childB3', personToId: 'parentB', personFromName: 'Child B3', personToName: 'Parent B', relationshipType: 'PARENT' },
        ],
      }

      const { nodes } = convertToFlowData(structure)

      const getNodePosition = (id: string) => {
        const node = nodes.find((n) => n.id === id)
        if (!node) throw new Error(`Node ${id} not found`)
        return node.position
      }

      const parentAPos = getNodePosition('parentA')
      const parentBPos = getNodePosition('parentB')
      const childA1Pos = getNodePosition('childA1')
      const childA2Pos = getNodePosition('childA2')
      const childB1Pos = getNodePosition('childB1')
      const childB2Pos = getNodePosition('childB2')
      const childB3Pos = getNodePosition('childB3')

      // Parents should be at the same level (same y)
      expect(parentAPos.y).toBe(parentBPos.y)

      // Children should be below their parents (higher y value)
      expect(childA1Pos.y).toBeGreaterThan(parentAPos.y)
      expect(childB1Pos.y).toBeGreaterThan(parentBPos.y)

      // CRITICAL: Children should be CLOSER to (or equidistant from) their own parent vs the other parent
      // This ensures hierarchical clustering, not flat spreading across the whole tree
      // Note: We use <= to handle edge cases where a child is exactly equidistant
      const distA1ToParentA = Math.abs(childA1Pos.x - parentAPos.x)
      const distA1ToParentB = Math.abs(childA1Pos.x - parentBPos.x)
      expect(distA1ToParentA).toBeLessThanOrEqual(distA1ToParentB)

      const distA2ToParentA = Math.abs(childA2Pos.x - parentAPos.x)
      const distA2ToParentB = Math.abs(childA2Pos.x - parentBPos.x)
      expect(distA2ToParentA).toBeLessThanOrEqual(distA2ToParentB)

      const distB1ToParentB = Math.abs(childB1Pos.x - parentBPos.x)
      const distB1ToParentA = Math.abs(childB1Pos.x - parentAPos.x)
      expect(distB1ToParentB).toBeLessThanOrEqual(distB1ToParentA)

      const distB2ToParentB = Math.abs(childB2Pos.x - parentBPos.x)
      const distB2ToParentA = Math.abs(childB2Pos.x - parentAPos.x)
      expect(distB2ToParentB).toBeLessThanOrEqual(distB2ToParentA)

      const distB3ToParentB = Math.abs(childB3Pos.x - parentBPos.x)
      const distB3ToParentA = Math.abs(childB3Pos.x - parentAPos.x)
      expect(distB3ToParentB).toBeLessThanOrEqual(distB3ToParentA)

      // Families should NOT overlap - A's children separate from B's children
      const maxChildAX = Math.max(childA1Pos.x, childA2Pos.x)
      const minChildBX = Math.min(childB1Pos.x, childB2Pos.x, childB3Pos.x)

      // If parent A is to the left of parent B, children of A should also be to the left
      if (parentAPos.x < parentBPos.x) {
        expect(maxChildAX).toBeLessThan(minChildBX)
      } else {
        expect(minChildBX).toBeLessThan(maxChildAX)
      }
    })

    it('should position spouses next to each other at the same level', () => {
      // Convention: PARENT type means personTo IS THE PARENT OF personFrom
      const structure: TreeStructure = {
        treeId: 'test-tree',
        treeName: 'Test Tree',
        persons: [
          { id: 'husband', fullName: 'Husband', gender: 'MALE', birthDate: {}, createdAt: '', updatedAt: '' },
          { id: 'wife', fullName: 'Wife', gender: 'FEMALE', birthDate: {}, createdAt: '', updatedAt: '' },
          { id: 'child', fullName: 'Child', gender: 'MALE', birthDate: {}, createdAt: '', updatedAt: '' },
        ],
        relationships: [
          { id: 'r1', personFromId: 'husband', personToId: 'wife', personFromName: 'Husband', personToName: 'Wife', relationshipType: 'SPOUSE' },
          // PARENT: personTo (husband) IS THE PARENT OF personFrom (child)
          { id: 'r2', personFromId: 'child', personToId: 'husband', personFromName: 'Child', personToName: 'Husband', relationshipType: 'PARENT' },
          { id: 'r3', personFromId: 'child', personToId: 'wife', personFromName: 'Child', personToName: 'Wife', relationshipType: 'PARENT' },
        ],
      }

      const { nodes } = convertToFlowData(structure)

      const getNodePosition = (id: string) => {
        const node = nodes.find((n) => n.id === id)
        if (!node) throw new Error(`Node ${id} not found`)
        return node.position
      }

      const husbandPos = getNodePosition('husband')
      const wifePos = getNodePosition('wife')
      const childPos = getNodePosition('child')

      // Spouses should be at the same y level
      expect(husbandPos.y).toBe(wifePos.y)

      // Child should be centered under the spouses (within reasonable tolerance)
      const parentsCenterX = (husbandPos.x + wifePos.x) / 2
      expect(Math.abs(childPos.x - parentsCenterX)).toBeLessThan(200)
    })

    it('should handle deep family trees with multiple generations', () => {
      // Convention: PARENT type means personTo IS THE PARENT OF personFrom
      const structure: TreeStructure = {
        treeId: 'test-tree',
        treeName: 'Test Tree',
        persons: [
          { id: 'grandpa', fullName: 'Grandpa', gender: 'MALE', birthDate: {}, createdAt: '', updatedAt: '' },
          { id: 'parent', fullName: 'Parent', gender: 'MALE', birthDate: {}, createdAt: '', updatedAt: '' },
          { id: 'grandchild', fullName: 'Grandchild', gender: 'MALE', birthDate: {}, createdAt: '', updatedAt: '' },
        ],
        relationships: [
          // PARENT: personTo (grandpa) IS THE PARENT OF personFrom (parent)
          { id: 'r1', personFromId: 'parent', personToId: 'grandpa', personFromName: 'Parent', personToName: 'Grandpa', relationshipType: 'PARENT' },
          { id: 'r2', personFromId: 'grandchild', personToId: 'parent', personFromName: 'Grandchild', personToName: 'Parent', relationshipType: 'PARENT' },
        ],
      }

      const { nodes } = convertToFlowData(structure)

      const getNodePosition = (id: string) => {
        const node = nodes.find((n) => n.id === id)
        if (!node) throw new Error(`Node ${id} not found`)
        return node.position
      }

      const grandpaPos = getNodePosition('grandpa')
      const parentPos = getNodePosition('parent')
      const grandchildPos = getNodePosition('grandchild')

      // Each generation should be at a different y level, increasing downward
      expect(parentPos.y).toBeGreaterThan(grandpaPos.y)
      expect(grandchildPos.y).toBeGreaterThan(parentPos.y)

      // In a single lineage, x positions should remain relatively aligned
      expect(Math.abs(grandpaPos.x - parentPos.x)).toBeLessThan(200)
      expect(Math.abs(parentPos.x - grandchildPos.x)).toBeLessThan(200)
    })

    it('should handle empty tree structure', () => {
      const structure: TreeStructure = {
        treeId: 'test-tree',
        treeName: 'Test Tree',
        persons: [],
        relationships: [],
      }

      const { nodes, edges } = convertToFlowData(structure)

      expect(nodes).toHaveLength(0)
      expect(edges).toHaveLength(0)
    })

    it('should handle CHILD relationship type (inverse of PARENT)', () => {
      // Convention: CHILD means personTo IS THE CHILD OF personFrom
      // So the parent node is personFrom, child node is personTo
      const structure: TreeStructure = {
        treeId: 'test-tree',
        treeName: 'Test Tree',
        persons: [
          { id: 'parent', fullName: 'Parent', gender: 'MALE', birthDate: {}, createdAt: '', updatedAt: '' },
          { id: 'child', fullName: 'Child', gender: 'MALE', birthDate: {}, createdAt: '', updatedAt: '' },
        ],
        relationships: [
          // CHILD: personTo (child) IS THE CHILD OF personFrom (parent)
          { id: 'r1', personFromId: 'parent', personToId: 'child', personFromName: 'Parent', personToName: 'Child', relationshipType: 'CHILD' },
        ],
      }

      const { nodes } = convertToFlowData(structure)

      const getNodePosition = (id: string) => {
        const node = nodes.find((n) => n.id === id)
        if (!node) throw new Error(`Node ${id} not found`)
        return node.position
      }

      const parentPos = getNodePosition('parent')
      const childPos = getNodePosition('child')

      // Parent should be above child (lower y value) even though relationship is CHILD type
      expect(parentPos.y).toBeLessThan(childPos.y)
    })
  })

  describe('edges', () => {
    it('should create edges for all relationships', () => {
      // Convention: PARENT type means personTo IS THE PARENT OF personFrom
      const structure: TreeStructure = {
        treeId: 'test-tree',
        treeName: 'Test Tree',
        persons: [
          { id: 'parent', fullName: 'Parent', gender: 'MALE', birthDate: {}, createdAt: '', updatedAt: '' },
          { id: 'child', fullName: 'Child', gender: 'FEMALE', birthDate: {}, createdAt: '', updatedAt: '' },
        ],
        relationships: [
          { id: 'r1', personFromId: 'child', personToId: 'parent', personFromName: 'Child', personToName: 'Parent', relationshipType: 'PARENT' },
        ],
      }

      const { edges } = convertToFlowData(structure)

      expect(edges).toHaveLength(1)
      // Edge flows from parent to child (arrow points downward)
      expect(edges[0].source).toBe('parent')
      expect(edges[0].target).toBe('child')
    })

    it('should style spouse edges differently from parent edges', () => {
      // Convention: PARENT type means personTo IS THE PARENT OF personFrom
      const structure: TreeStructure = {
        treeId: 'test-tree',
        treeName: 'Test Tree',
        persons: [
          { id: 'husband', fullName: 'Husband', gender: 'MALE', birthDate: {}, createdAt: '', updatedAt: '' },
          { id: 'wife', fullName: 'Wife', gender: 'FEMALE', birthDate: {}, createdAt: '', updatedAt: '' },
          { id: 'child', fullName: 'Child', gender: 'MALE', birthDate: {}, createdAt: '', updatedAt: '' },
        ],
        relationships: [
          { id: 'r1', personFromId: 'husband', personToId: 'wife', personFromName: 'Husband', personToName: 'Wife', relationshipType: 'SPOUSE' },
          // PARENT: personTo (husband) IS THE PARENT OF personFrom (child)
          { id: 'r2', personFromId: 'child', personToId: 'husband', personFromName: 'Child', personToName: 'Husband', relationshipType: 'PARENT' },
        ],
      }

      const { edges } = convertToFlowData(structure)

      const spouseEdge = edges.find((e) => e.source === 'husband' && e.target === 'wife')
      // Edge flows from parent (husband) to child (arrow points downward)
      const parentEdge = edges.find((e) => e.source === 'husband' && e.target === 'child')

      expect(spouseEdge).toBeDefined()
      expect(parentEdge).toBeDefined()

      // Spouse edges should be animated and straight
      expect(spouseEdge!.animated).toBe(true)
      expect(spouseEdge!.type).toBe('straight')

      // Parent edges should not be animated and use smoothstep
      expect(parentEdge!.animated).toBeFalsy()
      expect(parentEdge!.type).toBe('smoothstep')
    })
  })
})
