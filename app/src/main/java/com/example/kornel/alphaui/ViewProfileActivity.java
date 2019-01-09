package com.example.kornel.alphaui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kornel.alphaui.utils.User;
import com.example.kornel.alphaui.utils.Utils;
import com.squareup.picasso.Picasso;

import static com.example.kornel.alphaui.sharelocation.FindOthersMapFragment.EXTRA_USER;

public class ViewProfileActivity extends AppCompatActivity {
    private static final String TAG = "ViewProfileActivity";

    private ImageView mAvatarImageView;
    private TextView mNameTextView;
    private TextView mTotalDistanceTextView;
    private TextView mTotalTimeTextView;
    private TextView mWorkoutsCountTextView;
    private TextView mSharedLocationsCountTextView;
    private TextView mHmm2TextView;
    private TextView mFriendsCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        User user = (User) getIntent().getExtras().getSerializable(EXTRA_USER);
        Log.d(TAG, "onClickConfirmed: " + user);
        mAvatarImageView = findViewById(R.id.avatarImageView);
        mNameTextView = findViewById(R.id.nameTextView);
        mTotalDistanceTextView = findViewById(R.id.totalDistanceTextView);
        mTotalTimeTextView = findViewById(R.id.totalTimeTextView2);
        mWorkoutsCountTextView = findViewById(R.id.workoutsCountTextView);
        mSharedLocationsCountTextView = findViewById(R.id.sharedLocationsCountTextView);
        mHmm2TextView = findViewById(R.id.hmm2TextView);
        mFriendsCountTextView = findViewById(R.id.friendsCountTextView2);

        Picasso.get()
                .load(user.getAvatarUrl())
                .placeholder(R.drawable.ic_person_black_64dp)
                .error(R.drawable.ic_error_red_64dp)
                .into(mAvatarImageView);
        mNameTextView.setText(user.getFirstName() + " " + user.getSurname());
        mTotalDistanceTextView.setText(Utils.distanceMetersToKm(user.getTotalDistance()) + "km");
        mTotalTimeTextView.setText(Utils.getDurationHoursString(user.getTotalDuration()) + "h");
        mWorkoutsCountTextView.setText(user.getTotalWorkouts() + "");
        if (user.getFriends() == null) {
            mFriendsCountTextView.setText(0 + "");
        } else {
            mFriendsCountTextView.setText(user.getFriends().size() + "");
        }
        if (user.getSharedLocations() == null) {
            mSharedLocationsCountTextView.setText(0 + "");
        } else {
            mSharedLocationsCountTextView.setText(user.getSharedLocations().size() + "");
        }

    }
}
