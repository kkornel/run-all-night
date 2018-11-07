package com.example.kornel.alphaui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class WorkoutSummary extends AppCompatActivity {
    private static final String TAG = "WorkoutSummary";
    private TextView mSummaryTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_summary);

        mSummaryTextView = findViewById(R.id.summaryTextView);
        String summary = getIntent().getExtras().getString("summary");
        mSummaryTextView.setText(summary);
    }
}
