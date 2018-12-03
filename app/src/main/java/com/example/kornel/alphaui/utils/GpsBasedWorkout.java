package com.example.kornel.alphaui.utils;

public enum GpsBasedWorkout {
    RUNNING("Bieganie"),
    WALKING("Chód"),
    SKATEBOARDING("Jazda na deskorolce"),
    RIDING("Jazda konno"),
    ICE_SKATING("Jazda na łyżwach"),
    SKIING("Jazda na nartach"),
    ROLLER_SKATING("Jazda na rolkach"),
    SNOWBOARDING("Jazda na snowboardzie"),
    CYCLING("Jazda rowerem"),
    SWIMMING("Pływanie"),
    TREKKING("Trekking"),
    ROWING("Wiosłowanie");

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
