package com.example.kornel.alphaui;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kornel.alphaui.gpsworkout.PaceAdapter;
import com.example.kornel.alphaui.gpsworkout.WorkoutGpsSummary;
import com.example.kornel.alphaui.gpsworkout.WorkoutSummaryActivity;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.IconUtils;
import com.example.kornel.alphaui.utils.Lap;
import com.example.kornel.alphaui.utils.LatLon;
import com.example.kornel.alphaui.utils.User;
import com.example.kornel.alphaui.weather.NetworkUtils;
import com.example.kornel.alphaui.weather.WeatherConsts;
import com.example.kornel.alphaui.weather.WeatherInfoCompressed;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.kornel.alphaui.gpsworkout.StartGpsWorkoutActivity.WORKOUT_DETAILS_EXTRA_INTENT;
import static com.example.kornel.alphaui.mainactivity.FeedYouFragment.WORKOUT_INTENT_EXTRA;
import static com.example.kornel.alphaui.weather.WeatherInfo.CELSIUS;

public class WorkoutGpsDetails extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "WorkoutGpsDetails";

    private CardView mWorkoutCardView;
    private ImageView mActivityIconImageView;
    private TextView mActivityTypeTextView;
    private TextView mDateTextView;

    private GoogleMap mMap;

    private CardView mSummaryCardView;
    private ImageView mDurationImageView;
    private TextView mDurationTextView;
    private ImageView mAvgPaceImageView;
    private TextView mAvgPaceTextView;
    private ImageView mAvgSpeedImageView;
    private TextView mAvgSpeedTextView;
    private ImageView mDistanceImageView;
    private TextView mDistanceTextView;
    private ImageView mMaxPaceImageView;
    private TextView mMaxPaceTextView;
    private ImageView mMaxSpeedImageView;
    private TextView mMaxSpeedTextView;

    private TextView mStatusLabel;
    private CardView mStatusCardView;
    private TextView mStatusTextView;

    private TextView mPhotoLabel;
    private CardView mPhotoCardView;
    private ImageView mPhotoImageView;

    private CardView mPrivacyCardView;
    private TextView mPrivacyTextView;

    private TextView mWeatherLabel;
    private CardView mWeatherCardView;
    private TextView mWeatherSummaryTextView;
    private ImageView mWeatherImageView;
    private TextView mWeatherTempTextView;

    private TextView mLapsLabel;
    private CardView mLapsCardView;
    private RecyclerView mRecyclerView;
    private PaceAdapter mPaceAdapter;

    private WorkoutGpsSummary mWorkoutGpsSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_gps_details);

        getSupportActionBar().setTitle(R.string.summary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mWorkoutGpsSummary = getIntent().getExtras().getParcelable(WORKOUT_INTENT_EXTRA);

        mWorkoutCardView = findViewById(R.id.workoutCardView);
        mActivityIconImageView = findViewById(R.id.activityIconImageView);
        mActivityTypeTextView = findViewById(R.id.activityTypeTextView);
        mDateTextView = findViewById(R.id.dateTextView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActivityIconImageView.setImageDrawable(getResources().getDrawable(IconUtils.getWorkoutIcon(mWorkoutGpsSummary.getWorkoutName()), getApplicationContext().getTheme()));
        } else {
            mActivityIconImageView.setImageDrawable(getResources().getDrawable(IconUtils.getWorkoutIcon(mWorkoutGpsSummary.getWorkoutName())));
        }
        mActivityTypeTextView.setText(mWorkoutGpsSummary.getWorkoutName());
        mDateTextView.setText(mWorkoutGpsSummary.getFullDateStringPlWithTime());


        SupportMapFragment map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.summaryMap));
        map.getMapAsync(this);


        mSummaryCardView = findViewById(R.id.summaryCardView);
        mDurationImageView = findViewById(R.id.durationImageView);
        mDurationTextView = findViewById(R.id.durationTextView);
        mAvgPaceImageView = findViewById(R.id.avgPaceImageView);
        mAvgPaceTextView = findViewById(R.id.avgPaceTextView);
        mAvgSpeedImageView = findViewById(R.id.avgSpeedImageView);
        mAvgSpeedTextView = findViewById(R.id.avgSpeedTextView);
        mDistanceImageView = findViewById(R.id.distanceImageView);
        mDistanceTextView = findViewById(R.id.distanceTextView);
        mMaxPaceImageView = findViewById(R.id.maxPaceImageView);
        mMaxPaceTextView = findViewById(R.id.maxPaceTextView);
        mMaxSpeedImageView = findViewById(R.id.maxSpeedImageView);
        mMaxSpeedTextView = findViewById(R.id.maxSpeedTextView);

        mDurationTextView.setText(mWorkoutGpsSummary.getDuration());
        mDistanceTextView.setText(mWorkoutGpsSummary.getDistance());
        mAvgPaceTextView.setText(mWorkoutGpsSummary.getAvgPace());
        mMaxPaceTextView.setText(mWorkoutGpsSummary.getMaxPace());
        mAvgSpeedTextView.setText(mWorkoutGpsSummary.getAvgSpeed());
        mMaxSpeedTextView.setText(mWorkoutGpsSummary.getMaxSpeed());

        Log.d(TAG, "onCreate: " +mWorkoutGpsSummary);

        mStatusLabel = findViewById(R.id.statusLabel);
        mStatusCardView = findViewById(R.id.statusCardView);
        mStatusTextView = findViewById(R.id.statusTextView);

        if (mWorkoutGpsSummary.getStatus() == null || mWorkoutGpsSummary.getStatus().equals("")) {
            mStatusLabel.setVisibility(View.GONE);
            mStatusCardView.setVisibility(View.GONE);
            Log.d(TAG, "mStatusLabel.setVisibility(View.GONE);: ");
        } else {
            mStatusTextView.setText(mWorkoutGpsSummary.getStatus());
            Log.d(TAG, "mWorkoutGpsSummary.getStatus(): ");
        }

        mPhotoLabel = findViewById(R.id.photoLabel);
        mPhotoCardView = findViewById(R.id.photoCardView);
        mPhotoImageView = findViewById(R.id.photoImageView);

        if (mWorkoutGpsSummary.getPicUrl() == null || mWorkoutGpsSummary.getPicUrl().equals("")) {
            mPhotoLabel.setVisibility(View.GONE);
            mPhotoCardView.setVisibility(View.GONE);
            Log.d(TAG, "mPhotoLabel.setVisibility(View.GONE);: ");
        } else {
            Picasso.get()
                    .load(mWorkoutGpsSummary.getPicUrl())
                    .into(mPhotoImageView);
            Log.d(TAG, "Picasso.get(): ");
        }

        mPrivacyCardView = findViewById(R.id.privacyCardView);
        mPrivacyTextView = findViewById(R.id.privacyTextView);
        mPrivacyTextView.setText(mWorkoutGpsSummary.isPrivate() ? getString(R.string.just_you) :  getString(R.string.friends));


        mWeatherLabel = findViewById(R.id.weatherLabel);
        mWeatherCardView = findViewById(R.id.weatherCardView);
        mWeatherSummaryTextView = findViewById(R.id.weatherSummaryTextView);
        mWeatherImageView = findViewById(R.id.weatherImageView);
        mWeatherTempTextView = findViewById(R.id.weatherTempTextView);

        WeatherInfoCompressed weatherInfoCompressed = mWorkoutGpsSummary.getWeatherInfoCompressed();
        if (weatherInfoCompressed != null) {
            mWeatherSummaryTextView.setText(WeatherConsts.getConditionPlByCode(weatherInfoCompressed.getCode()));
            Picasso.get()
                    .load(weatherInfoCompressed.getConditionIconURL())
                    .into(mWeatherImageView);
            mWeatherTempTextView.setText(weatherInfoCompressed.getTempC() + CELSIUS);
        } else {
            mWeatherLabel.setVisibility(View.GONE);
            mWeatherCardView.setVisibility(View.GONE);
        }


        mLapsLabel = findViewById(R.id.lapsLabel);
        mLapsCardView = findViewById(R.id.lapsCardView);
        mRecyclerView = findViewById(R.id.recyclerViewPace);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);


        ArrayList<Lap> laps = mWorkoutGpsSummary.getLaps();
        if (laps == null || laps.size() == 0) {
            mLapsLabel.setVisibility(View.GONE);
            mLapsCardView.setVisibility(View.GONE);
        } else {
            mPaceAdapter = new PaceAdapter(laps);
            mRecyclerView.setAdapter(mPaceAdapter);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ArrayList<LatLon> path = mWorkoutGpsSummary.getPath();

        if (path == null || path.size() == 0) {
            return;
        }

        final int padding = 40;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        PolylineOptions polylineOptions = new PolylineOptions();

        for (int i = 0; i < path.size(); i++) {
            LatLon latLon = path.get(i);
            polylineOptions.add(new LatLng(latLon.getLatitude(), latLon.getLongitude()));
            builder.include(new LatLng(latLon.getLatitude(), latLon.getLongitude()));
        }

        Polyline polyline = mMap.addPolyline(polylineOptions);
        final LatLngBounds bounds = builder.build();

        LatLng startPoint = new LatLng(path.get(0).getLatitude(), path.get(0).getLongitude());
        LatLng endPoint = new LatLng(path.get(path.size()-1).getLatitude(), path.get(path.size()-1).getLongitude());


        mMap.addMarker(new MarkerOptions()
                .position(startPoint)
                .title(getString(R.string.beginning))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        mMap.addMarker(new MarkerOptions()
                .position(endPoint)
                .title(getString(R.string.end))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        ArrayList<Lap> laps = mWorkoutGpsSummary.getLaps();
        if (laps != null || laps.size() > 1) {
            int i = 1;
            for (Lap lap : laps) {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lap.getLatLon().getLatitude(), lap.getLatLon().getLongitude()))
                        .title("KM: " + i++)
                        .snippet(lap.getTimeString())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

            }
        }

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteButton:
                deleteWorkout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteWorkout() {
        new AlertDialog.Builder(WorkoutGpsDetails.this)
                .setTitle(R.string.confirm)
                .setMessage(R.string.confirm_delete_workout)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        delete();

                        finish();
                    }})
                .setNegativeButton(R.string.no, null).show();
    }

    private void delete() {
        if (!NetworkUtils.isConnected(WorkoutGpsDetails.this)) {
            NetworkUtils.requestInternetConnection(mWorkoutCardView);
            return;
        }

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final DatabaseReference rootRef = database.getReference();
        final DatabaseReference userRef = rootRef.child(Database.USERS);
        final DatabaseReference workoutRef = rootRef.child(Database.WORKOUTS);

        final String userUid = user.getUid();
        final String key = mWorkoutGpsSummary.getKey();

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user.getLastWorkout().equals(key)) {

                    ValueEventListener workoutListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long i = 1;
                            long childrenCount = dataSnapshot.getChildrenCount();
                            for (DataSnapshot workout : dataSnapshot.getChildren()) {
                                if (i == childrenCount - 1) {
                                    userRef.child(userUid).child(Database.LAST_WORKOUT).setValue(workout.getKey());
                                }
                                i++;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                            throw databaseError.toException();
                        }
                    };

                    workoutRef.child(userUid).addListenerForSingleValueEvent(workoutListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                throw databaseError.toException();
            }
        };
        userRef.child(userUid).addListenerForSingleValueEvent(userListener);

        ValueEventListener workoutListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                WorkoutGpsSummary workout = dataSnapshot.getValue(WorkoutGpsSummary.class);

                if (workout.getPicUrl() != null && !workout.getPicUrl().equals("")) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference picsRef = storageRef.child(Database.PICTURES);

                    picsRef.child(userUid).child(key + ".jpg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: PICTURE DELETED");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e(TAG, "onCancelled: " + exception.getMessage());
                        }
                    });
                } else {
                    Log.d(TAG, "onSuccess: WORKOUT DOESNOT COTAING PICTURE");
                }

                workoutRef.child(userUid).child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(WorkoutGpsDetails.this, getString(R.string.workout_deleted), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "onCancelled: " + exception.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                throw databaseError.toException();
            }
        };
        workoutRef.child(userUid).child(key).addListenerForSingleValueEvent(workoutListener);


    }
}
