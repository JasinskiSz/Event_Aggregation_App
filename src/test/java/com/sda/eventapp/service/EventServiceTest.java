package com.sda.eventapp.service;

import com.sda.eventapp.model.entities.Event;
import com.sda.eventapp.model.entities.User;
import com.sda.eventapp.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private EventService eventService;
    private User testUser1;
    private User testUser2;
    private Event eventTest1;
    private Event eventTest2;
    private Set<User> setUsersTest = new HashSet<>();

    @BeforeEach
    void prepareTestData() {

        testUser1 = User.builder()
                .id(1)
                .username("user-test")
                .email("user-test@gmail.com")
                .password("useruser")
                .attendingEvents(new HashSet<>())
                .ownedEvents(new HashSet<>())
                .comments(new HashSet<>())
                .build();
        testUser2 = User.builder()
                .id(2)
                .username("user-test2")
                .email("user-test2@gmail.com")
                .password("useruser2")
                .attendingEvents(new HashSet<>())
                .ownedEvents(new HashSet<>())
                .comments(new HashSet<>())
                .build();
        eventTest1 = Event.builder()
                .id(1L)
                .title("title1")
                .description("description1")
                .owner(testUser1)
                .build();
        eventTest2 = Event.builder()
                .id(2L)
                .title("title2")
                .description("description2")
                .owner(testUser2)
                .build();
    }

    @Test
    void shouldFindEventById() {
        Long eventId = 1L;
        eventTest1.setId(eventId);
        Long ownerId = 2L;
        testUser1.setId(ownerId);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventTest1));
        Event actual = eventService.findById(eventId);
        assertThat(actual).isEqualTo(eventTest1);
        assertNotNull(actual);
    }

    @Test
    void shouldFindByIdFetchOwnerFetchUsers() {
        Long id = 1L;
        when(eventRepository.findByIdFetchOwnerFetchUsersFetchImage(id)).thenReturn(Optional.of(eventTest1));
        Event actualEvent = eventService.findByIdFetchOwnerFetchUsersFetchImage(id);
        assertEquals(eventTest1, actualEvent);
    }

    @Test
    void shouldThrowExceptionWhenEvenedtIdDoesNotExist() {
        Long idEvent = 2L;
        when(eventRepository.findById(idEvent)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> eventService.findById(idEvent))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Event with id " + idEvent + " not found");
    }

    @Test
    void shouldFindOwnerIdByEventId() {
        Long eventId = 1L;
        eventTest1.setId(eventId);
        Long ownerId = 2L;
        testUser1.setId(ownerId);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventTest1));
        Long result = eventService.findOwnerIdByEventId(eventId);
        assertEquals(ownerId, result);
    }

    @Test
    void shouldFindOwnerIdByEventId2() {
        Long id = 1L;
        when(eventRepository.findById(id)).thenReturn(Optional.of(eventTest1));
        Event result = eventService.findById(1L);
        assertEquals(eventTest1, result);
    }

    @Test
    void shouldSignUpForEvent() {
        eventTest1.setStartingDateTime(LocalDateTime.now().plusDays(2));
        eventTest1.setEndingDateTime(LocalDateTime.now().plusDays(3));
        eventTest1.setUsers(setUsersTest);
        when(eventRepository.findByIdFetchOwnerFetchUsersFetchImage(eventTest1.getId())).thenReturn(Optional.of(eventTest1));
        Event returnedEvent = eventService.signUpForEvent(testUser2, eventTest1.getId());
        assertTrue(returnedEvent.getUsers().contains(testUser2));
        assertThat(eventTest1.getUsers()).hasSize(1);
    }

    @Test
    void shouldThrowExceptionWhenOwnerTrySignUpByEvent() {
        eventTest1.setUsers(setUsersTest);
        when(eventRepository.findByIdFetchOwnerFetchUsersFetchImage(eventTest1.getId())).thenReturn(Optional.of(eventTest1));
        assertThatThrownBy(() -> eventService.signUpForEvent(testUser1, eventTest1.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("403 FORBIDDEN \"ACCESS DENIED - OWNER CANNOT SIGN UP FOR AN EVENT\"");
    }

    @Test
    void shouldThrowExceptionWhenUserTrySignUpByEventThatAlreadyStarted() {
        eventTest1.setStartingDateTime(LocalDateTime.now().minusDays(1));
        eventTest1.setEndingDateTime(LocalDateTime.now().plusDays(3));
        eventTest1.setUsers(setUsersTest);
        when(eventRepository.findByIdFetchOwnerFetchUsersFetchImage(eventTest1.getId())).thenReturn(Optional.of(eventTest1));
        assertThatThrownBy(() -> eventService.signUpForEvent(testUser2, eventTest1.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("400 BAD_REQUEST \"ACCESS DENIED - CANNOT SIGN UP FOR AN EVENT THAT HAS ALREADY STARTED\"");
    }

    @Test
    void shouldThrowExceptionWhenUserTrySignUpByEventWhenIsAlreadyAssigned() {
        eventTest1.setStartingDateTime(LocalDateTime.now().plusDays(1));
        eventTest1.setEndingDateTime(LocalDateTime.now().plusDays(3));
        eventTest1.setUsers(setUsersTest);
        setUsersTest.add(testUser2);
        when(eventRepository.findByIdFetchOwnerFetchUsersFetchImage(eventTest1.getId())).thenReturn(Optional.of(eventTest1));
        assertThatThrownBy(() -> eventService.signUpForEvent(testUser2, eventTest1.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("400 BAD_REQUEST \"ACCESS DENIED - CANNOT SIGN UP FOR AN EVENT IF ALREADY ASSIGNED\"");
    }

    @Test
    void shouldSignOutForEvent() {
        eventTest1.setStartingDateTime(LocalDateTime.now().plusDays(1));
        eventTest1.setEndingDateTime(LocalDateTime.now().plusDays(3));
        setUsersTest.add(testUser2);
        eventTest1.setUsers(setUsersTest);
        when(eventRepository.findByIdFetchOwnerFetchUsersFetchImage(eventTest1.getId())).thenReturn(Optional.of(eventTest1));
        Event returnedEvent = eventService.signOutFromEvent(testUser2, eventTest1.getId());
        assertFalse(returnedEvent.getUsers().contains(testUser2));
    }

    @Test
    void shouldThrowExceptionWhenOwnerTrySignOutByEvent() {
        eventTest1.setStartingDateTime(LocalDateTime.now().plusDays(2));
        eventTest1.setEndingDateTime(LocalDateTime.now().plusDays(3));
        eventTest1.setUsers(setUsersTest);
        eventTest1.setOwner(testUser1);
        when(eventRepository.findByIdFetchOwnerFetchUsersFetchImage(eventTest1.getId())).thenReturn(Optional.of(eventTest1));
        assertThatThrownBy(() -> eventService.signOutFromEvent(testUser1, eventTest1.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("403 FORBIDDEN \"ACCESS DENIED - OWNER CANNOT SIGN OUT FROM AN EVENT\"");
    }

    @Test
    void shouldThrowExceptionWhenUserTrySignOutEventThatAlreadyStarted() {
        eventTest1.setStartingDateTime(LocalDateTime.now().minusDays(1));
        eventTest1.setEndingDateTime(LocalDateTime.now().plusDays(3));
        eventTest1.setUsers(setUsersTest);
        when(eventRepository.findByIdFetchOwnerFetchUsersFetchImage(eventTest1.getId())).thenReturn(Optional.of(eventTest1));
        assertThatThrownBy(() -> eventService.signOutFromEvent(testUser2, eventTest1.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("400 BAD_REQUEST \"ACCESS DENIED - CANNOT SIGN OUT FROM AN EVENT THAT HAS ALREADY STARTED\"");
    }

    @Test
    void shouldThrowExceptionWhenUserTrySignOutIfHasNotAssigned() {
        eventTest1.setStartingDateTime(LocalDateTime.now().plusDays(1));
        eventTest1.setEndingDateTime(LocalDateTime.now().plusDays(3));
        eventTest1.setUsers(setUsersTest);
        when(eventRepository.findByIdFetchOwnerFetchUsersFetchImage(eventTest1.getId())).thenReturn(Optional.of(eventTest1));
        assertThatThrownBy(() -> eventService.signOutFromEvent(testUser2, eventTest1.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("400 BAD_REQUEST \"ACCESS DENIED - CANNOT SIGN OUT FROM AN EVENT IF HAS NOT ASSIGNED\"");
    }
}
