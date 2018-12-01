package com.example.kornel.alphaui.mainactivity;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
    private static final int TYPE_WITHOUT_DESCRIPTION = 1;
    private static final int TYPE_WITH_DESCRIPTION = 2;

    private Context mContext;

    private final ListItemClickListener mOnClickListener;

    private List<FriendWorkout> mFriendsFeedList;

    public FeedFriendsAdapter(Context context, ListItemClickListener onClickListener, List<FriendWorkout> feedList) {
        mContext = context;
        mOnClickListener = onClickListener;
        mFriendsFeedList = feedList;
    }

    @Override
    public int getItemViewType(int position) {
        FriendWorkout workout = mFriendsFeedList.get(position);
        if (workout.getWorkout().getStatus() != null) {
            return TYPE_WITH_DESCRIPTION;
        }
        return TYPE_WITHOUT_DESCRIPTION;
    }

    @Override
    public FeedFriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Called by the layout manager when it needs a new view
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        if (viewType == TYPE_WITH_DESCRIPTION) {
            int layoutIdForListItem = R.layout.feed_friends_list_item_with_description;
            View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
            FeedFriendsViewHolder activityViewHolder = new FeedFriendsViewHolder(view);
            return activityViewHolder;
        } else {
            int layoutIdForListItem = R.layout.feed_friends_list_item_without_description;
            View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
            FeedFriendsViewHolder activityViewHolder = new FeedFriendsViewHolder(view);
            return activityViewHolder;
        }
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

            if (activityViewHolder.getItemViewType() == TYPE_WITH_DESCRIPTION) {
                String description = workout.getStatus();
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
        private ImageView mAvatarImageView;
        private ImageView mIconImageView;
        private TextView mNameTextView;
        private TextView mDateTextView;
        private TextView mDescriptionTextView;
        private TextView mDistanceTextView;
        private TextView mDurationTextView;

        public FeedFriendsViewHolder(View itemView) {
            super(itemView);
            mAvatarImageView = itemView.findViewById(R.id.avatarImageView);
            mIconImageView = itemView.findViewById(R.id.activityImageView);
            mNameTextView = itemView.findViewById(R.id.friendNameTextView);
            mDateTextView = itemView.findViewById(R.id.dateTextView);
            mDescriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            mDistanceTextView = itemView.findViewById(R.id.distanceTextView);
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
