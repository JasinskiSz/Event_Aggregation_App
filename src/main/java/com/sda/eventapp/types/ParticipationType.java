package com.sda.eventapp.types;

import lombok.Getter;

@Getter
public enum ParticipationType {
    OWNED("Owned"),
    ATTENDED("Attended"),
    ALL("All");

    private final String name;

    ParticipationType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
