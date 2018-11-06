package com.example.kornel.alphaui.utils;

public enum GpsBasedActivity {
    CYCLING("Cycling"),
    HIKING("Hiking"),
    ICE_SKATING("Ice skating"),
    KAYAKING("Kayaking"),
    RIDING("Riding"),
    ROLLER_SKATING("Roller skating"),
    ROWING("Rowing"),
    RUNNING("Running"),
    SKATEBOARDING("Skateboarding"),
    SKIING("Skiing"),
    SNOWBOARDING("Snowboarding"),
    SWIMMING("Swimming"),
    WALKING("Walking"),
    WHEELCHAIR("Wheelchair");

    private String value;

    GpsBasedActivity(final String value) {
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
