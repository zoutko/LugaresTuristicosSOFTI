package com.proyecto.test.utils;

public enum Environment {
    INDOOR,
    MIXED,
    OUTDOOR;

    public boolean isOutdoor() {
        return this == OUTDOOR || this == MIXED;
    }
}
