package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.configuration.SecurityConfig;
import com.sda.eventapp.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest(HomepageController.class)
class HomepageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private HomepageController homepageController;

//    @MockBean
//    private EventService eventService;


    @Test
    void shouldAllowAccessForAnonymousUser() throws Exception {

        //todo null title handling
        mockMvc
                .perform(MockMvcRequestBuilders.get("/home").param("title", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))

                .andExpect(model().attributeExists("events"))
                .andExpect(model().attributeExists("title"));


    }


    @Test
    void getAllEventsView() {
    }
}