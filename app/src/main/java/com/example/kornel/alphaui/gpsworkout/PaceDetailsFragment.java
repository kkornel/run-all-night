package com.example.kornel.alphaui.gpsworkout;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.utils.Lap;

public class PaceDetailsFragment extends Fragment {
    private static final String TAG = "PaceDetailsFragment";

    private TextView mTimeTextView;
    private TextView mAvgPaceTextView;
    private TextView mCurrentPaceTextView;

    private RecyclerView mRecyclerView;
    private PaceAdapter mPaceAdapter;

    private ArrayList<Lap> mLapsList;

    public PaceDetailsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.laps_details_fragment, container, false);

        mTimeTextView = rootView.findViewById(R.id.timeTextViewPace);
        mAvgPaceTextView = rootView.findViewById(R.id.avgTextViewPace);
        mCurrentPaceTextView = rootView.findViewById(R.id.currentTextViewPace);

        mRecyclerView = rootView.findViewById(R.id.recyclerViewPace);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mLapsList = new ArrayList<>();

        mPaceAdapter = new PaceAdapter(mLapsList);
        mRecyclerView.setAdapter(mPaceAdapter);

        return rootView;
    }

    public void setTime(String time) {
        mTimeTextView.setText(time);
    }

    public void setAvgPace(String avg) {
        mAvgPaceTextView.setText(avg);
    }

    public void setCurrentPace(String current) {
        mCurrentPaceTextView.setText(current);
    }

    public void setLapsList(ArrayList<Lap> lapsList) {
        mLapsList = lapsList;
        mPaceAdapter.loadNewData(mLapsList);
    }
}
