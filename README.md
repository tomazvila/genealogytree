# Geneinator - Genealogy Tree Application

A family tree management application built with Spring Boot 4.0.1 and React 19.

## Quick Start (Development)

### Prerequisites
- **Nix** (recommended) or manually install Java 21, Node.js 22+
- **Docker** (for PostgreSQL and RabbitMQ)

### 1. Start Infrastructure

```bash
# Start PostgreSQL and RabbitMQ
docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# Verify they're running
docker compose ps
```

### 2. Start Backend

```bash
cd backend

# Using Nix (recommended)
nix develop --command ./gradlew bootRun

# The backend runs at http://localhost:8080
```

### 3. Start Frontend

```bash
cd frontend

# Install dependencies (first time only)
nix develop --command npm install

# Start dev server
nix develop --command npm run dev

# The frontend runs at http://localhost:3000
```

### 4. Open in Browser

Go to: **http://localhost:3000**

---

## Browser Testing Checklist

### Authentication Flow

#### Test 1: Registration
1. Go to http://localhost:3000/register
2. Fill in the form:
   - Email: `test@example.com`
   - Password: `SecurePass123!`
   - Display Name: `Test User`
3. Click "Register"
4. **Expected**: Redirected to login page with success message

#### Test 2: Login
1. Go to http://localhost:3000/login
2. Enter the credentials you just registered
3. Click "Sign in"
4. **Expected**: Redirected to Dashboard, see "Welcome back, Test User!"

#### Test 3: Protected Routes
1. Open a new incognito window
2. Go to http://localhost:3000/dashboard
3. **Expected**: Redirected to login page

#### Test 4: Logout
1. While logged in, click "Logout" button in header
2. **Expected**: Redirected to home page, session cleared

#### Test 5: Invalid Login
1. Go to http://localhost:3000/login
2. Enter wrong password
3. **Expected**: Error message "Invalid credentials"

#### Test 6: Duplicate Registration
1. Go to http://localhost:3000/register
2. Try to register with same email again
3. **Expected**: Error message "Email already exists"

#### Test 7: Form Validation
1. Go to http://localhost:3000/register
2. Click "Register" without filling anything
3. **Expected**: Validation errors for email and password

---

### Person Management

#### Test 8: Create Person
1. Login and navigate to a tree
2. Click "Add Person" button
3. Fill in:
   - Full Name: `John Doe`
   - Birth Year: `1950`
   - Gender: `Male`
   - Location of Birth: `New York`
4. Click "Create"
5. **Expected**: Person created and appears in tree

#### Test 9: Person Merge
1. Create two duplicate persons (e.g., same person entered twice)
2. Go to person details page
3. Click "Merge with another person"
4. Select the duplicate person
5. **Expected**:
   - If you own one of the persons: Merge succeeds
   - If you don't own either: Error "Not authorized to merge"

#### Test 10: Search Descendants
1. Go to search page
2. Select a person who has children
3. Click "Find Descendants"
4. **Expected**: List of all descendants (children, grandchildren, etc.)

#### Test 11: Search Ancestors
1. Go to search page
2. Select a person who has parents in the tree
3. Click "Find Ancestors"
4. **Expected**: List of all ancestors (parents, grandparents, etc.)

---

### Dashboard

#### Test 12: Dashboard Display
1. Login with valid credentials
2. **Expected**: See Dashboard with:
   - Welcome message with your name
   - Quick Actions section
   - Statistics section
   - Recent Activity section

#### Test 13: Navigation
1. From Dashboard, click "View Family Tree"
2. **Expected**: Navigate to /tree page

---

### Family Tree

#### Test 14: Tree Selection
1. Login and go to http://localhost:3000/tree
2. **Expected**: See tree selection page with:
   - "Please select a family tree" message
   - List of available trees (or "No trees found" message)

#### Test 15: Tree Visualization
1. If trees exist, click on a tree card
2. **Expected**: See React Flow tree visualization with:
   - Person nodes (color-coded by gender)
   - Relationship edges
   - Controls (zoom in/out)
   - Minimap

