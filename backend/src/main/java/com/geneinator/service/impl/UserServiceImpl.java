package com.geneinator.service.impl;

import com.geneinator.dto.user.UserDto;
import com.geneinator.dto.user.UserUpdateRequest;
import com.geneinator.entity.User;
import com.geneinator.entity.User.UserStatus;
import com.geneinator.exception.ResourceNotFoundException;
import com.geneinator.repository.UserRepository;
import com.geneinator.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> findByStatus(UserStatus status, Pageable pageable) {
        return userRepository.findByStatus(status, pageable).map(this::toDto);
    }

    @Override
    @Transactional
    public UserDto update(UUID id, UserUpdateRequest request) {
        log.info("Updating user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (request.getPassword() != null) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        User saved = userRepository.save(user);
        log.info("User updated: {}", id);

        return toDto(saved);
    }

    @Override
    @Transactional
    public void approveUser(UUID id) {
        log.info("Approving user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        log.info("User approved: {}", id);
    }

    @Override
    @Transactional
    public void suspendUser(UUID id) {
        log.info("Suspending user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setStatus(UserStatus.SUSPENDED);
        userRepository.save(user);

        log.info("User suspended: {}", id);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        userRepository.delete(user);
        log.info("User deleted: {}", id);
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }
}
