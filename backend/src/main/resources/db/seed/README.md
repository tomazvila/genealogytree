# Mock Data Migration Guide

This guide explains how to set up the Geneinator project from scratch and apply the mock genealogy data for development testing.

## Prerequisites

- Docker and Docker Compose installed
- Java 21+ (for running backend locally)
- PostgreSQL client (`psql`) - optional, for manual SQL execution

---

## Step 1: Start Infrastructure Services

Start PostgreSQL and RabbitMQ using Docker Compose:

```bash
cd /Users/lilvilla/Programming/Geneinator

# Start only the database and message queue
docker compose up -d postgres rabbitmq
```

Wait for services to be healthy:

```bash
docker compose ps
```

You should see both services with status `healthy`.

**Connection details:**
- PostgreSQL: `localhost:5432`
- Database: `geneinator`
- Username: `geneinator`
- Password: `geneinator_dev`

---

## Step 2: Start the Backend (Creates Schema)

The backend uses Hibernate `ddl-auto: update` in dev mode, which automatically creates database tables from JPA entities.

### Option A: Run with Gradle (Recommended for Development)

```bash
cd backend

# Run the Spring Boot application
./gradlew bootRun
```

### Option B: Run with Docker Compose

```bash
docker compose up -d backend
```

**Wait for the backend to fully start.** Check logs:

```bash
# If using Gradle
# Watch the terminal output

# If using Docker
docker compose logs -f backend
```

Look for: `Started GeneinatorApplication in X seconds`

At this point, Hibernate has created all database tables automatically.

---

## Step 3: Register a Test User

The mock data requires a user for the `created_by` fields. Register one via the API:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@example.com",
    "password": "password123",
    "displayName": "Test User"
  }'
```

**Save the returned user ID** from the response. It will look like:

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "email": "testuser@example.com",
  "displayName": "Test User"
}
```

**Activate the user** (new users require admin approval by default):

```bash
PGPASSWORD=geneinator_dev psql -h localhost -U geneinator -d geneinator -c "
UPDATE users SET status = 'ACTIVE' WHERE email = 'testuser@example.com';
"
```

---

## Step 4: Update the Migration File with User ID

Edit the migration file to use your registered user's ID:

```bash
# Open the migration file
nano backend/src/main/resources/db/seed/V999__mock_genealogy_data.sql
```

**Option A: Replace the placeholder UUID**

Find and replace all occurrences of the mock user UUID with your actual user ID:

```sql
-- Find this pattern (the UUID will differ):
'c7525ffc-01ec-4818-a372-af2b2d972e3f'

-- Replace with your user ID from Step 3:
'a1b2c3d4-e5f6-7890-abcd-ef1234567890'
```

Using sed:

```bash
# Replace USER_ID_FROM_STEP_3 with actual ID
sed -i '' 's/MOCK_USER_UUID_IN_FILE/YOUR_ACTUAL_USER_ID/g' \
  backend/src/main/resources/db/seed/V999__mock_genealogy_data.sql
```

**Option B: Use SQL to get any existing user (simpler)**

Prepend this to the migration file or run separately:

```sql
-- Get the first available user ID
DO $$
DECLARE
    v_user_id UUID;
BEGIN
    SELECT id INTO v_user_id FROM users LIMIT 1;
    IF v_user_id IS NULL THEN
        RAISE EXCEPTION 'No users found. Please register a user first.';
    END IF;
    RAISE NOTICE 'Using user ID: %', v_user_id;
END $$;
```

---

## Step 5: Apply the Mock Data Migration

### Option A: Using psql (Recommended)

```bash
# Connect to the database and run the migration
PGPASSWORD=geneinator_dev psql \
  -h localhost \
  -U geneinator \
  -d geneinator \
  -f backend/src/main/resources/db/seed/V999__mock_genealogy_data.sql
```

### Option B: Using Docker exec

```bash
# Copy the file into the container
docker cp backend/src/main/resources/db/seed/V999__mock_genealogy_data.sql \
  geneinator-postgres:/tmp/migration.sql

# Execute the migration
docker exec -it geneinator-postgres \
  psql -U geneinator -d geneinator -f /tmp/migration.sql
```

### Option C: Using a database GUI

1. Connect to `localhost:5432` with credentials above
2. Open `V999__mock_genealogy_data.sql`
3. Execute the entire script

---

## Step 6: Start the Frontend

In a new terminal:

```bash
cd /Users/lilvilla/Programming/Geneinator/frontend

npm install   # First time only
npm run dev
```

Frontend will be available at: **http://localhost:5173**

