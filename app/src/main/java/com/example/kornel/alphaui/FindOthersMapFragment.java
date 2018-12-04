package com.example.kornel.alphaui;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kornel.alphaui.utils.CurrentUserProfile;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.User;
import com.example.kornel.alphaui.utils.Utils;
import com.example.kornel.alphaui.weather.LocationUtils;
import com.google.android.gms.common.internal.Objects;
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

public class FindOthersMapFragment extends Fragment implements OnMapReadyCallback, LocationUtils.MyLocationResult, GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = "FindOthersMapFragment";

    private Spinner mWorkoutTypeSpinner;
    private EditText mDistanceEditText;
    private Button mFindButton;
    private TextView mResultsLabel;
    private TextView mResultsTextView;

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
                try {
                    Utils.hideKeyboard(FindOthersMapFragment.this);
                    String workoutType = mWorkoutTypeSpinner.getSelectedItem().toString();
                    String distanceStringInput = mDistanceEditText.getText().toString();
                    if (distanceStringInput.equals("")) {
                        showSnackbar("Wprowadź zasięg.");
                        return;
                    }
                    double distanceInput = Double.parseDouble(distanceStringInput);
                    find(workoutType, distanceInput);
                } catch (NumberFormatException ex) {
                    showSnackbar("Wprowadzono niepoprawną liczbę.");
                }

            }
        });

        SupportMapFragment map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.summaryMap));
        map.getMapAsync(this);

        mResultsTextView = rootView.findViewById(R.id.resultsTextView);
        mResultsLabel = rootView.findViewById(R.id.resultsLabel);
        mResultsLabel.setVisibility(View.INVISIBLE);

        return rootView;
    }

    private void showSnackbar(String message) {
        Snackbar.make(
                mFindButton,
                message,
                Snackbar.LENGTH_LONG)
                .show();
    }

    private List<SharedLocationInfo> mSharedLocationInfoList;

    private void find(final String workoutType, final double distance) {
        i = 1;
        mMap.clear();
        mSharedLocationInfoList = new ArrayList<>();
        addYouMarker(mYouLatLng);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference rootRef = database.getReference();
        DatabaseReference sharedLocRef = rootRef.child(Database.SHARED_LOCATIONS);
        final DatabaseReference userRef = rootRef.child(Database.USERS);

        final String userUid = user.getUid();


        ValueEventListener sharedLocListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dsSharedLocation : dataSnapshot.getChildren()) {
                    SharedLocationInfo sharedLocationInfo = dsSharedLocation.getValue(SharedLocationInfo.class);
                    sharedLocationInfo.setUserUid(dsSharedLocation.getKey());

                    if (sharedLocationInfo.getUserUid().equals(userUid)) {
                        continue;
                    }

                    double distanceToUser = mYouLocation.distanceTo(sharedLocationInfo.getLocation());
                    distanceToUser /= 1000;

                    if (distanceToUser > distance) {
                        continue;
                    }

                    sharedLocationInfo.setDistanceToUser(distanceToUser);
                    mSharedLocationInfoList.add(sharedLocationInfo);
                }

                mResultsLabel.setVisibility(View.VISIBLE);
                mResultsTextView.setText(String.valueOf(mSharedLocationInfoList.size()));

                if (mSharedLocationInfoList.size() == 0) {
                    Toast.makeText(getContext(), "Nie znaleziono nikogo w danym zasięgu", Toast.LENGTH_LONG).show();
                    return;
                }

                for (final SharedLocationInfo sharedLoc : mSharedLocationInfoList) {
                    ValueEventListener userProfileListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User userProfile = dataSnapshot.getValue(User.class);
                            //Log.d(TAG, "userProfile: " + userProfile);
                            userProfile.setUserUid(sharedLoc.getUserUid());
                            sharedLoc.setUserProfile(userProfile);
                            //Log.d(TAG, "sharedLoc: " + sharedLoc);
                            //Log.d(TAG, "mSharedLocationInfoList: " + mSharedLocationInfoList);
                            //updateMap();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                            throw databaseError.toException();
                        }
                    };
                    userRef.child(sharedLoc.getUserUid()).addListenerForSingleValueEvent(userProfileListener);
                }

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                for (SharedLocationInfo sharedLoc : mSharedLocationInfoList) {
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

        sharedLocRef.child(workoutType).addListenerForSingleValueEvent(sharedLocListener);
    }

    private int i = 1;

    private void updateMap() {
        Log.d(TAG, "updateMap: " + i);
        i++;
        if (i != mSharedLocationInfoList.size()) {
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (SharedLocationInfo sharedLoc : mSharedLocationInfoList) {
            Marker userLocation = mMap.addMarker(new MarkerOptions()
                    .position(sharedLoc.getLatLon().toLatLng())
                    .title(sharedLoc.getUserProfile().getFullName())
                    .snippet(sharedLoc.getMessage()));
            builder.include(userLocation.getPosition());
        }
        builder.include(mYouLatLng);
        LatLngBounds bounds = builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
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


        // Set a listener for info window events.
        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG, "mYouMarker: " + mYouMarker.toString());
        Log.d(TAG, "marker: " + marker.toString());
        if (marker.equals(mYouMarker)) {
            Toast.makeText(getContext(), "You", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "not you", Toast.LENGTH_SHORT).show();
        }

    }


    private Location mYouLocation;
    private LatLng mYouLatLng;
    Marker mYouMarker;

    @Override
    public void gotLocation(Location location, LocationUtils.LocationErrorType errorType) {
        mYouLocation = location;
        mYouLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        addYouMarker(mYouLatLng);
    }

    private void addYouMarker(LatLng youLatLng) {
        mYouMarker = mMap.addMarker(new MarkerOptions()
                .position(youLatLng)
                .title("You")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(youLatLng, 17));
    }
}
