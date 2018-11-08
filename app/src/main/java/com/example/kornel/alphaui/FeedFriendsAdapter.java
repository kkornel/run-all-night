package com.example.kornel.alphaui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kornel.alphaui.utils.ListItemClickListener;

import java.util.List;

public class FeedFriendsAdapter extends RecyclerView.Adapter<FeedFriendsAdapter.FeedFriendsViewHolder> {
    private final ListItemClickListener mOnClickListener;

    private List<String> mFriendsFeedList;

    public FeedFriendsAdapter(ListItemClickListener onClickListener, List<String> feedList) {
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
            activityViewHolder.mNameTextView.setText("ERROR");
        } else {
            activityViewHolder.mNameTextView.setText(mFriendsFeedList.get(position));
        }

        // Googles way
        // paceViewHolder.bind(position);
    }

    @Override
    public int getItemCount() {
        return ((mFriendsFeedList != null) && (mFriendsFeedList.size() !=0) ? mFriendsFeedList.size() : 1);
    }

    void loadNewData(List<String> newActivities) {
        mFriendsFeedList = newActivities;
        notifyDataSetChanged();
    }

    class FeedFriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "WorkoutViewHolder";

        private ImageView mAvatarImageView;
        private TextView mNameTextView;
        private TextView mDateTextView;
        private TextView mDescriptionTextView;
        private TextView mDistanceTextView;
        private TextView mDuartionTextView;

        public FeedFriendsViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "WorkoutViewHolder: ");
            mAvatarImageView = itemView.findViewById(R.id.avatarImageView);
            mNameTextView = itemView.findViewById(R.id.friendNameTextView);
            mDateTextView = itemView.findViewById(R.id.dateTextView);
            mDescriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            mDistanceTextView = itemView.findViewById(R.id.distanceTextView);
            mDuartionTextView = itemView.findViewById(R.id.durationTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: ");
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
