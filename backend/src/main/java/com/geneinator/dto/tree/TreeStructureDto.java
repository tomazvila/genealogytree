package com.geneinator.dto.tree;

import com.geneinator.dto.person.PersonDto;
import com.geneinator.dto.relationship.RelationshipDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class TreeStructureDto {
    private UUID treeId;
    private String treeName;
    private UUID createdBy;
    private List<PersonDto> persons;
    private List<RelationshipDto> relationships;
}
