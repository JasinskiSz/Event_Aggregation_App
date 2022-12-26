package com.sda.eventapp.mapper;

import com.sda.eventapp.model.User;
import com.sda.eventapp.web.mvc.form.CreateUserForm;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toUser(CreateUserForm form) {
        return User.builder()
                .username(form.getUsername())
                .password(form.getPassword())
                .email(form.getEmail())
                .build();
    }
}