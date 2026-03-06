import { render, screen, waitFor, fireEvent } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import PersonCreatePage from '@/pages/PersonCreatePage'

// Mock the persons API
vi.mock('@/api/persons', () => ({
  personsApi: {
    create: vi.fn(),
  },
}))

// Mock the trees API
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

// Mock navigation
const mockNavigate = vi.fn()
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom')
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  }
})

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

// Mock tree data
const mockTree = {
  treeId: 'tree-123',
  treeName: 'Doe Family Tree',
  createdBy: 'user-123',
  persons: [],
  relationships: [],
}

// Helper to fill form with minimal required data
async function fillFormWithRequiredData(user: ReturnType<typeof userEvent.setup>) {
  await user.type(screen.getByLabelText(/full name/i), 'John Doe')
  await user.type(screen.getByLabelText(/^year \*/i), '1950')
  await user.type(screen.getAllByLabelText(/^month$/i)[0], '3')
  await user.type(screen.getAllByLabelText(/^day$/i)[0], '15')
}

function renderPersonCreatePage(treeId: string, queryClient: QueryClient) {
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[`/tree/${treeId}/person/new`]}>
        <Routes>
          <Route path="/tree/:treeId/person/new" element={<PersonCreatePage />} />
          <Route path="/person/:personId" element={<div>Person Page</div>} />
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>
  )
}

