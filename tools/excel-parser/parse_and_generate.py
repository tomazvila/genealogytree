#!/usr/bin/env python3
"""
Parse genealogy data from Excel and generate obfuscated mock data for dev testing.
"""

import zipfile
import xml.etree.ElementTree as ET
import re
import uuid
from datetime import datetime
from faker import Faker
import random
import json

excel_path = "/Users/lilvilla/Programming/Geneinator/GENEALOGINIS MEDIS.xlsx"

# Initialize Faker with multiple locales for varied names
fake = Faker(['en_US', 'de_DE', 'fr_FR', 'it_IT', 'es_ES'])
Faker.seed(42)  # For reproducibility
random.seed(42)

def extract_text_from_excel():
    """Extract all text from Excel drawing shapes."""
    texts = []
    with zipfile.ZipFile(excel_path, 'r') as z:
        content = z.read('xl/drawings/drawing1.xml').decode('utf-8')
        root = ET.fromstring(content)

        for elem in root.iter():
            if elem.text and elem.text.strip():
                tag = elem.tag.split('}')[-1] if '}' in elem.tag else elem.tag
                if tag == 't':
                    texts.append(elem.text.strip())
    return texts


def parse_person_data(texts):
    """Parse raw texts into structured person records."""
    persons = []
    current_text = ""

    # Patterns for extracting data
    date_pattern = r'(\d{4})[\.\-]?(\d{2})?[\.\-]?(\d{2})?'
    age_pattern = r'[Aa]mžius\s*(\d+)\s*m'

    # Join fragmented texts that belong together
    combined_texts = []
    for t in texts:
        # Skip contact info, URLs, and misc notes
        if any(skip in t.lower() for skip in ['http', 'kontaktai', '+370', '.lt', '.html']):
            continue
        combined_texts.append(t)

    # Process texts to extract persons
    i = 0
    while i < len(combined_texts):
        text = combined_texts[i]

        # Try to identify a person entry (name with potential dates)
        # Look for patterns like "Name Surname YYYY" or "Name Surname - Married Name"
        if re.search(r'[A-ZĄČĘĖĮŠŲŪŽ][a-ząčęėįšųūž]+', text):
            # Collect related text fragments
            entry_parts = [text]
            j = i + 1

            # Look ahead for continuation (dates, age, etc.)
            while j < len(combined_texts) and j < i + 5:
                next_text = combined_texts[j]
                # Check if this looks like a continuation (date, age, small fragment)
                if (re.search(date_pattern, next_text) or
                    re.search(age_pattern, next_text) or
                    len(next_text) < 20 or
                    next_text.startswith(('amžius', 'Amžius', '1', '2'))):
                    entry_parts.append(next_text)
                    j += 1
                else:
                    break

            # Combine and parse the entry
            full_entry = ' '.join(entry_parts)

            # Extract dates
            dates = re.findall(date_pattern, full_entry)
            birth_date = None
            death_date = None

            if len(dates) >= 2:
                birth_date = dates[0]
                death_date = dates[1]
            elif len(dates) == 1:
                birth_date = dates[0]

            # Extract name (first part before dates/numbers)
            name_match = re.match(r'^([A-ZĄČĘĖĮŠŲŪŽ][^\d\(\)]+)', full_entry)
            if name_match:
                name = name_match.group(1).strip()
                # Clean up name
                name = re.sub(r'\s*[-–]\s*$', '', name)
                name = re.sub(r'\s+', ' ', name)

                if len(name) > 3 and ' ' in name:  # Only valid names
                    persons.append({
                        'original_name': name,
                        'birth_date': birth_date,
                        'death_date': death_date,
                        'raw_entry': full_entry[:200]
                    })

            i = j
        else:
            i += 1

    return persons


