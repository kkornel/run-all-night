package com.example.kornel.alphaui.gpsworkout;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.utils.OnNewActivityState;

import static com.example.kornel.alphaui.mainactivity.WorkoutFragment.WORKOUT_NAME_EXTRA_INTENT;
import static com.example.kornel.alphaui.utils.ServiceUtils.ACTION_START_FOREGROUND_SERVICE;
import static com.example.kornel.alphaui.utils.ServiceUtils.ACTION_STOP_FOREGROUND_SERVICE;
import static com.example.kornel.alphaui.utils.WorkoutUtils.WORKOUT_DETAILS_EXTRA_INTENT;

public class StartNonGpsWorkoutActivity extends AppCompatActivity implements OnNewActivityState {
    private static final int START_BUTTON_INDEX_IN_VIEW_FLIPPER = 0;
    private static final int PAUSE_BUTTON_INDEX_IN_VIEW_FLIPPER = 1;
    private static final int RESUME_FINISH_BUTTON_INDEX_IN_VIEW_FLIPPER = 2;

    // A reference to the service used to get location updates.
    private IndoorWorkoutService mService;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    private boolean mIsForegroundServiceRunning = false;

    // UI elements
    private TextView mTimeTextView;
    private ViewFlipper mViewFlipper;
    private Button mStartButton;
    private ImageButton mPauseButton;
    private Button mResumeButton;
    private Button mFinishButton;

    // Timer
    private Handler mTimeHandler;
    private Runnable mTimeRunnable;

    // Monitors the state of the connection to the service.
    // Defines callbacks for service binding, passed to bindService()
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            IndoorWorkoutService.IndoorWorkoutBinder binder = (IndoorWorkoutService.IndoorWorkoutBinder) service;
            mService = binder.getService();
            mBound = true;

            mService.setCallback(StartNonGpsWorkoutActivity.this);

            if (mService.isServiceRunning()) {
                updateButtons(mService.isTrainingPaused());
            }
            mTimeHandler.postDelayed(mTimeRunnable, 0);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_non_gps_workout);

        getSupportActionBar().hide();

        // Set up the ViewPager with the sections adapter.
        mTimeTextView = findViewById(R.id.timeTextView);
        mViewFlipper = findViewById(R.id.viewFlipper);
        mStartButton = findViewById(R.id.startButton);
        mPauseButton = findViewById(R.id.pauseButton);
        mResumeButton = findViewById(R.id.resumeButton);
        mFinishButton = findViewById(R.id.stopButton);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWorkout();
                mViewFlipper.setDisplayedChild(PAUSE_BUTTON_INDEX_IN_VIEW_FLIPPER);
            }
        });
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseWorkout();
                mViewFlipper.setDisplayedChild(RESUME_FINISH_BUTTON_INDEX_IN_VIEW_FLIPPER);
            }
        });
        mResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeWorkout();
                mViewFlipper.setDisplayedChild(PAUSE_BUTTON_INDEX_IN_VIEW_FLIPPER);
            }
        });
        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWorkout();
            }
        });

        mTimeHandler = new Handler();
        mTimeRunnable = new Runnable() {
            @Override
            public void run() {
                String time = mService.getTimeString();
                mTimeTextView.setText(time);
                mTimeHandler.postDelayed(this, 500);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, IndoorWorkoutService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            // Unregister
            mService.setCallback(null);
            unbindService(mConnection);
            mBound = false;
            mTimeHandler.removeCallbacks(mTimeRunnable);
        }
    }

    @Override
    public void updateButtons(boolean isPaused) {
        if (isPaused) {
            mViewFlipper.setDisplayedChild(RESUME_FINISH_BUTTON_INDEX_IN_VIEW_FLIPPER);
        } else {
            mViewFlipper.setDisplayedChild(PAUSE_BUTTON_INDEX_IN_VIEW_FLIPPER);
        }
    }

    private void startWorkout() {
        if (!mIsForegroundServiceRunning) {
            Intent intent = new Intent(StartNonGpsWorkoutActivity.this, IndoorWorkoutService.class);
            intent.setAction(ACTION_START_FOREGROUND_SERVICE);
            intent.putExtra(WORKOUT_NAME_EXTRA_INTENT, getIntent().getStringExtra(WORKOUT_NAME_EXTRA_INTENT));
            startService(intent);
            mIsForegroundServiceRunning = true;
        }
    }

    private void pauseWorkout() {
        mService.pauseWorkout();
    }

    private void resumeWorkout() {
        mService.resumeWorkout();
    }

    private void finishWorkout() {
        // Stop service
        Intent intent = new Intent(this, IndoorWorkoutService.class);
        intent.setAction(ACTION_STOP_FOREGROUND_SERVICE);
        startService(intent);
        mIsForegroundServiceRunning = false;

        Intent summaryActivity = new Intent(this, WorkoutNonGpsSummaryActivity.class);
        WorkoutSummary workoutSummary = mService.getWorkOutSummary();
        summaryActivity.putExtra(WORKOUT_DETAILS_EXTRA_INTENT, workoutSummary);
        startActivity(summaryActivity);
    }

    @Override
    public void onBackPressed() {
        if (!mService.isServiceRunning()) {
            super.onBackPressed();
        }
    }
}
