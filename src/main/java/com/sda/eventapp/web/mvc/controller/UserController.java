package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.authentication.IAuthenticationFacade;
import com.sda.eventapp.model.User;
import com.sda.eventapp.service.UserService;
import com.sda.eventapp.web.mvc.form.CreateUserForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final IAuthenticationFacade authenticationFacade;

    @GetMapping("/register")
    public String register(ModelMap map) {
        Authentication authentication = authenticationFacade.getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }
        map.addAttribute("user", new CreateUserForm());
        return "create-user";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute("user") @Valid CreateUserForm form, Errors errors) {
        if (errors.hasErrors()) {
            return "create-user";
        }
        userService.save(form);
        return "redirect:/home";
    }
}