def obfuscate_date(date_tuple, offset_years=None):
    """Obfuscate a date by shifting it randomly."""
    if not date_tuple:
        return None

    year = int(date_tuple[0]) if date_tuple[0] else None
    month = int(date_tuple[1]) if len(date_tuple) > 1 and date_tuple[1] else None
    day = int(date_tuple[2]) if len(date_tuple) > 2 and date_tuple[2] else None

    if year:
        # Shift by offset_years or random amount
        offset = offset_years if offset_years is not None else random.randint(-15, 15)
        year = year + offset

    if month:
        # Slightly adjust month
        month = max(1, min(12, month + random.randint(-2, 2)))

    if day:
        # Slightly adjust day
        day = max(1, min(28, day + random.randint(-5, 5)))

    return (year, month, day)


def generate_fake_name(original_name, gender=None):
    """Generate a fake name preserving structure (maiden name, married name, etc.)."""
    # Detect if there's a maiden name pattern (- or née)
    has_married_name = '-' in original_name or 'ienė' in original_name.lower()

    # Detect gender from Lithuanian suffixes
    if gender is None:
        if any(suffix in original_name.lower() for suffix in ['ienė', 'aitė', 'ytė', 'utė']):
            gender = 'FEMALE'
        elif any(suffix in original_name.lower() for suffix in ['as', 'is', 'us', 'ys']):
            gender = 'MALE'
        else:
            gender = random.choice(['MALE', 'FEMALE'])

    if gender == 'FEMALE':
        first_name = fake.first_name_female()
        surname = fake.last_name()
        if has_married_name:
            maiden_surname = fake.last_name()
            return f"{first_name} {surname}-{maiden_surname}"
        return f"{first_name} {surname}"
    else:
        first_name = fake.first_name_male()
        surname = fake.last_name()
        return f"{first_name} {surname}"


def generate_mock_persons(original_persons, year_offset):
    """Generate mock persons with obfuscated data."""
    mock_persons = []

    for i, person in enumerate(original_persons):
        person_id = str(uuid.uuid4())

        # Detect gender from original name
        gender = 'UNKNOWN'
        name_lower = person['original_name'].lower()
        if any(s in name_lower for s in ['ienė', 'aitė', 'ytė', 'utė', 'ela', 'ona', 'ija']):
            gender = 'FEMALE'
        elif any(s in name_lower for s in ['as', 'is', 'us', 'ys', 'jonas', 'antanas']):
            gender = 'MALE'

        fake_name = generate_fake_name(person['original_name'], gender)

        # Obfuscate dates
        birth_date = obfuscate_date(person.get('birth_date'), year_offset)
        death_date = obfuscate_date(person.get('death_date'), year_offset)

        # Generate biography
        bios = [
            f"Family member documented in historical records.",
            f"Lived in the rural countryside during their lifetime.",
            f"Contributed to the family legacy through several generations.",
            f"Known for their dedication to family traditions.",
            f"Part of a large extended family with deep roots.",
        ]

        mock_person = {
            'id': person_id,
            'full_name': fake_name,
            'gender': gender,
            'birth_year': birth_date[0] if birth_date else None,
            'birth_month': birth_date[1] if birth_date and len(birth_date) > 1 else None,
            'birth_day': birth_date[2] if birth_date and len(birth_date) > 2 else None,
            'death_year': death_date[0] if death_date else None,
            'death_month': death_date[1] if death_date and len(death_date) > 1 else None,
            'death_day': death_date[2] if death_date and len(death_date) > 2 else None,
            'biography': random.choice(bios),
            'location_birth': random.choice(['Springfield', 'Riverside', 'Oakville', 'Maplewood', 'Brookfield']),
            'location_death': random.choice(['Springfield', 'Riverside', 'Oakville', 'Maplewood', 'Brookfield']) if death_date else None,
        }

        mock_persons.append(mock_person)

    return mock_persons


