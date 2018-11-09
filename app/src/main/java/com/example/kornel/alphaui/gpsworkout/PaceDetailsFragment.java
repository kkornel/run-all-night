package com.example.kornel.alphaui.gpsworkout;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.kornel.alphaui.R;


public class PaceDetailsFragment extends Fragment implements
        PaceAdapter.ListItemClickListener {
    private static final String TAG = "PaceDetailsFragment";

    private TextView mTimeTextView;
    private TextView mAvgTextView;
    private TextView mCurrentTextView;

    private RecyclerView mRecyclerView;
    private PaceAdapter mPaceAdapter;

    private List<String> mPaceList;

    private Toast mToast;

    public PaceDetailsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.laps_details_fragment, container, false);

        mTimeTextView = rootView.findViewById(R.id.timeTextViewPace);
        mAvgTextView =  rootView.findViewById(R.id.avgTextViewPace);
        mCurrentTextView = rootView.findViewById(R.id.currentTextViewPace);

        mRecyclerView = rootView.findViewById(R.id.recyclerViewPace);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        // TODO change 100
        testPace();
        mPaceAdapter = new PaceAdapter(this, mPaceList);
        mRecyclerView.setAdapter(mPaceAdapter);

        return rootView;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        if (mToast != null) {
            mToast.cancel();
        }

        String msg = "Clicked: " + clickedItemIndex;
        mToast = Toast.makeText(getContext(), msg, Toast.LENGTH_LONG);
        mToast.show();
    }

    public void setTime(String time) {
        mTimeTextView.setText(time);
    }

    public void setAvg(String avg) {
        mAvgTextView.setText(avg);
    }

    public void setCurrent(String current) {
        mCurrentTextView.setText(current);
    }

    void testPace() {
        mPaceList = new ArrayList<>();
        mPaceList.add("3:01");
        mPaceList.add("2:21");
        mPaceList.add("1:41");
        mPaceList.add("3:01");
        mPaceList.add("5:54");
        mPaceList.add("4:32");
        mPaceList.add("2:12");
        mPaceList.add("3:34");
        mPaceList.add("3:41");
        mPaceList.add("2:01");
        mPaceList.add("3:21");
        mPaceList.add("3:41");
        mPaceList.add("3:21");
        mPaceList.add("3:51");
        mPaceList.add("2:01");
        mPaceList.add("2:11");
        mPaceList.add("3:21");
        mPaceList.add("3:51");
    }
}
