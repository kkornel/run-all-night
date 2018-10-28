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

public class FeedYouAdapter extends RecyclerView.Adapter<FeedYouAdapter.FeedYouViewHolder> {
    private final ListItemClickListener mOnClickListener;

    private List<String> mActivities;

    public FeedYouAdapter(ListItemClickListener onClickListener, List<String> activities) {
        mOnClickListener = onClickListener;
        mActivities = activities;
    }

    @Override
    public FeedYouViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Called by the layout manager when it needs a new view
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.feed_you_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        FeedYouViewHolder activityViewHolder = new FeedYouViewHolder(view);

        return activityViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedYouViewHolder activityViewHolder, int position) {
        // Called by the layout manager when it wants new data in an existing row

        if ((mActivities == null) || (mActivities.size() == 0)) {
            activityViewHolder.mDateTextView.setText("ERROR");
        } else {
            activityViewHolder.mDateTextView.setText(mActivities.get(position));
        }

        // Googles way
        // paceViewHolder.bind(position);
    }

    @Override
    public int getItemCount() {
        return ((mActivities != null) && (mActivities.size() !=0) ? mActivities.size() : 1);
    }

    void loadNewData(List<String> newActivities) {
        mActivities = newActivities;
        notifyDataSetChanged();
    }

    class FeedYouViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "FeedYouViewHolder";

        private ImageView mActivityImageView;
        private TextView mDateTextView;
        private TextView mDistanceTextView;
        private TextView mDuartionTextView;

        public FeedYouViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "ActivityViewHolder: ");
            this.mActivityImageView = itemView.findViewById(R.id.activityImageView);
            this.mDateTextView = itemView.findViewById(R.id.dateTextView);
            this.mDistanceTextView = itemView.findViewById(R.id.distanceTextView);
            this.mDuartionTextView = itemView.findViewById(R.id.durationTextView);
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
