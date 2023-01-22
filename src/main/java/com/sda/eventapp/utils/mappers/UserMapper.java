package com.sda.eventapp.utils.mappers;

import com.sda.eventapp.dto.form.CreateUserForm;
import com.sda.eventapp.entities.User;
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