package com.sda.eventapp.filters;

public enum DateType {
    FUTURE("Future"),
    FUTURE_AND_ONGOING("Future and Ongoing"),
    PAST("Past"),
    ALL("All");

    private final String name;

    DateType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
