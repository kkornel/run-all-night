package com.example.kornel.alphaui.mainactivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kornel.alphaui.FriendWorkout;
import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.gpsworkout.WorkoutGpsSummary;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.ListItemClickListener;
import com.example.kornel.alphaui.utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class FeedYouFragment extends Fragment implements ListItemClickListener {
    private static final String TAG = "FeedYouFragment";

    private TextView mNoDataInfoTextView;
    private FeedYouAdapter mFeedYouAdapter;
    private RecyclerView mRecyclerView;

    private FirebaseUser mUser;
    private String mUserUid;
    private DatabaseReference mRootRef;
    private DatabaseReference mWorkoutsRef;
    private List<WorkoutGpsSummary> mFeedYouList;

    public FeedYouFragment() {

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

        mFeedYouAdapter = new FeedYouAdapter(this, mFeedYouList);
        mRecyclerView.setAdapter(mFeedYouAdapter);

        mNoDataInfoTextView.setText(getString(R.string.loading));
        mNoDataInfoTextView.setVisibility(View.VISIBLE);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart: ");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
        mUserUid = mUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mRootRef = database.getReference();
        mWorkoutsRef = mRootRef.child(Database.WORKOUTS);

        readYourWorkouts();
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.d(TAG, "onListItemClick: ");
        Toast.makeText(getContext(), "Clicked: " + clickedItemIndex, Toast.LENGTH_SHORT).show();
    }

    public void fetchNewData() {
        Log.d(TAG, "fetchNewData: ");
        readYourWorkouts();
    }

    public void setFeedYouList(List<WorkoutGpsSummary> feedYouList) {
        mFeedYouList = feedYouList;
    }

    public void loadNewData(List<WorkoutGpsSummary> feedYouList) {
        sortListByDate(feedYouList);
        setFeedYouList(feedYouList);
        checkIfListIsEmpty();
        mFeedYouAdapter.loadNewData(mFeedYouList);
    }

    private void checkIfListIsEmpty() {
        if (mFeedYouList.size() == 0) {
            mNoDataInfoTextView.setVisibility(View.VISIBLE);
            mNoDataInfoTextView.setText(getString(R.string.no_your_workouts));
        } else {
            mNoDataInfoTextView.setVisibility(View.GONE);
        }
    }

    private void readYourWorkouts() {
        final DatabaseReference myWorkoutsRef = mWorkoutsRef.child(mUserUid);
        ValueEventListener myWorkoutsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mFeedYouList = new ArrayList<>();
                if (dataSnapshot.getValue() == null) {
                    loadNewData(mFeedYouList);
                } else {
                    for (DataSnapshot workoutSnapshot : dataSnapshot.getChildren()) {
                        mFeedYouList.add(workoutSnapshot.getValue(WorkoutGpsSummary.class));
                        if (dataSnapshot.getChildrenCount() == mFeedYouList.size()) {
                            loadNewData(mFeedYouList);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                throw databaseError.toException();
            }
        };
        myWorkoutsRef.addListenerForSingleValueEvent(myWorkoutsListener);
    }

    private void sortListByDate(List<WorkoutGpsSummary> list) {
        Collections.sort(list, new Comparator<WorkoutGpsSummary>() {
            public int compare(WorkoutGpsSummary o1, WorkoutGpsSummary o2) {
                if (o1.getDate() == null || o2.getDate() == null)
                    return 0;
                return o2.getDate().compareTo(o1.getDate());
            }
        });
    }
}
