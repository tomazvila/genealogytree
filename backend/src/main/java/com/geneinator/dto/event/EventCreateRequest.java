package com.geneinator.dto.event;

import com.geneinator.dto.common.ApproximateDateDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class EventCreateRequest {

    @NotNull(message = "Event type is required")
    private String eventType;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private ApproximateDateDto eventDate;
    private String location;
    private List<ParticipantRequest> participants;
    private Map<String, Object> privacySettings;

    @Data
    @Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ParticipantRequest {
        private UUID personId;
        private String role;
    }
}
