# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Feature Implementation

**Before implementing any new feature, read `REQUIREMENTS.md` for specifications:**

- Data model definitions (entities, fields, relationships)
- API endpoint specifications
- Visibility and privacy rules
- User roles and permissions
- Security requirements

When implementing a feature:
1. Find the feature in `REQUIREMENTS.md` and understand its full specification
2. Follow the data model definitions for entity structure
3. Use the API design patterns specified
4. Implement visibility/privacy rules as documented
5. Mark the feature checkbox in `REQUIREMENTS.md` when complete

## Codemaps

**Before making changes, read the relevant codemap(s) in `.codemaps/` to understand the architecture:**

- `.codemaps/overview.md` - System architecture, component interactions, request flows
- `.codemaps/backend.md` - Backend layers, API endpoints, services, entity relationships
- `.codemaps/frontend.md` - Component tree, state management, API client patterns
- `.codemaps/image-worker.md` - Async processing pipeline, RabbitMQ configuration

When working on a feature or bug:
1. Read `overview.md` first for context
2. Read the component-specific codemap (backend/frontend/image-worker)
3. Follow the patterns and conventions documented there

## Test-Driven Development (Required)

**All changes must follow TDD:**
1. Write a test that fails (verifies the bug exists or the feature is missing)
2. Run the test to confirm it fails
3. Scafold feature by implementing mocks, stubs first
4. Implement the minimum code to make the test pass
5. Run the test to confirm it passes
6. Refactor if needed (keeping tests green)

Do not implement code without a corresponding failing test first.

## Frontend Build Verification (Required)

**After any frontend changes, always run the production build to verify it compiles:**
```bash
cd frontend
nix develop --command npm run build
```

The dev server is more lenient than the production build. TypeScript errors, unused imports, and other issues may not surface during development but will fail the production build. Always verify your changes build successfully before considering the task complete.

## Project Overview

Geneinator is a genealogy tree management application with three main components:
- **Backend**: Spring Boot 4.0.1 (Java 21) REST API on port 8080
- **Frontend**: React 19 + Vite + TypeScript SPA on port 3000
- **Image Worker**: Async service for photo processing via RabbitMQ

## Development Commands

**All frontend and backend commands must be run through `nix develop --command`.** There are no global dependencies installed - Java, Node, npm, Gradle, etc. are only available inside the Nix shell.

### Infrastructure (PostgreSQL + RabbitMQ)
```bash
docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d
docker compose down  # Stop services
```

### Backend (Spring Boot)
```bash
cd backend
nix develop --command ./gradlew bootRun        # Start API server
nix develop --command ./gradlew test           # Run tests (171 tests)
nix develop --command ./gradlew test jacocoTestReport  # Tests + coverage
```

### Frontend (React/Vite)
```bash
cd frontend
nix develop --command npm install              # First time setup
nix develop --command npm run dev              # Dev server
nix develop --command npm test                 # Unit tests (Vitest)
nix develop --command npm run test:coverage    # Coverage report
nix develop --command npm run lint             # ESLint
nix develop --command npm run build            # Production build
```

### E2E Tests (Playwright)
```bash
cd frontend
nix develop --command npm run test:e2e         # Headless
nix develop --command npm run test:e2e:headed  # Browser visible
```

### Full Stack Start Order
1. `docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d`
2. Terminal 1: `cd backend && nix develop --command ./gradlew bootRun`
3. Terminal 2: `cd frontend && nix develop --command npm run dev`
4. Open http://localhost:3000

## Architecture

```
Browser (React)     Backend API        Image Worker
localhost:3000  →   localhost:8080  →  RabbitMQ Consumer
                         │                    │
                    PostgreSQL           File Storage
                    port 5432         /data/geneinator/
```

### Backend Structure (Spring layered architecture)
- `controller/` - REST endpoints (8 controllers: Auth, Person, Tree, Relationship, Photo, Event, Search, Admin)
- `service/` - Business logic (interface + impl pattern)
- `repository/` - JPA data access (Spring Data)
- `entity/` - Domain models (User, Person, Tree, Relationship, Photo, Event, etc.)
- `dto/` - Request/Response objects organized by domain
- `security/` - JWT authentication (24h access, 7d refresh tokens)
- `messaging/` - RabbitMQ configuration

### Frontend Structure
- `pages/` - Route components (Welcome, Login, Register, Dashboard, Tree, Person, Photos, Admin)
- `components/` - Reusable UI (Header, Layout, ProtectedRoute, shadcn/ui components)
- `api/` - HTTP client modules (auth, persons, trees, photos, admin)
- `store/` - Zustand stores (authStore with localStorage persistence, uiStore)
- `hooks/` - Custom React hooks
- `types/` - TypeScript definitions

### Key Patterns
- **Auth**: JWT tokens stored in localStorage, injected via fetch wrapper
- **Server state**: TanStack React Query (5-min stale time, auto-retry)
- **Forms**: React Hook Form + Zod validation
- **Tree visualization**: React Flow library
- **Photo processing**: Async via RabbitMQ (thumbnails: small/medium/large, EXIF extraction)

## Testing

Backend uses JUnit 5 + Mockito + Testcontainers. Frontend uses Vitest + Testing Library + Playwright.

Run single backend test class:
```bash
cd backend
nix develop --command ./gradlew test --tests "PersonServiceTest"
```

Run single frontend test file:
```bash
cd frontend
nix develop --command npm test -- useAuth.test.ts
```

## Environment

Uses Nix flakes for reproducible dev environment (Java 21, Node 22, PostgreSQL 16, ImageMagick). The `flake.nix` is located at the project root level (not in frontend or backend directories).

Required env vars:
- `DB_PASSWORD` - PostgreSQL password
- `JWT_SECRET` - 256+ bit Base64 key (auto-generated in nix shell if unset)

Spring profiles: `dev` (default), `prod`, `test`

## Key Entities

- **User** - System users with roles (USER, ADMIN)
- **Tree** - Family tree containers (users can have multiple)
- **Person** - Individuals in a tree
- **Relationship** - Links between persons (parent, spouse, sibling)
- **Photo** - Images with processing status (PENDING, PROCESSING, COMPLETED, FAILED)
- **Event** - Family events with participant associations
- **ApproximateDate** - Handles uncertain historical dates (genealogy-specific)
