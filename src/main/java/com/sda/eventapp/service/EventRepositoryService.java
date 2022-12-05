package com.sda.eventapp.service;

import com.sda.eventapp.model.Comment;
import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.User;
import com.sda.eventapp.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventRepositoryService implements CommandLineRunner {

    private final EventRepository eventRepository;

    public List<Event> findAll(){
        return StreamSupport.stream(eventRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public void run(String... args) throws Exception {

        //adding test events
        /*User user1 = User.builder()
                .username("blazej1")
                .email("blazej1@gmail.com")
                .password("blazej11234")
                .build();

        User user2 = User.builder()
                .username("blazej2")
                .email("blazej2@gmail.com")
                .password("blazej21234")
                .build();
        User user3 = User.builder()
                .username("blazej3")
                .email("blazej3@gmail.com")
                .password("blazej31234")
                .build();

        Comment comment1 = Comment.builder()
                .text("This is the test comment text")
                .user(user2)
                .build();

        Comment comment2 = Comment.builder()
                .text("This is the test comment text2")
                .user(user2)
                .build();

        Set<Comment> comments = new HashSet<>();

        Set<User> users = new HashSet<>();
        users.add(user2);
        users.add(user3);

        eventRepository.save(Event.builder()
                        .title("This is a test title1")
                .description("This is the test description1")
                        .owner(user1)
                        .startingDateTime(LocalDateTime.of(2023,10,28,18,0))
                        .endingDateTime(LocalDateTime.of(2023,10,28,20,0))
                .build());

        eventRepository.save(Event.builder()
                .title("This is a test title2")
                .description("This is the test description2")
                .owner(user2)
                .startingDateTime(LocalDateTime.of(2021,10,28,18,0))
                .endingDateTime(LocalDateTime.of(2021,10,28,20,0))
                .build());

        eventRepository.save(Event.builder()
                .title("This is a test title3")
                .description("This is the test description3")
                .owner(user3)
                .startingDateTime(LocalDateTime.now())
                .endingDateTime(LocalDateTime.now().plusHours(3))
                .build());*/
    }
}
