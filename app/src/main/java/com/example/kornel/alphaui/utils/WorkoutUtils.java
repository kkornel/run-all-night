package com.example.kornel.alphaui.utils;

public class WorkoutUtils {
    public static boolean isGpsBased(String workout) {
        for (GpsBasedWorkout gpsWorkout : GpsBasedWorkout.values()) {
            if (gpsWorkout.toString().equals(workout)) {
                return true;
            }
        }
        return false;
    }
}
