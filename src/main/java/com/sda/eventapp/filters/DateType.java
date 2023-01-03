package com.sda.eventapp.filters;

public enum DateType {
    FUTURE("Future"),
    FUTURE_AND_ONGOING("Future and Ongoing"),
    PAST("Past"),
    ALL("All");

    private final String displayValue;

    DateType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
