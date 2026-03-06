# System Overview Codemap

## Component Architecture

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                              GENEINATOR SYSTEM                               │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────┐        ┌──────────────┐        ┌──────────────────────┐  │
│  │   Frontend   │  HTTP  │   Backend    │  AMQP  │    Image Worker      │  │
│  │   (React)    │ ────── │ (Spring Boot)│ ────── │   (Spring Boot)      │  │
│  │  :3000       │        │  :8080       │        │   RabbitMQ Consumer  │  │
│  └──────────────┘        └──────────────┘        └──────────────────────┘  │
│         │                       │                          │                │
│         │                       │ JDBC                     │ JDBC           │
│         │                       ▼                          ▼                │
│         │               ┌──────────────┐          ┌──────────────┐         │
│         │               │  PostgreSQL  │          │  PostgreSQL  │         │
│         │               │    :5432     │          │   (shared)   │         │
│         │               └──────────────┘          └──────────────┘         │
│         │                                                                   │
│         │                       ┌──────────────┐                           │
│         │                       │   RabbitMQ   │                           │
│         │                       │    :5672     │                           │
│         │                       └──────────────┘                           │
│         │                                                                   │
│         │               ┌────────────────────────────────────────┐         │
│         └──────────────▶│     File Storage: /data/geneinator     │         │
│           (serves       │  ├── originals/                        │         │
│            images)      │  ├── thumbnails/{small,medium,large}/  │         │
│                         │  └── temp/                             │         │
│                         └────────────────────────────────────────┘         │
│                                                                              │
│  Production only:                                                           │
│  ┌──────────────┐                                                           │
│  │    Nginx     │  Reverse proxy (:80 or :8888)                            │
│  │  /api  ──────┼──▶ Backend :8080                                         │
│  │  /     ──────┼──▶ Frontend :80                                          │
│  │  /media ─────┼──▶ /data/geneinator (static, 30d cache)                 │
│  └──────────────┘                                                           │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

## Docker Compose Profiles

- **docker-compose.yml** - Full stack (all services)
- **docker-compose.dev.yml** - Infrastructure only (PostgreSQL + RabbitMQ); app services run locally via `nix develop`
- **docker-compose.prod.yml** - Production overrides; hides internal ports, exposes only Nginx :8888

## Request Flow Examples

### Authentication Flow
```
User submits login form
        │
        ▼
Frontend: LoginPage.tsx
        │ POST /api/auth/login (credentials: include)
        ▼
Backend: AuthController.login()
        │
        ▼
AuthServiceImpl.authenticate()
        │
        ▼
JwtService.generateToken()
        │
        ▼
Backend: Set HttpOnly cookies (ACCESS_TOKEN, REFRESH_TOKEN)
        │ Returns UserInfoResponse (no tokens in body)
        ▼
Frontend: authStore.setAuth(user)
        │ stores user metadata in localStorage (no tokens)
        ▼
User redirected to Dashboard

Page reload:
  AuthProvider calls GET /api/auth/me (cookie auto-sent)
        │
        ▼
  Re-hydrates user state from server response
```

### Photo Upload Flow
```
User uploads photo
        │
        ▼
Frontend: PersonPage.tsx
        │ POST /api/photos (multipart)
        ▼
Backend: PhotoController.upload()
        │
        ▼
PhotoServiceImpl.uploadPhoto()
        │
        ├── Save file to /data/geneinator/originals/
        │
        ├── Create Photo entity (status=PENDING)
        │
        └── Publish to RabbitMQ
                │
                ▼
        Image Worker: ImageProcessingListener
                │
                ▼
        ImageProcessingServiceImpl.process()
                │
                ├── ThumbnailServiceImpl (small, medium, large)
                │
                ├── ExifServiceImpl (metadata extraction)
                │
                └── Update Photo entity (status=COMPLETED)
```

### Tree Visualization Flow
```
User opens tree page
        │
        ▼
Frontend: TreePage.tsx
        │ GET /api/trees/{id}/structure
        ▼
Backend: TreeController.getStructure()
        │
        ▼
TreeServiceImpl.getTreeStructure()
        │
        ├── PersonRepository.findByTreeId()
        │
        └── RelationshipRepository.findByTreeId()
                │
                ▼
        TreeStructureDto (nodes + edges)
                │
                ▼
Frontend: treeLayout.ts (dagre algorithm)
        │
        ▼
React Flow renders hierarchical tree
```

### Person Creation Flow
```
User navigates to /tree/:treeId/person/new
        │
        ▼
Frontend: PersonCreatePage.tsx
        │ POST /api/persons
        ▼
Backend: PersonController.create()
        │
        ▼
PersonServiceImpl.createPerson()
        │
        ▼
User redirected to PersonPage
```

## Database Entity Relationships

```
User (1) ──────────────────── (*) Tree
  │                               │
  │                               │ (1)
  │                               ▼
  │                         (*) Person ──────── (*) PersonPhoto ──────── (*) Photo
  │                               │
  │                               │
  │                         (*) Relationship (person1_id, person2_id, type)
  │                               │
  │                         (*) EventParticipant
  │                               │
  │                               ▼
  │                         (*) Event
  │
  └── (*) AuditLog
```

## Key Ports
- Frontend: 3000 (dev), 80 (container)
- Backend API: 8080
- PostgreSQL: 5432
- RabbitMQ: 5672 (AMQP), 15672 (Management UI)
- Nginx: 80 / 8888 (production only)

## Additional Tools
- `tools/excel-parser/` - Python utilities for importing genealogy data from Excel files