#### Test 16: Tree View Toggle
1. On the tree page, find the view toggle (Graph/List buttons)
2. Click "List" view
3. **Expected**: Tree displays as a table with columns for Name, Birth, Death, etc.
4. Click "Graph" view
5. **Expected**: Returns to React Flow visualization

#### Test 17: Tree Merge
1. Have two trees (create if needed)
2. Go to source tree settings
3. Click "Merge into another tree"
4. Select target tree
5. **Expected**: All persons moved to target tree, source tree deleted

---

### Photo Upload

#### Test 18: Upload Photo
1. Login and go to Photos page
2. Click "Upload Photo"
3. Select an image file (JPG, PNG)
4. **Expected**: Photo uploads and thumbnails are generated

#### Test 19: Link Photo to Person
1. After uploading, click on a photo
2. Click "Link to Person"
3. Select a person from the list
4. **Expected**: Photo linked and appears on person's profile

---

### Error Handling

#### Test 20: Error Boundary
1. (Development only) Trigger a JavaScript error in a component
2. **Expected**: Error boundary catches it, shows "Something went wrong" with retry button

#### Test 21: API Error Display
1. Turn off the backend server
2. Try to load a page that fetches data
3. **Expected**: Graceful error message, not a blank screen

---

## Running Automated Tests

### Backend Tests (171 tests)

```bash
cd backend
nix develop --command ./gradlew test

# View report
open build/reports/tests/test/index.html
```

### Frontend Unit Tests

```bash
cd frontend
nix develop --command npm test
```

### E2E Tests (Playwright)

```bash
cd frontend

# Run all E2E tests (headless)
nix develop --command npm run test:e2e

# Run with browser visible
nix develop --command npm run test:e2e:headed

# Run with Playwright UI
nix develop --command npm run test:e2e:ui
```

**Note**: E2E tests require both backend and frontend to be running.

---

## Project Status

### Working Features (Browser Testable)
- [x] User Registration
- [x] User Login/Logout
- [x] JWT Authentication
- [x] Protected Routes
- [x] Dashboard
- [x] Family Tree Visualization (React Flow)
- [x] Tree Selection
- [x] Admin Panel (users, audit logs)

### Backend Controllers (with Tests)
- [x] AuthController - register, login, logout, token refresh
- [x] PersonController - CRUD, search, relatives, photos, events
- [x] TreeController - CRUD, structure, merge
- [x] RelationshipController - create, delete, findByPerson, areRelated
- [x] AdminController - users management, audit logs, stats

### Backend Services (Complete with Tests)
- [x] AuthService - registration, login, token refresh
- [x] PersonService - CRUD, search, relatives
- [x] TreeService - create, merge, structure
- [x] RelationshipService - create, delete, BFS pathfinding
- [x] EventService - CRUD with participants
- [x] PhotoService - upload, link, thumbnails
- [x] VisibilityService - permissions, distance calculation

### Pending (Not Yet in Browser)
- [ ] Person management pages (create/edit persons)
- [ ] Photo gallery
- [ ] Event management

---

## Architecture

```
Frontend (React)          Backend (Spring Boot)       Database
localhost:3000     --->   localhost:8080       --->   PostgreSQL:5432
                                |
                                v
                          RabbitMQ:5672
                                |
                                v
                          Image Worker
```

---

## Troubleshooting

### Backend won't start
```bash
# Check PostgreSQL
docker compose logs postgres

# Restart if needed
docker compose restart postgres
```

### Frontend can't connect to backend
- Check backend is running on port 8080
- Check vite proxy config in `frontend/vite.config.ts`

### Login doesn't work
- Check browser console for errors
- Check backend logs for authentication errors
- Verify user is in database: `docker exec -it geneinator-postgres psql -U geneinator -d geneinator -c "SELECT email, status FROM users;"`

---

## Development Commands

```bash
# Backend
cd backend
nix develop --command ./gradlew bootRun      # Start server
nix develop --command ./gradlew test         # Run tests

# Frontend
cd frontend
nix develop --command npm run dev            # Start dev server
nix develop --command npm test               # Unit tests
nix develop --command npm run test:e2e       # E2E tests
nix develop --command npm run build          # Production build

# Infrastructure
docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d   # Start
docker compose down                                                      # Stop
docker compose logs -f postgres                                          # View logs
```
