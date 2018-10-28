package com.example.kornel.alphaui;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkoutFragment extends Fragment {
    private static final String TAG = "WorkoutFragment";

    public static final String ACTIVITY_RESULT = "result";
    public final int PICK_ACTIVITY_REQUEST = 1;

    // Welcome CardView
    private TextView mWelcomeTextView;
    private TextView mLastTrainingTextView;

    // Weather CardView
    private CardView mWeatherCardView;
    private ImageView mWeatherImageView;
    private TextView mWeatherTextView;
    private TextView mWeatherInfoTextView;
    private TextView mTempTextView;

    // Activity CardView
    private CardView mActivityCardView;
    private ImageView mActivityImageView;
    private TextView mActivityNameTextView;

    // Music CardView
    private CardView mMusicCardView;
    private ImageView mMusicImageView;
    private TextView mSelectMusicTextView;

    private Button mStartActivityButton;

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

        mActivityCardView = rootView.findViewById(R.id.activityCardView);
        mActivityCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChooseSportActivity.class);
                startActivityForResult(intent, PICK_ACTIVITY_REQUEST);
            }
        });
        mActivityImageView = rootView.findViewById(R.id.activityImageView);
        mActivityNameTextView = rootView.findViewById(R.id.activityNameTextView);

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

        mStartActivityButton = rootView.findViewById(R.id.startActivityButton);
        mStartActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "isSpotifyInstalled: " + isSpotifyInstalled());
                Log.d(TAG, "isGpsEnabled: " + isGpsEnabled());
                Log.d(TAG, "isInternetConnection: " + isInternetConnection());
            }
        });
        
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_ACTIVITY_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra(ACTIVITY_RESULT);
                mActivityNameTextView.setText(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
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
