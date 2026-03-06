# Geneinator - Genealogy Tree Application

## Overview

A web application for managing and visualizing family genealogy trees. Built for families who want to preserve their heritage with photos, stories, and detailed family connections.

---

## Technical Stack

| Layer | Technology | Version |
|-------|------------|---------|
| Language | Java (LTS) | 21.0.8+ |
| Build Tool | Gradle | 9.2.1 |
| Backend Framework | Spring Boot | 4.0.1 |
| ORM | Hibernate ORM | 7.0+ (managed by Spring Boot) |
| Database | PostgreSQL | 16.x |
| Message Queue | RabbitMQ | 4.2.x |
| Spring AMQP | Spring AMQP | 4.0.x |
| Frontend | React | 19.2.x |
| UI Components | shadcn/ui + Radix UI | Latest |
| CSS | Tailwind CSS | 3.x |
| State (Server) | TanStack Query | 5.x |
| State (Client) | Zustand | 5.x |
| Tree Visualization | React Flow | 12.x |
| Forms | React Hook Form + Zod | Latest |
| HTTP Client | Axios | 1.x |
| PostgreSQL JDBC | pgjdbc | 42.7.7 |
| Authentication | JWT (jjwt) | Latest |
| Containerization | Docker / Docker Compose | Latest |

