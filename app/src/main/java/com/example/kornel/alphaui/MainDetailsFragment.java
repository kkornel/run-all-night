package com.example.kornel.alphaui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainDetailsFragment extends Fragment {
    private static final String TAG = "MainDetailsFragment";

    private TextView mTimeTextView;
    private TextView mDistanceTextView;
    private TextView mCurrentTextView;
    private TextView mAvgTextView;

    private Handler mTimeHandler;
    private Runnable mTimeRunnable;

    private OnDetailsChanged mCallBack;

    public MainDetailsFragment() {
    }

    interface OnDetailsChanged {
        String getTimeString();
        double getDistance();
        String getDistanceString();
    }

    public void setCallBack(OnDetailsChanged callBack) {
        mCallBack = callBack;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        Log.d(TAG, "setUserVisibleHint: ");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");

        View rootView = inflater.inflate(R.layout.main_training_details_fragment, container, false);

        mTimeTextView = rootView.findViewById(R.id.timeTextView);
        mDistanceTextView = rootView.findViewById(R.id.distanceTextView);
        mCurrentTextView = rootView.findViewById(R.id.currentPaceTextView);
        mAvgTextView = rootView.findViewById(R.id.avgTextView);

        mTimeHandler = new Handler();
        mTimeRunnable = new Runnable() {
            @Override
            public void run() {
                String time = mCallBack.getTimeString();
                String distance = mCallBack.getDistanceString();
                mDistanceTextView.setText(distance);
                mTimeTextView.setText(time);
                mTimeHandler.postDelayed(this, 500);
            }
        };

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        mTimeHandler.postDelayed(mTimeRunnable, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    public void setTime(String time) {
        mTimeTextView.setText(time);
    }

    public void setDistance(String distance) {
        mDistanceTextView.setText(distance);
    }

    public void setAvg(String avg) {
        mCurrentTextView.setText(avg);
    }

    public void setCurrent(String current) {
        mAvgTextView.setText(current);
    }
}
