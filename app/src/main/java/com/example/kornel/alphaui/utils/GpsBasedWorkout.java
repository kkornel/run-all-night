package com.example.kornel.alphaui.utils;

public enum GpsBasedWorkout {
    CYCLING("Jazda rowerem"),
    ICE_SKATING("Jazda na łyżwach"),
    RIDING("Jazda Konno"),
    ROLLER_SKATING("Jazda na rolkach"),
    ROWING("Wiosłowanie"),
    RUNNING("Bieganie"),
    SKATEBOARDING("Jazda na deskorolce"),
    SKIING("Jazda na nartach"),
    SNOWBOARDING("Jazda na snowboardzie"),
    SWIMMING("Pływanie"),
    TREKKING("Trekking"),
    WALKING("Chód"),
    WHEELCHAIR("Jazda na wózku");

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
