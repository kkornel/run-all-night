package com.example.kornel.alphaui;

public enum NonGpsBasedActivity  {
    AEROBICS("Aerobics"),
    BADMINTON("Badminton"),
    BASKETBALL("Basketball"),
    BOXING("Boxing"),
    CROSSFIR("Crossfit"),
    DANCING("Dancing"),
    ELLIPTICAL("Elliptical"),
    GYMNASTICS("Gymnastics"),
    INDOOR_CYCLING("Indoor cycling"),
    INDOOR_ROWING("Indoor rowing"),
    ROPE_JUMPING("Rope jumping"),
    SQUASH("Squash"),
    YOGA("Yoga"),
    WEIGHT_TRAINING("Weight training"),
    ZUMBA("Zumba");

    private String value;

    NonGpsBasedActivity(final String value) {
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
