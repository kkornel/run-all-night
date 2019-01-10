package com.example.kornel.alphaui.utils;

public enum GpsBasedWorkout {
    CYCLING("Cycling"),
    RIDING("Horse riding"),
    ICE_SKATING("Ice skating"),
    ROLLER_SKATING("Roller skating"),
    ROWING("Rowing"),
    RUNNING("Running"),
    SKATEBOARDING("Skateboarding"),
    SKIING("Skiing"),
    SNOWBOARDING("Snowboarding"),
    SWIMMING("Swimming"),
    TREKKING("Trekking"),
    WALKING("Walking");

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
