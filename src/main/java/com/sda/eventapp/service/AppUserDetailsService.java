package com.sda.eventapp.service;

import com.sda.eventapp.model.User;
import com.sda.eventapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Value("${admin.email}")
    private String adminEmail;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with email: \"" + email + "\" not found"));
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        // If user has an admin email add them "ROLE_ADMIN"
        if (user.getEmail().equals(adminEmail)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        user.setAuthorities(authorities);
        return user;
    }
}
