package com.example.kornel.alphaui.gpsworkout;


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

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.utils.LatLon;
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
    public static final String ACTION_LAST_LOCATION = "last_location_intent_filter";

    private static final int ZOOM_VALUE = 17;

    private GoogleMap mMap;

    private OnMapUpdate mCallback;

    private FloatingActionButton mFab;

    private IntentFilter mLocationIntentFilter;
    private LocationBroadcastReceiver mLocationReceiver;

    private PolylineOptions mPolylineOptions;
    private Polyline mPolyline;

    interface OnMapUpdate {
        void onFabClicked();
    }

    public MapsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.maps_fragment, container, false);

        SupportMapFragment map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        map.getMapAsync(this);

        mFab = rootView.findViewById(R.id.returnFab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onFabClicked();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mLocationIntentFilter = new IntentFilter();
        mLocationIntentFilter.addAction(ACTION_LOCATION_CHANGED);
        mLocationIntentFilter.addAction(ACTION_LAST_LOCATION);
        mLocationReceiver = new LocationBroadcastReceiver(new Handler());
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mLocationReceiver, mLocationIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mLocationReceiver);
    }

    private void centerMapOnTheLocationZoom(Location location, int zoom) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void setCallback(OnMapUpdate mCallback) {
        this.mCallback = mCallback;
    }

    // TODO: Przesyłać całą tablice? Czy tylko pojedyncze LatLng?
    public class LocationBroadcastReceiver extends BroadcastReceiver {
        private static final String TAG = "LocationBroadcastReceiv";

        public static final String LOCATION_EXTRA_BROADCAST_INTENT = "array_path";
        public static final String LAST_LOCATION_EXTRA_BROADCAST_INTENT = "last_location";

        private final Handler handler;

        public LocationBroadcastReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case ACTION_LAST_LOCATION:
                    final Location lastLocation = intent.getParcelableExtra(LAST_LOCATION_EXTRA_BROADCAST_INTENT);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            centerMapOnTheLocationZoom(lastLocation, ZOOM_VALUE);
                        }
                    });

                    break;

                case ACTION_LOCATION_CHANGED:
                    final ArrayList<LatLon> latLonPath = intent.getParcelableArrayListExtra(LOCATION_EXTRA_BROADCAST_INTENT);
                    final ArrayList<LatLng> path = LatLon.latLonToLatLng(latLonPath);

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
                            int idx = path.size() - 1;
                            LatLng newLatLng = path.get(idx);

                            Location newLocation = new Location(LocationManager.GPS_PROVIDER);
                            newLocation.setLatitude(newLatLng.latitude);
                            newLocation.setLongitude(newLatLng.longitude);

                            centerMapOnTheLocationZoom(newLocation, ZOOM_VALUE);
                        }
                    });

                    break;
            }
        }
    }
}