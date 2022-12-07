package com.sda.eventapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 500)
    private String text;

    @Column(name = "writing_date")
    private LocalDateTime writingDate;

    @ManyToMany(mappedBy = "comments", fetch = FetchType.LAZY)
    private Set<Event> events = new HashSet<>();
}
