package com.geneinator.controller;

import com.geneinator.dto.tree.TreeCreateRequest;
import com.geneinator.dto.tree.TreeDto;
import com.geneinator.dto.tree.TreeStructureDto;
import com.geneinator.entity.User;
import com.geneinator.service.TreeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/trees")
@RequiredArgsConstructor
public class TreeController {

    private final TreeService treeService;

    @GetMapping
    public ResponseEntity<Page<TreeDto>> findAll(Pageable pageable) {
        Page<TreeDto> trees = treeService.findAll(pageable);
        return ResponseEntity.ok(trees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TreeStructureDto> findById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user) {
        UUID viewerId = user != null ? user.getId() : null;
        TreeStructureDto structure = treeService.getTreeStructure(id, viewerId);
        return ResponseEntity.ok(structure);
    }

    @PostMapping
    public ResponseEntity<TreeDto> create(
            @Valid @RequestBody TreeCreateRequest request,
            @AuthenticationPrincipal User user) {
        UUID userId = user != null ? user.getId() : UUID.randomUUID();
        TreeDto created = treeService.create(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/merge")
    public ResponseEntity<Void> mergeTrees(
            @RequestParam UUID sourceTreeId,
            @RequestParam UUID targetTreeId) {
        treeService.mergeTrees(sourceTreeId, targetTreeId);
        return ResponseEntity.noContent().build();
    }
}