### Version Sources
- [Spring Boot 4.0.1](https://spring.io/blog/2025/12/18/spring-boot-4-0-1-available-now/)
- [Java 21 LTS](https://www.oracle.com/java/technologies/java-se-support-roadmap.html)
- [Hibernate ORM 7.x](https://hibernate.org/orm/releases/)
- [PostgreSQL JDBC 42.7.7](https://jdbc.postgresql.org/)
- [React 19.2](https://react.dev/blog/2025/10/01/react-19-2)
- [Spring AMQP 4.0](https://spring.io/projects/spring-amqp/)
- [Gradle 9.2.1](https://gradle.org/releases/)
- [RabbitMQ 4.2.x](https://www.rabbitmq.com/release-information)

---

## Architecture

### High-Level Architecture

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   React SPA     │────▶│  Spring Boot    │────▶│   PostgreSQL    │
│   (Frontend)    │     │  REST API       │     │   (Database)    │
└─────────────────┘     └────────┬────────┘     └─────────────────┘
                                 │
                                 ▼
                        ┌─────────────────┐     ┌─────────────────┐
                        │    RabbitMQ     │────▶│  Image Worker   │
                        │  (Message Queue)│     │  (Processing)   │
                        └─────────────────┘     └────────┬────────┘
                                                         │
                                                         ▼
                                                ┌─────────────────┐
                                                │  File Storage   │
                                                │  (Local + CDN)  │
                                                └─────────────────┘
```

### Event-Driven Processing (RabbitMQ)

**Image Processing Pipeline:**
1. User uploads photo via REST API
2. API saves original file and publishes message to `image.processing` queue
3. Image Worker consumes message and:
   - Generates thumbnails (small: 150px, medium: 400px, large: 800px)
   - Compresses/optimizes images (WebP conversion)
   - Extracts EXIF metadata (date taken, GPS coordinates if available)
   - Optional future: Face detection for tagging suggestions
4. Worker updates database with processed image metadata
5. Frontend polls or receives notification when processing complete

**Queues:**
- `image.upload` - New image upload events
- `image.processing` - Image processing tasks
- `image.processing.dead-letter` - Failed processing tasks

### File Storage Strategy

For single-server deployment with CDN-like performance:
- **Primary storage**: Local filesystem with organized directory structure
- **Serving**: Nginx as reverse proxy with aggressive caching headers
- **Structure**:
  ```
  /data/geneinator/
  ├── originals/
  │   └── {year}/{month}/{uuid}.{ext}
  ├── thumbnails/
  │   ├── small/
  │   ├── medium/
  │   └── large/
  └── temp/
  ```
- **Future**: Easy migration path to S3-compatible storage if needed

---

## User Roles & Permissions

### Role Hierarchy

```
Admin
  └── Branch Editor
        └── Authenticated User
              └── Anonymous (Welcome page only)
```

### Role Definitions

| Role | Description | Permissions |
|------|-------------|-------------|
| **Anonymous** | Not logged in | View welcome/landing page only |
| **Authenticated User** | Logged in, no edit rights | Browse tree (visibility rules apply), view allowed persons/photos/stories |
| **Branch Editor** | Approved editor for specific branch(es) | All User permissions + Create/Edit/Delete persons, photos, events, stories within their branch(es) |
| **Admin** | System administrator | Manage users, approve requests, configure system, view audit logs, merge duplicates. **Note**: Branch owners can hide specific data from admin |

### Permission Model

- **Branch ownership**: A user can be designated as owner of a branch (a subtree starting from a specific ancestor)
- **Multiple editors**: Multiple users can have edit rights to the same branch
- **Approval workflow**: Edit requests approved by either Admin OR current Branch Owner
- **Field-level privacy**: When adding/editing data, users control visibility (see Privacy section)

---

## Visibility & Privacy

### Visibility Rules (Admin Configurable)

Global defaults set in admin panel, with options:

| Setting | Description | Default |
|---------|-------------|---------|
| `spouse_family_visible` | Can user see spouse's family tree | `true` |
| `max_relationship_hops` | How many relationship steps away can user see | `3` |
| `include_marriage_connections` | Do marriage connections count for visibility | `true` |

These settings are configurable per installation via the admin panel.

### Branch Definition (Admin Configurable)

How a "branch" is defined can be configured:
- A subtree starting from a specific ancestor
- A lineage following a specific surname
- Custom groupings defined by admin

### Field-Level Privacy

When adding/editing any data, the creator can set visibility:
- **Public** - Visible to all authenticated users (within relationship rules)
- **Branch only** - Visible only to branch members
- **Private** - Visible only to creator and designated persons
- **Hidden from admin** - Branch owner can hide specific data from admin view

### Privacy Considerations

- Living persons may have stricter default privacy
- Contact information hidden by default (configurable)
- GDPR compliance: Users can request deletion of their data

---

## Data Model

### Core Entities

#### Person
| Field | Type | Required | Notes |
|-------|------|----------|-------|
| id | UUID | Yes | Primary key |
| full_name | String | **Yes** | Including maiden name in parentheses if applicable |
| birth_date | ApproximateDate | **Yes** | Supports partial/approximate dates |
| death_date | ApproximateDate | No | |
| gender | Enum | No | MALE, FEMALE, OTHER, UNKNOWN |
| biography | Text | No | Free-text story/about section |
| contact_info | JSON | No | Phone, email, address (privacy controlled) |
| location_birth | String | No | Place of birth |
| location_death | String | No | Place of death |
| location_burial | String | No | Burial location |
| created_by | UUID | Yes | User who created this record |
| created_at | Timestamp | Yes | |
| updated_at | Timestamp | Yes | |
| privacy_settings | JSON | Yes | Field-level visibility rules |

#### ApproximateDate (Value Object)
Supports partial and approximate dates like "1943", "circa 1830", "1884.03.06"

| Field | Type | Notes |
|-------|------|-------|
| year | Integer | Required if any date info exists |
| month | Integer | Optional (1-12) |
| day | Integer | Optional (1-31) |
| is_approximate | Boolean | True if prefixed with "circa" / "apie" / "approximately" |
| date_text | String | Original text representation |

#### Relationship
| Field | Type | Required | Notes |
|-------|------|----------|-------|
| id | UUID | Yes | |
| person_from_id | UUID | Yes | FK to Person |
| person_to_id | UUID | Yes | FK to Person |
| relationship_type | Enum | Yes | PARENT, CHILD, SPOUSE |
| start_date | ApproximateDate | No | Marriage date for SPOUSE |
| end_date | ApproximateDate | No | Divorce date if applicable |
| is_divorced | Boolean | No | For SPOUSE relationships |

#### Photo
| Field | Type | Required | Notes |
|-------|------|----------|-------|
| id | UUID | Yes | |
| original_path | String | Yes | Path to original file |
| thumbnail_small | String | No | Generated |
| thumbnail_medium | String | No | Generated |
| thumbnail_large | String | No | Generated |
| caption | String | No | |
| date_taken | ApproximateDate | No | From EXIF or manual |
| location | String | No | From EXIF GPS or manual |
| exif_data | JSON | No | Extracted metadata |
| processing_status | Enum | Yes | PENDING, PROCESSING, COMPLETED, FAILED |
| uploaded_by | UUID | Yes | |
| privacy_settings | JSON | Yes | |

#### PersonPhoto (Junction)
| Field | Type | Notes |
|-------|------|-------|
| person_id | UUID | FK to Person |
| photo_id | UUID | FK to Photo |
| is_primary | Boolean | Primary photo for person |

#### Event
| Field | Type | Required | Notes |
|-------|------|----------|-------|
| id | UUID | Yes | |
| event_type | Enum | Yes | WEDDING, GRADUATION, MILITARY_SERVICE, BIRTH, DEATH, BAPTISM, OTHER |
| title | String | Yes | |
| description | Text | No | |
| event_date | ApproximateDate | No | |
| location | String | No | |
| created_by | UUID | Yes | |
| privacy_settings | JSON | Yes | |

#### EventParticipant (Junction)
| Field | Type | Notes |
|-------|------|-------|
| event_id | UUID | FK to Event |
| person_id | UUID | FK to Person |
| role | String | Role in event (e.g., "bride", "groom", "witness") |

#### User
| Field | Type | Required | Notes |
|-------|------|----------|-------|
| id | UUID | Yes | |
| email | String | Yes | Unique, used for login |
| password_hash | String | Yes | BCrypt hashed |
| display_name | String | Yes | |
| role | Enum | Yes | ADMIN, USER |
| status | Enum | Yes | PENDING_APPROVAL, ACTIVE, SUSPENDED |
| created_at | Timestamp | Yes | |
| last_login | Timestamp | No | |

#### BranchPermission
| Field | Type | Notes |
|-------|------|-------|
| id | UUID | |
| user_id | UUID | FK to User |
| root_person_id | UUID | FK to Person (root of branch) |
| permission_type | Enum | VIEWER, EDITOR, OWNER |
| granted_by | UUID | FK to User (admin or owner who granted) |
| granted_at | Timestamp | |

#### Tree
| Field | Type | Notes |
|-------|------|-------|
| id | UUID | |
| name | String | Tree name (e.g., "Mažvila Family") |
| description | Text | |
| root_person_id | UUID | FK to Person (optional, can have multiple roots) |
| created_by | UUID | |
| is_mergeable | Boolean | Can be merged with other trees |

#### AuditLog
| Field | Type | Notes |
|-------|------|-------|
| id | UUID | |
| user_id | UUID | Who performed action |
| action | Enum | CREATE, UPDATE, DELETE, MERGE, LOGIN, etc. |
| entity_type | String | Person, Photo, Event, etc. |
| entity_id | UUID | |
| old_value | JSON | Previous state |
| new_value | JSON | New state |
| timestamp | Timestamp | |
| ip_address | String | |

---

## Features

### Phase 1 - Core (MVP)

#### Authentication & Authorization
- [ ] User registration with admin approval workflow
- [ ] JWT-based authentication
- [ ] Role-based access control (Admin, User)
- [ ] Password requirements: min 8 characters
- [ ] Account lockout after 5 failed attempts (15 min)

#### Person Management
- [ ] Create/Read/Update/Delete persons
- [ ] Required fields: full name, birth date
- [ ] Support for approximate/partial dates
- [ ] Biography/story text field
- [ ] Contact information (privacy controlled)
- [ ] Orphan nodes allowed (person without connections)
- [ ] Hard delete

#### Relationship Management
- [ ] Parent/Child relationships
- [ ] Spouse relationships (with marriage date)
- [ ] Divorce tracking (end date, is_divorced flag)

#### Photo Management
- [ ] Upload multiple photos per person
- [ ] No file size limit
- [ ] Async processing via RabbitMQ
- [ ] Thumbnail generation (small, medium, large)
- [ ] EXIF metadata extraction
- [ ] Primary photo designation

#### Tree Visualization
- [ ] Interactive graphical tree view (zoom, pan, click)
- [ ] List/table view alternative

#### Search
- [ ] Search by name
- [ ] Search by date ranges
- [ ] Search by location
- [ ] Search by relationships ("descendants of X")

#### Visibility & Privacy
- [ ] Configurable visibility rules (admin panel)
- [ ] Field-level privacy controls
- [ ] Branch-based access control
- [ ] Option to hide data from admin (branch owner privilege)

#### Admin Panel
- [ ] User management (approve, suspend, delete)
- [ ] System configuration (visibility defaults, branch definitions)
- [ ] Audit log viewer
- [ ] Merge duplicate persons

### Phase 2 - Enhanced

#### Export/Import
- [ ] Export tree data (JSON format) for backup
- [ ] Import for backup restore (admin only)
- [ ] Export branch as standalone file

#### Events
- [ ] Create/manage life events
- [ ] Multiple participants per event
- [ ] Event photos

#### Tree Management
- [ ] Multiple independent trees per installation
- [ ] Tree merging when connections discovered

### Phase 3 - Future (Architecture Ready, Not Implemented)

- [ ] PDF generation / family book export
- [ ] Comments on persons/photos/events
- [ ] Two-factor authentication (2FA)
- [ ] Face detection for photo tagging
- [ ] GEDCOM import/export
- [ ] System statistics dashboard
- [ ] Bulk data import/edit
- [ ] Mobile-optimized views

---

## API Design

### REST Endpoints

```
Authentication:
POST   /api/auth/register          - Register new user
POST   /api/auth/login              - Login, receive JWT
POST   /api/auth/refresh            - Refresh JWT token
POST   /api/auth/logout             - Invalidate token

Users (Admin):
GET    /api/admin/users             - List users (with filters)
PATCH  /api/admin/users/:id/approve - Approve pending user
PATCH  /api/admin/users/:id/suspend - Suspend user
DELETE /api/admin/users/:id         - Delete user

Persons:
GET    /api/persons                 - List persons (paginated, filtered by visibility)
GET    /api/persons/:id             - Get person details
POST   /api/persons                 - Create person
PUT    /api/persons/:id             - Update person
DELETE /api/persons/:id             - Delete person (hard delete)
GET    /api/persons/:id/relatives   - Get related persons
GET    /api/persons/:id/photos      - Get person's photos
GET    /api/persons/:id/events      - Get person's events

Relationships:
POST   /api/relationships           - Create relationship
DELETE /api/relationships/:id       - Delete relationship

Photos:
POST   /api/photos/upload           - Upload photo (multipart)
GET    /api/photos/:id              - Get photo details
DELETE /api/photos/:id              - Delete photo
POST   /api/photos/:id/persons      - Link photo to persons

Events:
GET    /api/events                  - List events
POST   /api/events                  - Create event
PUT    /api/events/:id              - Update event
DELETE /api/events/:id              - Delete event

Trees:
GET    /api/trees                   - List trees user can access
GET    /api/trees/:id               - Get tree structure
POST   /api/trees                   - Create new tree
POST   /api/trees/merge             - Merge two trees (admin)

Search:
GET    /api/search                  - Unified search endpoint

Admin:
GET    /api/admin/audit-logs        - View audit logs
GET    /api/admin/settings          - Get system settings
PUT    /api/admin/settings          - Update system settings
POST   /api/admin/persons/merge     - Merge duplicate persons

Branch Permissions:
GET    /api/branches/permissions    - List user's branch permissions
POST   /api/branches/request-access - Request edit access
POST   /api/branches/:id/approve    - Approve access request (owner/admin)
```

---

## Internationalization (i18n)

### Supported Languages
- English (en) - Default
- Lithuanian (lt)

### Implementation
- Frontend: react-i18next
- Backend: Spring MessageSource for error messages
- Database: Store data in original language (no translation of user content)

---

## Deployment

### Docker Compose Setup

```yaml
services:
  app:
    build: ./backend
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - rabbitmq
    volumes:
      - ./data:/data/geneinator
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_PASSWORD=${DB_PASSWORD}

  frontend:
    build: ./frontend
    ports:
      - "3000:80"

  postgres:
    image: postgres:16
    volumes:
      - postgres_data:/var/lib/postgresql/data
    environment:
      POSTGRES_DB: geneinator
      POSTGRES_USER: geneinator
      POSTGRES_PASSWORD: ${DB_PASSWORD}

  rabbitmq:
    image: rabbitmq:4-management
    ports:
      - "5672:5672"
      - "15672:15672"

  image-worker:
    build: ./image-worker
    depends_on:
      - rabbitmq
    volumes:
      - ./data:/data/geneinator

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./data:/data/geneinator:ro

volumes:
  postgres_data:
```

### Server Requirements
- **Target**: Home server (repurposed PC)
- **Specs**: Intel i5 10th gen, 32GB RAM
- **OS**: Linux
- **Storage**: Local filesystem

### Resource Allocation (Recommended)
- PostgreSQL: 4GB RAM
- RabbitMQ: 512MB RAM
- Spring Boot App: 2GB RAM
- Image Worker: 1GB RAM (limit concurrent processing to 2-3 images)
- Nginx: 256MB RAM
- React (served static): Minimal

---

## Security Considerations

- Passwords hashed with BCrypt (cost factor 12)
- JWT tokens with 24h expiry, refresh tokens with 7d expiry
- Rate limiting on auth endpoints
- Input validation on all endpoints
- SQL injection prevention via parameterized queries (Hibernate)
- XSS prevention in React (default escaping)
- CORS configured for frontend origin only
- File upload validation (image types only)
- No sensitive data in JWT payload
- Account lockout after 5 failed login attempts

---

## Testing Strategy

- **Unit tests**: Services, utilities
- **Integration tests**: Repository layer, API endpoints
- **E2E tests**: Critical user flows (login, add person, upload photo)
- **Coverage target**: 70% for backend

---

## Project Structure

```
geneinator/
├── backend/
│   ├── src/main/java/com/geneinator/
│   │   ├── config/          # Spring configuration
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data transfer objects
│   │   ├── entity/          # JPA entities
│   │   ├── repository/      # Spring Data repositories
│   │   ├── service/         # Business logic
│   │   ├── security/        # JWT, auth filters
│   │   ├── messaging/       # RabbitMQ producers/consumers
│   │   └── exception/       # Custom exceptions
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── application-dev.yml
│   │   ├── application-prod.yml
│   │   └── messages/        # i18n message bundles
│   └── build.gradle.kts
├── frontend/
│   ├── src/
│   │   ├── components/      # Reusable React components
│   │   │   └── ui/          # shadcn/ui components
│   │   ├── pages/           # Page components
│   │   ├── features/        # Feature-specific components
│   │   │   ├── tree/        # React Flow tree visualization
│   │   │   ├── persons/     # Person management
│   │   │   ├── photos/      # Photo gallery/upload
│   │   │   └── auth/        # Login/register
│   │   ├── api/             # Axios API clients
│   │   ├── hooks/           # Custom React hooks
│   │   ├── store/           # Zustand stores
│   │   ├── lib/             # Utilities, helpers
│   │   ├── i18n/            # Translation files (en, lt)
│   │   └── types/           # TypeScript types
│   ├── tailwind.config.js
│   └── package.json
├── image-worker/
│   └── (Separate Spring Boot app for async image processing)
├── docker-compose.yml
├── docker-compose.dev.yml
├── flake.nix                # Nix development environment
├── REQUIREMENTS.md
└── README.md
```

---

## Technology Decisions

| Category | Choice | Rationale |
|----------|--------|-----------|
| Tree visualization | **React Flow** | Most popular for node-based UIs in React, built-in zoom/pan/minimap, excellent docs, active development |
| Image processing | **libvips** | Faster than ImageMagick, lower memory usage, better for thumbnails |
| State management | **TanStack Query + Zustand** | TanStack Query for server/API state (caching, refetching), Zustand for minimal local UI state. Modern combo for medium-sized apps |
| UI Components | **shadcn/ui** | Clean modern aesthetic, built on Radix UI + Tailwind CSS, highly customizable, rapidly growing popularity |
| CSS Framework | **Tailwind CSS** | Required by shadcn/ui, utility-first, fast development |
| Form handling | **React Hook Form + Zod** | Performant forms, Zod for schema validation |
| HTTP Client | **Axios** | Interceptors for JWT refresh, better error handling than fetch |

---

## Revision History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2026-01-03 | Initial requirements document |
