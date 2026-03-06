package com.geneinator.dto.event;

import com.geneinator.dto.common.ApproximateDateDto;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class EventDto {
    private UUID id;
    private String eventType;
    private String title;
    private String description;
    private ApproximateDateDto eventDate;
    private String location;
    private List<EventParticipantDto> participants;
    private Instant createdAt;

    @Data
    @Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class EventParticipantDto {
        private UUID personId;
        private String personName;
        private String role;
    }
}
