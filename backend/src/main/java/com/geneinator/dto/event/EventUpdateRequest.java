package com.geneinator.dto.event;

import com.geneinator.dto.common.ApproximateDateDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class EventUpdateRequest {
    private String eventType;
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
