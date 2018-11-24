package com.example.kornel.alphaui.gpsworkout;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.utils.Lap;

import java.util.ArrayList;
import java.util.List;


public class PaceAdapter extends RecyclerView.Adapter<PaceAdapter.PaceViewHolder> {

    private final ListItemClickListener mOnClickListener;
    private ArrayList<Lap> mLapsList;

    public PaceAdapter(ListItemClickListener onClickListener, ArrayList<Lap> lapsList) {
        mOnClickListener = onClickListener;
        mLapsList = lapsList;
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    @NonNull
    @Override
    public PaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Called by the layout manager when it needs a new view
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.pace_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        PaceViewHolder paceViewHolder = new PaceViewHolder(view);

        return paceViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PaceViewHolder paceViewHolder, int position) {
        // Called by the layout manager when it wants new data in an existing row

        if ((mLapsList == null) || (mLapsList.size() == 0)) {
            paceViewHolder.mLapNumberTextView.setText("ERROR");
        } else {
            paceViewHolder.mLapNumberTextView.setText(String.valueOf(position));
            paceViewHolder.mLapTimeTextView.setText(mLapsList.get(position).getTimeString());
            if (mLapsList.size() < 2) {
                paceViewHolder.mLapProgressBar.setMax((int)mLapsList.get(position).getTime());
                paceViewHolder.mLapProgressBar.setProgress((int)mLapsList.get(position).getTime());
            } else {
                long longestLap = searchForLongestLap();
                paceViewHolder.mLapProgressBar.setMax((int) longestLap);
                paceViewHolder.mLapProgressBar.setProgress((int)mLapsList.get(position).getTime());
            }

        }

        // Googles way
        // paceViewHolder.bind(position);
    }

    private long searchForLongestLap() {
        long longest = mLapsList.get(0).getTime();
        for (int i = 1; i < mLapsList.size(); i++) {
            if (mLapsList.get(i).getTime() > longest) {
                longest = mLapsList.get(i).getTime();
            }
        }
        return longest;
    }

    @Override
    public int getItemCount() {
        return  ((mLapsList != null) && (mLapsList.size() != 0) ? mLapsList.size() : 1);
    }

    void loadNewData(ArrayList<Lap> newLapsList) {
        mLapsList = newLapsList;
        notifyDataSetChanged();
    }

    class PaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mLapNumberTextView;
        private TextView mLapTimeTextView;
        private ProgressBar mLapProgressBar;

        public PaceViewHolder(View itemView) {
            super(itemView);

            mLapNumberTextView = itemView.findViewById(R.id.lapNumberTextView);
            mLapTimeTextView = itemView.findViewById(R.id.lapTimeTextView);
            mLapProgressBar = itemView.findViewById(R.id.lapProgressBar);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }

        // private void bind(int listIndex) {
        //     paceItemTextView.setText(String.valueOf(listIndex));
        // }
    }
}

