package com.example.kornel.alphaui.gpsworkout;


import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Stopwatch {
    private static final String TAG = "Stopwatch";

    // total ms elapsed since start
    private long millisecondsTime;

    // ms when the stopwatch started
    private long startTime;

    // ms buffer for pause
    private long timeBuff;
    private long updateTime;

    private int milliseconds;
    private int seconds;
    private int minutes;

    private List<Integer> laps;
    private int lastLapSec;

    private Handler handler;
    private Runnable runnable;

    public Stopwatch() {
        resetStopwatch();
        laps = new ArrayList<>();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                millisecondsTime = SystemClock.uptimeMillis() - startTime;
                updateTime = timeBuff + millisecondsTime;
                seconds = (int) (updateTime / 1000);

                minutes = seconds / 60;

                seconds = seconds % 60;

                milliseconds = (int) (updateTime % 1000);

                handler.postDelayed(this, 500);
            }
        };
    }

    public void startStopwatch() {
        startTime = SystemClock.uptimeMillis();
        Log.d(TAG, "startStopwatch: " + startTime);
        handler.postDelayed(runnable, 0);
    }

    public void pauseStopwatch() {
        timeBuff += millisecondsTime;
        Log.d(TAG, "pauseStopwatch: " + timeBuff);
        handler.removeCallbacks(runnable);
    }

    public void resetStopwatch() {
        millisecondsTime = 0;
        startTime = 0;
        timeBuff = 0;
        updateTime = 0;
        seconds = 0;
        minutes = 0;
        milliseconds = 0;
        lastLapSec = 0;
    }

    public long getTotalMilliSecs() {
        return millisecondsTime;
    }

    public void makeLap() {
        int lap = (int) seconds + minutes * 60 - lastLapSec;
        lastLapSec = lap;
        laps.add(lap);
        Log.d(TAG, "makeLap: " + lap);
    }

    public String getTimeString() {
        String time = minutes + ":" + String.format("%02d", seconds);
        return time;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getMinutes() {
        return minutes;
    }
}