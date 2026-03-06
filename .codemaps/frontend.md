# Frontend Codemap

## Directory Structure

```
frontend/src/
├── main.tsx                           # App entry point, React Query provider
├── App.tsx                            # Router configuration
├── index.css                          # Global styles
├── pages/                             # Route components
│   ├── WelcomePage.tsx               # Public landing (/)
│   ├── LoginPage.tsx                 # Auth (/login)
│   ├── RegisterPage.tsx              # Registration (/register)
│   ├── DashboardPage.tsx             # Main dashboard (/dashboard)
│   ├── TreePage.tsx                  # Tree visualization (/tree/:treeId)
│   ├── PersonPage.tsx                # Person details (/person/:personId)
│   ├── PersonCreatePage.tsx          # Person creation (/tree/:treeId/person/new)
│   ├── AdminPage.tsx                 # Admin panel (/admin)
│   └── NotFoundPage.tsx              # 404 page
├── components/                        # Reusable components
│   ├── Layout.tsx                    # Main app layout wrapper
│   ├── Header.tsx                    # Navigation header (logout, theme, language)
│   ├── Sidebar.tsx                   # Navigation sidebar
│   ├── ProtectedRoute.tsx            # Auth guard HOC
│   ├── ErrorBoundary.tsx             # Error catching
│   ├── PersonEditModal.tsx           # Modal for editing person details
│   ├── RelationshipCreateModal.tsx   # Modal for creating relationships
│   ├── RelationshipFilterControls.tsx # UI for filtering relationship types
│   └── ui/                           # shadcn/ui components
│       ├── button.tsx
│       ├── input.tsx
│       ├── label.tsx
│       ├── checkbox.tsx
│       └── toaster.tsx
├── api/                               # HTTP client layer
│   ├── client.ts                     # Base fetch wrapper with JWT + token refresh
│   ├── auth.ts                       # Auth endpoints
│   ├── persons.ts                    # Person CRUD
│   ├── trees.ts                      # Tree operations
│   ├── relationships.ts              # Relationship CRUD
│   ├── photos.ts                     # Photo upload/fetch
│   └── admin.ts                      # Admin operations
├── store/                             # State management
│   ├── authStore.ts                  # Auth state (Zustand + localStorage)
│   └── uiStore.ts                    # UI state (sidebar, language, theme, relationship filters)
├── hooks/                             # Custom React hooks
├── types/                             # TypeScript definitions
│   └── index.ts                      # Shared types (User, Person, Relationship, Photo, Tree, Event)
├── utils/                             # Utility functions
│   └── treeLayout.ts                 # React Flow layout using dagre algorithm
├── lib/                               # Library utilities
│   └── utils.ts                      # Helper functions (cn, etc.)
├── i18n/                              # Internationalization
│   ├── index.ts                      # i18next setup
│   ├── en.json                       # English translations
│   └── lt.json                       # Lithuanian translations
├── test/
│   └── setup.ts                      # Vitest setup
└── __tests__/                         # Unit tests
    ├── components/
    │   ├── WelcomePage.test.tsx
    │   ├── TreePage.test.tsx
    │   ├── Sidebar.test.tsx
    │   ├── PersonPage.test.tsx
    │   └── RelationshipCreateModal.test.tsx
    ├── pages/
    │   └── PersonCreatePage.test.tsx
    ├── hooks/
    │   └── useAuth.test.ts
    └── utils/
        └── treeLayout.test.ts
```

Note: An empty `features/` directory exists with subdirectories (admin, auth, persons, photos, tree) for planned feature-based organization, but is not currently in use.

## Component Tree

```
<BrowserRouter>
  <QueryClientProvider>
    <ErrorBoundary>
      <Routes>
        │
        ├── "/" ─────────────────── <WelcomePage />
        ├── "/login" ────────────── <LoginPage />
        ├── "/register" ─────────── <RegisterPage />
        │
        └── <ProtectedRoute> ────── Requires authentication
              │
              └── <Layout>
                    ├── <Header />
                    ├── <Sidebar />
                    └── <Outlet />
                          │
                          ├── "/dashboard" ──────────── <DashboardPage />
                          ├── "/tree/:treeId" ────────── <TreePage />
                          │     ├── <RelationshipFilterControls />
                          │     ├── <PersonEditModal />
                          │     └── <RelationshipCreateModal />
                          ├── "/tree/:treeId/person/new" ─ <PersonCreatePage />
                          ├── "/person/:personId" ──────── <PersonPage />
                          │     ├── <PersonEditModal />
                          │     └── <RelationshipCreateModal />
                          ├── "/admin" ──────────────── <AdminPage />
                          └── "*" ───────────────────── <NotFoundPage />
      </Routes>
    </ErrorBoundary>
  </QueryClientProvider>
</BrowserRouter>
```

## Data Flow

```
┌─────────────────────────────────────────────────────────────────────┐
│                         React Component                              │
│                                                                      │
│  ┌─────────────┐    ┌─────────────────┐    ┌─────────────────────┐ │
│  │ useQuery()  │    │ useMutation()   │    │ Zustand Store       │ │
│  │ (read)      │    │ (write)         │    │ (client state)      │ │
│  └──────┬──────┘    └────────┬────────┘    └──────────┬──────────┘ │
│         │                    │                        │             │
└─────────┼────────────────────┼────────────────────────┼─────────────┘
          │                    │                        │
          ▼                    ▼                        │
┌─────────────────────────────────────────────┐        │
│              API Client (api/*.ts)           │        │
│  - Adds Authorization header from authStore │◄───────┘
│  - Auto-refreshes tokens on 401 responses   │
│  - Handles request/response                 │
└──────────────────────┬──────────────────────┘
                       │
                       ▼
              Backend API :8080
```

