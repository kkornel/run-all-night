package com.example.kornel.alphaui.gpsworkout;


import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.example.kornel.alphaui.utils.Lap;
import com.example.kornel.alphaui.utils.LatLon;

import java.util.ArrayList;
import java.util.List;


public class CurrentGpsWorkout {
    private static final String TAG = "CurrentGpsWorkout";

    // Type of current activity
    private String mWorkoutName;

    // Array of all LatLng on path
    private ArrayList<LatLon> mPath;

    // Array of all laps. Lap = 1 km
    private ArrayList<Lap> mLaps;

    // Total distance since started tracking in meters
    // private double mTotalDistance;

    // Total time since started tracking in milliseconds
    // private long mDuration;

    private Stopwatch mStopWatch;

    public CurrentGpsWorkout(String workout) {
        mWorkoutName = workout;
        mPath = new ArrayList<>();
        mLaps = new ArrayList<>();
        mTotalDistance = 0.00;
        mDuration = 0;
        mPace = 0.0;
        mSpeed = 0.0;
        mPaceString = "0:00";
        mStopWatch = new Stopwatch();
        startStopwatch();
    }

    public void startStopwatch() {
        mStopWatch.startStopwatch();
    }

    public void pauseStopwatch() {
        mStopWatch.pauseStopwatch();
    }

    public void calculateDistanceBetweenTwoLocations(Location previous, Location newLocation) {
        mTotalDistance += previous.distanceTo(newLocation);
    }

    public float distanceBetweenTwoLastLocations(Location previous, Location newLocation) {
        return previous.distanceTo(newLocation);
    }

    private long timeBetweenTwoLastLocations() {
        int pathSize = mPath.size();
        if (pathSize <= 1) {
            return 0;
        }
        long previousLocationTimeStamp = mPath.get(pathSize - 2).getTimeStamp();
        long currentLocationTimeStamp = mPath.get(pathSize - 1).getTimeStamp();
        return currentLocationTimeStamp - previousLocationTimeStamp;
    }

    // duration [ms]
    private long mDuration;
    // distance [m]
    private double mTotalDistance;
    // pace [min/km]
    private double mPace;
    // speed [km/h]
    private double mSpeed;

    private String mPaceString = "0:00";

    private int laps = 1;

    private void checkForLap(Location newLocation) {
        /*
         * mTotalDistance = 854
         * km = 0
         * 1 == 0
         * laps ++
         */
        /*
         * mTotalDistance = 1054
         * km = 1
         * 1 == 1
         * LAP!
         * laps ++
         * ale zostaje 54m!
         */
        int km = (int) mTotalDistance / 1000;

        if (laps == km) {
            Log.d(TAG, "checkForLap: new LAP! at: " + mTotalDistance);
            LatLon newLatLon = new LatLon(
                    newLocation.getLatitude(),
                    newLocation.getLongitude(),
                    mDuration);
            Lap newLap = null;
            if (mLaps.size() == 0) {
                newLap = new Lap(newLatLon, mDuration);
            } else {
                newLap = new Lap(newLatLon, mDuration - mLaps.get(mLaps.size()-1).getLatLonTimeStamp());
            }
            mLaps.add(newLap);
            laps++;
            for (Lap lap : mLaps) {
                Log.d(TAG, "checkForLap: " + lap);
            }
        }
    }

