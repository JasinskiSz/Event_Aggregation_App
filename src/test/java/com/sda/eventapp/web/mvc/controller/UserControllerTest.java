package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.configuration.SecurityConfig;
import com.sda.eventapp.model.User;
import com.sda.eventapp.repository.UserRepository;
import com.sda.eventapp.service.UserService;
import com.sda.eventapp.web.mvc.form.CreateCommentForm;
import com.sda.eventapp.web.mvc.form.CreateUserForm;
import com.sda.eventapp.web.mvc.form.validation.validator.UniqueEmailValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
class UserControllerTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
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
    void shouldAllowAccessForAnonymousUser() throws Exception {


        mockMvc
                .perform(MockMvcRequestBuilders.get("/user/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-user"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void shouldNotAllowAccessForAuthenticatedUser() throws Exception {

        User testUser1 = prepareTestData();
        userRepository.save(testUser1);
        mockMvc
                .perform(MockMvcRequestBuilders.get("/user/register")
                        .with(user(userRepository.findById(testUser1.getId()).get()))) //todo optional handling?
                .andExpect(model().attributeDoesNotExist("user"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/home/**"));

        userRepository.deleteAll();
    }

    @Test
    void shouldRedirectToHomeAfterCreatingNewUser() throws Exception {
        userRepository.deleteAll();

        mockMvc
                .perform(MockMvcRequestBuilders.post("/user/register")
                        .param("username", "create-test-user3")
                        .param("email", "createtestuser3@gmail.com")
                        .param("password", "test-password")
                        .param("confirmPassword", "test-password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/home/**"));


        //todo there is a problem with instantiating custom validation annotation (maybe because of connecting with db?)

        //        CreateUserForm cum = new CreateUserForm();
//        cum.setUsername("create-test-user2");
//        cum.setEmail("createtestuser2@gmail.com");
//        cum.setPassword("test-password");
//        cum.setConfirmPassword("test-password");
//
//        Set<ConstraintViolation<CreateUserForm>> violations = validator.validate(cum);
//        assertThat(violations).isEmpty();
    }

    @Test
    void shouldNotCreateNewUserAndRedirectToUserRegisterIfPasswordLessThan8Characters() throws Exception {
        userRepository.deleteAll();

        mockMvc
                .perform(MockMvcRequestBuilders.post("/user/register")
                        .param("username", "create-test-user3")
                        .param("email", "createtestuser3@gmail.com")
                        .param("password", "test")
                        .param("confirmPassword", "test-password")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("create-user"));


        //todo there is a problem with instantiating custom validation annotation (maybe because of connecting with db?)

        //        CreateUserForm cum = new CreateUserForm();
//        cum.setUsername("create-test-user2");
//        cum.setEmail("createtestuser2@gmail.com");
//        cum.setPassword("test-password");
//        cum.setConfirmPassword("test-password");
//
//        Set<ConstraintViolation<CreateUserForm>> violations = validator.validate(cum);
//        assertThat(violations).isEmpty();
    }

}