package com.geneinator.dto.auth;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserInfoResponse {
    private UUID userId;
    private String username;
    private String role;
}
