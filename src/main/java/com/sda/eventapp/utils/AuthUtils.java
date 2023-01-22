package com.sda.eventapp.utils;

import org.springframework.security.core.Authentication;

public class AuthUtils {
    public static boolean isUserLoggedIn(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }
}