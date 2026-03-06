package com.geneinator.service;

import com.geneinator.dto.relationship.RelationshipCreateRequest;
import com.geneinator.dto.relationship.RelationshipDto;

import java.util.List;
import java.util.UUID;

public interface RelationshipService {

    RelationshipDto create(RelationshipCreateRequest request);

    void delete(UUID id);

    List<RelationshipDto> findByPersonId(UUID personId);

    boolean areRelated(UUID personId1, UUID personId2, int maxHops);
}
