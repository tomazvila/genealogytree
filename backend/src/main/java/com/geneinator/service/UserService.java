package com.geneinator.service;

import com.geneinator.dto.user.UserDto;
import com.geneinator.dto.user.UserUpdateRequest;
import com.geneinator.entity.User.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    UserDto findById(UUID id);

    UserDto findByUsername(String username);

    Page<UserDto> findAll(Pageable pageable);

    Page<UserDto> findByStatus(UserStatus status, Pageable pageable);

    UserDto update(UUID id, UserUpdateRequest request);

    void approveUser(UUID id);

    void suspendUser(UUID id);

    void delete(UUID id);
}
