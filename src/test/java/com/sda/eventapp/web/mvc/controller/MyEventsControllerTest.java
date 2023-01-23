package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.configs.SecurityConfig;
import com.sda.eventapp.dto.views.EventView;
import com.sda.eventapp.model.EventFilters;
import com.sda.eventapp.model.entities.User;
import com.sda.eventapp.service.FiltersService;
import com.sda.eventapp.service.entityservices.EventService;
import com.sda.eventapp.types.DateType;
import com.sda.eventapp.types.ParticipationType;
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
    @MockBean
    private FiltersService filterService;

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
        List<EventView> list = List.of();
        String participationType = "";
        String dateType = "";
        EventFilters eventFilters = EventFilters.builder()
                .participationType(ParticipationType.OWNED)
                .dateType(DateType.FUTURE)
                .build();

        // User should not have primitive type of id value. When this will be changed it will break this code with
        // nullPointerException on the first parameter. Please, changed it to null then.
        Mockito.when(eventService.findAllEventViews(user.getId(), participationType, dateType)).thenReturn(list);

        Mockito.when(filterService.getEventFilters()).thenReturn(eventFilters);

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