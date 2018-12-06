package com.example.kornel.alphaui.utils;

import com.example.kornel.alphaui.mainactivity.MainActivityLog;

import java.util.Date;

public class WorkoutUtils {
    public static final String WORKOUT_DETAILS_EXTRA_INTENT = "workout_summary";


    public static boolean isGpsBased(String workout) {
        for (GpsBasedWorkout gpsWorkout : GpsBasedWorkout.values()) {
            if (gpsWorkout.toString().equals(workout)) {
                return true;
            }
        }
        return false;
    }

    public static String gapBetweenWorkouts(Date workoutDate) {
        Date todayDate = new Date();
        long different = todayDate.getTime() - workoutDate.getTime();

        MainActivityLog.d("todayDate : " + todayDate);
        MainActivityLog.d("workoutDate : " + workoutDate);
        MainActivityLog.d("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        MainActivityLog.d(elapsedDays + " days, " + elapsedHours + " hours, " + elapsedMinutes + " minutes, " + elapsedSeconds + " seconds");

        if (elapsedDays > 0) {
            if (elapsedDays == 1) {
                return "1 dzień temu";
            } else {
                return String.valueOf(elapsedDays) + " dni temu";
            }
        } else {
            if (elapsedHours < 1) {
                if (elapsedMinutes < 2) {
                    return "Chwilę temu";
                } else if (elapsedMinutes >= 2 && elapsedMinutes < 5) {
                    return String.valueOf(elapsedMinutes) + " minuty temu";
                } else {
                    return String.valueOf(elapsedMinutes) + " minut temu";
                }
            } else if (elapsedHours == 1) {
                return "1 godzinę temu";
            } else if (elapsedHours >= 2 && elapsedHours < 5) {
                return String.valueOf(elapsedMinutes) + " godziny temu";
            } else {
                return String.valueOf(elapsedHours) + " godzin temu";
            }
        }
    }
}