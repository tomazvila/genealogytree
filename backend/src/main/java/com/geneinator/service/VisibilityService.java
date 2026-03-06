package com.geneinator.service;

import java.util.Set;
import java.util.UUID;

public interface VisibilityService {

    boolean canView(UUID viewerId, UUID personId);

    boolean canEdit(UUID userId, UUID personId);

    Set<UUID> getVisiblePersonIds(UUID viewerId);

    int getRelationshipDistance(UUID personId1, UUID personId2);
}
