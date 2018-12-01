package com.example.kornel.alphaui.mainactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.example.kornel.alphaui.WorkoutGpsDetails;
import com.example.kornel.alphaui.gpsworkout.WorkoutGpsSummary;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.ListItemClickListener;
import com.example.kornel.alphaui.utils.User;
import com.example.kornel.alphaui.utils.WorkoutUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FeedFriendsFragment extends Fragment implements ListItemClickListener {
    private static final String TAG = "FeedFriendsFragment";

    public static final String FRIEND_WORKOUT_INTENT_EXTRA = "friends-workout";

    private SwipeRefreshLayout mSwipeRefresh;
    private TextView mNoDataInfoTextView;
    private FeedFriendsAdapter mFeedFriendsAdapter;
    private RecyclerView mRecyclerView;

    private FirebaseUser mUser;
    private String mUserUid;
    private DatabaseReference mRootRef;
    private DatabaseReference mWorkoutsRef;
    private DatabaseReference mUsersRef;

    private List<FriendWorkout> mFeedFriendsList;
    private List<String> mFriendsIds;

    // I do it, because I want to call loadNewData() only when I get all the data from ALL friends
    // not every for every friend separately
    private long mNumberOfFriendsAlreadyIterated = 0;
    private long mFriendsCount;

    private boolean mFragmentJustStarted;

    public FeedFriendsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view_swipe_refresh, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mSwipeRefresh = rootView.findViewById(R.id.swipeRefresh);
        mSwipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        // fetchNewData();
                        readFriendsWorkouts(mFriendsIds);
                    }
                }
        );

        mNoDataInfoTextView = rootView.findViewById(R.id.noDataInfoTextView);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mFeedFriendsAdapter = new FeedFriendsAdapter(getContext(), this, mFeedFriendsList);
        mRecyclerView.setAdapter(mFeedFriendsAdapter);

        mNoDataInfoTextView.setText(getString(R.string.loading));
        mNoDataInfoTextView.setVisibility(View.VISIBLE);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
        mUserUid = mUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mRootRef = database.getReference();
        mUsersRef = mRootRef.child(Database.USERS);
        mWorkoutsRef = mRootRef.child(Database.WORKOUTS);

        mFragmentJustStarted = true;

        mFriendsIds = new ArrayList<>();

        final DatabaseReference friendsRef = mUsersRef.child(mUserUid).child(Database.FRIENDS);
        ValueEventListener friendsIdsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    mFriendsIds.add(ds.getKey());
                }
                if (mFriendsIds.size() == 0) {
                    showNoFriendsMsg();
                } else {
                    for (final String friendUid : mFriendsIds) {

                        DatabaseReference friendUidWorkoutsRef = mWorkoutsRef.child(friendUid);
                        ChildEventListener childWorkoutListener = new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                if (!mFragmentJustStarted) {
                                    Toast.makeText(getActivity(), getString(R.string.new_post_swipe_to_refresh), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { /* EMPTY */}
                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { /* EMPTY */}
                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { /* EMPTY */}

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                                throw databaseError.toException();
                            }
                        };

                        friendUidWorkoutsRef.addChildEventListener(childWorkoutListener);
                    }
                    readFriendsWorkouts(mFriendsIds);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                throw databaseError.toException();
            }
        };
        friendsRef.addListenerForSingleValueEvent(friendsIdsListener);
        //queryForFriends();
    }


    @Override
    public void onListItemClick(int clickedItemIndex) {
        boolean isGpsBased = WorkoutUtils.isGpsBased(mFeedFriendsList.get(clickedItemIndex).getWorkout().getWorkoutName());
        if (isGpsBased) {
            Intent i = new Intent(getActivity(), WorkoutGpsDetails.class);
            i.putExtra(FRIEND_WORKOUT_INTENT_EXTRA, mFeedFriendsList.get(clickedItemIndex));
            startActivity(i);
        } else {
            // Intent i = new Intent(getActivity(), WorkoutNonGpsDeatils.class);
            // startActivity(i);
        }
    }

    // public void fetchNewData() {
    //     queryForFriends();
    // }

    public void setFeedFriendsList(List<FriendWorkout> feedFriendsList) {
        mFeedFriendsList = feedFriendsList;
    }

    public void loadNewData(List<FriendWorkout> feedFriendsList) {
        sortListByDate(feedFriendsList);
        setFeedFriendsList(feedFriendsList);
        checkIfListIsEmpty();
        mFeedFriendsAdapter.loadNewData(mFeedFriendsList);
    }

    // private void queryForFriends() {
    //     final List<String> friendsIds = new ArrayList<>();
    //     final DatabaseReference friendsRef = mUsersRef.child(mUserUid).child(Database.FRIENDS);
    //     ValueEventListener friendsIdsListener = new ValueEventListener() {
    //         @Override
    //         public void onDataChange(DataSnapshot dataSnapshot) {
    //             for (DataSnapshot ds : dataSnapshot.getChildren()) {
    //                 friendsIds.add(ds.getKey());
    //             }
    //             if (friendsIds.size() == 0) {
    //                 showNoFriendsMsg();
    //             } else {
    //                 readFriendsWorkouts(friendsIds);
    //             }
    //         }
    //
    //         @Override
    //         public void onCancelled(@NonNull DatabaseError databaseError) {
    //             Log.e(TAG, "onCancelled: " + databaseError.getMessage());
    //             throw databaseError.toException();
    //         }
    //     };
    //     friendsRef.addListenerForSingleValueEvent(friendsIdsListener);
    // }

    private void readFriendsWorkouts(List<String> friendsIds) {
        for (final String friendUid : friendsIds) {
            mFriendsCount = friendsIds.size();

            DatabaseReference friendUidRef = mUsersRef.child(friendUid);
            ValueEventListener friendsProfilesListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mFeedFriendsList = new ArrayList<>();
                    User user = dataSnapshot.getValue(User.class);
                    final String avatarUrl = user.getAvatarUrl();
                    final String friendName = user.getFullName();

                    DatabaseReference friendUidWorkoutsRef = mWorkoutsRef.child(friendUid);
                    ValueEventListener friendsWorkoutsListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                WorkoutGpsSummary workout = ds.getValue(WorkoutGpsSummary.class);
                                if (!workout.isPrivate()) {
                                    Log.d(TAG, "not private - adding: " + ds);
                                    FriendWorkout friendWorkout = new FriendWorkout(friendName, avatarUrl, workout);
                                    mFeedFriendsList.add(friendWorkout);
                                } else {
                                    Log.d(TAG, "private - not adding: " + ds);
                                }
                            }

                            mNumberOfFriendsAlreadyIterated++;
                            if (mNumberOfFriendsAlreadyIterated == mFriendsCount) {
                                mNumberOfFriendsAlreadyIterated = 0;
                                mFragmentJustStarted = false;
                                loadNewData(mFeedFriendsList);
                                mSwipeRefresh.setRefreshing(false);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                            throw databaseError.toException();
                        }
                    };
                    friendUidWorkoutsRef.addListenerForSingleValueEvent(friendsWorkoutsListener);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                    throw databaseError.toException();
                }
            };
            friendUidRef.addListenerForSingleValueEvent(friendsProfilesListener);
        }
    }

    private void sortListByDate(List<FriendWorkout> list) {
        Collections.sort(list, new Comparator<FriendWorkout>() {
            public int compare(FriendWorkout o1, FriendWorkout o2) {
                if (o1.getWorkout().getDate() == null || o2.getWorkout().getDate() == null)
                    return 0;
                return o2.getWorkout().getDate().compareTo(o1.getWorkout().getDate());
            }
        });
    }

    private void checkIfListIsEmpty() {
        if (mFeedFriendsList.size() == 0) {
            mNoDataInfoTextView.setVisibility(View.VISIBLE);
            mNoDataInfoTextView.setText(getString(R.string.no_friends_workouts));
        } else {
            mNoDataInfoTextView.setVisibility(View.GONE);
        }
    }

    private void showNoFriendsMsg() {
        mNoDataInfoTextView.setVisibility(View.VISIBLE);
        mNoDataInfoTextView.setText(getString(R.string.no_friends));
    }

    // private void readFriendsWorkouts() {
    //     final DatabaseReference friendsRef = mUsersRef.child(mUserUid).child(Database.FRIENDS);
    //     ValueEventListener friendsIdsListener = new ValueEventListener() {
    //         @Override
    //         public void onDataChange(DataSnapshot dataSnapshot) {
    //             for (DataSnapshot ds : dataSnapshot.getChildren()) {
    //                 final String friendUid = ds.getKey();
    //                 mFriendsCount = dataSnapshot.getChildrenCount();
    //
    //                 DatabaseReference friendUidRef = mUsersRef.child(friendUid);
    //                 ValueEventListener friendsProfilesListener = new ValueEventListener() {
    //                     @Override
    //                     public void onDataChange(DataSnapshot dataSnapshot) {
    //                         mFeedFriendsList = new ArrayList<>();
    //                         User user = dataSnapshot.getValue(User.class);
    //                         final String avatarUrl = user.getAvatarUrl();
    //                         final String friendName = user.getFullName();
    //
    //                         DatabaseReference friendUidWorkoutsRef = mWorkoutsRef.child(friendUid);
    //                         ValueEventListener friendsWorkoutsListener = new ValueEventListener() {
    //                             @Override
    //                             public void onDataChange(DataSnapshot dataSnapshot) {
    //                                 for (DataSnapshot ds : dataSnapshot.getChildren()) {
    //                                     WorkoutGpsSummary workout = ds.getValue(WorkoutGpsSummary.class);
    //                                     FriendWorkout friendWorkout = new FriendWorkout(friendName, avatarUrl, workout);
    //                                     mFeedFriendsList.add(friendWorkout);
    //                                 }
    //
    //                                 mNumberOfFriendsAlreadyIterated++;
    //                                 if (mNumberOfFriendsAlreadyIterated == mFriendsCount) {
    //                                     loadNewData(mFeedFriendsList);
    //                                 }
    //                             }
    //
    //                             @Override
    //                             public void onCancelled(@NonNull DatabaseError databaseError) {
    //                                 Log.e(TAG, databaseError.getMessage());
    //                             }
    //                         };
    //                         friendUidWorkoutsRef.addListenerForSingleValueEvent(friendsWorkoutsListener);
    //                     }
    //
    //                     @Override
    //                     public void onCancelled(@NonNull DatabaseError databaseError) {
    //                         Log.e(TAG, databaseError.getMessage());
    //                     }
    //                 };
    //                 friendUidRef.addListenerForSingleValueEvent(friendsProfilesListener);
    //             }
    //         }
    //
    //         @Override
    //         public void onCancelled(@NonNull DatabaseError databaseError) {
    //             Log.e(TAG, databaseError.getMessage());
    //         }
    //     };
    //     friendsRef.addListenerForSingleValueEvent(friendsIdsListener);
    // }
}
