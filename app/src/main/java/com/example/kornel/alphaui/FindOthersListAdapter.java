package com.example.kornel.alphaui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kornel.alphaui.utils.ListItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FindOthersListAdapter extends RecyclerView.Adapter<FindOthersListAdapter.FindOthersListViewHolder> {
    private final ListItemClickListener mOnClickListener;
    private List<SharedLocationInfo> mSharedLocationInfoList;

    public FindOthersListAdapter(ListItemClickListener onClickListener, List<SharedLocationInfo> sharedLocationInfoList) {
        mOnClickListener = onClickListener;
        mSharedLocationInfoList = sharedLocationInfoList;
    }

    @Override
    public FindOthersListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Called by the layout manager when it needs a new view
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_list_find_others;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        FindOthersListViewHolder viewHolder = new FindOthersListViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FindOthersListViewHolder findOthersListViewHolder, int position) {
        // Called by the layout manager when it wants new data in an existing row

        if ((mSharedLocationInfoList == null) || (mSharedLocationInfoList.size() == 0)) {
            findOthersListViewHolder.mNameTextView.setText("ERROR");
        } else {
            SharedLocationInfo sli = mSharedLocationInfoList.get(position);

            Picasso.get()
                    .load(sli.getUserProfile().getAvatarUrl())
                    .placeholder(R.drawable.ic_person_black_64dp)
                    .error(R.drawable.ic_error_red_64dp)
                    .into(findOthersListViewHolder.mAvatarImageView);

            findOthersListViewHolder.mNameTextView.setText(sli.getUserProfile().getFullName());
            findOthersListViewHolder.mDistanceTextView.setText(sli.getDistanceToYouString());
            findOthersListViewHolder.mMessageTextView.setText(sli.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return ((mSharedLocationInfoList != null) && (mSharedLocationInfoList.size() != 0) ? mSharedLocationInfoList.size() : 0);
    }

    void loadNewData(List<SharedLocationInfo> sharedLocationInfoList) {
        mSharedLocationInfoList = sharedLocationInfoList;

        Collections.sort(mSharedLocationInfoList, new Comparator<SharedLocationInfo>() {
            @Override
            public int compare(SharedLocationInfo o1, SharedLocationInfo o2) {
                return Double.compare(o1.getDistanceToYou(), o2.getDistanceToYou());
            }
        });

        notifyDataSetChanged();
    }

    class FindOthersListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "WorkoutViewHolder";

        private ImageView mAvatarImageView;
        private TextView mNameTextView;
        private TextView mDistanceTextView;
        private TextView mMessageTextView;

        public FindOthersListViewHolder(View itemView) {
            super(itemView);
            mAvatarImageView = itemView.findViewById(R.id.avatarImageView);
            mNameTextView = itemView.findViewById(R.id.nameTextView);
            mDistanceTextView = itemView.findViewById(R.id.distanceTextView);
            mMessageTextView = itemView.findViewById(R.id.messageTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}