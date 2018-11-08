package com.example.kornel.alphaui.gpsworkout;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.example.kornel.alphaui.utils.Lap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class CurrentGpsWorkout {
    private static final String TAG = "CurrentGpsWorkout";

    // Type of current activity
    private String mWorkoutName;

    // Array of all LatLng on path
    private ArrayList<LatLng> mPath;

    // Array of all laps. Lap = 1 km
    private List<Lap> mLaps;

    // Total distance since started tracking in meters
    private double mDistance;

    // Total time since started tracking in milliseconds
    private long mDuration;

    private Stopwatch mStopWatch;

    public CurrentGpsWorkout(String workout) {
        mWorkoutName = workout;
        mPath = new ArrayList<>();
        mLaps = new ArrayList<>();
        mDistance = 0.00;
        mDuration = 0;
        mStopWatch = new Stopwatch();
        mStopWatch.startStopwatch();
    }

    public void startStopwatch() {
        mStopWatch.startStopwatch();
    }

    public void pauseStopwatch() {
        mStopWatch.pauseStopwatch();
    }

    public String getTime() {
        return mStopWatch.getTimeString();
    }

    public ArrayList<LatLng> getPath() {
        return mPath;
    }

    public String getWorkoutName() {
        return mWorkoutName;
    }

    public void calculateDistanceBetweenTwoLastLocations() {
        int pathSize = mPath.size();
        if (pathSize <= 1) {
            return;
        }
        // I am doing this after already adding new LatLng to the path.
        // So current location has index = size-1. Because already added.
        // And last location has index = size-2.

        Location previousLocation = new Location(LocationManager.GPS_PROVIDER);
        LatLng previousLatLng = mPath.get(pathSize - 2);
        previousLocation.setLatitude(previousLatLng.latitude);
        previousLocation.setLongitude(previousLatLng.longitude);

        Location currentLocation = new Location(LocationManager.GPS_PROVIDER);
        LatLng currentLatLng = mPath.get(pathSize - 1);
        currentLocation.setLatitude(currentLatLng.latitude);
        currentLocation.setLongitude(currentLatLng.longitude);

        double distance = previousLocation.distanceTo(currentLocation);
        mDistance += distance;

        Log.d(TAG, "distance between " + previousLatLng + " and " + currentLatLng + " = " + distance);
        Log.d(TAG, "total distance = " + mDistance);

        mDuration = mStopWatch.getTotalMilliSecs() / 1000;
        Log.d(TAG, "mDistance = " + mDistance);
        Log.d(TAG, "mDuration = " + mDuration);
        Log.d(TAG, "s/m = " + mDuration / mDistance);
        Log.d(TAG, "m/s = " + mDistance / mDuration);
        Log.d(TAG, "km/h = " + ((mDistance/1000) / (mDuration)/60));

        // TODO: Check if made lap. How to do it in a nice way?
        if (mDistance / 1000 >= 1) {
            mDuration = mStopWatch.getTotalMilliSecs();

            long previousLapTimeStamp = 0;
            if (mLaps.size() > 1) {
                previousLapTimeStamp = mLaps.get(mLaps.size()-1).getTimeStamp();
            }
            Lap newLap = new Lap(currentLatLng, mDuration, mDuration - previousLapTimeStamp);
            mLaps.add(newLap);
            Log.d("dsa", "new lap! " + newLap);
        }
    }

    public void addLatLngToPath(LatLng newLatLng) {
        Log.d(TAG, "Adding newLatLng to path: " + newLatLng);
        mPath.add(newLatLng);
    }


    public double getDistance() {
        return mDistance;
    }
}