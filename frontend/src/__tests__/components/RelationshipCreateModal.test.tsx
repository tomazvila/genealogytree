import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import RelationshipCreateModal from '@/components/RelationshipCreateModal'

// Mock the relationships API
vi.mock('@/api/relationships', () => ({
  relationshipsApi: {
    create: vi.fn(),
  },
}))

// Mock the trees API for getting persons in tree
vi.mock('@/api/trees', () => ({
  treesApi: {
    getById: vi.fn(),
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

// Mock current person
const mockCurrentPerson = {
  id: 'person-123',
  fullName: 'John Doe',
  birthDate: { year: 1950 },
  createdAt: '2024-01-01T00:00:00Z',
  updatedAt: '2024-01-01T00:00:00Z',
}

// Mock tree with other persons
const mockTree = {
  treeId: 'tree-123',
  treeName: 'Doe Family Tree',
  createdBy: 'user-123',
  persons: [
    mockCurrentPerson,
    {
      id: 'person-456',
      fullName: 'Jane Doe',
      birthDate: { year: 1955 },
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z',
    },
    {
      id: 'person-789',
      fullName: 'Jim Doe',
      birthDate: { year: 1980 },
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z',
    },
  ],
  relationships: [],
}

interface RenderProps {
  isOpen?: boolean
  onClose?: () => void
  onSuccess?: () => void
}

function renderModal(queryClient: QueryClient, props: RenderProps = {}) {
  const defaultProps = {
    isOpen: true,
    onClose: vi.fn(),
    onSuccess: vi.fn(),
    currentPerson: mockCurrentPerson,
    treeId: 'tree-123',
  }

  return render(
    <QueryClientProvider client={queryClient}>
      <RelationshipCreateModal {...defaultProps} {...props} />
    </QueryClientProvider>
  )
}

describe('RelationshipCreateModal', () => {
  let queryClient: QueryClient

  beforeEach(async () => {
    queryClient = createTestQueryClient()
    vi.clearAllMocks()

    // Default mock for tree
    const { treesApi } = await import('@/api/trees')
    vi.mocked(treesApi.getById).mockResolvedValue(mockTree)
  })

  describe('rendering', () => {
    it('should display modal title', async () => {
      renderModal(queryClient)

      await screen.findByRole('dialog', { name: /add family member/i })
    })

    it('should display relationship type selector', async () => {
      renderModal(queryClient)

      await screen.findByLabelText(/relationship type/i)
    })

    it('should display person selector', async () => {
      renderModal(queryClient)

      await screen.findByLabelText(/select person/i)
    })

    it('should show other persons from tree (excluding current person)', async () => {
      renderModal(queryClient)

      // Wait for tree to load
      await screen.findByLabelText(/select person/i)

      // Should show other persons but not the current one
      const personSelect = screen.getByLabelText(/select person/i)
      expect(personSelect).toBeInTheDocument()

      // Check that Jane and Jim are in options but not John (current person)
      await waitFor(() => {
        expect(screen.getByText(/jane doe/i)).toBeInTheDocument()
        expect(screen.getByText(/jim doe/i)).toBeInTheDocument()
      })
    })

    it('should display cancel and save buttons', async () => {
      renderModal(queryClient)

      await screen.findByRole('button', { name: /cancel/i })
      await screen.findByRole('button', { name: /add relationship/i })
    })
  })

  describe('relationship types', () => {
    it('should have Parent option', async () => {
      renderModal(queryClient)

      const typeSelect = await screen.findByLabelText(/relationship type/i)
      expect(typeSelect).toBeInTheDocument()
      expect(screen.getByRole('option', { name: /parent/i })).toBeInTheDocument()
    })

    it('should have Child option', async () => {
      renderModal(queryClient)

      await screen.findByLabelText(/relationship type/i)
      expect(screen.getByRole('option', { name: /child/i })).toBeInTheDocument()
    })

    it('should have Spouse option', async () => {
      renderModal(queryClient)

      await screen.findByLabelText(/relationship type/i)
      expect(screen.getByRole('option', { name: /spouse/i })).toBeInTheDocument()
    })

    it('should have Sibling option', async () => {
      renderModal(queryClient)

      await screen.findByLabelText(/relationship type/i)
      expect(screen.getByRole('option', { name: /sibling/i })).toBeInTheDocument()
    })

    it('should show marriage date fields when Spouse is selected', async () => {
      const user = userEvent.setup()
      renderModal(queryClient)

      const typeSelect = await screen.findByLabelText(/relationship type/i)
      await user.selectOptions(typeSelect, 'SPOUSE')

      await screen.findByLabelText(/marriage date/i)
    })
  })

  describe('form submission', () => {
    it('should call API with correct data when creating parent relationship', async () => {
      const user = userEvent.setup()
      const onSuccess = vi.fn()
      const { relationshipsApi } = await import('@/api/relationships')
      vi.mocked(relationshipsApi.create).mockResolvedValue({
        id: 'rel-123',
        personFromId: 'person-123',
        personFromName: 'John Doe',
        personToId: 'person-456',
        personToName: 'Jane Doe',
        relationshipType: 'PARENT',
      })

      renderModal(queryClient, { onSuccess })

      // Wait for tree to load
      await screen.findByLabelText(/select person/i)

      // Select relationship type
      await user.selectOptions(screen.getByLabelText(/relationship type/i), 'PARENT')

      // Select person
      await user.selectOptions(screen.getByLabelText(/select person/i), 'person-456')

      // Submit
      await user.click(screen.getByRole('button', { name: /add relationship/i }))

      await waitFor(() => {
        expect(relationshipsApi.create).toHaveBeenCalledWith(
          expect.objectContaining({
            personFromId: 'person-123',
            personToId: 'person-456',
            relationshipType: 'PARENT',
          })
        )
      })
    })

    it('should call onSuccess after successful creation', async () => {
      const user = userEvent.setup()
      const onSuccess = vi.fn()
      const { relationshipsApi } = await import('@/api/relationships')
      vi.mocked(relationshipsApi.create).mockResolvedValue({
        id: 'rel-123',
        personFromId: 'person-123',
        personFromName: 'John Doe',
        personToId: 'person-456',
        personToName: 'Jane Doe',
        relationshipType: 'CHILD',
      })

      renderModal(queryClient, { onSuccess })

      await screen.findByLabelText(/select person/i)

      await user.selectOptions(screen.getByLabelText(/relationship type/i), 'CHILD')
      await user.selectOptions(screen.getByLabelText(/select person/i), 'person-456')
      await user.click(screen.getByRole('button', { name: /add relationship/i }))

      await waitFor(() => {
        expect(onSuccess).toHaveBeenCalled()
      })
    })

    it('should show error message on API failure', async () => {
      const user = userEvent.setup()
      const { relationshipsApi } = await import('@/api/relationships')
      vi.mocked(relationshipsApi.create).mockRejectedValue(new Error('Network error'))

      renderModal(queryClient)

      await screen.findByLabelText(/select person/i)

      await user.selectOptions(screen.getByLabelText(/relationship type/i), 'SIBLING')
      await user.selectOptions(screen.getByLabelText(/select person/i), 'person-456')
      await user.click(screen.getByRole('button', { name: /add relationship/i }))

      await screen.findByText(/failed to create relationship/i)
    })
  })

  describe('spouse-specific fields', () => {
    it('should include marriage date when provided for spouse', async () => {
      const user = userEvent.setup()
      const { relationshipsApi } = await import('@/api/relationships')
      vi.mocked(relationshipsApi.create).mockResolvedValue({
        id: 'rel-123',
        personFromId: 'person-123',
        personFromName: 'John Doe',
        personToId: 'person-456',
        personToName: 'Jane Doe',
        relationshipType: 'SPOUSE',
        startDate: { year: 1975 },
      })

      renderModal(queryClient)

      await screen.findByLabelText(/select person/i)

      await user.selectOptions(screen.getByLabelText(/relationship type/i), 'SPOUSE')
      await user.selectOptions(screen.getByLabelText(/select person/i), 'person-456')
      await user.type(screen.getByLabelText(/marriage date/i), '1975')
      await user.click(screen.getByRole('button', { name: /add relationship/i }))

      await waitFor(() => {
        expect(relationshipsApi.create).toHaveBeenCalledWith(
          expect.objectContaining({
            relationshipType: 'SPOUSE',
            startDate: expect.objectContaining({ year: 1975 }),
          })
        )
      })
    })

    it('should include divorced flag when checked', async () => {
      const user = userEvent.setup()
      const { relationshipsApi } = await import('@/api/relationships')
      vi.mocked(relationshipsApi.create).mockResolvedValue({
        id: 'rel-123',
        personFromId: 'person-123',
        personFromName: 'John Doe',
        personToId: 'person-456',
        personToName: 'Jane Doe',
        relationshipType: 'SPOUSE',
        isDivorced: true,
      })

      renderModal(queryClient)

      await screen.findByLabelText(/select person/i)

      await user.selectOptions(screen.getByLabelText(/relationship type/i), 'SPOUSE')
      await user.selectOptions(screen.getByLabelText(/select person/i), 'person-456')
      await user.click(screen.getByRole('checkbox', { name: /divorced/i }))
      await user.click(screen.getByRole('button', { name: /add relationship/i }))

      await waitFor(() => {
        expect(relationshipsApi.create).toHaveBeenCalledWith(
          expect.objectContaining({
            isDivorced: true,
          })
        )
      })
    })
  })

  describe('cancel action', () => {
    it('should call onClose when clicking cancel', async () => {
      const user = userEvent.setup()
      const onClose = vi.fn()

      renderModal(queryClient, { onClose })

      await screen.findByRole('dialog')

      await user.click(screen.getByRole('button', { name: /cancel/i }))

      expect(onClose).toHaveBeenCalled()
    })
  })

  describe('validation', () => {
    it('should show error when no person selected', async () => {
      const user = userEvent.setup()
      renderModal(queryClient)

      await screen.findByLabelText(/select person/i)

      await user.selectOptions(screen.getByLabelText(/relationship type/i), 'PARENT')
      // Don't select a person
      await user.click(screen.getByRole('button', { name: /add relationship/i }))

      await screen.findByText(/please select a person/i)
    })

    it('should show error when no relationship type selected', async () => {
      const user = userEvent.setup()
      renderModal(queryClient)

      // Wait for person options to load
      await waitFor(() => {
        expect(screen.getByText(/jane doe/i)).toBeInTheDocument()
      })

      // Select person but not type
      await user.selectOptions(screen.getByLabelText(/select person/i), 'person-456')
      await user.click(screen.getByRole('button', { name: /add relationship/i }))

      await screen.findByText(/please select a relationship type/i)
    })
  })
})
