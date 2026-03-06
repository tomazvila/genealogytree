package com.geneinator.repository;

import com.geneinator.entity.ApproximateDate;
import com.geneinator.entity.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("PersonRepository")
class PersonRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PersonRepository personRepository;

    @Test
    @DisplayName("should search persons by name (case insensitive)")
    void shouldSearchByNameCaseInsensitive() {
        // Given
        Person person = Person.builder()
                .fullName("Jonas Mažvila")
                .birthDate(ApproximateDate.fromYear(1925))
                .createdBy(UUID.randomUUID())
                .build();
        entityManager.persistAndFlush(person);

        // When
        Page<Person> result = personRepository.searchByName("jonas", PageRequest.of(0, 10));

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFullName()).isEqualTo("Jonas Mažvila");
    }

    @Test
    @DisplayName("should search persons by partial name")
    void shouldSearchByPartialName() {
        // Given
        Person person1 = Person.builder()
                .fullName("Jonas Mažvila")
                .birthDate(ApproximateDate.fromYear(1925))
                .createdBy(UUID.randomUUID())
                .build();
        Person person2 = Person.builder()
                .fullName("Ona Mažvilaitė")
                .birthDate(ApproximateDate.fromYear(1930))
                .createdBy(UUID.randomUUID())
                .build();
        entityManager.persistAndFlush(person1);
        entityManager.persistAndFlush(person2);

        // When
        Page<Person> result = personRepository.searchByName("Mažvil", PageRequest.of(0, 10));

        // Then
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("should find persons by birth year range")
    void shouldFindByBirthYearRange() {
        // Given
        Person person1 = Person.builder()
                .fullName("Person 1900")
                .birthDate(ApproximateDate.fromYear(1900))
                .createdBy(UUID.randomUUID())
                .build();
        Person person2 = Person.builder()
                .fullName("Person 1925")
                .birthDate(ApproximateDate.fromYear(1925))
                .createdBy(UUID.randomUUID())
                .build();
        Person person3 = Person.builder()
                .fullName("Person 1950")
                .birthDate(ApproximateDate.fromYear(1950))
                .createdBy(UUID.randomUUID())
                .build();
        entityManager.persistAndFlush(person1);
        entityManager.persistAndFlush(person2);
        entityManager.persistAndFlush(person3);

        // When
        Page<Person> result = personRepository.findByBirthYearBetween(1910, 1940, PageRequest.of(0, 10));

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFullName()).isEqualTo("Person 1925");
    }
}
