package com.geneinator.dto.auth;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserInfoResponse {
    private UUID userId;
    private String email;
    private String displayName;
    private String role;
}
