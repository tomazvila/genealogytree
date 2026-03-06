// User types
export interface User {
  id: string
  email: string
  displayName: string
  role: 'ADMIN' | 'USER'
  status: 'PENDING_APPROVAL' | 'ACTIVE' | 'SUSPENDED'
  createdAt: string
  lastLogin?: string
}

// Auth types
export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  email: string
  password: string
  displayName: string
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  userId: string
  email: string
  displayName: string
  role: string
}

// Approximate date
export interface ApproximateDate {
  year?: number
  month?: number
  day?: number
  isApproximate?: boolean
  dateText?: string
}

// Person types
export interface Person {
  id: string
  fullName: string
  birthDate: ApproximateDate
  deathDate?: ApproximateDate
  gender?: 'MALE' | 'FEMALE' | 'OTHER' | 'UNKNOWN'
  biography?: string
  contactInfo?: Record<string, string>
  locationBirth?: string
  locationDeath?: string
  locationBurial?: string
  treeId?: string
  primaryPhotoUrl?: string
  createdAt: string
  updatedAt: string
}

// Relationship filter type
export type RelationshipFilterType = 'PARENT' | 'CHILD' | 'SPOUSE' | 'SIBLING' | 'COUSIN'

// Relative type includes person info plus relationship type
export interface Relative extends Person {
  relationshipType: RelationshipFilterType
}

export interface PersonCreateRequest {
  fullName: string
  birthDate: ApproximateDate
  deathDate?: ApproximateDate
  gender?: string
  biography?: string
  contactInfo?: Record<string, string>
  locationBirth?: string
  locationDeath?: string
  locationBurial?: string
  treeId?: string
  privacySettings?: Record<string, unknown>
}

// Relationship types
export interface Relationship {
  id: string
  personFromId: string
  personFromName: string
  personToId: string
  personToName: string
  relationshipType: 'PARENT' | 'CHILD' | 'SPOUSE' | 'SIBLING'
  startDate?: ApproximateDate
  endDate?: ApproximateDate
  isDivorced?: boolean
}

export interface RelationshipCreateRequest {
  personFromId: string
  personToId: string
  relationshipType: 'PARENT' | 'CHILD' | 'SPOUSE' | 'SIBLING'
  startDate?: ApproximateDate
  endDate?: ApproximateDate
  isDivorced?: boolean
}

// Photo types
export interface Photo {
  id: string
  originalUrl: string
  thumbnailSmallUrl?: string
  thumbnailMediumUrl?: string
  thumbnailLargeUrl?: string
  caption?: string
  dateTaken?: ApproximateDate
  location?: string
  processingStatus: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED'
  personIds: string[]
  createdAt: string
}

// Tree types
export interface Tree {
  id: string
  name: string
  description?: string
  rootPersonId?: string
  rootPersonName?: string
  personCount: number
  isMergeable?: boolean
  createdBy?: string
  createdAt: string
}

export interface TreeStructure {
  treeId: string
  treeName: string
  createdBy?: string
  persons: Person[]
  relationships: Relationship[]
}

// Event types
export interface Event {
  id: string
  eventType: 'WEDDING' | 'GRADUATION' | 'MILITARY_SERVICE' | 'BIRTH' | 'DEATH' | 'BAPTISM' | 'OTHER'
  title: string
  description?: string
  eventDate?: ApproximateDate
  location?: string
  participants: EventParticipant[]
  createdAt: string
}

export interface EventParticipant {
  personId: string
  personName: string
  role?: string
}

// API Response types
export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

export interface ApiError {
  timestamp: string
  status: number
  error: string
  message: string
  validationErrors?: Record<string, string>
}
