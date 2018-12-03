package com.example.kornel.alphaui.gpsworkout;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.kornel.alphaui.utils.Lap;
import com.example.kornel.alphaui.utils.LatLon;
import com.example.kornel.alphaui.utils.NotificationUtils;
import com.example.kornel.alphaui.utils.OnNewActivityState;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.kornel.alphaui.gpsworkout.MapsFragment.ACTION_LAST_LOCATION;
import static com.example.kornel.alphaui.gpsworkout.MapsFragment.ACTION_LOCATION_CHANGED;
import static com.example.kornel.alphaui.gpsworkout.MapsFragment.LocationBroadcastReceiver.LAST_LOCATION_EXTRA_BROADCAST_INTENT;
import static com.example.kornel.alphaui.gpsworkout.MapsFragment.LocationBroadcastReceiver.LOCATION_EXTRA_BROADCAST_INTENT;
import static com.example.kornel.alphaui.mainactivity.WorkoutFragment.WORKOUT_NAME_EXTRA_INTENT;
import static com.example.kornel.alphaui.utils.NotificationUtils.ACTION_PAUSE_WORKOUT;
import static com.example.kornel.alphaui.utils.NotificationUtils.ACTION_RESUME_WORKOUT;
import static com.example.kornel.alphaui.utils.NotificationUtils.MOON_RUNNER_WORKOUT_NOTIFICATION_ID;
import static com.example.kornel.alphaui.utils.ServiceUtils.ACTION_START_FOREGROUND_SERVICE;
import static com.example.kornel.alphaui.utils.ServiceUtils.ACTION_STOP_FOREGROUND_SERVICE;

public class LocationTrackingService extends Service {
    private static final String TAG = "LocationTrackingService";

    private static final int MIN_VALUE_OF_MILLI_SEC_BETWEEN_UPDATES = 4000;

    // The desired interval for location updates. Inexact. Updates may be more or less frequent.
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // The fastest rate for active location updates.
    // Updates will never be more frequent than this value.
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // The priority of the request, which gives the Google Play services location services
    // a strong hint about which location sources to use.
    private static final int LOCATION_UPDATES_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;

    // Provides access to the Fused Location Provider API.
    private FusedLocationProviderClient mFusedLocationClient;

    // Contains parameters used by FusedLocationProviderApi.
    private LocationRequest mLocationRequest;

    // Callback for changes in location.
    private LocationCallback mLocationCallback;

    // Binder given to clients
    private final IBinder mBinder = new LocationTrackingBinder();

    // Framework location APIs - Not recommended
    // private LocationManager mLocationManager;
    // private LocationListener mLocationListener;

    private Handler mNotificationHandler;
    private Runnable mNotificationRunnable;

    // For button state update
    private OnNewActivityState mButtonCallback;

    private CurrentGpsWorkout mCurrentGpsWorkout;

    private boolean mIsTrainingPaused;
    private boolean mDidComeFromNotification;
    private boolean mIsServiceRunning;

    public LocationTrackingService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        mIsTrainingPaused = true;
        mDidComeFromNotification = false;
        mIsServiceRunning = false;

        // Framework location APIs - Not recommended
        // mLocationManager = (LocationManager)
        //         getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        setupGoogleLocationServicesApi();
        setupNotifications();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        switch (action) {
            case ACTION_START_FOREGROUND_SERVICE:
                String workoutName = intent.getStringExtra(WORKOUT_NAME_EXTRA_INTENT);
                startForegroundService(workoutName);
                Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_SHORT).show();
                break;

