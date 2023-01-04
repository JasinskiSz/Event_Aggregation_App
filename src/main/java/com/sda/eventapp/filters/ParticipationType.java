package com.sda.eventapp.filters;

public enum ParticipationType {
    OWNED_EVENTS("Owned Events"),
    ATTENDED_EVENTS("Attended Events"),
    ALL_EVENTS("All Events");

    private final String name;

    ParticipationType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
