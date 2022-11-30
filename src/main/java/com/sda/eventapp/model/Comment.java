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
public class Comment {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String text;

    @Column(name = "writing_date")
    private LocalDateTime writingDate;

    @ManyToMany(mappedBy = "comments", fetch = FetchType.LAZY)
    private Set<Event> events = new HashSet<>();


}
