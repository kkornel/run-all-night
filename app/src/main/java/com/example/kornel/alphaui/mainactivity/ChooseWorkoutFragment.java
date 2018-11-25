package com.example.kornel.alphaui.mainactivity;

import android.app.Activity;
import android.content.Intent;
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
import com.example.kornel.alphaui.mainactivity.WorkoutAdapter;
import com.example.kornel.alphaui.utils.ListItemClickListener;

import java.util.List;

import static com.example.kornel.alphaui.mainactivity.WorkoutFragment.WORKOUT_RESULT;

public class ChooseWorkoutFragment extends Fragment implements ListItemClickListener {
    private static final String TAG = "ChooseWorkoutFragment";

    private WorkoutAdapter mWorkoutAdapter;
    private RecyclerView mRecyclerView;

    private List<String> mWorkoutsList;

    public ChooseWorkoutFragment() {

    }

    public void setWorkoutsList(List<String> workoutsList) {
        mWorkoutsList = workoutsList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view_empty, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mWorkoutAdapter = new WorkoutAdapter(getContext(), this, mWorkoutsList);
        mRecyclerView.setAdapter(mWorkoutAdapter);

        return rootView;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent returnIntent = new Intent();
        String activity = mWorkoutsList.get(clickedItemIndex);
        returnIntent.putExtra(WORKOUT_RESULT, activity);
        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        getActivity().finish();
    }
}
