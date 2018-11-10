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

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.gpsworkout.WorkoutGpsSummary;
import com.example.kornel.alphaui.utils.ListItemClickListener;

import java.util.List;

public class FeedYouFragment extends Fragment implements ListItemClickListener {
    private static final String TAG = "FeedYouFragment";

    private FeedYouAdapter mFeedYouAdapter;
    private RecyclerView mRecyclerView;

    private List<WorkoutGpsSummary> mFeedYouList;

    public FeedYouFragment() {
    }

    public void setFeedYouList(List<WorkoutGpsSummary> feedYouList) {
        mFeedYouList = feedYouList;
        if (mFeedYouAdapter != null) {
            mFeedYouAdapter.loadNewData(feedYouList);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        Log.d(TAG, "onCreateView: ");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

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
