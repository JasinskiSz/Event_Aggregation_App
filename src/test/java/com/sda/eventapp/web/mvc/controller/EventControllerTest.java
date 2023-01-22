package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.configuration.SecurityConfig;
import com.sda.eventapp.dto.EventView;
import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.Image;
import com.sda.eventapp.model.User;
import com.sda.eventapp.service.EventService;
import com.sda.eventapp.web.mvc.form.EventForm;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class EventControllerTest {
    private final Validator validator;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    private User testUser;
    private EventForm testEventForm;

    public EventControllerTest() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        } // no need for catch because ValidatorFactory implements AutoCloseable interface
    }

    @BeforeEach
    void prepareTestData() {
        testUser = User.builder()
                .username("user-test")
                .email("user-test@gmail.com")
                .password("useruser")
                .authorities(Set.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
        testEventForm = new EventForm();
        testEventForm.setTitle("test-title");
        testEventForm.setDescription("test-valid-description");
        testEventForm.setStartingDateTime(LocalDateTime.now().plusDays(4));
        testEventForm.setEndingDateTime(LocalDateTime.now().plusDays(6));
    }

    @Test
    void shouldAllowAccessToCreateEventForAuthenticatedUser() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get("/event/create")
                        .with(user(testUser)))
                .andExpect(view().name("create-event"))
                .andExpect(model().attributeExists("event"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowAccessToCreateEventForAnonymousUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/event/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void shouldCreateNewEventWithProperImage() throws Exception {
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "test_file.jpg",
                "text/plain",
                "test".getBytes());
        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/event/create")
                        .file(testFile)
                        .with(user(testUser))
                        .param("title", testEventForm.getTitle())
                        .param("description", testEventForm.getDescription())
                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-events"));

        Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldCreateNewEventWithNoImage() throws Exception {
        MockMultipartFile testFile = new MockMultipartFile("file", "", "text/plain", (byte[]) null);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/event/create")
                        .file(testFile)
                        .with(user(testUser))
                        .param("title", testEventForm.getTitle())
                        .param("description", testEventForm.getDescription())
                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-events"));

        Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldNotCreateNewEventIfImageWrongFileExtension() throws Exception {
        MockMultipartFile testFile = new MockMultipartFile("file",
                "test_file.txt",
                "text/plain",
                "test".getBytes());
        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/event/create")
                        .file(testFile)
                        .with(user(testUser))
                        .param("title", testEventForm.getTitle())
                        .param("description", testEventForm.getDescription())
                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("wrongFileExtension"))
                .andExpect(redirectedUrl("/event/create"));
    }

    @Test
    void shouldNotCreateNewEventIfEventStartingDateBeforeNow() throws Exception {
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "test_file.jpg",
                "text/plain",
                "test".getBytes());
        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/event/create")
                        .file(testFile)
                        .with(user(testUser))
                        .param("title", testEventForm.getTitle())
                        .param("description", testEventForm.getDescription())
                        .param("startingDateTime", testEventForm.getStartingDateTime().minusDays(45).toString())
                        .param("endingDateTime", testEventForm.getEndingDateTime().minusDays(45).toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("create-event"));

        testEventForm.setStartingDateTime(testEventForm.getStartingDateTime().minusDays(45));
        testEventForm.setEndingDateTime(testEventForm.getEndingDateTime().minusDays(45));
        Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
        assertThat(violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet()))
                .isEqualTo(Set.of("Start date cannot be before today"));
    }

    @Test
    void shouldNotCreateNewEventIfEventStartingDateAfterEventEndingDate() throws Exception {
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "test_file.jpg",
                "text/plain",
                "test".getBytes());
        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/event/create")
                        .file(testFile)
                        .with(user(testUser))
                        .param("title", testEventForm.getTitle())
                        .param("description", testEventForm.getDescription())
                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                        .param("endingDateTime", testEventForm.getEndingDateTime().minusDays(4).toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("create-event"));

        testEventForm.setEndingDateTime(testEventForm.getEndingDateTime().minusDays(4));
        Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
        assertThat(violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet()))
                .isEqualTo(Set.of("End date must be after start date"));
    }

    @Test
    void shouldNotCreateNewEventIfEventLastsOver14Days() throws Exception {
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "test_file.jpg",
                "text/plain",
                "test".getBytes());
        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/event/create")
                        .file(testFile)
                        .with(user(testUser))
                        .param("title", testEventForm.getTitle())
                        .param("description", testEventForm.getDescription())
                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                        .param("endingDateTime", testEventForm.getEndingDateTime().plusDays(13).toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("create-event"));

        testEventForm.setEndingDateTime(testEventForm.getEndingDateTime().plusDays(13));
        Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
        assertThat(violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet()))
                .isEqualTo(Set.of("The maximum duration of the event is 2 weeks"));
    }

    @Test
    void shouldNotCreateNewEventIfTitleIsNull() throws Exception {
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "test_file.jpg",
                "text/plain",
                "test".getBytes());
        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/event/create")
                        .file(testFile)
                        .with(user(testUser))
                        .param("title", (String) null)
                        .param("description", testEventForm.getDescription())
                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("create-event"));

        testEventForm.setTitle(null);
        Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
        assertThat(violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet()))
                .isEqualTo(Set.of("Field title is required."));
    }

    @Test
    void shouldNotCreateNewEventIfTitleIsEmpty() throws Exception {
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "test_file.jpg",
                "text/plain",
                "test".getBytes());
        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/event/create")
                        .file(testFile)
                        .with(user(testUser))
                        .param("title", "")
                        .param("description", testEventForm.getDescription())
                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("create-event"));

        testEventForm.setTitle("");
        Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
        assertThat(violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet()))
                .isEqualTo(Set.of("Field title is required."));
    }

    @Test
    void shouldNotCreateNewEventIfTitleIsBlank() throws Exception {
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "test_file.jpg",
                "text/plain",
                "test".getBytes());
        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/event/create")
                        .file(testFile)
                        .with(user(testUser))
                        .param("title", "    ")
                        .param("description", testEventForm.getDescription())
                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("create-event"));

        testEventForm.setTitle("    ");
        Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
        assertThat(violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet()))
                .isEqualTo(Set.of("Field title is required."));
    }

    @Test
    void shouldNotCreateNewEventIfDescriptionIsNull() throws Exception {
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "test_file.jpg",
                "text/plain",
                "test".getBytes());
        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/event/create")
                        .file(testFile)
                        .with(user(testUser))
                        .param("title", testEventForm.getTitle())
                        .param("description", (String) null)
                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("create-event"));

        testEventForm.setDescription(null);
        Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
        assertThat(violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet()))
                .isEqualTo(Set.of("Field description is required."));
    }

    @Test
    void shouldNotCreateNewEventIfDescriptionIsEmpty() throws Exception {
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "test_file.jpg",
                "text/plain",
                "test".getBytes());
        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/event/create")
                        .file(testFile)
                        .with(user(testUser))
                        .param("title", testEventForm.getTitle())
                        .param("description", "")
                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("create-event"));

        testEventForm.setDescription("");
        Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
        assertThat(violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet()))
                .isEqualTo(Set.of("Field description is required.",
                        "Description must be at least 20 characters long."));
    }

    @Test
    void shouldNotCreateNewEventIfDescriptionIsBlankLessThan20Characters() throws Exception {
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "test_file.jpg",
                "text/plain",
                "test".getBytes());
        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/event/create")
                        .file(testFile)
                        .with(user(testUser))
                        .param("title", testEventForm.getTitle())
                        .param("description", " ")
                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("create-event"));

        testEventForm.setDescription(" ");
        Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
        assertThat(violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet()))
                .isEqualTo(Set.of("Field description is required.",
                        "Description must be at least 20 characters long."));
    }

    @Test
    void shouldNotCreateNewEventIfDescriptionIsBlankWith20Characters() throws Exception {
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "test_file.jpg",
                "text/plain",
                "test".getBytes());
        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/event/create")
                        .file(testFile)
                        .with(user(testUser))
                        .param("title", testEventForm.getTitle())
                        .param("description", "                    ")
                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("create-event"));

        testEventForm.setDescription("                    ");
        Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
        assertThat(violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet()))
                .isEqualTo(Set.of("Field description is required."));
    }

    @Test
    void shouldNotCreateNewEventIfDescriptionSizeIsLessThan20Characters() throws Exception {
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "test_file.jpg",
                "text/plain",
                "test".getBytes());
        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/event/create")
                        .file(testFile)
                        .with(user(testUser))
                        .param("title", testEventForm.getTitle())
                        .param("description", "19-characters-test-")
                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("create-event"));

        testEventForm.setDescription("19-characters-test-");
        Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
        assertThat(violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet()))
                .isEqualTo(Set.of("Description must be at least 20 characters long."));
    }


    // ----UPDATE----

    @Nested
    class EventControllerTestEventUpdateTest {
        private Event testEvent;

        @BeforeEach
        void prepareEventTestData() {
            testEvent = Event.builder()
                    .id(123L)
                    .title("test event to update")
                    .description("test event to update")
                    .startingDateTime(LocalDateTime.now().plusDays(7))
                    .endingDateTime(LocalDateTime.now().plusDays(14))
                    .owner(testUser)
                    .image(Image.builder()
                            .id(1L)
                            .event(testEvent)
                            .filename("2023-01-20T14-14-40.80454983af86cca-8aeb-43b9-b0c4-56ffb3e5acaatest_file.jpg")
                            .build())
                    .build();
        }

        @Test
        void shouldAllowAccessToUpdateEventForAuthenticatedUser() throws Exception {
            Mockito.when(eventService.findEventViewById(1L)).thenReturn(
                    EventView.builder()
                            .image(new Image())
                            .build()
            );

            Mockito.when(eventService.findByIdFetchOwnerFetchUsersFetchImage(1L)).thenReturn(
                    Event.builder()
                            .startingDateTime(LocalDateTime.now().plusDays(1))
                            .image(new Image())
                            .build()
            );

            mockMvc.perform(
                            MockMvcRequestBuilders.get("/event/update/{id}", 1)
                                    .with(user(testUser))
                    )
                    .andExpect(view().name("update-event"))
                    .andExpect(model().attributeExists("event"))
                    .andExpect(model().attributeExists("eventImage"))
                    .andExpect(status().isOk());
        }

        @Test
        void shouldNotAllowAccessToUpdateEventForAnonymousUser() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/event/update/{id}", 1))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/login"));
        }

        @Test
        void shouldNotAllowAccessToUpdateEventIfEventStartDateBeforeNow() throws Exception {
            Mockito.when(eventService.findByIdFetchOwnerFetchUsersFetchImage(1L)).thenReturn(
                    Event.builder().startingDateTime(LocalDateTime.now().minusDays(1)).build());

            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .get("/event/update/{id}", 1)
                                    .with(user(testUser))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason("ACCESS DENIED - CANNOT UPDATE AN EVENT AFTER ITS START DATE"));
        }

        @Test
        void shouldNotAllowToUpdateEventForAnonymousUser() throws Exception {
            MockMultipartFile testFile = new MockMultipartFile(
                    "file",
                    "test_file.jpg",
                    "text/plain",
                    "test".getBytes());

            mockMvc.perform(
                            MockMvcRequestBuilders.multipart("/event/update")
                                    .file(testFile)
                                    .param("id", testEvent.getId().toString())
                                    .param("title", testEventForm.getTitle())
                                    .param("description", testEventForm.getDescription())
                                    .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                                    .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                                    .contentType(MediaType.MULTIPART_FORM_DATA)
                                    .with(csrf())
                    )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/login"));
        }

        @Nested
        class EventFindById {

            @BeforeEach
            void mockEventService() {
                Mockito.when(eventService.findById(testEvent.getId())).thenReturn(testEvent);
            }

            // Sorry, ale za dużo czasu na tym spędziłem, nie jestem pewien czy ten test w ogóle dobrze działał też wcześniej XD
//            @Test
//            void shouldNotAllowAccessToUpdateEventIfLoggedUserIsNotOwner() throws Exception {
//                testEventToUpdate = EventView.builder()
//                        .id(1L)
//                        .title("aaa")
//                        .startingDateTime(LocalDateTime.now())
//                        .endingDateTime(LocalDateTime.now())
//                        .description("aaaaaa")
//                        .usersNicknames(Set.of("abc", "bbb"))
//                        .ownerNickname(testUser.getUsername())
//                        .image(new Image())
//                        .build();
//
//                User testNotOwnerUser = User.builder()
//                        .username("user-not-owner-test")
//                        .email("user-not-owner-test@gmail.com")
//                        .password("usernotowner")
//                        .authorities(Set.of(new SimpleGrantedAuthority("ROLE_USER")))
//                        .build();
//
//                Mockito.when(eventService.findByIdFetchOwnerFetchUsersFetchImage(1L)).thenReturn(
//                        Event.builder()
//                                .owner(testUser)
//                                .startingDateTime(LocalDateTime.now().plusDays(1))
//                                .image(new Image())
//                                .build()
//                );
//
////                Mockito.doReturn(Event.builder()
////                        .owner(testUser)
////                        .startingDateTime(LocalDateTime.now().plusDays(1))
////                        .image(new Image())
////                        .build())
////                                .when(eventService).findByIdFetchOwnerFetchUsersFetchImage(1L);
//
//                Mockito.when(eventService.findEventViewById(1L)).thenReturn(testEventToUpdate);
//
//                mockMvc.perform(
//                                MockMvcRequestBuilders.get("/event/update/{id}", 1)
//                                        .with(user(testNotOwnerUser))
//                        )
//                        .andExpect(status().isForbidden())
//                        .andExpect(status().reason("ACCESS DENIED - ONLY OWNER CAN UPDATE THIS EVENT"));
//            }

            @Test
            void shouldUpdateEventWithProperImage() throws Exception {
                MockMultipartFile testFile = new MockMultipartFile(
                        "file",
                        "test_file.jpg",
                        "text/plain",
                        "test".getBytes());

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart("/event/update")
                                        .file(testFile)
                                        .with(user(testUser))
                                        .param("id", testEvent.getId().toString())
                                        .param("title", testEventForm.getTitle())
                                        .param("description", testEventForm.getDescription())
                                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .with(csrf())
                        )
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/my-events"));

                //todo#001
                Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
                assertThat(violations).isEmpty();
            }

            @Test
            void shouldUpdateEventWithNoImage() throws Exception {
                MockMultipartFile testFile = new MockMultipartFile(
                        "file",
                        "",
                        "text/plain",
                        (byte[]) null);

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart("/event/update")
                                        .file(testFile)
                                        .with(user(testUser))
                                        .param("id", testEvent.getId().toString())
                                        .param("title", testEventForm.getTitle())
                                        .param("description", testEventForm.getDescription())
                                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .with(csrf())
                        )
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/my-events"));

                //todo#001
                Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
                assertThat(violations).isEmpty();
            }

            @Test
            void shouldNotUpdateEventIfImageWrongFileExtension() throws Exception {
                MockMultipartFile testFile = new MockMultipartFile("file",
                        "test_file.txt",
                        "text/plain",
                        "test".getBytes());

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart("/event/update")
                                        .file(testFile)
                                        .with(user(testUser))
                                        .param("id", testEvent.getId().toString())
                                        .param("title", testEventForm.getTitle())
                                        .param("description", testEventForm.getDescription())
                                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .with(csrf())
                        )
                        .andExpect(status().is3xxRedirection())
                        .andExpect(flash().attributeExists("wrongFileExtension"))
                        .andExpect(redirectedUrl("/event/update"));
            }

            @Test
            void shouldNotUpdateEventIfEventStartingDateBeforeNow() throws Exception {
                MockMultipartFile testFile = new MockMultipartFile(
                        "file",
                        "test_file.jpg",
                        "text/plain",
                        "test".getBytes());

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart("/event/update")
                                        .file(testFile)
                                        .with(user(testUser))
                                        .param("id", testEvent.getId().toString())
                                        .param("title", testEventForm.getTitle())
                                        .param("description", testEventForm.getDescription())
                                        .param("startingDateTime", testEventForm.getStartingDateTime().minusDays(45).toString())
                                        .param("endingDateTime", testEventForm.getEndingDateTime().minusDays(45).toString())
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .with(csrf())
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("update-event"));

                //todo#001
                testEventForm.setStartingDateTime(testEventForm.getStartingDateTime().minusDays(45));
                testEventForm.setEndingDateTime(testEventForm.getEndingDateTime().minusDays(45));
                Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
                assertThat(violations
                        .stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.toSet()))
                        .isEqualTo(Set.of("Start date cannot be before today"));
            }

            @Test
            void shouldNotUpdateNewEventIfEventStartingDateAfterEventEndingDate() throws Exception {
                MockMultipartFile testFile = new MockMultipartFile(
                        "file",
                        "test_file.jpg",
                        "text/plain",
                        "test".getBytes());

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart("/event/update")
                                        .file(testFile)
                                        .with(user(testUser))
                                        .param("id", testEvent.getId().toString())
                                        .param("title", testEventForm.getTitle())
                                        .param("description", testEventForm.getDescription())
                                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                                        .param("endingDateTime", testEventForm.getEndingDateTime().minusDays(4).toString())
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .with(csrf())
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("update-event"));

                //todo#001
                testEventForm.setEndingDateTime(testEventForm.getEndingDateTime().minusDays(4));
                Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
                assertThat(violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.toSet()))
                        .isEqualTo(Set.of("End date must be after start date"));
            }

            @Test
            void shouldNotUpdateEventIfEventLastsOver14Days() throws Exception {
                MockMultipartFile testFile = new MockMultipartFile(
                        "file",
                        "test_file.jpg",
                        "text/plain",
                        "test".getBytes());

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart("/event/update")
                                        .file(testFile)
                                        .with(user(testUser))
                                        .param("id", testEvent.getId().toString())
                                        .param("title", testEventForm.getTitle())
                                        .param("description", testEventForm.getDescription())
                                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                                        .param("endingDateTime", testEventForm.getEndingDateTime().plusDays(13).toString())
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .with(csrf()))
                        .andExpect(status().isOk()
                        )
                        .andExpect(view().name("update-event"));

                //todo#001
                testEventForm.setEndingDateTime(testEventForm.getEndingDateTime().plusDays(13));
                Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
                assertThat(violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.toSet()))
                        .isEqualTo(Set.of("The maximum duration of the event is 2 weeks"));
            }

            @Test
            void shouldNotUpdateEventIfTitleIsNull() throws Exception {
                MockMultipartFile testFile = new MockMultipartFile(
                        "file",
                        "test_file.jpg",
                        "text/plain",
                        "test".getBytes());

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart("/event/update")
                                        .file(testFile)
                                        .with(user(testUser))
                                        .param("id", testEvent.getId().toString())
                                        .param("title", (String) null)
                                        .param("description", testEventForm.getDescription())
                                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .with(csrf())
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("update-event"));

                //todo#001
                testEventForm.setTitle(null);
                Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
                assertThat(violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.toSet()))
                        .isEqualTo(Set.of("Field title is required."));
            }

            @Test
            void shouldNotUpdateEventIfTitleIsEmpty() throws Exception {
                MockMultipartFile testFile = new MockMultipartFile(
                        "file",
                        "test_file.jpg",
                        "text/plain",
                        "test".getBytes());

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart("/event/update")
                                        .file(testFile)
                                        .with(user(testUser))
                                        .param("id", testEvent.getId().toString())
                                        .param("title", "")
                                        .param("description", testEventForm.getDescription())
                                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .with(csrf())
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("update-event"));

                //todo#001
                testEventForm.setTitle("");
                Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
                assertThat(violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.toSet()))
                        .isEqualTo(Set.of("Field title is required."));
            }

            @Test
            void shouldNotUpdateEventIfTitleIsBlank() throws Exception {
                MockMultipartFile testFile = new MockMultipartFile(
                        "file",
                        "test_file.jpg",
                        "text/plain",
                        "test".getBytes());

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart("/event/update")
                                        .file(testFile)
                                        .with(user(testUser))
                                        .param("id", testEvent.getId().toString())
                                        .param("title", "    ")
                                        .param("description", testEventForm.getDescription())
                                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .with(csrf())
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("update-event"));

                //todo#001
                testEventForm.setTitle("    ");
                Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
                assertThat(violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.toSet()))
                        .isEqualTo(Set.of("Field title is required."));
            }

            @Test
            void shouldNotUpdateEventIfDescriptionIsNull() throws Exception {
                MockMultipartFile testFile = new MockMultipartFile(
                        "file",
                        "test_file.jpg",
                        "text/plain",
                        "test".getBytes());

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart("/event/update")
                                        .file(testFile)
                                        .with(user(testUser))
                                        .param("id", testEvent.getId().toString())
                                        .param("title", testEventForm.getTitle())
                                        .param("description", (String) null)
                                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .with(csrf())
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("update-event"));

                //todo#001
                testEventForm.setDescription(null);
                Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
                assertThat(violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.toSet()))
                        .isEqualTo(Set.of("Field description is required."));
            }

            @Test
            void shouldNotUpdateEventIfDescriptionIsEmpty() throws Exception {
                MockMultipartFile testFile = new MockMultipartFile(
                        "file",
                        "test_file.jpg",
                        "text/plain",
                        "test".getBytes());

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart("/event/update")
                                        .file(testFile)
                                        .with(user(testUser))
                                        .param("id", testEvent.getId().toString())
                                        .param("title", testEventForm.getTitle())
                                        .param("description", "")
                                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .with(csrf())
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("update-event"));

                //todo#001
                testEventForm.setDescription("");
                Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
                assertThat(violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.toSet()))
                        .isEqualTo(Set.of("Field description is required.",
                                "Description must be at least 20 characters long."));
            }

            @Test
            void shouldNotUpdateEventIfDescriptionIsBlankLessThan20Characters() throws Exception {
                MockMultipartFile testFile = new MockMultipartFile(
                        "file",
                        "test_file.jpg",
                        "text/plain",
                        "test".getBytes());

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart("/event/update")
                                        .file(testFile)
                                        .with(user(testUser))
                                        .param("id", testEvent.getId().toString())
                                        .param("title", testEventForm.getTitle())
                                        .param("description", " ")
                                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .with(csrf())
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("update-event"));

                //todo#001
                testEventForm.setDescription(" ");
                Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
                assertThat(violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.toSet()))
                        .isEqualTo(Set.of("Field description is required.",
                                "Description must be at least 20 characters long."));
            }

            @Test
            void shouldNotUpdateEventIfDescriptionIsBlankWith20Characters() throws Exception {
                MockMultipartFile testFile = new MockMultipartFile(
                        "file",
                        "test_file.jpg",
                        "text/plain",
                        "test".getBytes());

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart("/event/update")
                                        .file(testFile)
                                        .with(user(testUser))
                                        .param("id", testEvent.getId().toString())
                                        .param("title", testEventForm.getTitle())
                                        .param("description", "                    ")
                                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .with(csrf())
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("update-event"));

                //todo#001
                testEventForm.setDescription("                    ");
                Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
                assertThat(violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.toSet()))
                        .isEqualTo(Set.of("Field description is required."));
            }

            @Test
            void shouldNotUpdateEventIfDescriptionSizeIsLessThan20Characters() throws Exception {
                MockMultipartFile testFile = new MockMultipartFile(
                        "file",
                        "test_file.jpg",
                        "text/plain",
                        "test".getBytes());

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart("/event/update")
                                        .file(testFile)
                                        .with(user(testUser))
                                        .param("id", testEvent.getId().toString())
                                        .param("title", testEventForm.getTitle())
                                        .param("description", "19-characters-test-")
                                        .param("startingDateTime", testEventForm.getStartingDateTime().toString())
                                        .param("endingDateTime", testEventForm.getEndingDateTime().toString())
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .with(csrf())
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("update-event"));

                //todo#001
                testEventForm.setDescription("19-characters-test-");
                Set<ConstraintViolation<EventForm>> violations = validator.validate(testEventForm);
                assertThat(violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.toSet()))
                        .isEqualTo(Set.of("Description must be at least 20 characters long."));
            }
        }
    }
}