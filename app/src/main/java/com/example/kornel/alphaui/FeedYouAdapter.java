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

    private List<String> mWorkouts;

    public FeedYouAdapter(ListItemClickListener onClickListener, List<String> workouts) {
        mOnClickListener = onClickListener;
        mWorkouts = workouts;
    }

    @Override
    public FeedYouViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Called by the layout manager when it needs a new view
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.feed_you_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        FeedYouViewHolder workoutViewHolder = new FeedYouViewHolder(view);

        return workoutViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedYouViewHolder workoutViewHolder, int position) {
        // Called by the layout manager when it wants new data in an existing row

        if ((mWorkouts == null) || (mWorkouts.size() == 0)) {
            workoutViewHolder.mDateTextView.setText("ERROR");
        } else {
            workoutViewHolder.mDateTextView.setText(mWorkouts.get(position));
        }

        // Googles way
        // paceViewHolder.bind(position);
    }

    @Override
    public int getItemCount() {
        return ((mWorkouts != null) && (mWorkouts.size() !=0) ? mWorkouts.size() : 1);
    }

    void loadNewData(List<String> newActivities) {
        mWorkouts = newActivities;
        notifyDataSetChanged();
    }

    class FeedYouViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "FeedYouViewHolder";

        private ImageView mWorkoutImageView;
        private TextView mDateTextView;
        private TextView mDistanceTextView;
        private TextView mDuartionTextView;

        public FeedYouViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "WorkoutViewHolder: ");
            this.mWorkoutImageView = itemView.findViewById(R.id.activityImageView);
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
