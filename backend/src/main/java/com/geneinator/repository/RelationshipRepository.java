package com.geneinator.repository;

import com.geneinator.entity.Relationship;
import com.geneinator.entity.Relationship.RelationshipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, UUID> {

    List<Relationship> findByPersonFromId(UUID personFromId);

    List<Relationship> findByPersonToId(UUID personToId);

    @Query("SELECT r FROM Relationship r WHERE r.personFrom.id = :personId OR r.personTo.id = :personId")
    List<Relationship> findAllByPersonId(@Param("personId") UUID personId);

    List<Relationship> findByPersonFromIdAndRelationshipType(UUID personFromId, RelationshipType type);

    boolean existsByPersonFromIdAndPersonToIdAndRelationshipType(
            UUID personFromId, UUID personToId, RelationshipType type);

    /**
     * Find all descendant IDs using recursive CTE (single query instead of N queries).
     * Follows PARENT relationships where personFrom is the parent and personTo is the child.
     */
    @Query(value = """
            WITH RECURSIVE descendants AS (
                -- Base case: direct children of the ancestor
                SELECT r.person_to_id AS descendant_id
                FROM relationships r
                WHERE r.person_from_id = :ancestorId
                  AND r.relationship_type = 'PARENT'

                UNION

                -- Recursive case: children of descendants
                SELECT r.person_to_id
                FROM relationships r
                INNER JOIN descendants d ON r.person_from_id = d.descendant_id
                WHERE r.relationship_type = 'PARENT'
            )
            SELECT descendant_id FROM descendants
            """, nativeQuery = true)
    List<UUID> findAllDescendantIds(@Param("ancestorId") UUID ancestorId);

    /**
     * Find all ancestor IDs using recursive CTE (single query instead of N queries).
     * Follows PARENT relationships backwards (personFrom is parent of personTo).
     */
    @Query(value = """
            WITH RECURSIVE ancestors AS (
                -- Base case: direct parents of the descendant
                SELECT r.person_from_id AS ancestor_id
                FROM relationships r
                WHERE r.person_to_id = :descendantId
                  AND r.relationship_type = 'PARENT'

                UNION

                -- Recursive case: parents of ancestors
                SELECT r.person_from_id
                FROM relationships r
                INNER JOIN ancestors a ON r.person_to_id = a.ancestor_id
                WHERE r.relationship_type = 'PARENT'
            )
            SELECT ancestor_id FROM ancestors
            """, nativeQuery = true)
    List<UUID> findAllAncestorIds(@Param("descendantId") UUID descendantId);

    /**
     * Find all relationships where both persons are in the given set of IDs.
     */
    @Query("SELECT r FROM Relationship r WHERE r.personFrom.id IN :personIds AND r.personTo.id IN :personIds")
    List<Relationship> findAllByPersonIdsIn(@Param("personIds") List<UUID> personIds);

    /**
     * Find sibling IDs for a person (people who share at least one parent).
     * A sibling is someone who:
     * - Has a PARENT relationship to the same person as the given person, OR
     * - Is a CHILD of the same parent (via reverse relationship)
     * This handles both full siblings and half-siblings.
     */
    @Query(value = """
            SELECT DISTINCT sibling_id FROM (
                -- Case 1: Both have outgoing PARENT relationship to same parent
                SELECT r2.person_from_id AS sibling_id
                FROM relationships r1
                JOIN relationships r2 ON r1.person_to_id = r2.person_to_id
                WHERE r1.person_from_id = :personId
                  AND r1.relationship_type = 'PARENT'
                  AND r2.relationship_type = 'PARENT'
                  AND r2.person_from_id != :personId

                UNION

                -- Case 2: Person has outgoing PARENT, sibling has incoming (parent->child)
                SELECT r2.person_to_id AS sibling_id
                FROM relationships r1
                JOIN relationships r2 ON r1.person_to_id = r2.person_from_id
                WHERE r1.person_from_id = :personId
                  AND r1.relationship_type = 'PARENT'
                  AND r2.relationship_type = 'PARENT'
                  AND r2.person_to_id != :personId

                UNION

                -- Case 3: Person has incoming (parent->child), sibling has outgoing PARENT
                SELECT r2.person_from_id AS sibling_id
                FROM relationships r1
                JOIN relationships r2 ON r1.person_from_id = r2.person_to_id
                WHERE r1.person_to_id = :personId
                  AND r1.relationship_type = 'PARENT'
                  AND r2.relationship_type = 'PARENT'
                  AND r2.person_from_id != :personId

                UNION

                -- Case 4: Both have incoming (parent->child) from same parent
                SELECT r2.person_to_id AS sibling_id
                FROM relationships r1
                JOIN relationships r2 ON r1.person_from_id = r2.person_from_id
                WHERE r1.person_to_id = :personId
                  AND r1.relationship_type = 'PARENT'
                  AND r2.relationship_type = 'PARENT'
                  AND r2.person_to_id != :personId
            ) AS siblings
            """, nativeQuery = true)
    List<UUID> findSiblingIds(@Param("personId") UUID personId);

    /**
     * Find cousin IDs for a person (parent's sibling's children).
     * A cousin is a child of a parent's sibling.
     */
    @Query(value = """
            WITH person_parents AS (
                SELECT person_to_id AS parent_id
                FROM relationships
                WHERE person_from_id = :personId
                  AND relationship_type = 'PARENT'
                UNION
                SELECT person_from_id AS parent_id
                FROM relationships
                WHERE person_to_id = :personId
                  AND relationship_type = 'PARENT'
            ),
            parent_siblings AS (
                SELECT DISTINCT r2.person_from_id AS aunt_uncle_id
                FROM person_parents pp
                JOIN relationships r1 ON pp.parent_id = r1.person_from_id AND r1.relationship_type = 'PARENT'
                JOIN relationships r2 ON r1.person_to_id = r2.person_to_id AND r2.relationship_type = 'PARENT'
                WHERE r2.person_from_id != pp.parent_id
                UNION
                SELECT DISTINCT r2.person_to_id AS aunt_uncle_id
                FROM person_parents pp
                JOIN relationships r1 ON pp.parent_id = r1.person_from_id AND r1.relationship_type = 'PARENT'
                JOIN relationships r2 ON r1.person_to_id = r2.person_from_id AND r2.relationship_type = 'PARENT'
                WHERE r2.person_to_id != pp.parent_id
                UNION
                SELECT DISTINCT r2.person_from_id AS aunt_uncle_id
                FROM person_parents pp
                JOIN relationships r1 ON pp.parent_id = r1.person_to_id AND r1.relationship_type = 'PARENT'
                JOIN relationships r2 ON r1.person_from_id = r2.person_to_id AND r2.relationship_type = 'PARENT'
                WHERE r2.person_from_id != pp.parent_id
                UNION
                SELECT DISTINCT r2.person_to_id AS aunt_uncle_id
                FROM person_parents pp
                JOIN relationships r1 ON pp.parent_id = r1.person_to_id AND r1.relationship_type = 'PARENT'
                JOIN relationships r2 ON r1.person_from_id = r2.person_from_id AND r2.relationship_type = 'PARENT'
                WHERE r2.person_to_id != pp.parent_id
            ),
            cousins AS (
                SELECT r.person_to_id AS cousin_id
                FROM parent_siblings ps
                JOIN relationships r ON ps.aunt_uncle_id = r.person_from_id
                WHERE r.relationship_type = 'PARENT'
                UNION
                SELECT r.person_from_id AS cousin_id
                FROM parent_siblings ps
                JOIN relationships r ON ps.aunt_uncle_id = r.person_to_id
                WHERE r.relationship_type = 'PARENT'
            )
            SELECT DISTINCT cousin_id FROM cousins WHERE cousin_id != :personId
            """, nativeQuery = true)
    List<UUID> findCousinIds(@Param("personId") UUID personId);
}
