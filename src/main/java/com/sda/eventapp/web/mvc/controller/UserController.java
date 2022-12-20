package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.service.UserService;
import com.sda.eventapp.web.mvc.form.CreateUserForm;
import com.sda.eventapp.web.mvc.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/create/user")
public class UserController {
    private final UserService userService;

    @GetMapping
    public String create(ModelMap map) {
        map.addAttribute("user", new CreateUserForm());
        return "create-user";
    }

    @PostMapping
    public String handleCreate(@ModelAttribute("user") @Valid CreateUserForm form, Errors errors) {
        log.info("Creating user from form: {}", form);
        if (errors.hasErrors()) {
            return "create-user";
        }
        userService.save(UserMapper.toEntity(form));
        return "index";
    }
}