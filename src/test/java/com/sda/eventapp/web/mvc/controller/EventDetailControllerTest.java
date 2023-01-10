package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.configuration.SecurityConfig;
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
    private MockMvc mockMvc;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void shouldAllowAccessForAnonymousUser() throws Exception {

        //given
        User user1 = User.builder()
                .username("user-test")
                .email("user-test@gmail.com")
                .password("useruser")
                .build();
        userRepository.save(user1);
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
                .build());


        //todo null title handling
        mockMvc.perform(MockMvcRequestBuilders.get("/detail-view/{id}", testEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("event-detail-view"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeDoesNotExist("comment"))
                .andExpect(model().attributeDoesNotExist("loggedUser"));
    }

    @Test
        //@WithMockUser(username = "duke")
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

}