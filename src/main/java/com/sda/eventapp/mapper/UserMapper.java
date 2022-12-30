package com.sda.eventapp.mapper;

import com.sda.eventapp.model.User;
import com.sda.eventapp.web.mvc.form.CreateUserForm;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
    public User toUser(CreateUserForm form) {
        return User.builder()
                .username(form.getUsername())
                .password(form.getPassword())
                .email(form.getEmail())
                .build();
    }

    public List<User> toUser(List<User> users) {
        return users.stream()
                .map(user -> User.builder()
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .build())
                .toList();
    }
}