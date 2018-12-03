package com.example.kornel.alphaui.gpsworkout;

import java.util.Date;

public class CurrentIndoorWorkout {
    private static final String TAG = "CurrentIndoorWorkout";

    private Date mDate;
    private String mWorkoutName;
    private long mDuration;         // [ms]
    private Stopwatch mStopWatch;

    public CurrentIndoorWorkout(String workout) {
        mDate = new Date();
        mWorkoutName = workout;
        mDuration = 0;
        mStopWatch = new Stopwatch();
        startStopwatch();
    }

    public void startStopwatch() {
        mStopWatch.startStopwatch();
    }

    public void pauseStopwatch() {
        mStopWatch.pauseStopwatch();
    }

    public Date getDate() {
        return mDate;
    }

    public String getWorkoutName() {
        return mWorkoutName;
    }

    public long getDuration() {
        return mDuration;
    }

    public String getDurationString() {
        return mStopWatch.getDurationString();
    }

    @Override
    public String toString() {
        return "CurrentIndoorWorkout{" +
                "mDate=" + mDate +
                ", mWorkoutName='" + mWorkoutName + '\'' +
                ", mDuration=" + mDuration +
                ", mStopWatch=" + mStopWatch +
                '}';
    }
}
