package com.example.kornel.alphaui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.FriendRequest;
import com.example.kornel.alphaui.utils.ListItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendsRequestAdapter extends RecyclerView.Adapter<FriendsRequestAdapter.FriendsRequestViewHolder> {
    private static final String TAG = "FriendsRequestAdapter";

    private final ListItemClickListener mOnClickListener;

    private List<FriendRequest> mFriendsRequestList;

    public FriendsRequestAdapter(ListItemClickListener onClickListener, List<FriendRequest> friendsRequestList) {
        mOnClickListener = onClickListener;
        mFriendsRequestList = friendsRequestList;
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

        if ((mFriendsRequestList == null) || (mFriendsRequestList.size() == 0)) {
            friendListViewHolder.mNameTextView.setText("ERROR");
        } else {
            Picasso.get()
                    .load(mFriendsRequestList.get(position).getAvatarUrl())
                    .placeholder(R.drawable.ic_person_black_64dp)
                    .error(R.drawable.ic_error_red_64dp)
                    .into(friendListViewHolder.mAvatarImageView);
            friendListViewHolder.mNameTextView.setText(mFriendsRequestList.get(position).getFriendName());
            String requestType;
            if (mFriendsRequestList.get(position).getRequestType().equals(Database.FRIENDS_REQUESTS_SENT)) {
                requestType = "Wysłano";
            } else {
                requestType = "Akceptuj";
            }
            friendListViewHolder.mRequestTypeTextView.setText(requestType);
        }
    }

    @Override
    public int getItemCount() {
        return ((mFriendsRequestList != null) && (mFriendsRequestList.size() != 0) ? mFriendsRequestList.size() : 0);
    }

    void loadNewData(List<FriendRequest> friendsRequestList) {
        mFriendsRequestList = friendsRequestList;
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