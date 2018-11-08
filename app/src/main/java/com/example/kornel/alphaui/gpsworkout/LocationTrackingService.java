package com.example.kornel.alphaui.gpsworkout;


import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.kornel.alphaui.utils.NotificationUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.kornel.alphaui.gpsworkout.MapsFragment.ACTION_LOCATION_CHANGED;
import static com.example.kornel.alphaui.gpsworkout.MapsFragment.LocationBroadcastReceiver.LOCATION_EXTRA_BROADCAST_INTENT;
import static com.example.kornel.alphaui.mainactivity.WorkoutFragment.WORKOUT_NAME_EXTRA_INTENT;
import static com.example.kornel.alphaui.utils.NotificationUtils.ACTION_PAUSE_WORKOUT;
import static com.example.kornel.alphaui.utils.NotificationUtils.ACTION_RESUME_WORKOUT;
import static com.example.kornel.alphaui.utils.NotificationUtils.LOCATION_TRACKING_NOTIFICATION_ID;


public class LocationTrackingService extends Service {
    private static final String TAG = "LocationTrackingService";

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";

    // The desired interval for location updates. Inexact. Updates may be more or less frequent.
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // The fastest rate for active location updates. Updates will never be more frequent than this value.
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Provides access to the Fused Location Provider API.
    private FusedLocationProviderClient mFusedLocationClient;

    // Contains parameters used by {@link com.google.android.gms.location.FusedLocationProviderApi}.
    private LocationRequest mLocationRequest;

    // Callback for changes in location.
    private LocationCallback mLocationCallback;

    // Binder given to clients
    private final IBinder mBinder = new LocationTrackingBinder();

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private Handler mNotificationHandler;
    private Runnable mNotificationRunnable;

    // For button state update
    private OnNewActivityState mButtonCallback;

    private CurrentGpsWorkout mCurrentGpsWorkout;

    private List<LatLng> mCoordinates;

    private boolean mIsTrainingPaused;
    private boolean mCameFromNotification;
    private boolean mIsServiceRunning;

    // ////////////////////////////////////////////////////////////////////////////////////////////X

    private List<LatLng> mTestCoordinates;
    private List<LatLng> mTestCoordinates2;

    private int i = -1;

    // ////////////////////////////////////////////////////////////////////////////////////////////

    public interface OnNewActivityState {
        void updateButtons(boolean isPaused);
    }

    public LocationTrackingService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Log.d(TAG, "onBind: ");
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");

        createTest();

        mIsTrainingPaused = true;
        mCameFromNotification = false;
        mIsServiceRunning = false;

        mLocationManager = (LocationManager)
                getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        mCoordinates = new ArrayList<>();

