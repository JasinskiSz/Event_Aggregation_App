package com.sda.eventapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Event {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 65535) //todo: przetestować czy da się wpisać opis dłuższy niż 255
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "starting_date")
    private LocalDateTime startingDate;

    @Column(name = "ending_date")
    private LocalDateTime endingDate;

    @ManyToMany(mappedBy = "attendingEvents", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY) // cascade = CascadeType.PERSIST
    @JoinTable(name = "events_comments",
            joinColumns = {
                    @JoinColumn(name = "event_id", referencedColumnName = "id",
                            nullable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "comment_id", referencedColumnName = "id",
                            nullable = false)})
    private Set<Comment> comments = new HashSet<>();

    //todo: byc moze starting_time, ending_time, enum ONLINE/INPLACE, capacity, venueId
}
