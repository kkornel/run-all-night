package com.example.kornel.alphaui;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kornel.alphaui.utils.CurrentUserProfile;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.LatLon;
import com.example.kornel.alphaui.utils.Utils;
import com.example.kornel.alphaui.weather.LocationUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ShareYourLocationMapFragment extends Fragment implements OnMapReadyCallback, LocationUtils.MyLocationResult {
    private static final int MESSAGE_MAX_LENGTH = 140;
    private static final int MAP_ZOOM = 15;

    private OnDataChanged mOnDataChangedCallback;

    private Spinner mWorkoutTypeSpinner;
    private EditText mMessageEditText;
    private Button mPreviewButton;
    private MapWrapperLayout mMapWrapperLayout;
    private GoogleMap mMap;
    private Button mShareButton;

    private ViewGroup mInfoWindow;
    private ImageView mAvatarImageView;
    private TextView mNameTextView;
    private TextView mDistanceTextView;
    private TextView mMessageTextView;
    private Button mShowProfileButton;
    private Button mAddToFriendsButton;

    private LatLng mYouLatLng;
    private Marker mYouMarker;

    public interface OnDataChanged {
        void onUploadCompleted();
    }

    public void setCallBack(OnDataChanged callBack) {
        mOnDataChangedCallback = callBack;
    }

    public ShareYourLocationMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_share_your_location_map, container, false);

        mWorkoutTypeSpinner = rootView.findViewById(R.id.workoutTypeSpinner);
        mMessageEditText = rootView.findViewById(R.id.messageEditText);
        mMessageEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(MESSAGE_MAX_LENGTH)});
        mPreviewButton = rootView.findViewById(R.id.previewButton);
        mPreviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                addYouMarker(mYouLatLng);
                mShareButton.setEnabled(true);
                Utils.hideKeyboard(ShareYourLocationMapFragment.this);
            }
        });

        mMapWrapperLayout = rootView.findViewById(R.id.map_relative_layout);
        SupportMapFragment map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.summaryMap));
        map.getMapAsync(this);

        mShareButton = rootView.findViewById(R.id.shareButton);
        mShareButton.setEnabled(false);
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mMessageEditText.getText().toString();
                if (mYouLatLng == null || message.equals("")) {
                    Toast.makeText(getContext(), "Wiadomość nie może być pusta!", Toast.LENGTH_LONG).show();
                    return;
                }
                upload();
            }
        });

        mYouLatLng = null;

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                new LocationUtils().findUserLocation(getActivity(), getContext(), ShareYourLocationMapFragment.this);
            }
        });

        // MapWrapperLayout initialization
        // 39 - default marker height
        // 20 - offset between the default InfoWindow bottom edge and it's content bottom edge
        mMapWrapperLayout.init(mMap, FindOthersMapFragment.getPixelsFromDp(getContext(), 39 + 20));

        // We want to reuse the info window for all the markers,
        // so let's create only one class member instance
        mInfoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.marker_info_window, null);
        mAvatarImageView = mInfoWindow.findViewById(R.id.avatarImageView);
        mNameTextView = mInfoWindow.findViewById(R.id.nameTextView);
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

                mShowProfileButton.setVisibility(View.GONE);
                mAddToFriendsButton.setVisibility(View.GONE);

                mNameTextView.setText(CurrentUserProfile.getFullName());
                mDistanceTextView.setText("0" + "km");
                String message = mMessageEditText.getText().toString();
                mMessageTextView.setText(message);

                Picasso.get()
                        .load(CurrentUserProfile.getAvatarUrl())
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
        // mMap.clear();
        mYouLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mYouLatLng, MAP_ZOOM));
        // mMap.getUiSettings().setAllGesturesEnabled(false);
        // addYouMarker(mYouLatLng);
    }

    private void upload() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference rootRef = database.getReference();
        DatabaseReference sharedLocRef = rootRef.child(Database.SHARED_LOCATIONS);
        DatabaseReference userRef = rootRef.child(Database.USERS);

        String userUid = user.getUid();
        String message = mMessageEditText.getText().toString();
        LatLon latLon = new LatLon(mYouLatLng.latitude, mYouLatLng.longitude);
        // final String key = mWorkoutRef.child(mUserUid).push().getKey();

        SharedLocationInfo sharedLocationInfo = new SharedLocationInfo(latLon, message);

        String workoutType = mWorkoutTypeSpinner.getSelectedItem().toString();
        sharedLocRef.child(workoutType).child(userUid).setValue(sharedLocationInfo);

        mMessageEditText.setText("");
        Toast.makeText(getContext(), "Uploaded!", Toast.LENGTH_SHORT).show();
        mOnDataChangedCallback.onUploadCompleted();
        mMap.clear();
    }

    private void addYouMarker(LatLng youLatLng) {
        mYouMarker = mMap.addMarker(new MarkerOptions()
                .position(youLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(youLatLng, MAP_ZOOM));
    }
}
