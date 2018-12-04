package com.example.kornel.alphaui.utils;

public enum Privacy {
    ONLY_ME("Tylko ja"),
    FRIENDS("Znajomi");

    private String value;

    Privacy(final String value) {
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
