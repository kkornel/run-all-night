package com.example.kornel.alphaui.sharelocation;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.ListItemClickListener;
import com.example.kornel.alphaui.utils.User;
import com.example.kornel.alphaui.utils.Utils;
import com.example.kornel.alphaui.weather.LocationUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindOthersListFragment extends Fragment implements ListItemClickListener, LocationUtils.MyLocationResult {
    private static final String TAG = "FindOthersListFragment";

    private Spinner mWorkoutTypeSpinner;
    private EditText mDistanceEditText;
    private Button mFindButton;
    private TextView mResultsLabel;
    private TextView mResultsTextView;

    private TextView mNoDataInfoTextView;
    private FindOthersListAdapter mFindOthersListAdapter;
    private RecyclerView mRecyclerView;

    private FindOthersMapFragment.OnFindOthersCallback mOnFindOthersCallback;

    private Location mYouLocation;
    private List<SharedLocationInfo> mSharedLocationInfoList;

    public FindOthersListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_find_others_list, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mWorkoutTypeSpinner = rootView.findViewById(R.id.workoutTypeSpinner);
        mDistanceEditText = rootView.findViewById(R.id.distanceEditText);
        mFindButton = rootView.findViewById(R.id.findButton);
        mFindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFindButtonClicked();
            }
        });

        mResultsTextView = rootView.findViewById(R.id.resultsTextView);
        mResultsLabel = rootView.findViewById(R.id.resultsLabel);
        mResultsLabel.setVisibility(View.INVISIBLE);

        mWorkoutTypeSpinner.post(new Runnable() {
            @Override
            public void run() {
                mWorkoutTypeSpinner.setSelection(8);
            }
        });

        mNoDataInfoTextView = rootView.findViewById(R.id.noDataInfoTextView);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mFindOthersListAdapter = new FindOthersListAdapter(this, mSharedLocationInfoList);
        mRecyclerView.setAdapter(mFindOthersListAdapter);

        mNoDataInfoTextView.setText("");
        mNoDataInfoTextView.setVisibility(View.VISIBLE);

        return rootView;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        String friendUid = mSharedLocationInfoList.get(clickedItemIndex).getUserUid();

        Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void gotLocation(Location location, LocationUtils.LocationErrorType errorType) {
        mYouLocation = location;
        mOnFindOthersCallback.onNewRequest(mYouLocation);
    }

    private void find(final String workoutType, final double distance) {
        new LocationUtils().findUserLocation(getActivity(), getContext(), FindOthersListFragment.this);
        mSharedLocationInfoList = new ArrayList<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference rootRef = database.getReference();
        DatabaseReference sharedLocRef = rootRef.child(Database.SHARED_LOCATIONS);
        final DatabaseReference userRef = rootRef.child(Database.USERS);

        final String userUid = user.getUid();

        ValueEventListener sharedLocListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshotSharedLocation : dataSnapshot.getChildren()) {

                    SharedLocationInfo sharedLocationInfo = snapshotSharedLocation.getValue(SharedLocationInfo.class);
                    sharedLocationInfo.setUserUid(snapshotSharedLocation.getKey());

                    if (sharedLocationInfo.getUserUid().equals(userUid)) {
                        continue;
                    }

                    double distanceToYou = mYouLocation.distanceTo(sharedLocationInfo.getLocation());
                    distanceToYou /= 1000;

                    if (distanceToYou > distance) {
                        continue;
                    }

                    sharedLocationInfo.setDistanceToYou(distanceToYou);
                    mSharedLocationInfoList.add(sharedLocationInfo);
                }

                mResultsLabel.setVisibility(View.VISIBLE);
                mResultsTextView.setText(String.valueOf(mSharedLocationInfoList.size()));

                mOnFindOthersCallback.onGotAllSharedLocations(mSharedLocationInfoList);

                if (mSharedLocationInfoList.size() == 0) {
                    Toast.makeText(getContext(), getString(R.string.no_results_in_given_range), Toast.LENGTH_LONG).show();
                    return;
                }

                for (final SharedLocationInfo sharedLoc : mSharedLocationInfoList) {

                    ValueEventListener userProfileListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User userProfile = dataSnapshot.getValue(User.class);
                            userProfile.setUserUid(sharedLoc.getUserUid());
                            sharedLoc.setUserProfile(userProfile);

                            mOnFindOthersCallback.onMapUpdate(sharedLoc);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                            throw databaseError.toException();
                        }
                    };
                    userRef.child(sharedLoc.getUserUid()).addListenerForSingleValueEvent(userProfileListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                throw databaseError.toException();
            }
        };

        sharedLocRef.child(workoutType).addListenerForSingleValueEvent(sharedLocListener);
    }

    public void setCallback(FindOthersMapFragment.OnFindOthersCallback onFindOthersCallback) {
        mOnFindOthersCallback = onFindOthersCallback;
    }

    public void loadNewData() {
        checkIfListIsEmpty();
        mFindOthersListAdapter.loadNewData(mSharedLocationInfoList);
    }

    private void onFindButtonClicked() {
        try {
            Utils.hideKeyboard(FindOthersListFragment.this);
            String workoutType = mWorkoutTypeSpinner.getSelectedItem().toString();
            String distanceStringInput = mDistanceEditText.getText().toString();
            if (distanceStringInput.equals("")) {
                showSnackbar(getString(R.string.input_range));
                return;
            }
            double distanceInput = Double.parseDouble(distanceStringInput);
            mFindOthersListAdapter.loadNewData(new ArrayList<SharedLocationInfo>());
            find(workoutType, distanceInput);
        } catch (NumberFormatException ex) {
            showSnackbar(getString(R.string.invalid_number));
        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(
                mFindButton,
                message,
                Snackbar.LENGTH_LONG)
                .show();
    }

    private void checkIfListIsEmpty() {
        if (mSharedLocationInfoList.size() == 0) {
            mNoDataInfoTextView.setVisibility(View.VISIBLE);
            mNoDataInfoTextView.setText(getString(R.string.no_results));
        } else {
            mNoDataInfoTextView.setVisibility(View.GONE);
        }
    }
}
