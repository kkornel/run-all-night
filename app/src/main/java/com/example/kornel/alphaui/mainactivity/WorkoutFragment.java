package com.example.kornel.alphaui.mainactivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.gpsworkout.StartGpsWorkoutActivity;
import com.example.kornel.alphaui.gpsworkout.WorkoutGpsSummary;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.GpsBasedWorkout;
import com.example.kornel.alphaui.utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WorkoutFragment extends Fragment {
    private static final String TAG = "WorkoutFragment";

    public static final String WORKOUT_NAME_EXTRA_INTENT = "workout_name";

    public static final String WORKOUT_RESULT = "workout_result";
    public final int PICK_WORKOUT_REQUEST = 1;

    // Welcome CardView
    private TextView mWelcomeTextView;
    private TextView mLastTrainingTextView;

    // Weather CardView
    private CardView mWeatherCardView;
    private ImageView mWeatherImageView;
    private TextView mWeatherTextView;
    private TextView mWeatherInfoTextView;
    private TextView mTempTextView;

    // Workout CardView
    private CardView mWorkoutCardView;
    private ImageView mWorkoutImageView;
    private TextView mWorkoutNameTextView;

    // Music CardView
    private CardView mMusicCardView;
    private ImageView mMusicImageView;
    private TextView mSelectMusicTextView;

    private Button mStartWorkoutButton;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public WorkoutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_workout, container, false);
        
        mWelcomeTextView = rootView.findViewById(R.id.welcomeTextView);
        mLastTrainingTextView = rootView.findViewById(R.id.lastTrainingTextView);

        mWeatherCardView = rootView.findViewById(R.id.weatherCardView);
        mWeatherImageView = rootView.findViewById(R.id.weatherImageView);
        mWeatherTextView = rootView.findViewById(R.id.weatherTextView);
        mWeatherInfoTextView = rootView.findViewById(R.id.weatherInfoTextView);
        mTempTextView = rootView.findViewById(R.id.tempTextView);

        mWorkoutCardView = rootView.findViewById(R.id.activityCardView);
        mWorkoutCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChooseWorkoutActivity.class);
                startActivityForResult(intent, PICK_WORKOUT_REQUEST);
            }
        });
        mWorkoutImageView = rootView.findViewById(R.id.activityImageView);
        mWorkoutNameTextView = rootView.findViewById(R.id.activityNameTextView);

        mMusicCardView = rootView.findViewById(R.id.musicCardView);
        mMusicCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_MUSIC);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        mMusicImageView = rootView.findViewById(R.id.musicImageView);
        mSelectMusicTextView = rootView.findViewById(R.id.selectMusicTextView);

        mWeatherCardView = rootView.findViewById(R.id.weatherCardView);

        mStartWorkoutButton = rootView.findViewById(R.id.startActivityButton);
        mStartWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "isSpotifyInstalled: " + isSpotifyInstalled());
                Log.d(TAG, "isGpsEnabled: " + isGpsEnabled());
                Log.d(TAG, "isInternetConnection: " + isInternetConnection());

                String workout = mWorkoutNameTextView.getText().toString();
                boolean isGpsBased = isGpsBased(workout);

                if (isGpsBased) {
                    Intent intent = new Intent(getContext(), StartGpsWorkoutActivity.class);
                    intent.putExtra(WORKOUT_NAME_EXTRA_INTENT, workout);
                    startActivity(intent);
                } else {
                    // Intent intent = new Intent(getContext(), StartNonWorkoutActivity.class);
                    // intent.putExtra(WORKOUT_NAME_EXTRA_INTENT, workout);
                    // startActivity(intent);
                    Log.d(TAG, "onClick: non");
                    buildAlertMessageNoGps();
                }
            }
        });

        return rootView;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        final String userUid = mUser.getUid();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference(Database.USERS);

        ValueEventListener userInfoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.toString());
                // TODO: it's broken cause I've overwritten database
                User user = dataSnapshot.getValue(User.class);
                mWelcomeTextView.setText(user.getFirstName());

                database.getReference().child("workouts").child(userUid).child(user.getLastWorkout()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: " + dataSnapshot.toString());
                        mLastTrainingTextView.setText(dataSnapshot.getValue(WorkoutGpsSummary.class).getDate().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                        throw databaseError.toException();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                throw databaseError.toException();
            }
        };

        // usersRef.child(userUid).addListenerForSingleValueEvent(userInfoListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_WORKOUT_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra(WORKOUT_RESULT);
                mWorkoutNameTextView.setText(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }

    private boolean isGpsBased(String workout) {
        for (GpsBasedWorkout gpsWorkout : GpsBasedWorkout.values()) {
            if (gpsWorkout.toString().equals(workout)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSpotifyInstalled() {
        PackageManager pm = getActivity().getPackageManager();
        boolean isSpotifyInstalled;
        try {
            pm.getPackageInfo("com.spotify.music", 0);
            isSpotifyInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            isSpotifyInstalled = false;
        }
        return isSpotifyInstalled;
    }

    private void openSpotify() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("spotify:album:0sNOF9WDwhWunNAHPD3Baj"));
        intent.putExtra(Intent.EXTRA_REFERRER,
                Uri.parse("android-app://" + getActivity().getPackageName()));
        // Uri.parse("android-app://" + context.getPackageName()));
        startActivity(intent);
    }

    private boolean isGpsEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }

    private boolean isInternetConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        @SuppressLint("MissingPermission")
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
