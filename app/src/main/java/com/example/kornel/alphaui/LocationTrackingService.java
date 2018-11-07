package com.example.kornel.alphaui;


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
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static com.example.kornel.alphaui.MapsFragment.ACTION_LOCATION_CHANGED;
import static com.example.kornel.alphaui.utils.NotificationUtils.ACTION_PAUSE_SPORT_ACTIVITY;
import static com.example.kornel.alphaui.utils.NotificationUtils.ACTION_RESUME_SPORT_ACTIVITY;
import static com.example.kornel.alphaui.utils.NotificationUtils.LOCATION_TRACKING_NOTIFICATION_ID;


public class LocationTrackingService extends Service {
    private static final String TAG = "LocationTrackingService";

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";

    // Binder given to clients
    private final IBinder mBinder = new LocationTrackingBinder();

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private Stopwatch mStopwatch;
    private Handler mNotificationHandler;
    private Runnable mNotificationRunnable;
    private ServiceCallbacks mServiceCallbacks;

    private List<LatLng> mCoordinates;
    private List<LatLng> mTestCoordinates;

    private boolean mIsTrainingPaused;
    private boolean mCameFromNotification;
    private boolean mIsServiceRunning;

    public interface ServiceCallbacks {
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

        mStopwatch = new Stopwatch();
        mCoordinates = new ArrayList<>();

        mNotificationHandler = new Handler();
        mNotificationRunnable = new Runnable() {
            @Override
            public void run() {
                // TODO replace with activity name
                String ch183 =  Character.toString ((char) 183);
                String ch187 =  Character.toString ((char) 187);

                // String message = "Run " + ch183 + " 2:57 "  + ch183 + " 3.54km";
                String message = "Run " + ch187 + "  " + mStopwatch.getTimeString() + "  "  + ch187 + "  3.54km";

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

                startForegroundService();

                Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_LONG).show();
                break;

            case ACTION_STOP_FOREGROUND_SERVICE:
                Log.d(TAG, "onStartCommand: Stopping service.");

                stopForegroundService();

                Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
                break;

            case ACTION_RESUME_SPORT_ACTIVITY:
                Log.d(TAG, "onStartCommand: You clicked RESUME button.");

                mCameFromNotification = true;
                resumeSportActivity();
                break;

            case ACTION_PAUSE_SPORT_ACTIVITY:
                Log.d(TAG, "onStartCommand: You click PAUSE button.");

                mCameFromNotification = true;
                pauseSportActivity();
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        if(mLocationManager != null && mLocationListener != null) {
            Log.d(TAG, "onDestroy: ");
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    private void startForegroundService() {
        Log.d(TAG, "startForegroundService: ");

        mStopwatch.startStopwatch();

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged: ");

                onNewLocation(location);
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

    public void resumeSportActivity() {
        mIsTrainingPaused = false;
        mStopwatch.startStopwatch();
        startNotificationHandler();
        NotificationUtils.toggleActionButtons(getApplicationContext());

        if (mCameFromNotification) {
            if (mServiceCallbacks != null) {
                mServiceCallbacks.updateButtons(false);
            }
            mCameFromNotification = false;
        }
    }

    public void pauseSportActivity() {
        mIsTrainingPaused = true;
        mStopwatch.pauseStopwatch();
        stopNotificationHandler();
        NotificationUtils.toggleActionButtons(getApplicationContext());

        if (mCameFromNotification) {
            if (mServiceCallbacks != null) {
                mServiceCallbacks.updateButtons(true);
            }
            mCameFromNotification = false;
        }
    }

    private void onNewLocation(Location location) {
        // TODO calculate everything
        Log.d(TAG, "onNewLocation: " + location);

        Intent intent = new Intent(ACTION_LOCATION_CHANGED);
        intent.putExtra("lat", location.getLatitude());
        intent.putExtra("lng", location.getLongitude());
        sendBroadcast(intent);
        mCoordinates.add(new LatLng(location.getLatitude(), location.getLongitude()));
        mStopwatch.makeLap();
    }

    private void startNotificationHandler() {
        mNotificationHandler.postDelayed(mNotificationRunnable, 0);
    }

    private void stopNotificationHandler() {
        mNotificationHandler.removeCallbacks(mNotificationRunnable);
    }


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocationTrackingBinder extends Binder {
        private static final String TAG = "LocationTrackingBinder";
        LocationTrackingService getService() {
            Log.d(TAG, "getService: ");
            // Return this instance of LocationTrackingBinder so clients can call public methods
            return LocationTrackingService.this;
        }
    }

    public void setServiceCallbacks(ServiceCallbacks serviceCallbacks) {
        mServiceCallbacks = serviceCallbacks;
    }

    /** method for clients */
    public List<LatLng> getLatLngArray() {
        Log.d(TAG, "getLatLngArray: ");
        return mCoordinates;
    }

    public boolean isTrainingPaused() {
        return mIsTrainingPaused;
    }

    public boolean isServiceRunning() {
        return mIsServiceRunning;
    }

    public void createTest() {
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
