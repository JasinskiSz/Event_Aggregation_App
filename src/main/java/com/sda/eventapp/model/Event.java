package com.sda.eventapp.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import javax.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@ToString
@Slf4j
@NoArgsConstructor
public class Event {
    public Event(String title, Date startEventDate, Date endEventDate, String description) {
        this.title = title;
        this.startEventDate = startEventDate;
        this.endEventDate = endEventDate;
        this.description = description;

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    @Column//(length = 65535) //todo: test longer than varchar (255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = true)
    private User owner;


    @Column(name = "starting_date")
    private Date startEventDate;

    @Column(name = "ending_date")

    private Date endEventDate;

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
}