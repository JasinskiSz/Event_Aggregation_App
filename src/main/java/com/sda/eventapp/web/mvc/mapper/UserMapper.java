package com.sda.eventapp.web.mvc.mapper;

import com.sda.eventapp.model.User;
import com.sda.eventapp.web.mvc.form.CreateUserForm;

public class UserMapper {
    public static User toEntity(CreateUserForm form) {
        return User.builder()
                .username(form.getUsername())
                .password(form.getPassword())
                .email(form.getEmail())
                .build();
    }
}