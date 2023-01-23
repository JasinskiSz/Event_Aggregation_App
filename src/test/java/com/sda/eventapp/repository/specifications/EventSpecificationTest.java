package com.sda.eventapp.repository.specifications;

import com.sda.eventapp.model.entities.Event;
import com.sda.eventapp.model.entities.User;
import com.sda.eventapp.repository.EventRepository;
import com.sda.eventapp.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Java6Assertions.assertThat;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class EventSpecificationTest {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    private final User user1 = User.builder()
            .username("user1")
            .email("user1@gmail.com")
            .password("user1user1")
            .build();
    private final User user2 = User.builder()
            .username("user2")
            .email("user2@gmail.com")
            .password("user2user2")
            .build();
    private final Event pastEventOwnedByUser1AttendedByUser2 = Event.builder()
            .title("Past event owned by user1 attended by user2")
            .description("Past event description")
            .startingDateTime(LocalDateTime.now().minusDays(7))
            .endingDateTime(LocalDateTime.now().minusDays(3))
            .owner(user1)
            .users(Set.of(user2))
            .build();
    private final Event pastEventOwnedByUser2AttendedByUser1 = Event.builder()
            .title("Past event owned by user2 attended by user1")
            .description("Past event description")
            .startingDateTime(LocalDateTime.now().minusDays(7))
            .endingDateTime(LocalDateTime.now().minusDays(3))
            .owner(user2)
            .users(Set.of(user1))
            .build();
    private final Event ongoingEventOwnedByUser1AttendedByUser2 = Event.builder()
            .title("Ongoing event owned by user1 attended by user2")
            .description("Ongoing event description")
            .startingDateTime(LocalDateTime.now().minusDays(3))
            .endingDateTime(LocalDateTime.now().plusDays(4))
            .owner(user1)
            .users(Set.of(user2))
            .build();
    private final Event ongoingEventOwnedByUser2AttendedByUser1 = Event.builder()
            .title("Ongoing event owned by user2 attended by user1")
            .description("Ongoing event description")
            .startingDateTime(LocalDateTime.now().minusDays(3))
            .endingDateTime(LocalDateTime.now().plusDays(4))
            .owner(user2)
            .users(Set.of(user1))
            .build();
    private final Event futureEventOwnedByUser1AttendedByUser2 = Event.builder()
            .title("Future event owned by user1 attended by user2")
            .description("Future event description")
            .startingDateTime(LocalDateTime.now().plusDays(2))
            .endingDateTime(LocalDateTime.now().plusDays(5))
            .owner(user1)
            .users(Set.of(user2))
            .build();
    private final Event futureEventOwnedByUser2AttendedByUser1 = Event.builder()
            .title("Future event owned by user2 attended by user1")
            .description("Future event description")
            .startingDateTime(LocalDateTime.now().plusDays(2))
            .endingDateTime(LocalDateTime.now().plusDays(5))
            .owner(user2)
            .users(Set.of(user1))
            .build();

    @BeforeEach
    void beforeEach() {
        userRepository.save(user1);
        userRepository.save(user2);
        eventRepository.save(pastEventOwnedByUser2AttendedByUser1);
        eventRepository.save(pastEventOwnedByUser1AttendedByUser2);
        eventRepository.save(ongoingEventOwnedByUser2AttendedByUser1);
        eventRepository.save(ongoingEventOwnedByUser1AttendedByUser2);
        eventRepository.save(futureEventOwnedByUser2AttendedByUser1);
        eventRepository.save(futureEventOwnedByUser1AttendedByUser2);
    }

    @AfterEach
    void afterEach() {
        eventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldReturnPastEvents() {
        //given
        Specification<Event> specification = EventSpecification.isPast();
        //when
        List<Event> actual = eventRepository.findAll(specification);
        //then
        assertThat(actual)
                .isNotNull()
                .hasSize(2)
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "Past event owned by user1 attended by user2",
                        "Past event owned by user2 attended by user1");
    }

    @Test
    void shouldReturnOngoingEvents() {
        //given
        Specification<Event> specification = EventSpecification.isOngoing();
        //when
        List<Event> actual = eventRepository.findAll(specification);
        //then
        assertThat(actual)
                .isNotNull()
                .hasSize(2)
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "Ongoing event owned by user1 attended by user2",
                        "Ongoing event owned by user2 attended by user1");
    }

    @Test
    void shouldReturnFutureEvents() {
        //given
        Specification<Event> specification = EventSpecification.isFuture();
        //when
        List<Event> actual = eventRepository.findAll(specification);
        //then
        assertThat(actual)
                .isNotNull()
                .hasSize(2)
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "Future event owned by user1 attended by user2",
                        "Future event owned by user2 attended by user1");
    }

    @Test
    void shouldReturnPastAndOngoingEvents() {
        //given
        Specification<Event> specification = EventSpecification.isPast().or(EventSpecification.isOngoing());
        //when
        List<Event> actual = eventRepository.findAll(specification);
        //then
        assertThat(actual)
                .isNotNull()
                .hasSize(4)
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "Past event owned by user1 attended by user2",
                        "Past event owned by user2 attended by user1",
                        "Ongoing event owned by user1 attended by user2",
                        "Ongoing event owned by user2 attended by user1");
    }

    @Test
    void shouldReturnPastAndFutureEvents() {
        //given
        Specification<Event> specification = EventSpecification.isPast().or(EventSpecification.isFuture());
        //when
        List<Event> actual = eventRepository.findAll(specification);
        //then
        assertThat(actual)
                .isNotNull()
                .hasSize(4)
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "Past event owned by user1 attended by user2",
                        "Past event owned by user2 attended by user1",
                        "Future event owned by user1 attended by user2",
                        "Future event owned by user2 attended by user1");
    }

    @Test
    void shouldReturnOngoingAndFutureEvents() {
        //given
        Specification<Event> specification = EventSpecification.isOngoing().or(EventSpecification.isFuture());
        //when
        List<Event> actual = eventRepository.findAll(specification);
        //then
        assertThat(actual)
                .isNotNull()
                .hasSize(4)
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "Ongoing event owned by user1 attended by user2",
                        "Ongoing event owned by user2 attended by user1",
                        "Future event owned by user1 attended by user2",
                        "Future event owned by user2 attended by user1");
    }

    @Test
    void shouldReturnPastAndOngoingAndFutureEvents() {
        //given
        Specification<Event> specification = EventSpecification.isOngoing()
                .or(EventSpecification.isFuture())
                .or(EventSpecification.isPast());
        //when
        List<Event> actual = eventRepository.findAll(specification);
        //then
        assertThat(actual)
                .isNotNull()
                .hasSize(6)
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "Ongoing event owned by user1 attended by user2",
                        "Ongoing event owned by user2 attended by user1",
                        "Future event owned by user1 attended by user2",
                        "Future event owned by user2 attended by user1",
                        "Past event owned by user1 attended by user2",
                        "Past event owned by user2 attended by user1");
    }

    @Test
    void shouldReturnEventsOwnedByUser1() {
        //given
        Specification<Event> specification = EventSpecification.isOwnedBy(user1.getId());
        //when
        List<Event> actual = eventRepository.findAll(specification);
        //then
        assertThat(actual)
                .isNotNull()
                .hasSize(3)
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "Future event owned by user1 attended by user2",
                        "Past event owned by user1 attended by user2",
                        "Ongoing event owned by user1 attended by user2");
    }

    @Test
    void shouldReturnEventsOwnedByUser2() {
        //given
        Specification<Event> specification = EventSpecification.isOwnedBy(user2.getId());
        //when
        List<Event> actual = eventRepository.findAll(specification);
        //then
        assertThat(actual)
                .isNotNull()
                .hasSize(3)
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "Future event owned by user2 attended by user1",
                        "Past event owned by user2 attended by user1",
                        "Ongoing event owned by user2 attended by user1");
    }

    @Test
    void shouldReturnEventsAttendedByUser1() {
        //given
        Specification<Event> specification = EventSpecification.isAttendedBy(user1.getId());
        //when
        List<Event> actual = eventRepository.findAll(specification);
        //then
        assertThat(actual)
                .isNotNull()
                .hasSize(3)
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "Future event owned by user2 attended by user1",
                        "Past event owned by user2 attended by user1",
                        "Ongoing event owned by user2 attended by user1");
    }

    @Test
    void shouldReturnEventsAttendedByUser2() {
        //given
        Specification<Event> specification = EventSpecification.isAttendedBy(user2.getId());
        //when
        List<Event> actual = eventRepository.findAll(specification);
        //then
        assertThat(actual)
                .isNotNull()
                .hasSize(3)
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "Future event owned by user1 attended by user2",
                        "Past event owned by user1 attended by user2",
                        "Ongoing event owned by user1 attended by user2");
    }

    @Test
    void shouldReturnEventsOwnedAndAttendedByUser1() {
        //given
        Specification<Event> specification = EventSpecification.isOwnedBy(user1.getId())
                .or(EventSpecification.isAttendedBy(user1.getId()));
        //when
        List<Event> actual = eventRepository.findAll(specification);
        //then
        assertThat(actual)
                .isNotNull()
                .hasSize(6)
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "Future event owned by user1 attended by user2",
                        "Past event owned by user1 attended by user2",
                        "Ongoing event owned by user1 attended by user2",
                        "Future event owned by user2 attended by user1",
                        "Past event owned by user2 attended by user1",
                        "Ongoing event owned by user2 attended by user1");
    }

    @Test
    void shouldReturnPastEventsAndOwnedByUser1() {
        //given
        Specification<Event> specification = EventSpecification.isPast()
                .and(EventSpecification.isOwnedBy(user1.getId()));
        //when
        List<Event> actual = eventRepository.findAll(specification);
        //then
        assertThat(actual)
                .isNotNull()
                .hasSize(1)
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "Past event owned by user1 attended by user2");
    }

    @Test
    void shouldReturnOngoingEventsAndOwnedByUser1() {
        //given
        Specification<Event> specification = EventSpecification.isOngoing()
                .and(EventSpecification.isOwnedBy(user1.getId()));
        //when
        List<Event> actual = eventRepository.findAll(specification);
        //then
        assertThat(actual)
                .isNotNull()
                .hasSize(1)
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "Ongoing event owned by user1 attended by user2");
    }

    @Test
    void shouldReturnFutureEventsAndOwnedByUser1() {
        //given
        Specification<Event> specification = EventSpecification.isFuture()
                .and(EventSpecification.isOwnedBy(user1.getId()));
        //when
        List<Event> actual = eventRepository.findAll(specification);
        //then
        assertThat(actual)
                .isNotNull()
                .hasSize(1)
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "Future event owned by user1 attended by user2");
    }

    @Test
    void shouldReturnPastEventsAndAttendedByUser1() {
        //given
        Specification<Event> specification = EventSpecification.isPast()
                .and(EventSpecification.isAttendedBy(user1.getId()));
        //when
        List<Event> actual = eventRepository.findAll(specification);
        //then
        assertThat(actual)
                .isNotNull()
                .hasSize(1)
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "Past event owned by user2 attended by user1");
    }

    @Test
    void shouldReturnOngoingEventsAndAttendedByUser1() {
        //given
        Specification<Event> specification = EventSpecification.isOngoing()
                .and(EventSpecification.isAttendedBy(user1.getId()));
        //when
        List<Event> actual = eventRepository.findAll(specification);
        //then
        assertThat(actual)
                .isNotNull()
                .hasSize(1)
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "Ongoing event owned by user2 attended by user1");
    }

    @Test
    void shouldReturnFutureEventsAndAttendedByUser1() {
        //given
        Specification<Event> specification = EventSpecification.isFuture()
                .and(EventSpecification.isAttendedBy(user1.getId()));
        //when
        List<Event> actual = eventRepository.findAll(specification);
        //then
        assertThat(actual)
                .isNotNull()
                .hasSize(1)
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "Future event owned by user2 attended by user1");
    }
    @NullAndEmptySource
    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    void shouldReturnEventsByBlankOrNullTitle(String input) {
        //given
        Specification<Event> specification = EventSpecification.titleContains(input);
        //when
        List<Event> actual = eventRepository.findAll(specification);
        //then
        assertThat(actual)
                .isNotNull()
                .hasSize(6)
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "Future event owned by user1 attended by user2",
                        "Past event owned by user1 attended by user2",
                        "Ongoing event owned by user1 attended by user2",
                        "Future event owned by user2 attended by user1",
                        "Past event owned by user2 attended by user1",
                        "Ongoing event owned by user2 attended by user1");
    }

    @Test
    void shouldReturnEventsByTitle(){
            //given
            Specification<Event> specification = EventSpecification.titleContains("user1 att");
            //when
            List<Event> actual = eventRepository.findAll(specification);
            //then
            assertThat(actual)
                    .isNotNull()
                    .hasSize(3)
                    .extracting("title")
                    .containsExactlyInAnyOrder(
                            "Future event owned by user1 attended by user2",
                            "Past event owned by user1 attended by user2",
                            "Ongoing event owned by user1 attended by user2");
    }

    @Test
    void shouldNotReturnEventsByTitle(){
        //given
        Specification<Event> specification = EventSpecification.titleContains("mockTitle");
        //when
        List<Event> actual = eventRepository.findAll(specification);
        //then
        assertThat(actual)
                .isNotNull()
                .hasSize(0);
    }
}