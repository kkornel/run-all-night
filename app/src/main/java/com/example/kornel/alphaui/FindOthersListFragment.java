package com.example.kornel.alphaui;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kornel.alphaui.friends.FriendsListAdapter;
import com.example.kornel.alphaui.utils.ListItemClickListener;
import com.example.kornel.alphaui.utils.User;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FindOthersListFragment extends Fragment implements ListItemClickListener {
    private TextView mNoDataInfoTextView;
    private FindOthersListAdapter mFindOthersListAdapter;
    private RecyclerView mRecyclerView;

    private List<SharedLocationInfo> mSharedLocationInfoList;


    public FindOthersListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mNoDataInfoTextView = rootView.findViewById(R.id.noDataInfoTextView);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mFindOthersListAdapter = new FindOthersListAdapter(this, mSharedLocationInfoList);
        mRecyclerView.setAdapter(mFindOthersListAdapter);

        mNoDataInfoTextView.setText(getString(R.string.loading));
        mNoDataInfoTextView.setVisibility(View.VISIBLE);

        return rootView;
    }

    public void loadNewData(List<SharedLocationInfo> sharedLocationInfoList) {
        mSharedLocationInfoList = sharedLocationInfoList;
        checkIfListIsEmpty();
        mFindOthersListAdapter.loadNewData(mSharedLocationInfoList);
    }

    private void checkIfListIsEmpty() {
        if (mSharedLocationInfoList.size() == 0) {
            mNoDataInfoTextView.setVisibility(View.VISIBLE);
            mNoDataInfoTextView.setText("Nie znaleziono nikogo");
        } else {
            mNoDataInfoTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        String friendUid = mSharedLocationInfoList.get(clickedItemIndex).getUserUid();

        Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
    }
}