def extract_shapes_and_connectors():
    """Extract shapes (persons) and connectors (relationships) from Excel drawing."""
    NS = {
        'xdr': 'http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing',
        'a': 'http://schemas.openxmlformats.org/drawingml/2006/main'
    }

    shapes = []
    connectors = []

    with zipfile.ZipFile(excel_path, 'r') as z:
        content = z.read('xl/drawings/drawing1.xml').decode('utf-8')
        root = ET.fromstring(content)

        for anchor in root.findall('.//xdr:twoCellAnchor', NS):
            from_elem = anchor.find('xdr:from', NS)
            to_elem = anchor.find('xdr:to', NS)

            if from_elem is None or to_elem is None:
                continue

            from_col = int(from_elem.find('xdr:col', NS).text)
            from_row = int(from_elem.find('xdr:row', NS).text)
            to_col = int(to_elem.find('xdr:col', NS).text)
            to_row = int(to_elem.find('xdr:row', NS).text)

            # Check if it's a connector (relationship arrow)
            cxn = anchor.find('xdr:cxnSp', NS)
            if cxn is not None:
                connectors.append({
                    'from_col': from_col, 'from_row': from_row,
                    'to_col': to_col, 'to_row': to_row
                })
                continue

            # Check if it's a shape with text (person box)
            sp = anchor.find('xdr:sp', NS)
            if sp is not None:
                texts = []
                for t in sp.findall('.//a:t', NS):
                    if t.text:
                        texts.append(t.text)

                if texts:
                    full_text = ''.join(texts).strip()
                    if full_text and len(full_text) > 3:
                        shapes.append({
                            'text': full_text,
                            'from_col': from_col, 'from_row': from_row,
                            'to_col': to_col, 'to_row': to_row,
                            'center_col': (from_col + to_col) / 2,
                            'center_row': (from_row + to_row) / 2
                        })

    return shapes, connectors


def find_closest_shape(shapes, col, row):
    """Find the shape whose bounding box contains or is closest to the given point."""
    best_shape = None
    best_distance = float('inf')

    for shape in shapes:
        # Check if point is inside bounding box
        if (shape['from_col'] <= col <= shape['to_col'] and
            shape['from_row'] <= row <= shape['to_row']):
            return shape

        # Calculate distance to shape center
        dist = ((shape['center_col'] - col) ** 2 + (shape['center_row'] - row) ** 2) ** 0.5
        if dist < best_distance:
            best_distance = dist
            best_shape = shape

    # Only return if reasonably close (within ~10 cells)
    if best_distance < 15:
        return best_shape
    return None


def build_relationships_from_connectors(shapes, connectors, person_lookup):
    """
    Build relationships by matching connectors to shapes.
    Connectors go from parent (lower row) to child (higher row).
    """
    relationships = []

    for conn in connectors:
        # Find shapes at connector endpoints
        # In Excel, lower row number = higher on page = parent
        # The connector 'from' point is typically at the parent, 'to' at the child

        # Try both endpoints and determine parent/child by row position
        shape_at_from = find_closest_shape(shapes, conn['from_col'], conn['from_row'])
        shape_at_to = find_closest_shape(shapes, conn['to_col'], conn['to_row'])

        if shape_at_from is None or shape_at_to is None:
            continue

        if shape_at_from == shape_at_to:
            continue  # Self-connection, skip

        # Determine which is parent (lower row = higher on page = older generation)
        if shape_at_from['center_row'] < shape_at_to['center_row']:
            parent_shape = shape_at_from
            child_shape = shape_at_to
        else:
            parent_shape = shape_at_to
            child_shape = shape_at_from

        # Look up person IDs
        parent_id = person_lookup.get(parent_shape['text'])
        child_id = person_lookup.get(child_shape['text'])

        if parent_id and child_id and parent_id != child_id:
            rel_id = str(uuid.uuid4())
            relationships.append({
                'id': rel_id,
                'person_from_id': parent_id,
                'person_to_id': child_id,
                'relationship_type': 'PARENT'
            })

    # Deduplicate relationships
    seen = set()
    unique_relationships = []
    for rel in relationships:
        key = (rel['person_from_id'], rel['person_to_id'], rel['relationship_type'])
        if key not in seen:
            seen.add(key)
            unique_relationships.append(rel)

    return unique_relationships


