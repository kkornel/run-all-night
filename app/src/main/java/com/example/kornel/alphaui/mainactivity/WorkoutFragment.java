package com.example.kornel.alphaui.mainactivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.example.kornel.alphaui.BuildConfig;
import com.example.kornel.alphaui.StartNonGpsWorkoutActivity;
import com.example.kornel.alphaui.gpsworkout.StartGpsWorkoutActivity;
import com.example.kornel.alphaui.gpsworkout.WorkoutGpsSummary;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.GpsBasedWorkout;
import com.example.kornel.alphaui.utils.IconUtils;
import com.example.kornel.alphaui.utils.User;
import com.example.kornel.alphaui.utils.WorkoutUtils;
import com.example.kornel.alphaui.weather.LocationUtils;
import com.example.kornel.alphaui.weather.NetworkUtils;
import com.example.kornel.alphaui.weather.Weather;
import com.example.kornel.alphaui.weather.WeatherConsts;
import com.example.kornel.alphaui.weather.WeatherInfo;
import com.example.kornel.alphaui.weather.WeatherInfoCompressed;
import com.example.kornel.alphaui.weather.WeatherInfoListener;
import com.example.kornel.alphaui.weather.WeatherLog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.example.kornel.alphaui.mainactivity.WeatherDetailsActivity.WEATHER_INFO_INTENT_EXTRAS;
import static com.example.kornel.alphaui.weather.WeatherInfo.CELSIUS;

public class WorkoutFragment extends Fragment implements WeatherInfoListener {
    private static final String TAG = "WorkoutFragment";

    public static final String WORKOUT_NAME_EXTRA_INTENT = "workout_name";
    public static final String WEATHER_INFO_EXTRA_INTENT = "weather_info";

    public static final String WORKOUT_RESULT = "workout_result";
    public final int PICK_WORKOUT_REQUEST = 1;

    private static final int REQUEST_CODE_FINE_LOCATION_PERMISSIONS = 53;

    // Welcome CardView
    private CardView mWelcomeCardView;
    private TextView mWelcomeTextView;
    private TextView mLastTrainingTextView;
    private TextView mNoInternetTextView;

    // Weather CardView
    private CardView mCurrentWeatherCardView;
    private ImageView mCurrentWeatherIconImageView;
    private TextView mCurrentWeatherTextView;
    private TextView mCurrentTempTextView;
    private TextView mCurrentTimeLocationTextView;
    private TextView mNoGpsTextView;

    // Workout CardView
    private CardView mWorkoutCardView;
    private ImageView mWorkoutImageView;
    private TextView mWorkoutNameTextView;

    // Music CardView
    private CardView mMusicCardView;
    private ImageView mMusicImageView;
    private TextView mSelectMusicTextView;

    private Button mStartWorkoutButton;

    private Weather mWeather = Weather.getInstance(true);
    private WeatherInfo mWeatherInfo;
    private WeatherInfoCompressed mWeatherInfoCompressed;

    private boolean mHasMusicChosen = false;