## State Management

### authStore.ts (Zustand + persist)
```typescript
{
  user: User | null,
  accessToken: string | null,
  refreshToken: string | null,
  isAuthenticated: boolean,
  isAdmin: boolean,

  // Actions
  setAuth(tokens, user),
  clearAuth(),
  updateTokens(access, refresh)
}
```
Persisted to localStorage - survives page refresh.

### uiStore.ts (Zustand)
```typescript
{
  toasts: Toast[],
  sidebarOpen: boolean,
  theme: 'light' | 'dark',
  language: string,
  relationshipFilters: Record<RelationshipFilterType, boolean>,

  // Actions
  addToast(message, type),
  removeToast(id),
  toggleSidebar(),
  setRelationshipFilter(type, enabled),
  resetRelationshipFilters()
}
```

Relationship filter types: PARENT, SPOUSE, SIBLING, CHILD, COUSIN

### Server State (TanStack Query)
- Automatic caching (5 min stale time)
- Background refetching
- Optimistic updates
- Error retry (1 attempt)

## API Client Pattern

```typescript
// api/client.ts - Base configuration with token refresh
const apiClient = {
  get: (url) => fetch(url, { headers: authHeaders() }),
  post: (url, data) => fetch(url, { method: 'POST', body: JSON.stringify(data), headers: authHeaders() }),
  // Automatically retries with refreshed token on 401
}

// api/persons.ts - Domain-specific
export const personsApi = {
  getAll: () => apiClient.get('/api/persons'),
  getById: (id) => apiClient.get(`/api/persons/${id}`),
  create: (data) => apiClient.post('/api/persons', data),
  // ...
}

// api/relationships.ts - Relationship operations
export const relationshipsApi = {
  create: (data) => apiClient.post('/api/relationships', data),
  delete: (id) => apiClient.delete(`/api/relationships/${id}`),
  // ...
}
```

## Key Components

### ProtectedRoute.tsx
```
Checks authStore.isAuthenticated
  │
  ├── True ──► Render <Outlet /> (child routes)
  │
  └── False ─► Redirect to /login
```

### Layout.tsx
```
┌─────────────────────────────────────────────┐
│ Header                                       │
├─────────┬───────────────────────────────────┤
│         │                                    │
│ Sidebar │   <Outlet /> (page content)       │
│         │                                    │
│         │                                    │
└─────────┴───────────────────────────────────┘
```

### TreePage.tsx (@xyflow/react + dagre)
- Fetches tree structure from API
- Uses `utils/treeLayout.ts` with dagre for hierarchical node positioning
- Converts to React Flow nodes/edges
- Supports relationship filtering via RelationshipFilterControls
- Handles node click navigation
- Supports zoom/pan
- Opens PersonEditModal and RelationshipCreateModal

### PersonCreatePage.tsx
- Dedicated page for adding a new person to a tree
- Form with React Hook Form + Zod validation
- Navigated to from TreePage via `/tree/:treeId/person/new`

### PersonEditModal.tsx
- Modal for editing existing person details
- Used within TreePage and PersonPage

### RelationshipCreateModal.tsx
- Modal for creating relationships between persons
- Supports relationship types: PARENT, SPOUSE, SIBLING

## Key Dependencies

| Package | Version | Purpose |
|---------|---------|---------|
| react | 19.0.0 | UI framework |
| react-router | 7.0.0 | Client-side routing |
| @tanstack/react-query | 5.62.0 | Server state management |
| zustand | 5.0.0 | Client state management |
| react-hook-form | 7.53.2 | Form handling |
| zod | 3.23.8 | Schema validation |
| @xyflow/react | 12.3.0 | Tree visualization |
| @dagrejs/dagre | 1.1.8 | Graph layout algorithm |
| i18next | 24.0.0 | Internationalization |
| vite | 6.0.1 | Build tool |
| vitest | 2.1.5 | Unit testing |
| playwright | 1.57.0 | E2E testing |

## Testing

```bash
# Unit tests (Vitest)
npm test                    # Watch mode
npm run test:coverage       # Coverage report

# E2E tests (Playwright)
npm run test:e2e            # Headless
npm run test:e2e:headed     # Browser visible

# Run single test file
npm test -- useAuth.test.ts
```

## Key Patterns

### Form Handling
```typescript
// React Hook Form + Zod
const schema = z.object({
  email: z.string().email(),
  password: z.string().min(8)
});

const { register, handleSubmit, formState: { errors } } = useForm({
  resolver: zodResolver(schema)
});
```

### API Queries
```typescript
// TanStack Query
const { data, isLoading, error } = useQuery({
  queryKey: ['persons', id],
  queryFn: () => personsApi.getById(id)
});

const mutation = useMutation({
  mutationFn: personsApi.create,
  onSuccess: () => queryClient.invalidateQueries(['persons'])
});
```
