package com.example.kornel.alphaui.gpsworkout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.mainactivity.MainActivity;
import com.example.kornel.alphaui.utils.Database;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.example.kornel.alphaui.gpsworkout.StartGpsWorkoutActivity.WORKOUT_DETAILS_EXTRA_INTENT;

public class WorkoutSummary extends AppCompatActivity {
    private static final String TAG = "WorkoutSummary";

    private TextView mTimeTextView;
    private TextView mDistanceTextView;
    private Button mSaveButton;
    private Button mDontSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_summary);

        // mTimeTextView = findViewById(R.id.timetv);
        // mDistanceTextView = findViewById(R.id.distancetv);
        // mSaveButton = findViewById(R.id.saveButton);
        // mDontSaveButton = findViewById(R.id.dontSaveButton);

        // final WorkoutGpsSummary workoutSummary = (WorkoutGpsSummary) getIntent().getExtras().getParcelable(WORKOUT_DETAILS_EXTRA_INTENT);
        //
        // mTimeTextView.setText(workoutSummary.getDuration());
        //
        // double distance = workoutSummary.getDistance();
        // distance /= 1000;
        // // String.format("%.5g%n", distance);
        // DecimalFormat df = new DecimalFormat("#.##");
        // mDistanceTextView.setText(df.format(distance));
        //
        // mSaveButton.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View v) {
        //
        //         if (!hasInternetConnection()) {
        //             requestInternetConnection();
        //             return;
        //         }
        //
        //         saveWorkout(workoutSummary);
        //
        //         Toast.makeText(WorkoutSummary.this, "Workout saved", Toast.LENGTH_LONG).show();
        //
        //         Intent intent = new Intent(WorkoutSummary.this, MainActivity.class);
        //         startActivity(intent);
        //     }
        // });
        //
        // mDontSaveButton.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View v) {
        //         Toast.makeText(WorkoutSummary.this, "Workout not saved", Toast.LENGTH_LONG).show();
        //
        //         Intent intent = new Intent(WorkoutSummary.this, MainActivity.class);
        //         startActivity(intent);
        //     }
        // });
    }

    public void requestInternetConnection() {
        Snackbar.make(
                mSaveButton,
                R.string.enable_internet,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent viewIntent = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
                        startActivity(viewIntent);
                    }
                })
                .show();
    }

    public void saveWorkout(WorkoutGpsSummary workoutSummary) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference workoutRef = database.getReference("workouts");
        DatabaseReference userRef = database.getReference(Database.USERS);

        String userUid = user.getUid();
        String key = workoutRef.child(userUid).push().getKey();

        // HashMap<String, Object> result = new HashMap<>();
        // result.put("name", workoutSummary.getWorkoutName());
        // result.put("time", workoutSummary.getDuration());
        // result.put("distance", workoutSummary.getDistance());
        // result.put("path", workoutSummary.g);

        userRef.child(userUid).child("lastWorkout").setValue(key);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/workouts/" + userUid + "/" + key, workoutSummary);
        // childUpdates.put("/users/" + userUid, key);

        database.getReference().updateChildren(childUpdates);
    }

    private boolean hasInternetConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
