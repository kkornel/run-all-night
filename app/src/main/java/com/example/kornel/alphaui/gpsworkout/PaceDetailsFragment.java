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
import com.example.kornel.alphaui.utils.Lap;
import com.example.kornel.alphaui.utils.LatLon;


public class PaceDetailsFragment extends Fragment implements PaceAdapter.ListItemClickListener {
    private static final String TAG = "PaceDetailsFragment";

    private TextView mTimeTextView;
    private TextView mAvgTextView;
    private TextView mCurrentTextView;

    private RecyclerView mRecyclerView;
    private PaceAdapter mPaceAdapter;

    private ArrayList<Lap> mPaceList;

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
        mPaceList.add(new Lap(new LatLon(21.3, 12.3), 303997785));
        mPaceList.add(new Lap(new LatLon(21.3, 12.3), 203997785));
        mPaceList.add(new Lap(new LatLon(21.3, 12.3), 103997785));
        mPaceList.add(new Lap(new LatLon(21.3, 12.3), 203997785));
        mPaceList.add(new Lap(new LatLon(21.3, 12.3), 503997785));
        mPaceList.add(new Lap(new LatLon(21.3, 12.3), 333997785));
        mPaceList.add(new Lap(new LatLon(21.3, 12.3), 533997785));
        mPaceList.add(new Lap(new LatLon(21.3, 12.3), 313997785));
        mPaceList.add(new Lap(new LatLon(21.3, 12.3), 103997785));
        mPaceList.add(new Lap(new LatLon(21.3, 12.3), 603997785));
        mPaceList.add(new Lap(new LatLon(21.3, 12.3), 903997785));
        mPaceList.add(new Lap(new LatLon(21.3, 12.3), 803997785));
        mPaceList.add(new Lap(new LatLon(21.3, 12.3), 663997785));
        mPaceList.add(new Lap(new LatLon(21.3, 12.3), 963997785));
        mPaceList.add(new Lap(new LatLon(21.3, 12.3), 153997785));
        mPaceList.add(new Lap(new LatLon(21.3, 12.3), 123997785));
    }
}
