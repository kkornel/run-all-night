package com.example.kornel.alphaui.mainactivity;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.gpsworkout.WorkoutSummary;
import com.example.kornel.alphaui.utils.IconUtils;
import com.example.kornel.alphaui.utils.ListItemClickListener;
import com.example.kornel.alphaui.utils.WorkoutUtils;

import java.util.List;

public class FeedYouAdapter extends RecyclerView.Adapter<FeedYouAdapter.FeedYouViewHolder> {
    private Context mContext;

    private final ListItemClickListener mOnClickListener;

    private List<WorkoutSummary> mWorkouts;

    public FeedYouAdapter(Context context, ListItemClickListener onClickListener, List<WorkoutSummary> workouts) {
        mContext = context;
        mOnClickListener = onClickListener;
        mWorkouts = workouts;
    }

    @Override
    public FeedYouViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        if ((mWorkouts == null) || (mWorkouts.size() == 0)) {
            workoutViewHolder.mDateTextView.setText("");
        } else {
            // non GPS
            if (!WorkoutUtils.isGpsBased(mWorkouts.get(position).getWorkoutName())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    workoutViewHolder.mDistanceImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_stopwatch, mContext.getApplicationContext().getTheme()));
                } else {
                    workoutViewHolder.mDistanceImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_stopwatch));
                }
                workoutViewHolder.mDistanceTextView.setText(mWorkouts.get(position).getDurationString());
                workoutViewHolder.mDurationImageView.setVisibility(View.GONE);
                workoutViewHolder.mDurationTextView.setVisibility(View.GONE);
            } else {
                workoutViewHolder.mDistanceTextView.setText(mWorkouts.get(position).getDistanceKmString());
                workoutViewHolder.mDurationTextView.setText(mWorkouts.get(position).getDurationString());
            }

            workoutViewHolder.mDateTextView.setText(mWorkouts.get(position).getDateStringPlWithTime());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                workoutViewHolder.mWorkoutImageView.setImageDrawable(mContext.getResources().getDrawable(IconUtils.getWorkoutIcon(mWorkouts.get(position).getWorkoutName()), mContext.getApplicationContext().getTheme()));
            } else {
                workoutViewHolder.mWorkoutImageView.setImageDrawable(mContext.getResources().getDrawable(IconUtils.getWorkoutIcon(mWorkouts.get(position).getWorkoutName())));
            }
        }
    }

    @Override
    public int getItemCount() {
        return ((mWorkouts != null) && (mWorkouts.size() != 0) ? mWorkouts.size() : 0);
    }

    void loadNewData(List<WorkoutSummary> newActivities) {
        mWorkouts = newActivities;
        notifyDataSetChanged();
    }

    class FeedYouViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mWorkoutImageView;
        private TextView mDateTextView;
        private TextView mDistanceTextView;
        private TextView mDurationTextView;

        private ImageView mDistanceImageView;
        private ImageView mDurationImageView;

        public FeedYouViewHolder(View itemView) {
            super(itemView);
            this.mWorkoutImageView = itemView.findViewById(R.id.activityImageView);
            this.mDateTextView = itemView.findViewById(R.id.dateTextView);
            this.mDistanceTextView = itemView.findViewById(R.id.timeTextView);
            this.mDurationTextView = itemView.findViewById(R.id.durationTextView);
            this.mDistanceImageView = itemView.findViewById(R.id.distanceImageView);
            this.mDurationImageView = itemView.findViewById(R.id.durationImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
