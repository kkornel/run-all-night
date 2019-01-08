package com.example.kornel.alphaui.friends;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.FriendRequest;
import com.example.kornel.alphaui.utils.ListItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendsRequestAdapter extends RecyclerView.Adapter<FriendsRequestAdapter.FriendsRequestViewHolder> {
    private final ListItemClickListener mOnClickListener;
    private List<FriendRequest> mFriendsRequestsList;

    public FriendsRequestAdapter(ListItemClickListener onClickListener, List<FriendRequest> friendsRequestList) {
        mOnClickListener = onClickListener;
        mFriendsRequestsList = friendsRequestList;
    }

    @Override
    public FriendsRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        if ((mFriendsRequestsList == null) || (mFriendsRequestsList.size() == 0)) {
            friendListViewHolder.mNameTextView.setText("ERROR");
        } else {
            Picasso.get()
                    .load(mFriendsRequestsList.get(position).getAvatarUrl())
                    .placeholder(R.drawable.ic_person_black_64dp)
                    .error(R.drawable.ic_error_red_64dp)
                    .into(friendListViewHolder.mAvatarImageView);
            friendListViewHolder.mNameTextView.setText(mFriendsRequestsList.get(position).getFriendName());
            String requestType;
            if (mFriendsRequestsList.get(position).getRequestType().equals(Database.FRIENDS_REQUESTS_SENT)) {
                requestType = "Invitation sent";
            } else {
                requestType = "New invitation";
            }
            friendListViewHolder.mRequestTypeTextView.setText(requestType);
        }
    }

    @Override
    public int getItemCount() {
        return ((mFriendsRequestsList != null) && (mFriendsRequestsList.size() != 0) ? mFriendsRequestsList.size() : 0);
    }

    void loadNewData(List<FriendRequest> friendsRequestList) {
        mFriendsRequestsList = friendsRequestList;
        notifyDataSetChanged();
    }

    class FriendsRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mAvatarImageView;
        private TextView mNameTextView;
        private TextView mRequestTypeTextView;

        public FriendsRequestViewHolder(View itemView) {
            super(itemView);
            mAvatarImageView = itemView.findViewById(R.id.friendAvatarImageView);
            mNameTextView = itemView.findViewById(R.id.friendNameTextView);
            mRequestTypeTextView = itemView.findViewById(R.id.requestTypeTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}