Log in with the credentials from Step 3:
- Email: `testuser@example.com`
- Password: `password123`

---

## Step 7: Verify the Data (Optional)

Check that data was inserted correctly:

```bash
PGPASSWORD=geneinator_dev psql -h localhost -U geneinator -d geneinator -c "
SELECT
  (SELECT COUNT(*) FROM trees) as trees,
  (SELECT COUNT(*) FROM persons) as persons,
  (SELECT COUNT(*) FROM relationships) as relationships;
"
```

Expected output:

```
 trees | persons | relationships
-------+---------+---------------
     1 |      75 |            54
```

View some sample persons:

```bash
PGPASSWORD=geneinator_dev psql -h localhost -U geneinator -d geneinator -c "
SELECT full_name, gender, birth_year, death_year
FROM persons
ORDER BY birth_year
LIMIT 10;
"
```

---

## Quick Start Script

For convenience, here's a complete script that does everything:

```bash
#!/bin/bash
set -e

cd /Users/lilvilla/Programming/Geneinator

echo "=== Step 1: Starting infrastructure ==="
docker compose up -d postgres rabbitmq
sleep 5

echo "=== Step 2: Starting backend ==="
cd backend
./gradlew bootRun &
BACKEND_PID=$!
sleep 30  # Wait for backend to start and create schema

echo "=== Step 3: Registering test user ==="
USER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "devuser@test.local",
    "password": "devpassword123",
    "displayName": "Dev User"
  }')

USER_ID=$(echo $USER_RESPONSE | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "Created user: $USER_ID"

echo "=== Step 4: Updating migration with user ID ==="
cd ..
# Get the current mock user ID from the file
OLD_UUID=$(grep -o "'[0-9a-f-]\{36\}'" backend/src/main/resources/db/seed/V999__mock_genealogy_data.sql | head -1 | tr -d "'")
sed -i '' "s/$OLD_UUID/$USER_ID/g" backend/src/main/resources/db/seed/V999__mock_genealogy_data.sql

echo "=== Step 5: Applying migration ==="
PGPASSWORD=geneinator_dev psql -h localhost -U geneinator -d geneinator \
  -f backend/src/main/resources/db/seed/V999__mock_genealogy_data.sql

echo "=== Step 6: Verifying ==="
PGPASSWORD=geneinator_dev psql -h localhost -U geneinator -d geneinator -c "
SELECT
  (SELECT COUNT(*) FROM trees) as trees,
  (SELECT COUNT(*) FROM persons) as persons,
  (SELECT COUNT(*) FROM relationships) as relationships;
"

echo "=== Done! ==="
echo "Backend running on http://localhost:8080"
echo "Login with: devuser@test.local / devpassword123"

# Keep backend running
wait $BACKEND_PID
```

---

## Troubleshooting

### Error: "relation 'users' does not exist"

The backend hasn't created the schema yet. Make sure the backend is running and has fully started before applying the migration.

### Error: "violates foreign key constraint"

The user ID in the migration doesn't exist. Either:
1. Register a user first (Step 3)
2. Update the migration file with a valid user ID (Step 4)

### Error: "duplicate key value violates unique constraint"

The migration has already been run. To reset:

```bash
PGPASSWORD=geneinator_dev psql -h localhost -U geneinator -d geneinator -c "
DELETE FROM relationships WHERE id IN (SELECT id FROM relationships WHERE person_from_id IN (SELECT id FROM persons WHERE tree_id = 'TREE_ID_FROM_MIGRATION'));
DELETE FROM persons WHERE tree_id = 'TREE_ID_FROM_MIGRATION';
DELETE FROM trees WHERE id = 'TREE_ID_FROM_MIGRATION';
"
```

Or completely reset the database:

```bash
docker compose down -v  # Removes volumes (all data)
docker compose up -d postgres rabbitmq
# Then start from Step 2
```

### Want to regenerate mock data?

Run the parser again:

```bash
cd tools/excel-parser
nix develop --command python3 parse_and_generate.py
```

---

## Data Summary

| Entity | Count | Description |
|--------|-------|-------------|
| Tree | 1 | "Sample Family Tree" |
| Persons | 75 | Obfuscated names (international), shifted dates (+20 years) |
| Relationships | 54 | Parent-child and spouse relationships |

The data structure preserves the original genealogy tree hierarchy but with:
- Fake names from various locales (EN, DE, FR, IT, ES)
- All dates shifted by +20 years
- Generic location names (Springfield, Riverside, etc.)
- No connection to original Lithuanian family data
