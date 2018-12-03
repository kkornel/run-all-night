package com.example.kornel.alphaui.mainactivity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kornel.alphaui.FriendWorkout;
import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.gpsworkout.PaceAdapter;
import com.example.kornel.alphaui.gpsworkout.WorkoutSummary;
import com.example.kornel.alphaui.utils.IconUtils;
import com.example.kornel.alphaui.utils.Lap;
import com.example.kornel.alphaui.utils.LatLon;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.kornel.alphaui.mainactivity.FeedFriendsFragment.FRIEND_WORKOUT_INTENT_EXTRA;
import static com.example.kornel.alphaui.weather.WeatherInfo.CELSIUS;

public class WorkoutGpsDetailsFriend extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "WorkoutGpsDetailsFriend";

    private ImageView mAvatarImageView;
    private TextView mNameTextView;

    private CardView mWorkoutCardView;
    private ImageView mActivityIconImageView;
    private TextView mActivityTypeTextView;
    private TextView mTimeTextView;
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

    private CardView mPhotoCardView;
    private ImageView mPhotoImageView;
    private View mPhotoStatusDivider;
    private TextView mStatusTextView;

    private CardView mLapsCardView;
    private RecyclerView mRecyclerView;
    private PaceAdapter mPaceAdapter;

    private CardView mWeatherCardView;
    private TextView mWeatherSummaryTextView;
    private ImageView mWeatherImageView;
    private TextView mWeatherTempTextView;

    private WorkoutSummary mWorkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_gps_details_friend);

        FriendWorkout friendWorkout = getIntent().getExtras().getParcelable(FRIEND_WORKOUT_INTENT_EXTRA);
        mWorkout = friendWorkout.getWorkout();

        getSupportActionBar().setTitle(mWorkout.getWorkoutName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAvatarImageView = findViewById(R.id.avatarImageView);
        mNameTextView = findViewById(R.id.nameTextView);

        Picasso.get()
                .load(friendWorkout.getAvatarUrl())
                .into(mAvatarImageView);
        mNameTextView.setText(friendWorkout.getFriendName());


        mWorkoutCardView = findViewById(R.id.workoutCardView);
        mActivityIconImageView = findViewById(R.id.activityIconImageView);
        mActivityTypeTextView = findViewById(R.id.activityTypeTextView);
        mTimeTextView = findViewById(R.id.timeTextView2);
        mDateTextView = findViewById(R.id.dateTextView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActivityIconImageView.setImageDrawable(getResources().getDrawable(IconUtils.getWorkoutIcon(mWorkout.getWorkoutName()), getApplicationContext().getTheme()));
        } else {
            mActivityIconImageView.setImageDrawable(getResources().getDrawable(IconUtils.getWorkoutIcon(mWorkout.getWorkoutName())));
        }
        mTimeTextView.setText(mWorkout.getTimeHourMin());
        mDateTextView.setText(mWorkout.getDateStringPl());


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
        mDistanceTextView = findViewById(R.id.timeTextView);
        mMaxPaceImageView = findViewById(R.id.maxPaceImageView);
        mMaxPaceTextView = findViewById(R.id.maxPaceTextView);
        mMaxSpeedImageView = findViewById(R.id.maxSpeedImageView);
        mMaxSpeedTextView = findViewById(R.id.maxSpeedTextView);

        mDurationTextView.setText(mWorkout.getDuration());
        mDistanceTextView.setText(mWorkout.getDistance());
        mAvgPaceTextView.setText(mWorkout.getAvgPace());
        mMaxPaceTextView.setText(mWorkout.getMaxPace());
        mAvgSpeedTextView.setText(mWorkout.getAvgSpeed());
        mMaxSpeedTextView.setText(mWorkout.getMaxSpeed());


        mPhotoCardView = findViewById(R.id.photoCardView);
        mPhotoImageView = findViewById(R.id.photoImageView);
        mPhotoStatusDivider = findViewById(R.id.photoStatusDivider);
        mStatusTextView = findViewById(R.id.statusTextView);

        boolean noStatus = false;
        boolean noPhoto = false;

        if (mWorkout.getPicUrl() == null || mWorkout.getPicUrl().equals("")) {
            mPhotoImageView.setVisibility(View.GONE);
            mPhotoStatusDivider.setVisibility(View.GONE);
            noPhoto = true;
        } else {
            Picasso.get()
                    .load(mWorkout.getPicUrl())
                    .into(mPhotoImageView);
        }

        if (mWorkout.getStatus() == null || mWorkout.getStatus().equals("")) {
            mStatusTextView.setVisibility(View.GONE);
            mPhotoStatusDivider.setVisibility(View.GONE);
            noStatus = true;
        } else {
            mStatusTextView.setText(mWorkout.getStatus());
        }

        if (noPhoto && noStatus) {
            mPhotoCardView.setVisibility(View.GONE);
        }


        mLapsCardView = findViewById(R.id.lapsCardView);
        mRecyclerView = findViewById(R.id.recyclerViewPace);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        ArrayList<Lap> laps = mWorkout.getLaps();
        if (laps == null || laps.size() == 0) {
            mLapsCardView.setVisibility(View.GONE);
        } else {
            mPaceAdapter = new PaceAdapter(laps);
            mRecyclerView.setAdapter(mPaceAdapter);
        }


        mWeatherCardView = findViewById(R.id.weatherCardView);
        mWeatherSummaryTextView = findViewById(R.id.weatherSummaryTextView);
        mWeatherImageView = findViewById(R.id.weatherImageView);
        mWeatherTempTextView = findViewById(R.id.weatherTempTextView);

        WeatherInfoCompressed weatherInfoCompressed = mWorkout.getWeatherInfoCompressed();
        if (weatherInfoCompressed != null) {
            mWeatherSummaryTextView.setText(WeatherConsts.getConditionPlByCode(weatherInfoCompressed.getCode()));
            Picasso.get()
                    .load(weatherInfoCompressed.getConditionIconURL())
                    .into(mWeatherImageView);
            mWeatherTempTextView.setText(weatherInfoCompressed.getTempC() + CELSIUS);
        } else {
            mWeatherCardView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ArrayList<LatLon> path = mWorkout.getPath();

        if (path == null || path.size() == 0) {
            return;
        }

        final int padding = 60;

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
        LatLng endPoint = new LatLng(path.get(path.size() - 1).getLatitude(), path.get(path.size() - 1).getLongitude());


        mMap.addMarker(new MarkerOptions()
                .position(startPoint)
                .title(getString(R.string.beginning))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        mMap.addMarker(new MarkerOptions()
                .position(endPoint)
                .title(getString(R.string.end))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        ArrayList<Lap> laps = mWorkout.getLaps();
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
