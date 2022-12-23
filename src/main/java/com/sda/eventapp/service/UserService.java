package com.sda.eventapp.service;

import com.sda.eventapp.mapper.UserMapper;
import com.sda.eventapp.model.User;
import com.sda.eventapp.repository.UserRepository;
import com.sda.eventapp.web.mvc.form.CreateUserForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    public User save(CreateUserForm form) {
        return repository.save(mapper.toUser(form));
    }
}
