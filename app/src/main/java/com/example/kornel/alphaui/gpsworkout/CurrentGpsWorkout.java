package com.example.kornel.alphaui.gpsworkout;

import android.location.Location;

import com.example.kornel.alphaui.utils.Lap;
import com.example.kornel.alphaui.utils.LatLon;
import com.example.kornel.alphaui.utils.NewLocationLog;
import com.example.kornel.alphaui.utils.Position;
import com.example.kornel.alphaui.utils.Utils;

import java.util.ArrayList;
import java.util.Date;

public class CurrentGpsWorkout {
    private static final int MAX_OMITTED_LAT_LONS = 5;
    private static final double MIN_VALUE_OF_TOTAL_DISTANCE_METERS = 0.5;
    private static final double MIN_VALUE_OF_METERS_BETWEEN_LOCATIONS = 0.5;

    private Date mDate;

    private String mWorkoutName;

    // duration [ms]
    private long mDuration;

    // distance [m]
    private double mTotalDistance;

    // pace [min/km]
    // total time / total distance
    private double mAvgPace;
    private String mAvgPaceString;

    // time between positions / distance between positions
    private double mCurrentPace;
    private String mCurrentPaceString;

    // the smallest of current paces
    private double mMaxPace;
    private String mMaxPaceString;

    // speed [km/h]
    // total distance / total time
    private double mAvgSpeed;
    // distance between positions / time between positions
    private double mCurrentSpeed;
    // the largest of current speeds
    private double mMaxSpeed;

    // Array of selected LatLng on path. Not storing all, because it's a large set od data.
    private ArrayList<LatLon> mPath;

    // Array of all laps. Lap = 1 km
    private ArrayList<Lap> mLaps;

    private Stopwatch mStopWatch;

    private Location mPreviousLocation;

    private Position mCurrentPosition;
    private Position mPreviousPosition;

    private int mCurrentLap;

    private int mCurrentNewLatLon;

    public CurrentGpsWorkout(String workout) {
        mDate = new Date();
        mWorkoutName = workout;
        mDuration = 0;
        mTotalDistance = 0.0;
        mAvgPace = 0.0;
        mAvgPaceString = "0:00";
        mCurrentPace = 0.0;
        mCurrentPaceString = "0:00";
        mMaxPace = 1000.0;
        mMaxPaceString = "0:00";
        mAvgSpeed = 0.0;
        mCurrentSpeed = 0.0;
        mMaxSpeed = 0.0;
        mPath = new ArrayList<>();
        mLaps = new ArrayList<>();
        mStopWatch = new Stopwatch();
        mPreviousLocation = null;
        mCurrentPosition = null;
        mPreviousPosition = null;
        mCurrentLap = 1;
        mCurrentNewLatLon = 1;
        startStopwatch();
    }

    public void onNewLocation(Location newLocation) {
        LatLon newLatLon = new LatLon(
                newLocation.getLatitude(),
                newLocation.getLongitude());

        mCurrentPosition = new Position(
                newLatLon,
                mStopWatch.getTotalMilliSecs());

        NewLocationLog.d("onNewLocation: mCurrentPosition=" + mCurrentPosition.toString());

        if (mCurrentNewLatLon == 1) {
            NewLocationLog.d("onNewLocation: mCurrentNewLatLon=" + mCurrentNewLatLon + " - ADDING TO THE PATH!");
            addLatLngToPath(newLatLon);
        } else {
            NewLocationLog.d("onNewLocation: mCurrentNewLatLon=" + mCurrentNewLatLon + " - OMITTING!");
        }

        calculateNewDetails(newLocation);

        if (++mCurrentNewLatLon == MAX_OMITTED_LAT_LONS) {
            mCurrentNewLatLon = 1;
        }

        mPreviousPosition = mCurrentPosition;
        mPreviousLocation = newLocation;
    }

