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
import android.widget.Toast;

import java.util.ArrayList;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.utils.Lap;
import com.example.kornel.alphaui.utils.LatLon;
import com.example.kornel.alphaui.utils.Position;


public class PaceDetailsFragment extends Fragment implements PaceAdapter.ListItemClickListener {
    private static final String TAG = "PaceDetailsFragment";

    private TextView mTimeTextView;
    private TextView mAvgPaceTextView;
    private TextView mCurrentPaceTextView;

    private RecyclerView mRecyclerView;
    private PaceAdapter mPaceAdapter;

    private ArrayList<Lap> mLapsList;

    private Toast mToast;

    public PaceDetailsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.laps_details_fragment, container, false);

        mTimeTextView = rootView.findViewById(R.id.timeTextViewPace);
        mAvgPaceTextView =  rootView.findViewById(R.id.avgTextViewPace);
        mCurrentPaceTextView = rootView.findViewById(R.id.currentTextViewPace);

        mRecyclerView = rootView.findViewById(R.id.recyclerViewPace);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mLapsList = new ArrayList<>();
        test();
        mPaceAdapter = new PaceAdapter(this, mLapsList);
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

    void test() {
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 324922));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 424722));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 524522));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 724422));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 124322));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 224822));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 924922));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 024422));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
        mLapsList.add(new Lap(new Position(new LatLon(12.23, 23.12),  324922), 624222));
    }
}
