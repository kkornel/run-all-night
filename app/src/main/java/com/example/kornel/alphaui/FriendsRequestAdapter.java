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

public class FriendsRequestAdapter extends RecyclerView.Adapter<FriendsRequestAdapter.FriendsRequestViewHolder> {
    private final ListItemClickListener mOnClickListener;

    private List<String> mFriendsFeedList;

    public FriendsRequestAdapter(ListItemClickListener onClickListener, List<String> feedList) {
        mOnClickListener = onClickListener;
        mFriendsFeedList = feedList;
    }

    @Override
    public FriendsRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Called by the layout manager when it needs a new view
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.friends_request_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        FriendsRequestViewHolder viewHolder = new FriendsRequestViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsRequestViewHolder friendListViewHolder, int position) {
        // Called by the layout manager when it wants new data in an existing row

        if ((mFriendsFeedList == null) || (mFriendsFeedList.size() == 0)) {
            friendListViewHolder.mNameTextView.setText("ERROR");
        } else {
            friendListViewHolder.mNameTextView.setText(mFriendsFeedList.get(position));
            friendListViewHolder.mRequestTypeTextView.setText("Wysłano");
        }
    }

    @Override
    public int getItemCount() {
        return ((mFriendsFeedList != null) && (mFriendsFeedList.size() !=0) ? mFriendsFeedList.size() : 1);
    }

    void loadNewData(List<String> newActivities) {
        mFriendsFeedList = newActivities;
        notifyDataSetChanged();
    }

    class FriendsRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "WorkoutViewHolder";

        private ImageView mAvatarImageView;
        private TextView mNameTextView;
        private TextView mRequestTypeTextView;

        public FriendsRequestViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "WorkoutViewHolder: ");
            mAvatarImageView = itemView.findViewById(R.id.friendAvatarImageView);
            mNameTextView = itemView.findViewById(R.id.friendNameTextView);
            mRequestTypeTextView = itemView.findViewById(R.id.requestTypeTextView);
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