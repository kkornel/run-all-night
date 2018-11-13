package com.example.kornel.alphaui;

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

import com.example.kornel.alphaui.utils.FriendRequest;
import com.example.kornel.alphaui.utils.ListItemClickListener;

import java.util.List;

public class FriendsListFragment extends Fragment implements ListItemClickListener {
    private static final String TAG = "FeedFriendsFragment";

    private TextView mNoDataInfoTextView;

    private FriendsListAdapter mFriendsListAdapter;
    private RecyclerView mRecyclerView;

    private List<String> mFeedFriendsList;

    public FriendsListFragment() {
    }

    public void setFriendsList(List<String> feedFriendsList) {
        mFeedFriendsList = feedFriendsList;
    }

    public void loadNewData(List<String> friendsRequestList) {
        setFriendsList(friendsRequestList);
        checkIfListIsEmpty();
        mFriendsListAdapter.loadNewData(mFeedFriendsList);
    }

    private void checkIfListIsEmpty() {
        if (mFeedFriendsList.size() == 0 ) {
            mNoDataInfoTextView.setVisibility(View.VISIBLE);
            mNoDataInfoTextView.setText(getString(R.string.no_friends));
        } else {
            mNoDataInfoTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mNoDataInfoTextView = rootView.findViewById(R.id.noDataInfoTextView);
        checkIfListIsEmpty();

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mFriendsListAdapter = new FriendsListAdapter(this, mFeedFriendsList);
        mRecyclerView.setAdapter(mFriendsListAdapter);

        return rootView;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.d(TAG, "onListItemClick: ");
    }
}
