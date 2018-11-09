package com.example.kornel.alphaui.gpsworkout;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.example.kornel.alphaui.utils.Lap;
import com.example.kornel.alphaui.utils.LatLon;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class CurrentGpsWorkout {
    private static final String TAG = "CurrentGpsWorkout";

    // Type of current activity
    private String mWorkoutName;

    // Array of all LatLng on path
    private ArrayList<LatLon> mPath;

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

    public String getTimeString() {
        return mStopWatch.getTimeString();
    }

    public ArrayList<LatLon> getPath() {
        return mPath;
    }

    public String getWorkoutName() {
        return mWorkoutName;
    }

    public void calculateDistanceBetweenTwoLocations(Location previous, Location newLocation) {
        mDistance += previous.distanceTo(newLocation);
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
        LatLon previousLatLng = mPath.get(pathSize - 2);
        previousLocation.setLatitude(previousLatLng.getLatitude());
        previousLocation.setLongitude(previousLatLng.getLongitude());

        Location currentLocation = new Location(LocationManager.GPS_PROVIDER);
        LatLon currentLatLng = mPath.get(pathSize - 1);
        currentLocation.setLatitude(currentLatLng.getLatitude());
        currentLocation.setLongitude(currentLatLng.getLatitude());

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
            // Lap newLap = new Lap(currentLatLng, mDuration, mDuration - previousLapTimeStamp);
            // mLaps.add(newLap);
            // Log.d("dsa", "new lap! " + newLap);
        }
    }

    public void addLatLngToPath(LatLon newLatLng) {
        Log.d(TAG, "Adding newLatLng to path: " + newLatLng);
        mPath.add(newLatLng);
    }


    public double getDistance() {
        return mDistance;
    }

    public ArrayList<LatLon> getTestLatLng() {
        ArrayList<LatLon> mTestCoordinates = new ArrayList<>();
        mTestCoordinates.add(new LatLon(52.416042, 16.939496));
        mTestCoordinates.add(new LatLon(52.415358, 16.939367));
        mTestCoordinates.add(new LatLon(52.414553, 16.939206));
        mTestCoordinates.add(new LatLon(52.413627, 16.939072));
        mTestCoordinates.add(new LatLon(52.412538, 16.938852));
        mTestCoordinates.add(new LatLon(52.411671, 16.938482));
        mTestCoordinates.add(new LatLon(52.410830, 16.938246));
        mTestCoordinates.add(new LatLon(52.409943, 16.938353));
        mTestCoordinates.add(new LatLon(52.409449, 16.938267));
        mTestCoordinates.add(new LatLon(52.409010, 16.938155));
        mTestCoordinates.add(new LatLon(52.408706, 16.938106));
        mTestCoordinates.add(new LatLon(52.408238, 16.938047));
        mTestCoordinates.add(new LatLon(52.407708, 16.937945));
        mTestCoordinates.add(new LatLon(52.407407, 16.937881));
        mTestCoordinates.add(new LatLon(52.407138, 16.937838));
        mTestCoordinates.add(new LatLon(52.406863, 16.937801));
        mTestCoordinates.add(new LatLon(52.406340, 16.937704));
        mTestCoordinates.add(new LatLon(52.405842, 16.937591));
        mTestCoordinates.add(new LatLon(52.405430, 16.937559));
        mTestCoordinates.add(new LatLon(52.405083, 16.937490));
        mTestCoordinates.add(new LatLon(52.404573, 16.937345));
        mTestCoordinates.add(new LatLon(52.403615, 16.936963));
        mTestCoordinates.add(new LatLon(52.403060, 16.936833));
        mTestCoordinates.add(new LatLon(52.402435, 16.936616));
        mTestCoordinates.add(new LatLon(52.401926, 16.936400));
        mTestCoordinates.add(new LatLon(52.401483, 16.936255));
        mTestCoordinates.add(new LatLon(52.401002, 16.936043));
        mTestCoordinates.add(new LatLon(52.400829, 16.936231));
        mTestCoordinates.add(new LatLon(52.400959, 16.936802));
        mTestCoordinates.add(new LatLon(52.401138, 16.937424));
        mTestCoordinates.add(new LatLon(52.401352, 16.937919));
        mTestCoordinates.add(new LatLon(52.401457, 16.938247));
        mTestCoordinates.add(new LatLon(52.401613, 16.938716));
        return mTestCoordinates;
    }
}