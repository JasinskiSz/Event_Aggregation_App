package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.configuration.SecurityConfig;
import com.sda.eventapp.mapper.EventMapper;
import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.Image;
import com.sda.eventapp.model.User;
import com.sda.eventapp.repository.EventRepository;
import com.sda.eventapp.repository.UserRepository;
import com.sda.eventapp.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@Import(SecurityConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class EventDetailControllerTest {

    @Autowired
    EventService eventService;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    EventMapper mapper;
    //create after each with deleting entities from every repository
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAllowAccessForAnonymousUser() throws Exception {

        //given
        User user1 = User.builder()
                .username("user-test")
                .email("user-test@gmail.com")
                .password("useruser")
                .build();
        User user2 = User.builder()
                .username("user2-test")
                .email("user2-test@gmail.com")
                .password("user2user2")
                .build();
        Set<User> attendingUsers = new HashSet<>();
        attendingUsers.add(user2);
        userRepository.save(user1);
        userRepository.save(user2);
        Image defaultImage = Image.builder()
                .filename("default-event-image.jpeg")
                .build();
        Event testEvent = eventRepository.save(Event.builder()
                .title("test event x")
                .description("test event x description")
                .startingDateTime(LocalDateTime.now().minusDays(7))
                .endingDateTime(LocalDateTime.now().plusDays(7))
                .owner(user1)
                .image(defaultImage)
                .users(attendingUsers)
                .build());


        //todo null title handling
        mockMvc.perform(MockMvcRequestBuilders.get("/detail-view/{id}", testEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("event-detail-view"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeDoesNotExist("comment"))
                .andExpect(model().attributeDoesNotExist("loggedUser"));

        //todo is it correct approach?
        assertThat(eventService.findByIdFetchOwnerFetchUsers(testEvent.getId())).isEqualTo(testEvent);
        // assertThat(eventService.findEventViewById(testEvent.getId()).getTitle()).isEqualTo("test event x");
    }

    @Test
    void shouldAllowAccessForAuthenticatedUser() throws Exception {

        //todo handle with empty User, handle good url???
        mockMvc
                .perform(MockMvcRequestBuilders.get("/detail-view/1").with(user(User.builder().build())))
                .andExpect(status().isOk())
                .andExpect(view().name("event-detail-view"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("comment"))
                .andExpect(model().attributeExists("loggedUser"));
    }


    @Test
    void shouldSignUpForEvent() throws Exception {
        //given
        User user1 = User.builder()
                .username("user-test")
                .email("user-test@gmail.com")
                .password("useruser")
                .build();
        User user2 = User.builder()
                .username("user2-test")
                .email("user2-test@gmail.com")
                .password("user2user2")
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
        Image defaultImage = Image.builder()
                .filename("default-event-image.jpeg")
                .build();
        Event testEvent = eventRepository.save(Event.builder()
                .title("test event x")
                .description("test event x description")
                .startingDateTime(LocalDateTime.now().plusDays(7))
                .endingDateTime(LocalDateTime.now().plusDays(14))
                .owner(user1)

                .image(defaultImage)
                .build());

        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/sign-up-for-event", testEvent.getId()).with(csrf()).with(user(userRepository.findById(user2.getId()).get())))
                .andExpect(status().is3xxRedirection());
    }


    @Test
    void shouldNotSignUpForEventIfOwner() throws Exception {
        //given
        User user1 = User.builder()
                .username("user-test")
                .email("user-test@gmail.com")
                .password("useruser")
                .build();
        User user2 = User.builder()
                .username("user2-test")
                .email("user2-test@gmail.com")
                .password("user2user2")
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
        Image defaultImage = Image.builder()
                .filename("default-event-image.jpeg")
                .build();
        Event testEvent = eventRepository.save(Event.builder()
                .title("test event x")
                .description("test event x description")
                .startingDateTime(LocalDateTime.now().plusDays(7))
                .endingDateTime(LocalDateTime.now().plusDays(14))
                .owner(user1)

                .image(defaultImage)
                .build());

        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/sign-up-for-event", testEvent.getId()).with(csrf()).with(user(userRepository.findById(user1.getId()).get())))
                .andExpect(status().is4xxClientError())
                .andExpect(status().reason("ACCESS DENIED - OWNER CANNOT SIGN UP FOR AN EVENT"));
    }

    @Test
    void shouldNotSignUpForEventIfEventStartingDateIsBeforeNow() throws Exception {
        //given
        User user1 = User.builder()
                .username("user-test")
                .email("user-test@gmail.com")
                .password("useruser")
                .build();
        User user2 = User.builder()
                .username("user2-test")
                .email("user2-test@gmail.com")
                .password("user2user2")
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
        Image defaultImage = Image.builder()
                .filename("default-event-image.jpeg")
                .build();
        Event testEvent = eventRepository.save(Event.builder()
                .title("test event x")
                .description("test event x description")
                .startingDateTime(LocalDateTime.now().minusDays(7))
                .endingDateTime(LocalDateTime.now().minusDays(4))
                .owner(user1)

                .image(defaultImage)
                .build());

        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/sign-up-for-event", testEvent.getId()).with(csrf()).with(user(userRepository.findById(user2.getId()).get())))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("ACCESS DENIED - CANNOT SIGN UP FOR AN EVENT THAT HAS ALREADY STARTED"));
    }

    @Test
    void shouldNotSignUpForEventIfAlreadySignedUp() throws Exception {
        //given
        User user1 = User.builder()
                .username("user-test")
                .email("user-test@gmail.com")
                .password("useruser")
                .build();
        User user2 = User.builder()
                .username("user2-test")
                .email("user2-test@gmail.com")
                .password("user2user2")
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
        Image defaultImage = Image.builder()
                .filename("default-event-image.jpeg")
                .build();
        Event testEvent = eventRepository.save(Event.builder()
                .title("test event x")
                .description("test event x description")
                .startingDateTime(LocalDateTime.now().plusDays(7))
                .endingDateTime(LocalDateTime.now().plusDays(14))
                .owner(user1)
                .users(Set.of(user2))
                .image(defaultImage)
                .build());

        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/sign-up-for-event", testEvent.getId()).with(csrf()).with(user(userRepository.findById(user2.getId()).get())))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("ACCESS DENIED - CANNOT SIGNUP FOR AN EVENT IF ALREADY ASSIGNED"));
    }


    @Test
    void shouldSignOutFromEvent() throws Exception {
        //given
        User user1 = User.builder()
                .username("user-test")
                .email("user-test@gmail.com")
                .password("useruser")
                .build();
        User user2 = User.builder()
                .username("user2-test")
                .email("user2-test@gmail.com")
                .password("user2user2")
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
        Image defaultImage = Image.builder()
                .filename("default-event-image.jpeg")
                .build();
        Event testEvent = eventRepository.save(Event.builder()
                .title("test event x")
                .description("test event x description")
                .startingDateTime(LocalDateTime.now().plusDays(7))
                .endingDateTime(LocalDateTime.now().plusDays(14))
                .owner(user1)
                .users(Set.of(user2))

                .image(defaultImage)
                .build());

        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/sign-out-from-event", testEvent.getId()).with(csrf()).with(user(userRepository.findById(user2.getId()).get())))
                .andExpect(status().is3xxRedirection());
    }


    @Test
    void shouldNotSignOutFromEventIfOwner() throws Exception {
        //given
        User user1 = User.builder()
                .username("user-test")
                .email("user-test@gmail.com")
                .password("useruser")
                .build();
        User user2 = User.builder()
                .username("user2-test")
                .email("user2-test@gmail.com")
                .password("user2user2")
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
        Image defaultImage = Image.builder()
                .filename("default-event-image.jpeg")
                .build();
        Event testEvent = eventRepository.save(Event.builder()
                .title("test event x")
                .description("test event x description")
                .startingDateTime(LocalDateTime.now().plusDays(7))
                .endingDateTime(LocalDateTime.now().plusDays(14))
                .owner(user1)
                .users(Set.of(user2))

                .image(defaultImage)
                .build());

        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/sign-out-from-event", testEvent.getId()).with(csrf()).with(user(userRepository.findById(user1.getId()).get())))
                .andExpect(status().is4xxClientError())
                .andExpect(status().reason("ACCESS DENIED - OWNER CANNOT SIGN OUT FROM AN EVENT"));
    }

    @Test
    void shouldNotSignOutFromEventIfEventStartingDateIsBeforeNow() throws Exception {
        //given
        User user1 = User.builder()
                .username("user-test")
                .email("user-test@gmail.com")
                .password("useruser")
                .build();
        User user2 = User.builder()
                .username("user2-test")
                .email("user2-test@gmail.com")
                .password("user2user2")
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
        Image defaultImage = Image.builder()
                .filename("default-event-image.jpeg")
                .build();
        Event testEvent = eventRepository.save(Event.builder()
                .title("test event x")
                .description("test event x description")
                .startingDateTime(LocalDateTime.now().minusDays(7))
                .endingDateTime(LocalDateTime.now().minusDays(4))
                .owner(user1)
                .users(Set.of(user2))

                .image(defaultImage)
                .build());

        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/sign-out-from-event", testEvent.getId()).with(csrf()).with(user(userRepository.findById(user2.getId()).get())))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("ACCESS DENIED - CANNOT SIGN OUT FROM AN EVENT THAT HAS ALREADY STARTED"));
    }

    @Test
    void shouldNotSignOutFromEventIfNotSignedUp() throws Exception {
        //given
        User user1 = User.builder()
                .username("user-test")
                .email("user-test@gmail.com")
                .password("useruser")
                .build();
        User user2 = User.builder()
                .username("user2-test")
                .email("user2-test@gmail.com")
                .password("user2user2")
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
        Image defaultImage = Image.builder()
                .filename("default-event-image.jpeg")
                .build();
        Event testEvent = eventRepository.save(Event.builder()
                .title("test event x")
                .description("test event x description")
                .startingDateTime(LocalDateTime.now().plusDays(7))
                .endingDateTime(LocalDateTime.now().plusDays(14))
                .owner(user1)

                .image(defaultImage)
                .build());

        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/sign-out-from-event", testEvent.getId()).with(csrf()).with(user(userRepository.findById(user2.getId()).get())))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("ACCESS DENIED - CANNOT SIGNUP OUT FROM AN EVENT IF HAS NOT ASSIGNED"));
    }

}