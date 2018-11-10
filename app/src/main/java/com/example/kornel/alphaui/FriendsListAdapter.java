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


public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendListViewHolder> {
    private final ListItemClickListener mOnClickListener;

    private List<String> mFriendsFeedList;

    public FriendsListAdapter(ListItemClickListener onClickListener, List<String> feedList) {
        mOnClickListener = onClickListener;
        mFriendsFeedList = feedList;
    }

    @Override
    public FriendListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Called by the layout manager when it needs a new view
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.friends_list_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        FriendListViewHolder viewHolder = new FriendListViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListViewHolder friendListViewHolder, int position) {
        // Called by the layout manager when it wants new data in an existing row

        if ((mFriendsFeedList == null) || (mFriendsFeedList.size() == 0)) {
            friendListViewHolder.mNameTextView.setText("ERROR");
        } else {
            friendListViewHolder.mNameTextView.setText(mFriendsFeedList.get(position));
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

    class FriendListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "WorkoutViewHolder";

        private ImageView mAvatarImageView;
        private TextView mNameTextView;

        public FriendListViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "WorkoutViewHolder: ");
            mAvatarImageView = itemView.findViewById(R.id.friendAvatarImageView);
            mNameTextView = itemView.findViewById(R.id.friendNameTextView);
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
