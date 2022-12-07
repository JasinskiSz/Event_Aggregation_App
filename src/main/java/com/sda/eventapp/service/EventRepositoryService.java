package com.sda.eventapp.service;

import com.sda.eventapp.model.Event;
import com.sda.eventapp.repository.EventRepository;
import com.sda.eventapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventRepositoryService implements CommandLineRunner {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    //todo: default should return ongoing and future events
    public List<Event> findAllWithFilters(boolean futureEventsFilter, boolean ongoingEventsFilter, boolean pastEventsFilter) {
        //future
        if (futureEventsFilter && !ongoingEventsFilter && !pastEventsFilter) {
            return StreamSupport.stream(eventRepository.findAllFutureEvents().spliterator(), false).collect(Collectors.toList());
        }
        //ongoing
        else if (!futureEventsFilter && ongoingEventsFilter && !pastEventsFilter) {
            return StreamSupport.stream(eventRepository.findAllOngoingEvents().spliterator(), false).collect(Collectors.toList());
        }
        //past
        else if (!futureEventsFilter && !ongoingEventsFilter && pastEventsFilter) {
            return StreamSupport.stream(eventRepository.findAllPastEvents().spliterator(), false).collect(Collectors.toList());
        }

        //future past
        else if (futureEventsFilter && !ongoingEventsFilter && pastEventsFilter) {
            return StreamSupport.stream(eventRepository.findAllFutureAndPastEvents().spliterator(), false).collect(Collectors.toList());
        }
        //ongoing past
        else if (!futureEventsFilter && ongoingEventsFilter && pastEventsFilter) {
            return StreamSupport.stream(eventRepository.findAllOngoingAndPastEvents().spliterator(), false).collect(Collectors.toList());
        }
        //future ongoing past
        else if (futureEventsFilter && ongoingEventsFilter && pastEventsFilter) {
            return StreamSupport.stream(eventRepository.findAll().spliterator(), false).collect(Collectors.toList());
        }
        //default - ongoing + future
        else {
            return StreamSupport.stream(eventRepository.findAllOngoingAndFutureEvents().spliterator(), false).collect(Collectors.toList());
        }
    }

    public List<Event> findAllByTitleWithFilters(String title, boolean futureEventsFilter, boolean ongoingEventsFilter, boolean pastEventsFilter) {
        //future
        if (futureEventsFilter && !ongoingEventsFilter && !pastEventsFilter) {
            return StreamSupport.stream(eventRepository.findAllFutureEventsByTitle(title).spliterator(), false).collect(Collectors.toList());
        }
        //ongoing
        else if (!futureEventsFilter && ongoingEventsFilter && !pastEventsFilter) {
            return StreamSupport.stream(eventRepository.findAllOngoingEventsByTitle(title).spliterator(), false).collect(Collectors.toList());
        }
        //past
        else if (!futureEventsFilter && !ongoingEventsFilter && pastEventsFilter) {
            return StreamSupport.stream(eventRepository.findAllPastEventsByTitle(title).spliterator(), false).collect(Collectors.toList());
        }
        //future past
        else if (futureEventsFilter && !ongoingEventsFilter && pastEventsFilter) {
            return StreamSupport.stream(eventRepository.findAllFutureAndPastEventsByTitle(title).spliterator(), false).collect(Collectors.toList());
        }
        //ongoing past
        else if (!futureEventsFilter && ongoingEventsFilter && pastEventsFilter) {
            return StreamSupport.stream(eventRepository.findAllOngoingAndPastEventsByTitle(title).spliterator(), false).collect(Collectors.toList());
        }
        //future ongoing past
        else if (futureEventsFilter && ongoingEventsFilter && pastEventsFilter) {
            return StreamSupport.stream(eventRepository.findAllByTitle(title).spliterator(), false).collect(Collectors.toList());
        }
        //default - ongoing + future
        else {
            return StreamSupport.stream(eventRepository.findAllOngoingAndFutureEventsByTitle(title).spliterator(), false).collect(Collectors.toList());
        }
    }


    @Override
    public void run(String... args) throws Exception {

        //adding test events
        /*eventRepository.save(Event.builder()
                .title("Event from November 22 to March 23")
                .description("It is very interesting event. This is the event from November 22 to March 23.")
                .owner(userRepository.findById(1))
                .startingDateTime(LocalDateTime.of(2022,11,1,6,30))
                .endingDateTime(LocalDateTime.of(2023,3,31,22,0))
                .build());*/
        /*eventRepository.save(Event.builder()
                .title("Unsound Festival 2023")
                .description("Unsound focuses on a broad swath of contemporary music — emerging, experimental, and leftfield —" +
                        " whose sweep doesn't follow typical genre constraints. Influential, it has developed a reputation" +
                        " for identifying innovative scenes and radical sounds.\n" +
                        "Founded in 2003, Unsound wasn’t always the festival it is now. The very first edition" +
                        " ended with artists thrown out of a club for playing music that was too weird for" +
                        " regular patrons. Now, with the main festival still happening every year at a number of" +
                        " venues across Kraków, regular events also take place in New York, Adelaide, Toronto, and London" +
                        ". Between 2016 and 2018, Unsound also produced eleven festivals in Eastern Europe, Central Asia" +
                        " and the Caucasus, part of a long history of working with curators and artists in the post-Soviet" +
                        " region. \n" +
                        "As well as spotlighting emerging artists, Unsound also commissions new shows and encourages" +
                        " transborder collaborations, adapts and reimagines abandoned spaces for concerts and club nights," +
                        " manages cutting-edge artists, and is known for its sound-inspired Ephemera perfume project. Unsound" +
                        " is also a platform, commissioning and releasing new music and books.")
                .owner(userRepository.findById(3))
                .startingDateTime(LocalDateTime.of(2023,9,5,12,00))
                .endingDateTime(LocalDateTime.of(2023,9,12,23,59))
                .build());*/

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
