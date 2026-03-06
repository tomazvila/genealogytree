import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import PersonPage from '@/pages/PersonPage'
import { useUIStore } from '@/store/uiStore'

// Mock the persons API
vi.mock('@/api/persons', () => ({
  personsApi: {
    getById: vi.fn(),
    getRelatives: vi.fn(),
    update: vi.fn(),
  },
}))

// Mock the trees API
vi.mock('@/api/trees', () => ({
  treesApi: {
    getById: vi.fn(),
  },
}))

// Mock the photos API
vi.mock('@/api/photos', () => ({
  photosApi: {
    getByPersonId: vi.fn(),
    upload: vi.fn(),
    linkToPersons: vi.fn(),
  },
}))

// Mock i18next
vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (_key: string, fallback: string) => fallback,
  }),
}))

// Mock authStore - use an object that tests can mutate
let mockAuthState = {
  user: { id: 'user-123', username: 'testuser', role: 'USER' as const, status: 'ACTIVE' as const, createdAt: '2024-01-01T00:00:00Z' } as { id: string; username: string; role: 'USER' | 'ADMIN'; status: 'ACTIVE' | 'PENDING_APPROVAL' | 'SUSPENDED'; createdAt: string } | null,
  isAdmin: false,
}

vi.mock('@/store/authStore', () => ({
  useAuthStore: vi.fn((selector: (state: typeof mockAuthState) => unknown) => {
    return selector(mockAuthState)
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

// Mock person data
const mockPerson = {
  id: 'person-123',
  fullName: 'John Doe',
  gender: 'MALE' as const,
  birthDate: { year: 1950, month: 3, day: 15, isApproximate: false },
  deathDate: { year: 2020, month: 12, day: 25, isApproximate: true },
  biography: 'A loving father and grandfather.',
  locationBirth: 'New York, USA',
  locationDeath: 'Los Angeles, USA',
  locationBurial: 'Forest Lawn Cemetery',
  primaryPhotoUrl: '/photos/john-doe.jpg',
  treeId: 'tree-123',
  createdAt: '2024-01-01T00:00:00Z',
  updatedAt: '2024-01-01T00:00:00Z',
}

// Mock tree data
const mockTree = {
  treeId: 'tree-123',
  treeName: 'Doe Family Tree',
  createdBy: 'owner-user-id',
  persons: [],
  relationships: [],
}

const mockRelatives = [
  {
    id: 'person-456',
    fullName: 'Jane Doe',
    gender: 'FEMALE' as const,
    birthDate: { year: 1955 },
    relationshipType: 'SPOUSE' as const,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'person-789',
    fullName: 'Jim Doe',
    gender: 'MALE' as const,
    birthDate: { year: 1980 },
    relationshipType: 'CHILD' as const,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
]

const mockRelativesWithChildren = [
  {
    id: 'person-parent',
    fullName: 'Mary Doe',
    gender: 'FEMALE' as const,
    birthDate: { year: 1920 },
    relationshipType: 'PARENT' as const,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'person-child-1',
    fullName: 'Alice Doe',
    gender: 'FEMALE' as const,
    birthDate: { year: 1975 },
    relationshipType: 'CHILD' as const,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'person-child-2',
    fullName: 'Bob Doe',
    gender: 'MALE' as const,
    birthDate: { year: 1978 },
    relationshipType: 'CHILD' as const,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
]

const mockRelativesWithSiblings = [
  {
    id: 'person-sibling-1',
    fullName: 'Sarah Doe',
    gender: 'FEMALE' as const,
    birthDate: { year: 1952 },
    relationshipType: 'SIBLING' as const,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'person-sibling-2',
    fullName: 'Tom Doe',
    gender: 'MALE' as const,
    birthDate: { year: 1948 },
    relationshipType: 'SIBLING' as const,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
]

const mockRelativesWithCousins = [
  {
    id: 'person-cousin-1',
    fullName: 'Emily Smith',
    gender: 'FEMALE' as const,
    birthDate: { year: 1953 },
    relationshipType: 'COUSIN' as const,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
]

const mockRelativesAllTypes = [
  {
    id: 'person-parent',
    fullName: 'Mary Doe',
    gender: 'FEMALE' as const,
    birthDate: { year: 1920 },
    relationshipType: 'PARENT' as const,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'person-spouse',
    fullName: 'Jane Doe',
    gender: 'FEMALE' as const,
    birthDate: { year: 1955 },
    relationshipType: 'SPOUSE' as const,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'person-sibling',
    fullName: 'Sarah Doe',
    gender: 'FEMALE' as const,
    birthDate: { year: 1952 },
    relationshipType: 'SIBLING' as const,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'person-child',
    fullName: 'Jim Doe',
    gender: 'MALE' as const,
    birthDate: { year: 1980 },
    relationshipType: 'CHILD' as const,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'person-cousin',
    fullName: 'Emily Smith',
    gender: 'FEMALE' as const,
    birthDate: { year: 1953 },
    relationshipType: 'COUSIN' as const,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
]

function renderPersonPage(personId: string, queryClient: QueryClient) {
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[`/person/${personId}`]}>
        <Routes>
          <Route path="/person/:personId" element={<PersonPage />} />
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>
  )
}

describe('PersonPage', () => {
  let queryClient: QueryClient

  beforeEach(async () => {
    queryClient = createTestQueryClient()
    vi.clearAllMocks()
    // Default mock for photos - tests that need specific behavior will override
    const { photosApi } = await import('@/api/photos')
    vi.mocked(photosApi.getByPersonId).mockResolvedValue({
      content: [],
      totalElements: 0,
      totalPages: 0,
      size: 20,
      number: 0,
      first: true,
      last: true,
    })
  })

  describe('loading state', () => {
    it('should show loading indicator while fetching person', async () => {
      const { personsApi } = await import('@/api/persons')
      let resolvePromise: (value: unknown) => void
      const promise = new Promise((resolve) => {
        resolvePromise = resolve
      })
      vi.mocked(personsApi.getById).mockReturnValue(promise as Promise<typeof mockPerson>)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])

      renderPersonPage('person-123', queryClient)

      expect(screen.getByText('Loading...')).toBeInTheDocument()

      resolvePromise!(mockPerson)
    })
  })

  describe('error state', () => {
    it('should show error message when person fetch fails', async () => {
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.getById).mockRejectedValue(new Error('Network error'))
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])

      renderPersonPage('person-123', queryClient)

      await screen.findByText('Failed to load person details')
    })
  })

  describe('person details display', () => {
    it('should display person full name', async () => {
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
    })

    it('should display birth date', async () => {
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      // Birth date appears in multiple places - just verify it exists somewhere
      const birthDates = screen.getAllByText(/15\/03\/1950/)
      expect(birthDates.length).toBeGreaterThan(0)
    })

    it('should display death date with approximate indicator', async () => {
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      // Death date with approximate indicator appears in multiple places
      const deathDates = screen.getAllByText(/~25\/12\/2020/)
      expect(deathDates.length).toBeGreaterThan(0)
    })

    it('should display biography', async () => {
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])

      renderPersonPage('person-123', queryClient)

      await screen.findByText('A loving father and grandfather.')
    })

    it('should display birth location', async () => {
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])

      renderPersonPage('person-123', queryClient)

      await screen.findByText('New York, USA')
    })

    it('should display primary photo when available', async () => {
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      const photo = screen.getByRole('img', { name: 'John Doe' })
      expect(photo).toHaveAttribute('src', '/photos/john-doe.jpg')
    })
  })

  describe('relatives section', () => {
    it('should display relatives when available', async () => {
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue(mockRelatives)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      await waitFor(() => {
        expect(screen.getByText('Jane Doe')).toBeInTheDocument()
        expect(screen.getByText('Jim Doe')).toBeInTheDocument()
      })
    })

    it('should show relatives section header', async () => {
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue(mockRelatives)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      await screen.findByText('Family Members')
    })

    it('should display relationship type for each relative', async () => {
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue(mockRelatives)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      await waitFor(() => {
        // Should show relationship type labels
        expect(screen.getByText('Spouse')).toBeInTheDocument()
        expect(screen.getByText('Child')).toBeInTheDocument()
      })
    })

    it('should show Children section when person has children', async () => {
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue(mockRelativesWithChildren)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      await waitFor(() => {
        // Section header (h3)
        expect(screen.getByRole('heading', { name: 'Children' })).toBeInTheDocument()
        expect(screen.getByText('Alice Doe')).toBeInTheDocument()
        expect(screen.getByText('Bob Doe')).toBeInTheDocument()
      })
    })

    it('should show Parents section when person has parents', async () => {
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue(mockRelativesWithChildren)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      await waitFor(() => {
        // Section header (h3)
        expect(screen.getByRole('heading', { name: 'Parents' })).toBeInTheDocument()
        expect(screen.getByText('Mary Doe')).toBeInTheDocument()
      })
    })
  })

  describe('siblings section', () => {
    it('should show Siblings section when person has siblings', async () => {
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue(mockRelativesWithSiblings)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      await waitFor(() => {
        // Section header (h3)
        expect(screen.getByRole('heading', { name: 'Siblings' })).toBeInTheDocument()
        expect(screen.getByText('Sarah Doe')).toBeInTheDocument()
        expect(screen.getByText('Tom Doe')).toBeInTheDocument()
      })
    })

    it('should display Sibling relationship type label', async () => {
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue(mockRelativesWithSiblings)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      await waitFor(() => {
        expect(screen.getAllByText('Sibling').length).toBeGreaterThan(0)
      })
    })
  })

  describe('cousins section', () => {
    it('should show Cousins section when person has cousins', async () => {
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue(mockRelativesWithCousins)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      await waitFor(() => {
        // Section header (h3)
        expect(screen.getByRole('heading', { name: 'Cousins' })).toBeInTheDocument()
        expect(screen.getByText('Emily Smith')).toBeInTheDocument()
      })
    })

    it('should display Cousin relationship type label', async () => {
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue(mockRelativesWithCousins)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      await waitFor(() => {
        expect(screen.getByText('Cousin')).toBeInTheDocument()
      })
    })
  })

  describe('relationship filter controls', () => {
    beforeEach(() => {
      // Reset relationship filters before each test
      useUIStore.getState().resetRelationshipFilters()
    })

    it('should display filter controls when relatives are present', async () => {
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue(mockRelativesAllTypes)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      await waitFor(() => {
        expect(screen.getByRole('checkbox', { name: /parents/i })).toBeInTheDocument()
        expect(screen.getByRole('checkbox', { name: /spouses/i })).toBeInTheDocument()
        expect(screen.getByRole('checkbox', { name: /siblings/i })).toBeInTheDocument()
        expect(screen.getByRole('checkbox', { name: /children/i })).toBeInTheDocument()
        expect(screen.getByRole('checkbox', { name: /cousins/i })).toBeInTheDocument()
      })
    })

    it('should hide relatives when filter is unchecked', async () => {
      const user = userEvent.setup()
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue(mockRelativesAllTypes)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      await waitFor(() => {
        expect(screen.getByText('Sarah Doe')).toBeInTheDocument()
      })

      // Uncheck siblings filter
      const siblingsCheckbox = screen.getByRole('checkbox', { name: /siblings/i })
      await user.click(siblingsCheckbox)

      await waitFor(() => {
        expect(screen.queryByText('Sarah Doe')).not.toBeInTheDocument()
      })
    })

    it('should show relatives when filter is re-checked', async () => {
      const user = userEvent.setup()
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue(mockRelativesAllTypes)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      await waitFor(() => {
        expect(screen.getByText('Sarah Doe')).toBeInTheDocument()
      })

      // Uncheck then re-check siblings filter
      const siblingsCheckbox = screen.getByRole('checkbox', { name: /siblings/i })
      await user.click(siblingsCheckbox)
      await waitFor(() => {
        expect(screen.queryByText('Sarah Doe')).not.toBeInTheDocument()
      })

      await user.click(siblingsCheckbox)
      await waitFor(() => {
        expect(screen.getByText('Sarah Doe')).toBeInTheDocument()
      })
    })
  })

  describe('person without optional fields', () => {
    it('should handle person without death date', async () => {
      const { personsApi } = await import('@/api/persons')
      const livingPerson = {
        ...mockPerson,
        deathDate: undefined,
        locationDeath: undefined,
        locationBurial: undefined,
      }
      vi.mocked(personsApi.getById).mockResolvedValue(livingPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      // Should not crash and should not show death-related info
      expect(screen.queryByText(/Death/i)).not.toBeInTheDocument()
    })

    it('should handle person without photo', async () => {
      const { personsApi } = await import('@/api/persons')
      const personWithoutPhoto = {
        ...mockPerson,
        primaryPhotoUrl: undefined,
      }
      vi.mocked(personsApi.getById).mockResolvedValue(personWithoutPhoto)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      // Should show placeholder or initials instead
      expect(screen.queryByRole('img', { name: 'John Doe' })).not.toBeInTheDocument()
    })
  })

  describe('photos section', () => {
    const mockPhotosPage = {
      content: [
        {
          id: 'photo-1',
          originalUrl: '/photos/originals/test1.jpg',
          thumbnailSmallUrl: '/photos/thumbnails/small/test1.jpg',
          thumbnailMediumUrl: '/photos/thumbnails/medium/test1.jpg',
          processingStatus: 'COMPLETED' as const,
          personIds: ['person-123'],
          createdAt: '2024-01-01T00:00:00Z',
        },
        {
          id: 'photo-2',
          originalUrl: '/photos/originals/test2.jpg',
          thumbnailSmallUrl: '/photos/thumbnails/small/test2.jpg',
          thumbnailMediumUrl: '/photos/thumbnails/medium/test2.jpg',
          processingStatus: 'COMPLETED' as const,
          personIds: ['person-123'],
          createdAt: '2024-01-02T00:00:00Z',
        },
      ],
      totalElements: 2,
      totalPages: 1,
      size: 20,
      number: 0,
      first: true,
      last: true,
    }

    const mockEmptyPhotosPage = {
      content: [],
      totalElements: 0,
      totalPages: 0,
      size: 20,
      number: 0,
      first: true,
      last: true,
    }

    it('should display photos section header', async () => {
      const { personsApi } = await import('@/api/persons')
      const { photosApi } = await import('@/api/photos')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])
      vi.mocked(photosApi.getByPersonId).mockResolvedValue(mockPhotosPage)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      await screen.findByRole('heading', { name: /photos/i })
    })

    it('should display photos for the person', async () => {
      const { personsApi } = await import('@/api/persons')
      const { photosApi } = await import('@/api/photos')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])
      vi.mocked(photosApi.getByPersonId).mockResolvedValue(mockPhotosPage)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      await waitFor(() => {
        const images = screen.getAllByRole('img', { name: /photo/i })
        expect(images.length).toBe(2)
      })
    })

    it('should show empty state when person has no photos', async () => {
      const { personsApi } = await import('@/api/persons')
      const { photosApi } = await import('@/api/photos')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])
      vi.mocked(photosApi.getByPersonId).mockResolvedValue(mockEmptyPhotosPage)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      await screen.findByText(/no photos/i)
    })

    it('should have upload button', async () => {
      const { personsApi } = await import('@/api/persons')
      const { photosApi } = await import('@/api/photos')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])
      vi.mocked(photosApi.getByPersonId).mockResolvedValue(mockEmptyPhotosPage)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      expect(screen.getByRole('button', { name: /add photo/i })).toBeInTheDocument()
    })

    it('should upload photo and link to person', async () => {
      const user = userEvent.setup()
      const { personsApi } = await import('@/api/persons')
      const { photosApi } = await import('@/api/photos')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])
      vi.mocked(photosApi.getByPersonId).mockResolvedValue(mockEmptyPhotosPage)
      vi.mocked(photosApi.upload).mockResolvedValue({
        photoId: 'new-photo-id',
        originalUrl: '/photos/originals/new.jpg',
      })
      vi.mocked(photosApi.linkToPersons).mockResolvedValue(undefined)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')

      const fileInput = screen.getByTestId('person-photo-upload-input')
      const file = new File(['test'], 'test.jpg', { type: 'image/jpeg' })

      await user.upload(fileInput, file)

      await waitFor(() => {
        expect(photosApi.upload).toHaveBeenCalledWith(file)
        expect(photosApi.linkToPersons).toHaveBeenCalledWith('new-photo-id', ['person-123'], 'person-123')
      })
    })

    it('should open lightbox when clicking a photo thumbnail', async () => {
      const user = userEvent.setup()
      const { personsApi } = await import('@/api/persons')
      const { photosApi } = await import('@/api/photos')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])
      vi.mocked(photosApi.getByPersonId).mockResolvedValue(mockPhotosPage)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      await waitFor(() => {
        expect(screen.getAllByRole('img', { name: /photo/i }).length).toBe(2)
      })

      // Click on the first photo thumbnail
      const photos = screen.getAllByRole('img', { name: /photo/i })
      await user.click(photos[0])

      // Lightbox should be open with the full-size image
      await waitFor(() => {
        expect(screen.getByRole('dialog')).toBeInTheDocument()
        // Should show the original URL, not thumbnail
        expect(screen.getByTestId('lightbox-image')).toHaveAttribute('src', '/photos/originals/test1.jpg')
      })
    })

    it('should close lightbox when clicking close button', async () => {
      const user = userEvent.setup()
      const { personsApi } = await import('@/api/persons')
      const { photosApi } = await import('@/api/photos')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])
      vi.mocked(photosApi.getByPersonId).mockResolvedValue(mockPhotosPage)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      await waitFor(() => {
        expect(screen.getAllByRole('img', { name: /photo/i }).length).toBe(2)
      })

      // Click on a photo to open lightbox
      const photos = screen.getAllByRole('img', { name: /photo/i })
      await user.click(photos[0])

      await waitFor(() => {
        expect(screen.getByRole('dialog')).toBeInTheDocument()
      })

      // Click close button
      const closeButton = screen.getByRole('button', { name: /close/i })
      await user.click(closeButton)

      await waitFor(() => {
        expect(screen.queryByRole('dialog')).not.toBeInTheDocument()
      })
    })

    it('should close lightbox when pressing Escape key', async () => {
      const user = userEvent.setup()
      const { personsApi } = await import('@/api/persons')
      const { photosApi } = await import('@/api/photos')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])
      vi.mocked(photosApi.getByPersonId).mockResolvedValue(mockPhotosPage)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      await waitFor(() => {
        expect(screen.getAllByRole('img', { name: /photo/i }).length).toBe(2)
      })

      // Click on a photo to open lightbox
      const photos = screen.getAllByRole('img', { name: /photo/i })
      await user.click(photos[0])

      await waitFor(() => {
        expect(screen.getByRole('dialog')).toBeInTheDocument()
      })

      // Press Escape
      await user.keyboard('{Escape}')

      await waitFor(() => {
        expect(screen.queryByRole('dialog')).not.toBeInTheDocument()
      })
    })
  })

  describe('edit functionality', () => {
    beforeEach(async () => {
      // Reset to non-admin user by default
      mockAuthState.user = { id: 'user-123', username: 'testuser',role: 'USER' as const, status: 'ACTIVE' as const, createdAt: '2024-01-01T00:00:00Z' }
      mockAuthState.isAdmin = false
    })

    it('should show Edit button when user is admin', async () => {
      mockAuthState.isAdmin = true
      mockAuthState.user = { id: 'admin-user', username: 'admin',role: 'ADMIN' as const, status: 'ACTIVE' as const, createdAt: '2024-01-01T00:00:00Z' }

      const { personsApi } = await import('@/api/persons')
      const { treesApi } = await import('@/api/trees')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])
      vi.mocked(treesApi.getById).mockResolvedValue(mockTree)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      expect(screen.getByRole('button', { name: /edit/i })).toBeInTheDocument()
    })

    it('should show Edit button when user is tree owner', async () => {
      mockAuthState.user = { id: 'owner-user-id', username: 'owner',role: 'USER' as const, status: 'ACTIVE' as const, createdAt: '2024-01-01T00:00:00Z' }
      mockAuthState.isAdmin = false

      const { personsApi } = await import('@/api/persons')
      const { treesApi } = await import('@/api/trees')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])
      vi.mocked(treesApi.getById).mockResolvedValue(mockTree)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      // Need to wait for tree query to load
      await waitFor(() => {
        expect(screen.getByRole('button', { name: /edit/i })).toBeInTheDocument()
      })
    })

    it('should NOT show Edit button when user is neither admin nor tree owner', async () => {
      mockAuthState.user = { id: 'random-user', username: 'random',role: 'USER' as const, status: 'ACTIVE' as const, createdAt: '2024-01-01T00:00:00Z' }
      mockAuthState.isAdmin = false

      const { personsApi } = await import('@/api/persons')
      const { treesApi } = await import('@/api/trees')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])
      vi.mocked(treesApi.getById).mockResolvedValue(mockTree)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      // Wait for tree to load to ensure edit button definitely won't appear
      await waitFor(() => {
        expect(screen.queryByRole('button', { name: /edit/i })).not.toBeInTheDocument()
      })
    })

    it('should open edit modal when clicking Edit button', async () => {
      const user = userEvent.setup()
      mockAuthState.isAdmin = true
      mockAuthState.user = { id: 'admin-user', username: 'admin',role: 'ADMIN' as const, status: 'ACTIVE' as const, createdAt: '2024-01-01T00:00:00Z' }

      const { personsApi } = await import('@/api/persons')
      const { treesApi } = await import('@/api/trees')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])
      vi.mocked(treesApi.getById).mockResolvedValue(mockTree)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      const editButton = screen.getByRole('button', { name: /edit/i })
      await user.click(editButton)

      await waitFor(() => {
        expect(screen.getByRole('dialog', { name: /edit person/i })).toBeInTheDocument()
      })
    })

    it('should close edit modal when clicking Cancel', async () => {
      const user = userEvent.setup()
      mockAuthState.isAdmin = true
      mockAuthState.user = { id: 'admin-user', username: 'admin',role: 'ADMIN' as const, status: 'ACTIVE' as const, createdAt: '2024-01-01T00:00:00Z' }

      const { personsApi } = await import('@/api/persons')
      const { treesApi } = await import('@/api/trees')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])
      vi.mocked(treesApi.getById).mockResolvedValue(mockTree)

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      const editButton = screen.getByRole('button', { name: /edit/i })
      await user.click(editButton)

      await waitFor(() => {
        expect(screen.getByRole('dialog', { name: /edit person/i })).toBeInTheDocument()
      })

      const cancelButton = screen.getByRole('button', { name: /cancel/i })
      await user.click(cancelButton)

      await waitFor(() => {
        expect(screen.queryByRole('dialog', { name: /edit person/i })).not.toBeInTheDocument()
      })
    })

    it('should update person when saving', async () => {
      const user = userEvent.setup()
      mockAuthState.isAdmin = true
      mockAuthState.user = { id: 'admin-user', username: 'admin',role: 'ADMIN' as const, status: 'ACTIVE' as const, createdAt: '2024-01-01T00:00:00Z' }

      const { personsApi } = await import('@/api/persons')
      const { treesApi } = await import('@/api/trees')
      vi.mocked(personsApi.getById).mockResolvedValue(mockPerson)
      vi.mocked(personsApi.getRelatives).mockResolvedValue([])
      vi.mocked(treesApi.getById).mockResolvedValue(mockTree)
      vi.mocked(personsApi.update).mockResolvedValue({ ...mockPerson, fullName: 'John Updated Doe' })

      renderPersonPage('person-123', queryClient)

      await screen.findByText('John Doe')
      const editButton = screen.getByRole('button', { name: /edit/i })
      await user.click(editButton)

      await waitFor(() => {
        expect(screen.getByRole('dialog', { name: /edit person/i })).toBeInTheDocument()
      })

      // Change the name
      const nameInput = screen.getByLabelText(/full name/i)
      await user.clear(nameInput)
      await user.type(nameInput, 'John Updated Doe')

      const saveButton = screen.getByRole('button', { name: /save/i })
      await user.click(saveButton)

      await waitFor(() => {
        expect(personsApi.update).toHaveBeenCalledWith('person-123', expect.objectContaining({
          fullName: 'John Updated Doe',
        }))
      })
    })
  })
})
