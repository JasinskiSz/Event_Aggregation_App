package com.sda.eventapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    public Event(String title, String description, Date startingDateTime, Date endingDateTime) {
        this.title = title;
        this.description = description;
        this.startingDateTime = startingDateTime;
        this.endingDateTime = endingDateTime;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 65535)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    // TODO: Need to change type Data to LocalDataTime. LocalDataTime now doesn't work with Create Event Form, because of calender field.
    @Column(name = "starting_date_time")
    private Date startingDateTime;

    @Column(name = "ending_date_time")
    private Date endingDateTime;

    @ManyToMany(mappedBy = "attendingEvents", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST) //
    @JoinTable(name = "events_comments",
            // TODO: check if there is need to set property nullable = false;
            joinColumns = {
                    @JoinColumn(name = "event_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                    @JoinColumn(name = "comment_id", referencedColumnName = "id")})
    private Set<Comment> comments = new HashSet<>();

    //todo: optional: starting_time, ending_time, enum ONLINE/INPLACE, capacity, venueId
}
