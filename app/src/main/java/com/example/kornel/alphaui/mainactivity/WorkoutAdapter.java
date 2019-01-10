package com.example.kornel.alphaui.mainactivity;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.utils.IconUtils;
import com.example.kornel.alphaui.utils.ListItemClickListener;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private Context mContext;
    private final ListItemClickListener mOnClickListener;
    private List<String> mWorkouts;

    public WorkoutAdapter(Context context, ListItemClickListener onClickListener, List<String> activities) {
        mContext = context;
        mOnClickListener = onClickListener;
        mWorkouts = activities;
    }

    @Override
    public WorkoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.workout_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        WorkoutViewHolder workoutViewHolder = new WorkoutViewHolder(view);

        return workoutViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder workoutViewHolder, int position) {
        if ((mWorkouts == null) || (mWorkouts.size() == 0)) {
            workoutViewHolder.mWorkoutTextView.setText("ERROR");
        } else {
            workoutViewHolder.mWorkoutTextView.setText(mWorkouts.get(position));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                workoutViewHolder.mWorkoutImageView.setImageDrawable(mContext.getResources().getDrawable(IconUtils.getWorkoutIcon(mWorkouts.get(position)), mContext.getApplicationContext().getTheme()));
            } else {
                workoutViewHolder.mWorkoutImageView.setImageDrawable(mContext.getResources().getDrawable(IconUtils.getWorkoutIcon(mWorkouts.get(position))));
            }
        }
        // Googles way
        // paceViewHolder.bind(position);
    }

    @Override
    public int getItemCount() {
        return ((mWorkouts != null) && (mWorkouts.size() != 0) ? mWorkouts.size() : 0);
    }

    void loadNewData(List<String> newActivities) {
        mWorkouts = newActivities;
        notifyDataSetChanged();
    }

    class WorkoutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mWorkoutImageView;
        private TextView mWorkoutTextView;

        public WorkoutViewHolder(View itemView) {
            super(itemView);
            this.mWorkoutImageView = itemView.findViewById(R.id.activityImageView);
            this.mWorkoutTextView = itemView.findViewById(R.id.activityTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
