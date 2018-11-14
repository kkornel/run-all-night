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
import com.example.kornel.alphaui.utils.ListItemClickListener;
import com.example.kornel.alphaui.utils.User;
import com.squareup.picasso.Picasso;

import java.util.List;


public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendListViewHolder> {
    private final ListItemClickListener mOnClickListener;
    private List<User> mFriendsProfileList;

    public FriendsListAdapter(ListItemClickListener onClickListener, List<User> feedList) {
        mOnClickListener = onClickListener;
        mFriendsProfileList = feedList;
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

        if ((mFriendsProfileList == null) || (mFriendsProfileList.size() == 0)) {
            friendListViewHolder.mNameTextView.setText("ERROR");
        } else {
            Picasso.get()
                    .load(mFriendsProfileList.get(position).getAvatarUrl())
                    .placeholder(R.drawable.ic_person_black_64dp)
                    .error(R.drawable.ic_error_red_64dp)
                    .into(friendListViewHolder.mAvatarImageView);

            String name = mFriendsProfileList.get(position).getFirstName()
                    + " " + mFriendsProfileList.get(position).getSurname();
            friendListViewHolder.mNameTextView.setText(name);
        }
    }

    @Override
    public int getItemCount() {
        return ((mFriendsProfileList != null) && (mFriendsProfileList.size() != 0) ? mFriendsProfileList.size() : 0);
    }

    void loadNewData(List<User> newActivities) {
        mFriendsProfileList = newActivities;
        notifyDataSetChanged();
    }

    class FriendListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "WorkoutViewHolder";

        private ImageView mAvatarImageView;
        private TextView mNameTextView;

        public FriendListViewHolder(View itemView) {
            super(itemView);
            mAvatarImageView = itemView.findViewById(R.id.friendAvatarImageView);
            mNameTextView = itemView.findViewById(R.id.friendNameTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
