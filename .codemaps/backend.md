# Backend Codemap

## Directory Structure

```
backend/src/main/java/com/geneinator/
├── GeinatorApplication.java          # Main entry point
├── config/
│   └── SecurityConfig.java           # Spring Security configuration
├── controller/                        # REST API endpoints (9 controllers)
│   ├── AuthController.java           # /api/auth/*
│   ├── PersonController.java         # /api/persons/*
│   ├── TreeController.java           # /api/trees/*
│   ├── RelationshipController.java   # /api/relationships/*
│   ├── PhotoController.java          # /api/photos/*
│   ├── EventController.java          # /api/events/*
│   ├── SearchController.java         # /api/search/*
│   ├── StorageController.java        # /api/storage/* (file serving)
│   └── AdminController.java          # /api/admin/*
├── service/                           # Business logic interfaces
│   ├── AuthService.java
│   ├── PersonService.java
│   ├── TreeService.java
│   ├── RelationshipService.java
│   ├── PhotoService.java
│   ├── EventService.java
│   ├── UserService.java
│   ├── AuditService.java
│   ├── VisibilityService.java
│   ├── SettingsService.java
│   └── StorageService.java
│   └── impl/                          # Service implementations
│       ├── AuthServiceImpl.java
│       ├── PersonServiceImpl.java
│       ├── TreeServiceImpl.java
│       ├── RelationshipServiceImpl.java
│       ├── PhotoServiceImpl.java
│       ├── EventServiceImpl.java
│       ├── UserServiceImpl.java
│       ├── AuditServiceImpl.java
│       ├── VisibilityServiceImpl.java
│       ├── SettingsServiceImpl.java
│       └── LocalStorageServiceImpl.java
├── repository/                        # JPA data access
│   ├── UserRepository.java
│   ├── PersonRepository.java
│   ├── TreeRepository.java
│   ├── RelationshipRepository.java
│   ├── PhotoRepository.java
│   ├── PersonPhotoRepository.java
│   ├── EventRepository.java
│   ├── BranchPermissionRepository.java
│   └── AuditLogRepository.java
├── entity/                            # Domain models
│   ├── BaseEntity.java               # Common fields (id, created, updated)
│   ├── User.java                     # System users
│   ├── Person.java                   # Tree individuals
│   ├── Tree.java                     # Family trees
│   ├── Relationship.java            # Person connections
│   ├── Photo.java                    # Uploaded images
│   ├── PersonPhoto.java             # Person-photo associations
│   ├── Event.java                    # Family events
│   ├── EventParticipant.java        # Event attendees
│   ├── ApproximateDate.java         # Uncertain dates (embeddable)
│   ├── BranchPermission.java        # Access control
│   └── AuditLog.java                # System audit trail
├── dto/                               # Data Transfer Objects
│   ├── auth/                         # Auth DTOs
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   ├── RegisterRequest.java
│   │   ├── RegisterResponse.java
│   │   ├── RefreshTokenRequest.java
│   │   └── TokenResponse.java
│   ├── person/                       # Person DTOs
│   │   ├── PersonDto.java
│   │   ├── PersonCreateRequest.java
│   │   ├── PersonUpdateRequest.java
│   │   ├── PersonMergeRequest.java
│   │   └── RelativeDto.java
│   ├── tree/                         # Tree DTOs
│   │   ├── TreeDto.java
│   │   ├── TreeCreateRequest.java
│   │   └── TreeStructureDto.java
│   ├── relationship/
│   │   ├── RelationshipDto.java
│   │   └── RelationshipCreateRequest.java
│   ├── photo/
│   │   ├── PhotoDto.java
│   │   └── PhotoUploadResponse.java
│   ├── event/
│   │   ├── EventDto.java
│   │   ├── EventCreateRequest.java
│   │   └── EventUpdateRequest.java
│   ├── user/
│   │   ├── UserDto.java
│   │   └── UserUpdateRequest.java
│   ├── settings/
│   │   └── SystemSettingsDto.java
│   ├── audit/
│   │   └── AuditLogDto.java
│   └── common/
│       └── ApproximateDateDto.java
├── security/                          # JWT authentication
│   ├── JwtService.java               # Token generation/validation
│   ├── JwtAuthenticationFilter.java  # Request filter
│   └── UserDetailsServiceImpl.java   # User loading
├── messaging/                         # RabbitMQ integration
│   ├── RabbitMQConfig.java           # Queue/exchange setup
│   ├── ImageProcessingMessage.java   # Message DTO
│   └── ImageMessagePublisher.java    # Message sender
└── exception/                         # Custom exceptions
    ├── GlobalExceptionHandler.java   # @ControllerAdvice
    ├── ResourceNotFoundException.java
    ├── DuplicateResourceException.java
    └── AccountLockedException.java
```

