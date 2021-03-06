package com.example.kornel.alphaui.utils;

public enum NonGpsBasedWorkout {
    AEROBICS("Aerobics"),
    BADMINTON("Badminton"),
    BASKETBALL("Basketball"),
    BOXING("Boxing"),
    CROSSFIT("Crossfit"),
    DANCING("Dancing"),
    ELLIPTICAL("Elliptical"),
    GYMNASTICS("Gymnastics"),
    INDOOR_ROWING("Indoor rowing"),
    ROPE_JUMPING("Rope jumping"),
    SPINNING("Spinning"),
    SQUASH("Squash"),
    YOGA("Yoga"),
    WEIGHT_TRAINING("Weight training"),
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
