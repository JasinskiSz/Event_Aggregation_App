package com.sda.eventapp.service;

import com.sda.eventapp.entities.User;
import com.sda.eventapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class AppUserDetailsServiceTest {


    @Autowired
    AppUserDetailsService appUserDetailsService;
    @MockBean
    UserRepository userRepository;
    User userTest;

    @BeforeEach
    void beforeEach() {
        userTest = User
                .builder()
                .email("userTest@gmail.com")
                .build();
    }

    @Test
    void shouldAddRoleUserToLoggedUser() {
        //given
        Mockito.when(userRepository.findByEmail(userTest.getEmail())).thenReturn(Optional.of(userTest));
        //when
        UserDetails actual = appUserDetailsService.loadUserByUsername("userTest@gmail.com");
        //then
        assertThat(actual.getAuthorities())
                .isNotNull()
                .hasSize(1)
                .isEqualTo(Set.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void shouldAddRoleUserAndRoleAdminToLoggedAdmin() {
        //given
        userTest.setEmail("admin@eventapp.com");
        Mockito.when(userRepository.findByEmail(userTest.getEmail())).thenReturn(Optional.of(userTest));
        //when
        UserDetails actual = appUserDetailsService.loadUserByUsername("admin@eventapp.com");
        //then
        assertThat(actual.getAuthorities())
                .isNotNull()
                .hasSize(2)
                .isEqualTo(Set.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void shouldThrowUsernameFoundExceptionIfEmailNotFound() {
        //given
        Mockito.when(userRepository.findByEmail(userTest.getEmail()))
                .thenThrow(new UsernameNotFoundException("User with email: \"" + userTest.getEmail() + "\" not found"));
        //then
        assertThatThrownBy(() -> userRepository.findByEmail(userTest.getEmail()))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}