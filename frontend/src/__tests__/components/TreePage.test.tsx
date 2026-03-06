import { render, screen, waitFor } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import TreePage from '@/pages/TreePage'

// Mock the trees API
vi.mock('@/api/trees', () => ({
  treesApi: {
    getById: vi.fn(),
    getAll: vi.fn(),
  },
}))

// Mock i18next
vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (_key: string, fallback: string) => fallback,
  }),
}))

// Create a fresh QueryClient for each test
function createTestQueryClient() {
  return new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  })
}

// Mock tree structure data
const mockTreeStructure = {
  treeId: 'test-tree-id',
  treeName: 'Test Family Tree',
  persons: [
    {
      id: 'person-1',
      fullName: 'John Doe',
      gender: 'MALE' as const,
      birthDate: { year: 1950 },
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z',
    },
    {
      id: 'person-2',
      fullName: 'Jane Doe',
      gender: 'FEMALE' as const,
      birthDate: { year: 1955 },
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z',
    },
  ],
  relationships: [
    {
      id: 'rel-1',
      personFromId: 'person-1',
      personToId: 'person-2',
      personFromName: 'John Doe',
      personToName: 'Jane Doe',
      relationshipType: 'SPOUSE' as const,
    },
  ],
}

// Empty tree structure
const mockEmptyTreeStructure = {
  treeId: 'empty-tree-id',
  treeName: 'Empty Family Tree',
  persons: [],
  relationships: [],
}

function renderTreePage(treeId: string, queryClient: QueryClient) {
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[`/tree/${treeId}`]}>
        <Routes>
          <Route path="/tree/:treeId" element={<TreePage />} />
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>
  )
}

// Render with a parent container that simulates Layout context
function renderTreePageWithLayoutContext(treeId: string, queryClient: QueryClient) {
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[`/tree/${treeId}`]}>
        <div style={{ height: '100vh', display: 'flex', flexDirection: 'column' }}>
          <div style={{ flex: 1, minHeight: 0 }}>
            <Routes>
              <Route path="/tree/:treeId" element={<TreePage />} />
            </Routes>
          </div>
        </div>
      </MemoryRouter>
    </QueryClientProvider>
  )
}

describe('TreePage', () => {
  let queryClient: QueryClient

  beforeEach(() => {
    queryClient = createTestQueryClient()
    vi.clearAllMocks()
  })

  describe('React Flow container sizing', () => {
    it('should have a container with explicit minimum height for React Flow to render', async () => {
      // Setup mock to return tree data
      const { treesApi } = await import('@/api/trees')
      vi.mocked(treesApi.getById).mockResolvedValue(mockTreeStructure)

      renderTreePage('test-tree-id', queryClient)

      // Wait for tree name to appear (indicates data loaded)
      const treeName = await screen.findByText('Test Family Tree')
      expect(treeName).toBeInTheDocument()

      // Find the React Flow container - it should have the class that wraps ReactFlow
      const reactFlowContainer = document.querySelector('.react-flow')
      expect(reactFlowContainer).toBeInTheDocument()

      // The parent container of React Flow should have explicit dimensions
      const parentContainer = reactFlowContainer?.parentElement
      expect(parentContainer).toBeInTheDocument()

      // The container should have a min-height class to ensure React Flow can render
      // This is critical: React Flow error #004 occurs when container has no dimensions
      expect(parentContainer?.className).toMatch(/min-h-\[/)
    })

    it('should have React Flow container with data-testid for explicit targeting', async () => {
      const { treesApi } = await import('@/api/trees')
      vi.mocked(treesApi.getById).mockResolvedValue(mockTreeStructure)

      renderTreePage('test-tree-id', queryClient)

      // Wait for data to load
      await screen.findByText('Test Family Tree')

      // The React Flow container should have a data-testid for reliable testing
      const flowContainer = screen.getByTestId('react-flow-container')
      expect(flowContainer).toBeInTheDocument()
    })

    it('should use min-height with viewport units for reliable sizing', async () => {
      const { treesApi } = await import('@/api/trees')
      vi.mocked(treesApi.getById).mockResolvedValue(mockTreeStructure)

      renderTreePage('test-tree-id', queryClient)

      await screen.findByText('Test Family Tree')

      // The container should have a minimum height that doesn't rely solely on parent percentage
      const flowContainer = screen.getByTestId('react-flow-container')

      // Check that the container has proper sizing CSS classes
      expect(flowContainer.className).toMatch(/min-h-\[/)
    })

    it('should render React Flow with nodes when tree has persons', async () => {
      const { treesApi } = await import('@/api/trees')
      vi.mocked(treesApi.getById).mockResolvedValue(mockTreeStructure)

      renderTreePageWithLayoutContext('test-tree-id', queryClient)

      await screen.findByText('Test Family Tree')

      // React Flow should be present and contain nodes
      const reactFlowContainer = document.querySelector('.react-flow')
      expect(reactFlowContainer).toBeInTheDocument()

      // Nodes should be rendered (React Flow creates nodes with react-flow__node class)
      await waitFor(() => {
        const nodes = document.querySelectorAll('.react-flow__node')
        expect(nodes.length).toBeGreaterThan(0)
      })
    })

    it('should have proper height chain from page root to React Flow container', async () => {
      const { treesApi } = await import('@/api/trees')
      vi.mocked(treesApi.getById).mockResolvedValue(mockTreeStructure)

      renderTreePage('test-tree-id', queryClient)

      await screen.findByText('Test Family Tree')

      // Find the page root element
      const pageRoot = screen.getByTestId('tree-page-root')
      expect(pageRoot).toBeInTheDocument()

      // The page root should establish height context with viewport-based height
      expect(pageRoot.className).toMatch(/h-\[calc|min-h-\[/)
    })
  })

  describe('loading state', () => {
    it('should show loading indicator while fetching tree', async () => {
      const { treesApi } = await import('@/api/trees')
      // Create a promise that we can control
      let resolvePromise: (value: unknown) => void
      const promise = new Promise((resolve) => {
        resolvePromise = resolve
      })
      vi.mocked(treesApi.getById).mockReturnValue(promise as Promise<typeof mockTreeStructure>)

      renderTreePage('test-tree-id', queryClient)

      // Should show loading state
      expect(screen.getByText('Loading...')).toBeInTheDocument()

      // Resolve the promise
      resolvePromise!(mockTreeStructure)
    })
  })

  describe('error state', () => {
    it('should show error message when tree fetch fails', async () => {
      const { treesApi } = await import('@/api/trees')
      vi.mocked(treesApi.getById).mockRejectedValue(new Error('Network error'))

      renderTreePage('test-tree-id', queryClient)

      // Wait for error message
      await screen.findByText('Failed to load family tree')
    })
  })

  describe('empty tree', () => {
    it('should show empty state when tree has no persons', async () => {
      const { treesApi } = await import('@/api/trees')
      vi.mocked(treesApi.getById).mockResolvedValue(mockEmptyTreeStructure)

      renderTreePage('empty-tree-id', queryClient)

      // Wait for empty state message
      await screen.findByText('This family tree is empty.')
      expect(screen.getByText('Add First Person')).toBeInTheDocument()
    })
  })
})
