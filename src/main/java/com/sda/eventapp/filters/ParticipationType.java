package com.sda.eventapp.filters;

public enum ParticipationType {
    OWNED_EVENTS("Owned Events"),
    ATTENDED_EVENTS("Attended Events"),
    ALL_EVENTS("All Events");

    private final String displayValue;

    ParticipationType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
