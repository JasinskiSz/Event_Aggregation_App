package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.configuration.SecurityConfig;
import com.sda.eventapp.model.User;
import com.sda.eventapp.repository.UserRepository;
import com.sda.eventapp.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
//@WebMvcTest(HomepageController.class)
class HomepageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;


    @Test
    void shouldAllowAccessForAnonymousUser() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get("/home").param("title", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attributeExists("title"));
    }

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
                .perform(MockMvcRequestBuilders.get("/home").param("title", "").with(user(userRepository.findById(testUser1.getId()).get())))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attributeExists("title"));

        userRepository.deleteAll();
    }
}