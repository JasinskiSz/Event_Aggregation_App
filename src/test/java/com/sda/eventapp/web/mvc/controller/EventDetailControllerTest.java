package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.configuration.SecurityConfig;
import com.sda.eventapp.dto.CommentView;
import com.sda.eventapp.dto.EventView;
import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.Image;
import com.sda.eventapp.model.User;
import com.sda.eventapp.service.CommentService;
import com.sda.eventapp.service.EventService;
import com.sda.eventapp.web.mvc.form.CreateCommentForm;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
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

import java.time.LocalDateTime;
import java.util.List;
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
class EventDetailControllerTest {
    private static final String BLANK_COMMENT = "";
    private static final String COMMENT_WITH_500_CHARACTERS = """
            Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa.
            Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis,
            ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo,
            fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis
            vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibu""";
    private static final String COMMENT_WITH_501_CHARACTERS = """
            Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa.
            Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis,
            ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo,
            fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis
            vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibu1""";

    private final Validator validator;
    @MockBean
    private EventService eventService;
    @MockBean
    private CommentService commentService;
    @Autowired
    private MockMvc mockMvc;

    private User testUser1;
    private User testUser2;

    public EventDetailControllerTest() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        } // no need for catch because ValidatorFactory implements AutoCloseable interface
    }

    @BeforeEach
    void prepareTestData() {
        testUser1 = User.builder()
                .username("user-test")
                .email("user-test@gmail.com")
                .password("useruser")
                .build();
        testUser2 = User.builder()
                .username("user2-test")
                .email("user2-test@gmail.com")
                .password("user2user2")
                .build();
    }

    @Test
    void shouldAllowAccessForAnonymousUser() throws Exception {
        Mockito.when(eventService.findEventViewById(1L)).thenReturn(
                EventView.builder()
                        .image(new Image())
                        .build());
        Mockito.when(commentService.findCommentViewsByEventId(1L)).thenReturn(
                List.of(CommentView.builder().build()));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/detail-view/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("event-detail-view"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeDoesNotExist("comment"))
                .andExpect(model().attributeDoesNotExist("loggedUser"));
    }

    @Test
    void shouldAllowAccessForAuthenticatedUser() throws Exception {
        Mockito.when(eventService.findEventViewById(1L)).thenReturn(
                EventView.builder()
                        .startingDateTime(LocalDateTime.now())
                        .image(new Image())
                        .build()
        );
        Mockito.when(commentService.findCommentViewsByEventId(1L)).thenReturn(
                List.of(CommentView.builder().build())
        );
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/detail-view/{id}", 1L)
                        .with(csrf()).with(user(testUser1))
                                )
                .andExpect(status().isOk())
                .andExpect(view().name("event-detail-view"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("comment"))
                .andExpect(model().attributeExists("loggedUser"));
    }

    @Test
    void shouldNotSignUpForEventIfOwner() throws Exception {
        Mockito.when(eventService.findByIdFetchOwnerFetchUsersFetchImage(1L))
                .thenReturn(Event.builder().owner(testUser1).build());
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/detail-view/{id}/sign-up-for-event", 1L)
                        .with(csrf())
                        .with(user(testUser1)))
                .andExpect(status().isForbidden())
                .andExpect(status().reason("ACCESS DENIED - OWNER CANNOT SIGN UP FOR AN EVENT"));
    }

    @Test
    void shouldNotSignUpForEventIfEventStartingDateIsBeforeNow() throws Exception {
        Mockito.when(eventService.findByIdFetchOwnerFetchUsersFetchImage(1L))
                .thenReturn(Event.builder()
                        .startingDateTime(LocalDateTime.now().minusDays(1))
                        .owner(testUser1)
                        .build());
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/detail-view/{id}/sign-up-for-event", 1L)
                        .with(csrf())
                        .with(user(testUser2)))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("ACCESS DENIED - CANNOT SIGN UP FOR AN EVENT THAT HAS ALREADY STARTED"));
    }

    @Test
    void shouldNotSignUpForEventIfAlreadySignedUp() throws Exception {
        Mockito.when(eventService.findByIdFetchOwnerFetchUsersFetchImage(1L))
                .thenReturn(Event.builder()
                        .startingDateTime(LocalDateTime.now().plusDays(1))
                        .owner(testUser1)
                        .users(Set.of(testUser2))
                        .build());
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/detail-view/{id}/sign-up-for-event", 1L)
                        .with(csrf())
                        .with(user(testUser2)))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("ACCESS DENIED - CANNOT SIGNUP FOR AN EVENT IF ALREADY ASSIGNED"));
    }

    @Test
    void shouldSignUpForEvent() throws Exception {
        Mockito.when(eventService.findByIdFetchOwnerFetchUsersFetchImage(1L))
                .thenReturn(Event.builder()
                        .startingDateTime(LocalDateTime.now().plusDays(1))
                        .owner(testUser1)
                        .users(Set.of())
                        .build());
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/detail-view/{id}/sign-up-for-event", 1L)
                        .with(csrf())
                        .with(user(testUser2)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/detail-view/**"));
    }

    @Test
    void shouldNotSignOutFromEventIfOwner() throws Exception {
        Mockito.when(eventService.findByIdFetchOwnerFetchUsersFetchImage(1L))
                .thenReturn(Event.builder()
                        .startingDateTime(LocalDateTime.now().plusDays(1))
                        .owner(testUser1)
                        .users(Set.of())
                        .build());
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/detail-view/{id}/sign-out-from-event", 1L)
                        .with(csrf())
                        .with(user(testUser1)))
                .andExpect(status().is4xxClientError())
                .andExpect(status().reason("ACCESS DENIED - OWNER CANNOT SIGN OUT FROM AN EVENT"));
    }

    @Test
    void shouldNotSignOutFromEventIfEventStartingDateIsBeforeNow() throws Exception {
        Mockito.when(eventService.findByIdFetchOwnerFetchUsersFetchImage(1L))
                .thenReturn(Event.builder()
                        .startingDateTime(LocalDateTime.now().minusDays(1))
                        .owner(testUser1)
                        .users(Set.of(testUser2))
                        .build());
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/detail-view/{id}/sign-out-from-event", 1L)
                        .with(csrf())
                        .with(user(testUser2)))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("ACCESS DENIED - CANNOT SIGN OUT FROM AN EVENT THAT HAS ALREADY STARTED"));
    }

    @Test
    void shouldNotSignOutFromEventIfNotSignedUp() throws Exception {
        Mockito.when(eventService.findByIdFetchOwnerFetchUsersFetchImage(1L))
                .thenReturn(Event.builder()
                        .startingDateTime(LocalDateTime.now().plusDays(1))
                        .owner(testUser1)
                        .users(Set.of())
                        .build());
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/detail-view/{id}/sign-out-from-event", 1L)
                        .with(csrf())
                        .with(user(testUser2)))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("ACCESS DENIED - CANNOT SIGNUP OUT FROM AN EVENT IF HAS NOT ASSIGNED"));
    }

    @Test
    void shouldNotSignUpForEventIfAnonymousUser() throws Exception {
        Mockito.when(eventService.findByIdFetchOwnerFetchUsersFetchImage(1L))
                .thenReturn(Event.builder()
                        .startingDateTime(LocalDateTime.now().plusDays(1))
                        .owner(testUser1)
                        .users(Set.of())
                        .build());
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/detail-view/{id}/sign-up-for-event", 1L)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void shouldSignOutFromEvent() throws Exception {
        Mockito.when(eventService.findByIdFetchOwnerFetchUsersFetchImage(1L))
                .thenReturn(Event.builder()
                        .startingDateTime(LocalDateTime.now().plusDays(1))
                        .owner(testUser1)
                        .users(Set.of(testUser2))
                        .build());
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/detail-view/{id}/sign-out-from-event", 1L)
                        .with(csrf()).with(user(testUser2)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/detail-view/**"));
    }

    @Test
    void shouldNotSignOutFromEventIfAnonymousUser() throws Exception {
        Mockito.when(eventService.findByIdFetchOwnerFetchUsersFetchImage(1L))
                .thenReturn(Event.builder()
                        .startingDateTime(LocalDateTime.now().plusDays(1))
                        .owner(testUser1)
                        .users(Set.of(testUser2))
                        .build());
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/detail-view/{id}/sign-out-from-event", 1L)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void shouldAddComment() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/detail-view/{id}/add-comment", 1L)
                        .with(csrf())
                        .with(user(testUser2))
                        .param("text", COMMENT_WITH_500_CHARACTERS)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
        CreateCommentForm cfm = new CreateCommentForm();
        cfm.setText(COMMENT_WITH_500_CHARACTERS);
        Set<ConstraintViolation<CreateCommentForm>> violations = validator.validate(cfm);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldNotAddCommentIfCommentIsEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/detail-view/{id}/add-comment", 1L)
                        .with(csrf())
                        .with(user(testUser2))
                        .param("text", BLANK_COMMENT)
                        .with(csrf()))
                .andExpect(flash().attributeExists("commentErrors"))
                .andExpect(flash().attribute("commentErrors", List.of("Comment cannot be empty")))
                .andExpect(status().is3xxRedirection());
        CreateCommentForm cfm = new CreateCommentForm();
        cfm.setText(BLANK_COMMENT);
        Set<ConstraintViolation<CreateCommentForm>> violations = validator.validate(cfm);
        assertThat(violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet()))
                .isEqualTo(Set.of("Comment cannot be empty"));
    }

    @Test
    void shouldNotAddCommentIfCommentHasOver500Characters() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/detail-view/{id}/add-comment", 1L)
                        .with(csrf())
                        .with(user(testUser2))
                        .param("text", COMMENT_WITH_501_CHARACTERS)
                        .with(csrf()))
                .andExpect(flash().attributeExists("commentErrors"))
                .andExpect(flash().attribute("commentErrors",
                        List.of("Comment cannot have more than 500 characters")))
                .andExpect(status().is3xxRedirection());
        CreateCommentForm cfm = new CreateCommentForm();
        cfm.setText(COMMENT_WITH_501_CHARACTERS);
        Set<ConstraintViolation<CreateCommentForm>> violations = validator.validate(cfm);
        assertThat(violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet()))
                .isEqualTo(Set.of("Comment cannot have more than 500 characters"));
    }

    @Test
    void shouldNotAddCommentIfAnonymousUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/detail-view/{id}/add-comment", 1L)
                        .with(csrf())
                        .param("text", COMMENT_WITH_500_CHARACTERS)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}