            case ACTION_STOP_FOREGROUND_SERVICE:
                stopForegroundService();
                Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_SHORT).show();
                break;

            case ACTION_RESUME_WORKOUT:
                mDidComeFromNotification = true;
                resumeWorkout();
                break;

            case ACTION_PAUSE_WORKOUT:
                mDidComeFromNotification = true;
                pauseWorkout();
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForegroundService(String workoutName) {
        // Create new workout
        mCurrentGpsWorkout = new CurrentGpsWorkout(workoutName);

        // Build the notification.
        Notification notification = NotificationUtils.createNotification(getApplicationContext(), LocationTrackingService.class);

        // Start foreground service.
        startForeground(MOON_RUNNER_WORKOUT_NOTIFICATION_ID, notification);

        mIsServiceRunning = true;
        mIsTrainingPaused = false;

        // Google Location Services API
        getLastLocation();
        startLocationUpdates();
        startNotificationHandler();
    }

    private void stopForegroundService() {
        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();

        mIsServiceRunning = false;

        // Google Location Services API
        stopLocationUpdates();
    }

    public void pauseWorkout() {
        mIsTrainingPaused = true;
        mCurrentGpsWorkout.pauseStopwatch();
        stopNotificationHandler();
        NotificationUtils.toggleActionButtons(getApplicationContext());
        stopLocationUpdates();

        if (mDidComeFromNotification) {
            if (mButtonCallback != null) {
                mButtonCallback.updateButtons(true);
            }
            mDidComeFromNotification = false;
        }
    }

    public void resumeWorkout() {
        mIsTrainingPaused = false;
        mCurrentGpsWorkout.startStopwatch();
        startNotificationHandler();
        NotificationUtils.toggleActionButtons(getApplicationContext());
        startLocationUpdates();

        if (mDidComeFromNotification) {
            if (mButtonCallback != null) {
                mButtonCallback.updateButtons(false);
            }
            mDidComeFromNotification = false;
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////

    private static final int DISTANCE_MIN_DIFF_METERS = 15;
    private static final int DISTANCE_MAX_DIFF_METERS = 20;

    private Location mPreviousLocation = null;

    private void onNewLocation(Location newLocation) {
        NewLocationLog.d("==================================================================");
        NewLocationLog.d("onNewLocation: newLocation: " + newLocation.toString());


        if (mPreviousLocation != null) {
            long prevStamp = mPreviousLocation.getTime();
            long newStamp = newLocation.getTime();
            long msBetweenPrevAndNew = newStamp - prevStamp;
            if (msBetweenPrevAndNew < MIN_VALUE_OF_MILLI_SEC_BETWEEN_UPDATES) {
                NewLocationLog.d("onNewLocation: OMITTING because timeBetweenUpdates = : " + msBetweenPrevAndNew);
                return;
            }
        }

        mCurrentGpsWorkout.onNewLocation(newLocation);

        LatLon newLatLon = new LatLon(
                newLocation.getLatitude(),
                newLocation.getLongitude());

        Intent intent = new Intent(ACTION_LOCATION_CHANGED);
        intent.putExtra(LOCATION_EXTRA_BROADCAST_INTENT, newLatLon);
        sendBroadcast(intent);

        mPreviousLocation = newLocation;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////


    private void setupGoogleLocationServicesApi() {
        // Google Location Services API
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        createLocationRequest();

        // Google Location Services API
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                onNewLocation(locationResult.getLastLocation());
            }
        };

        // Google Location Services API
        Log.d(TAG, "onCreate: " + (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS));
    }

    // Google Location Services API
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LOCATION_UPDATES_PRIORITY);
    }

    // Google Location Services API
    private void startLocationUpdates() {
        try {
            //getLastLocation();

            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        } catch (SecurityException unlikely) {
            Log.e(TAG, "startLocationUpdates: ", unlikely);
        }
    }

    // Google Location Services API
    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void setupNotifications() {
        mNotificationHandler = new Handler();
        mNotificationRunnable = new Runnable() {
            @Override
            public void run() {
                String ch187 = Character.toString((char) 187);

                String message = mCurrentGpsWorkout.getWorkoutName() + "  "
                        + ch187 + "  " + mCurrentGpsWorkout.getDurationString() + "  "
                        + ch187 + "  " + getDistanceString() + "km";

                NotificationUtils.updateNotification(message);
                mNotificationHandler.postDelayed(this, 500);
            }
        };
    }

    private void startNotificationHandler() {
        mNotificationHandler.postDelayed(mNotificationRunnable, 0);
    }

    private void stopNotificationHandler() {
        mNotificationHandler.removeCallbacks(mNotificationRunnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        return mBinder;
    }

    // Class used for the client Binder. Because we know this service always runs
    // in the same process as its clients, we don't need to deal with IPC.
    public class LocationTrackingBinder extends Binder {
        LocationTrackingService getService() {
            // Return this instance of LocationTrackingBinder so clients can call public methods
            return LocationTrackingService.this;
        }
    }

    /**
     * Methods for clients.
     */

    // Google Location Services API
    public void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location == null) {
                        return;
                    }
                    Intent intent = new Intent(ACTION_LAST_LOCATION);
                    intent.putExtra(LAST_LOCATION_EXTRA_BROADCAST_INTENT, location);
                    sendBroadcast(intent);
                }
            });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "getLastLocation: ", unlikely);
        }
    }

    public void setCallback(OnNewActivityState callback) {
        mButtonCallback = callback;
    }

    public WorkoutSummary getWorkOutSummary() {
        WorkoutSummary workoutSummary = new WorkoutSummary(
                mCurrentGpsWorkout.getDate(),
                mCurrentGpsWorkout.getWorkoutName(),
                mCurrentGpsWorkout.getDurationString(),
                mCurrentGpsWorkout.getTotalDistanceString(),
                mCurrentGpsWorkout.getAvgPaceString(),
                mCurrentGpsWorkout.getMaxPaceString(),
                mCurrentGpsWorkout.getAvgSpeedString(),
                mCurrentGpsWorkout.getMaxSpeedString(),
                mCurrentGpsWorkout.getPath(),
                mCurrentGpsWorkout.getLaps());
        return workoutSummary;
    }

    public List<LatLon> getPath() {
        return mCurrentGpsWorkout.getPath();
    }

    public String getTimeString() {
        if (mCurrentGpsWorkout != null) {
            return mCurrentGpsWorkout.getDurationString();
        } else {
            return "0:00";
        }
    }

    public String getDistanceString() {
        if (mCurrentGpsWorkout != null) {
            return mCurrentGpsWorkout.getTotalDistanceString();
        } else {
            return "0.00";
        }
    }

    public String getAvgPace() {
        if (mCurrentGpsWorkout != null) {
            return mCurrentGpsWorkout.getAvgPaceString();
        } else {
            return "0:00";
        }
    }

    public String getCurrentPace() {
        if (mCurrentGpsWorkout != null) {
            return mCurrentGpsWorkout.getCurrentPaceString();
        } else {
            return "0:00";
        }
    }

    public ArrayList<Lap> getLaps() {
        if (mCurrentGpsWorkout != null) {
            return mCurrentGpsWorkout.getLaps();
        } else {
            return new ArrayList<>();
        }
    }

    public boolean isTrainingPaused() {
        return mIsTrainingPaused;
    }

    public boolean isServiceRunning() {
        return mIsServiceRunning;
    }

    // Framework location APIs - Not recommended
    // public Location getLastLocation() {
    //     try {
    //         return mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    //     } catch (SecurityException unlikely) {
    //         return mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    //     }
    // }

    // Framework location APIs - Not recommended
    // public void setUpFrameworkLocationApis() {
    //     Framework location APIs - Not recommended
    //     mLocationListener = new LocationListener() {
    //         @Override
    //         public void onLocationChanged(Location location) {
    //             Log.d(TAG, "onLocationChanged: ");
    //             // onNewLocation(location);
    //             onNewLocation(location);
    //         }
    //
    //         @Override
    //         public void onStatusChanged(String provider, int status, Bundle extras) {}
    //         @Override
    //         public void onProviderEnabled(String provider) {}
    //         @Override
    //         public void onProviderDisabled(String provider) {}
    //     };
    //
    //     Framework location APIs - Not recommended
    //     try {
    //         mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1500, 0, mLocationListener);
    //     } catch (SecurityException unlikely) {
    //         Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
    //     }
    // }

    // @Override
    // public void onDestroy() {
    //     super.onDestroy();
    //     Log.d(TAG, "onDestroy: ");
    //     Framework location APIs - Not recommended
    //     if (mLocationManager != null && mLocationListener != null) {
    //         Log.d(TAG, "onDestroy: ");
    //         mLocationManager.removeUpdates(mLocationListener);
    //     }
    // }
}
