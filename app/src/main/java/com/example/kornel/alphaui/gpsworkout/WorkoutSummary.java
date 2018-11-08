package com.example.kornel.alphaui.gpsworkout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.utils.Database;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static com.example.kornel.alphaui.gpsworkout.StartGpsWorkoutActivity.WORKOUT_DETAILS_EXTRA_INTENT;

public class WorkoutSummary extends AppCompatActivity {
    private static final String TAG = "WorkoutSummary";

    private TextView mTimeTextView;
    private TextView mDistanceTextView;
    private Button mSaveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_summary);

        mTimeTextView = findViewById(R.id.timetv);
        mDistanceTextView = findViewById(R.id.distancetv);
        mSaveButton = findViewById(R.id.saveButton);

        final WorkoutGpsSummary workoutSummary = (WorkoutGpsSummary) getIntent().getExtras().getParcelable(WORKOUT_DETAILS_EXTRA_INTENT);

        mTimeTextView.setText(workoutSummary.getDuration());

        double distance = workoutSummary.getDistance();
        distance /= 1000;
        // String.format("%.5g%n", distance);
        DecimalFormat df = new DecimalFormat("#.##");
        mDistanceTextView.setText(df.format(distance));

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWorkout(workoutSummary);
            }
        });
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

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/workouts/" + userUid + "/" + key, workoutSummary);
        // childUpdates.put("/users/" + userUid, key);

        database.getReference().updateChildren(childUpdates);
    }
}
