package com.sda.eventapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "user_name")
    private String userName;

    private String password;

    private String email;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY) //cascade = CascadeType.ALL ??????
    private Set<Comment> comments;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)  //todo: problem z eventami ktore ma owner a z tworzeniem tabeli manytomany
    private Set<Event> ownedEvents;

    @ManyToMany(fetch = FetchType.LAZY) // cascade = CascadeType.PERSIST
    @JoinTable(name = "users_events",
    joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id",
            nullable = false)},
    inverseJoinColumns = {
            @JoinColumn(name = "event_id", referencedColumnName = "id",
            nullable = false)})
    private Set<Event> attendingEvents = new HashSet<>();

}
