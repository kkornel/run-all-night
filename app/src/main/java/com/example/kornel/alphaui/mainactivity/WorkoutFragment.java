package com.example.kornel.alphaui.mainactivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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
import com.example.kornel.alphaui.weather.NetworkUtils;
import com.example.kornel.alphaui.weather.Weather;
import com.example.kornel.alphaui.weather.WeatherConsts;
import com.example.kornel.alphaui.weather.WeatherInfo;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import static com.example.kornel.alphaui.mainactivity.WeatherDetailsActivity.WEATHER_INFO_INTENT_EXTRAS;
import static com.example.kornel.alphaui.weather.WeatherInfo.CELSIUS;

public class WorkoutFragment extends Fragment implements WeatherInfoListener {
    private static final String TAG = "WorkoutFragment";

    public static final String WORKOUT_NAME_EXTRA_INTENT = "workout_name";

    public static final String WORKOUT_RESULT = "workout_result";
    public final int PICK_WORKOUT_REQUEST = 1;

    // Welcome CardView
    private CardView mWelcomeCardView;
    private TextView mWelcomeTextView;
    private TextView mLastTrainingTextView;

    // Weather CardView
    private ImageView mCurrentWeatherIconImageView;
    private TextView mCurrentWeatherTextView;
    private TextView mCurrentTempTextView;
    private TextView mCurrentTimeLocationTextView;
    private CardView mCurrentWeatherCardView;

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

    private Weather mWeather = Weather.getInstance(true);
    private WeatherInfo mWeatherInfo;

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
                searchByGPS();
                // searchByLatLon(52.399040, 16.949450);
            }
        });
        mWelcomeTextView = rootView.findViewById(R.id.welcomeTextView);
        mLastTrainingTextView = rootView.findViewById(R.id.lastTrainingTextView);

        mCurrentWeatherCardView = rootView.findViewById(R.id.currentWeatherCardView);
        mCurrentWeatherCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (WorkoutFragment.this.getActivity(), WeatherDetailsActivity.class);
                if (mWeatherInfo != null) {
                    intent.putExtra(WEATHER_INFO_INTENT_EXTRAS, mWeatherInfo);
                }
                startActivity(intent);
            }
        });
        mCurrentWeatherIconImageView = rootView.findViewById(R.id.currentWeatherIconImageView);
        mCurrentWeatherTextView = rootView.findViewById(R.id.currentWeatherDescriptionTextView);
        mCurrentTempTextView = rootView.findViewById(R.id.currentTemperatureTextView);
        mCurrentTimeLocationTextView = rootView.findViewById(R.id.currentTimeLocationTextView);

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

    @Override
    public void onStart() {
        super.onStart();

        mWeatherInfo = null;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        final String userUid = user.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = database.getReference();
        DatabaseReference usersRef = rootRef.child(Database.USERS);
        final DatabaseReference workoutsRef = rootRef.child(Database.WORKOUTS);

        MainActivityLog.d(userUid);
        MainActivityLog.d(rootRef.toString());

        ValueEventListener userInfoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String firstName = user.getFirstName();
                String lastWorkoutId = user.getLastWorkout();
                String welcomeMessage = "Cześć, " + firstName + "!";
                String noLastWorkoutDate = "Nie zrobiłeś jeszcze żadnego treningu, pora to zmienić!";

                mWelcomeTextView.setText(welcomeMessage);

                if (lastWorkoutId == null) {
                    mLastTrainingTextView.setText(noLastWorkoutDate);
                } else {
                    workoutsRef.child(userUid).child(user.getLastWorkout()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: " + dataSnapshot.toString());
                            String lastWorkoutDate = dataSnapshot.getValue(WorkoutGpsSummary.class).getDateStringPl();
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

        searchByGPS();

        // if (!NetworkUtils.isConnected(getContext())) {
        //     buildAlertMessageNoInternetConnection();
        // }
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
            mCurrentTimeLocationTextView.setText(weatherInfo.getAddress().getThoroughfare() + ", " + weatherInfo.getAddress().getLocality());

        } else {
            WeatherLog.e("gotWeatherInfo: NULL" + errorType);
        }

    }

    private void searchByGPS() {
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

    private void buildAlertMessageNoInternetConnection() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Aby w pełni korzystać z możliwości aplikacji wymagane jest połączenie z Internetem. Czy chcesz włączyć Internet?")
                .setCancelable(false)
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent=new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                        ComponentName cName = new ComponentName("com.android.phone","com.android.phone.Settings");
                        intent.setComponent(cName);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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
