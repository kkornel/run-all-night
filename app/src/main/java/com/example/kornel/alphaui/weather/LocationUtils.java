package com.example.kornel.alphaui.weather;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.example.kornel.alphaui.mainactivity.WorkoutLog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationUtils {
    public enum LocationErrorType {
        FIND_LOCATION_NOT_PERMITTED,
        LOCATION_SERVICE_IS_NOT_AVAILABLE,
        Unknown,
    }

    private static final int LOCATION_REQUEST_INTERVAL_MILLI_SECONDS = 10000;
    private static final int LOCATION_REQUEST_FASTEST_INTERVAL_MILLI_SECONDS =
            LOCATION_REQUEST_INTERVAL_MILLI_SECONDS / 2;
    private static int LOCATION_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY;

    private LocationManager mLocationManager;

    private FusedLocationProviderClient mFusedLocationClient;
    private FusedLocationProviderClient mFusedLocationClient2;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private LocationCallback mLocationCallback2;

    private MyLocationResult mLocationResult;
    private MyLocationResult mLocationResult2;
    private boolean mGpsEnabled = false;
    private LocationErrorType mErrorType = null;

    public static Location lastKnowLocation = null;

    public interface MyLocationResult {
        void gotLocation(Location location, LocationErrorType errorType);
    }

    public void findUserLocation(Activity activity, Context context, final MyLocationResult result) {
        mLocationResult = result;

        if (mFusedLocationClient == null || mLocationManager == null) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            createLocationRequest();
        }

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                lastKnowLocation = locationResult.getLastLocation();
                mLocationResult.gotLocation(locationResult.getLastLocation(), null);
                stopLocationUpdates();
            }
        };

        try {
            mGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            mErrorType = LocationErrorType.LOCATION_SERVICE_IS_NOT_AVAILABLE;
        }

        if (!mGpsEnabled) {
            mErrorType = LocationErrorType.LOCATION_SERVICE_IS_NOT_AVAILABLE;
            WeatherLog.d(mErrorType.name() + mErrorType.toString());
            result.gotLocation(null, mErrorType);
            return;
        }

        if (mGpsEnabled) {
            try {
                startLocationUpdates();
            } catch (SecurityException e) {
                mErrorType = LocationErrorType.FIND_LOCATION_NOT_PERMITTED;
            }
        }

        if (mErrorType != null) {
            mLocationResult.gotLocation(null, mErrorType);
        } else {
            getLastLocation(activity);
        }
    }

    private void getLastLocation(Activity activity) {
        try {
            if (mGpsEnabled) {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    lastKnowLocation = location;
                                    WeatherLog.d(location.toString());
                                    mLocationResult.gotLocation(location, null);
                                } else {
                                    mLocationResult.gotLocation(null, null);
                                }
                            }
                        });
            }
        } catch (SecurityException ex) {
            WeatherLog.e(ex.getMessage());
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_REQUEST_INTERVAL_MILLI_SECONDS);
        mLocationRequest.setFastestInterval(LOCATION_REQUEST_FASTEST_INTERVAL_MILLI_SECONDS);
        mLocationRequest.setPriority(LOCATION_ACCURACY);
    }

    private void startLocationUpdates() {
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        } catch (SecurityException se) {
            WeatherLog.e(se.getMessage());
        }
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    public static boolean isGpsEnabled(final Context context) {
        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }
}