def generate_sql_migration(persons, relationships, tree_id, tree_name):
    """Generate SQL migration file for seeding mock data."""

    sql_parts = []

    # Generate a mock user UUID for created_by fields
    mock_user_id = str(uuid.uuid4())

    sql_parts.append(f"""-- Mock Data Migration for Geneinator Dev Testing
-- Generated from obfuscated genealogy data
-- WARNING: This is fake data for testing only - no real personal information

-- Create a mock user for the created_by fields
-- Note: This user should exist before running this migration, or use an existing test user
-- You may need to run: INSERT INTO users (id, email, password_hash, display_name, role, status, created_at, updated_at)
-- VALUES ('{mock_user_id}', 'mockuser@test.local', '$2a$10$dummyhash', 'Mock User', 'USER', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- If you need to create the user, uncomment the line below:
-- INSERT INTO users (id, email, password_hash, display_name, role, status, created_at, updated_at)
-- VALUES ('{mock_user_id}', 'mockuser@test.local', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'Mock User', 'USER', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
-- ON CONFLICT (email) DO UPDATE SET id = EXCLUDED.id;

-- Alternatively, use this to get an existing user ID:
-- DO $$
-- DECLARE mock_user_id UUID;
-- BEGIN
--     SELECT id INTO mock_user_id FROM users LIMIT 1;
--     -- Then use mock_user_id in subsequent inserts
-- END $$;

-- Tree creation
INSERT INTO trees (id, name, description, created_by, is_mergeable, created_at, updated_at)
VALUES (
    '{tree_id}',
    '{tree_name}',
    'Mock genealogy tree for development and testing purposes',
    '{mock_user_id}',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Persons
""")

    for p in persons:
        # Handle NULL values properly
        birth_year = f"{p['birth_year']}" if p.get('birth_year') else 'NULL'
        birth_month = f"{p['birth_month']}" if p.get('birth_month') else 'NULL'
        birth_day = f"{p['birth_day']}" if p.get('birth_day') else 'NULL'
        death_year = f"{p['death_year']}" if p.get('death_year') else 'NULL'
        death_month = f"{p['death_month']}" if p.get('death_month') else 'NULL'
        death_day = f"{p['death_day']}" if p.get('death_day') else 'NULL'
        loc_birth = f"'{p['location_birth']}'" if p.get('location_birth') else 'NULL'
        loc_death = f"'{p['location_death']}'" if p.get('location_death') else 'NULL'
        biography = p.get('biography', '').replace("'", "''")

        sql_parts.append(f"""INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '{p['id']}',
    '{p['full_name'].replace("'", "''")}',
    '{p['gender']}',
    {birth_year}, {birth_month}, {birth_day}, false,
    {death_year}, {death_month}, {death_day}, false,
    '{biography}',
    {loc_birth},
    {loc_death},
    '{tree_id}',
    '{mock_user_id}',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
""")

    sql_parts.append("\n-- Relationships\n")

    for r in relationships:
        start_year = f"{r['start_year']}" if r.get('start_year') else 'NULL'
        is_divorced = 'false'

        sql_parts.append(f"""INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '{r['id']}',
    '{r['person_from_id']}',
    '{r['person_to_id']}',
    '{r['relationship_type']}',
    {start_year},
    {is_divorced},
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
""")

    # Update tree root person
    if persons:
        # Find oldest person as root
        oldest = min(persons, key=lambda x: x.get('birth_year') or 9999)
        sql_parts.append(f"""
-- Set tree root person
UPDATE trees SET root_person_id = '{oldest['id']}' WHERE id = '{tree_id}';
""")

    return '\n'.join(sql_parts)


