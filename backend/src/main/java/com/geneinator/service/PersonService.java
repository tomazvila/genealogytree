package com.geneinator.service;

import com.geneinator.dto.person.PersonCreateRequest;
import com.geneinator.dto.person.PersonDto;
import com.geneinator.dto.person.PersonMergeRequest;
import com.geneinator.dto.person.PersonUpdateRequest;
import com.geneinator.dto.person.RelativeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PersonService {

    PersonDto findById(UUID id);

    Page<PersonDto> findAll(Pageable pageable);

    Page<PersonDto> search(String query, Pageable pageable);

    Page<PersonDto> searchByBirthYearRange(Integer fromYear, Integer toYear, Pageable pageable);

    Page<PersonDto> searchByLocation(String location, Pageable pageable);

    Page<PersonDto> searchAdvanced(String name, Integer birthYearFrom, Integer birthYearTo,
                                    String location, Pageable pageable);

    PersonDto create(PersonCreateRequest request, UUID createdBy);

    PersonDto update(UUID id, PersonUpdateRequest request);

    void delete(UUID id);

    List<RelativeDto> findRelatives(UUID personId);

    PersonDto findWithRelationships(UUID id);

    PersonDto merge(PersonMergeRequest request);

    PersonDto merge(PersonMergeRequest request, UUID requestedBy);

    Page<PersonDto> findDescendants(UUID ancestorId, Pageable pageable);

    Page<PersonDto> findAncestors(UUID descendantId, Pageable pageable);
}
