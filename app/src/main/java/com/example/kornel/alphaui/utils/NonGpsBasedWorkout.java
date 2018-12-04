package com.example.kornel.alphaui.utils;

public enum NonGpsBasedWorkout {
    AEROBICS("Areobik"),
    BADMINTON("Badminton"),
    BASKETBALL("Koszykówka"),
    BOXING("Boks"),
    CROSSFIT("Crossfit"),
    DANCING("Taniec"),
    ELLIPTICAL("Orbitrek"),
    GYMNASTICS("Gimnastyka"),
    INDOOR_ROWING("Wiosłowanie indoor"),
    ROPE_JUMPING("Skoki na skakance"),
    SPINNING("Spinning"),
    SQUASH("Squash"),
    YOGA("Yoga"),
    WEIGHT_TRAINING("Trening siłowy"),
    ZUMBA("Zumba");

    private String value;

    NonGpsBasedWorkout(final String value) {
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