    // avg pace
    // max pace
    // avg speed
    // max speed
    public void calculateNewDetails(Location previous, Location newLocation) {

        mDuration = mStopWatch.getTotalMilliSecs();

        float distanceBetweenTwoLocations = distanceBetweenTwoLastLocations(previous, newLocation);

        mTotalDistance += distanceBetweenTwoLocations;

        checkForLap(newLocation);

        long timeBetweenTwoLastLocations = timeBetweenTwoLastLocations();

        double km = mTotalDistance / 1000.0;

        int sec = (int) (getTimeStamp() / 1000);

        int minInt = sec / 60;
        double minDouble = (double) sec / 60.0;

        int hourInt = minInt / 60;
        double hourDouble = minDouble / 60.0;

        double minIntPerKm = (double) (minInt / km);
        double minDoublePerKm = (double) minDouble / km;

        long mins = (long) minDoublePerKm;
        double secs = minDoublePerKm - mins;
        double correctSecs = 60 * secs;
        double correctSecsRounded = Math.round(correctSecs);
        mPaceString = mins + ":" + (int) correctSecsRounded;
        mPace = minDoublePerKm;


        double kmPerHourInt = (double) km / hourInt;
        double kmPerHourDouble = (double) km / hourDouble;
        mSpeed = kmPerHourDouble;

        Log.d("calculateNewDetails", "distanceBetweenTwoLocations: " + distanceBetweenTwoLocations);
        Log.d("calculateNewDetails", "timeBetweenTwoLastLocations: " + timeBetweenTwoLastLocations);
        Log.d("calculateNewDetails", "mTotalDistance: " + mTotalDistance);
        Log.d("calculateNewDetails", "mDuration: " + mDuration);
        Log.d("calculateNewDetails", "km: " + km);
        Log.d("calculateNewDetails", "sec: " + sec);
        Log.d("calculateNewDetails", "minInt: " + minInt);
        Log.d("calculateNewDetails", "minDouble: " + minDouble);
        Log.d("calculateNewDetails", "hourInt: " + hourInt);
        Log.d("calculateNewDetails", "hourDouble: " + hourDouble);
        Log.d("calculateNewDetails", "minIntPerKm: " + minIntPerKm);
        Log.d("calculateNewDetails", "minDoublePerKm: " + minDoublePerKm);
        Log.d("calculateNewDetails", "kmPerHourInt: " + kmPerHourInt);
        Log.d("calculateNewDetails", "kmPerHourDouble: " + kmPerHourDouble);
        Log.d("calculateNewDetails", "========================================================================");
        Log.d("calculateNewDetails", "minDoublePerKm: " + minDoublePerKm);
        Log.d("calculateNewDetails", "mins: " + mins);
        Log.d("calculateNewDetails", "secs: " + secs);
        Log.d("calculateNewDetails", "correctSecs: " + correctSecs);
        Log.d("calculateNewDetails", "correctSecsRounded: " + correctSecsRounded);
        Log.d("calculateNewDetails", "mPaceString: " + mPaceString);
        Log.d("calculateNewDetails", "========================================================================");
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
        mTotalDistance += distance;

        Log.d(TAG, "distance between " + previousLatLng + " and " + currentLatLng + " = " + distance);
        Log.d(TAG, "total distance = " + mTotalDistance);

        mDuration = mStopWatch.getTotalMilliSecs() / 1000;
        Log.d(TAG, "mDistance = " + mTotalDistance);
        Log.d(TAG, "mDuration = " + mDuration);
        Log.d(TAG, "s/m = " + mDuration / mTotalDistance);
        Log.d(TAG, "m/s = " + mTotalDistance / mDuration);
        Log.d(TAG, "km/h = " + ((mTotalDistance / 1000) / (mDuration) / 60));

        // TODO: Check if made lap. How to do it in a nice way?
        if (mTotalDistance / 1000 >= 1) {
            mDuration = mStopWatch.getTotalMilliSecs();

            long previousLapTimeStamp = 0;
            if (mLaps.size() > 1) {
                // previousLapTimeStamp = mLaps.get(mLaps.size() - 1).getTimeStamp();
            }
            // Lap newLap = new Lap(currentLatLng, mDuration, mDuration - previousLapTimeStamp);
            // mLaps.add(newLap);
            // Log.d("dsa", "new lap! " + newLap);
        }
    }

    public void addLatLngToPath(LatLon newLatLng) {
        mPath.add(newLatLng);
    }

    public String getWorkoutName() {
        return mWorkoutName;
    }

    public double getTotalDistance() {
        return mTotalDistance;
    }

    public long getDuration() {
        return mDuration;
    }

    public String getDurationString() {
        return mStopWatch.getDurationString();
    }

    public ArrayList<LatLon> getPath() {
        return mPath;
    }

    public double getPace() {
        return mPace;
    }

    public double getSpeed() {
        return mSpeed;
    }

    public String getPaceString() {
        return mPaceString;
    }

    public ArrayList<Lap> getLaps() {
        return mLaps;
    }

    public long getTimeStamp() {
        return mStopWatch.getTotalMilliSecs();
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