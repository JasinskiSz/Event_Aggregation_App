package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.config.SecurityConfig;
import com.sda.eventapp.dto.form.CreateUserForm;
import com.sda.eventapp.model.entities.User;
import com.sda.eventapp.service.UserService;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    //todo #001 description line 76
    //private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldAllowAccessForAnonymousUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-user"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void shouldRedirectToHomeAfterCreatingNewUser() throws Exception {
        Mockito.when(userService.save(new CreateUserForm())).thenReturn(new User());

        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .param("username", "create-test-user")
                        .param("email", "createtestuser@gmail.com")
                        .param("password", "test-password")
                        .param("confirmPassword", "test-password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/home/**"));

        //todo #001 there is a problem with instantiating custom
        // validation annotation (maybe because of connecting with db?)
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
        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .param("username", "create-test-user3")
                        .param("email", "createtestuser3@gmail.com")
                        .param("password", "test")
                        .param("confirmPassword", "test-password")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("create-user"));

        //todo #001
    }

    @Test
    void shouldNotAllowAccessForAuthenticatedUser() throws Exception {
        User testUser = new User();

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/user/register")
                                .with(user(testUser))
                )
                .andExpect(model().attributeDoesNotExist("user"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/home/**"));
    }
}