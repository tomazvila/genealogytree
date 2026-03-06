# Image Worker Codemap

## Directory Structure

```
image-worker/src/main/java/com/geneinator/worker/
├── ImageWorkerApplication.java        # Main entry point
├── config/
│   └── RabbitMQConfig.java           # Queue/exchange configuration
├── entity/
│   └── Photo.java                    # Simplified Photo entity (shared DB table)
├── repository/
│   └── PhotoRepository.java          # JPA repository for status updates
├── messaging/
│   ├── ImageProcessingMessage.java   # Message DTO
│   └── ImageProcessingListener.java  # RabbitMQ consumer
└── service/
    ├── ImageProcessingService.java    # Processing interface
    ├── ImageProcessingServiceImpl.java # Orchestrates processing
    ├── ImageProcessingResult.java     # Result DTO
    ├── ThumbnailService.java          # Thumbnail interface
    ├── ThumbnailServiceImpl.java      # Generates thumbnails (Java AWT)
    ├── ExifService.java               # EXIF interface
    └── ExifServiceImpl.java           # Extracts metadata
```

## Processing Pipeline

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       IMAGE PROCESSING FLOW                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  Backend publishes message                                               │
│         │                                                                │
│         ▼                                                                │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │  RabbitMQ Queue: image.processing                                 │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│         │                                                                │
│         ▼                                                                │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │  ImageProcessingListener.handleMessage()                          │  │
│  │  - Receives ImageProcessingMessage(photoId, originalPath)         │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│         │                                                                │
│         ▼                                                                │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │  ImageProcessingServiceImpl.process()                             │  │
│  │                                                                    │  │
│  │  1. Validate file exists + path security checks                   │  │
│  │  2. Generate thumbnails (ThumbnailService)                        │  │
│  │  3. Extract EXIF metadata (ExifService)                           │  │
│  │  4. Update Photo entity in database via PhotoRepository           │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│         │                                                                │
│         ├───────────────────┬───────────────────┐                       │
│         ▼                   ▼                   ▼                       │
│  ┌────────────┐      ┌────────────┐      ┌────────────┐                │
│  │ Thumbnail  │      │ Thumbnail  │      │ Thumbnail  │                │
│  │  Small     │      │  Medium    │      │  Large     │                │
│  │  150px     │      │  400px     │      │  800px     │                │
│  └────────────┘      └────────────┘      └────────────┘                │
│         │                   │                   │                       │
│         └───────────────────┴───────────────────┘                       │
│                             │                                            │
│                             ▼                                            │
│                   /data/geneinator/thumbnails/                          │
│                   ├── small/{photoId}.jpg                               │
│                   ├── medium/{photoId}.jpg                              │
│                   └── large/{photoId}.jpg                               │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

## Message Flow

```
ImageProcessingMessage {
  photoId: Long          // Database ID of Photo entity
  originalPath: String   // Path to original file
}
```

## RabbitMQ Configuration

```
Queues:
├── image.processing           # Main processing queue
└── image.processing.dlq       # Dead-letter queue (failed jobs)

Exchange: image.exchange (direct)

Bindings:
├── image.processing ← routing key: image.process
└── image.processing.dlq ← routing key: image.process.dlq
```

## Entity & Repository

The worker has its own simplified `Photo` entity mapping to the shared database table:
```java
Photo {
  UUID id
  String thumbnailSmall, thumbnailMedium, thumbnailLarge
  String exifData  // JSONB column
  ProcessingStatus status  // PENDING, PROCESSING, COMPLETED, FAILED
}
```

`PhotoRepository` provides JPA access for updating processing status and thumbnail/EXIF results.

## Service Details

### ThumbnailServiceImpl
- Uses **Java AWT** (BufferedImage, Graphics2D) for image manipulation
- Maintains aspect ratio
- Outputs JPEG format
- Thumbnail sizes: 150px (small), 400px (medium), 800px (large)
- Proper image resource disposal

### ExifServiceImpl
- Uses metadata-extractor library
- Extracts: date taken, GPS coordinates, camera make/model, orientation
- Detects file size, dimensions, MIME type
- Handles missing/corrupt EXIF gracefully

### ImageProcessingServiceImpl - Path Security
- Validates against `../` path traversal sequences
- Rejects absolute paths
- Handles URL-encoded traversal attempts
- Ensures resolved paths stay within configured base directory

### Error Handling
```
Processing attempt
       │
       ├── Success ─► Update Photo.status = COMPLETED
       │
       └── Failure ─► Retry (3 attempts, exponential backoff 1s → 10s)
                │
                └── All retries exhausted ─► Dead-letter queue
                                           ─► Photo.status = FAILED
```

## Photo Status Lifecycle

```
PENDING ──► PROCESSING ──┬──► COMPLETED
                         │
                         └──► FAILED
```

## Configuration (application.yml)

```yaml
storage:
  base-path: ../backend/data/geneinator
processing:
  max-concurrent: 3
  retry:
    max-attempts: 3
    backoff: 1s → 10s (exponential)
thumbnail:
  sizes: 150px, 400px, 800px
```

## Technologies

- **Java AWT** - Image manipulation (BufferedImage, Graphics2D)
- **TwelveMonkeys ImageIO** (3.12.0) - Extended format support
- **metadata-extractor** (2.19.0) - EXIF parsing
- **Spring Retry** (2.0.11) - Retry logic
- **Spring AMQP** - RabbitMQ integration
- **Jackson** (2.20.1) - JSON message serialization
- **PostgreSQL** (42.7.7) - Database driver

## Testing

```bash
cd image-worker
./gradlew test
```

Test classes (JUnit 5 + Mockito + AssertJ):
- `ImageProcessingListenerTest` - Message handling, status transitions
- `ImageProcessingServiceTest` - Path validation, EXIF extraction, thumbnail generation, security
- `ThumbnailServiceTest` - Aspect ratio, dimension handling, portrait/landscape
- `ExifServiceTest` - Metadata extraction, MIME type detection, error cases

Integration tests use Testcontainers (RabbitMQ, PostgreSQL).
