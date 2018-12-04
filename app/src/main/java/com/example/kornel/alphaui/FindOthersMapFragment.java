package com.example.kornel.alphaui;


import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.kornel.alphaui.utils.CurrentUserProfile;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.LatLon;
import com.example.kornel.alphaui.weather.LocationUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FindOthersMapFragment extends Fragment implements OnMapReadyCallback, LocationUtils.MyLocationResult {

    private static final String TAG = "FindOthersMapFragment";

    private Spinner mWorkoutTypeSpinner;
    private EditText mDistanceEditText;
    private Button mFindButton;

    private GoogleMap mMap;

    public FindOthersMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_find_others_map, container, false);

        mWorkoutTypeSpinner = rootView.findViewById(R.id.workoutTypeSpinner);
        mDistanceEditText = rootView.findViewById(R.id.distanceEditText);
        mFindButton = rootView.findViewById(R.id.findButton);

        mFindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find();
            }
        });

        SupportMapFragment map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.summaryMap));
        map.getMapAsync(this);


        return rootView;
    }

    private List<SharedLocationInfo> mSharedLocationInfoList;

    private void find() {
        mMap.clear();
        mSharedLocationInfoList = new ArrayList<>();
        addYouMarker(mYouLatLng);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference rootRef = database.getReference();
        DatabaseReference sharedLocRef = rootRef.child(Database.SHARED_LOCATIONS);
        DatabaseReference userRef = rootRef.child(Database.USERS);

        String userUid = user.getUid();

        // Log.d(TAG, "find: " + );

        ValueEventListener sharedLocListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Log.d(TAG, "onDataChange: " + dataSnapshot.toString());
                // Log.d(TAG, "onDataChange: " + CurrentUserProfile.getUserUid());
                for (DataSnapshot sharedLoc : dataSnapshot.getChildren()) {
                    // Log.d(TAG, "onDataChange: " + sharedLoc.toString());
                    SharedLocationInfo sharedLocationInfo = sharedLoc.getValue(SharedLocationInfo.class);
                    sharedLocationInfo.setUserUid(sharedLoc.getKey());

                    // Log.d(TAG, "onDataChange: " + sharedLocationInfo.getUserUid());

                    if (sharedLocationInfo.getUserUid().equals(CurrentUserProfile.getUserUid())) {
                        continue;
                    }

                    double distance = mYouLocation.distanceTo(sharedLocationInfo.getLocation());
                    distance /= 1000;

                    String distanceStringInput = mDistanceEditText.getText().toString();
                    double distanceInput = Double.parseDouble(distanceStringInput);

                    Log.d(TAG, "distance: " + distance);
                    Log.d(TAG, "distanceInput: " + distanceInput);

                    if (distance > distanceInput) {
                        continue;
                    }

                    Log.d(TAG, "distance: " + distance);

                    mSharedLocationInfoList.add(sharedLocationInfo);
                }
                LatLngBounds.Builder builder = new LatLngBounds.Builder();


                for (SharedLocationInfo sharedLoc : mSharedLocationInfoList) {
                    // Log.d(TAG, "onDataChange: " + sharedLoc.toString());
                    Marker userLocation = mMap.addMarker(new MarkerOptions()
                            .position(sharedLoc.getLatLon().toLatLng())
                            .title(sharedLoc.getUserUid())
                            .snippet(sharedLoc.getMessage()));
                    builder.include(userLocation.getPosition());
                }
                builder.include(mYouLatLng);
                LatLngBounds bounds = builder.build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                throw databaseError.toException();
            }
        };

        sharedLocRef.child(mWorkoutTypeSpinner.getSelectedItem().toString()).addListenerForSingleValueEvent(sharedLocListener);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                new LocationUtils().findUserLocation(getActivity(), getContext(),FindOthersMapFragment.this);
            }
        });
    }

    private Location mYouLocation;
    private LatLng mYouLatLng;

    @Override
    public void gotLocation(Location location, LocationUtils.LocationErrorType errorType) {
        mYouLocation = location;
        mYouLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        addYouMarker(mYouLatLng);
    }

    private void addYouMarker(LatLng youLatLng) {
        Marker youMarker = mMap.addMarker(new MarkerOptions()
                .position(youLatLng)
                .title("You")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(youLatLng, 17));
    }
}
