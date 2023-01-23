package com.sda.eventapp.service;

import com.sda.eventapp.model.entities.User;
import com.sda.eventapp.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AdminAccountCreator {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.password}")
    private String adminPassword;

    public AdminAccountCreator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void createAdminAccount() {
        // check if admin account already exists
        if (!userRepository.existsByEmail(adminEmail)) {
            // create new admin user
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setUsername("Admin");
            userRepository.save(admin);
        }
    }
}
