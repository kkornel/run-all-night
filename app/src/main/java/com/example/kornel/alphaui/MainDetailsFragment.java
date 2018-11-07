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

    String time;
    String distance;
    String avg;
    String current;

    private Handler mTimeHandler;
    private Runnable mTimeRunnable;

    private OnDetailsChanged mCallBack;

    public MainDetailsFragment() {
    }

    interface OnDetailsChanged {
        String onTimeChanged();
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

        final TextView timeTextView = (TextView) rootView.findViewById(R.id.timeTextView);
        TextView distanceTextView = (TextView) rootView.findViewById(R.id.distanceTextView);
        TextView currentTextView = (TextView) rootView.findViewById(R.id.currentPaceTextView);
        TextView avgTextView = (TextView) rootView.findViewById(R.id.avgTextView);

        timeTextView.setText(time);
        distanceTextView.setText(distance);
        currentTextView.setText(avg);
        avgTextView.setText(current);

        mTimeHandler = new Handler();
        mTimeRunnable = new Runnable() {
            @Override
            public void run() {
                String time = mCallBack.onTimeChanged();
                timeTextView.setText(time);
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
        this.time = time;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setAvg(String avg) {
        this.avg = avg;
    }

    public void setCurrent(String current) {
        this.current = current;
    }
}
