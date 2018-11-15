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

import com.example.kornel.alphaui.FriendWorkout;
import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.gpsworkout.WorkoutGpsSummary;
import com.example.kornel.alphaui.utils.ListItemClickListener;

import java.util.List;

public class FeedFriendsFragment extends Fragment implements ListItemClickListener {
    private static final String TAG = "FeedFriendsFragment";

    private TextView mNoDataInfoTextView;
    private FeedFriendsAdapter mFeedFriendsAdapter;
    private RecyclerView mRecyclerView;

    private List<FriendWorkout> mFeedFriendsList;

    public FeedFriendsFragment() {
    }

    private void checkIfListIsEmpty() {
        if (mFeedFriendsList.size() == 0) {
            mNoDataInfoTextView.setVisibility(View.VISIBLE);
            mNoDataInfoTextView.setText(getString(R.string.no_workouts));
        } else {
            mNoDataInfoTextView.setVisibility(View.GONE);
        }
    }

    public void setFeedFriendsList(List<FriendWorkout> feedFriendsList) {
        mFeedFriendsList = feedFriendsList;
    }

    public void loadNewData(List<FriendWorkout> feedFriendsList) {
        setFeedFriendsList(feedFriendsList);
        checkIfListIsEmpty();
        mFeedFriendsAdapter.loadNewData(feedFriendsList);
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

        mFeedFriendsAdapter = new FeedFriendsAdapter(this, mFeedFriendsList);
        mRecyclerView.setAdapter(mFeedFriendsAdapter);

        return rootView;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.d(TAG, "onListItemClick: ");
    }
}
