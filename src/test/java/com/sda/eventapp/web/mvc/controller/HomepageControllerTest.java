package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.configuration.SecurityConfig;
import com.sda.eventapp.model.User;
import com.sda.eventapp.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class HomepageControllerTest {

    private static final String EXCEPTION_MESSAGE = "User not found";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;


    @Test
    void shouldAllowAccessForAnonymousUser() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/home")
                        .param("title", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attributeExists("title"));
    }

    @Nested
    class HomepageControllerTestWithUserTestData {
        private User testUser;

        @BeforeEach
        void prepareTestData() {
            testUser = User.builder()
                    .username("user-test")
                    .email("user-test@gmail.com")
                    .password("usertest")
                    .build();
            userRepository.save(testUser);
        }

        @AfterEach
        void deleteDataFromDatabase() {
            userRepository.deleteAll();
        }

        @Test
        void shouldAllowAccessForAuthenticatedUser() throws Exception {
            mockMvc
                    .perform(MockMvcRequestBuilders.get("/home")
                            .param("title", "")
                            .with(user(userRepository.findById(testUser.getId())
                                    .orElseThrow(() -> new RuntimeException(EXCEPTION_MESSAGE)))))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attributeExists("events"))
                    .andExpect(model().attributeExists("title"));
        }
    }
}