    private void calculateNewDetails(Location newLocation) {
        mDuration = mStopWatch.getTotalMilliSecs();
        int durationSec = (int) (mDuration / 1000);
        double durationMin = (double) durationSec / 60.0;
        double durationHour = durationMin / 60.0;

        if (mPreviousLocation == null) {
            return;
        }

        float distanceBetweenTwoLocations = distanceBetweenTwoLastLocations(mPreviousLocation, newLocation);
        mTotalDistance += distanceBetweenTwoLocations;
        double totalDistanceKm = mTotalDistance / 1000.0;


        NewLocationLog.d("calculateNewDetails: mDuration: " + mDuration);
        NewLocationLog.d("calculateNewDetails: durationSec: " + durationSec);
        NewLocationLog.d("calculateNewDetails: durationMin: " + durationMin);
        NewLocationLog.d("calculateNewDetails: durationHour: " + durationHour);
        NewLocationLog.d("calculateNewDetails: mPreviousLocation: " + mPreviousLocation);
        NewLocationLog.d("calculateNewDetails: distanceBetweenTwoLocations: " + distanceBetweenTwoLocations);
        NewLocationLog.d("calculateNewDetails: mTotalDistance: " + mTotalDistance);
        NewLocationLog.d("calculateNewDetails: totalDistanceKm: " + totalDistanceKm);

        checkForLap(newLocation);

        if (mTotalDistance < MIN_VALUE_OF_TOTAL_DISTANCE_METERS) {
            NewLocationLog.d("2d", "mTotalDistance < 1.5 : " + mTotalDistance + " ZAWRACAM!");
            return;
        }

        mAvgPace = durationMin / totalDistanceKm;
        mAvgPaceString = Utils.paceToString(mAvgPace);

        // mAvgSpeed = totalDistanceKm / durationHour;
        mAvgSpeed = (double) Math.round((totalDistanceKm / durationHour) * 100) / 100;

        if (distanceBetweenTwoLocations < MIN_VALUE_OF_METERS_BETWEEN_LOCATIONS) {
            NewLocationLog.d("2d", "distanceBetweenTwoLocations < 1.5 : " + distanceBetweenTwoLocations + " ZAWRACAM!");
            return;
        }

        long msBetweenTwoLastPositions = timeBetweenTwoLastPositions();
        int secBetweenTwoPositions = (int) (msBetweenTwoLastPositions / 1000);
        double minBetweenTwoPositions = (double) secBetweenTwoPositions / 60.0;
        double hourBetweenTwoPositions = minBetweenTwoPositions / 60.0;
        double kmBetweenTwoPositions = distanceBetweenTwoLocations / 1000.0;

        mCurrentPace = minBetweenTwoPositions / kmBetweenTwoPositions;
        mCurrentPaceString =  Utils.paceToString(mCurrentPace);

        // TODO: Problem here is that I can get the maxPace = 0.0 ..
        // Now I am only taking in consideration paces after 13s from starting workout
        // It should omit 2 first location updates
        if (mCurrentPace < mMaxPace && durationSec > 13) {
            mMaxPace = mCurrentPace;
            mMaxPaceString =  Utils.paceToString(mMaxPace);
        }

        // mCurrentSpeed =  kmBetweenTwoPositions / hourBetweenTwoPositions;
        mCurrentSpeed = (double) Math.round((kmBetweenTwoPositions / hourBetweenTwoPositions) * 100) / 100;

        if (mCurrentSpeed > mMaxSpeed) {
            mMaxSpeed = mCurrentSpeed;
        }

        double secBetweenTwoPositions2 = (double) msBetweenTwoLastPositions / 1000.0;
        NewLocationLog.d("secBetweenTwoPositions2", "distanceBetweenTwoLocations: " + distanceBetweenTwoLocations);
        NewLocationLog.d("secBetweenTwoPositions2", "msBetweenTwoLastPositions: " + msBetweenTwoLastPositions);
        NewLocationLog.d("secBetweenTwoPositions2", "secBetweenTwoPositions2: " + secBetweenTwoPositions2);

        NewLocationLog.d("calculateNewDetails: msBetweenTwoLastPositions: " + msBetweenTwoLastPositions);
        NewLocationLog.d("calculateNewDetails: secBetweenTwoPositions: " + secBetweenTwoPositions);
        NewLocationLog.d("calculateNewDetails: minBetweenTwoPositions: " + minBetweenTwoPositions);
        NewLocationLog.d("calculateNewDetails: hourBetweenTwoPositions: " + hourBetweenTwoPositions);
        NewLocationLog.d("calculateNewDetails: kmBetweenTwoPositions: " + kmBetweenTwoPositions);

        NewLocationLog.d("calculateNewDetails: mAvgPace: " + mAvgPace);
        NewLocationLog.d("calculateNewDetails: mAvgPaceString: " + mAvgPaceString);
        NewLocationLog.d("calculateNewDetails: mCurrentPace: " + mCurrentPace);
        NewLocationLog.d("calculateNewDetails: mCurrentPaceString: " + mCurrentPaceString);
        NewLocationLog.d("calculateNewDetails: mMaxPace: " + mMaxPace);
        NewLocationLog.d("calculateNewDetails: mMaxPaceString: " + mMaxPaceString);

        NewLocationLog.d("calculateNewDetails: mAvgSpeed: " + mAvgSpeed);
        NewLocationLog.d("calculateNewDetails: mCurrentSpeed: " + mCurrentSpeed);
        NewLocationLog.d("calculateNewDetails: mMaxSpeed: " + mMaxSpeed);
    }

