package com.sda.eventapp.service;

import com.sda.eventapp.model.User;
import com.sda.eventapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id" + id + " not found"));
    }

    private final UserRepository repository;

    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id" + id + " not found"));
    }

    public User save(User user) {
        return repository.save(user);
    }
}
