package com.example.kornel.alphaui.sharelocation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kornel.alphaui.mainactivity.MainActivity;
import com.example.kornel.alphaui.utils.ErrorLog;
import com.example.kornel.alphaui.utils.MainActivityLog;
import com.example.kornel.alphaui.utils.MapWrapperLayout;
import com.example.kornel.alphaui.utils.OnInfoWindowElemTouchListener;
import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.profile.ViewProfileActivity;
import com.example.kornel.alphaui.utils.Database;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindOthersMapFragment extends Fragment implements OnMapReadyCallback, LocationUtils.MyLocationResult {
    private static final String TAG = "FindOthersMapFragment";

    public static final String EXTRA_USER = "extra_user";

    private static final int MARKER_MAP_PADDING = 200;
    private static final int MAP_ZOOM = 17;

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

    private OnInfoWindowElemTouchListener mShowProfileButtonListener;
    private OnInfoWindowElemTouchListener mAddToFriendsButtonListener;

    private OnFindOthersCallback mOnFindOthersCallback;

    private LatLng mYouLatLng;
    private Marker mYouMarker;

    private List<SharedLocationInfo> mSharedLocationInfoList;
    private Map<Marker, SharedLocationInfo> mMarkersMap;

    private SharedLocationInfo mSli;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRootRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mFriendRequestsRef;
    private String mUserUid;
    private String mFriendUid;

    private boolean mFirstLoad = true;

    public interface OnFindOthersCallback {
        void onGotAllSharedLocations(List<SharedLocationInfo> sharedLocationInfoList);

        void onFindOthersSuccess();

        void onMapUpdate(SharedLocationInfo sharedLoc);

        void onNewRequest(Location location);
    }

    public FindOthersMapFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_find_others_map, container, false);

        mMapWrapperLayout = rootView.findViewById(R.id.map_relative_layout);
        SupportMapFragment map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.summaryMap));
        map.getMapAsync(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mUserUid = mFirebaseAuth.getUid();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRootRef = mFirebaseDatabase.getReference();

        mUsersRef = mRootRef.child(Database.USERS);
        mFriendRequestsRef = mRootRef.child(Database.FRIENDS_REQUESTS);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if (mFirstLoad) {
                    mFirstLoad = false;
                    mMarkersMap = new HashMap<>();
                    addYouMarker(mYouLatLng);
                }

                // new LocationUtils().findUserLocation(getActivity(), getContext(), FindOthersMapFragment.this);
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

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the mInfoWindow with current's marker info
                if (marker.equals(mYouMarker)) {
                    mNameTextView.setText(R.string.you);

                    mAvatarImageView.setVisibility(View.GONE);
                    mDistanceLabel.setVisibility(View.GONE);
                    mDistanceTextView.setVisibility(View.GONE);
                    mMessageTextView.setVisibility(View.GONE);
                    mShowProfileButton.setVisibility(View.GONE);
                    mAddToFriendsButton.setVisibility(View.GONE);
                    //mShowProfileButtonListener.setMarker(marker);

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

                mSli = mMarkersMap.get(marker);

                mFriendUid = mSli.getUserUid();

                DatabaseReference userFriendRef = mUsersRef.child(mUserUid).child(Database.FRIENDS).child(mFriendUid);
                ValueEventListener friendsListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            mAddToFriendsButton.setEnabled(false);
                            mAddToFriendsButton.setText(getString(R.string.already_a_friends));
                        } else {

                            DatabaseReference userFriendRequestsRef = mFriendRequestsRef.child(mUserUid).child(mFriendUid);
                            ValueEventListener userFriendRequestsListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        mAddToFriendsButton.setEnabled(false);
                                        mAddToFriendsButton.setText(getString(R.string.friends_dialog_invitation_sent));
                                    } else {
                                        mAddToFriendsButton.setEnabled(true);
                                        mAddToFriendsButton.setText(getString(R.string.invite_to_friends));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                                    throw databaseError.toException();
                                }
                            };
                            userFriendRequestsRef.addListenerForSingleValueEvent(userFriendRequestsListener);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                        throw databaseError.toException();
                    }
                };
                userFriendRef.addListenerForSingleValueEvent(friendsListener);

                mNameTextView.setText(mSli.getUserProfile().getFullName());

                mDistanceTextView.setText(mSli.getDistanceToYouString() + " km");
                mMessageTextView.setText(mSli.getMessage());
                mShowProfileButtonListener.setMarker(marker);

                Picasso.get()
                        .load(mSli.getUserProfile().getAvatarUrl())
                        .into(mAvatarImageView);

                // We must call this to set the current marker and mInfoWindow references
                // to the MapWrapperLayout
                mMapWrapperLayout.setMarkerWithInfoWindow(marker, mInfoWindow);
                return mInfoWindow;
            }
        });

        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        mShowProfileButtonListener = new OnInfoWindowElemTouchListener(mShowProfileButton) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                // Toast.makeText(getContext(), marker.getTitle() + "'s button clicked!", Toast.LENGTH_SHORT).show();

                SharedLocationInfo iasd = mMarkersMap.get(marker);

                Intent i = new Intent(getContext(), ViewProfileActivity.class);
                i.putExtra(EXTRA_USER, iasd.getUserProfile());

                startActivity(i);
            }
        };
        mShowProfileButton.setOnTouchListener(mShowProfileButtonListener);

        mAddToFriendsButtonListener = new OnInfoWindowElemTouchListener(mShowProfileButton) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {

                mAddToFriendsButton.setEnabled(true);
                mAddToFriendsButton.setText(getString(R.string.invite_to_friends));

                mFriendRequestsRef.child(mUserUid).child(mFriendUid).setValue(Database.FRIENDS_REQUESTS_SENT);
                mFriendRequestsRef.child(mFriendUid).child(mUserUid).setValue(Database.FRIENDS_REQUESTS_RECEIVED);

                Toast.makeText(getContext(), R.string.friends_dialog_invitation_sent, Toast.LENGTH_SHORT).show();

                mAddToFriendsButton.setText(getString(R.string.friends_dialog_invitation_sent));
                mAddToFriendsButton.setEnabled(false);
            }
        };
        mAddToFriendsButton.setOnTouchListener(mAddToFriendsButtonListener);
    }

    @Override
    public void gotLocation(Location location, LocationUtils.LocationErrorType errorType) {
        if (errorType == LocationUtils.LocationErrorType.LOCATION_SERVICE_IS_NOT_AVAILABLE) {
            ErrorLog.d(TAG + " - gotLocation - " + errorType);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.gps_not_enabled_explenation)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getActivity().finish();
                        }
                    });
            builder.create().show();
            return;
        }

        if (location == null) {
            ErrorLog.d(TAG + " - gotLocation - " + location);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.couldnt_get_location)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getActivity().finish();
                        }
                    });
            builder.create().show();
            return;
        }

        mYouLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        addYouMarker(mYouLatLng);
    }

    public void onGotSharedLocationInfoList(List<SharedLocationInfo> sharedLocationInfoList) {
        mSharedLocationInfoList = sharedLocationInfoList;
    }

    public void onNewRequest(final Location location) {
        mYouLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (mMap == null || mFirstLoad) {
            ErrorLog.d(TAG + " - onNewRequest - " + mMap + " " + mFirstLoad);
            return;
        }

        mMap.clear();
        mMarkersMap = new HashMap<>();
        addYouMarker(mYouLatLng);
    }

    public void onMapUpdate(final SharedLocationInfo sharedLoc) {
        Marker otherMarker = mMap.addMarker(new MarkerOptions()
                .position(sharedLoc.getLatLon().toLatLng())
                .title(sharedLoc.getUserProfile().getFullName()));

        mMarkersMap.put(otherMarker, sharedLoc);

        if (mMarkersMap.size() != mSharedLocationInfoList.size()) {
            return;
        }

        mOnFindOthersCallback.onFindOthersSuccess();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Marker marker : mMarkersMap.keySet()) {
            builder.include(marker.getPosition());
        }
        builder.include(mYouLatLng);
        LatLngBounds bounds = builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, MARKER_MAP_PADDING));
    }

    private void addYouMarker(LatLng youLatLng) {
        if (youLatLng == null) {
            ErrorLog.d(TAG + " - addYouMarker - " + youLatLng);
            return;
        }

        mYouMarker = mMap.addMarker(new MarkerOptions()
                .position(youLatLng)
                .title(getString(R.string.you))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(youLatLng, MAP_ZOOM));
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void setCallback(OnFindOthersCallback onFindOthersCallback) {
        mOnFindOthersCallback = onFindOthersCallback;
    }
}