    private void addLatLngToPath(LatLon newLatLng) {
        mPath.add(newLatLng);
    }

    private float distanceBetweenTwoLastLocations(Location previous, Location newLocation) {
        return previous.distanceTo(newLocation);
    }

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
        NewLocationLog.d("checkForLap: km=" + km);

        if (mCurrentLap == km) {
            NewLocationLog.d("checkForLap: new LAP! at: " + mTotalDistance);
            LatLon lapLatLon = new LatLon(
                    newLocation.getLatitude(),
                    newLocation.getLongitude());

            Position lapPosition = new Position(lapLatLon, mDuration);

            Lap newLap = null;
            if (mLaps.size() == 0) {
                newLap = new Lap(lapPosition, mDuration);
            } else {
                int lastLapIdx = mLaps.size() - 1;
                long differenceBetweenLaps = mDuration - mLaps.get(lastLapIdx).getPositionTimeStamp();
                newLap = new Lap(lapPosition, differenceBetweenLaps);
            }
            mLaps.add(newLap);
            mCurrentLap++;

            for (Lap lap : mLaps) {
                NewLocationLog.d("checkForLap: lap=" + lap);
            }
        }
    }

    private long timeBetweenTwoLastPositions() {
        if (mPreviousPosition == null) {
            return 0;
        }
        long previousPositionTimeStamp = mPreviousPosition.getTimeStamp();
        long currentPositionTimeStamp = mCurrentPosition.getTimeStamp();
        return currentPositionTimeStamp - previousPositionTimeStamp;
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

    public double getTotalDistance() {
        return mTotalDistance;
    }

    public String getTotalDistanceString() {
        return Utils.distanceMetersToKm(getTotalDistance());
    }

    public long getDuration() {
        return mDuration;
    }

    public String getDurationString() {
        return mStopWatch.getDurationString();
    }

    public double getAvgPace() {
        return mAvgPace;
    }

    public String getAvgPaceString() {
        return mAvgPaceString;
    }

    public double getCurrentPace() {
        return mCurrentPace;
    }

    public String getCurrentPaceString() {
        return mCurrentPaceString;
    }

    public double getMaxPace() {
        if (mMaxPace == 1000.0) {
            return 0.0;
        }
        return mMaxPace;
    }

    public String getMaxPaceString() {
        return mMaxPaceString;
    }

    public double getAvgSpeed() {
        return mAvgSpeed;
    }

    public String getAvgSpeedString() {
        return String.valueOf(mAvgSpeed);
    }

    public double getCurrentSpeed() {
        return mCurrentSpeed;
    }

    public double getMaxSpeed() {
        return mMaxSpeed;
    }

    public String getMaxSpeedString() {
        return String.valueOf(mMaxSpeed);
    }

    public ArrayList<LatLon> getPath() {
        return mPath;
    }

    public ArrayList<Lap> getLaps() {
        return mLaps;
    }

    @Override
    public String toString() {
        return "CurrentGpsWorkout{" +
                "mDate=" + mDate +
                ", mWorkoutName='" + mWorkoutName + '\'' +
                ", mDuration=" + mDuration +
                ", mTotalDistance=" + mTotalDistance +
                ", mAvgPace=" + mAvgPace +
                ", mAvgPaceString='" + mAvgPaceString + '\'' +
                ", mCurrentPace=" + mCurrentPace +
                ", mCurrentPaceString='" + mCurrentPaceString + '\'' +
                ", mMaxPace=" + mMaxPace +
                ", mMaxPaceString='" + mMaxPaceString + '\'' +
                ", mAvgSpeed=" + mAvgSpeed +
                ", mCurrentSpeed=" + mCurrentSpeed +
                ", mMaxSpeed=" + mMaxSpeed +
                ", mPath=" + mPath +
                ", mLaps=" + mLaps +
                ", mStopWatch=" + mStopWatch +
                ", mPreviousLocation=" + mPreviousLocation +
                ", mCurrentPosition=" + mCurrentPosition +
                ", mPreviousPosition=" + mPreviousPosition +
                ", mCurrentLap=" + mCurrentLap +
                ", mCurrentNewLatLon=" + mCurrentNewLatLon +
                '}';
    }
}