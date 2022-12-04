package com.sda.eventapp.web.mvc.mappers;

import com.sda.eventapp.model.User;
import com.sda.eventapp.web.mvc.forms.CreateUserForm;

public class UserMapper {
    public static User toEntity(CreateUserForm form) {
        return new User(form.getEmail(), form.getUsername(), form.getPassword());
    }
}