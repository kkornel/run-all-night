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

public class PaceAdapter extends RecyclerView.Adapter<PaceAdapter.PaceViewHolder> {

    private ArrayList<Lap> mLapsList;

    public PaceAdapter(ArrayList<Lap> lapsList) {
        mLapsList = lapsList;
    }

    @NonNull
    @Override
    public PaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
        if ((mLapsList == null) || (mLapsList.size() == 0)) {
            paceViewHolder.mLapNumberTextView.setText("ERROR");
        } else {
            paceViewHolder.mLapNumberTextView.setText(String.valueOf(position + 1));
            Lap lap = mLapsList.get(position);
            paceViewHolder.mLapTimeTextView.setText(lap.getTimeString());
            if (mLapsList.size() < 2) {
                int secProgress = (int) lap.getTime() / 1000;
                paceViewHolder.mLapProgressBar.setMax(secProgress);
                paceViewHolder.mLapProgressBar.setProgress(secProgress);
            } else {
                int secLongestLap = (int) searchForLongestLap() / 1000;
                paceViewHolder.mLapProgressBar.setMax(secLongestLap);
                int secProgress = (int) lap.getTime() / 1000;
                paceViewHolder.mLapProgressBar.setProgress(secProgress);
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
        return ((mLapsList != null) && (mLapsList.size() != 0) ? mLapsList.size() : 0);
    }

    void loadNewData(ArrayList<Lap> newLapsList) {
        mLapsList = newLapsList;
        notifyDataSetChanged();
    }

    class PaceViewHolder extends RecyclerView.ViewHolder {
        private TextView mLapNumberTextView;
        private TextView mLapTimeTextView;
        private ProgressBar mLapProgressBar;

        public PaceViewHolder(View itemView) {
            super(itemView);

            mLapNumberTextView = itemView.findViewById(R.id.lapNumberTextView);
            mLapTimeTextView = itemView.findViewById(R.id.lapTimeTextView);
            mLapProgressBar = itemView.findViewById(R.id.lapProgressBar);
        }
    }
}

