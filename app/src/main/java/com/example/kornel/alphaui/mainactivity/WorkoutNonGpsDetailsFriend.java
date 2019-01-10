package com.example.kornel.alphaui.mainactivity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kornel.alphaui.utils.FriendWorkout;
import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.gpsworkout.WorkoutSummary;
import com.example.kornel.alphaui.utils.IconUtils;
import com.squareup.picasso.Picasso;

import static com.example.kornel.alphaui.mainactivity.FeedFriendsFragment.FRIEND_WORKOUT_INTENT_EXTRA;

public class WorkoutNonGpsDetailsFriend extends AppCompatActivity {
    private ImageView mAvatarImageView;
    private TextView mNameTextView;

    private CardView mWorkoutCardView;
    private ImageView mActivityIconImageView;
    private TextView mActivityTypeTextView;
    private TextView mTimeTextView;
    private TextView mDateTextView;

    private CardView mSummaryCardView;
    private ImageView mDurationImageView;
    private TextView mDurationTextView;

    private CardView mPhotoCardView;
    private ImageView mPhotoImageView;
    private View mPhotoStatusDivider;
    private TextView mStatusTextView;

    private WorkoutSummary mWorkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_non_gps_details_friend);

        FriendWorkout friendWorkout = getIntent().getExtras().getParcelable(FRIEND_WORKOUT_INTENT_EXTRA);
        mWorkout = friendWorkout.getWorkout();

        getSupportActionBar().setTitle(mWorkout.getWorkoutName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAvatarImageView = findViewById(R.id.avatarImageView);
        mNameTextView = findViewById(R.id.nameTextView);

        Picasso.get()
                .load(friendWorkout.getAvatarUrl())
                .into(mAvatarImageView);
        mNameTextView.setText(friendWorkout.getFriendName());


        mWorkoutCardView = findViewById(R.id.workoutCardView);
        mActivityIconImageView = findViewById(R.id.activityIconImageView);
        mActivityTypeTextView = findViewById(R.id.activityTypeTextView);
        mTimeTextView = findViewById(R.id.timeTextView2);
        mDateTextView = findViewById(R.id.dateTextView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActivityIconImageView.setImageDrawable(getResources().getDrawable(IconUtils.getWorkoutIcon(mWorkout.getWorkoutName()), getApplicationContext().getTheme()));
        } else {
            mActivityIconImageView.setImageDrawable(getResources().getDrawable(IconUtils.getWorkoutIcon(mWorkout.getWorkoutName())));
        }
        mTimeTextView.setText(mWorkout.getTimeHourMin());
        mDateTextView.setText(mWorkout.getDateStringPl());


        mSummaryCardView = findViewById(R.id.summaryCardView);
        mDurationImageView = findViewById(R.id.durationImageView);
        mDurationTextView = findViewById(R.id.durationTextView);

        mDurationTextView.setText(mWorkout.getDurationString());

        mPhotoCardView = findViewById(R.id.photoCardView);
        mPhotoImageView = findViewById(R.id.photoImageView);
        mPhotoStatusDivider = findViewById(R.id.photoStatusDivider);
        mStatusTextView = findViewById(R.id.statusTextView);

        boolean noStatus = false;
        boolean noPhoto = false;

        if (mWorkout.getPicUrl() == null || mWorkout.getPicUrl().equals("")) {
            mPhotoImageView.setVisibility(View.GONE);
            mPhotoStatusDivider.setVisibility(View.GONE);
            noPhoto = true;
        } else {
            Picasso.get()
                    .load(mWorkout.getPicUrl())
                    .into(mPhotoImageView);
        }

        if (mWorkout.getStatus() == null || mWorkout.getStatus().equals("")) {
            mStatusTextView.setVisibility(View.GONE);
            mPhotoStatusDivider.setVisibility(View.GONE);
            noStatus = true;
        } else {
            mStatusTextView.setText(mWorkout.getStatus());
        }

        if (noPhoto && noStatus) {
            mPhotoCardView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
