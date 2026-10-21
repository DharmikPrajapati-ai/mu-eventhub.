package com.mueventhub.model;

public enum EventType {
    HACKATHON("Hackathon"),
    TECHFEST("Tech Fest"),
    CULTURAL("Cultural Fest"),
    WORKSHOP("Workshop"),
    COMPETITION("Competition"),
    SPORTS("Sports"),
    OTHER("Other");

    private final String label;

    EventType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** CSS class suffix, e.g. "hackathon". */
    public String getCss() {
        return name().toLowerCase();
    }
}
