package com.geneinator.service.impl;

import com.geneinator.dto.common.ApproximateDateDto;
import com.geneinator.dto.event.EventCreateRequest;
import com.geneinator.dto.event.EventDto;
import com.geneinator.dto.event.EventUpdateRequest;
import com.geneinator.entity.ApproximateDate;
import com.geneinator.entity.Event;
import com.geneinator.entity.Event.EventType;
import com.geneinator.entity.EventParticipant;
import com.geneinator.entity.EventParticipant.EventParticipantId;
import com.geneinator.entity.Person;
import com.geneinator.exception.ResourceNotFoundException;
import com.geneinator.repository.EventRepository;
import com.geneinator.repository.PersonRepository;
import com.geneinator.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final PersonRepository personRepository;

    @Override
    @Transactional(readOnly = true)
    public EventDto findById(UUID id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        return toDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventDto> findAll(Pageable pageable) {
        return eventRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventDto> findByPersonId(UUID personId, Pageable pageable) {
        return eventRepository.findByPersonId(personId, pageable).map(this::toDto);
    }

    @Override
    @Transactional
    public EventDto create(EventCreateRequest request, UUID createdBy) {
        log.info("Creating event: {} ({})", request.getTitle(), request.getEventType());

        Event event = Event.builder()
                .eventType(EventType.valueOf(request.getEventType()))
                .title(request.getTitle())
                .description(request.getDescription())
                .eventDate(toEntity(request.getEventDate()))
                .location(request.getLocation())
                .createdBy(createdBy)
                .privacySettings(request.getPrivacySettings())
                .build();

        if (request.getParticipants() != null && !request.getParticipants().isEmpty()) {
            Set<EventParticipant> participants = new HashSet<>();
            for (EventCreateRequest.ParticipantRequest participantRequest : request.getParticipants()) {
                Person person = personRepository.findById(participantRequest.getPersonId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Person not found with id: " + participantRequest.getPersonId()));

                EventParticipant participant = EventParticipant.builder()
                        .id(new EventParticipantId(null, participantRequest.getPersonId()))
                        .event(event)
                        .person(person)
                        .role(participantRequest.getRole())
                        .build();
                participants.add(participant);
            }
            event.setParticipants(participants);
        }

        Event saved = eventRepository.save(event);
        log.info("Event created with id: {}", saved.getId());

        return toDto(saved);
    }

    @Override
    @Transactional
    public EventDto update(UUID id, EventUpdateRequest request) {
        log.info("Updating event: {}", id);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        if (request.getEventType() != null) {
            event.setEventType(EventType.valueOf(request.getEventType()));
        }
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            event.setEventDate(toEntity(request.getEventDate()));
        }
        if (request.getLocation() != null) {
            event.setLocation(request.getLocation());
        }
        if (request.getPrivacySettings() != null) {
            event.setPrivacySettings(request.getPrivacySettings());
        }

        if (request.getParticipants() != null) {
            event.getParticipants().clear();

            for (EventUpdateRequest.ParticipantRequest participantRequest : request.getParticipants()) {
                Person person = personRepository.findById(participantRequest.getPersonId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Person not found with id: " + participantRequest.getPersonId()));

                EventParticipant participant = EventParticipant.builder()
                        .id(new EventParticipantId(event.getId(), participantRequest.getPersonId()))
                        .event(event)
                        .person(person)
                        .role(participantRequest.getRole())
                        .build();
                event.getParticipants().add(participant);
            }
        }

        Event saved = eventRepository.save(event);
        log.info("Event updated: {}", id);

        return toDto(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting event: {}", id);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        eventRepository.delete(event);
        log.info("Event deleted: {}", id);
    }

    private EventDto toDto(Event event) {
        List<EventDto.EventParticipantDto> participants = Collections.emptyList();
        if (event.getParticipants() != null && !event.getParticipants().isEmpty()) {
            participants = event.getParticipants().stream()
                    .map(p -> EventDto.EventParticipantDto.builder()
                            .personId(p.getPerson().getId())
                            .personName(p.getPerson().getFullName())
                            .role(p.getRole())
                            .build())
                    .collect(Collectors.toList());
        }

        return EventDto.builder()
                .id(event.getId())
                .eventType(event.getEventType().name())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(toDto(event.getEventDate()))
                .location(event.getLocation())
                .participants(participants)
                .createdAt(event.getCreatedAt())
                .build();
    }

    private ApproximateDateDto toDto(ApproximateDate date) {
        if (date == null) return null;
        return ApproximateDateDto.builder()
                .year(date.getYear())
                .month(date.getMonth())
                .day(date.getDay())
                .isApproximate(date.getIsApproximate())
                .dateText(date.getDateText())
                .build();
    }

    private ApproximateDate toEntity(ApproximateDateDto dto) {
        if (dto == null) return null;
        return ApproximateDate.builder()
                .year(dto.getYear())
                .month(dto.getMonth())
                .day(dto.getDay())
                .isApproximate(dto.getIsApproximate())
                .dateText(dto.getDateText())
                .build();
    }
}