## Layer Flow

```
HTTP Request
     │
     ▼
┌─────────────────────────────────────────────────────────────┐
│  JwtAuthenticationFilter                                     │
│  - Extracts JWT from Authorization header                   │
│  - Validates token via JwtService                           │
│  - Sets SecurityContext                                     │
└─────────────────────────────────────────────────────────────┘
     │
     ▼
┌─────────────────────────────────────────────────────────────┐
│  Controller Layer                                            │
│  - Request validation (@Valid)                              │
│  - Authorization (@PreAuthorize, @AuthenticationPrincipal) │
│  - HTTP response mapping                                    │
└─────────────────────────────────────────────────────────────┘
     │
     ▼
┌─────────────────────────────────────────────────────────────┐
│  Service Layer                                               │
│  - Business logic                                           │
│  - Transaction management (@Transactional)                  │
│  - DTO <-> Entity conversion                                │
│  - Cross-cutting concerns (audit, visibility)               │
└─────────────────────────────────────────────────────────────┘
     │
     ▼
┌─────────────────────────────────────────────────────────────┐
│  Repository Layer                                            │
│  - Spring Data JPA interfaces                               │
│  - Custom queries (@Query)                                  │
│  - Derived query methods                                    │
└─────────────────────────────────────────────────────────────┘
     │
     ▼
┌─────────────────────────────────────────────────────────────┐
│  PostgreSQL Database                                         │
└─────────────────────────────────────────────────────────────┘
```

## API Endpoints

| Controller | Base Path | Key Endpoints |
|------------|-----------|---------------|
| AuthController | `/api/auth` | POST /login, /register, /refresh, /logout |
| PersonController | `/api/persons` | CRUD, GET /{id}/relatives, POST /merge |
| TreeController | `/api/trees` | CRUD, GET /{id}/structure, POST /merge |
| RelationshipController | `/api/relationships` | POST /, DELETE /{id} |
| PhotoController | `/api/photos` | POST /upload, GET /{id}, POST /{id}/link |
| EventController | `/api/events` | CRUD, POST /{id}/participants |
| SearchController | `/api/search` | GET /persons, /ancestors, /descendants |
| StorageController | `/api/storage` | GET /files (serves stored images/thumbnails) |
| AdminController | `/api/admin` | GET /users, /audit-logs, /statistics |

## Key Services

### AuthServiceImpl
- `register()` - Create user with bcrypt password
- `authenticate()` - Validate credentials, return JWT pair
- `refreshToken()` - Issue new access token

### PersonServiceImpl
- `createPerson()` - Add person to tree
- `getRelatives()` - Find family members (returns RelativeDto)
- `mergePeople()` - Combine duplicate records

### TreeServiceImpl
- `getTreeStructure()` - Build visualization data (nodes + edges)
- `mergeTrees()` - Combine trees from different users

### PhotoServiceImpl
- `uploadPhoto()` - Save file, create entity, publish to RabbitMQ
- `linkToPersons()` - Associate photo with people

### VisibilityServiceImpl
- `isVisible()` - Check privacy rules
- `getRelationshipDistance()` - BFS pathfinding

### StorageController
- Serves stored files (originals, thumbnails) via HTTP
- Used by frontend to display uploaded photos

## Build Configuration

- **Spring Boot**: 4.0.1
- **Java**: 21
- **Build tool**: Gradle Kotlin DSL (build.gradle.kts)
- **Key libraries**: JJWT 0.12.6, MapStruct 1.6.3, PostgreSQL 42.7.7

## Testing

Tests location: `backend/src/test/java/com/geneinator/`

```
Run all: ./gradlew test
Run single class: ./gradlew test --tests "PersonServiceTest"
Run single method: ./gradlew test --tests "PersonServiceTest.shouldCreatePerson"
Coverage: ./gradlew test jacocoTestReport
```
