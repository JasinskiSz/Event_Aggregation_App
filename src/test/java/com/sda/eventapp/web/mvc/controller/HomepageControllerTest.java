package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.configs.SecurityConfig;
import com.sda.eventapp.model.entities.User;
import com.sda.eventapp.service.entityservices.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class HomepageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final static String TITLE = "testTitle";
    @MockBean
    private EventService eventService;

    @BeforeEach
    void prepareTests() {
        Mockito.when(eventService.findAllEventViews(TITLE, false, false, false)).thenReturn(List.of());
    }

    @Test
    void shouldAllowAccessForAnonymousUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/home")
                        .param("title", TITLE))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attributeExists("title"));
    }

    @Test
    void shouldAllowAccessForAuthenticatedUser() throws Exception {
        User testUser = User.builder()
                .username("user-test")
                .email("user-test@gmail.com")
                .password("usertest")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get("/home")
                        .param("title", TITLE)
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attributeExists("title"));
    }
}