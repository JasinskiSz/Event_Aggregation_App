package com.sda.eventapp.filters;

public enum ParticipationType {
    OWNED_EVENTS("Owned Events"),
    ATTENDED_EVENTS("Attended Events"),
    ALL("All Events");

    private final String displayValue;


    ParticipationType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
