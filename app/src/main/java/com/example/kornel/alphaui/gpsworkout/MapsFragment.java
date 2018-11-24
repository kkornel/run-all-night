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
import com.example.kornel.alphaui.weather.LocationUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import static com.example.kornel.alphaui.gpsworkout.MapsFragment.LocationBroadcastReceiver.LAST_LOCATION_EXTRA_BROADCAST_INTENT;


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

    private ArrayList<LatLng> mPath;

    interface OnMapUpdate {
        void onFabClicked();
    }

    public MapsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.maps_fragment, container, false);

        SupportMapFragment map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.summaryMap));
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

        mPath = new ArrayList<>();
        Log.d(TAG, "onStart: ");
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

    private Marker mCurrentMarker;

    private void centerMapOnTheLocationZoom(Location location, int zoom) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void updatePath(ArrayList<LatLng> path) {
        if (mPolylineOptions == null) {
            mPolylineOptions = new PolylineOptions();
            // .add(newLatLng);
            mPolyline = mMap.addPolyline(mPolylineOptions);
        } else {
            mPolyline.setPoints(path);
        }
        if (mCurrentMarker != null) {
            mCurrentMarker.remove();
        }

        mCurrentMarker = mMap.addMarker(new MarkerOptions()
                .position(path.get(path.size() - 1)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Location lastKnowLocation = LocationUtils.lastKnowLoacation;
        Log.d("qwe", "onMapReady: location= " +lastKnowLocation);
        if (lastKnowLocation != null) {
            Log.d("qwe", "onSuccess: location= " + lastKnowLocation.toString());
            centerMapOnTheLocationZoom(lastKnowLocation, ZOOM_VALUE);
        }
    }

    public void setCallback(OnMapUpdate mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public boolean getUserVisibleHint() {
        return super.getUserVisibleHint();
    }

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
                    LatLon latLon = intent.getParcelableExtra(LOCATION_EXTRA_BROADCAST_INTENT);
                    mPath.add(new LatLng(latLon.getLatitude(), latLon.getLongitude()));
                    // final ArrayList<LatLon> latLonPath = intent.getParcelableArrayListExtra(LOCATION_EXTRA_BROADCAST_INTENT);
                    // final ArrayList<LatLng> path = LatLon.latLonToLatLng(latLonPath);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // If user is looking and map do not move camera, just update path.
                            // Otherwise move and zoom camera to the new location.
                            updatePath(mPath);

                            Log.d(TAG, "run: " + getUserVisibleHint());

                            if (!getUserVisibleHint()) {
                                int idxOfNewLocation = mPath.size() - 1;
                                LatLng newLatLng = mPath.get(idxOfNewLocation);

                                Location newLocation = new Location(LocationManager.GPS_PROVIDER);
                                newLocation.setLatitude(newLatLng.latitude);
                                newLocation.setLongitude(newLatLng.longitude);

                                centerMapOnTheLocationZoom(newLocation, ZOOM_VALUE);
                            }
                        }
                    });

                    break;
            }
        }
    }
}