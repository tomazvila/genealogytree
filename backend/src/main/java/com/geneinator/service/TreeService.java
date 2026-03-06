package com.geneinator.service;

import com.geneinator.dto.tree.TreeCreateRequest;
import com.geneinator.dto.tree.TreeDto;
import com.geneinator.dto.tree.TreeStructureDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TreeService {

    TreeDto findById(UUID id);

    Page<TreeDto> findAll(Pageable pageable);

    TreeDto create(TreeCreateRequest request, UUID createdBy);

    TreeStructureDto getTreeStructure(UUID treeId, UUID viewerId);

    void mergeTrees(UUID sourceTreeId, UUID targetTreeId);
}
