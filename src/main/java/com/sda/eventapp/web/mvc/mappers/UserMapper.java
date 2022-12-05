package com.sda.eventapp.web.mvc.mappers;

import com.sda.eventapp.model.User;
import com.sda.eventapp.web.mvc.forms.CreateUserForm;

public class UserMapper {
    public static User toEntity(CreateUserForm form) {
        return new User(form.getUsername(), form.getPassword(), form.getEmail());
    }
}