def main():
    print("Extracting shapes and connectors from Excel...")
    shapes, connectors = extract_shapes_and_connectors()
    print(f"Found {len(shapes)} shapes (persons)")
    print(f"Found {len(connectors)} connectors (relationships)")

    print("\nParsing person data from shapes...")
    # Convert shapes to person records for parsing
    persons = []
    for shape in shapes:
        text = shape['text']
        # Extract dates from text
        date_pattern = r'(\d{4})[\.\-]?(\d{2})?[\.\-]?(\d{2})?'
        dates = re.findall(date_pattern, text)
        birth_date = dates[0] if len(dates) >= 1 else None
        death_date = dates[1] if len(dates) >= 2 else None

        # Extract name (text before dates)
        name_match = re.match(r'^([A-ZĄČĘĖĮŠŲŪŽ][^\d\(\)]+)', text)
        if name_match:
            name = name_match.group(1).strip()
            name = re.sub(r'\s*[-–]\s*$', '', name)
            name = re.sub(r'\s+', ' ', name)

            if len(name) > 3:
                persons.append({
                    'original_name': name,
                    'original_text': text,  # Keep full text for matching
                    'birth_date': birth_date,
                    'death_date': death_date,
                })

    print(f"Extracted {len(persons)} person records")

    # Show sample
    print("\nSample extracted records:")
    for p in persons[:5]:
        print(f"  - {p['original_name']}")

    print("\nGenerating obfuscated mock data...")
    year_offset = random.randint(-20, 20)
    mock_persons = generate_mock_persons(persons, year_offset)
    print(f"Generated {len(mock_persons)} mock persons")

    # Build lookup from original text to person ID for relationship matching
    print("\nBuilding relationship lookup...")
    person_lookup = {}
    for i, person in enumerate(persons):
        if i < len(mock_persons):
            # Map original shape text to mock person ID
            original_text = person.get('original_text', person['original_name'])
            person_lookup[original_text] = mock_persons[i]['id']
            # Also map by original name
            person_lookup[person['original_name']] = mock_persons[i]['id']

    # Also build lookup from shape text directly
    for shape in shapes:
        # Find matching person by checking if shape text starts with any person's name
        for person in persons:
            if shape['text'].startswith(person['original_name'][:20]):
                if person.get('original_text') in person_lookup:
                    person_lookup[shape['text']] = person_lookup[person['original_text']]
                    break

    print("\nExtracting relationships from connectors...")
    relationships = build_relationships_from_connectors(shapes, connectors, person_lookup)
    print(f"Extracted {len(relationships)} relationships from Excel connectors")

    # Show relationship stats
    parent_count = {}
    for rel in relationships:
        parent_id = rel['person_from_id']
        parent_count[parent_id] = parent_count.get(parent_id, 0) + 1

    if parent_count:
        max_children = max(parent_count.values())
        print(f"Max children per parent: {max_children}")

    print("\nGenerating SQL migration...")
    tree_id = str(uuid.uuid4())
    tree_name = "Sample Family Tree"
    sql = generate_sql_migration(mock_persons, relationships, tree_id, tree_name)

    # Write migration file
    output_path = "/Users/lilvilla/Programming/Geneinator/backend/src/main/resources/db/seed/V999__mock_genealogy_data.sql"
    import os
    os.makedirs(os.path.dirname(output_path), exist_ok=True)

    with open(output_path, 'w', encoding='utf-8') as f:
        f.write(sql)

    print(f"\nMigration written to: {output_path}")
    print(f"Total persons: {len(mock_persons)}")
    print(f"Total relationships: {len(relationships)}")

    # Summary
    summary = {
        'tree_id': tree_id,
        'tree_name': tree_name,
        'person_count': len(mock_persons),
        'relationship_count': len(relationships),
        'year_offset_applied': year_offset,
    }

    print("\nSummary:")
    print(json.dumps(summary, indent=2))


if __name__ == '__main__':
    main()
