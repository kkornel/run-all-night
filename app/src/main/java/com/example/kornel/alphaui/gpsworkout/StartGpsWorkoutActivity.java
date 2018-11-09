package com.example.kornel.alphaui.gpsworkout;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.kornel.alphaui.BuildConfig;
import com.example.kornel.alphaui.R;
import com.google.android.gms.maps.model.LatLng;
import com.pixelcan.inkpageindicator.InkPageIndicator;

import java.util.List;

import static com.example.kornel.alphaui.mainactivity.WorkoutFragment.WORKOUT_NAME_EXTRA_INTENT;

public class StartGpsWorkoutActivity extends AppCompatActivity implements
        LocationTrackingService.OnNewActivityState,
        MainDetailsFragment.OnDetailsChanged,
        MapsFragment.OnMapUpdate {
    private static final String TAG = "StartGpsWorkoutActivity";

    public static final String WORKOUT_DETAILS_EXTRA_INTENT = "workout_summary";

    private static final int REQUEST_CODE_PERMISSIONS_FINE_LOCATION = 34;

    private static final int START_BUTTON_INDEX_IN_VIEW_FLIPPER = 0;
    private static final int PAUSE_BUTTON_INDEX_IN_VIEW_FLIPPER = 1;
    private static final int RESUME_FINISH_BUTTON_INDEX_IN_VIEW_FLIPPER = 2;

    private static final int MAPS_FRAGMENT_INDEX_IN_VIEW_PAGER = 0;
    private static final int MAIN_DETAIL_FRAGMENT_INDEX_IN_VIEW_PAGER = 1;
    private static final int PACE_DETAIL_FRAGMENT_INDEX_IN_VIEW_PAGER = 2;

    // A reference to the service used to get location updates.
    private LocationTrackingService mService;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    private boolean mIsForegroundServiceRunning = false;

    private MapsFragment mMapsFragment;
    private MainDetailsFragment mMainDetailsFragment;
    private PaceDetailsFragment mPaceDetailsFragment;

    // UI elements
    private ViewFlipper mViewFlipper;
    private Button mStartButton;
    private ImageButton mPauseButton;
    private Button mResumeButton;
    private Button mFinishButton;

    // The ViewPager that will host the section contents.
    private ViewPager mViewPager;

    // The PagerAdapter that will provide fragments for each of the sections.
    // We use a FragmentPagerAdapter derivative, which will keep every loaded fragment in memory.
    // If this becomes too memory intensive, it may be best to switch to a FragmentStatePagerAdapter.
    private SectionsPagerAdapter mSectionsPagerAdapter;

    // Monitors the state of the connection to the service.
    // Defines callbacks for service binding, passed to bindService()
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.d(TAG, "onServiceConnected: ");

            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocationTrackingService.LocationTrackingBinder binder = (LocationTrackingService.LocationTrackingBinder) service;
            mService = binder.getService();
            mBound = true;

            mService.setCallback(StartGpsWorkoutActivity.this);

            if (mService.isServiceRunning()) {
                Log.d(TAG, "onServiceConnected: " + mService.isTrainingPaused());
                mTimeHandler.postDelayed(mTimeRunnable, 0);
                updateButtons(mService.isTrainingPaused());
            }
            // updateButtons(mService.isTrainingPaused());

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "onServiceDisconnected: ");
            mBound = false;
        }
    };

    private Handler mTimeHandler;
    private Runnable mTimeRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_gpsworkout);

        getSupportActionBar().hide();

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.viewPager);
        mViewFlipper = findViewById(R.id.viewFlipper);
        mStartButton = findViewById(R.id.startButton);
        mPauseButton = findViewById(R.id.pauseButton);
        mResumeButton = findViewById(R.id.resumeButton);
        mFinishButton = findViewById(R.id.stopButton);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasLocationPermissions()) {
                    requestPermissions();
                } else if (!isGpsEnabled()) {
                    requestGpsEnabled();
                } else {
                    startWorkout();
                    // mViewFlipper.showNext();
                    mViewFlipper.setDisplayedChild(PAUSE_BUTTON_INDEX_IN_VIEW_FLIPPER);
                }
            }
        });
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseWorkout();
                // mViewFlipper.showNext();
                mViewFlipper.setDisplayedChild(RESUME_FINISH_BUTTON_INDEX_IN_VIEW_FLIPPER);
            }
        });
        mResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeWorkout();
                // mViewFlipper.showPrevious();
                mViewFlipper.setDisplayedChild(PAUSE_BUTTON_INDEX_IN_VIEW_FLIPPER);
            }
        });
        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWorkout();
                Toast.makeText(StartGpsWorkoutActivity.this, "Finished", Toast.LENGTH_LONG).show();
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        InkPageIndicator pageIndicatorView = findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {/*empty*/}

            @Override
            public void onPageSelected(int position) {
                // pageIndicatorView.setSelection(position);

                if (position == 0 || position == 2) {
                    Log.d(TAG, "onPageSelected: ");
                    TranslateAnimation animate = new TranslateAnimation(0, 0, 0, mViewFlipper.getHeight());
                    animate.setDuration(100);
                    animate.setFillAfter(true);
                    mViewFlipper.startAnimation(animate);
                    mViewFlipper.setVisibility(View.GONE);
                } else {
                    if (mViewFlipper.getVisibility() == View.GONE) {
                        TranslateAnimation animate = new TranslateAnimation(mViewFlipper.getWidth(), 0, 0, 0);
                        // TranslateAnimation animate = new TranslateAnimation(0,0,0,(float)(-mViewFlipper.getHeight()/2.3));
                        animate.setDuration(300);
                        // animate.setFillAfter(true);
                        mViewFlipper.startAnimation(animate);
                        mViewFlipper.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {/*empty*/}
        });

        mViewPager.setCurrentItem(MAIN_DETAIL_FRAGMENT_INDEX_IN_VIEW_PAGER);

        // requestPermissions();

        if (!hasLocationPermissions()) {
            requestPermissions();
        } else if (!isGpsEnabled()) {
            requestGpsEnabled();
        }




        mTimeHandler = new Handler();
        mTimeRunnable = new Runnable() {
            @Override
            public void run() {
                int currentActiveFragmentIdx = mViewPager.getCurrentItem();

                // switch (currentActiveFragmentIdx) {
                //     case MAPS_FRAGMENT_INDEX_IN_VIEW_PAGER:
                //         break;
                //
                //     case MAIN_DETAIL_FRAGMENT_INDEX_IN_VIEW_PAGER:
                //         mMainDetailsFragment.setTime(mService.getTimeString());
                //         mMainDetailsFragment.setDistance(mService.getDistanceString());
                //
                //         break;
                //
                //     case PACE_DETAIL_FRAGMENT_INDEX_IN_VIEW_PAGER:
                //         mPaceDetailsFragment.setTime(mService.getTimeString());
                //
                //         break;
                // }

                mMainDetailsFragment.setTime(mService.getTimeString());
                mMainDetailsFragment.setDistance(mService.getDistanceString());
                mPaceDetailsFragment.setTime(mService.getTimeString());

                mTimeHandler.postDelayed(this, 500);
            }
        };
        mViewFlipper.setDisplayedChild(START_BUTTON_INDEX_IN_VIEW_FLIPPER);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        Intent intent = new Intent(this, LocationTrackingService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();

        // if (!hasLocationPermissions()) {
        //     requestPermissions();
        // }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
        if (mBound) {
            // unregister
            mService.setCallback(null);
            unbindService(mConnection);
            mBound = false;
            mTimeHandler.removeCallbacks(mTimeRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void updateButtons(boolean isPaused) {
        Log.d(TAG, "updateButtons: " + isPaused);
        if (isPaused) {
            mViewFlipper.setDisplayedChild(RESUME_FINISH_BUTTON_INDEX_IN_VIEW_FLIPPER);
        } else {
            mViewFlipper.setDisplayedChild(PAUSE_BUTTON_INDEX_IN_VIEW_FLIPPER);
        }
    }

    private void startWorkout() {
        Log.d(TAG, "startWorkout: ");

        if (!mIsForegroundServiceRunning) {
            Intent intent = new Intent(StartGpsWorkoutActivity.this, LocationTrackingService.class);
            intent.setAction(LocationTrackingService.ACTION_START_FOREGROUND_SERVICE);
            intent.putExtra(WORKOUT_NAME_EXTRA_INTENT, getIntent().getStringExtra(WORKOUT_NAME_EXTRA_INTENT));
            startService(intent);
            mIsForegroundServiceRunning = true;
        }
    }

    private void pauseWorkout() {
        Log.d(TAG, "pauseWorkout: ");
        mService.pauseWorkout();
    }

    private void resumeWorkout() {
        Log.d(TAG, "resumeWorkout: ");
        mService.resumeWorkout();
    }

    private void finishWorkout() {
        // Stop service
        Intent intent = new Intent(this, LocationTrackingService.class);
        intent.setAction(LocationTrackingService.ACTION_STOP_FOREGROUND_SERVICE);
        startService(intent);
        mIsForegroundServiceRunning = false;

        Intent summaryActivity = new Intent(this, WorkoutSummary.class);
        summaryActivity.putExtra(WORKOUT_DETAILS_EXTRA_INTENT, mService.getWorkOutSummary());
        startActivity(summaryActivity);
    }

    @Override
    public void onFabClicked() {
        mViewPager.setCurrentItem(MAIN_DETAIL_FRAGMENT_INDEX_IN_VIEW_PAGER);
    }

    @Override
    public List<LatLng> onMapUpdate() {
        if (mService != null) {
            return mService.getPath();
        } else {
            return null;
        }
    }

    @Override
    public Location getLastLocation() {
        if (mService != null) {
            return mService.getLastLocation();
        } else {
            return null;
        }
    }

    @Override
    public String getTimeString() {
        if (mService != null) {
            return mService.getTimeString();
        } else {
            return "0:00";
        }
    }

    @Override
    public String getDistanceString() {
        if (mService != null) {
            return mService.getDistanceString();
        } else {
            return "0.00";
        }
    }

    // A FragmentPagerAdapter that returns a fragment corresponding to one of the sections/tabs/pages.
    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                // MapsFragment mapsFragment = new MapsFragment();
                mMapsFragment = new MapsFragment();
                mMapsFragment.setCallback(StartGpsWorkoutActivity.this);
                return mMapsFragment;
            } else if (position == 1) {
                // MainDetailsFragment mainDetailsFragment = new MainDetailsFragment();
                mMainDetailsFragment = new MainDetailsFragment();
                // mainDetailsFragment.setTime("12:12");
                // mainDetailsFragment.setDistance("31:11");
                // mainDetailsFragment.setCurrent("4:11");
                // mainDetailsFragment.setAvg("3:11");
                mMainDetailsFragment.setCallBack(StartGpsWorkoutActivity.this);
                return mMainDetailsFragment;
            } else {
                // PaceDetailsFragment paceDetailsFragment = new PaceDetailsFragment();
                mPaceDetailsFragment = new PaceDetailsFragment();
                // paceDetailsFragment.setTime("12:12");
                // paceDetailsFragment.setAvg("3:32");
                // paceDetailsFragment.setCurrent("2:11");
                return mPaceDetailsFragment;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    private boolean isGpsEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void requestGpsEnabled() {
        Snackbar.make(
                mViewFlipper,
                R.string.enable_gps,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent viewIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(viewIntent);
                    }
                })
                .show();
    }

    private boolean hasLocationPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?

            boolean shouldProvideRationale =
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION);

            // Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn't check the "Don't ask again" checkbox.
            if (shouldProvideRationale) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(
                        mViewFlipper,
                        R.string.permission_rationale,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Request permission
                                ActivityCompat.requestPermissions(StartGpsWorkoutActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_CODE_PERMISSIONS_FINE_LOCATION);
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
                ActivityCompat.requestPermissions(StartGpsWorkoutActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_PERMISSIONS_FINE_LOCATION);
            }
        } else {
            // Permission has already been granted
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        // permission was granted, yay! Do the
                        // location-related task you need to do.
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Snackbar.make(
                            mViewFlipper,
                            R.string.permission_denied_explanation,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.settings, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    boolean shouldProvideRationale =
                                            ActivityCompat.shouldShowRequestPermissionRationale(StartGpsWorkoutActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);

                                    // Provide an additional rationale to the user. This would happen if the user denied the
                                    // request previously, but didn't check the "Don't ask again" checkbox.
                                    if (shouldProvideRationale) {
                                        // Show an explanation to the user *asynchronously* -- don't block
                                        // this thread waiting for the user's response! After the user
                                        // sees the explanation, try again to request the permission.
                                        ActivityCompat.requestPermissions(StartGpsWorkoutActivity.this,
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                REQUEST_CODE_PERMISSIONS_FINE_LOCATION);
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


}
