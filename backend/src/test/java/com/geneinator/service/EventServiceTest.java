package com.geneinator.service;

import com.geneinator.dto.common.ApproximateDateDto;
import com.geneinator.dto.event.EventCreateRequest;
import com.geneinator.dto.event.EventDto;
import com.geneinator.dto.event.EventUpdateRequest;
import com.geneinator.entity.ApproximateDate;
import com.geneinator.entity.Event;
import com.geneinator.entity.Event.EventType;
import com.geneinator.entity.EventParticipant;
import com.geneinator.entity.Person;
import com.geneinator.exception.ResourceNotFoundException;
import com.geneinator.repository.EventRepository;
import com.geneinator.repository.PersonRepository;
import com.geneinator.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventService")
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private PersonRepository personRepository;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventService = new EventServiceImpl(eventRepository, personRepository);
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("should create wedding event with participants")
        void shouldCreateWeddingEventWithParticipants() {
            // Given
            UUID createdBy = UUID.randomUUID();
            UUID brideId = UUID.randomUUID();
            UUID groomId = UUID.randomUUID();

            Person bride = Person.builder()
                    .fullName("Ona Petraitytė")
                    .createdBy(createdBy)
                    .build();
            Person groom = Person.builder()
                    .fullName("Jonas Mažvila")
                    .createdBy(createdBy)
                    .build();

            EventCreateRequest request = EventCreateRequest.builder()
                    .eventType("WEDDING")
                    .title("Jonas and Ona Wedding")
                    .description("Wedding ceremony in Vilnius")
                    .eventDate(ApproximateDateDto.builder().year(1920).month(6).day(15).build())
                    .location("Vilnius, Lithuania")
                    .participants(List.of(
                            EventCreateRequest.ParticipantRequest.builder()
                                    .personId(brideId)
                                    .role("BRIDE")
                                    .build(),
                            EventCreateRequest.ParticipantRequest.builder()
                                    .personId(groomId)
                                    .role("GROOM")
                                    .build()
                    ))
                    .build();

            when(personRepository.findById(brideId)).thenReturn(Optional.of(bride));
            when(personRepository.findById(groomId)).thenReturn(Optional.of(groom));
            when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            EventDto result = eventService.create(request, createdBy);

            // Then
            assertThat(result.getEventType()).isEqualTo("WEDDING");
            assertThat(result.getTitle()).isEqualTo("Jonas and Ona Wedding");
            assertThat(result.getLocation()).isEqualTo("Vilnius, Lithuania");
            assertThat(result.getParticipants()).hasSize(2);
            verify(eventRepository).save(any(Event.class));
        }

        @Test
        @DisplayName("should create birth event")
        void shouldCreateBirthEvent() {
            // Given
            UUID createdBy = UUID.randomUUID();

            EventCreateRequest request = EventCreateRequest.builder()
                    .eventType("BIRTH")
                    .title("Birth of Petras Mažvila")
                    .eventDate(ApproximateDateDto.builder().year(1925).build())
                    .build();

            when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            EventDto result = eventService.create(request, createdBy);

            // Then
            assertThat(result.getEventType()).isEqualTo("BIRTH");
            assertThat(result.getTitle()).isEqualTo("Birth of Petras Mažvila");
            assertThat(result.getEventDate().getYear()).isEqualTo(1925);
        }

        @Test
        @DisplayName("should throw exception when participant not found")
        void shouldThrowExceptionWhenParticipantNotFound() {
            // Given
            UUID createdBy = UUID.randomUUID();
            UUID unknownPersonId = UUID.randomUUID();

            EventCreateRequest request = EventCreateRequest.builder()
                    .eventType("WEDDING")
                    .title("Test Wedding")
                    .participants(List.of(
                            EventCreateRequest.ParticipantRequest.builder()
                                    .personId(unknownPersonId)
                                    .role("BRIDE")
                                    .build()
                    ))
                    .build();

            when(personRepository.findById(unknownPersonId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> eventService.create(request, createdBy))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return event when found")
        void shouldReturnEventWhenFound() {
            // Given
            UUID eventId = UUID.randomUUID();
            Event event = Event.builder()
                    .eventType(EventType.GRADUATION)
                    .title("University Graduation")
                    .createdBy(UUID.randomUUID())
                    .build();

            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

            // When
            EventDto result = eventService.findById(eventId);

            // Then
            assertThat(result.getEventType()).isEqualTo("GRADUATION");
            assertThat(result.getTitle()).isEqualTo("University Graduation");
        }

        @Test
        @DisplayName("should throw exception when event not found")
        void shouldThrowExceptionWhenNotFound() {
            // Given
            UUID eventId = UUID.randomUUID();
            when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> eventService.findById(eventId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByPersonId")
    class FindByPersonId {

        @Test
        @DisplayName("should return events for a person")
        void shouldReturnEventsForPerson() {
            // Given
            UUID personId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            Event event = Event.builder()
                    .eventType(EventType.WEDDING)
                    .title("Wedding Event")
                    .createdBy(UUID.randomUUID())
                    .build();

            when(eventRepository.findByPersonId(personId, pageable))
                    .thenReturn(new PageImpl<>(List.of(event)));

            // When
            Page<EventDto> result = eventService.findByPersonId(personId, pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getEventType()).isEqualTo("WEDDING");
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("should update event")
        void shouldUpdateEvent() {
            // Given
            UUID eventId = UUID.randomUUID();
            Event existingEvent = Event.builder()
                    .eventType(EventType.WEDDING)
                    .title("Old Title")
                    .description("Old Description")
                    .createdBy(UUID.randomUUID())
                    .build();

            EventUpdateRequest request = EventUpdateRequest.builder()
                    .title("New Title")
                    .description("New Description")
                    .location("New Location")
                    .build();

            when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
            when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            EventDto result = eventService.update(eventId, request);

            // Then
            assertThat(result.getTitle()).isEqualTo("New Title");
            assertThat(result.getDescription()).isEqualTo("New Description");
            assertThat(result.getLocation()).isEqualTo("New Location");
        }

        @Test
        @DisplayName("should update event participants")
        void shouldUpdateEventParticipants() {
            // Given
            UUID eventId = UUID.randomUUID();
            UUID newPersonId = UUID.randomUUID();

            Person newPerson = Person.builder()
                    .fullName("New Participant")
                    .createdBy(UUID.randomUUID())
                    .build();

            Event existingEvent = Event.builder()
                    .eventType(EventType.WEDDING)
                    .title("Wedding")
                    .createdBy(UUID.randomUUID())
                    .build();

            EventUpdateRequest request = EventUpdateRequest.builder()
                    .participants(List.of(
                            EventUpdateRequest.ParticipantRequest.builder()
                                    .personId(newPersonId)
                                    .role("WITNESS")
                                    .build()
                    ))
                    .build();

            when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
            when(personRepository.findById(newPersonId)).thenReturn(Optional.of(newPerson));
            when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            EventDto result = eventService.update(eventId, request);

            // Then
            assertThat(result.getParticipants()).hasSize(1);
            assertThat(result.getParticipants().get(0).getRole()).isEqualTo("WITNESS");
        }

        @Test
        @DisplayName("should throw exception when event not found for update")
        void shouldThrowExceptionWhenNotFoundForUpdate() {
            // Given
            UUID eventId = UUID.randomUUID();
            EventUpdateRequest request = EventUpdateRequest.builder().title("New Title").build();

            when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> eventService.update(eventId, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should delete event")
        void shouldDeleteEvent() {
            // Given
            UUID eventId = UUID.randomUUID();
            Event event = Event.builder()
                    .eventType(EventType.WEDDING)
                    .title("Wedding")
                    .createdBy(UUID.randomUUID())
                    .build();

            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

            // When
            eventService.delete(eventId);

            // Then
            verify(eventRepository).delete(event);
        }

        @Test
        @DisplayName("should throw exception when event not found for delete")
        void shouldThrowExceptionWhenNotFoundForDelete() {
            // Given
            UUID eventId = UUID.randomUUID();
            when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> eventService.delete(eventId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return paginated events")
        void shouldReturnPaginatedEvents() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Event event1 = Event.builder()
                    .eventType(EventType.WEDDING)
                    .title("Wedding 1")
                    .createdBy(UUID.randomUUID())
                    .build();
            Event event2 = Event.builder()
                    .eventType(EventType.BIRTH)
                    .title("Birth 1")
                    .createdBy(UUID.randomUUID())
                    .build();

            when(eventRepository.findAll(pageable))
                    .thenReturn(new PageImpl<>(List.of(event1, event2)));

            // When
            Page<EventDto> result = eventService.findAll(pageable);

            // Then
            assertThat(result.getContent()).hasSize(2);
        }
    }
}
