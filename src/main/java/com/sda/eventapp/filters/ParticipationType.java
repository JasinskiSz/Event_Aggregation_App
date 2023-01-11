package com.sda.eventapp.filters;

public enum ParticipationType {
    OWNED_EVENTS("Owned"),
    ATTENDED_EVENTS("Attended"),
    ALL_EVENTS("All");

    private final String name;

    ParticipationType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
