package com.sda.eventapp.service;

import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.User;
import com.sda.eventapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserRepositoryService implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {

        Event event1 = Event.builder()
                .title("Test1")
                .build();
        Event event2 = Event.builder()
                .title("Test2")
                .build();

        Set<Event> events = new HashSet<>();
        events.add(event1);
        events.add(event2);

        /*userRepository.save(User.builder()
                .username("przemsio")
                .email("przemsio@gmail.com")
                .password("1234")

                .build());*/




    }
}
