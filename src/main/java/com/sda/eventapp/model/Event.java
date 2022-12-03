package com.sda.eventapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 65535)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = true)
    private User owner;

    @Column(name = "starting_date")
    private LocalDateTime startingDate;

    @Column(name = "ending_date")
    private LocalDateTime endingDate;

    @ManyToMany(mappedBy = "attendingEvents", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST) //
    @JoinTable(name = "events_comments",
            joinColumns = {
                    @JoinColumn(name = "event_id", referencedColumnName = "id",
                            nullable = true)},
            inverseJoinColumns = {
                    @JoinColumn(name = "comment_id", referencedColumnName = "id",
                            nullable = true)})
    private Set<Comment> comments = new HashSet<>();

    //todo: optional: starting_time, ending_time, enum ONLINE/INPLACE, capacity, venueId
}
