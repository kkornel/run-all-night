package com.example.kornel.alphaui.utils;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.kornel.alphaui.gpsworkout.WorkoutSummary;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Utils {

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboard(Fragment fragment) {
        InputMethodManager imm = (InputMethodManager) fragment.getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(fragment.getView().getRootView().getWindowToken(), 0);
    }

    private void sortListByDate(List<WorkoutSummary> list) {
        Collections.sort(list, new Comparator<WorkoutSummary>() {
            public int compare(WorkoutSummary w1, WorkoutSummary w2) {
                if (w1.getWorkoutDate() == null || w2.getWorkoutDate() == null)
                    return 0;
                return w2.getWorkoutDate().compareTo(w1.getWorkoutDate());
            }
        });
    }

    public static String paceToString(double minPerKm) {
        long minPartOfMinPerKm = (long) minPerKm;
        double secPartOfMinPerKm = minPerKm - minPartOfMinPerKm;

        double correctSecs = 60 * secPartOfMinPerKm;
        double correctSecsRounded = Math.round(correctSecs);

        return minPartOfMinPerKm + ":" + String.format("%02d", (int) correctSecsRounded);
    }

    public static String distanceMetersToKm(double distance) {

        if (distance == 0) {
            return "0.00";
        }

        distance /= 1000;

        // String.format("%.5g%n", distance);
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(distance);
    }

    public static String getDurationString(long ms) {
        int seconds = (int) (ms / 1000);

        int hours = seconds / 3600;

        int minutes = seconds / 60;

        seconds = seconds % 60;

        minutes = minutes % 60;

        String time;
        if (hours == 0) {
            time = minutes + ":" + String.format("%02d", seconds);
        } else {
            time = hours + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        }
        return time;
    }

    public static String getDurationHoursString(long ms) {
        int seconds = (int) (ms / 1000);

        int hours = seconds / 3600;

        int minutes = seconds / 60;

        seconds = seconds % 60;

        minutes = minutes % 60;

        String time;
        if (hours < 1) {
            time = String.format("%.2f", minutes/60.0);
        } else {
            time = hours + String.format("%.2f", minutes/60.0);
        }
        return time;
    }
}
