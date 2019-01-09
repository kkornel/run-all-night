package com.example.kornel.alphaui.sharelocation;

import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

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

    private AlertDialog mInfoWindowDialog;


    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRootRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mFriendRequestsRef;
    private String mUserUid;
    private String mFriendUid;

    private boolean mFirstLocationUpdate = true;

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

        mNoDataInfoTextView = rootView.findViewById(R.id.noDataInfoTextView);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mFindOthersListAdapter = new FindOthersListAdapter(this, mSharedLocationInfoList);
        mRecyclerView.setAdapter(mFindOthersListAdapter);

        mNoDataInfoTextView.setText("");
        mNoDataInfoTextView.setVisibility(View.VISIBLE);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mUserUid = mFirebaseAuth.getUid();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRootRef = mFirebaseDatabase.getReference();

        mUsersRef = mRootRef.child(Database.USERS);
        mFriendRequestsRef = mRootRef.child(Database.FRIENDS_REQUESTS);

        new LocationUtils().findUserLocation(getActivity(), getContext(), FindOthersListFragment.this);

        return rootView;
    }

    private void showCameraGalleryDialog(int clickedItemIndex) {
        User user = mSharedLocationInfoList.get(clickedItemIndex).getUserProfile();
        SharedLocationInfo sli = mSharedLocationInfoList.get(clickedItemIndex);
        final String userUid = user.getUserUid();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // Get the layout inflater
        final LayoutInflater inflater = getLayoutInflater();

        View mInfoWindow = inflater.inflate(R.layout.marker_info_window, null);
        ImageView avatarImageView = mInfoWindow.findViewById(R.id.avatarImageView);
        TextView nameTextView = mInfoWindow.findViewById(R.id.nameTextView);
        TextView distanceLabel = mInfoWindow.findViewById(R.id.distanceLabel);
        TextView distanceTextView = mInfoWindow.findViewById(R.id.distanceTextView);
        TextView messageTextView = mInfoWindow.findViewById(R.id.messageTextView);
        Button showProfileButton = mInfoWindow.findViewById(R.id.viewProfileButton);
        final Button addToFriendsButton = mInfoWindow.findViewById(R.id.addToFriendButton);

        DatabaseReference userFriendRef = mUsersRef.child(mUserUid).child(Database.FRIENDS).child(userUid);
        ValueEventListener friendsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    addToFriendsButton.setEnabled(false);
                    addToFriendsButton.setText(getString(R.string.already_a_friends));
                } else {

                    DatabaseReference userFriendRequestsRef = mFriendRequestsRef.child(mUserUid).child(userUid);
                    ValueEventListener userFriendRequestsListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                addToFriendsButton.setEnabled(false);
                                addToFriendsButton.setText(getString(R.string.friends_dialog_invitation_sent));
                            } else {
                                addToFriendsButton.setEnabled(true);
                                addToFriendsButton.setText(getString(R.string.invite_to_friends));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                            throw databaseError.toException();
                        }
                    };
                    userFriendRequestsRef.addListenerForSingleValueEvent(userFriendRequestsListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                throw databaseError.toException();
            }
        };
        userFriendRef.addListenerForSingleValueEvent(friendsListener);

        nameTextView.setText(sli.getUserProfile().getFullName());

        distanceTextView.setText(sli.getDistanceToYouString() + "km");
        messageTextView.setText(sli.getMessage());

        Picasso.get()
                .load(sli.getUserProfile().getAvatarUrl())
                .into(avatarImageView);

        mInfoWindowDialog = builder.create();
        mInfoWindowDialog.show();
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        String userUid = mSharedLocationInfoList.get(clickedItemIndex).getUserUid();



        showCameraGalleryDialog(clickedItemIndex);



        Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void gotLocation(Location location, LocationUtils.LocationErrorType errorType) {
        mYouLocation = location;
        if (mFirstLocationUpdate) {
            mFirstLocationUpdate = false;
            return;
        }
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

                    // TODO i added this
                    if(mYouLocation == null) {
                        Toast.makeText(getContext(), "Couldn't get your location", Toast.LENGTH_LONG).show();
                        return;
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
