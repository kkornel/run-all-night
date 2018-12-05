package com.example.kornel.alphaui;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.User;
import com.example.kornel.alphaui.utils.Utils;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindOthersMapFragment extends Fragment implements OnMapReadyCallback, LocationUtils.MyLocationResult {
    private static final String TAG = "FindOthersMapFragment";

    private Spinner mWorkoutTypeSpinner;
    private EditText mDistanceEditText;
    private Button mFindButton;
    private TextView mResultsLabel;
    private TextView mResultsTextView;

    private MapWrapperLayout mMapWrapperLayout;
    private GoogleMap mMap;

    private ViewGroup mInfoWindow;
    private ImageView mAvatarImageView;
    private TextView mNameTextView;
    private TextView mDistanceLabel;
    private TextView mDistanceTextView;
    private TextView mMessageTextView;
    private Button mShowProfileButton;
    private Button mAddToFriendsButton;

    private OnInfoWindowElemTouchListener mInfoButtonListener;

    private Location mYouLocation;
    private LatLng mYouLatLng;
    private Marker mYouMarker;

    private List<SharedLocationInfo> mSharedLocationInfoList;

    private OnFindOthersResult mOnFindOthersResult;

    public interface OnFindOthersResult {
        void onFindOthersSuccess(List<SharedLocationInfo> sharedLocationInfoList);
    }

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
                onFindButtonClicked();
            }
        });

        mMapWrapperLayout = rootView.findViewById(R.id.map_relative_layout);
        SupportMapFragment map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.summaryMap));
        map.getMapAsync(this);

        mResultsTextView = rootView.findViewById(R.id.resultsTextView);
        mResultsLabel = rootView.findViewById(R.id.resultsLabel);
        mResultsLabel.setVisibility(View.INVISIBLE);

        mWorkoutTypeSpinner.post(new Runnable() {
            @Override
            public void run() {
                mWorkoutTypeSpinner.setSelection(8);
            }
        });

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                new LocationUtils().findUserLocation(getActivity(), getContext(), FindOthersMapFragment.this);
            }
        });

        // MapWrapperLayout initialization
        // 39 - default marker height
        // 20 - offset between the default InfoWindow bottom edge and it's content bottom edge
        mMapWrapperLayout.init(mMap, getPixelsFromDp(getContext(), 39 + 20));

        // We want to reuse the info window for all the markers,
        // so let's create only one class member instance
        mInfoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.marker_info_window, null);
        mAvatarImageView = mInfoWindow.findViewById(R.id.avatarImageView);
        mNameTextView = mInfoWindow.findViewById(R.id.nameTextView);
        mDistanceLabel = mInfoWindow.findViewById(R.id.distanceLabel);
        mDistanceTextView = mInfoWindow.findViewById(R.id.distanceTextView);
        mMessageTextView = mInfoWindow.findViewById(R.id.messageTextView);
        mShowProfileButton = mInfoWindow.findViewById(R.id.viewProfileButton);
        mAddToFriendsButton = mInfoWindow.findViewById(R.id.addToFriendButton);

        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        mInfoButtonListener = new OnInfoWindowElemTouchListener(mShowProfileButton) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                Toast.makeText(getContext(), marker.getTitle() + "'s button clicked!", Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(Main2Activity.this, Main3Activity.class));

                SharedLocationInfo iasd = mMyMarkersmap.get(marker);
                Log.d(TAG, "onClickConfirmed: " + iasd.getUserProfile());

                startActivity(new Intent(getContext(), ViewProfileActivity.class));
            }
        };
        mShowProfileButton.setOnTouchListener(mInfoButtonListener);


        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the mInfoWindow with current's marker info
                if (marker.equals(mYouMarker)) {
                    Log.d(TAG, "getInfoContents: ");

                    mNameTextView.setText("Ty");

                    mAvatarImageView.setVisibility(View.GONE);
                    mDistanceLabel.setVisibility(View.GONE);
                    mDistanceTextView.setVisibility(View.GONE);
                    mMessageTextView.setVisibility(View.GONE);
                    mShowProfileButton.setVisibility(View.GONE);
                    mAddToFriendsButton.setVisibility(View.GONE);
                    //mInfoButtonListener.setMarker(marker);


                    // We must call this to set the current marker and mInfoWindow references
                    // to the MapWrapperLayout
                    mMapWrapperLayout.setMarkerWithInfoWindow(marker, mInfoWindow);
                    return mInfoWindow;

                }

                mAvatarImageView.setVisibility(View.VISIBLE);
                mDistanceLabel.setVisibility(View.VISIBLE);
                mDistanceTextView.setVisibility(View.VISIBLE);
                mMessageTextView.setVisibility(View.VISIBLE);
                mShowProfileButton.setVisibility(View.VISIBLE);
                mAddToFriendsButton.setVisibility(View.VISIBLE);

                SharedLocationInfo sli = mMyMarkersmap.get(marker);

                mNameTextView.setText(sli.getUserProfile().getFullName());

                mDistanceTextView.setText(sli.getDistanceToYouString() + "km");
                mMessageTextView.setText(sli.getMessage());
                mInfoButtonListener.setMarker(marker);

                Picasso.get()
                        .load(sli.getUserProfile().getAvatarUrl())
                        .into(mAvatarImageView);

                // We must call this to set the current marker and mInfoWindow references
                // to the MapWrapperLayout
                mMapWrapperLayout.setMarkerWithInfoWindow(marker, mInfoWindow);
                return mInfoWindow;
            }
        });

        // Let's add a couple of markers

        mMap.addMarker(new MarkerOptions()
                .title("India")
                .snippet("New Delhi")
                .position(new LatLng(20.59, 78.96)));

        mMap.addMarker(new MarkerOptions()
                .title("Prague")
                .snippet("Czech Republic")
                .position(new LatLng(50.08, 14.43)));

        mMap.addMarker(new MarkerOptions()
                .title("Paris")
                .snippet("France")
                .position(new LatLng(48.86,2.33)));

        mMap.addMarker(new MarkerOptions()
                .title("London")
                .snippet("United Kingdom")
                .position(new LatLng(51.51,-0.1)));
    }

    private void onFindButtonClicked() {
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


    private void find(final String workoutType, final double distance) {
        mMap.clear();
        mMarkersList = new ArrayList<>();
        mMyMarkersList = new ArrayList<>();
        mSharedLocationInfoList = new ArrayList<>();
        mMyMarkersmap = new HashMap<>();
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

                for (DataSnapshot snapshotSharedLocation : dataSnapshot.getChildren()) {

                    SharedLocationInfo sharedLocationInfo = snapshotSharedLocation.getValue(SharedLocationInfo.class);
                    sharedLocationInfo.setUserUid(snapshotSharedLocation.getKey());

                    if (sharedLocationInfo.getUserUid().equals(userUid)) {
                        continue;
                    }

                    double distanceToYou = mYouLocation.distanceTo(sharedLocationInfo.getLocation());
                    distanceToYou /= 1000;

                    if (distanceToYou > distance) {
                        continue;
                    }

                    sharedLocationInfo.setDistanceToYou(distanceToYou);
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
                            userProfile.setUserUid(sharedLoc.getUserUid());
                            sharedLoc.setUserProfile(userProfile);

                            updateMap(sharedLoc);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                            throw databaseError.toException();
                        }
                    };
                    userRef.child(sharedLoc.getUserUid()).addListenerForSingleValueEvent(userProfileListener);
                }

                // LatLngBounds.Builder builder = new LatLngBounds.Builder();
                //
                // for (SharedLocationInfo sharedLoc : mSharedLocationInfoList) {
                //     Marker userLocation = mMap.addMarker(new MarkerOptions()
                //             .position(sharedLoc.getLatLon().toLatLng())
                //             .title(sharedLoc.getUserUid())
                //             .snippet(sharedLoc.getMessage()));
                //     builder.include(userLocation.getPosition());
                // }
                // builder.include(mYouLatLng);
                // LatLngBounds bounds = builder.build();
                // mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                throw databaseError.toException();
            }
        };

        sharedLocRef.child(workoutType).addListenerForSingleValueEvent(sharedLocListener);
    }

    private List<Marker> mMarkersList;
    private List<MyMarker> mMyMarkersList;
    private Map<Marker, SharedLocationInfo> mMyMarkersmap;

    static class MyMarker {
        public Marker marker;
        public SharedLocationInfo info;

        public MyMarker(Marker marker, SharedLocationInfo info) {
            this.marker = marker;
            this.info = info;
        }
    }

    private void updateMap(final SharedLocationInfo sharedLoc) {
        Marker otherMarker = mMap.addMarker(new MarkerOptions()
                        .position(sharedLoc.getLatLon().toLatLng())
                        .title(sharedLoc.getUserProfile().getFullName()));

        mMarkersList.add(otherMarker);

        mMyMarkersList.add(new MyMarker(otherMarker, sharedLoc));

        mMyMarkersmap.put(otherMarker, sharedLoc);

        Log.d(TAG, "************************************************************: ");
        Log.d(TAG, "sharedLoc: " + sharedLoc);
        Log.d(TAG, "mMarkersList.size(): " + mMarkersList.size());
        Log.d(TAG, "mSharedLocationInfoList.size(): " + mSharedLocationInfoList.size());

        if (mMarkersList.size() != mSharedLocationInfoList.size()) {
            return;
        }

        mOnFindOthersResult.onFindOthersSuccess(mSharedLocationInfoList);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Marker marker : mMarkersList) {
            builder.include(marker.getPosition());
        }
        builder.include(mYouLatLng);
        LatLngBounds bounds = builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }



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

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    private void showSnackbar(String message) {
        Snackbar.make(
                    mFindButton,
                    message,
                    Snackbar.LENGTH_LONG)
                .show();
    }

    public void setCallback(OnFindOthersResult onFindOthersResult) {
        mOnFindOthersResult = onFindOthersResult;
    }
}
