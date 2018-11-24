package com.example.kornel.alphaui.gpsworkout;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kornel.alphaui.R;


public class MainDetailsFragment extends Fragment {
    private static final String TAG = "MainDetailsFragment";

    private TextView mTimeTextView;
    private TextView mDistanceTextView;
    private TextView mCurrentTextView;
    private TextView mAvgTextView;

    public MainDetailsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_training_details_fragment, container, false);

        mTimeTextView = rootView.findViewById(R.id.timeTextView);
        mDistanceTextView = rootView.findViewById(R.id.distanceTextView);
        mCurrentTextView = rootView.findViewById(R.id.currentPaceTextView);
        mAvgTextView = rootView.findViewById(R.id.avgTextView);

        return rootView;
    }

    public void setTime(String time) {
        mTimeTextView.setText(time);
    }

    public void setDistance(String distance) {
        mDistanceTextView.setText(distance);
    }

    public void setAvg(String avg) {
        mAvgTextView.setText(avg);
    }

    public void setCurrent(String current) {
        mCurrentTextView.setText(current);
    }
}
