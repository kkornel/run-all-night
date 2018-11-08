package com.example.kornel.alphaui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "MapsFragment";

    public static final String ACTION_LOCATION_CHANGED = "location_changed_intent_filter";

    private static final int ZOOM_VALUE = 17;

    private GoogleMap mMap;

    private OnMapUpdate mCallback;

    private FloatingActionButton mFab;

    private IntentFilter mLocationIntentFilter;
    private LocationBroadcastReceiver mLocationReceiver;


    private PolylineOptions mPolylineOptions;
    private Polyline mPolyline;

    interface OnMapUpdate {
        Location getLastLocation();
        List<LatLng> onMapUpdate();
        void onFabClicked();
    }
    public void setCallback(OnMapUpdate mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            // getActivity().registerReceiver(mLocationReceiver, mLocationIntentFilter);

        } else {
            if (getActivity() != null && mLocationReceiver != null ) {
                // getActivity().unregisterReceiver(mLocationReceiver);
            }
        }
    }


    public MapsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");

        View rootView = inflater.inflate(R.layout.maps_fragment, container, false);;

        SupportMapFragment map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        map.getMapAsync(this);

        mFab = rootView.findViewById(R.id.returnFab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onFabClicked();
            }
        });

        // mLocationIntentFilter = new IntentFilter();
        // mLocationIntentFilter.addAction(ACTION_LOCATION_CHANGED);
        // mLocationReceiver = new LocationBroadcastReceiver(new Handler());
        // getActivity().registerReceiver(mLocationReceiver, mLocationIntentFilter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        // getActivity().registerReceiver(mLocationReceiver, mLocationIntentFilter);
        mLocationIntentFilter = new IntentFilter();
        mLocationIntentFilter.addAction(ACTION_LOCATION_CHANGED);
        mLocationReceiver = new LocationBroadcastReceiver(new Handler());
        // getActivity().registerReceiver(mLocationReceiver, mLocationIntentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        getActivity().registerReceiver(mLocationReceiver, mLocationIntentFilter);
        Log.d(TAG, "onResume: " + mLocationReceiver);
        // Google Location Services API
        // Log.d(TAG, "onCreate: " + (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        getActivity().unregisterReceiver(mLocationReceiver);
        Log.d(TAG, "onPause: " + mLocationReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        // if (mLocationManager != null && mLocationListener != null) {
        //     Log.d(TAG, "onDestroy: ");
        //     mLocationManager.removeUpdates(mLocationListener);
        // }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    private void centerMapOnTheLocationZoom(Location location, int zoom) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if (mCallback != null) {
                    Location lastKnowLocation = mCallback.getLastLocation();
                    if (lastKnowLocation != null) {
                        centerMapOnTheLocationZoom(mCallback.getLastLocation(), ZOOM_VALUE);
                    }
                }
            }
        });

        Log.d(TAG, "onMapReady: ");

        // Google Location Services API
        // createLocationRequest();
        // startLocationUpdates();

        // Not Google
        // mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);
    }

    // TODO: Przesyłać całą tablice? Czy tylko pojedyncze LatLng?
    public class LocationBroadcastReceiver extends BroadcastReceiver {
        private static final String TAG = "LocationBroadcastReceiv";

        public static final String LOCATION_EXTRA_BROADCAST_INTENT = "ArrayPath";

        private final Handler handler;

        public LocationBroadcastReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();

            Log.d(TAG, "onReceive: " + action);

            final ArrayList<LatLng> path = intent.getParcelableArrayListExtra(LOCATION_EXTRA_BROADCAST_INTENT);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mPolylineOptions == null) {
                        mPolylineOptions = new PolylineOptions();
                        // .add(newLatLng);
                        mPolyline = mMap.addPolyline(mPolylineOptions);
                    } else {
                        mPolyline.setPoints(path);
                    }
                    LatLng newLatLng = path.get(path.size()-1);
                    // mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
                    Location newLocation = new Location(LocationManager.GPS_PROVIDER);
                    newLocation.setLatitude(newLatLng.latitude);
                    newLocation.setLongitude(newLatLng.longitude);
                    centerMapOnTheLocationZoom(newLocation, ZOOM_VALUE);
                }
            });
        }
    }

    // Google Location Services API
    // private void startLocationUpdates() {
    //     try {
    //         mFusedLocationClient.requestLocationUpdates(mLocationRequest,
    //                 mLocationCallback,
    //                 null /* Looper */);
    //     } catch (SecurityException e) {
    //         e.printStackTrace();
    //     }
    // }
    //
    // private void stopLocationUpdates() {
    //     mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    // }
    //
    //
    // private void createLocationRequest() {
    //     Log.d(TAG, "createLocationRequest: ");
    //     mLocationRequest = new LocationRequest();
    //     mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
    //     mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
    //     mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    // }
    //
    // private void getLastLocationGoogle() {
    //     try {
    //         mFusedLocationClient.getLastLocation()
    //                 .addOnSuccessListener(this, new OnSuccessListener<Location>() {
    //                     @Override
    //                     public void onSuccess(Location location) {
    //                         Log.d(TAG, "getLastLocation: ");
    //                         // Got last known location. In some rare situations this can be null.
    //                         if (location != null) {
    //                             // Logic to handle location object
    //                             Log.d(TAG, "getLastLocation: " + location);
    //                             centerMapOnTheLocationZoom(location);
    //                         }
    //                     }
    //                 });
    //
    //     } catch (SecurityException e) {
    //         e.printStackTrace();
    //     }
    // }

}