package com.example.kornel.alphaui.gpsworkout;

import android.util.Log;

public class NewLocationLog {
    private static final String TAG = "NewLocationLog";
    private static boolean isDebuggable = true;

    public static void setDebuggable(final boolean isDebuggable) {
        NewLocationLog.isDebuggable = isDebuggable;
    }

    public static void d(final String tag, final String message) {
        if (!isDebuggable) return;
        Log.d(tag, message);
    }

    public static void d(final String message) {
        if (!isDebuggable) return;
        Log.d(TAG, message);
    }

    public static void e(final String tag, final String message) {
        if (!isDebuggable) return;
        Log.e(tag, message);
    }

    public static void e(final String message) {
        if (!isDebuggable) return;
        Log.e(TAG, message);
    }

    public static void printStack(final Exception e) {
        if (!isDebuggable) return;
        e.printStackTrace();
    }

}
