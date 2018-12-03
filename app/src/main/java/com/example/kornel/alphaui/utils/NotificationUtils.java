package com.example.kornel.alphaui.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.kornel.alphaui.gpsworkout.LocationTrackingService;
import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.gpsworkout.StartGpsWorkoutActivity;

public class NotificationUtils {
    public static final int MOON_RUNNER_WORKOUT_NOTIFICATION_ID = 1138;

    private static final int MOON_RUNNER_PENDING_INTENT_ID = 3417;
    private static final String MOON_RUNNER_CHANNEL_ID = "moon_runner_notification_channel_01";

    private static final int ACTION_RESUME_PENDING_INTENT_ID = 3418;
    public static final String ACTION_RESUME_WORKOUT = "resume_sport_activity";

    private static final int ACTION_PAUSE_PENDING_INTENT_ID = 3419;
    public static final String ACTION_PAUSE_WORKOUT = "pause_sport_activity";

    private static NotificationManager mNotificationManager;
    private static NotificationCompat.Builder mNotificationBuilder;

    private static NotificationCompat.Action mResumeAction;
    private static NotificationCompat.Action mPauseAction;

    public static Notification createNotification(Context context) {
        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        mResumeAction = resumeWorkout(context);
        mPauseAction = pauseWorkout(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    MOON_RUNNER_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        mNotificationBuilder =
                new NotificationCompat.Builder(context, MOON_RUNNER_CHANNEL_ID)
                        .setColor(ContextCompat.getColor(context, R.color.secondaryLightColor))
                        .setSmallIcon(R.drawable.ic_brightness_3_black_24dp)
                        .setLargeIcon(largeIcon(context))
                        .addAction(mPauseAction)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setOnlyAlertOnce(true)
                        .setContentIntent(contentIntent(context));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mNotificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        return mNotificationBuilder.build();
    }

    public static Notification createIndoorNotification(Context context, Class<?> cls) {
        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        mResumeAction = resumeIndoorWorkout(context, cls);
        mPauseAction = pauseIndoorWorkout(context, cls);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    MOON_RUNNER_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        mNotificationBuilder =
                new NotificationCompat.Builder(context, MOON_RUNNER_CHANNEL_ID)
                        .setColor(ContextCompat.getColor(context, R.color.secondaryLightColor))
                        .setSmallIcon(R.drawable.ic_brightness_3_black_24dp)
                        .setLargeIcon(largeIcon(context))
                        .addAction(mPauseAction)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setOnlyAlertOnce(true)
                        .setContentIntent(contentIntent(context));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mNotificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        return mNotificationBuilder.build();
    }

    private static NotificationCompat.Action resumeIndoorWorkout(Context context, Class<?> cls) {
        Intent resumeSportActivityIntent = new Intent(context, cls);
        resumeSportActivityIntent.setAction(ACTION_RESUME_WORKOUT);

        PendingIntent resumeSportActivityPendingIntent = PendingIntent.getService(
                context,
                ACTION_RESUME_PENDING_INTENT_ID,
                resumeSportActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action resumeAction = new NotificationCompat.Action(
                R.drawable.ic_play_arrow_black_24dp,
                context.getString(R.string.resume_action_button),
                resumeSportActivityPendingIntent);

        return resumeAction;
    }

    private static NotificationCompat.Action pauseIndoorWorkout(Context context, Class<?> cls) {
        Intent pauseSportActivityIntent = new Intent(context, cls);
        pauseSportActivityIntent.setAction(ACTION_PAUSE_WORKOUT);

        PendingIntent pauseSportActivityPendingIntent = PendingIntent.getService(
                context,
                ACTION_PAUSE_PENDING_INTENT_ID,
                pauseSportActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action pauseAction = new NotificationCompat.Action(
                R.drawable.ic_pause_black_24dp,
                context.getString(R.string.pause_action_button),
                pauseSportActivityPendingIntent);

        return pauseAction;
    }

    public static void toggleActionButtons(Context context) {
        if (mNotificationBuilder.mActions.contains(mPauseAction)) {
            removePauseAction();
            addResumeAction();
            mNotificationBuilder.setContentText(context.getString(R.string.pause_action_content_text));
            notifyManager();
        } else {
            removeResumeAction();
            addPauseAction();
            mNotificationBuilder.setContentText(null);
            notifyManager();
        }
    }

    public static void updateNotification(String message) {
        mNotificationBuilder.setContentTitle(message);
        notifyManager();
    }

    public static void notifyManager() {
        mNotificationManager.notify(MOON_RUNNER_WORKOUT_NOTIFICATION_ID, mNotificationBuilder.build());
    }

    private static void addResumeAction() {
        mNotificationBuilder.addAction(mResumeAction);
    }

    private static void addPauseAction() {
        mNotificationBuilder.addAction(mPauseAction);
    }

    private static void removeResumeAction() {
        mNotificationBuilder.mActions.remove(mResumeAction);
    }

    private static void removePauseAction() {
        mNotificationBuilder.mActions.remove(mPauseAction);
    }

    public static void clearAllNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private static NotificationCompat.Action resumeWorkout(Context context) {
        Intent resumeSportActivityIntent = new Intent(context, LocationTrackingService.class);
        resumeSportActivityIntent.setAction(ACTION_RESUME_WORKOUT);

        PendingIntent resumeSportActivityPendingIntent = PendingIntent.getService(
                context,
                ACTION_RESUME_PENDING_INTENT_ID,
                resumeSportActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action resumeAction = new NotificationCompat.Action(
                R.drawable.ic_play_arrow_black_24dp,
                context.getString(R.string.resume_action_button),
                resumeSportActivityPendingIntent);

        return resumeAction;
    }

    private static NotificationCompat.Action pauseWorkout(Context context) {
        Intent pauseSportActivityIntent = new Intent(context, LocationTrackingService.class);
        pauseSportActivityIntent.setAction(ACTION_PAUSE_WORKOUT);

        PendingIntent pauseSportActivityPendingIntent = PendingIntent.getService(
                context,
                ACTION_PAUSE_PENDING_INTENT_ID,
                pauseSportActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action pauseAction = new NotificationCompat.Action(
                R.drawable.ic_pause_black_24dp,
                context.getString(R.string.pause_action_button),
                pauseSportActivityPendingIntent);

        return pauseAction;
    }

    // Create a helper method called contentIntent with a single parameter for a Context. It
    // should return a PendingIntent. This method will create the pending intent which will trigger when
    // the notification is pressed. This pending intent should open up the MainActivity.
    private static PendingIntent contentIntent(Context context) {
        // Create an intent that opens up the MainActivity
        Intent startActivityIntent = new Intent(context, StartGpsWorkoutActivity.class);

        // Create a PendingIntent using getActivity that:
        // - Take the context passed in as a parameter
        // - Takes an unique integer ID for the pending intent
        // - Takes the intent to open the MainActivity you just created; this is what is triggered
        //   when the notification is triggered
        // - Has the flag FLAG_UPDATE_CURRENT, so that if the intent is created again, keep the
        // intent but update the data

        return PendingIntent.getActivity(
                context,
                MOON_RUNNER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // Create a helper method called largeIcon which takes in a Context as a parameter and
    // returns a Bitmap. This method is necessary to decode a bitmap needed for the notification.
    private static Bitmap largeIcon(Context context) {
        // Get a Resources object from the context.
        Resources resources = context.getResources();

        // Create and return a bitmap using BitmapFactory.decodeResource, passing in the
        // resources object and R.drawable.ic_local_drink_black_24px
        Bitmap largeIcon = BitmapFactory.decodeResource(
                resources,
                R.drawable.ic_directions_bike_black_24dp);

        return largeIcon;
    }

    // Create a method called remindUserBecauseCharging which takes a Context.
    // This method will create a notification for charging. It might be helpful
    // to take a look at this guide to see an example of what the code in this method will look like:
    // https://developer.android.com/training/notify-user/build-notification.html
    public static void remindUserAboutTracking(Context context) {
        // Get the NotificationManager using context.getSystemService
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android O devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    MOON_RUNNER_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
        // In the remindUser method use NotificationCompat.Builder to create a notification
        // that:
        // - has a color of R.colorPrimary - use ContextCompat.getColor to get a compatible color
        // - has ic_drink_notification as the small icon
        // - uses icon returned by the largeIcon helper method as the large icon
        // - sets the title to the location_tracking_notification_title String resource
        // - sets the text to the location_tracking_notification_body String resource
        // - sets the style to NotificationCompat.BigTextStyle().bigText(text)
        // - sets the notification defaults to vibrate
        // - uses the content intent returned by the contentIntent helper method for the contentIntent
        // - automatically cancels the notification when the notification is clicked
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, MOON_RUNNER_CHANNEL_ID)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setSmallIcon(R.drawable.ic_drink_notification)
                        .setLargeIcon(largeIcon(context))
                        .setContentTitle(context.getString(R.string.location_tracking_notification_title))
                        .setContentText(context.getString(R.string.location_tracking_notification_body))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(
                                context.getString(R.string.location_tracking_notification_body)))
                        .addAction(resumeWorkout(context))
                        .addAction(pauseWorkout(context))
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setContentIntent(contentIntent(context));

        // If the build version is greater than JELLY_BEAN and lower than OREO,
        // set the notification's priority to PRIORITY_HIGH.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        // Trigger the notification by calling notify on the NotificationManager.
        // Pass in a unique ID of your choosing for the notification and .build()
        notificationManager.notify(MOON_RUNNER_WORKOUT_NOTIFICATION_ID, notificationBuilder.build());
    }
}