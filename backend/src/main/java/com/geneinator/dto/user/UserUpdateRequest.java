package com.geneinator.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class UserUpdateRequest {
    private String password;
}
