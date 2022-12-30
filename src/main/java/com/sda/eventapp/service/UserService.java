package com.sda.eventapp.service;

import com.sda.eventapp.mapper.UserMapper;
import com.sda.eventapp.model.User;
import com.sda.eventapp.repository.UserRepository;
import com.sda.eventapp.web.mvc.form.CreateUserForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<User> eventUserList(Long id) {
        return mapper.toUser(repository.findAllUserInEvent(id));
    }
}