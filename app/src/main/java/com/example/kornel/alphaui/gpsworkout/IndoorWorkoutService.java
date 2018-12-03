package com.example.kornel.alphaui.gpsworkout;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.kornel.alphaui.utils.NotificationUtils;
import com.example.kornel.alphaui.utils.OnNewActivityState;

import static com.example.kornel.alphaui.mainactivity.WorkoutFragment.WORKOUT_NAME_EXTRA_INTENT;
import static com.example.kornel.alphaui.utils.NotificationUtils.ACTION_PAUSE_WORKOUT;
import static com.example.kornel.alphaui.utils.NotificationUtils.ACTION_RESUME_WORKOUT;
import static com.example.kornel.alphaui.utils.NotificationUtils.MOON_RUNNER_WORKOUT_NOTIFICATION_ID;
import static com.example.kornel.alphaui.utils.ServiceUtils.ACTION_START_FOREGROUND_SERVICE;
import static com.example.kornel.alphaui.utils.ServiceUtils.ACTION_STOP_FOREGROUND_SERVICE;

public class IndoorWorkoutService extends Service {
    private static final String TAG = "IndoorWorkoutService";

    // Binder given to clients
    private final IBinder mBinder = new IndoorWorkoutService.IndoorWorkoutBinder();

    // For button state update
    private OnNewActivityState mButtonCallback;

    private Handler mNotificationHandler;
    private Runnable mNotificationRunnable;

    private CurrentIndoorWorkout mCurrentIndoorWorkout;

    private boolean mIsTrainingPaused;
    private boolean mDidComeFromNotification;
    private boolean mIsServiceRunning;

    public IndoorWorkoutService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        mIsTrainingPaused = true;
        mDidComeFromNotification = false;
        mIsServiceRunning = false;

        setupNotifications();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        switch (action) {
            case ACTION_START_FOREGROUND_SERVICE:
                String workoutName = intent.getStringExtra(WORKOUT_NAME_EXTRA_INTENT);
                startForegroundService(workoutName);
                Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_SHORT).show();
                break;

            case ACTION_STOP_FOREGROUND_SERVICE:
                stopForegroundService();
                Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_SHORT).show();
                break;

            case ACTION_RESUME_WORKOUT:
                mDidComeFromNotification = true;
                resumeWorkout();
                break;

            case ACTION_PAUSE_WORKOUT:
                mDidComeFromNotification = true;
                pauseWorkout();
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForegroundService(String workoutName) {
        // Create new workout
        mCurrentIndoorWorkout = new CurrentIndoorWorkout(workoutName);

        // Build the notification.
        Notification notification = NotificationUtils.createIndoorNotification(getApplicationContext(), IndoorWorkoutService.class);

        // Start foreground service.
        startForeground(MOON_RUNNER_WORKOUT_NOTIFICATION_ID, notification);

        mIsServiceRunning = true;
        mIsTrainingPaused = false;

        startNotificationHandler();
    }

    private void stopForegroundService() {
        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();

        mIsServiceRunning = false;
    }

    public void pauseWorkout() {
        mIsTrainingPaused = true;
        mCurrentIndoorWorkout.pauseStopwatch();
        stopNotificationHandler();
        NotificationUtils.toggleActionButtons(getApplicationContext());

        if (mDidComeFromNotification) {
            if (mButtonCallback != null) {
                mButtonCallback.updateButtons(true);
            }
            mDidComeFromNotification = false;
        }
    }

    public void resumeWorkout() {
        mIsTrainingPaused = false;
        mCurrentIndoorWorkout.startStopwatch();
        startNotificationHandler();
        NotificationUtils.toggleActionButtons(getApplicationContext());

        if (mDidComeFromNotification) {
            if (mButtonCallback != null) {
                mButtonCallback.updateButtons(false);
            }
            mDidComeFromNotification = false;
        }
    }

    private void setupNotifications() {
        mNotificationHandler = new Handler();
        mNotificationRunnable = new Runnable() {
            @Override
            public void run() {
                String ch187 = Character.toString((char) 187);

                String message = mCurrentIndoorWorkout.getWorkoutName() + "  "
                        + ch187 + "  " + getTimeString();

                NotificationUtils.updateNotification(message);
                mNotificationHandler.postDelayed(this, 500);
            }
        };
    }

    private void startNotificationHandler() {
        mNotificationHandler.postDelayed(mNotificationRunnable, 0);
    }

    private void stopNotificationHandler() {
        mNotificationHandler.removeCallbacks(mNotificationRunnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        return mBinder;
    }

    // Class used for the client Binder. Because we know this service always runs
    // in the same process as its clients, we don't need to deal with IPC.
    public class IndoorWorkoutBinder extends Binder {
        IndoorWorkoutService getService() {
            // Return this instance of LocationTrackingBinder so clients can call public methods
            return IndoorWorkoutService.this;
        }
    }

    /**
     * Methods for clients.
     */

    public void setCallback(OnNewActivityState callback) {
        mButtonCallback = callback;
    }

    public boolean isTrainingPaused() {
        return mIsTrainingPaused;
    }

    public boolean isServiceRunning() {
        return mIsServiceRunning;
    }

    public WorkoutGpsSummary getWorkOutSummary() {
        WorkoutGpsSummary workoutGpsSummary = new WorkoutGpsSummary(
                mCurrentIndoorWorkout.getDate(),
                mCurrentIndoorWorkout.getWorkoutName(),
                mCurrentIndoorWorkout.getDurationString());
        return workoutGpsSummary;
    }

    public String getTimeString() {
        if (mCurrentIndoorWorkout != null) {
            return mCurrentIndoorWorkout.getDurationString();
        } else {
            return "0:00";
        }
    }
}