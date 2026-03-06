-- Mock Data Migration for Geneinator Dev Testing
-- Generated from obfuscated genealogy data
-- WARNING: This is fake data for testing only - no real personal information

-- Create a mock user for the created_by fields
-- Note: This user should exist before running this migration, or use an existing test user
-- You may need to run: INSERT INTO users (id, email, password_hash, display_name, role, status, created_at, updated_at)
-- VALUES ('627b55c9-971d-4223-bc9d-d042304538f7', 'mockuser@test.local', '$2a$10$dummyhash', 'Mock User', 'USER', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- If you need to create the user, uncomment the line below:
-- INSERT INTO users (id, email, password_hash, display_name, role, status, created_at, updated_at)
-- VALUES ('627b55c9-971d-4223-bc9d-d042304538f7', 'mockuser@test.local', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'Mock User', 'USER', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
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
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    'Sample Family Tree',
    'Mock genealogy tree for development and testing purposes',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Persons

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'fbe8580a-e7d6-4a8d-8cae-3d6f5d510e50',
    'Allison Moulin-Etzold',
    'FEMALE',
    1963, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'c19c9372-5e92-4ac1-bcf4-0bb3d8a4c03d',
    'Michael Bonet',
    'UNKNOWN',
    1904, 3, 4, false,
    1982, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Riverside',
    'Springfield',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '4a073518-a9f0-407e-b9ed-e15ee89391e1',
    'Héctor Johnson',
    'MALE',
    1898, 12, 28, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'c2d5865e-91cc-4b2f-98b6-abb3203f4467',
    'Eric Amor',
    'MALE',
    1892, 12, 2, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '54342115-e009-4a2c-9296-5497dbd31602',
    'Chita Rivas',
    'FEMALE',
    2007, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '409dce92-c18f-4990-b58e-ae6e1200a61a',
    'Ingrid Lobo',
    'FEMALE',
    1941, NULL, NULL, false,
    2033, 11, 16, false,
    'Part of a large extended family with deep roots.',
    'Riverside',
    'Brookfield',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '104c4808-9524-4d86-ae14-e27002ece963',
    'Olivia Thanel-Morpurgo',
    'FEMALE',
    1932, NULL, NULL, false,
    2023, 9, 17, false,
    'Known for their dedication to family traditions.',
    'Brookfield',
    'Oakville',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '93a7f71a-e5fb-4b03-bf26-04acd4f23866',
    'Colette Wiek-Henry',
    'FEMALE',
    1932, 12, 1, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'c923a5a8-e697-430a-a1ec-25206af0123c',
    'James Barre',
    'MALE',
    1930, NULL, NULL, false,
    1990, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Riverside',
    'Riverside',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '87690f3c-4fd4-4025-b287-bcec29b5beb6',
    'Eligio Blair',
    'MALE',
    1939, 4, 2, false,
    2036, 9, 28, false,
    'Family member documented in historical records.',
    'Oakville',
    'Oakville',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'fec62ce3-d1fc-46ff-a63f-875bf66d7ba7',
    'Beatriz Bosurgi',
    'FEMALE',
    1945, NULL, NULL, false,
    2028, 3, 21, false,
    'Family member documented in historical records.',
    'Maplewood',
    'Brookfield',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'e10f20cc-3f1a-4c86-b148-c648834ac770',
    'Eugenio Pardo',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'de25f924-c495-41eb-aa65-e9fcbc979d1f',
    'Celia Williams-Wirth',
    'FEMALE',
    1972, 2, 28, false,
    2032, 1, 23, false,
    'Part of a large extended family with deep roots.',
    'Oakville',
    'Brookfield',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '458627d8-ec25-468e-9468-e8c2bf519fba',
    'Augustin Ditschlerin',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'e97bda3d-b925-40c9-9151-28782c995795',
    'Gustavo Ossani',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '41773f74-50a0-4f53-9692-16a82efdc046',
    'Hans-Georg Lacroix',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'b14b0148-80b8-41ac-83e2-94c515c0450d',
    'Arnaude Donoso',
    'FEMALE',
    1971, 4, 14, false,
    2034, 5, 17, false,
    'Known for their dedication to family traditions.',
    'Oakville',
    'Riverside',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '72f48577-d40e-4113-9b5b-d25973a2acaf',
    'Pastora Fröhlich-Parisi',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '18290f53-00f4-42bd-8795-b204ebc311e9',
    'Timothée Gimenez',
    'MALE',
    1951, 10, 12, false,
    2018, 4, 17, false,
    'Family member documented in historical records.',
    'Brookfield',
    'Riverside',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '953abda4-3c17-4d23-8963-3c6c82e8cd99',
    'Antoine Beckmann',
    'MALE',
    1958, 3, 1, false,
    2025, 11, 15, false,
    'Known for their dedication to family traditions.',
    'Oakville',
    'Brookfield',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '2b3fba29-fdc4-48e7-aadd-083a71e28495',
    'Luc François',
    'UNKNOWN',
    1953, 7, 25, false,
    2044, 3, 1, false,
    'Lived in the rural countryside during their lifetime.',
    'Springfield',
    'Oakville',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '48f96090-21ad-406f-99a9-b5dbe4983cc2',
    'Traude Miranda-Sölzer',
    'FEMALE',
    1964, 4, 14, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'c5639fc8-4b18-4709-855b-648758be64d0',
    'Paolo Cocci',
    'MALE',
    1956, 11, 20, false,
    2016, 5, 27, false,
    'Known for their dedication to family traditions.',
    'Maplewood',
    'Maplewood',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'e557f969-3783-4705-808f-c935575ec228',
    'Christine Weinhage',
    'FEMALE',
    1965, 1, 16, false,
    2043, 3, 18, false,
    'Part of a large extended family with deep roots.',
    'Brookfield',
    'Oakville',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '10a8db70-a90c-47df-a80f-ba651ba12ed7',
    'Julio César Letellier',
    'MALE',
    1923, NULL, NULL, false,
    2001, 3, 5, false,
    'Part of a large extended family with deep roots.',
    'Maplewood',
    'Oakville',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'b04284ec-5ce0-40ce-a9fd-6fa4b2db9c37',
    'Inés Pujol-Vasseur',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '531da4cb-2a9b-4b5f-be61-236fdc86b6cf',
    'Jaroslaw Burcardo',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '3c57f365-e7ab-4a2a-9c03-38741818e6ed',
    'Shannon Schottin',
    'FEMALE',
    1936, 7, 15, false,
    2021, 7, 17, false,
    'Lived in the rural countryside during their lifetime.',
    'Maplewood',
    'Brookfield',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '24cfe174-425a-4b14-87d3-1f9707a7ab24',
    'Reingard Boccaccio',
    'FEMALE',
    1931, 3, NULL, false,
    2022, 7, 19, false,
    'Part of a large extended family with deep roots.',
    'Maplewood',
    'Brookfield',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '76512bd6-9507-4953-846a-bb72fb1376af',
    'Orazio Solé',
    'MALE',
    1959, NULL, NULL, false,
    2017, 5, 15, false,
    'Family member documented in historical records.',
    'Springfield',
    'Brookfield',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '05b2de5b-7917-4425-ad36-eaf45e7173a3',
    'Edgardo Alberdi',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '1802746d-5973-4c23-8342-0b1b34d3204a',
    'Melanie Lasa-Marie',
    'FEMALE',
    1960, 5, 1, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '7d263d7f-eada-4fcf-a0ca-3a4f0668d8d6',
    'Martin Metz',
    'MALE',
    1959, 4, 22, false,
    2043, 12, 13, false,
    'Lived in the rural countryside during their lifetime.',
    'Brookfield',
    'Springfield',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'e9829f61-161f-40f0-8f34-16b6fcd1d235',
    'Tiffany Charrier',
    'FEMALE',
    1935, 4, 14, false,
    1979, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Brookfield',
    'Riverside',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'd2453f39-7186-4f1e-9e89-04f27996a16c',
    'Jolanthe Harrell',
    'FEMALE',
    1966, 7, 13, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '7bcde851-bf09-4816-9dd7-fb26d05c7ae1',
    'Claude Flor',
    'FEMALE',
    1975, 12, 1, false,
    6167, 12, 28, false,
    'Known for their dedication to family traditions.',
    'Springfield',
    'Springfield',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'bbc7076a-9a92-40d5-a7df-1799941fae15',
    'Gunhild Reuter',
    'FEMALE',
    1994, 12, 1, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '50f62bc8-d75a-48d7-aae8-c8e347aa1d36',
    'Ágata George',
    'FEMALE',
    1999, 6, 28, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'b4b00f2b-fa03-4ef4-b2ff-d8bcb0ddaed7',
    'Alessia Hickman-Dupuis',
    'FEMALE',
    1969, 8, 18, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '8ef8fba3-e642-4702-843f-94e630db651d',
    'Cynthia Bermúdez-Dennis',
    'FEMALE',
    1981, 2, 25, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '3722c981-39be-47f2-903b-a9777af4b87f',
    'Vanessa Casas-Sorgatz',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'eb403945-b76a-4d65-9361-a35d77e4c150',
    'Paloma Hettner',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'edee1a40-3feb-499e-be05-b6f00f4eb2a1',
    'Trini Rocha-Reuter',
    'FEMALE',
    1982, 4, 10, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '88f646be-7ab3-4597-87cf-bfe515eb50a2',
    'Olaf Alexandre',
    'MALE',
    1981, 4, 6, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'b45c70cf-06fb-4d64-9409-7efcee7f01a8',
    'Sabine Sanmiguel',
    'FEMALE',
    1982, 8, 21, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'e25fbf82-40d1-48ee-8604-b690b5f018ea',
    'Danielle Morgan-Grau',
    'FEMALE',
    1983, 5, 16, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '931019da-6484-4e0b-9789-52a02a9b360f',
    'Constanza Jones-Perez',
    'FEMALE',
    1946, 10, 18, false,
    2039, 7, 28, false,
    'Lived in the rural countryside during their lifetime.',
    'Springfield',
    'Springfield',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '9ae1a3ab-2955-45cd-af3f-c3de26a06de7',
    'Shannon Palmer-Laroche',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'd6cbc34c-9be8-49cf-ab50-c8033e1c7270',
    'Eva Catalán-Suárez',
    'FEMALE',
    1998, 1, 12, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'f6d3e2e0-9470-43e8-aa70-e8cdf04d8d3a',
    'Raisa Pisaroni',
    'FEMALE',
    2026, 7, 20, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'aecde14a-7d56-40a8-972f-91275acebc2f',
    'Antonietta Hopkins',
    'FEMALE',
    2029, 9, 5, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '526302ae-903b-4f23-ac90-4c4d14d979ad',
    'Pauline Schiavone',
    'FEMALE',
    2034, 6, 24, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '268faa82-fbaa-4f8b-b052-a0e81c7f816c',
    'Megan Brown',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '79922913-b309-4b87-ad93-eaac851fc7c7',
    'Bernard Gröttner',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '38d95258-6e66-48a7-979e-fee9c31f6c54',
    'Kathrin Cimarosa',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'c9610dd4-6ac9-4c24-905d-54d1b85b5236',
    'Elena Pinto-Bruder',
    'FEMALE',
    1941, 12, 28, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '6851b222-f869-48e5-be99-0f9c8d60258e',
    'Paloma Williams-Alcolea',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'dac11546-7fbc-484d-9297-d8a45330f5d1',
    'Tammy Geisel-Spanevello',
    'FEMALE',
    1973, 11, 26, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '5aa32cbf-417a-4399-91a8-b1190276bc27',
    'Giampaolo Biagiotti',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'ef281de1-cf4c-48f9-977f-cdb6c803f20d',
    'Hans-Herbert Galloway',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '10f52d73-f90b-469f-990b-62d15c2b009d',
    'Michel Briand',
    'UNKNOWN',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'e1be30a0-5599-4a30-9482-10e86f343f99',
    'Natalio Costanzi',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '50222f3a-4d7f-49d0-b0be-a013cba966fc',
    'Fabienne Bohlander',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '383d9852-75c8-40b3-b7ce-9571857d3181',
    'Paulina Barrera',
    'FEMALE',
    2000, 1, 24, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'cec666c8-facc-4e32-bc7c-fcb793d8b4b9',
    'Andrée Silva',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'd26914f8-5298-4a7f-9b46-bdb46813ab43',
    'Ángel Criado',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'a219a3e7-f59d-4603-8a11-9b65ae93c575',
    'Tonya York',
    'FEMALE',
    2004, 5, 25, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'ac119086-e7fe-4d85-9631-8a6a4453ddb8',
    'Linda Kusch',
    'FEMALE',
    1914, 5, 21, false,
    1997, 3, 1, false,
    'Lived in the rural countryside during their lifetime.',
    'Maplewood',
    'Springfield',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '0ca66fe0-9dc6-4094-81b7-c219ff57a082',
    'Teresa Hoz-Ariño',
    'FEMALE',
    1840, 12, 1, false,
    1885, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Oakville',
    'Maplewood',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '1d1abe8a-f03b-4afd-a493-ffb5d362b886',
    'Anita Zetticci',
    'FEMALE',
    1838, 12, 28, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '77ca0ee5-36a9-4a7f-a61d-b911befeba92',
    'Fausto Martins',
    'MALE',
    1869, 12, 28, false,
    1885, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Springfield',
    'Brookfield',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '742577a4-37ec-4a26-ba09-b2ece921dcb1',
    'Georges Delaunay',
    'MALE',
    1898, 12, 28, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '2b27412b-066b-4d08-9fd8-562233349c04',
    'Marie-Therese Albert',
    'FEMALE',
    1944, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'ce0ba3d7-685d-402f-8897-6f544211453d',
    'Arnaude Ellis',
    'FEMALE',
    1942, 12, 28, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'd3ac62f1-6802-45e9-b8c8-2942fc5e8d8a',
    'Armida Medina',
    'FEMALE',
    1970, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'b1530916-ac27-48ab-8416-5e5ea1a50aa2',
    'Dulce Keudel-Suarez',
    'FEMALE',
    1967, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'cc97d505-79f5-4a6b-a98c-96473e2decce',
    'Guy Mangold',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '1b2e1f93-8035-43b5-b10e-158e91479225',
    'Pastor Raymond',
    'MALE',
    2020, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '1a041264-aa85-4b17-a65f-434f1f46ccf8',
    'Adalberto Marí',
    'MALE',
    1990, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'ade0c58d-9d1b-4474-9f67-6bed75905e98',
    'Todd Hettner',
    'MALE',
    2023, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'b9b52246-9974-4cea-9c4e-f305c06f6f29',
    'Steve Cortés',
    'UNKNOWN',
    2030, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '4363f8f3-7e9a-469e-856f-3a7e578876f7',
    'Lorraine Galvez-Martins',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '7217984b-6a1d-45c2-b9e4-e6e08e7fc461',
    'Philippe Gregorio',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '6fe16cc8-736b-4df9-a78f-59203485333e',
    'Anna Lercari',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '67a87b6d-f560-45f3-8cc2-174375469d74',
    'April Schweitzer',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'd74cf487-9b6f-4e55-a1a7-de43a29c7574',
    'Édouard Villadicani',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '525ce00a-76b0-4c17-8cd3-1c8e86a8e031',
    'Agnolo Jones',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '18f2a5a0-a430-45bb-9eb2-886453ea6894',
    'Jörn Davenport',
    'MALE',
    1730, 12, 28, false,
    1782, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Oakville',
    'Maplewood',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'aefbbb8d-69cd-4a13-9d08-9b386507a31e',
    'Mamen Losekann',
    'FEMALE',
    1797, 12, 28, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'b6649be2-f2df-4ae4-87bd-ecc4d56b3e8e',
    'Aneta Marchal',
    'FEMALE',
    1759, 12, 28, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '7e5cfbfc-9e2b-4e23-82b5-5127ef64986f',
    'Steven Stoll',
    'MALE',
    1677, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'fa880b07-c148-4f9d-974c-63071b38fd39',
    'Marica Gomez-Ramos',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '36fb8657-b023-41b1-a72e-b72c19745c0e',
    'Pilar Hellwig',
    'FEMALE',
    1741, 12, 28, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '888bf1f4-9b41-411e-8595-e9bdf5440f5d',
    'Zenta Lehmann',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '2a997840-efae-4959-a424-6be9c8ffe030',
    'Christopher Bouchet',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'c0a69c55-4189-49bc-975f-f90779615b92',
    'Frank-Michael Zirme',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '40db7f5e-2313-4699-9af1-5555726cce2d',
    'Janet Garrison-Gute',
    'FEMALE',
    1976, 12, 21, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '11553aef-5e1a-42a4-a727-21e88baa1dc6',
    'Maurizio Dumas',
    'MALE',
    1998, 9, 11, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'e94724d8-968b-4d35-ae9b-ad89f8890daa',
    'Alicja Kuhl-Lopes',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'a33bc05d-e505-4a9c-94c2-fc873cb60e82',
    'Tracy Marty',
    'FEMALE',
    2030, 8, 20, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '95d12b5d-6b88-486d-aebd-51f0b369861e',
    'Gloria Muñoz',
    'FEMALE',
    2034, 8, 14, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '4330a183-6d1b-4dbd-8267-53bd8d16f995',
    'Brian Colin',
    'MALE',
    2040, 1, 16, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '51e75f01-9b59-4cee-8c3d-e76adfc79a31',
    'Dulce Rodriguez',
    'FEMALE',
    1996, 3, 11, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'cffa53ef-58fc-404c-aa50-6a791e3a00a0',
    'Matteo Nguyen',
    'MALE',
    1985, 6, 20, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '3b453297-79e2-40a0-a3f6-e234a91d866b',
    'José Mari Cruz',
    'UNKNOWN',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'a8e83a0d-7699-44dd-aa43-81a389e95823',
    'Claudio Bertrand',
    'UNKNOWN',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '71df2a19-208e-4299-af7b-437cb00e5ecd',
    'Achille Figueras',
    'UNKNOWN',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'b9ae0b06-19fd-426a-bce9-2d25670c2229',
    'Martin Rush',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '99e77720-95ee-4f78-9b29-65db492e3366',
    'Margaux Cortez-Guillou',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'b1c28ca8-bd8c-43aa-900d-98bb39713a22',
    'Odette Antoine-Lenoir',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '6d6dc7d0-78ce-4325-9ca8-3c950b32e542',
    'Drago Parini',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'a093f024-0f56-4436-b043-086a76297309',
    'Leopold Mascaró',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '457c5bad-6758-488d-ac1c-f1f7330d37a8',
    'Paloma Torres',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '4f3acd28-48d1-4c37-a9e6-f4876c23cc3a',
    'Hulda Montserrat',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'fb427193-b9f5-47ab-96c4-b12231647440',
    'Rembrandt Fuseli',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '415a8d0f-586b-40a2-9787-92905dd77d4e',
    'Jordi Sorgatz',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'b47eaa3e-5e57-473b-96b3-2789b2677c38',
    'Diane Naranjo',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'fe7750a7-f3a0-4c9d-b44f-a7051de2b457',
    'Robert Steinberg',
    'MALE',
    1968, 4, 28, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '320f6593-a484-4175-a48b-90d0b4b8129f',
    'Frédéric Finke',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '3910a11e-c28e-44fb-96b4-2489623ee3bb',
    'Angel Bonomo',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '79d3ea39-57ff-4e5b-b5aa-ff42739d30c1',
    'Pierluigi Ferreras',
    'MALE',
    1989, 5, 15, false,
    2037, 7, 10, false,
    'Family member documented in historical records.',
    'Riverside',
    'Oakville',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '0d192516-7866-4daf-8b8a-5ec14f8f5528',
    'Melissa Gori',
    'FEMALE',
    2025, 12, 13, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '2c07aa06-e225-452c-aac8-53a3a7dd72de',
    'Matthias Stafford',
    'MALE',
    2029, 9, 18, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'b5c74970-bca6-4411-9d45-5a27bc7657b0',
    'Richard Gierschner',
    'MALE',
    2009, 9, 15, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '0f8e286d-7e92-44a9-a438-bf12ab01029f',
    'Jordana Grant',
    'FEMALE',
    1939, 12, 9, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '76c2d222-a0ae-48c6-a6e3-09ef028886f9',
    'Joyce Turchi',
    'FEMALE',
    2006, 6, 28, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'a382259a-3ee4-4e93-8cb2-d0122c36ecae',
    'Martirio Faivre-Tiepolo',
    'FEMALE',
    2007, 9, 1, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'ad47ddb6-3a37-49bd-9a47-af04c82c9eb7',
    'Riccardo Weimer',
    'MALE',
    2006, 5, 6, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '393de9b1-24ed-4338-b1b2-5475d28a21d6',
    'Maria Hamel',
    'FEMALE',
    2005, 6, 19, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'ceba38af-e14b-4ff7-8f4c-fe3051fc8286',
    'Sibel Gosselin',
    'FEMALE',
    2004, 2, 7, false,
    2045, 5, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Springfield',
    'Springfield',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '861bf988-7207-44e0-8557-d8529b07336a',
    'Jose Carlos Höfig',
    'MALE',
    2014, 3, 25, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'd4674790-6956-41e8-881b-75771efb3545',
    'Beppe Huet',
    'MALE',
    1949, 11, NULL, false,
    2035, 5, 27, false,
    'Family member documented in historical records.',
    'Maplewood',
    'Brookfield',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'd0e88b87-c36e-4c33-8688-541ed4beb668',
    'Jacques Gimenez',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '62c31389-3265-4a10-92f3-c4ad7382a96d',
    'Brenda Kraus',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '074da226-d044-4bb6-8380-a88589ccf21a',
    'Nikolas Bolnbach',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '15cc60e2-f5b9-4398-b467-8f19c430c7b0',
    'Pier Allard',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '5157f7d0-3bde-4e3c-82c1-23c154db5075',
    'Pierluigi Clark',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '534a0aec-fc40-4c15-a441-82d4aa94ce60',
    'Lina Aller-Pintor',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '384eebe9-aa2b-49d1-8e99-2184604d93c9',
    'Adriano Léger',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '5ab6176b-6291-4aeb-ad9d-82eceec30454',
    'Piero Giménez',
    'MALE',
    1909, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '838cc8e2-ab32-462b-8346-0ed7d54d80dc',
    'Giancarlo Nguyen',
    'MALE',
    1922, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '7ebcf8e2-154d-42f4-9a69-950e3ec47ee5',
    'Alphons Morandi',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'f55e921e-d4a4-4ce7-959b-d61aa919037f',
    'Viridiana Mosemann',
    'FEMALE',
    1909, NULL, NULL, false,
    1995, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Oakville',
    'Brookfield',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '273c06e8-286a-42e5-891a-93f290cd5bcd',
    'Luciano Amores',
    'MALE',
    1941, NULL, NULL, false,
    1985, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Riverside',
    'Maplewood',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '9e06b78d-ac85-48bb-a6e7-2f3fb4d7ac84',
    'Alfredo Day',
    'MALE',
    1947, NULL, NULL, false,
    2020, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Riverside',
    'Brookfield',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '82d9a820-563e-4682-8337-a9cfe0cb7025',
    'Sven Faggiani',
    'MALE',
    1954, NULL, NULL, false,
    2005, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Oakville',
    'Maplewood',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'daa0b287-c06d-48ef-98d1-4116d5f5b844',
    'David Lecomte',
    'MALE',
    1981, NULL, NULL, false,
    1982, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Springfield',
    'Oakville',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'a852acd5-56cd-4f14-9abe-bc9b8df41316',
    'Ilaria Samson',
    'FEMALE',
    1978, NULL, NULL, false,
    1984, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Riverside',
    'Maplewood',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '70cd9e13-5bba-4f9e-9f89-1ea53d2581ab',
    'Gustavo Grifeo',
    'MALE',
    1978, NULL, NULL, false,
    1980, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Brookfield',
    'Oakville',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '4a00815d-7b4f-4118-b4ec-f436020f7aaa',
    'Julie Azorin',
    'FEMALE',
    1923, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '59178c74-12a8-4ab7-9722-dc0b40d593d6',
    'Frédéric Harrington',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '6fcb0a87-a504-457d-996e-c374213e2415',
    'Nicole Gute',
    'FEMALE',
    1924, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '5a4c80d9-00f9-47d0-b926-702a843bafe2',
    'Marc Eberth',
    'MALE',
    1850, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '408b42f1-c145-4a62-b59c-d611ec842f56',
    'Susanna Elliott-Mohaupt',
    'FEMALE',
    1849, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '450db307-56ba-4356-98b6-e69ad7b99a83',
    'Julien auch Schlauchin',
    'MALE',
    1851, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'aac55ab7-0bd7-445f-a64c-8b2e8983d2ac',
    'Raúl Strickland',
    'MALE',
    1890, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'f4eaca6d-c93e-4c45-9ed7-e55dc836b873',
    'Wiebke Nicolas-Amigó',
    'FEMALE',
    1892, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '10c880e4-c3f9-4f2f-b1b4-2a0fc5f419ac',
    'Sébastien Petrucci',
    'MALE',
    1900, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '2595a0bf-b519-4698-8c74-f6d447cc4bf7',
    'Arnfried Herrero',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '74b9b9a1-0e10-4df7-9ec8-3f143f9486ed',
    'Melissa Pottier',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '9921c1c6-1631-4783-9860-bc2f2420df5a',
    'Samuel Hogan',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'c3878897-c55c-4882-8824-2936f334734e',
    'Rosendo Reed',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '40ba16cb-b637-4e4e-9b2a-1ee2d6de90b5',
    'Napoleone Hunter',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '413656b6-8608-4e1b-95ca-d9c9d5b2125b',
    'Aimé Stoppani',
    'MALE',
    2011, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '19f7cb42-03f6-426e-9477-69eb9801726b',
    'Jessica Bousquet-Correr',
    'FEMALE',
    2010, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '31b7f3b1-d368-4baf-9d8e-ef5f2cd5a421',
    'Eleanora León',
    'FEMALE',
    2021, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'faee7857-08b4-4032-acc2-eb663a3c6b3c',
    'Roxana Tomás',
    'FEMALE',
    2039, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '4d874eb5-836e-485b-8709-ffba71a462af',
    'Raffaello Ledesma',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'dce9a0ee-781f-4eef-ba8f-c5addefa948d',
    'Gérard Ramirez',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '512d53ca-895b-48be-88a3-f8f6ae1a5031',
    'Gioachino Parini',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '7ad45e3b-bfa6-48f2-8920-ff25bca9524c',
    'Alix Modiano',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Springfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'f579378a-43b6-4ed6-9eef-12e04e36e197',
    'Janine Klapp-Leblanc',
    'FEMALE',
    1998, 6, 14, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '57202f0a-6ddd-4e12-b770-7b66c8dfc305',
    'Laure Agustín-Feijoo',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '0fd868fa-8329-4af1-a3e7-e5f08ce6523f',
    'David Staglieno',
    'MALE',
    1992, 5, 24, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'ee7d5011-cea0-4928-9b5c-3099e6f29c40',
    'Purificación Stumpf',
    'FEMALE',
    2032, 12, 9, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '963f21f5-44c5-463f-9888-b82b89aed0ca',
    'Silvestro Antonelli',
    'MALE',
    2023, 3, 21, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '4372ab5e-4c9b-486e-b41c-3fddbd3bec85',
    'Elizabeth Gonzaga',
    'FEMALE',
    6167, 12, 28, false,
    6841, 12, 28, false,
    'Lived in the rural countryside during their lifetime.',
    'Oakville',
    'Maplewood',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '79aab300-fd21-44df-9516-fe8f919c9425',
    'Vincent Peinado',
    'MALE',
    1922, NULL, NULL, false,
    2006, NULL, NULL, false,
    'Family member documented in historical records.',
    'Oakville',
    'Riverside',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'f01232cd-517d-426d-ac28-cf00eb2fed13',
    'Paola Núñez-Vigliotti',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '3044a852-f761-41fc-8541-b664673c9735',
    'Joseph Juvara',
    'UNKNOWN',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '65052546-8367-4972-a81a-8f858abd18b4',
    'Jeannine Dussen van-Jopich',
    'FEMALE',
    1939, NULL, NULL, false,
    2035, NULL, NULL, false,
    'Family member documented in historical records.',
    'Riverside',
    'Riverside',
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '57442c52-8846-4434-94ec-15ce6a7aaa16',
    'Patrick Gröttner',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '7b1d1cf3-49f1-45ee-86fa-d0574f8c60fc',
    'Nadja Lercari-Zamorano',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Riverside',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '56545826-90fe-40df-b925-3e66633a85b3',
    'Epifanio Wall',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '9cd36ae2-5503-44f9-b110-be42dde2f17b',
    'Gabriel Colin',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '4807fea8-f9c7-47eb-980c-66940813585a',
    'Aimé Castañeda',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'a6b75a10-c364-48ae-a1ae-cd2466ef0b76',
    'Alphonse Tran',
    'MALE',
    1887, 12, 21, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'f94462d7-e8c6-48b6-b225-dc224c59c769',
    'Marco Camilleri',
    'UNKNOWN',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '44bac9f9-425e-4b2f-b084-e7f33f4b3858',
    'Kenneth Soderini',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'af242ba4-0682-4242-8f77-01d192f97734',
    'Isa Birnbaum',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '2b11e17c-9d02-4966-911a-c7dc500808b2',
    'Olivier Short',
    'UNKNOWN',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '14f3ab39-5e4b-4400-b4ca-771606728458',
    'Melina Ray-Hernandez',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Contributed to the family legacy through several generations.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '69b49f21-8925-4a2e-81e1-7c5613464fe7',
    'Evaristo Mcknight',
    'MALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '9c3c7d6f-9e51-4c1d-937c-feb77a4da4e9',
    'Kristin Gelli',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Part of a large extended family with deep roots.',
    'Brookfield',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    'a584c31d-74da-4cc0-a181-c3495df14a93',
    'Fortunata Rubbia',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '6820467f-653f-40fc-be61-b7556b5cd5b9',
    'Pénélope Colas-Barrena',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Lived in the rural countryside during their lifetime.',
    'Oakville',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '21fae58f-4caa-40b7-98cc-fdd083b46aa5',
    'Pepita Meza-Schomber',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Known for their dedication to family traditions.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO persons (id, full_name, gender, birth_year, birth_month, birth_day, birth_is_approximate, death_year, death_month, death_day, death_is_approximate, biography, location_birth, location_death, tree_id, created_by, created_at, updated_at)
VALUES (
    '6f69ad78-2300-463e-b134-c2d9c406e13a',
    'Elsa Moore',
    'FEMALE',
    NULL, NULL, NULL, false,
    NULL, NULL, NULL, false,
    'Family member documented in historical records.',
    'Maplewood',
    NULL,
    '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd',
    '627b55c9-971d-4223-bc9d-d042304538f7',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Relationships

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '25597c10-85de-48b3-9038-9a23bb8a11b1',
    'c19c9372-5e92-4ac1-bcf4-0bb3d8a4c03d',
    'fbe8580a-e7d6-4a8d-8cae-3d6f5d510e50',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '41f72d78-0597-4cf5-a82d-9199b869e8c2',
    '54342115-e009-4a2c-9296-5497dbd31602',
    'c2d5865e-91cc-4b2f-98b6-abb3203f4467',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '714a653d-64a5-4223-aadd-1d8a5e4660f5',
    '54342115-e009-4a2c-9296-5497dbd31602',
    'fbe8580a-e7d6-4a8d-8cae-3d6f5d510e50',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '138fabfa-40c9-42f5-96c0-67a8ed3949b6',
    '409dce92-c18f-4990-b58e-ae6e1200a61a',
    'c19c9372-5e92-4ac1-bcf4-0bb3d8a4c03d',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '4dbf1681-b128-4a3d-bdbe-627efc34f3aa',
    '104c4808-9524-4d86-ae14-e27002ece963',
    'c19c9372-5e92-4ac1-bcf4-0bb3d8a4c03d',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'cd8c3a18-3f97-4da7-834a-863f48891ed3',
    '93a7f71a-e5fb-4b03-bf26-04acd4f23866',
    '742577a4-37ec-4a26-ba09-b2ece921dcb1',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '7dc45027-ad01-4141-9d43-5946d010aecb',
    '93a7f71a-e5fb-4b03-bf26-04acd4f23866',
    '77ca0ee5-36a9-4a7f-a61d-b911befeba92',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '2bee4682-46ea-49ac-9ba8-0fb835b856c1',
    'fec62ce3-d1fc-46ff-a63f-875bf66d7ba7',
    'c19c9372-5e92-4ac1-bcf4-0bb3d8a4c03d',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '9a88db9b-bd84-4edc-9735-047365b221a8',
    'de25f924-c495-41eb-aa65-e9fcbc979d1f',
    'fec62ce3-d1fc-46ff-a63f-875bf66d7ba7',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '8390da59-00f6-42fd-a1ac-5b66d5659e0a',
    'e10f20cc-3f1a-4c86-b148-c648834ac770',
    'fec62ce3-d1fc-46ff-a63f-875bf66d7ba7',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '304cd860-3378-4325-8908-30b24d12b80c',
    'b14b0148-80b8-41ac-83e2-94c515c0450d',
    '409dce92-c18f-4990-b58e-ae6e1200a61a',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '96dc2f50-684c-4e45-93fb-4a33a29242f8',
    '72f48577-d40e-4113-9b5b-d25973a2acaf',
    '409dce92-c18f-4990-b58e-ae6e1200a61a',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'aab3a80b-5c5d-4a1b-a911-30bf6f4b4856',
    '2b3fba29-fdc4-48e7-aadd-083a71e28495',
    '104c4808-9524-4d86-ae14-e27002ece963',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '7c79b642-b388-4463-ba81-00d1ec9924ed',
    '18290f53-00f4-42bd-8795-b204ebc311e9',
    '104c4808-9524-4d86-ae14-e27002ece963',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '5d1300e4-3b19-4008-905d-40754a1b3837',
    '953abda4-3c17-4d23-8963-3c6c82e8cd99',
    '104c4808-9524-4d86-ae14-e27002ece963',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '6dcbae8d-5724-4a34-b4c6-b933e84743a1',
    '2b3fba29-fdc4-48e7-aadd-083a71e28495',
    '93a7f71a-e5fb-4b03-bf26-04acd4f23866',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'c866a42b-55be-4b32-8eb0-fccc18563c0f',
    '3c57f365-e7ab-4a2a-9c03-38741818e6ed',
    'c19c9372-5e92-4ac1-bcf4-0bb3d8a4c03d',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '79fd5465-70ab-4f1d-a607-640f23f77ae5',
    '1802746d-5973-4c23-8342-0b1b34d3204a',
    '3c57f365-e7ab-4a2a-9c03-38741818e6ed',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'f170fa12-cd01-43c0-b365-5a897769b2f8',
    '05b2de5b-7917-4425-ad36-eaf45e7173a3',
    '3c57f365-e7ab-4a2a-9c03-38741818e6ed',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'bc29c784-a978-41e0-9c59-43a0269d9796',
    '76512bd6-9507-4953-846a-bb72fb1376af',
    '3c57f365-e7ab-4a2a-9c03-38741818e6ed',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '680f333e-0c6e-454c-ba39-0b25af30a125',
    'd2453f39-7186-4f1e-9e89-04f27996a16c',
    '87690f3c-4fd4-4025-b287-bcec29b5beb6',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '6ce770c8-a300-43f7-93d5-eccafd2b924a',
    '50f62bc8-d75a-48d7-aae8-c8e347aa1d36',
    'd2453f39-7186-4f1e-9e89-04f27996a16c',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '2098c730-9d93-4c00-99cd-9b6d132714b7',
    'bbc7076a-9a92-40d5-a7df-1799941fae15',
    'd2453f39-7186-4f1e-9e89-04f27996a16c',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '5160247a-5e35-4122-97c5-bbb2629985d5',
    'eb403945-b76a-4d65-9361-a35d77e4c150',
    '8ef8fba3-e642-4702-843f-94e630db651d',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'c05b5ac9-66a6-4980-b5d7-01ef5f934618',
    '3722c981-39be-47f2-903b-a9777af4b87f',
    '8ef8fba3-e642-4702-843f-94e630db651d',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '4f0d7046-409a-4469-b55c-7738ee99b5a0',
    'edee1a40-3feb-499e-be05-b6f00f4eb2a1',
    '2b3fba29-fdc4-48e7-aadd-083a71e28495',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'e564be29-d887-4ad4-b2d5-a0280e178b9d',
    '88f646be-7ab3-4597-87cf-bfe515eb50a2',
    '953abda4-3c17-4d23-8963-3c6c82e8cd99',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '26fa9ba3-e853-4f63-9647-2d3d15db37a3',
    'b45c70cf-06fb-4d64-9409-7efcee7f01a8',
    '953abda4-3c17-4d23-8963-3c6c82e8cd99',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'b3ea6948-92dc-430a-94a5-b3e2e4865b7e',
    'e25fbf82-40d1-48ee-8604-b690b5f018ea',
    '953abda4-3c17-4d23-8963-3c6c82e8cd99',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '26348baa-a54b-418b-ab0d-24bbd8db5d91',
    'f6d3e2e0-9470-43e8-aa70-e8cdf04d8d3a',
    '50f62bc8-d75a-48d7-aae8-c8e347aa1d36',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'e51ea060-3d46-44f8-8a96-340ea1dc3004',
    'aecde14a-7d56-40a8-972f-91275acebc2f',
    '50f62bc8-d75a-48d7-aae8-c8e347aa1d36',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '2b7a3de4-a3b8-42b6-be7b-ee059084607f',
    '526302ae-903b-4f23-ac90-4c4d14d979ad',
    '50f62bc8-d75a-48d7-aae8-c8e347aa1d36',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '32b52231-abbf-4287-809f-ea37d7c55c65',
    '79922913-b309-4b87-ad93-eaac851fc7c7',
    '38d95258-6e66-48a7-979e-fee9c31f6c54',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '8913c45c-e9c0-4ce8-9501-c0f5e298908e',
    '79922913-b309-4b87-ad93-eaac851fc7c7',
    '3722c981-39be-47f2-903b-a9777af4b87f',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '20869b9e-3ad4-47cd-9a7e-843b5bb83b54',
    '44bac9f9-425e-4b2f-b084-e7f33f4b3858',
    'c923a5a8-e697-430a-a1ec-25206af0123c',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '76fb0167-9da6-450a-a8b6-1878452f4ed5',
    'dac11546-7fbc-484d-9297-d8a45330f5d1',
    'c923a5a8-e697-430a-a1ec-25206af0123c',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'aeaf1ec7-5809-457d-bcf8-3c6f02ef830f',
    '5aa32cbf-417a-4399-91a8-b1190276bc27',
    'b04284ec-5ce0-40ce-a9fd-6fa4b2db9c37',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'eebffd96-8762-4825-81f2-4dbb437416fb',
    '10f52d73-f90b-469f-990b-62d15c2b009d',
    'b04284ec-5ce0-40ce-a9fd-6fa4b2db9c37',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '1e03b44b-6dfb-4017-a4d5-149952aa55b0',
    '383d9852-75c8-40b3-b7ce-9571857d3181',
    '1802746d-5973-4c23-8342-0b1b34d3204a',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '80a75a6b-657f-459b-8a2f-9e2d25058183',
    '48f96090-21ad-406f-99a9-b5dbe4983cc2',
    '104c4808-9524-4d86-ae14-e27002ece963',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '1e7b97c0-9eaa-4e26-b42f-bec4f18eedd2',
    'cec666c8-facc-4e32-bc7c-fcb793d8b4b9',
    '54342115-e009-4a2c-9296-5497dbd31602',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '6a2a19b0-5dc3-4f04-badb-8684380feb9f',
    '4a073518-a9f0-407e-b9ed-e15ee89391e1',
    '0ca66fe0-9dc6-4094-81b7-c219ff57a082',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'f96cf87f-2720-4545-b6b9-4a8d36f2b501',
    '77ca0ee5-36a9-4a7f-a61d-b911befeba92',
    '0ca66fe0-9dc6-4094-81b7-c219ff57a082',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'a79f5675-da78-49a1-9d11-aab027c1be98',
    'f55e921e-d4a4-4ce7-959b-d61aa919037f',
    '77ca0ee5-36a9-4a7f-a61d-b911befeba92',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'a2bf8ac6-4391-4c5e-997e-b4af5aa79ad4',
    '2b27412b-066b-4d08-9fd8-562233349c04',
    '742577a4-37ec-4a26-ba09-b2ece921dcb1',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'f2405c94-0fc7-48cb-af3a-1ad10ceba004',
    'd3ac62f1-6802-45e9-b8c8-2942fc5e8d8a',
    '2b27412b-066b-4d08-9fd8-562233349c04',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '4dfdaa83-e456-446b-a3ee-591e310a7f1f',
    '2b27412b-066b-4d08-9fd8-562233349c04',
    'b1530916-ac27-48ab-8416-5e5ea1a50aa2',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'e667b2a2-e9d1-40e4-a708-44b6d8f4b4b1',
    '1a041264-aa85-4b17-a65f-434f1f46ccf8',
    'b1530916-ac27-48ab-8416-5e5ea1a50aa2',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '64914d08-b1af-41e3-998f-ed06b53dd7e6',
    'b9b52246-9974-4cea-9c4e-f305c06f6f29',
    '1a041264-aa85-4b17-a65f-434f1f46ccf8',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'a3c2aae0-224c-4f03-a198-24e38bc62a59',
    '1a041264-aa85-4b17-a65f-434f1f46ccf8',
    'ade0c58d-9d1b-4474-9f67-6bed75905e98',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '5f4792d2-cee6-48cb-a03d-1c57536a04ee',
    '0ca66fe0-9dc6-4094-81b7-c219ff57a082',
    'c19c9372-5e92-4ac1-bcf4-0bb3d8a4c03d',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'e7a7b11e-8086-4aa1-8f42-b34146776a51',
    '6fe16cc8-736b-4df9-a78f-59203485333e',
    'c19c9372-5e92-4ac1-bcf4-0bb3d8a4c03d',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '7b0a25ab-2e9e-486a-ad17-364d39e75f82',
    'c19c9372-5e92-4ac1-bcf4-0bb3d8a4c03d',
    '4363f8f3-7e9a-469e-856f-3a7e578876f7',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'e9cd66fd-902b-478a-bce2-930d94f046e7',
    '4363f8f3-7e9a-469e-856f-3a7e578876f7',
    'd74cf487-9b6f-4e55-a1a7-de43a29c7574',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'b841ba01-2875-464a-ba64-c14fc4020b55',
    '4363f8f3-7e9a-469e-856f-3a7e578876f7',
    '525ce00a-76b0-4c17-8cd3-1c8e86a8e031',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'e126031d-5347-4d53-a4ab-5027927ca30b',
    '1d1abe8a-f03b-4afd-a493-ffb5d362b886',
    '7e5cfbfc-9e2b-4e23-82b5-5127ef64986f',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '0a3c9582-8097-4809-9aa6-004956bfb141',
    'aefbbb8d-69cd-4a13-9d08-9b386507a31e',
    'fa880b07-c148-4f9d-974c-63071b38fd39',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'e00278f7-e12c-45ae-8498-815dbdb80543',
    '0ca66fe0-9dc6-4094-81b7-c219ff57a082',
    '7217984b-6a1d-45c2-b9e4-e6e08e7fc461',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'e94a6fa5-e030-4ade-9cc4-cc30c1082e74',
    'a219a3e7-f59d-4603-8a11-9b65ae93c575',
    'b14b0148-80b8-41ac-83e2-94c515c0450d',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '8af58990-f5b6-49bf-ab4d-f06794cbc64b',
    '11553aef-5e1a-42a4-a727-21e88baa1dc6',
    'b14b0148-80b8-41ac-83e2-94c515c0450d',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '0beef27e-57f2-40b4-9675-cf3886db8a72',
    '95d12b5d-6b88-486d-aebd-51f0b369861e',
    'a219a3e7-f59d-4603-8a11-9b65ae93c575',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '83fca98a-b136-403f-91d1-0cd667540a23',
    'a33bc05d-e505-4a9c-94c2-fc873cb60e82',
    'a219a3e7-f59d-4603-8a11-9b65ae93c575',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '52370e64-3898-4cd5-8865-ef6d2bbdd358',
    '4330a183-6d1b-4dbd-8267-53bd8d16f995',
    'a219a3e7-f59d-4603-8a11-9b65ae93c575',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '1ff0569e-180e-4bd1-b90b-6aa98a4574a9',
    '51e75f01-9b59-4cee-8c3d-e76adfc79a31',
    '48f96090-21ad-406f-99a9-b5dbe4983cc2',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '112e1319-7aec-4f10-8c0a-12ba45b1fc9e',
    '3b453297-79e2-40a0-a3f6-e234a91d866b',
    '76512bd6-9507-4953-846a-bb72fb1376af',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '1a01a834-0852-42e3-8b1d-4a0799822bfe',
    'a8e83a0d-7699-44dd-aa43-81a389e95823',
    '76512bd6-9507-4953-846a-bb72fb1376af',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '3919e7b0-85a4-491f-a6d6-19608b1e2a94',
    '2a997840-efae-4959-a424-6be9c8ffe030',
    'c923a5a8-e697-430a-a1ec-25206af0123c',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'bf697910-941e-4c4a-bb01-30b48a353c14',
    '6f69ad78-2300-463e-b134-c2d9c406e13a',
    '99e77720-95ee-4f78-9b29-65db492e3366',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '805b8d4f-27d1-4400-89b7-7fba8c56a5c3',
    '2a997840-efae-4959-a424-6be9c8ffe030',
    'b1c28ca8-bd8c-43aa-900d-98bb39713a22',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '359aaace-d70e-4780-8985-e37665fa7b35',
    'c923a5a8-e697-430a-a1ec-25206af0123c',
    '4f3acd28-48d1-4c37-a9e6-f4876c23cc3a',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'ee25b9f7-82ba-42b4-9fcb-d5604ded8076',
    'fb427193-b9f5-47ab-96c4-b12231647440',
    '99e77720-95ee-4f78-9b29-65db492e3366',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '1d05a3ac-3ed8-41d1-b6c6-cde1f3fa381d',
    'b1c28ca8-bd8c-43aa-900d-98bb39713a22',
    'b47eaa3e-5e57-473b-96b3-2789b2677c38',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'c273aee4-f1cb-4664-ba39-3b2848b762d7',
    'dce9a0ee-781f-4eef-ba8f-c5addefa948d',
    'a093f024-0f56-4436-b043-086a76297309',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'a57ed4dd-b255-4d1b-814e-c03672ae8e75',
    'a093f024-0f56-4436-b043-086a76297309',
    '320f6593-a484-4175-a48b-90d0b4b8129f',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '9eeb1d30-3c98-4ce8-94fc-4669f195525e',
    '1b2e1f93-8035-43b5-b10e-158e91479225',
    '3910a11e-c28e-44fb-96b4-2489623ee3bb',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '912ae32b-11d3-4b1f-b872-de497e9278dc',
    '79d3ea39-57ff-4e5b-b5aa-ff42739d30c1',
    '18290f53-00f4-42bd-8795-b204ebc311e9',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '041bf14d-6423-478d-b02f-f8b43f300e6d',
    '0d192516-7866-4daf-8b8a-5ec14f8f5528',
    '51e75f01-9b59-4cee-8c3d-e76adfc79a31',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'a0a6d130-9d09-4403-a08c-f8cad8f5a14a',
    '2c07aa06-e225-452c-aac8-53a3a7dd72de',
    '51e75f01-9b59-4cee-8c3d-e76adfc79a31',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'e65fe65a-11f4-46b0-9c6a-3ae89ba838fc',
    'b5c74970-bca6-4411-9d45-5a27bc7657b0',
    'edee1a40-3feb-499e-be05-b6f00f4eb2a1',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '9f5ca956-a330-4fd1-b261-260129c52fc2',
    '0f8e286d-7e92-44a9-a438-bf12ab01029f',
    'edee1a40-3feb-499e-be05-b6f00f4eb2a1',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '5fde1f3c-0460-4dc4-ad38-d4538b2953be',
    '76c2d222-a0ae-48c6-a6e3-09ef028886f9',
    'e25fbf82-40d1-48ee-8604-b690b5f018ea',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '67f33e9b-2937-48d0-881f-51b898b0a47f',
    'c3878897-c55c-4882-8824-2936f334734e',
    'a382259a-3ee4-4e93-8cb2-d0122c36ecae',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'bc10af77-5147-423c-825b-9860e0044ee0',
    '861bf988-7207-44e0-8557-d8529b07336a',
    '88f646be-7ab3-4597-87cf-bfe515eb50a2',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '39d793e5-6e7d-4452-b285-e28567966483',
    'ad47ddb6-3a37-49bd-9a47-af04c82c9eb7',
    '88f646be-7ab3-4597-87cf-bfe515eb50a2',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '095e0d97-705e-4588-b665-d34aee4d8990',
    'ceba38af-e14b-4ff7-8f4c-fe3051fc8286',
    '88f646be-7ab3-4597-87cf-bfe515eb50a2',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '6c5bcbab-8e0d-4a11-af56-18a675e07cbc',
    '393de9b1-24ed-4338-b1b2-5475d28a21d6',
    '88f646be-7ab3-4597-87cf-bfe515eb50a2',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'f8df20db-ecae-49b6-bea5-7fc7e7e54ba8',
    '383d9852-75c8-40b3-b7ce-9571857d3181',
    '05b2de5b-7917-4425-ad36-eaf45e7173a3',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '136b50dd-cbe0-40a3-bd9e-6cb0024dbfc6',
    'd4674790-6956-41e8-881b-75771efb3545',
    '59178c74-12a8-4ab7-9722-dc0b40d593d6',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '864fc843-00fe-4e4f-80c4-89f3e03924f4',
    'd0e88b87-c36e-4c33-8688-541ed4beb668',
    '074da226-d044-4bb6-8380-a88589ccf21a',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '26369470-785c-413d-af59-633c5fb47713',
    'cec666c8-facc-4e32-bc7c-fcb793d8b4b9',
    '15cc60e2-f5b9-4398-b467-8f19c430c7b0',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '062fedeb-1735-43db-b5c4-6aec5334a475',
    'ac119086-e7fe-4d85-9631-8a6a4453ddb8',
    'af242ba4-0682-4242-8f77-01d192f97734',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '38ef71bb-d118-45d7-bd87-689514052a76',
    '534a0aec-fc40-4c15-a441-82d4aa94ce60',
    '838cc8e2-ab32-462b-8346-0ed7d54d80dc',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '9945b116-d7b0-4715-8206-2b078d9c7f5b',
    '534a0aec-fc40-4c15-a441-82d4aa94ce60',
    '5ab6176b-6291-4aeb-ad9d-82eceec30454',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '44bf11d5-42ec-4b70-b9f6-4fc89b0de372',
    'af242ba4-0682-4242-8f77-01d192f97734',
    '534a0aec-fc40-4c15-a441-82d4aa94ce60',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'e903bdbe-03b8-4410-b653-480f7557607d',
    '742577a4-37ec-4a26-ba09-b2ece921dcb1',
    '273c06e8-286a-42e5-891a-93f290cd5bcd',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '3763b08f-6325-4d55-aa06-b1e5dedc635c',
    '742577a4-37ec-4a26-ba09-b2ece921dcb1',
    '9e06b78d-ac85-48bb-a6e7-2f3fb4d7ac84',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '2c5b0842-bb5f-426c-8a46-6f8424ee8616',
    '70cd9e13-5bba-4f9e-9f89-1ea53d2581ab',
    '273c06e8-286a-42e5-891a-93f290cd5bcd',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '6db011ba-117f-4980-b09d-3a6b72bc4c6f',
    'a852acd5-56cd-4f14-9abe-bc9b8df41316',
    '9e06b78d-ac85-48bb-a6e7-2f3fb4d7ac84',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '36431d64-deee-4c0d-a977-694d9dfad9ba',
    '5a4c80d9-00f9-47d0-b926-702a843bafe2',
    '59178c74-12a8-4ab7-9722-dc0b40d593d6',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '7442c07a-2e8b-45c6-b9f6-85fa9d7e69eb',
    '5a4c80d9-00f9-47d0-b926-702a843bafe2',
    '409dce92-c18f-4990-b58e-ae6e1200a61a',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '4a301203-178f-4521-bffa-7ecf51f76a7a',
    '408b42f1-c145-4a62-b59c-d611ec842f56',
    '450db307-56ba-4356-98b6-e69ad7b99a83',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'd2461d44-4220-4941-8911-9c35ce9d60bc',
    '531da4cb-2a9b-4b5f-be61-236fdc86b6cf',
    'dac11546-7fbc-484d-9297-d8a45330f5d1',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '74cb1388-f12d-4bf2-876e-70a496584936',
    '2595a0bf-b519-4698-8c74-f6d447cc4bf7',
    'dac11546-7fbc-484d-9297-d8a45330f5d1',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '25385772-2054-4202-88af-4cfafff38073',
    'f94462d7-e8c6-48b6-b225-dc224c59c769',
    '6851b222-f869-48e5-be99-0f9c8d60258e',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '817770f7-4fdf-412f-8a92-43ae0b718ad7',
    '40ba16cb-b637-4e4e-9b2a-1ee2d6de90b5',
    'a382259a-3ee4-4e93-8cb2-d0122c36ecae',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '51d0788d-9c66-4ea1-81f4-ff4a4806f6a2',
    '31b7f3b1-d368-4baf-9d8e-ef5f2cd5a421',
    'b5c74970-bca6-4411-9d45-5a27bc7657b0',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '8cfca473-f342-4a48-81e6-9c080966629b',
    'faee7857-08b4-4032-acc2-eb663a3c6b3c',
    'b5c74970-bca6-4411-9d45-5a27bc7657b0',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'e1b5644b-0b7b-4e69-af0e-0572afdb920a',
    'a382259a-3ee4-4e93-8cb2-d0122c36ecae',
    'e25fbf82-40d1-48ee-8604-b690b5f018ea',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '074ee106-3aaa-42d5-a67e-c23264145ebe',
    'a33bc05d-e505-4a9c-94c2-fc873cb60e82',
    'de25f924-c495-41eb-aa65-e9fcbc979d1f',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'cc48f621-619e-4816-9651-f942433fe51e',
    '95d12b5d-6b88-486d-aebd-51f0b369861e',
    'de25f924-c495-41eb-aa65-e9fcbc979d1f',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '73388218-838a-4e26-8f23-2d81102a0d27',
    'd26914f8-5298-4a7f-9b46-bdb46813ab43',
    'e10f20cc-3f1a-4c86-b148-c648834ac770',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '4aaec3e6-b4f9-4029-a1a2-3d65ca6ffabf',
    'aefbbb8d-69cd-4a13-9d08-9b386507a31e',
    '7e5cfbfc-9e2b-4e23-82b5-5127ef64986f',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'dcbb4923-c473-4e43-b87b-8b346bc8b047',
    'fec62ce3-d1fc-46ff-a63f-875bf66d7ba7',
    'dce9a0ee-781f-4eef-ba8f-c5addefa948d',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '35db8d86-fefb-413c-9557-51d3bc62a9cf',
    '7ad45e3b-bfa6-48f2-8920-ff25bca9524c',
    'fec62ce3-d1fc-46ff-a63f-875bf66d7ba7',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '85ff295e-85c3-4a9e-b5ab-f207b5fff0ab',
    'fe7750a7-f3a0-4c9d-b44f-a7051de2b457',
    'f579378a-43b6-4ed6-9eef-12e04e36e197',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '01a42adb-e7a6-4fe7-b67c-2e4a90370294',
    '963f21f5-44c5-463f-9888-b82b89aed0ca',
    'f579378a-43b6-4ed6-9eef-12e04e36e197',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '9d191ad5-6d8e-4faf-a800-b46b3b44cf88',
    'ee7d5011-cea0-4928-9b5c-3099e6f29c40',
    'f579378a-43b6-4ed6-9eef-12e04e36e197',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'aa8ef1bf-b2cc-48f2-8208-f6645b7487cf',
    '512d53ca-895b-48be-88a3-f8f6ae1a5031',
    'fec62ce3-d1fc-46ff-a63f-875bf66d7ba7',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '7a5af67d-1e82-47f6-91b5-9de2fa2bfa84',
    '10c880e4-c3f9-4f2f-b1b4-2a0fc5f419ac',
    '79aab300-fd21-44df-9516-fe8f919c9425',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'be3e95bb-1e0f-40e7-9827-393d7f30a507',
    '79aab300-fd21-44df-9516-fe8f919c9425',
    'f01232cd-517d-426d-ac28-cf00eb2fed13',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '3ac67582-80d7-42d1-b91b-eddb04107d1c',
    'f01232cd-517d-426d-ac28-cf00eb2fed13',
    '3044a852-f761-41fc-8541-b664673c9735',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '317aab03-0f42-4d70-aa1a-457eaffc6356',
    '9cd36ae2-5503-44f9-b110-be42dde2f17b',
    'ac119086-e7fe-4d85-9631-8a6a4453ddb8',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'e8e3386c-0436-450e-950c-1e8f2182b841',
    '56545826-90fe-40df-b925-3e66633a85b3',
    'ac119086-e7fe-4d85-9631-8a6a4453ddb8',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'c69d4e30-b3c8-4e23-bfb6-729c08433a5c',
    'ac119086-e7fe-4d85-9631-8a6a4453ddb8',
    '4807fea8-f9c7-47eb-980c-66940813585a',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '54fc7614-eb11-4d08-9747-1f37baa479a5',
    '05b2de5b-7917-4425-ad36-eaf45e7173a3',
    '3b453297-79e2-40a0-a3f6-e234a91d866b',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '5e4acfc3-fba2-4960-bc50-aa04aecfc9dd',
    '413656b6-8608-4e1b-95ca-d9c9d5b2125b',
    'b04284ec-5ce0-40ce-a9fd-6fa4b2db9c37',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '33c152a3-8a69-419e-940c-ca5b0c57f638',
    'd0e88b87-c36e-4c33-8688-541ed4beb668',
    '62c31389-3265-4a10-92f3-c4ad7382a96d',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '4c415ba1-ee1f-4082-b3fa-6d81dcb6123c',
    '384eebe9-aa2b-49d1-8e99-2184604d93c9',
    '2b11e17c-9d02-4966-911a-c7dc500808b2',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'a12c04bd-98b7-4ca7-a0d2-686945f3825d',
    '2b11e17c-9d02-4966-911a-c7dc500808b2',
    '69b49f21-8925-4a2e-81e1-7c5613464fe7',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '6cc8ef8b-d634-45a9-a601-cd892c751c48',
    '69b49f21-8925-4a2e-81e1-7c5613464fe7',
    '14f3ab39-5e4b-4400-b4ca-771606728458',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '10a5d2d4-8825-47a8-9c1b-042e3b2f5428',
    '69b49f21-8925-4a2e-81e1-7c5613464fe7',
    'a584c31d-74da-4cc0-a181-c3495df14a93',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '6834abdc-8b01-464e-b3eb-daac12dd57bd',
    'fbe8580a-e7d6-4a8d-8cae-3d6f5d510e50',
    '6820467f-653f-40fc-be61-b7556b5cd5b9',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '08e9e3c6-2876-46ff-9145-0b29705fad6e',
    'fbe8580a-e7d6-4a8d-8cae-3d6f5d510e50',
    '21fae58f-4caa-40b7-98cc-fdd083b46aa5',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    'd6364264-ec16-46b1-a49e-4419925ed0d8',
    '6820467f-653f-40fc-be61-b7556b5cd5b9',
    '6f69ad78-2300-463e-b134-c2d9c406e13a',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO relationships (id, person_from_id, person_to_id, relationship_type, start_year, is_divorced, created_at, updated_at)
VALUES (
    '6bf62523-53e6-4645-9a1e-c84024d23cf4',
    '393de9b1-24ed-4338-b1b2-5475d28a21d6',
    'ceba38af-e14b-4ff7-8f4c-fe3051fc8286',
    'PARENT',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Set tree root person
UPDATE trees SET root_person_id = '7e5cfbfc-9e2b-4e23-82b5-5127ef64986f' WHERE id = '32fc5fe5-7a4d-4448-aacb-2c8d52e4ddbd';
