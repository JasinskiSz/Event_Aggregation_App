package com.sda.eventapp.model;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

//todo Builders must have ALLARGSCONSTRUCTOR

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_name")
    private String userName;

    private String password;

    private String email;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private Set<Event> ownedEvents;

    @ManyToMany(fetch = FetchType.LAZY)// cascade = CascadeType.PERSIST)
    @JoinTable(name = "users_events",
            joinColumns = {
                    @JoinColumn(name = "user_id", referencedColumnName = "id",
                            nullable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "event_id", referencedColumnName = "id",
                            nullable = false)})
    private Set<Event> attendingEvents = new HashSet<>();


}