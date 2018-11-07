package com.example.kornel.alphaui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    private GoogleMap mMap;

    private OnMapUpdate mCallback;

    private FloatingActionButton mFab;

    private IntentFilter mLocationIntentFilter;
    private LocationBroadcastReceiver mLocationReceiver;


    private PolylineOptions mPolylineOptions;
    private Polyline mPolyline;


    interface OnMapUpdate {
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
        // getActivity().registerReceiver(mLocationReceiver, mLocationIntentFilter);
        Log.d(TAG, "onResume: " + mLocationReceiver);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        // getActivity().unregisterReceiver(mLocationReceiver);
        Log.d(TAG, "onPause: " + mLocationReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public class LocationBroadcastReceiver extends BroadcastReceiver {
        private static final String TAG = "LocationBroadcastReceiv";

        private final Handler handler;

        public LocationBroadcastReceiver(Handler handler) {
            this.handler = handler;
        }



        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            final double lat = intent.getDoubleExtra("lat", 0.00);
            final double lng = intent.getDoubleExtra("lng", 0.00);
            final String msg = lat + " " + lng;
            Log.d(TAG, "onReceive: " + msg);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mPolylineOptions == null) {
                        mPolylineOptions = new PolylineOptions();
                        // .add(newLatLng);
                        mPolyline = mMap.addPolyline(mPolylineOptions);
                    } else {
                        mPolyline.setPoints(mCallback.onMapUpdate());
                    }
                    LatLng sydney = new LatLng(lat, lng);
                    mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                }
            });
        }
    }



}