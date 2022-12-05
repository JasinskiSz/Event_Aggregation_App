package com.sda.eventapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

//todo Builders must have ALLARGSCONSTRUCTOR

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "username")
    private String username;

    private String password;

    private String email;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private Set<Event> ownedEvents;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "users_events",
    joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id",
            nullable = true)},
    inverseJoinColumns = {
            @JoinColumn(name = "event_id", referencedColumnName = "id",
            nullable = true)})
    private Set<Event> attendingEvents = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