    public WorkoutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_workout, container, false);

        mWelcomeCardView = rootView.findViewById(R.id.welcomeCardView);
        mWelcomeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternetConnection();
            }
        });
        mWelcomeTextView = rootView.findViewById(R.id.welcomeTextView);
        mLastTrainingTextView = rootView.findViewById(R.id.lastTrainingTextView);
        mNoInternetTextView = rootView.findViewById(R.id.noInternetTextView);

        mCurrentWeatherCardView = rootView.findViewById(R.id.currentWeatherCardView);
        mCurrentWeatherCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasLocationPermissions()) {
                    requestLocationPermissions();
                } else {
                    if (!LocationUtils.isGpsEnabled(getContext())) {
                        setGpsLayout(false);
                        showNoGpsSnackBar();
                    } else {
                        setGpsLayout(true);
                        Intent intent = new Intent(WorkoutFragment.this.getActivity(), WeatherDetailsActivity.class);
                        if (mWeatherInfo != null) {
                            intent.putExtra(WEATHER_INFO_INTENT_EXTRAS, mWeatherInfo);
                        }
                        startActivity(intent);
                    }
                }
            }
        });
        mCurrentWeatherIconImageView = rootView.findViewById(R.id.currentWeatherIconImageView);
        mCurrentWeatherTextView = rootView.findViewById(R.id.currentWeatherDescriptionTextView);
        mCurrentTempTextView = rootView.findViewById(R.id.currentTemperatureTextView);
        mCurrentTimeLocationTextView = rootView.findViewById(R.id.currentTimeLocationTextView);
        mNoGpsTextView = rootView.findViewById(R.id.noGpsTextView);

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
                if (isSpotifyInstalled()) {
                    openSpotify();
                    mHasMusicChosen = true;
                } else {
                    openMusicPlayer();
                    mHasMusicChosen = true;
                }
            }
        });
        mMusicImageView = rootView.findViewById(R.id.musicImageView);
        mSelectMusicTextView = rootView.findViewById(R.id.selectMusicTextView);

        mStartWorkoutButton = rootView.findViewById(R.id.startActivityButton);
        mStartWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String workout = mWorkoutNameTextView.getText().toString();
                boolean isGpsBased = WorkoutUtils.isGpsBased(workout);

                if (isGpsBased) {
                    Intent intent = new Intent(getContext(), StartGpsWorkoutActivity.class);
                    intent.putExtra(WORKOUT_NAME_EXTRA_INTENT, workout);
                    intent.putExtra(WEATHER_INFO_EXTRA_INTENT, mWeatherInfoCompressed);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), StartNonGpsWorkoutActivity.class);
                    intent.putExtra(WORKOUT_NAME_EXTRA_INTENT, workout);
                    startActivity(intent);
                }
            }
        });

        if (isSpotifyInstalled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mMusicImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_spotify, getActivity().getApplicationContext().getTheme()));
            } else {
                mMusicImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_spotify));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mMusicImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_music_black_64dp, getActivity().getApplicationContext().getTheme()));
            } else {
                mMusicImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_music_black_64dp));
            }
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        mWeatherInfo = null;
        mWeatherInfoCompressed = null;

        if (!NetworkUtils.isConnected(getContext())) {
            mNoInternetTextView.setVisibility(View.VISIBLE);
            buildAlertMessageNoInternetConnection();
        } else {
            retrieveUserProfile();
        }

        if (!hasLocationPermissions()) {
            requestLocationPermissions();
        } else {
            if (!LocationUtils.isGpsEnabled(getContext())) {
                setGpsLayout(false);
                showNoGpsSnackBar();
            } else {
                setGpsLayout(true);
            }
        }
    }

    private void setGpsLayout(boolean hasGps) {
        if (hasGps) {
            mNoGpsTextView.setVisibility(View.GONE);
            searchByGPS();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mCurrentWeatherIconImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_umbrella, getActivity().getApplicationContext().getTheme()));
            } else {
                mMusicImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_umbrella));
            }
            mCurrentWeatherTextView.setText("");
            mCurrentTimeLocationTextView.setText("");
            mCurrentTempTextView.setText("");
            mNoGpsTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        if (mHasMusicChosen) {
            mSelectMusicTextView.setText("Wybrano");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void gotWeatherInfo(final WeatherInfo weatherInfo, Weather.ErrorType errorType) {
        if (weatherInfo != null) {
            mWeatherInfo = weatherInfo;

            WeatherLog.d("gotWeatherInfo: " + weatherInfo.toString());

            WeatherLog.d("====== CURRENT ======" + "\n" +
                    "date: " + weatherInfo.getCurrentConditionDate() + "\n" +
                    "weather: " + weatherInfo.getCurrentText() + "\n" +
                    "temperature in ºC: " + weatherInfo.getCurrentTempC() + "\n" +
                    "wind speed: " + weatherInfo.getWindSpeedMph() + "\n" +
                    "Humidity: " + weatherInfo.getAtmosphereHumidity() + "\n" +
                    "Pressure: " + weatherInfo.getAtmospherePressure() + "\n"
            );

            Picasso.get()
                    .load(weatherInfo.getCurrentConditionIconURL())
                    .into(mCurrentWeatherIconImageView);

            mCurrentTempTextView.setText(weatherInfo.getCurrentTempC() + CELSIUS);
            mCurrentWeatherTextView.setText(WeatherConsts.getConditionPlByCode(weatherInfo.getCurrentCode()));
            String fullAddress;
            String locality = weatherInfo.getAddress().getLocality();
            String thoroughfare = weatherInfo.getAddress().getThoroughfare();
            if (thoroughfare == null || thoroughfare.equals("")) {
                fullAddress = locality;
            } else {
                fullAddress = thoroughfare + ", " + locality;
            }

            mCurrentTimeLocationTextView.setText(fullAddress);

            mWeatherInfoCompressed = new WeatherInfoCompressed(
                    mWeatherInfo.getCurrentCode(),
                    mWeatherInfo.getCurrentTempC(),
                    mWeatherInfo.getCurrentConditionIconURL());
        } else {
            WeatherLog.e("gotWeatherInfo: NULL" + errorType);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_WORKOUT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra(WORKOUT_RESULT);
                mWorkoutNameTextView.setText(result);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mWorkoutImageView.setImageDrawable(getResources().getDrawable(IconUtils.getWorkoutIcon(result), getActivity().getApplicationContext().getTheme()));
                } else {
                    mWorkoutImageView.setImageDrawable(getResources().getDrawable(IconUtils.getWorkoutIcon(result)));
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MainActivityLog.d("requestLocationPermissions");
        switch (requestCode) {
            case REQUEST_CODE_FINE_LOCATION_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        // permission was granted, yay! Do the
                        // location-related task you need to do.
                        MainActivityLog.d("onRequestPermissionsResult - YE");
                        searchByGPS();
                    }
                } else {
                    MainActivityLog.d("onRequestPermissionsResult - NON");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Snackbar.make(
                            mStartWorkoutButton,
                            R.string.permission_denied_explanation,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.settings, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    boolean shouldProvideRationale =
                                            shouldShowRequestPermissionRationale(
                                                    Manifest.permission.ACCESS_FINE_LOCATION);

                                    // Provide an additional rationale to the user. This would happen if the user denied the
                                    // request previously, but didn't check the "Don't ask again" checkbox.
                                    if (shouldProvideRationale) {
                                        // Show an explanation to the user *asynchronously* -- don't block
                                        // this thread waiting for the user's response! After the user
                                        // sees the explanation, try again to request the permission.
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                REQUEST_CODE_FINE_LOCATION_PERMISSIONS);
                                    } else {
                                        // Build intent that displays the App settings screen.
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package",
                                                BuildConfig.APPLICATION_ID, null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    }
                                }
                            })
                            .show();
                }
        }
    }

    private boolean hasLocationPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            MainActivityLog.d("requestLocationPermissions - NON1");
            boolean shouldProvideRationale =
                    shouldShowRequestPermissionRationale(
                            Manifest.permission.ACCESS_FINE_LOCATION);

            // Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn't check the "Don't ask again" checkbox.
            if (shouldProvideRationale) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                Snackbar.make(
                        mStartWorkoutButton,
                        R.string.permission_rationale,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Request permission
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_CODE_FINE_LOCATION_PERMISSIONS);
                            }
                        })
                        .show();
            } else {
                // No explanation needed; request the permission
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
                // Request permission. It's possible this can be auto answered if device policy
                // sets the permission in a given state or the user denied the permission
                // previously and checked "Never ask again".
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_FINE_LOCATION_PERMISSIONS);
                MainActivityLog.d("requestLocationPermissions - NON2");
            }
        } else {
            searchByGPS();
            MainActivityLog.d("requestLocationPermissions - YE2");
        }
    }

    private void searchByGPS() {
        mNoGpsTextView.setVisibility(View.GONE);
        mWeather.setNeedDownloadIcons(true);
        mWeather.setTempUnit(Weather.TEMP_UNIT.CELSIUS);
        mWeather.queryWeatherByGPS(getActivity(), getContext(), this);
    }

    private void searchByLatLon(double lat, double lon) {
        mWeather.setNeedDownloadIcons(true);
        mWeather.setTempUnit(Weather.TEMP_UNIT.CELSIUS);
        mWeather.queryWeatherByLatLon(getContext(), lat, lon, this);
        // mWeather.queryWeatherByLatLon(getApplicationContext(), location, MainActivity.this);
    }

    private void checkInternetConnection() {
        if (!NetworkUtils.isConnected(getContext())) {
            mNoInternetTextView.setVisibility(View.VISIBLE);
            showNoInternetSnackBar();
        } else {
            retrieveUserProfile();
        }
    }

    private void showNoInternetSnackBar() {
        Snackbar.make(
                mStartWorkoutButton,
                "Wymagane jest połączenie z Internetem.",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkInternetConnection();
                    }
                })
                .show();
    }

    private void buildAlertMessageNoInternetConnection() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Aby w pełni korzystać z możliwości aplikacji wymagane jest połączenie z Internetem.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        checkInternetConnection();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void retrieveUserProfile() {
        mNoInternetTextView.setVisibility(View.GONE);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        final String userUid = user.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = database.getReference();
        DatabaseReference usersRef = rootRef.child(Database.USERS);
        final DatabaseReference workoutsRef = rootRef.child(Database.WORKOUTS);

        ValueEventListener userInfoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String firstName = user.getFirstName();
                String lastWorkoutId = user.getLastWorkout();
                String welcomeMessage = getString(R.string.hello) + ", " + firstName + "!";
                String noLastWorkoutDate = getString(R.string.no_workouts_yet);

                mWelcomeTextView.setText(welcomeMessage);

                if (lastWorkoutId == null) {
                    mLastTrainingTextView.setText(noLastWorkoutDate);
                } else {
                    workoutsRef.child(userUid).child(user.getLastWorkout()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // String lastWorkoutDate = dataSnapshot.getValue(WorkoutGpsSummary.class).getDateStringPl();
                            // mLastTrainingTextView.setText("Ostatni trening: " + lastWorkoutDate);
                            String lastWorkoutDate = dataSnapshot.getValue(WorkoutGpsSummary.class).gapBetweenWorkouts();
                            mLastTrainingTextView.setText("Ostatni trening: " + lastWorkoutDate);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                            throw databaseError.toException();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                throw databaseError.toException();
            }
        };
        usersRef.child(userUid).addListenerForSingleValueEvent(userInfoListener);
    }

    private void showNoGpsSnackBar() {
        Snackbar.make(
                mStartWorkoutButton,
                "Aby sprawdzić pogodę włącz GPS",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openLocationSettings();
                    }
                })
                .show();
    }

    private void openLocationSettings() {
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);

        // startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    private void openSpotify() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("spotify:album:0sNOF9WDwhWunNAHPD3Baj"));
        intent.putExtra(Intent.EXTRA_REFERRER,
                Uri.parse("android-app://" + getActivity().getPackageName()));
        // Uri.parse("android-app://" + context.getPackageName()));
        startActivity(intent);
    }

    private void openMusicPlayer() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_MUSIC);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
}
