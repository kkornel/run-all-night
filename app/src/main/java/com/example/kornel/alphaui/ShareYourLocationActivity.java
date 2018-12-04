package com.example.kornel.alphaui;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kornel.alphaui.mainactivity.ChooseWorkoutActivity;
import com.example.kornel.alphaui.utils.CurrentUserProfile;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.IconUtils;
import com.example.kornel.alphaui.utils.LatLon;
import com.example.kornel.alphaui.weather.LocationUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.kornel.alphaui.mainactivity.WorkoutFragment.WORKOUT_RESULT;

public class ShareYourLocationActivity extends AppCompatActivity implements OnMapReadyCallback, LocationUtils.MyLocationResult {
    public final int PICK_WORKOUT_REQUEST = 1;

    // Workout CardView
    private CardView mWorkoutCardView;
    private ImageView mWorkoutImageView;
    private TextView mWorkoutNameTextView;

    private EditText mMessageEditText;
    private Button mPreviewButton;
    private Button mShareButton;

    private GoogleMap mMap;

    private LatLng mLatLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_your_location);

        SupportMapFragment map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.summaryMap));
        map.getMapAsync(this);

        mWorkoutCardView = findViewById(R.id.activityCardView);
        mWorkoutCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShareYourLocationActivity.this, ChooseWorkoutActivity.class);
                startActivityForResult(intent, PICK_WORKOUT_REQUEST);
            }
        });
        mWorkoutImageView = findViewById(R.id.activityImageView);
        mWorkoutNameTextView = findViewById(R.id.activityNameTextView);
        mMessageEditText = findViewById(R.id.messageEditText);
        mPreviewButton = findViewById(R.id.previewButton);
        mShareButton = findViewById(R.id.shareButton);

        mPreviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new LocationUtils()).findUserLocation(ShareYourLocationActivity.this, ShareYourLocationActivity.this, ShareYourLocationActivity.this);
            }
        });
        mShareButton.setEnabled(false);
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mMessageEditText.getText().toString();
                if (mLatLng == null || message.equals("")) {
                    Toast.makeText(ShareYourLocationActivity.this, "Err", Toast.LENGTH_LONG).show();
                    return;
                }

                upload();
            }
        });

        mLatLng = null;
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
        LatLon latLon = new LatLon(mLatLng.latitude, mLatLng.longitude);
        // final String key = mWorkoutRef.child(mUserUid).push().getKey();

        SharedLocationInfo sharedLocationInfo = new SharedLocationInfo(latLon, message);

        String workoutType = mWorkoutNameTextView.getText().toString();
        sharedLocRef.child(workoutType).child(userUid).setValue(sharedLocationInfo);
    }

    public void gotLocation(Location location, LocationUtils.LocationErrorType errorType) {
        mMap.clear();
        String message = mMessageEditText.getText().toString();
        mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        Marker userLocation = mMap.addMarker(new MarkerOptions()
                .position(mLatLng)
                .title(CurrentUserProfile.getFullName())
                .snippet(message));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 17));

        mMap.getUiSettings().setAllGesturesEnabled(false);

        mShareButton.setEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_WORKOUT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String workoutName = data.getStringExtra(WORKOUT_RESULT);
                mWorkoutNameTextView.setText(workoutName);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mWorkoutImageView.setImageDrawable(getResources().getDrawable(IconUtils.getWorkoutIcon(workoutName), getApplicationContext().getTheme()));
                } else {
                    mWorkoutImageView.setImageDrawable(getResources().getDrawable(IconUtils.getWorkoutIcon(workoutName)));
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
