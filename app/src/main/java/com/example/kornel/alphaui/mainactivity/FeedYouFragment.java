package com.example.kornel.alphaui.mainactivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.gpsworkout.WorkoutGpsSummary;
import com.example.kornel.alphaui.utils.ListItemClickListener;
import com.example.kornel.alphaui.utils.User;

import java.util.List;

public class FeedYouFragment extends Fragment implements ListItemClickListener {
    private static final String TAG = "FeedYouFragment";

    private TextView mNoDataInfoTextView;
    private FeedYouAdapter mFeedYouAdapter;
    private RecyclerView mRecyclerView;

    private List<WorkoutGpsSummary> mFeedYouList;

    public FeedYouFragment() {
    }

    // public void setFeedYouList(List<WorkoutGpsSummary> feedYouList) {
    //     mFeedYouList = feedYouList;
    //     if (mFeedYouAdapter != null) {
    //         mFeedYouAdapter.loadNewData(feedYouList);
    //     }
    // }

    public void setFeedYouList(List<WorkoutGpsSummary> feedYouList) {
        mFeedYouList = feedYouList;
    }

    public void loadNewData(List<WorkoutGpsSummary> feedYouList) {
        setFeedYouList(feedYouList);
        checkIfListIsEmpty();
        mFeedYouAdapter.loadNewData(feedYouList);
    }

    private void checkIfListIsEmpty() {
        if (mFeedYouList.size() == 0) {
            mNoDataInfoTextView.setVisibility(View.VISIBLE);
            mNoDataInfoTextView.setText(getString(R.string.no_workouts));
        } else {
            mNoDataInfoTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        Log.d(TAG, "onCreateView: ");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mNoDataInfoTextView = rootView.findViewById(R.id.noDataInfoTextView);
        checkIfListIsEmpty();

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mFeedYouAdapter = new FeedYouAdapter(this, mFeedYouList);
        mRecyclerView.setAdapter(mFeedYouAdapter);

        return rootView;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.d(TAG, "onListItemClick: ");
    }
}
