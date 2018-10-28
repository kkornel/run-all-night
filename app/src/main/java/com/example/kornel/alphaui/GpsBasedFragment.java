package com.example.kornel.alphaui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

// import static com.example.kornel.alphaui.MainActivity.ACTIVITY_RESULT;

public class GpsBasedFragment extends Fragment implements
        ActivityAdapter.ListItemClickListener {

    private ActivityAdapter mActivityAdapter;
    private RecyclerView mRecyclerView;

    private List<String> mActivitiesList;

    public GpsBasedFragment() {
    }

    public void setActivitiesList(List<String> activitiesList) {
        mActivitiesList = activitiesList;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_choose_sport, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView = rootView.findViewById(R.id.activitiesRecyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mActivityAdapter = new ActivityAdapter(this, mActivitiesList);
        mRecyclerView.setAdapter(mActivityAdapter);

        return rootView;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent returnIntent = new Intent();
        String activity = mActivitiesList.get(clickedItemIndex);
        // returnIntent.putExtra(ACTIVITY_RESULT, activity);
        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        getActivity().finish();
    }
}
