package com.example.kornel.alphaui.mainactivity;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kornel.alphaui.FriendWorkout;
import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.gpsworkout.WorkoutGpsSummary;
import com.example.kornel.alphaui.utils.IconUtils;
import com.example.kornel.alphaui.utils.ListItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FeedFriendsAdapter extends RecyclerView.Adapter<FeedFriendsAdapter.FeedFriendsViewHolder> {
    private Context mContext;

    private final ListItemClickListener mOnClickListener;

    private List<FriendWorkout> mFriendsFeedList;

    public FeedFriendsAdapter(Context context, ListItemClickListener onClickListener, List<FriendWorkout> feedList) {
        mContext = context;
        mOnClickListener = onClickListener;
        mFriendsFeedList = feedList;
    }

    @Override
    public FeedFriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Called by the layout manager when it needs a new view
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.feed_friends_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        FeedFriendsViewHolder activityViewHolder = new FeedFriendsViewHolder(view);

        return activityViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedFriendsViewHolder activityViewHolder, int position) {
        // Called by the layout manager when it wants new data in an existing row

        if ((mFriendsFeedList == null) || (mFriendsFeedList.size() == 0)) {
            activityViewHolder.mNameTextView.setText("");
        } else {
            WorkoutGpsSummary workout = mFriendsFeedList.get(position).getWorkout();

            Picasso.get()
                    .load(mFriendsFeedList.get(position).getAvatarUrl())
                    .placeholder(R.drawable.ic_person_black_64dp)
                    .error(R.drawable.ic_error_red_64dp)
                    .into(activityViewHolder.mAvatarImageView);

            activityViewHolder.mNameTextView.setText(mFriendsFeedList.get(position).getFriendName());
            activityViewHolder.mDateTextView.setText(workout.getDateStringPlWithTime());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activityViewHolder.mIconImageView.setImageDrawable(mContext.getResources().getDrawable(IconUtils.getWorkoutIcon(workout.getWorkoutName()), mContext.getApplicationContext().getTheme()));
            } else {
                activityViewHolder.mIconImageView.setImageDrawable(mContext.getResources().getDrawable(IconUtils.getWorkoutIcon(workout.getWorkoutName())));
            }

            String description = workout.getStatus();
            Log.d("l",  "workout: " + workout);
            Log.d("l",  "description: " + description);
            if (description == null || description.equals("")) {
                activityViewHolder.mDescriptionTextView.setVisibility(View.GONE);
                ConstraintSet set = new ConstraintSet();

                set.clone(activityViewHolder.mConstraintLayout);
                // Comment out line above and uncomment line below to make the connection.
                set.connect(activityViewHolder.mDistanceLabel.getId(), ConstraintSet.TOP, activityViewHolder.mAvatarImageView.getId(), ConstraintSet.BOTTOM, 16);
                set.applyTo(activityViewHolder.mConstraintLayout);

            } else {
                activityViewHolder.mDescriptionTextView.setText(description);
            }
            activityViewHolder.mDistanceTextView.setText(String.valueOf(workout.getDistance()));
            activityViewHolder.mDurationTextView.setText(workout.getDuration());
        }

        // Googles way
        // paceViewHolder.bind(position);
    }

    @Override
    public int getItemCount() {
        return ((mFriendsFeedList != null) && (mFriendsFeedList.size() != 0) ? mFriendsFeedList.size() : 0);
    }

    void loadNewData(List<FriendWorkout> newActivities) {
        mFriendsFeedList = newActivities;
        notifyDataSetChanged();
    }

    class FeedFriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "WorkoutViewHolder";

        private ConstraintLayout mConstraintLayout;
        private ImageView mAvatarImageView;
        private ImageView mIconImageView;
        private TextView mNameTextView;
        private TextView mDateTextView;
        private TextView mDescriptionTextView;
        private TextView mDistanceTextView;
        private TextView mDistanceLabel;
        private TextView mDurationTextView;

        public FeedFriendsViewHolder(View itemView) {
            super(itemView);
            mConstraintLayout = itemView.findViewById(R.id.constraintLayout);
            mAvatarImageView = itemView.findViewById(R.id.avatarImageView);
            mIconImageView = itemView.findViewById(R.id.activityImageView);
            mNameTextView = itemView.findViewById(R.id.friendNameTextView);
            mDateTextView = itemView.findViewById(R.id.dateTextView);
            mDescriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            mDistanceTextView = itemView.findViewById(R.id.distanceTextView);
            mDistanceLabel = itemView.findViewById(R.id.distanceLabel);
            mDurationTextView = itemView.findViewById(R.id.durationLabel);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