describe('PersonCreatePage', () => {
  let queryClient: QueryClient

  beforeEach(async () => {
    queryClient = createTestQueryClient()
    vi.clearAllMocks()
    mockNavigate.mockClear()

    // Default mock for tree - provides tree name context
    const { treesApi } = await import('@/api/trees')
    vi.mocked(treesApi.getById).mockResolvedValue(mockTree)
  })

  describe('rendering', () => {
    it('should display the page title', async () => {
      renderPersonCreatePage('tree-123', queryClient)

      await screen.findByRole('heading', { name: /add new person/i })
    })

    it('should display the tree name in subtitle', async () => {
      renderPersonCreatePage('tree-123', queryClient)

      await screen.findByText(/doe family tree/i)
    })

    it('should display required form fields', async () => {
      renderPersonCreatePage('tree-123', queryClient)

      await screen.findByLabelText(/full name/i)
      // Birth year has "Year *" label (required)
      await screen.findByLabelText(/^year \*/i)
    })

    it('should display optional form fields', async () => {
      renderPersonCreatePage('tree-123', queryClient)

      await screen.findByLabelText(/gender/i)
      await screen.findByLabelText(/biography/i)
      await screen.findByLabelText(/birth place/i)
    })

    it('should display submit and cancel buttons', async () => {
      renderPersonCreatePage('tree-123', queryClient)

      await screen.findByRole('button', { name: /create person/i })
      await screen.findByRole('button', { name: /cancel/i })
    })
  })

  describe('form validation', () => {
    it('should show error when submitting empty form', async () => {
      const user = userEvent.setup()
      renderPersonCreatePage('tree-123', queryClient)

      await screen.findByRole('heading', { name: /add new person/i })

      const submitButton = screen.getByRole('button', { name: /create person/i })
      await user.click(submitButton)

      await screen.findByText(/full name is required/i)
    })

    it('should show error when birth year is missing', async () => {
      const user = userEvent.setup()
      renderPersonCreatePage('tree-123', queryClient)

      await screen.findByRole('heading', { name: /add new person/i })

      await user.type(screen.getByLabelText(/full name/i), 'John Doe')

      const submitButton = screen.getByRole('button', { name: /create person/i })
      await user.click(submitButton)

      await screen.findByText(/birth year is required/i)
    })
  })

  describe('form submission', () => {
    it('should call API with form data including treeId', async () => {
      const user = userEvent.setup()
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.create).mockResolvedValue({
        id: 'new-person-id',
        fullName: 'Jane Doe',
        birthDate: { year: 1960, month: 6, day: 15 },
        gender: 'FEMALE',
        treeId: 'tree-123',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      })

      renderPersonCreatePage('tree-123', queryClient)

      await screen.findByRole('heading', { name: /add new person/i })

      // Fill in the form
      await user.type(screen.getByLabelText(/full name/i), 'Jane Doe')
      await user.type(screen.getByLabelText(/^year \*/i), '1960')
      await user.type(screen.getAllByLabelText(/^month$/i)[0], '6')
      await user.type(screen.getAllByLabelText(/^day$/i)[0], '15')
      await user.selectOptions(screen.getByLabelText(/gender/i), 'FEMALE')

      const submitButton = screen.getByRole('button', { name: /create person/i })
      await user.click(submitButton)

      await waitFor(() => {
        expect(personsApi.create).toHaveBeenCalledWith(
          expect.objectContaining({
            fullName: 'Jane Doe',
            birthDate: expect.objectContaining({
              year: 1960,
              month: 6,
              day: 15,
            }),
            gender: 'FEMALE',
            treeId: 'tree-123',
          })
        )
      })
    })

    it('should navigate to person page on success', async () => {
      const user = userEvent.setup()
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.create).mockResolvedValue({
        id: 'new-person-id',
        fullName: 'John Doe',
        birthDate: { year: 1950, month: 3, day: 15 },
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      })

      renderPersonCreatePage('tree-123', queryClient)

      await screen.findByRole('heading', { name: /add new person/i })

      await fillFormWithRequiredData(user)

      const submitButton = screen.getByRole('button', { name: /create person/i })
      await user.click(submitButton)

      await waitFor(() => {
        expect(mockNavigate).toHaveBeenCalledWith('/person/new-person-id')
      })
    })

    it('should show error message on API failure', async () => {
      const user = userEvent.setup()
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.create).mockRejectedValue(new Error('Network error'))

      renderPersonCreatePage('tree-123', queryClient)

      await screen.findByRole('heading', { name: /add new person/i })

      await fillFormWithRequiredData(user)

      const submitButton = screen.getByRole('button', { name: /create person/i })
      await user.click(submitButton)

      await screen.findByText(/failed to create person/i)
    })
  })

  describe('optional fields', () => {
    it('should include biography when provided', async () => {
      const user = userEvent.setup()
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.create).mockResolvedValue({
        id: 'new-person-id',
        fullName: 'John Doe',
        birthDate: { year: 1950, month: 3, day: 15 },
        biography: 'A great person',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      })

      renderPersonCreatePage('tree-123', queryClient)

      await screen.findByRole('heading', { name: /add new person/i })

      await fillFormWithRequiredData(user)
      await user.type(screen.getByLabelText(/biography/i), 'A great person')

      const submitButton = screen.getByRole('button', { name: /create person/i })
      await user.click(submitButton)

      await waitFor(() => {
        expect(personsApi.create).toHaveBeenCalledWith(
          expect.objectContaining({
            biography: 'A great person',
          })
        )
      })
    })

    it('should include birth location when provided', async () => {
      const user = userEvent.setup()
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.create).mockResolvedValue({
        id: 'new-person-id',
        fullName: 'John Doe',
        birthDate: { year: 1950, month: 3, day: 15 },
        locationBirth: 'New York, USA',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      })

      renderPersonCreatePage('tree-123', queryClient)

      await screen.findByRole('heading', { name: /add new person/i })

      await fillFormWithRequiredData(user)
      await user.type(screen.getByLabelText(/birth place/i), 'New York, USA')

      const submitButton = screen.getByRole('button', { name: /create person/i })
      await user.click(submitButton)

      await waitFor(() => {
        expect(personsApi.create).toHaveBeenCalledWith(
          expect.objectContaining({
            locationBirth: 'New York, USA',
          })
        )
      })
    })

    it('should include approximate date flag when checked', async () => {
      const user = userEvent.setup()
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.create).mockResolvedValue({
        id: 'new-person-id',
        fullName: 'John Doe',
        birthDate: { year: 1950, month: 3, day: 15, isApproximate: true },
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      })

      renderPersonCreatePage('tree-123', queryClient)

      await screen.findByRole('heading', { name: /add new person/i })

      await fillFormWithRequiredData(user)

      // First approximate checkbox is for birth date
      const approximateCheckboxes = screen.getAllByRole('checkbox', { name: /approximate date/i })
      fireEvent.click(approximateCheckboxes[0])

      const submitButton = screen.getByRole('button', { name: /create person/i })
      await user.click(submitButton)

      await waitFor(() => {
        expect(personsApi.create).toHaveBeenCalledWith(
          expect.objectContaining({
            birthDate: expect.objectContaining({
              isApproximate: true,
            }),
          })
        )
      })
    })
  })

  describe('alive person (no death date)', () => {
    it('should submit successfully without a death date', async () => {
      const user = userEvent.setup()
      const { personsApi } = await import('@/api/persons')
      vi.mocked(personsApi.create).mockResolvedValue({
        id: 'alive-person-id',
        fullName: 'Alive Person',
        birthDate: { year: 2000, month: 1, day: 1 },
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      })

      renderPersonCreatePage('tree-123', queryClient)

      await screen.findByRole('heading', { name: /add new person/i })

      // Fill required fields (name + birth date + gender), leave death date completely empty
      await user.type(screen.getByLabelText(/full name/i), 'Alive Person')
      await user.type(screen.getByLabelText(/^year \*/i), '2000')
      await user.type(screen.getAllByLabelText(/^month$/i)[0], '1')
      await user.type(screen.getAllByLabelText(/^day$/i)[0], '1')
      await user.selectOptions(screen.getByLabelText(/gender/i), 'MALE')

      const submitButton = screen.getByRole('button', { name: /create person/i })
      await user.click(submitButton)

      await waitFor(() => {
        expect(personsApi.create).toHaveBeenCalledWith(
          expect.objectContaining({
            fullName: 'Alive Person',
            birthDate: expect.objectContaining({ year: 2000 }),
          })
        )
      })

      // Verify deathDate is not sent
      const callArgs = vi.mocked(personsApi.create).mock.calls[0][0]
      expect(callArgs.deathDate).toBeUndefined()
    })
  })

  describe('cancel action', () => {
    it('should navigate back to tree page when clicking cancel', async () => {
      const user = userEvent.setup()
      renderPersonCreatePage('tree-123', queryClient)

      await screen.findByRole('heading', { name: /add new person/i })

      const cancelButton = screen.getByRole('button', { name: /cancel/i })
      await user.click(cancelButton)

      expect(mockNavigate).toHaveBeenCalledWith('/tree/tree-123')
    })
  })
})
