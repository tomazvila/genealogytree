package com.geneinator.repository;

import com.geneinator.entity.BranchPermission;
import com.geneinator.entity.BranchPermission.PermissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BranchPermissionRepository extends JpaRepository<BranchPermission, UUID> {

    List<BranchPermission> findByUserId(UUID userId);

    List<BranchPermission> findByRootPersonId(UUID rootPersonId);

    Optional<BranchPermission> findByUserIdAndRootPersonId(UUID userId, UUID rootPersonId);

    List<BranchPermission> findByUserIdAndPermissionType(UUID userId, PermissionType type);

    boolean existsByUserIdAndRootPersonIdAndPermissionTypeIn(
            UUID userId, UUID rootPersonId, List<PermissionType> types);
}
