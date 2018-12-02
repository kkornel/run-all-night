package com.example.kornel.alphaui.mainactivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.gpsworkout.PaceAdapter;
import com.example.kornel.alphaui.gpsworkout.WorkoutGpsSummary;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.IconUtils;
import com.example.kornel.alphaui.utils.Lap;
import com.example.kornel.alphaui.utils.LatLon;
import com.example.kornel.alphaui.utils.User;
import com.example.kornel.alphaui.utils.Utils;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.v4.internal.view.SupportMenuItem.SHOW_AS_ACTION_ALWAYS;
import static com.example.kornel.alphaui.gpsworkout.WorkoutSummaryActivity.MAX_CHARS_IN_EDIT_TEXT;
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
    private ImageButton mStatusEditButton;

    private TextView mPhotoLabel;
    private CardView mPhotoCardView;
    private ImageView mPhotoImageView;
    private ImageButton mPhotoDeleteButton;

    private CardView mPrivacyCardView;
    private TextView mPrivacyTextView;
    private Button mPrivacyChangeButton;

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
    private boolean mWorkoutEdited;
    private boolean mPhotoDeleted;

    private Menu mMenu;

    private DatabaseReference mRootRef;

    private String mUserUid;
    private String mWorkoutKey;
    private StorageReference mPicsRef;

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


        mStatusLabel = findViewById(R.id.statusLabel);
        mStatusCardView = findViewById(R.id.statusCardView);
        mStatusTextView = findViewById(R.id.statusTextView);
        mStatusEditButton = findViewById(R.id.editDescriptionButton);
        mStatusEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditStatus();
            }
        });

        if (mWorkoutGpsSummary.getStatus() == null || mWorkoutGpsSummary.getStatus().equals("")) {
            mStatusTextView.setText(getString(R.string.edit_to_add_description));
        } else {
            mStatusTextView.setText(mWorkoutGpsSummary.getStatus());
        }

        mPhotoLabel = findViewById(R.id.photoLabel);
        mPhotoCardView = findViewById(R.id.photoCardView);
        mPhotoImageView = findViewById(R.id.photoImageView);
        mPhotoDeleteButton = findViewById(R.id.deletePhotoButton);
        mPhotoDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeletePhoto();
            }
        });

        if (mWorkoutGpsSummary.getPicUrl() == null || mWorkoutGpsSummary.getPicUrl().equals("")) {
            mPhotoLabel.setVisibility(View.GONE);
            mPhotoCardView.setVisibility(View.GONE);
        } else {
            Picasso.get()
                    .load(mWorkoutGpsSummary.getPicUrl())
                    .into(mPhotoImageView);
        }

        mPrivacyCardView = findViewById(R.id.privacyCardView);
        mPrivacyTextView = findViewById(R.id.privacyTextView);
        mPrivacyTextView.setText(mWorkoutGpsSummary.getPrivacy() ? getString(R.string.only_me) :  getString(R.string.friends));
        mPrivacyChangeButton = findViewById(R.id.changePrivacyButton);
        mPrivacyChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangePrivacy();
            }
        });

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

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        mRootRef = database.getReference();

        mUserUid = user.getUid();
        mWorkoutKey = mWorkoutGpsSummary.getKey();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        mPicsRef = storageRef.child(Database.PICTURES);

        mPhotoDeleted = false;
        mWorkoutEdited = false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ArrayList<LatLon> path = mWorkoutGpsSummary.getPath();

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
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (mWorkoutEdited) {
            dismissChangesDialogShow();
        } else {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mWorkoutEdited) {
            dismissChangesDialogShow();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save_delete_workout, menu);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setShowAsAction(SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_workout:
                saveChanges();
                return true;
            case R.id.delete_workout:
                deleteWorkout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onDeletePhoto() {
        new AlertDialog.Builder(WorkoutGpsDetails.this)
                .setTitle(R.string.photo_delete_confirmation)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deletePhoto();
                        Toast.makeText(WorkoutGpsDetails.this, getString(R.string.photo_will_be_deleted_after_saving), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .create()
                .show();
    }

    private void deletePhoto() {
        mPhotoLabel.setVisibility(View.GONE);
        mPhotoCardView.setVisibility(View.GONE);
        mPhotoDeleted = true;
        onWorkoutEdited();
    }

    private void onEditStatus() {
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_description, null);
        final EditText editText = view.findViewById(R.id.editStatusEditText);
        if (mStatusTextView.getText().toString().equals(getString(R.string.edit_to_add_description))) {
            editText.setText("");
        } else {
            editText.setText(mStatusTextView.getText());
        }
        editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(MAX_CHARS_IN_EDIT_TEXT) });
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        new AlertDialog.Builder(WorkoutGpsDetails.this)
                .setView(view)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String newStatus = editText.getText().toString();
                        mStatusTextView.setText(newStatus);
                        mWorkoutGpsSummary.setStatus(newStatus);
                        onWorkoutEdited();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();

        Utils.hideKeyboard(this);
    }
    
    private void onChangePrivacy() {
        CharSequence[] cs = {getString(R.string.friends), getString(R.string.only_me)};
        final int checkedItemId = mWorkoutGpsSummary.getPrivacy() ? 1 :  0;
        new AlertDialog.Builder(WorkoutGpsDetails.this)
                .setTitle(R.string.pick_settings)
                .setSingleChoiceItems(cs, checkedItemId, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (checkedItemId != which) {
                            boolean isPrivate = which == 1 ? true : false;
                            mWorkoutGpsSummary.setPrivacy(isPrivate);
                            if (isPrivate) {
                                mPrivacyTextView.setText(getString(R.string.only_me));
                            } else {
                                mPrivacyTextView.setText(getString(R.string.friends));
                            }
                            onWorkoutEdited();
                        }

                        dialog.dismiss();
                    }})
                .create()
                .show();
    }

    private void onWorkoutEdited() {
        mWorkoutEdited = true;
        mMenu.getItem(0).setVisible(true);
    }

    private void dismissChangesDialogShow() {
        new AlertDialog.Builder(WorkoutGpsDetails.this)
                .setTitle(R.string.have_unsaved_changes)
                .setMessage(R.string.still_want_to_exit)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        onBackPressed();
                    }})
                .setNegativeButton(R.string.no, null).show();
    }


    private void saveChanges() {
        if (!NetworkUtils.isConnected(WorkoutGpsDetails.this)) {
            NetworkUtils.requestInternetConnection(mWorkoutCardView);
            return;
        }

        if (mPhotoDeleted) {
            deletePhotoFromDatabase();
            mPhotoImageView.setVisibility(View.GONE);
            mWorkoutGpsSummary.setPicUrl(null);
        }

        Map<String, Object> workoutValues = mWorkoutGpsSummary.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + Database.WORKOUTS + "/" + mUserUid + "/" + mWorkoutKey, workoutValues);

        mRootRef.updateChildren(childUpdates);

        mWorkoutEdited = false;

        Toast.makeText(this, getString(R.string.changes_saved), Toast.LENGTH_SHORT).show();

        finish();
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
                .setNegativeButton(R.string.no, null)
                .create()
                .show();
    }

    private void deletePhotoFromDatabase() {
        mPicsRef.child(mUserUid).child(mWorkoutKey + ".jpg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: photo deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "onCancelled: " + exception.getMessage());
            }
        });
    }

    private void delete() {
        if (!NetworkUtils.isConnected(WorkoutGpsDetails.this)) {
            NetworkUtils.requestInternetConnection(mWorkoutCardView);
            return;
        }

        final DatabaseReference userRef = mRootRef.child(Database.USERS);
        final DatabaseReference workoutRef = mRootRef.child(Database.WORKOUTS);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user.getLastWorkout().equals(mWorkoutKey)) {

                    final ArrayList<WorkoutGpsSummary> workouts = new ArrayList<>();
                    ValueEventListener workoutListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null) {
                                userRef.child(mUserUid).child(Database.LAST_WORKOUT).setValue(null);
                            } else {
                                WorkoutGpsSummary w1 = null;
                                WorkoutGpsSummary w2 = null;

                                for (DataSnapshot workout : dataSnapshot.getChildren()) {
                                    w1 = workout.getValue(WorkoutGpsSummary.class);

                                    if (w2 == null) {
                                        w2 = w1;
                                        w2.setKey(workout.getKey());
                                        continue;
                                    }

                                    if (w1.getDateMilliseconds() > w2.getDateMilliseconds()) {
                                        Log.d(TAG, w1.getDateMilliseconds() + " > " + w2.getDateMilliseconds());
                                        w2 = w1;
                                        w2.setKey(workout.getKey());
                                    }
                                }


                                userRef.child(mUserUid).child(Database.LAST_WORKOUT).setValue(w2.getKey());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                            throw databaseError.toException();
                        }
                    };

                    workoutRef.child(mUserUid).addListenerForSingleValueEvent(workoutListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                throw databaseError.toException();
            }
        };
        userRef.child(mUserUid).addListenerForSingleValueEvent(userListener);

        if (mWorkoutGpsSummary.getPicUrl() != null && !mWorkoutGpsSummary.getPicUrl().equals("")) {
            deletePhotoFromDatabase();
        } else {
            Log.d(TAG, "onSuccess: WORKOUT DOESNOT COTAING PICTURE");
        }

        workoutRef.child(mUserUid).child(mWorkoutKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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

    private void sortListByDate(List<WorkoutGpsSummary> list) {
        Collections.sort(list, new Comparator<WorkoutGpsSummary>() {
            public int compare(WorkoutGpsSummary o1, WorkoutGpsSummary o2) {
                if (o1.getWorkoutDate() == null || o2.getWorkoutDate() == null)
                    return 0;
                return o2.getWorkoutDate().compareTo(o1.getWorkoutDate());
            }
        });
    }
}
