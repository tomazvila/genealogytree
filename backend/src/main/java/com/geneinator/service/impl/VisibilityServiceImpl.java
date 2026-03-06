package com.geneinator.service.impl;

import com.geneinator.entity.BranchPermission;
import com.geneinator.entity.BranchPermission.PermissionType;
import com.geneinator.entity.Relationship;
import com.geneinator.repository.BranchPermissionRepository;
import com.geneinator.repository.PersonRepository;
import com.geneinator.repository.RelationshipRepository;
import com.geneinator.service.VisibilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class VisibilityServiceImpl implements VisibilityService {

    private final BranchPermissionRepository branchPermissionRepository;
    private final RelationshipRepository relationshipRepository;
    private final PersonRepository personRepository;
    private final int maxHops;

    public VisibilityServiceImpl(
            BranchPermissionRepository branchPermissionRepository,
            RelationshipRepository relationshipRepository,
            PersonRepository personRepository,
            @Value("${app.visibility.default-max-relationship-hops:3}") int maxHops) {
        this.branchPermissionRepository = branchPermissionRepository;
        this.relationshipRepository = relationshipRepository;
        this.personRepository = personRepository;
        this.maxHops = maxHops;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canView(UUID viewerId, UUID personId) {
        log.debug("Checking view permission for viewer {} on person {}", viewerId, personId);

        List<BranchPermission> permissions = branchPermissionRepository.findByUserId(viewerId);
        if (permissions.isEmpty()) {
            log.debug("User {} has no branch permissions", viewerId);
            return false;
        }

        // Check if person is within reach of any of the user's branches
        for (BranchPermission permission : permissions) {
            UUID rootPersonId = permission.getRootPerson().getId();

            // Direct access to root person
            if (rootPersonId.equals(personId)) {
                return true;
            }

            // Check if person is within maxHops of the root
            int distance = getRelationshipDistance(rootPersonId, personId);
            if (distance >= 0 && distance <= maxHops) {
                return true;
            }
        }

        log.debug("User {} cannot view person {}", viewerId, personId);
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canEdit(UUID userId, UUID personId) {
        log.debug("Checking edit permission for user {} on person {}", userId, personId);

        List<BranchPermission> permissions = branchPermissionRepository.findByUserId(userId);
        if (permissions.isEmpty()) {
            log.debug("User {} has no branch permissions", userId);
            return false;
        }

        // Check if user has EDITOR or OWNER permission on a branch that includes this person
        for (BranchPermission permission : permissions) {
            if (permission.getPermissionType() == PermissionType.VIEWER) {
                continue; // Viewers cannot edit
            }

            UUID rootPersonId = permission.getRootPerson().getId();

            // Direct access to root person with edit permission
            if (rootPersonId.equals(personId)) {
                return true;
            }

            // Check if person is within maxHops of the root
            int distance = getRelationshipDistance(rootPersonId, personId);
            if (distance >= 0 && distance <= maxHops) {
                return true;
            }
        }

        log.debug("User {} cannot edit person {}", userId, personId);
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<UUID> getVisiblePersonIds(UUID viewerId) {
        log.debug("Getting visible person IDs for viewer {}", viewerId);

        Set<UUID> visibleIds = new HashSet<>();

        List<BranchPermission> permissions = branchPermissionRepository.findByUserId(viewerId);
        if (permissions.isEmpty()) {
            return visibleIds;
        }

        // For each branch permission, collect all persons within maxHops
        for (BranchPermission permission : permissions) {
            UUID rootPersonId = permission.getRootPerson().getId();
            Set<UUID> branchPersons = getPersonsWithinHops(rootPersonId, maxHops);
            visibleIds.addAll(branchPersons);
        }

        log.debug("User {} can view {} persons", viewerId, visibleIds.size());
        return visibleIds;
    }

    @Override
    @Transactional(readOnly = true)
    public int getRelationshipDistance(UUID personId1, UUID personId2) {
        if (personId1.equals(personId2)) {
            return 0;
        }

        // BFS to find shortest path
        Set<UUID> visited = new HashSet<>();
        Queue<UUID> queue = new LinkedList<>();
        Map<UUID, Integer> distances = new HashMap<>();

        queue.add(personId1);
        visited.add(personId1);
        distances.put(personId1, 0);

        while (!queue.isEmpty()) {
            UUID current = queue.poll();
            int currentDistance = distances.get(current);

            // Don't search beyond a reasonable limit
            if (currentDistance >= 10) {
                continue;
            }

            List<Relationship> relationships = relationshipRepository.findAllByPersonId(current);

            for (Relationship rel : relationships) {
                UUID nextPerson = rel.getPersonFrom().getId().equals(current)
                        ? rel.getPersonTo().getId()
                        : rel.getPersonFrom().getId();

                if (nextPerson.equals(personId2)) {
                    return currentDistance + 1;
                }

                if (!visited.contains(nextPerson)) {
                    visited.add(nextPerson);
                    queue.add(nextPerson);
                    distances.put(nextPerson, currentDistance + 1);
                }
            }
        }

        return -1; // Not connected
    }

    private Set<UUID> getPersonsWithinHops(UUID rootPersonId, int maxHops) {
        Set<UUID> result = new HashSet<>();
        Set<UUID> visited = new HashSet<>();
        Queue<UUID> queue = new LinkedList<>();
        Map<UUID, Integer> distances = new HashMap<>();

        queue.add(rootPersonId);
        visited.add(rootPersonId);
        distances.put(rootPersonId, 0);
        result.add(rootPersonId);

        while (!queue.isEmpty()) {
            UUID current = queue.poll();
            int currentDistance = distances.get(current);

            if (currentDistance >= maxHops) {
                continue;
            }

            List<Relationship> relationships = relationshipRepository.findAllByPersonId(current);

            for (Relationship rel : relationships) {
                UUID nextPerson = rel.getPersonFrom().getId().equals(current)
                        ? rel.getPersonTo().getId()
                        : rel.getPersonFrom().getId();

                if (!visited.contains(nextPerson)) {
                    visited.add(nextPerson);
                    queue.add(nextPerson);
                    distances.put(nextPerson, currentDistance + 1);
                    result.add(nextPerson);
                }
            }
        }

        return result;
    }
}
