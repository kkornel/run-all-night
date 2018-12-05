package com.example.kornel.alphaui;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindOthersMapFragment extends Fragment implements OnMapReadyCallback, LocationUtils.MyLocationResult {
    private static final String TAG = "FindOthersMapFragment";

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

    private OnInfoWindowElemTouchListener mInfoButtonListener;

    private OnFindOthersCallback mOnFindOthersCallback;

    private LatLng mYouLatLng;
    private Marker mYouMarker;

    private List<SharedLocationInfo> mSharedLocationInfoList;
    private Map<Marker, SharedLocationInfo> mMarkersMap;

    public interface OnFindOthersCallback {
        void onGotAllSharedLocations(List<SharedLocationInfo> sharedLocationInfoList);
        void onFindOthersSuccess();
        void onMapUpdate(SharedLocationInfo sharedLoc);
        void onNewRequest(Location location);
    }

    public FindOthersMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_find_others_map, container, false);

        mMapWrapperLayout = rootView.findViewById(R.id.map_relative_layout);
        SupportMapFragment map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.summaryMap));
        map.getMapAsync(this);

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

                SharedLocationInfo iasd = mMarkersMap.get(marker);
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

                SharedLocationInfo sli = mMarkersMap.get(marker);

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
    }

    @Override
    public void gotLocation(Location location, LocationUtils.LocationErrorType errorType) {
        mYouLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        addYouMarker(mYouLatLng);
    }

    public void onGotSharedLocationInfoList(List<SharedLocationInfo> sharedLocationInfoList) {
        mSharedLocationInfoList = sharedLocationInfoList;
    }

    public void onNewRequest(final Location location) {
        mYouLatLng = new LatLng(location.getLatitude(), location.getLongitude());
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
        mYouMarker = mMap.addMarker(new MarkerOptions()
                .position(youLatLng)
                .title("You")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(youLatLng, MAP_ZOOM));
    }

    private int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    public void setCallback(OnFindOthersCallback onFindOthersCallback) {
        mOnFindOthersCallback = onFindOthersCallback;
    }


}
