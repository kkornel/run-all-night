package com.example.kornel.alphaui.utils;

public enum GpsBasedWorkout {
    RUNNING("Running"),
    WALKING("Walking"),
    SKATEBOARDING("Skateboarding"),
    RIDING("Horse riding"),
    ICE_SKATING("Ice skating"),
    SKIING("Skiing"),
    ROLLER_SKATING("Roller skating"),
    SNOWBOARDING("Snowboarding"),
    CYCLING("Cycling"),
    SWIMMING("Swimming"),
    TREKKING("Trekking"),
    ROWING("Rowing");

    private String value;

    GpsBasedWorkout(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
