package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.configuration.SecurityConfig;
import com.sda.eventapp.model.User;
import com.sda.eventapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@Import(SecurityConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class MyEventsControllerTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

    private static User prepareTestData() {
        return User.builder()
                .username("user-test")
                .email("user-test@gmail.com")
                .password("useruser")
                .build();
    }

    @Test
    void shouldAllowAccessForAuthenticatedUser() throws Exception {

        User testUser1 = prepareTestData();
        userRepository.save(testUser1);
        mockMvc
                .perform(MockMvcRequestBuilders.get("/my-events")
                        .param("participationType", "")
                        .param("dateType", "")
                        .with(user(userRepository.findById(testUser1.getId()).get()))) //todo optional handling?
                .andExpect(view().name("my-events-view"))
                .andExpect(model().attributeExists("loggedUser"))
                .andExpect(model().attributeExists("participationTypes"))
                .andExpect(model().attributeExists("dateTypes"))
                .andExpect(model().attributeExists("boundEvents"))
                .andExpect(model().attributeExists("eventFilters"))
                .andExpect(status().isOk());

        //todo: uncomment when setting ddl-auto to create-drop
        //userRepository.deleteAll();
    }

    @Test
    void shouldNotAllowAccessForAnonymousUser() throws Exception {

        mockMvc
                .perform(MockMvcRequestBuilders.get("/my-events")
                        .param("participationType", "")
                        .param("dateType", ""))
                //.andExpect(view().("my-events-view"))
//                .andExpect(model().attributeDoesNotExist("loggedUser"))
//                .andExpect(model().attributeDoesNotExist("participationTypes"))
//                .andExpect(model().attributeDoesNotExist("dateTypes"))
//                .andExpect(model().attributeDoesNotExist("boundEvents"))
//                .andExpect(model().attributeDoesNotExist("eventFilters"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

    }

}