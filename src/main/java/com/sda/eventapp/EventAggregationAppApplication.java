package com.sda.eventapp;

import com.sda.eventapp.model.User;
import com.sda.eventapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@SpringBootApplication
public class EventAggregationAppApplication implements CommandLineRunner {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(EventAggregationAppApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        User user = new User();
        user.setId(1);
        user.setEmail("admin2@o2.pl");
        user.setUsername("admin2@o2.pl");
        user.setPassword(passwordEncoder.encode("haslo"));
        user.setRole(User.Roles.ROLE_ADMIN);
        repository.save(user);
    }
}