        mNotificationHandler = new Handler();
        mNotificationRunnable = new Runnable() {
            @Override
            public void run() {
                // TODO replace with activity name
                String ch183 = Character.toString((char) 183);
                String ch187 = Character.toString((char) 187);

                // String message = "Run " + ch183 + " 2:57 "  + ch183 + " 3.54km";
                // String message = "Run " + ch187 + "  " + mCurrentGpsWorkout.getTime() + "  " + ch187 + "  " + getDistanceString() + "km";
                String message = mCurrentGpsWorkout.getWorkoutName() + "  " + ch187 + "  " + mCurrentGpsWorkout.getTimeString() + "  " + ch187 + "  " + getDistanceString() + "km";

                NotificationUtils.updateNotification(message);
                mNotificationHandler.postDelayed(this, 500);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        Log.d(TAG, "onStartCommand: " + action);

        switch (action) {
            case ACTION_START_FOREGROUND_SERVICE:
                Log.d(TAG, "onStartCommand: Starting service.");

                String workoutName = intent.getStringExtra(WORKOUT_NAME_EXTRA_INTENT);
                startForegroundService(workoutName);
                Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_LONG).show();
                break;

            case ACTION_STOP_FOREGROUND_SERVICE:
                Log.d(TAG, "onStartCommand: Stopping service.");

                stopForegroundService();
                Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
                break;

            case ACTION_RESUME_WORKOUT:
                Log.d(TAG, "onStartCommand: You clicked RESUME button.");

                mCameFromNotification = true;
                resumeWorkout();
                break;

            case ACTION_PAUSE_WORKOUT:
                Log.d(TAG, "onStartCommand: You click PAUSE button.");

                mCameFromNotification = true;
                pauseWorkout();
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        if (mLocationManager != null && mLocationListener != null) {
            Log.d(TAG, "onDestroy: ");
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    private void startForegroundService(String workoutName) {
        Log.d(TAG, "startForegroundService: ");

        mCurrentGpsWorkout = new CurrentGpsWorkout(workoutName);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged: ");
                // onNewLocation(location);
                onNewLocation_X(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {}
        };

        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1500, 0, mLocationListener);
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }

        // Build the notification.
        Notification notification = NotificationUtils.createNotification(getApplicationContext());

        // Start foreground service.
        startForeground(LOCATION_TRACKING_NOTIFICATION_ID, notification);

        mIsServiceRunning = true;

        startNotificationHandler();
    }

    private void stopForegroundService() {
        Log.d(TAG, "stopForegroundService: ");

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();

        mIsServiceRunning = false;
    }

    public void pauseWorkout() {
        Log.d(TAG, "pauseWorkout: ");
        mIsTrainingPaused = true;
        mCurrentGpsWorkout.pauseStopwatch();
        stopNotificationHandler();
        NotificationUtils.toggleActionButtons(getApplicationContext());

        if (mCameFromNotification) {
            if (mButtonCallback != null) {
                mButtonCallback.updateButtons(true);
            }
            mCameFromNotification = false;
        }
    }

    public void resumeWorkout() {
        Log.d(TAG, "resumeWorkout: ");
        mIsTrainingPaused = false;
        mCurrentGpsWorkout.startStopwatch();
        startNotificationHandler();
        NotificationUtils.toggleActionButtons(getApplicationContext());

        if (mCameFromNotification) {
            if (mButtonCallback != null) {
                mButtonCallback.updateButtons(false);
            }
            mCameFromNotification = false;
        }
    }

    public WorkoutGpsSummary getWorkOutSummary() {

        // Type of current activity
        String mWorkoutName = mCurrentGpsWorkout.getWorkoutName();

        // Array of all LatLng on path
        ArrayList<LatLng> mPath = mCurrentGpsWorkout.getTestLatLng();

        // Total distance since started tracking in meters
        double mDistance = mCurrentGpsWorkout.getDistance();

        // Total time since started tracking in milliseconds
        String mDuration = mCurrentGpsWorkout.getTimeString();

        return new WorkoutGpsSummary(mWorkoutName, mDuration, mDistance, mPath);
    }

    private int mLocationUpdateNumber = 1;
    private Location mPreviousLocation;

    private void onNewLocation(Location newLocation) {
        // I have a new Location
        // Create LatLng based on new Location
        LatLng newLatLng = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());

        // Calculate new distance
        if (mPreviousLocation != null) {
            mCurrentGpsWorkout.calculateDistanceBetweenTwoLocations(mPreviousLocation, newLocation);
        }

        if (mLocationUpdateNumber == 1) {
            mCurrentGpsWorkout.addLatLngToPath(newLatLng);

            Intent intent = new Intent(ACTION_LOCATION_CHANGED);
            intent.putParcelableArrayListExtra(LOCATION_EXTRA_BROADCAST_INTENT, mCurrentGpsWorkout.getPath());
            sendBroadcast(intent);
        }

        mLocationUpdateNumber++;
        if (mLocationUpdateNumber >= 11) {
            mLocationUpdateNumber = 1;
        }
        mPreviousLocation = newLocation;
    }

    public void onNewLocation_X(Location newLocation) {
        // TODO calculate everything
        Log.d(TAG, "onNewLocation: " + newLocation);

        if (i == -1) {
            i++;
            return;
        }

        if (i >= mTestCoordinates.size())
            return;

        newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(mTestCoordinates.get(i).latitude);
        newLocation.setLongitude(mTestCoordinates.get(i).longitude);

        // I have a new Location
        // Create LatLng based on new Location
        LatLng newLatLng = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());

