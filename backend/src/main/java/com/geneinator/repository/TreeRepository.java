package com.geneinator.repository;

import com.geneinator.entity.Tree;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TreeRepository extends JpaRepository<Tree, UUID> {

    List<Tree> findByCreatedBy(UUID createdBy);

    Page<Tree> findByIsMergeableTrue(Pageable pageable);
}
