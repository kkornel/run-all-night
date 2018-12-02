package com.example.kornel.alphaui.utils;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.kornel.alphaui.FriendWorkout;
import com.example.kornel.alphaui.gpsworkout.WorkoutGpsSummary;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Utils {

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboard(Fragment fragment) {
        InputMethodManager imm = (InputMethodManager) fragment.getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(fragment.getView().getRootView().getWindowToken(), 0);
    }

    private void sortListByDate(List<WorkoutGpsSummary> list) {
        Collections.sort(list, new Comparator<WorkoutGpsSummary>() {
            public int compare(WorkoutGpsSummary w1, WorkoutGpsSummary w2) {
                if (w1.getWorkoutDate() == null || w2.getWorkoutDate() == null)
                    return 0;
                return w2.getWorkoutDate().compareTo(w1.getWorkoutDate());
            }
        });
    }
}