        // Move camera to new position
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));

        // Add new LatLng to the path
        mCurrentGpsWorkout.addLatLngToPath(newLatLng);

        // Calculate distance between two previous locations
        mCurrentGpsWorkout.calculateDistanceBetweenTwoLastLocations();

        if (i < 1) {
            // mPolylineOptions = new PolylineOptions()
            //         .add(newLatLng);
            // mPolyline = mMap.addPolyline(mPolylineOptions);
            mTestCoordinates2 = new ArrayList<>();
            mTestCoordinates2.add(newLatLng);
        } else {
            mTestCoordinates2.add(newLatLng);
            // mPolyline.setPoints(mTestCoordinates2);
        }

        i++;

        Intent intent = new Intent(ACTION_LOCATION_CHANGED);
        intent.putParcelableArrayListExtra(LOCATION_EXTRA_BROADCAST_INTENT, mCurrentGpsWorkout.getPath());
        sendBroadcast(intent);
    }

    private void startNotificationHandler() {
        mNotificationHandler.postDelayed(mNotificationRunnable, 0);
    }

    private void stopNotificationHandler() {
        mNotificationHandler.removeCallbacks(mNotificationRunnable);
    }

    // Class used for the client Binder. Because we know this service always runs
    // in the same process as its clients, we don't need to deal with IPC.
    public class LocationTrackingBinder extends Binder {
        private static final String TAG = "LocationTrackingBinder";

        LocationTrackingService getService() {
            Log.d(TAG, "getService: ");
            // Return this instance of LocationTrackingBinder so clients can call public methods
            return LocationTrackingService.this;
        }
    }

    public void setCallback(OnNewActivityState callback) {
        mButtonCallback = callback;
    }

    /**
     * Methods for clients.
     */

    public Location getLastLocation() {
        try {
            return mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException unlikely) {
            return mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
    }

    public List<LatLng> getPath() {
        return mCurrentGpsWorkout.getPath();
    }

    public String getTimeString() {
        if (mCurrentGpsWorkout != null) {
            return mCurrentGpsWorkout.getTimeString();
        } else {
            return "0:00";
        }
    }

    public String getDistanceString() {
        if (mCurrentGpsWorkout == null) {
            return "0.00";
        }
        double distance = mCurrentGpsWorkout.getDistance();
        distance /= 1000;
        // String.format("%.5g%n", distance);
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(distance);
    }

    public boolean isTrainingPaused() {
        return mIsTrainingPaused;
    }

    public boolean isServiceRunning() {
        return mIsServiceRunning;
    }

    private void createTest() {
        mTestCoordinates = new ArrayList<>();
        mTestCoordinates.add(new LatLng(52.416042, 16.939496));
        mTestCoordinates.add(new LatLng(52.415358, 16.939367));
        mTestCoordinates.add(new LatLng(52.414553, 16.939206));
        mTestCoordinates.add(new LatLng(52.413627, 16.939072));
        mTestCoordinates.add(new LatLng(52.412538, 16.938852));
        mTestCoordinates.add(new LatLng(52.411671, 16.938482));
        mTestCoordinates.add(new LatLng(52.410830, 16.938246));
        mTestCoordinates.add(new LatLng(52.409943, 16.938353));
        mTestCoordinates.add(new LatLng(52.409449, 16.938267));
        mTestCoordinates.add(new LatLng(52.409010, 16.938155));
        mTestCoordinates.add(new LatLng(52.408706, 16.938106));
        mTestCoordinates.add(new LatLng(52.408238, 16.938047));
        mTestCoordinates.add(new LatLng(52.407708, 16.937945));
        mTestCoordinates.add(new LatLng(52.407407, 16.937881));
        mTestCoordinates.add(new LatLng(52.407138, 16.937838));
        mTestCoordinates.add(new LatLng(52.406863, 16.937801));
        mTestCoordinates.add(new LatLng(52.406340, 16.937704));
        mTestCoordinates.add(new LatLng(52.405842, 16.937591));
        mTestCoordinates.add(new LatLng(52.405430, 16.937559));
        mTestCoordinates.add(new LatLng(52.405083, 16.937490));
        mTestCoordinates.add(new LatLng(52.404573, 16.937345));
        mTestCoordinates.add(new LatLng(52.403615, 16.936963));
        mTestCoordinates.add(new LatLng(52.403060, 16.936833));
        mTestCoordinates.add(new LatLng(52.402435, 16.936616));
        mTestCoordinates.add(new LatLng(52.401926, 16.936400));
        mTestCoordinates.add(new LatLng(52.401483, 16.936255));
        mTestCoordinates.add(new LatLng(52.401002, 16.936043));
        mTestCoordinates.add(new LatLng(52.400829, 16.936231));
        mTestCoordinates.add(new LatLng(52.400959, 16.936802));
        mTestCoordinates.add(new LatLng(52.401138, 16.937424));
        mTestCoordinates.add(new LatLng(52.401352, 16.937919));
        mTestCoordinates.add(new LatLng(52.401457, 16.938247));
        mTestCoordinates.add(new LatLng(52.401613, 16.938716));
    }
}
