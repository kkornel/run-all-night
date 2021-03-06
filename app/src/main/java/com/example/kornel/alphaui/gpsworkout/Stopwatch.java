package com.example.kornel.alphaui.gpsworkout;

import android.os.Handler;
import android.os.SystemClock;

public class Stopwatch {
    // total ms elapsed since start
    private long millisecondsTime;

    // ms when the stopwatch started
    private long startTime;

    // ms buffer for pause
    private long timeBuff;
    // timeBuff + millisecondsTime
    private long updateTime;

    // actual values
    private int milliseconds;
    private int seconds;
    private int minutes;
    private int hours;

    private Handler handler;
    private Runnable runnable;

    public Stopwatch() {
        resetStopwatch();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                millisecondsTime = SystemClock.uptimeMillis() - startTime;
                updateTime = timeBuff + millisecondsTime;
                seconds = (int) (updateTime / 1000);

                hours = seconds / 3600;

                minutes = seconds / 60;

                seconds = seconds % 60;

                minutes = minutes % 60;

                milliseconds = (int) (updateTime % 1000);

                handler.postDelayed(this, 500);
            }
        };
    }

    public void startStopwatch() {
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);
    }

    public void pauseStopwatch() {
        timeBuff += millisecondsTime;
        handler.removeCallbacks(runnable);
    }

    public void resetStopwatch() {
        millisecondsTime = 0;
        startTime = 0;
        timeBuff = 0;
        updateTime = 0;
        seconds = 0;
        minutes = 0;
        hours = 0;
        milliseconds = 0;
    }

    public long getTotalMilliSecs() {
        return updateTime;
    }

    public String getDurationString() {
        String time;
        if (hours == 0) {
            time = minutes + ":" + String.format("%02d", seconds);
        } else {
            time = hours + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        }
        return time;
    }
}