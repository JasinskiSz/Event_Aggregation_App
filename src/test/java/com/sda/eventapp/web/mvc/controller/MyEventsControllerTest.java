package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.configuration.SecurityConfig;
import com.sda.eventapp.model.User;
import com.sda.eventapp.service.EventService;
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
class MyEventsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Test
    void shouldNotAllowAccessForAnonymousUser() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/my-events")
                                .param("participationType", "")
                                .param("dateType", "")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void shouldAllowAccessForAuthenticatedUser() throws Exception {
        User user = new User();
        String participationType = "";
        String dateType = "";

        // User should not have primitive type of id value. When this will be changed it will break this code with
        // nullPointerException on the first parameter. Please, changed it to null then.
        Mockito.when(eventService.findAllEventViews(user.getId(), participationType, dateType)).thenReturn(List.of());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/my-events")
                                .param("participationType", "")
                                .param("dateType", "")
                                .with(user(new User()))
                )
                .andExpect(view().name("my-events-view"))
                .andExpect(model().attributeExists("loggedUser"))
                .andExpect(model().attributeExists("participationTypes"))
                .andExpect(model().attributeExists("dateTypes"))
                .andExpect(model().attributeExists("boundEvents"))
                .andExpect(model().attributeExists("eventFilters"))
                .andExpect(status().isOk());
    }
}