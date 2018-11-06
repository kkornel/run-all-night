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

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {
    private final ListItemClickListener mOnClickListener;

    private List<String> mActivities;

    public ActivityAdapter(ListItemClickListener onClickListener, List<String> activities) {
        mOnClickListener = onClickListener;
        mActivities = activities;
    }

    @Override
    public ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Called by the layout manager when it needs a new view
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.workout_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        ActivityViewHolder activityViewHolder = new ActivityViewHolder(view);

        return activityViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder activityViewHolder, int position) {
        // Called by the layout manager when it wants new data in an existing row

        if ((mActivities == null) || (mActivities.size() == 0)) {
            activityViewHolder.mActivityTextView.setText("ERROR");
        } else {
            activityViewHolder.mActivityTextView.setText(mActivities.get(position));
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

    class ActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mActivityImageView;
        private TextView mActivityTextView;

        public ActivityViewHolder(View itemView) {
            super(itemView);
            this.mActivityImageView = itemView.findViewById(R.id.activityImageView);
            this.mActivityTextView = itemView.findViewById(R.id.activityTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("s", "onClick: ");
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
