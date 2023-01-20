package com.sda.eventapp.services;

import com.sda.eventapp.dto.EventView;
import com.sda.eventapp.mapper.EventMapper;
import com.sda.eventapp.model.Comment;
import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.Image;
import com.sda.eventapp.model.User;

import static org.assertj.core.api.Assertions.assertThat;

import com.sda.eventapp.repository.EventRepository;
import com.sda.eventapp.repository.ImageRepository;
import com.sda.eventapp.repository.UserRepository;
import com.sda.eventapp.service.EventService;
import com.sda.eventapp.service.ImageService;
import com.sda.eventapp.web.mvc.form.EventForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private ImageRepository imageRepository;
    @InjectMocks
    private EventService eventService;
    @Mock
    private ImageService imageService;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private EventView eventView;
    private User testUser1;
    private User testUser2;
    private Event eventTest1;
    private Event eventTest2;
    private Event eventTest3;


    private EventForm testEventForm;
    private Set<Comment> setCommentsTest = new HashSet<>();
    private Set<Event> setOwnedEventsTest = new HashSet<>();
    private Set<Event> setAttendingEvents = new HashSet<>();
    private Set<User> setUsersTest = new HashSet<>();
    private EventView eventView1;
    private EventView eventView2;
    private EventView eventView3;


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
        eventTest3 = Event.builder()
                .id(3L)
                .title("title3")
                .description("description3")
                .owner(testUser1)
                .startingDateTime(LocalDateTime.now().plusDays(2))
                .endingDateTime(LocalDateTime.now().plusDays(4))
                .build();
        testEventForm = new EventForm();
        testEventForm.setId(1);
        testEventForm.setTitle("test-title");
        testEventForm.setDescription("test-valid-description");
        testEventForm.setStartingDateTime(LocalDateTime.now().plusDays(4));
        testEventForm.setEndingDateTime(LocalDateTime.now().plusDays(6));
        eventView1 = EventView.builder()
                .title("eventViewTitle1")
                .description("evenViewDescription1")
                .startingDateTime(LocalDateTime.now().minusDays(10L))
                .endingDateTime(LocalDateTime.now().minusDays(9L))
                .ownerNickname("nick1")
                .build();
        eventView2 = EventView.builder()
                .title("eventViewTitle2")
                .description("evenViewDescription2")
                .startingDateTime(LocalDateTime.now().minusDays(3L))
                .endingDateTime(LocalDateTime.now().plusDays(2L))
                .ownerNickname("nick2")
                .build();
        eventView3 = EventView.builder()
                .title("eventViewTitle3")
                .description("evenViewDescription3")
                .startingDateTime(LocalDateTime.now().plusDays(2L))
                .endingDateTime(LocalDateTime.now().plusDays(5L))
                .ownerNickname("nick3")
                .build();


    }

    @Test
    void shouldSaveEvent() {
        eventTest1.setImage(Image.builder().build());

        testEventForm = new EventForm();
        testEventForm.setTitle("test-title");
        testEventForm.setDescription("test-valid-descriptiozn");
        testEventForm.setStartingDateTime(LocalDateTime.now().plusDays(4));
        testEventForm.setEndingDateTime(LocalDateTime.now().plusDays(6));
        MockMultipartFile file = new MockMultipartFile("test.json", "", "application/json", "{\"key1\": \"value1\"}".getBytes());
        when(eventMapper.toEvent(testEventForm)).thenReturn(eventTest1);
        when(eventRepository.save(eventTest1)).thenReturn(eventTest1);

        Event actual = eventService.save(testEventForm, testUser1, file);
        assertThat(actual.equals(eventTest1));
        assertTrue(actual.getTitle().equals(eventTest1.getTitle()));
        assertTrue(actual.getDescription().equals(eventTest1.getDescription()));
        assertTrue(actual.getOwner().getUsername().equals(eventTest1.getOwner().getUsername()));
        assertTrue(actual.getTitle().equals(eventTest1.getTitle()));
    }

    @Test
    void shouldFindEventById() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(eventTest1));
        Event actual = eventService.findById(anyLong());
        assertThat(actual).isEqualTo(eventTest1);
        assertNotNull(actual);
    }


    @Test
    void shouldThrowExceptionWhedIvenedtIdDoesNotExist() {
        Long idEvent = 2L;
        when(eventRepository.findById(idEvent)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> eventService.findById(idEvent))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Event with id " + idEvent + " not found");
    }

    @Test
    void shouldFindOwnerIdByEventId() {
        eventTest1.setOwner(testUser1);
        when(eventRepository.findById(eventTest1.getId())).thenReturn(Optional.of(eventTest1));
        Long acutalIdOwner = eventService.findOwnerIdByEventId(eventTest1.getId());
        assertEquals(eventTest1.getOwner().getId(), acutalIdOwner);
    }

    @Test
    void shouldSignUpForEvent() {
        eventTest1.setStartingDateTime(LocalDateTime.now().plusDays(2));
        eventTest1.setEndingDateTime(LocalDateTime.now().plusDays(3));
        eventTest1.setUsers(setUsersTest);
        when(eventRepository.findByIdFetchOwnerFetchUsers(eventTest1.getId())).thenReturn(Optional.of(eventTest1));
        Event returnedEvent = eventService.signUpForEvent(testUser1, eventTest1.getId());
        assertTrue(returnedEvent.getUsers().contains(testUser1));
    }

    @Test
    void ShouldNotSignUpForEvent_StartindDateEventIsPastTime() {
        eventTest1.setStartingDateTime(LocalDateTime.now().minusDays(1));
        eventTest1.setEndingDateTime(LocalDateTime.now().plusDays(3));
        eventTest1.setUsers(setUsersTest);
        when(eventRepository.findByIdFetchOwnerFetchUsers(eventTest1.getId())).thenReturn(Optional.of(eventTest1));
        Event returnedEvent = eventService.signUpForEvent(testUser1, eventTest1.getId());
        assertFalse(returnedEvent.getUsers().contains(testUser1));
    }

    @Test
    void shouldSignOutForEvent() {
        eventTest1.setStartingDateTime(LocalDateTime.now().plusDays(1));
        eventTest1.setEndingDateTime(LocalDateTime.now().plusDays(3));
        setUsersTest.add(testUser1);
        eventTest1.setUsers(setUsersTest);
        when(eventRepository.findByIdFetchOwnerFetchUsers(eventTest1.getId())).thenReturn(Optional.of(eventTest1));
        Event returnedEvent = eventService.signOutFromEvent(testUser1, eventTest1.getId());
        assertFalse(returnedEvent.getUsers().contains(testUser1));
    }

    @Test
    void shouldNotSignOutForEvent_EndingDateEventIsPastTime() {
        eventTest1.setStartingDateTime(LocalDateTime.now().minusDays(4));
        eventTest1.setEndingDateTime(LocalDateTime.now().minusDays(2));
        setUsersTest.add(testUser1);
        eventTest1.setUsers(setUsersTest);
        when(eventRepository.findByIdFetchOwnerFetchUsers(eventTest1.getId())).thenReturn(Optional.of(eventTest1));
        Event returnedEvent = eventService.signOutFromEvent(testUser1, eventTest1.getId());
        assertTrue(returnedEvent.getUsers().contains(testUser1));
    }

    @Test
    void shouldFindAllEventsViews() {
        when(eventMapper.toEventViewList(eventRepository.findAll())).thenReturn(List.of(eventView1, eventView2, eventView3));
        List<EventView> listResult = eventService.findAllEventViews();
        assertThat(listResult)
                .hasSize(3)
                .isNotEmpty()
                .containsExactlyElementsOf(List.of(eventView1, eventView2, eventView3));

    }

    @Test
    void shoulFindEventViewsByDateRange() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        when(eventMapper.toEventViewList(eventRepository.findAllEventByDateRange(start, end))).thenReturn(List.of(eventView2, eventView3));
        List<EventView> listResult = eventService.findEventViewsByDateRange(start, end);
        assertThat(listResult)
                .hasSize(2)
                .isNotEmpty()
                .containsExactlyElementsOf(List.of(eventView2, eventView3));
    }

    @Test
    void shouldReturnEmptyListWithEmptyDateRange() {
        LocalDateTime start = null;
        LocalDateTime end = null;
        when(eventMapper.toEventViewList(eventRepository.findAllEventByDateRange(start, end))).thenReturn(List.of());
        List<EventView> listResult = eventService.findEventViewsByDateRange(start, end);
        assertThat(listResult).isEmpty();
    }

    @Test
    void shouldFindByIdFetchOwnerFetchUsers() {
        Long id = 1L;
        when(eventRepository.findByIdFetchOwnerFetchUsers(id)).thenReturn(Optional.of(eventTest1));
        Event actualEvent = eventService.findByIdFetchOwnerFetchUsers(id);
        assertEquals(eventTest1, actualEvent);
    }

    @Test
    void shouldFindAllWithFilters_AllFutureEvents() {
        when(eventMapper.toEventViewList(this.eventRepository.findAllFutureEvents())).thenReturn(List.of(eventView3));
        List<EventView> actualList2 = eventService.findAllEventViews("", true, false, false);
        assertThat(actualList2)
                .hasSize(1);

    }
}

