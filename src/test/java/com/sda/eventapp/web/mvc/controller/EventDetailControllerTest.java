package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.configuration.SecurityConfig;
import com.sda.eventapp.mapper.EventMapper;
import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.Image;
import com.sda.eventapp.model.User;
import com.sda.eventapp.repository.CommentRepository;
import com.sda.eventapp.repository.EventRepository;
import com.sda.eventapp.repository.ImageRepository;
import com.sda.eventapp.repository.UserRepository;
import com.sda.eventapp.service.EventService;
import com.sda.eventapp.web.mvc.form.CreateCommentForm;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
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
    EventService eventService;
    @Autowired
    EventRepository eventRepository;
    @Autowired
    UserRepository userRepository;
    private static final String BLANK_COMMENT = "";
    private static final String COMMENT_WITH_500_CHARACTERS = """
            Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa.
            Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis,
            ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo,
            fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis
            vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibu""";
    @Autowired
    EventMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static final String COMMENT_WITH_501_CHARACTERS = """
            Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa.
            Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis,
            ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo,
            fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis
            vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibu1""";
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ImageRepository imageRepository;
    Set<User> attendingUsers = new HashSet<>();
    Image defaultImage;
    Event testEvent;
    private User user1;
    private User user2;

    @BeforeEach
    void prepareTestData() {
        user1 = User.builder()
                .username("user-test")
                .email("user-test@gmail.com")
                .password("useruser")
                .build();
        user2 = User.builder()
                .username("user2-test")
                .email("user2-test@gmail.com")
                .password("user2user2")
                .build();
        attendingUsers.add(user2);
        userRepository.save(user1);
        userRepository.save(user2);
        defaultImage = Image.builder()
                .filename("default-event-image.jpeg")
                .build();
        testEvent = Event.builder()
                .title("test event x")
                .description("test event x description")
                .startingDateTime(LocalDateTime.now().minusDays(7))
                .endingDateTime(LocalDateTime.now().plusDays(7))
                .owner(user1)
                .image(defaultImage)
                .users(attendingUsers)
                .build();
    }

    @AfterEach
    void deleteTestDataFromDatabase() {
        commentRepository.deleteAll();
        imageRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldAllowAccessForAnonymousUser() throws Exception {
        eventRepository.save(testEvent);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/detail-view/{id}", testEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("event-detail-view"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeDoesNotExist("comment"))
                .andExpect(model().attributeDoesNotExist("loggedUser"));

        //todo #001 is it correct approach? maybe in different test?
        //assertThat(eventService.findByIdFetchOwnerFetchUsers(testEvent.getId())).isEqualTo(testEvent);
        assertThat(eventService.findEventViewById(testEvent.getId()).getTitle()).isEqualTo("test event x");
    }

    @Test
    void shouldAllowAccessForAuthenticatedUser() throws Exception {
        testEvent.getUsers().clear();
        eventRepository.save(testEvent);

        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/detail-view/{id}", testEvent.getId())
                        .with(csrf()).with(user(userRepository.findById(user1.getId()).get()))) //todo optional handling?
                .andExpect(status().isOk())
                .andExpect(view().name("event-detail-view"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("comment"))
                .andExpect(model().attributeExists("loggedUser"));
    }

    @Test
    void shouldNotSignUpForEventIfOwner() throws Exception {
        eventRepository.save(testEvent);
        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/sign-up-for-event", testEvent.getId()).with(csrf()).with(user(userRepository.findById(user1.getId()).get())))
                .andExpect(status().is4xxClientError())
                .andExpect(status().reason("ACCESS DENIED - OWNER CANNOT SIGN UP FOR AN EVENT"));
    }

    @Test
    void shouldNotSignUpForEventIfEventStartingDateIsBeforeNow() throws Exception {
        testEvent.getUsers().clear();
        testEvent.setEndingDateTime(LocalDateTime.now().minusDays(4));
        eventRepository.save(testEvent);

        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/sign-up-for-event", testEvent.getId()).with(csrf()).with(user(userRepository.findById(user2.getId()).get())))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("ACCESS DENIED - CANNOT SIGN UP FOR AN EVENT THAT HAS ALREADY STARTED"));
    }

    @Test
    void shouldNotSignUpForEventIfAlreadySignedUp() throws Exception {
        testEvent.setStartingDateTime(LocalDateTime.now().plusDays(7));
        testEvent.setEndingDateTime(LocalDateTime.now().plusDays(14));
        eventRepository.save(testEvent);

        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/sign-up-for-event", testEvent.getId()).with(csrf()).with(user(userRepository.findById(user2.getId()).get())))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("ACCESS DENIED - CANNOT SIGNUP FOR AN EVENT IF ALREADY ASSIGNED"));
    }

    @Test
    void shouldSignUpForEvent() throws Exception {
        testEvent.getUsers().clear();
        testEvent.setStartingDateTime(LocalDateTime.now().plusDays(7));
        testEvent.setEndingDateTime(LocalDateTime.now().plusDays(14));
        eventRepository.save(testEvent);

        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/sign-up-for-event", testEvent.getId()).with(csrf()).with(user(userRepository.findById(user2.getId()).get())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/detail-view/**"));
    }

    @Test
    void shouldNotSignOutFromEventIfOwner() throws Exception {
        testEvent.setStartingDateTime(LocalDateTime.now().plusDays(7));
        testEvent.setEndingDateTime(LocalDateTime.now().plusDays(14));
        eventRepository.save(testEvent);

        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/sign-out-from-event", testEvent.getId()).with(csrf()).with(user(userRepository.findById(user1.getId()).get())))
                .andExpect(status().is4xxClientError())
                .andExpect(status().reason("ACCESS DENIED - OWNER CANNOT SIGN OUT FROM AN EVENT"));
    }

    @Test
    void shouldNotSignOutFromEventIfEventStartingDateIsBeforeNow() throws Exception {
        testEvent.setEndingDateTime(LocalDateTime.now().minusDays(4));
        eventRepository.save(testEvent);

        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/sign-out-from-event", testEvent.getId()).with(csrf()).with(user(userRepository.findById(user2.getId()).get())))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("ACCESS DENIED - CANNOT SIGN OUT FROM AN EVENT THAT HAS ALREADY STARTED"));
    }

    @Test
    void shouldNotSignOutFromEventIfNotSignedUp() throws Exception {
        testEvent.getUsers().clear();
        testEvent.setStartingDateTime(LocalDateTime.now().plusDays(7));
        testEvent.setEndingDateTime(LocalDateTime.now().plusDays(14));
        eventRepository.save(testEvent);

        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/sign-out-from-event", testEvent.getId()).with(csrf()).with(user(userRepository.findById(user2.getId()).get())))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("ACCESS DENIED - CANNOT SIGNUP OUT FROM AN EVENT IF HAS NOT ASSIGNED"));
    }

    @Test
    void shouldNotSignUpForEventIfAnonymousUser() throws Exception {
        testEvent.getUsers().clear();
        testEvent.setStartingDateTime(LocalDateTime.now().plusDays(7));
        testEvent.setEndingDateTime(LocalDateTime.now().plusDays(14));
        eventRepository.save(testEvent);

        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/sign-up-for-event", testEvent.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void shouldSignOutFromEvent() throws Exception {
        testEvent.setStartingDateTime(LocalDateTime.now().plusDays(7));
        testEvent.setEndingDateTime(LocalDateTime.now().plusDays(14));
        eventRepository.save(testEvent);

        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/sign-out-from-event", testEvent.getId()).with(csrf()).with(user(userRepository.findById(user2.getId()).get())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/detail-view/**"));
    }

    @Test
    void shouldNotSignOutFromEventIfAnonymousUser() throws Exception {
        testEvent.getUsers().clear();
        testEvent.setStartingDateTime(LocalDateTime.now().plusDays(7));
        testEvent.setEndingDateTime(LocalDateTime.now().plusDays(14));
        eventRepository.save(testEvent);

        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/sign-out-from-event", testEvent.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void shouldAddComment() throws Exception {
        testEvent.setStartingDateTime(LocalDateTime.now().plusDays(7));
        testEvent.setEndingDateTime(LocalDateTime.now().plusDays(14));
        eventRepository.save(testEvent);

        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/add-comment", testEvent.getId())
                        .with(csrf())
                        .with(user(userRepository.findById(user2.getId()).get()))
                        .param("text", COMMENT_WITH_500_CHARACTERS)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        //todo#002
        //for positive scenario this part of test is redundant (there is no flash().attributeDoesNotExist() method.
        //That is why I added assertJ test
        //todo should it be made as an isolated unit test?
        CreateCommentForm cfm = new CreateCommentForm();
        cfm.setText(COMMENT_WITH_500_CHARACTERS);
        Set<ConstraintViolation<CreateCommentForm>> violations = validator.validate(cfm);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldNotAddCommentIfCommentIsEmpty() throws Exception {
        testEvent.setStartingDateTime(LocalDateTime.now().plusDays(7));
        testEvent.setEndingDateTime(LocalDateTime.now().plusDays(14));
        eventRepository.save(testEvent);


        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/add-comment", testEvent.getId())
                        .with(csrf())
                        .with(user(userRepository.findById(user2.getId()).get()))
                        .param("text", BLANK_COMMENT)
                        .with(csrf()))
                .andExpect(flash().attributeExists("commentErrors"))
                .andExpect(flash().attribute("commentErrors", List.of("Comment cannot be empty")))
                .andExpect(status().is3xxRedirection());

        //todo #002 should it be made as an isolated unit test?
        CreateCommentForm cfm = new CreateCommentForm();
        cfm.setText(BLANK_COMMENT);
        Set<ConstraintViolation<CreateCommentForm>> violations = validator.validate(cfm);
        assertThat(violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet())).isEqualTo(Set.of("Comment cannot be empty"));
    }

    @Test
    void shouldNotAddCommentIfCommentHasOver500Characters() throws Exception {
        testEvent.setStartingDateTime(LocalDateTime.now().plusDays(7));
        testEvent.setEndingDateTime(LocalDateTime.now().plusDays(14));
        eventRepository.save(testEvent);

        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/add-comment", testEvent.getId())
                        .with(csrf())
                        .with(user(userRepository.findById(user2.getId()).get()))
                        .param("text", COMMENT_WITH_501_CHARACTERS)
                        .with(csrf()))
                .andExpect(flash().attributeExists("commentErrors"))
                .andExpect(flash().attribute("commentErrors", List.of("Comment cannot have more than 500 characters")))
                .andExpect(status().is3xxRedirection());

        //todo#002 should it be made as an isolated unit test?
        CreateCommentForm cfm = new CreateCommentForm();
        cfm.setText(COMMENT_WITH_501_CHARACTERS);
        Set<ConstraintViolation<CreateCommentForm>> violations = validator.validate(cfm);
        assertThat(violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet())).isEqualTo(Set.of("Comment cannot have more than 500 characters"));
    }

    @Test
    void shouldNotAddCommentIfAnonymousUser() throws Exception {
        testEvent.setStartingDateTime(LocalDateTime.now().plusDays(7));
        testEvent.setEndingDateTime(LocalDateTime.now().plusDays(14));
        eventRepository.save(testEvent);

        mockMvc.perform(MockMvcRequestBuilders.post("/detail-view/{id}/add-comment", testEvent.getId())
                        .with(csrf())
                        .param("text", COMMENT_WITH_500_CHARACTERS)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}