package com.geneinator.dto.user;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class UserDto {
    private UUID id;
    private String email;
    private String displayName;
    private String role;
    private String status;
    private Instant createdAt;
    private Instant lastLogin;
}
