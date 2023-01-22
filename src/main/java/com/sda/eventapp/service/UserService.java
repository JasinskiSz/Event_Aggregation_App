package com.sda.eventapp.service;

import com.sda.eventapp.dto.form.CreateUserForm;
import com.sda.eventapp.entities.User;
import com.sda.eventapp.repository.UserRepository;
import com.sda.eventapp.utils.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final UserMapper mapper;

    public User save(CreateUserForm form) {
        User user = mapper.toUser(form);
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        return repository.save(user);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }
}